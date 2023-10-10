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
import android.util.Log
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
import com.google.android.fhir.DatastoreUtil
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.OffsetDateTimeTypeAdapter
import com.google.android.fhir.sync.OneTimeSyncState.Cancelled
import com.google.android.fhir.sync.OneTimeSyncState.Enqueued
import com.google.android.fhir.sync.OneTimeSyncState.Failed
import com.google.android.fhir.sync.OneTimeSyncState.Running
import com.google.android.fhir.sync.OneTimeSyncState.Succeeded
import com.google.android.fhir.sync.OneTimeSyncState.Unknown
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.OffsetDateTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
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
   * @return a [Flow] of [FhirSyncWorkStatus]
   */
  inline fun <reified W : FhirSyncWorker> oneTimeSync(
    context: Context,
    retryConfiguration: RetryConfiguration? = defaultRetryConfiguration,
  ): Flow<OneTimeSyncState> {
    val uniqueWorkName = "${W::class.java.name}-oneTimeSync"
    val flow = getWorkerInfo(context, uniqueWorkName)
    WorkManager.getInstance(context)
      .enqueueUniqueWork(
        uniqueWorkName,
        ExistingWorkPolicy.KEEP,
        createOneTimeWorkRequest(retryConfiguration, W::class.java, uniqueWorkName),
      )
    return combineSyncJobStatusAndWorkInfoStateForOnetimeSync(context, uniqueWorkName)
  }

  /**
   * Starts a periodic sync job based on [FhirSyncWorker].
   *
   * Use the returned [Flow] to get updates of the sync job. Alternatively, use [getWorkerInfo] with
   * the same [FhirSyncWorker] to retrieve the status of the job.
   *
   * @param periodicSyncConfiguration configuration to determine the sync frequency and retry
   *   mechanism
   * @return a [Flow] of [FhirSyncWorkStatus]
   */
  @ExperimentalCoroutinesApi
  inline fun <reified W : FhirSyncWorker> periodicSync(
    context: Context,
    periodicSyncConfiguration: PeriodicSyncConfiguration,
  ): Flow<PeriodicSyncState> {
    val uniqueWorkName = "${W::class.java.name}-periodicSync"
    val flow = getWorkerInfo(context, uniqueWorkName)
    WorkManager.getInstance(context)
      .enqueueUniquePeriodicWork(
        uniqueWorkName,
        ExistingPeriodicWorkPolicy.KEEP,
        createPeriodicWorkRequest(periodicSyncConfiguration, W::class.java, uniqueWorkName),
      )
    return createPeriodicSyncState(context, uniqueWorkName)
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

  @PublishedApi
  internal fun createPeriodicSyncState(
    context: Context,
    workName: String,
  ): Flow<PeriodicSyncState> {
    val workStateFlow: Flow<WorkInfo.State> = observeWorkState(context, workName)
    val syncJobStatusFlow: Flow<SyncJobStatus?>? =
      FhirEngineProvider.getFhirDataStore()?.getSyncJobStatusPreferencesFlow(workName)

    return if (syncJobStatusFlow != null) {
      workStateFlow.combine(syncJobStatusFlow) { workState, syncStatus ->
        val lastSyncJobStatus =
          FhirEngineProvider.getFhirDataStore()?.getLastSyncJobStatus(workName)
        PeriodicSyncState(
          lastJobState = lastSyncJobStatus?.let { createLastJobState(it) },
          currentJobState = syncStatus?.let { createCurrentJobState(it, workState) },
        )
      }
    } else {
      workStateFlow.map { workState ->
        Log.d("demo1", "work state : $workState")
        PeriodicSyncState()
      }
    }
  }

  @PublishedApi
  internal fun combineSyncJobStatusAndWorkInfoStateForOnetimeSync(
    context: Context,
    workName: String,
  ): Flow<OneTimeSyncState> {
    val workStateFlow: Flow<WorkInfo.State> = observeWorkState(context, workName)
    val syncJobStatusFlow: Flow<SyncJobStatus?>? =
      FhirEngineProvider.getFhirDataStore()?.getSyncJobStatusPreferencesFlow(workName)

    return if (syncJobStatusFlow != null) {
      workStateFlow.combine(syncJobStatusFlow) { workState, syncStatus ->
        createOneTimeSyncState(syncStatus, workState)
      }
    } else {
      workStateFlow.map { workState -> createOneTimeSyncState(schedulingStatus = workState) }
    }
  }

  private fun observeWorkState(context: Context, workName: String): Flow<WorkInfo.State> =
    WorkManager.getInstance(context)
      .getWorkInfosForUniqueWorkLiveData(workName)
      .asFlow()
      .flatMapConcat { it.asFlow() }
      .mapNotNull { workInfo -> workInfo.state }

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
          .putString(STRING_PREFERENCES_DATASTORE_KEY, uniqueWorkName)
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
      Data.Builder()
        .putInt(MAX_RETRIES_ALLOWED, it.maxRetries)
        .putString(STRING_PREFERENCES_DATASTORE_KEY, uniqueWorkName)
        .build()
    }
    return periodicWorkRequestBuilder.build()
  }

  /** Gets the timestamp of the last sync job. */
  fun getLastSyncTimestamp(context: Context): OffsetDateTime? {
    return DatastoreUtil(context).readLastSyncTimestamp()
  }

  private fun createOneTimeSyncState(
    syncJobStatus: SyncJobStatus? = null,
    schedulingStatus: WorkInfo.State,
  ): OneTimeSyncState {
    return when (schedulingStatus) {
      ENQUEUED -> {
        Enqueued()
      }
      RUNNING -> {
        Running(syncJobStatus!!)
      }
      SUCCEEDED -> {
        Succeeded(syncJobStatus!!)
      }
      FAILED -> {
        Failed(syncJobStatus!!)
      }
      CANCELLED -> {
        Cancelled()
      }
      else -> {
        Unknown()
      }
    }
  }

  private fun createLastJobState(
    lastSyncJobStatus: SyncJobStatus,
  ) =
    when (lastSyncJobStatus) {
      is SyncJobStatus.Finished -> {
        createOneTimeSyncState(lastSyncJobStatus, SUCCEEDED)
      }
      is SyncJobStatus.Failed, -> {
        createOneTimeSyncState(lastSyncJobStatus, FAILED)
      }
      else -> {
        null
      }
    }

  private fun createCurrentJobState(
    currentSyncJobStatus: SyncJobStatus,
    schedulingStatus: WorkInfo.State,
  ) =
    when (schedulingStatus) {
      RUNNING -> {
        when (currentSyncJobStatus) {
          is SyncJobStatus.Started,
          is SyncJobStatus.InProgress, -> {
            Running(currentSyncJobStatus)
          }
          is SyncJobStatus.Finished -> {
            Succeeded(currentSyncJobStatus)
          }
          is SyncJobStatus.Failed, -> {
            Failed(currentSyncJobStatus)
          }
        }
      }
      else -> {
        null
      }
    }
}
