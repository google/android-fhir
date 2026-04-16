/*
 * Copyright 2025-2026 Google LLC
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
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.hasKeyWithValueOfType
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.json.Json

/**
 * Android implementation of [SyncScheduler] that uses [WorkManager] to schedule sync jobs.
 *
 * @param context The application context.
 * @param workerClass The class of the [FhirSyncWorker] to be used for sync jobs.
 * @param dataStore The [FhirDataStore] instance for persisting sync state and metadata.
 */
@PublishedApi
internal class AndroidSyncScheduler(
  private val context: Context,
  private val workerClass: Class<out FhirSyncWorker>,
  private val dataStore: FhirDataStore,
) : SyncScheduler {

  private val json = Json { ignoreUnknownKeys = true }

  /**
   * Starts a one time sync job based on [FhirSyncWorker].
   *
   * Use the returned [Flow] to get updates of the sync job. Alternatively, use [getWorkerInfo] with
   * the same 'workName' to retrieve the status of the job.
   *
   * @param retryConfiguration configuration to guide the retry mechanism, or `null` to stop retry.
   * @return a [Flow] of [CurrentSyncJobStatus]
   */
  override suspend fun runOneTimeSync(
    retryConfiguration: RetryConfiguration?,
  ): Flow<CurrentSyncJobStatus> {
    val uniqueWorkName = createSyncUniqueName("oneTimeSync")
    val flow = getWorkerInfo(uniqueWorkName)
    val oneTimeWorkRequest = createOneTimeWorkRequest(retryConfiguration, uniqueWorkName)
    WorkManager.getInstance(context)
      .enqueueUniqueWork(
        uniqueWorkName,
        ExistingWorkPolicy.KEEP,
        oneTimeWorkRequest,
      )
    storeUniqueWorkNameInDataStore(uniqueWorkName)
    return combineSyncStateForOneTimeSync(uniqueWorkName, flow)
  }

  /**
   * Starts a periodic sync job based on [FhirSyncWorker].
   *
   * Use the returned [Flow] to get updates of the sync job. Alternatively, use [getWorkerInfo] with
   * the same 'workName' to retrieve the status of the job.
   *
   * @param config configuration to determine the sync frequency and retry mechanism
   * @return a [Flow] of [PeriodicSyncJobStatus]
   */
  override suspend fun schedulePeriodicSync(
    config: PeriodicSyncConfiguration,
  ): Flow<PeriodicSyncJobStatus> {
    val uniqueWorkName = createSyncUniqueName("periodicSync")
    val flow = getWorkerInfo(uniqueWorkName)
    val periodicWorkRequest = createPeriodicWorkRequest(config, uniqueWorkName)
    WorkManager.getInstance(context)
      .enqueueUniquePeriodicWork(
        uniqueWorkName,
        ExistingPeriodicWorkPolicy.KEEP,
        periodicWorkRequest,
      )
    storeUniqueWorkNameInDataStore(uniqueWorkName)
    return combineSyncStateForPeriodicSync(uniqueWorkName, flow)
  }

  override suspend fun cancelOneTimeSync() {
    cancelSync("oneTimeSync")
  }

  override suspend fun cancelPeriodicSync() {
    cancelSync("periodicSync")
  }

  @PublishedApi
  internal suspend fun cancelSync(syncType: String) {
    val uniqueWorkNameAsKey = createSyncUniqueName(syncType)
    val uniqueWorkNameValueFromDataStore = dataStore.fetchUniqueWorkName(uniqueWorkNameAsKey)
    if (uniqueWorkNameValueFromDataStore != null) {
      WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkNameValueFromDataStore)
    }
  }

  /**
   * Retrieves the work information for a specific unique work name as a flow of pairs containing
   * the work state and the corresponding progress data if available.
   *
   * @param workName The unique name of the work to retrieve information for.
   * @return A flow emitting pairs of [WorkInfo.State] and [SyncJobStatus].
   */
  @OptIn(ExperimentalCoroutinesApi::class)
  private fun getWorkerInfo(workName: String): Flow<Pair<WorkInfo.State, SyncJobStatus?>> =
    WorkManager.getInstance(context)
      .getWorkInfosForUniqueWorkLiveData(workName)
      .asFlow()
      .flatMapConcat { list: List<WorkInfo> -> list.asFlow() }
      .mapNotNull { workInfo: WorkInfo ->
        val syncStatus =
          workInfo.progress
            .takeIf { it.keyValueMap.isNotEmpty() && it.hasKeyWithValueOfType<String>("State") }
            ?.let {
              val stateType = it.getString("StateType")
              val stateData = it.getString("State")
              stateData?.let { data ->
                if (stateType != null) {
                  // Use Polymorphic serialization via SyncJobStatus
                  json.decodeFromString<SyncJobStatus>(data)
                } else {
                  json.decodeFromString<SyncJobStatus>(data)
                }
              }
            }
        workInfo.state to syncStatus
      }

  /**
   * Combines the sync state for a one-time sync operation, including work state, progress, and
   * terminal states.
   *
   * @param workName The name of the one-time sync work.
   * @param workerInfoFlow A flow representing the progress of the sync job.
   * @return A flow of [CurrentSyncJobStatus] combining the sync job states.
   */
  private fun combineSyncStateForOneTimeSync(
    workName: String,
    workerInfoFlow: Flow<Pair<WorkInfo.State, SyncJobStatus?>>,
  ): Flow<CurrentSyncJobStatus> {
    val syncJobStatusInDataStoreFlow: Flow<LastSyncJobStatus?> =
      dataStore.observeLastSyncJobStatus(workName)

    return combine(workerInfoFlow, syncJobStatusInDataStoreFlow) {
      workerInfoSyncJobStatusPairFromWorkManager,
      syncJobStatusFromDataStore,
      ->
      createSyncStateForOneTimeSync(
        workName,
        workerInfoSyncJobStatusPairFromWorkManager.first,
        workerInfoSyncJobStatusPairFromWorkManager.second,
        syncJobStatusFromDataStore,
      )
    }
  }

  /**
   * Combines the sync state for a periodic sync operation, including work state, progress, and
   * terminal states.
   *
   * @param workName The name of the periodic sync work.
   * @param workerInfoFlow A flow representing the progress of the sync job.
   * @return A flow of [PeriodicSyncJobStatus] combining the sync job states.
   */
  private fun combineSyncStateForPeriodicSync(
    workName: String,
    workerInfoFlow: Flow<Pair<WorkInfo.State, SyncJobStatus?>>,
  ): Flow<PeriodicSyncJobStatus> {
    val syncJobStatusInDataStoreFlow: Flow<LastSyncJobStatus?> =
      dataStore.observeLastSyncJobStatus(workName)
    return combine(workerInfoFlow, syncJobStatusInDataStoreFlow) {
      workerInfoSyncJobStatusPairFromWorkManager,
      syncJobStatusFromDataStore,
      ->
      PeriodicSyncJobStatus(
        lastSyncJobStatus = syncJobStatusFromDataStore,
        currentSyncJobStatus =
          createSyncStateForPeriodicSync(
            workName,
            workerInfoSyncJobStatusPairFromWorkManager.first,
            workerInfoSyncJobStatusPairFromWorkManager.second,
          ),
      )
    }
  }

  private suspend fun createSyncStateForOneTimeSync(
    uniqueWorkName: String,
    workInfoState: WorkInfo.State,
    syncJobStatusFromWorkManager: SyncJobStatus?,
    syncJobStatusFromDataStore: LastSyncJobStatus?,
  ): CurrentSyncJobStatus {
    return when (workInfoState) {
      WorkInfo.State.ENQUEUED -> CurrentSyncJobStatus.Enqueued
      WorkInfo.State.RUNNING -> {
        when (syncJobStatusFromWorkManager) {
          is SyncJobStatus.Started,
          is SyncJobStatus.InProgress, -> CurrentSyncJobStatus.Running(syncJobStatusFromWorkManager)
          is SyncJobStatus.Succeeded ->
            CurrentSyncJobStatus.Succeeded(syncJobStatusFromWorkManager.timestamp)
          is SyncJobStatus.Failed ->
            CurrentSyncJobStatus.Failed(syncJobStatusFromWorkManager.timestamp)
          null -> CurrentSyncJobStatus.Running(SyncJobStatus.Started())
        }
      }
      WorkInfo.State.SUCCEEDED -> {
        removeUniqueWorkNameInDataStore(uniqueWorkName)
        syncJobStatusFromDataStore?.let {
          when (it) {
            is LastSyncJobStatus.Succeeded -> CurrentSyncJobStatus.Succeeded(it.timestamp)
            else -> error("Inconsistent terminal syncJobStatus : $syncJobStatusFromDataStore")
          }
        }
          ?: error("Inconsistent terminal syncJobStatus.")
      }
      WorkInfo.State.FAILED -> {
        removeUniqueWorkNameInDataStore(uniqueWorkName)
        syncJobStatusFromDataStore?.let {
          when (it) {
            is LastSyncJobStatus.Failed -> CurrentSyncJobStatus.Failed(it.timestamp)
            else -> error("Inconsistent terminal syncJobStatus : $syncJobStatusFromDataStore")
          }
        }
          ?: error("Inconsistent terminal syncJobStatus.")
      }
      WorkInfo.State.CANCELLED -> {
        removeUniqueWorkNameInDataStore(uniqueWorkName)
        CurrentSyncJobStatus.Cancelled
      }
      WorkInfo.State.BLOCKED -> CurrentSyncJobStatus.Blocked
    }
  }

  private suspend fun createSyncStateForPeriodicSync(
    uniqueWorkName: String,
    workInfoState: WorkInfo.State,
    syncJobStatusFromWorkManager: SyncJobStatus?,
  ): CurrentSyncJobStatus {
    return when (workInfoState) {
      WorkInfo.State.ENQUEUED -> CurrentSyncJobStatus.Enqueued
      WorkInfo.State.RUNNING -> {
        return when (syncJobStatusFromWorkManager) {
          is SyncJobStatus.Started,
          is SyncJobStatus.InProgress, -> CurrentSyncJobStatus.Running(syncJobStatusFromWorkManager)
          is SyncJobStatus.Succeeded ->
            CurrentSyncJobStatus.Succeeded(syncJobStatusFromWorkManager.timestamp)
          is SyncJobStatus.Failed ->
            CurrentSyncJobStatus.Failed(syncJobStatusFromWorkManager.timestamp)
          null -> CurrentSyncJobStatus.Running(SyncJobStatus.Started())
        }
      }
      WorkInfo.State.CANCELLED -> {
        removeUniqueWorkNameInDataStore(uniqueWorkName)
        CurrentSyncJobStatus.Cancelled
      }
      WorkInfo.State.BLOCKED -> CurrentSyncJobStatus.Blocked
      else -> error("Inconsistent WorkInfo.State in periodic sync : $workInfoState.")
    }
  }

  @PublishedApi
  internal fun createSyncUniqueName(syncType: String): String {
    return "${workerClass.name}-$syncType"
  }

  @PublishedApi
  internal suspend fun storeUniqueWorkNameInDataStore(
    uniqueWorkName: String,
  ) {
    if (dataStore.fetchUniqueWorkName(uniqueWorkName) == null) {
      dataStore.storeUniqueWorkName(key = uniqueWorkName, value = uniqueWorkName)
    }
  }

  @PublishedApi
  internal suspend fun removeUniqueWorkNameInDataStore(
    uniqueWorkName: String,
  ) {
    if (dataStore.fetchUniqueWorkName(uniqueWorkName) != null) {
      dataStore.removeUniqueWorkName(key = uniqueWorkName)
    }
  }

  private fun createOneTimeWorkRequest(
    retryConfiguration: RetryConfiguration?,
    uniqueWorkName: String,
  ): OneTimeWorkRequest {
    val builder = OneTimeWorkRequest.Builder(workerClass)
    retryConfiguration?.let {
      builder.setBackoffCriteria(
        it.backoffCriteria.backoffPolicy.toWorkManagerBackoffPolicy(),
        it.backoffCriteria.backoffDelay.inWholeMilliseconds,
        TimeUnit.MILLISECONDS,
      )
      builder.setInputData(
        Data.Builder()
          .putInt(MAX_RETRIES_ALLOWED, it.maxRetries)
          .putString(UNIQUE_WORK_NAME, uniqueWorkName)
          .build(),
      )
    }
    return builder.build()
  }

  private fun createPeriodicWorkRequest(
    periodicSyncConfiguration: PeriodicSyncConfiguration,
    uniqueWorkName: String,
  ): PeriodicWorkRequest {
    val builder =
      PeriodicWorkRequest.Builder(
          workerClass,
          periodicSyncConfiguration.repeat.interval.inWholeMilliseconds,
          TimeUnit.MILLISECONDS,
        )
        .setConstraints(periodicSyncConfiguration.syncConstraints.toWorkManagerConstraints())

    periodicSyncConfiguration.retryConfiguration?.let {
      builder.setBackoffCriteria(
        it.backoffCriteria.backoffPolicy.toWorkManagerBackoffPolicy(),
        it.backoffCriteria.backoffDelay.inWholeMilliseconds,
        TimeUnit.MILLISECONDS,
      )
      builder.setInputData(
        Data.Builder()
          .putInt(MAX_RETRIES_ALLOWED, it.maxRetries)
          .putString(UNIQUE_WORK_NAME, uniqueWorkName)
          .build(),
      )
    }
    return builder.build()
  }
}

private fun com.google.android.fhir.sync.BackoffPolicy.toWorkManagerBackoffPolicy() =
  when (this) {
    com.google.android.fhir.sync.BackoffPolicy.EXPONENTIAL -> BackoffPolicy.EXPONENTIAL
    com.google.android.fhir.sync.BackoffPolicy.LINEAR -> BackoffPolicy.LINEAR
  }

private fun SyncConstraints.toWorkManagerConstraints() =
  Constraints.Builder()
    .apply {
      setRequiredNetworkType(
        when (requiredNetworkType) {
          NetworkType.NOT_REQUIRED -> androidx.work.NetworkType.NOT_REQUIRED
          NetworkType.CONNECTED -> androidx.work.NetworkType.CONNECTED
          NetworkType.UNMETERED -> androidx.work.NetworkType.UNMETERED
          NetworkType.NOT_ROAMING -> androidx.work.NetworkType.NOT_ROAMING
          NetworkType.METERED -> androidx.work.NetworkType.METERED
        },
      )
      setRequiresBatteryNotLow(requiresBatteryNotLow)
      setRequiresCharging(requiresCharging)
      setRequiresDeviceIdle(requiresDeviceIdle)
      setRequiresStorageNotLow(requiresStorageNotLow)
    }
    .build()
