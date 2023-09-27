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
import androidx.work.WorkManager
import androidx.work.hasKeyWithValueOfType
import com.google.android.fhir.DatastoreUtil
import com.google.android.fhir.OffsetDateTimeTypeAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.OffsetDateTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
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
   * @return a [Flow] of [SyncJobStatus]
   */
  inline fun <reified W : FhirSyncWorker> oneTimeSync(
    context: Context,
    retryConfiguration: RetryConfiguration? = defaultRetryConfiguration,
  ): Flow<SyncJobStatus> {
    val uniqueWorkName = "${W::class.java.name}-oneTimeSync"
    val flow = getWorkerInfo(context, uniqueWorkName)
    WorkManager.getInstance(context)
      .enqueueUniqueWork(
        uniqueWorkName,
        ExistingWorkPolicy.KEEP,
        createOneTimeWorkRequest(retryConfiguration, W::class.java),
      )
    return flow
  }

  /**
   * Starts a periodic sync job based on [FhirSyncWorker].
   *
   * Use the returned [Flow] to get updates of the sync job. Alternatively, use [getWorkerInfo] with
   * the same [FhirSyncWorker] to retrieve the status of the job.
   *
   * @param periodicSyncConfiguration configuration to determine the sync frequency and retry
   *   mechanism
   * @return a [Flow] of [SyncJobStatus]
   */
  @ExperimentalCoroutinesApi
  inline fun <reified W : FhirSyncWorker> periodicSync(
    context: Context,
    periodicSyncConfiguration: PeriodicSyncConfiguration,
  ): Flow<SyncJobStatus> {
    val uniqueWorkName = "${W::class.java.name}-periodicSync"
    val flow = getWorkerInfo(context, uniqueWorkName)
    WorkManager.getInstance(context)
      .enqueueUniquePeriodicWork(
        uniqueWorkName,
        ExistingPeriodicWorkPolicy.KEEP,
        createPeriodicWorkRequest(periodicSyncConfiguration, W::class.java),
      )
    return flow
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
  internal inline fun <W : FhirSyncWorker> createOneTimeWorkRequest(
    retryConfiguration: RetryConfiguration?,
    clazz: Class<W>,
  ): OneTimeWorkRequest {
    val oneTimeWorkRequestBuilder = OneTimeWorkRequest.Builder(clazz)
    retryConfiguration?.let {
      oneTimeWorkRequestBuilder.setBackoffCriteria(
        it.backoffCriteria.backoffPolicy,
        it.backoffCriteria.backoffDelay,
        it.backoffCriteria.timeUnit,
      )
      oneTimeWorkRequestBuilder.setInputData(
        Data.Builder().putInt(MAX_RETRIES_ALLOWED, it.maxRetries).build(),
      )
    }
    return oneTimeWorkRequestBuilder.build()
  }

  @PublishedApi
  internal inline fun <W : FhirSyncWorker> createPeriodicWorkRequest(
    periodicSyncConfiguration: PeriodicSyncConfiguration,
    clazz: Class<W>,
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
        Data.Builder().putInt(MAX_RETRIES_ALLOWED, it.maxRetries).build(),
      )
    }
    return periodicWorkRequestBuilder.build()
  }

  /** Gets the timestamp of the last sync job. */
  fun getLastSyncTimestamp(context: Context): OffsetDateTime? {
    return DatastoreUtil(context).readLastSyncTimestamp()
  }
}
