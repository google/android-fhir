/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.fhir.sync

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkInfo.State.CANCELLED
import androidx.work.WorkInfo.State.ENQUEUED
import androidx.work.WorkInfo.State.FAILED
import androidx.work.WorkInfo.State.RUNNING
import androidx.work.WorkInfo.State.SUCCEEDED
import androidx.work.WorkManager
import androidx.work.hasKeyWithValueOfType
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.OffsetDateTimeTypeAdapter
import com.google.android.fhir.sync.SyncState.Cancelled
import com.google.android.fhir.sync.SyncState.Enqueued
import com.google.android.fhir.sync.SyncState.Failed
import com.google.android.fhir.sync.SyncState.Running
import com.google.android.fhir.sync.SyncState.Succeeded
import com.google.android.fhir.sync.SyncState.Unknown
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.OffsetDateTime
import java.util.UUID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.mapNotNull

object Sync {
  val gson: Gson =
    GsonBuilder()
      .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeTypeAdapter().nullSafe())
      .create()

  /**
   * Starts a one time sync job based on [FhirSyncWorker].
   *
   * Use the returned [Flow] to get updates of the sync job. Alternatively, use [getWorkerInfo] with
   * the same [FhirSyncWorker] to retrieve the status of the job.
   *
   * @param retryConfiguration configuration to guide the retry mechanism, or `null` to stop retry.
   * @return a [Flow] of [SyncState]
   */
  inline fun <reified W : FhirSyncWorker> oneTimeSync(
    context: Context,
    retryConfiguration: RetryConfiguration? = defaultRetryConfiguration,
  ): Flow<SyncState> {
    val uniqueWorkName = "${W::class.java.name}-oneTimeSync"
    val flow = getWorkerInfo(context, uniqueWorkName)
    val oneTimeWorkRequest =
      createOneTimeWorkRequest(retryConfiguration, W::class.java, uniqueWorkName)
    WorkManager.getInstance(context)
      .enqueueUniqueWork(
        uniqueWorkName,
        ExistingWorkPolicy.KEEP,
        oneTimeWorkRequest,
      )
    return combineSyncStateForOneTimeSync(context, uniqueWorkName, flow, oneTimeWorkRequest.id)
  }

  /**
   * Starts a periodic sync job based on [FhirSyncWorker].
   *
   * Use the returned [Flow] to get updates of the sync job. Alternatively, use [getWorkerInfo] with
   * the same [FhirSyncWorker] to retrieve the status of the job.
   *
   * @param periodicSyncConfiguration configuration to determine the sync frequency and retry
   *   mechanism
   * @return a [Flow] of [PeriodicSyncState]
   */
  @ExperimentalCoroutinesApi
  inline fun <reified W : FhirSyncWorker> periodicSync(
    context: Context,
    periodicSyncConfiguration: PeriodicSyncConfiguration,
  ): Flow<PeriodicSyncState> {
    val uniqueWorkName = "${W::class.java.name}-periodicSync"
    val flow = getWorkerInfo(context, uniqueWorkName)
    val periodicWorkRequest =
      createPeriodicWorkRequest(periodicSyncConfiguration, W::class.java, uniqueWorkName)
    WorkManager.getInstance(context)
      .enqueueUniquePeriodicWork(
        uniqueWorkName,
        ExistingPeriodicWorkPolicy.KEEP,
        periodicWorkRequest,
      )
    return combineSyncStateForPeriodicSync(context, uniqueWorkName, flow, periodicWorkRequest.id)
  }

  /** Gets the worker info for the [FhirSyncWorker] */
  fun getWorkerInfo(context: Context, workName: String) =
    WorkManager.getInstance(context)
      .getWorkInfosForUniqueWorkLiveData(workName)
      .asFlow()
      .flatMapConcat { it.asFlow() }
      .mapNotNull { workInfo ->
        workInfo.progress
          .takeIf { it.keyValueMap.isNotEmpty() && it.hasKeyWithValueOfType<String>("StateType") }
          ?.let {
            val state = it.getString("StateType")!!
            val stateData = it.getString("State")
            gson.fromJson(stateData, Class.forName(state)) as SyncJobStatus
          }
      }

  /**
   * Combines the sync state for a periodic sync operation, including work state, progress, and
   * terminal states.
   *
   * @param context The Android application context.
   * @param workName The name of the periodic sync work.
   * @param syncJobProgressStateFlow A flow representing the progress of the sync job.
   * @return A flow of [PeriodicSyncState] combining the sync job states.
   */
  @PublishedApi
  internal fun combineSyncStateForPeriodicSync(
    context: Context,
    workName: String,
    syncJobProgressStateFlow: Flow<SyncJobStatus>,
    id: UUID,
  ): Flow<PeriodicSyncState> {
    val workStateFlow: Flow<WorkInfo.State> = observeWorkState(context, id)
    val syncJobTerminalStateFlow: Flow<SyncJobStatus> =
      FhirEngineProvider.getFhirDataStore(context).observeSyncJobTerminalState(workName)
    val syncJobFlow =
      combine(syncJobProgressStateFlow, syncJobTerminalStateFlow) { inProgress, terminal,
        ->
        if (terminal == SyncJobStatus.Unknown) {
          inProgress
        } else {
          terminal
        }
      }

    return combine(workStateFlow, syncJobFlow) { workInfoState, syncJobIntermediateState ->
      val lastSyncJobStatus =
        FhirEngineProvider.getFhirDataStore(context).getLastSyncJobStatus(workName)
      val currentJobState = createCurrentJobState(syncJobIntermediateState, workInfoState)
      PeriodicSyncState(
        lastJobState = mapSyncJobStatusToResult(lastSyncJobStatus),
        currentJobState = currentJobState,
      )
    }
  }

  /**
   * Combines the sync state for a one-time sync operation, including work state, progress, and
   * terminal states.
   *
   * @param context The Android application context.
   * @param workName The name of the one-time sync work.
   * @param syncJobProgressStateFlow A flow representing the progress of the sync job.
   * @return A flow of [SyncState] combining the sync job states.
   */
  @PublishedApi
  internal fun combineSyncStateForOneTimeSync(
    context: Context,
    workName: String,
    syncJobProgressStateFlow: Flow<SyncJobStatus>,
    uuid: UUID,
  ): Flow<SyncState> {
    val workStateFlow: Flow<WorkInfo.State> = observeWorkState(context, uuid)
    val syncJobTerminalStateFlow: Flow<SyncJobStatus> =
      FhirEngineProvider.getFhirDataStore(context).observeSyncJobTerminalState(workName)

    return combine(workStateFlow, syncJobProgressStateFlow, syncJobTerminalStateFlow) {
      workInfoState,
      currentSyncJobStatus,
      terminalSyncJobStatus,
      ->
      createSyncState(workInfoState, currentSyncJobStatus, terminalSyncJobStatus)
    }
  }

  private fun observeWorkState(context: Context, uuid: UUID): Flow<WorkInfo.State> {
    return WorkManager.getInstance(context).getWorkInfoByIdLiveData(uuid).asFlow().mapNotNull {
      workInfo ->
      workInfo.state
    }
  }

  @PublishedApi
  internal inline fun <W : FhirSyncWorker> createOneTimeWorkRequest(
    retryConfiguration: RetryConfiguration?,
    clazz: Class<W>,
    uniqueWorkName: String,
  ): OneTimeWorkRequest {
    val oneTimeWorkRequestBuilder = OneTimeWorkRequest.Builder(clazz)
    retryConfiguration?.let {
      oneTimeWorkRequestBuilder.setBackoffCriteria(
        it.backoffCriteria.backoffPolicy,
        it.backoffCriteria.backoffDelay,
        it.backoffCriteria.timeUnit,
      )
      oneTimeWorkRequestBuilder.setInputData(
        Data.Builder()
          .putInt(MAX_RETRIES_ALLOWED, it.maxRetries)
          .putString(SYNC_STATUS_PREFERENCES_DATASTORE_KEY, uniqueWorkName)
          .build(),
      )
    }
    return oneTimeWorkRequestBuilder.build()
  }

  @PublishedApi
  internal inline fun <W : FhirSyncWorker> createPeriodicWorkRequest(
    periodicSyncConfiguration: PeriodicSyncConfiguration,
    clazz: Class<W>,
    uniqueWorkName: String,
  ): PeriodicWorkRequest {
    val periodicWorkRequestBuilder =
      PeriodicWorkRequest.Builder(
          clazz,
          periodicSyncConfiguration.repeat.interval,
          periodicSyncConfiguration.repeat.timeUnit,
        )
        .setConstraints(periodicSyncConfiguration.syncConstraints)

    periodicSyncConfiguration.retryConfiguration?.let {
      periodicWorkRequestBuilder.setBackoffCriteria(
        it.backoffCriteria.backoffPolicy,
        it.backoffCriteria.backoffDelay,
        it.backoffCriteria.timeUnit,
      )
      periodicWorkRequestBuilder.setInputData(
        Data.Builder()
          .putInt(MAX_RETRIES_ALLOWED, it.maxRetries)
          .putString(SYNC_STATUS_PREFERENCES_DATASTORE_KEY, uniqueWorkName)
          .build(),
      )
    }
    return periodicWorkRequestBuilder.build()
  }

  /** Gets the timestamp of the last sync job. */
  fun getLastSyncTimestamp(context: Context): OffsetDateTime? {
    return FhirEngineProvider.getFhirDataStore(context).readLastSyncTimestamp()
  }

  private fun createSyncState(
    workInfoState: WorkInfo.State,
    currentSyncJobStatus: SyncJobStatus,
    terminalSyncJobStatus: SyncJobStatus,
  ): SyncState {
    return when (workInfoState) {
      ENQUEUED -> {
        Enqueued
      }
      RUNNING -> {
        Running(currentSyncJobStatus)
      }
      SUCCEEDED -> {
        Succeeded(terminalSyncJobStatus)
      }
      FAILED -> {
        Failed(terminalSyncJobStatus)
      }
      CANCELLED -> {
        Cancelled
      }
      else -> {
        Unknown
      }
    }
  }

  /**
   * Maps the [lastSyncJobStatus] to a specific [Result] based on the provided status.
   *
   * @param lastSyncJobStatus The last synchronization job status of type [SyncJobStatus].
   * @return The mapped [Result] based on the provided [lastSyncJobStatus]:
   * - [Result.Succeeded] with the timestamp if the last job status is [SyncJobStatus.Finished].
   * - [Result.Failed] with exceptions and timestamp if the last job status is
   *   [SyncJobStatus.Failed].
   * - `null` if the last job status is neither Finished nor Failed.
   */
  private fun mapSyncJobStatusToResult(
    lastSyncJobStatus: SyncJobStatus,
  ) =
    when (lastSyncJobStatus) {
      is SyncJobStatus.Finished -> Result.Succeeded(lastSyncJobStatus.timestamp)
      is SyncJobStatus.Failed ->
        Result.Failed(lastSyncJobStatus.exceptions, lastSyncJobStatus.timestamp)
      else -> Result.Unknown
    }

  /**
   * Creates the current job state based on the provided [currentSyncJobStatus] and
   * [schedulingStatus].
   *
   * @param currentSyncJobStatus The current synchronization job status.
   * @param schedulingStatus The scheduling status represented by [WorkInfo.State].
   * @return The current job state based on the given parameters:
   * - [Enqueued] if the job is in the enqueued state.
   * - [Running] if the job is currently running.
   * - [Succeeded] if the job has finished successfully.
   * - [Failed] if the job has encountered a failure.
   * - `null` if the scheduling status is neither enqueued nor running.
   */
  private fun createCurrentJobState(
    currentSyncJobStatus: SyncJobStatus,
    schedulingStatus: WorkInfo.State,
  ) =
    when (schedulingStatus) {
      WorkInfo.State.ENQUEUED -> Enqueued
      WorkInfo.State.RUNNING -> {
        when (currentSyncJobStatus) {
          is SyncJobStatus.Started,
          is SyncJobStatus.InProgress, -> Running(currentSyncJobStatus)
          is SyncJobStatus.Finished -> Succeeded(currentSyncJobStatus)
          is SyncJobStatus.Failed -> Failed(currentSyncJobStatus)
          is SyncJobStatus.Unknown -> Unknown
        }
      }
      else -> Unknown
    }
}
