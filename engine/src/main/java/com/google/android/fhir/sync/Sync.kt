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
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.OffsetDateTime
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
    return combineSyncStateForOneTimeSync(context, uniqueWorkName, flow)
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
    return combineSyncStateForPeriodicSync(context, uniqueWorkName, flow)
  }

  /** Gets the worker info for the [FhirSyncWorker] */
  fun getWorkerInfo(context: Context, workName: String) =
    WorkManager.getInstance(context)
      .getWorkInfosForUniqueWorkLiveData(workName)
      .asFlow()
      .flatMapConcat { it.asFlow() }
      .mapNotNull { workInfo ->
        workInfo.state to
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
    workerInfoSyncJobStatusPairFromWorkManagerFlow: Flow<Pair<WorkInfo.State, SyncJobStatus?>>,
  ): Flow<PeriodicSyncState> {
    val syncJobStatusInDataStoreFlow: Flow<SyncJobStatus?> =
      FhirEngineProvider.getFhirDataStore(context).observeTerminalSyncJobStatus(workName)

    return combine(workerInfoSyncJobStatusPairFromWorkManagerFlow, syncJobStatusInDataStoreFlow) {
      workerInfoSyncJobStatusPairFromWorkManager,
      syncJobStatusFromDataStore,
      ->
      PeriodicSyncState(
        lastJobState = mapSyncJobStatusToResult(syncJobStatusFromDataStore),
        currentJobState =
          createSyncState(
            WorkRequest.PERIODIC,
            workerInfoSyncJobStatusPairFromWorkManager.first,
            workerInfoSyncJobStatusPairFromWorkManager.second,
            syncJobStatusFromDataStore,
          ),
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
    workerInfoSyncJobStatusPairFromWorkManagerFlow: Flow<Pair<WorkInfo.State, SyncJobStatus?>>,
  ): Flow<SyncState> {
    val syncJobStatusInDataStoreFlow: Flow<SyncJobStatus?> =
      FhirEngineProvider.getFhirDataStore(context).observeTerminalSyncJobStatus(workName)

    return combine(workerInfoSyncJobStatusPairFromWorkManagerFlow, syncJobStatusInDataStoreFlow) {
      workerInfoSyncJobStatusPairFromWorkManager,
      syncJobStatusFromDataStore,
      ->
      createSyncState(
        WorkRequest.ONE_TIME,
        workerInfoSyncJobStatusPairFromWorkManager.first,
        workerInfoSyncJobStatusPairFromWorkManager.second,
        syncJobStatusFromDataStore,
      )
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
    workRequest: WorkRequest,
    workInfoState: WorkInfo.State,
    syncJobStatusFromWorkManager: SyncJobStatus?,
    syncJobStatusFromDataStore: SyncJobStatus?,
  ): SyncState {
    return when (syncJobStatusFromWorkManager) {
      is SyncJobStatus.Started,
      is SyncJobStatus.InProgress, -> Running(syncJobStatusFromWorkManager)
      null -> {
        when (workRequest) {
          WorkRequest.ONE_TIME ->
            handleNullWorkManagerStatusForOneTimeSync(workInfoState, syncJobStatusFromDataStore)
          WorkRequest.PERIODIC -> handleNullWorkManagerStatusForPeriodicSync(workInfoState)
        }
      }
      else -> error("Inconsistent syncJobStatus: $syncJobStatusFromWorkManager.")
    }
  }

  private fun handleNullWorkManagerStatus(
    workInfoState: WorkInfo.State,
    syncJobStatusFromDataStore: SyncJobStatus?,
  ): SyncState =
    when (workInfoState) {
      RUNNING -> Running(SyncJobStatus.Started())
      ENQUEUED -> Enqueued
      CANCELLED -> Cancelled
      SUCCEEDED,
      FAILED, -> handleFinishedOrFailedState(workInfoState, syncJobStatusFromDataStore)
      else -> error("Inconsistent WorkInfo.State: $workInfoState.")
    }

  /**
   * Only call this API when `syncJobStatusFromWorkManager` is null. Create a [SyncState] from
   * `syncJobStatusFromDataStore` if it is not null; otherwise, create it from [WorkInfo.State].
   */
  private fun handleNullWorkManagerStatusForOneTimeSync(
    workInfoState: WorkInfo.State,
    syncJobStatusFromDataStore: SyncJobStatus?,
  ): SyncState {
    syncJobStatusFromDataStore?.let {
      return handleFinishedOrFailedState(workInfoState, syncJobStatusFromDataStore)
    }
    return when (workInfoState) {
      RUNNING -> Running(SyncJobStatus.Started())
      ENQUEUED -> Enqueued
      CANCELLED -> Cancelled
      SUCCEEDED,
      FAILED, -> handleFinishedOrFailedState(workInfoState, syncJobStatusFromDataStore)
      else -> error("Inconsistent WorkInfo.State: $workInfoState.")
    }
  }

  /**
   * Only call this API when syncJobStatusFromWorkManager is null. Create a [SyncState] from
   * [WorkInfo.State]. (Note: syncJobStatusFromDataStore is updated as lastSynJobStatus, which is
   * the terminalSyncJobStatus.)
   */
  private fun handleNullWorkManagerStatusForPeriodicSync(
    workInfoState: WorkInfo.State,
  ): SyncState =
    when (workInfoState) {
      RUNNING -> Running(SyncJobStatus.Started())
      ENQUEUED -> Enqueued
      CANCELLED -> Cancelled
      else -> error("Inconsistent WorkInfo.State in periodic sync : $workInfoState.")
    }

  private fun handleFinishedOrFailedState(
    workInfoState: WorkInfo.State,
    syncJobStatusFromDataStore: SyncJobStatus?,
  ): SyncState =
    syncJobStatusFromDataStore?.let {
      when (it) {
        is SyncJobStatus.Finished -> Succeeded(it)
        is SyncJobStatus.Failed -> Failed(it)
        else -> error("Inconsistent terminal syncJobStatus : $syncJobStatusFromDataStore")
      }
    }
      ?: when (workInfoState) {
        SUCCEEDED -> Succeeded(SyncJobStatus.Finished())
        FAILED -> error("WorkInfo.State is $workInfoState")
        else -> error("Inconsistent WorkInfo.State: $workInfoState.")
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
    lastSyncJobStatus: SyncJobStatus?,
  ) =
    lastSyncJobStatus?.let {
      when (it) {
        is SyncJobStatus.Finished -> Result.Succeeded(it.timestamp)
        is SyncJobStatus.Failed -> Result.Failed(lastSyncJobStatus.timestamp)
        else -> error("Inconsistent terminal syncJobStatus : $lastSyncJobStatus")
      }
    }

  private enum class WorkRequest {
    ONE_TIME,
    PERIODIC,
  }
}
