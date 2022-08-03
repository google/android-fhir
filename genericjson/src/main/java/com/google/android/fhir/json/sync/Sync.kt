/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir.json.sync

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.android.fhir.json.JsonEngine
import com.google.android.fhir.json.JsonEngineProvider
import org.hl7.fhir.r4.model.ResourceType

object Sync {
  fun basicSyncJob(context: Context): SyncJob {
    return SyncJobImpl(context)
  }

  /**
   * Does a one time sync based on [ResourceSearchParams]. Returns a [Result] that tells caller
   * whether process was Success or Failure. In case of failure, caller needs to take care of the
   * retry
   */
  // TODO: Check if this api is required anymore since we have SyncJob.run to do the same work.
  suspend fun oneTimeSync(
    context: Context,
    jsonEngine: JsonEngine,
    downloadManager: DownloadWorkManager,
    resolver: ConflictResolver
  ): Result {
    return JsonEngineProvider.getDataSource(context)?.let {
      JsonSynchronizer(context, jsonEngine, it, downloadManager, conflictResolver = resolver)
        .synchronize()
    }
      ?: Result.Error(
        listOf(
          ResourceSyncException(
            ResourceType.Bundle,
            IllegalStateException(
              "JsonEngineConfiguration.ServerConfiguration is not set. Call JsonEngineProvider.init to initialize with appropriate configuration."
            )
          )
        )
      )
  }

  /**
   * Starts a one time sync based on [JsonSyncWorker]. In case of a failure, [RetryConfiguration]
   * will guide the retry mechanism. Caller can set [retryConfiguration] to [null] to stop retry.
   */
  inline fun <reified W : JsonSyncWorker> oneTimeSync(
    context: Context,
    retryConfiguration: RetryConfiguration? = defaultRetryConfiguration
  ) {
    WorkManager.getInstance(context)
      .enqueueUniqueWork(
        SyncWorkType.DOWNLOAD.workerName,
        ExistingWorkPolicy.KEEP,
        createOneTimeWorkRequest(retryConfiguration, W::class.java)
      )
  }
  /**
   * Starts a periodic sync based on [JsonSyncWorker]. It takes [PeriodicSyncConfiguration] to
   * determine the sync frequency and [RetryConfiguration] to guide the retry mechanism. Caller can
   * set [retryConfiguration] to [null] to stop retry.
   */
  inline fun <reified W : JsonSyncWorker> periodicSync(
    context: Context,
    periodicSyncConfiguration: PeriodicSyncConfiguration
  ) {

    WorkManager.getInstance(context)
      .enqueueUniquePeriodicWork(
        SyncWorkType.DOWNLOAD.workerName,
        ExistingPeriodicWorkPolicy.KEEP,
        createPeriodicWorkRequest(periodicSyncConfiguration, W::class.java)
      )
  }

  @PublishedApi
  internal inline fun <W : JsonSyncWorker> createOneTimeWorkRequest(
    retryConfiguration: RetryConfiguration?,
    clazz: Class<W>
  ): OneTimeWorkRequest {
    val oneTimeWorkRequestBuilder = OneTimeWorkRequest.Builder(clazz)
    retryConfiguration?.let {
      oneTimeWorkRequestBuilder.setBackoffCriteria(
        it.backoffCriteria.backoffPolicy,
        it.backoffCriteria.backoffDelay,
        it.backoffCriteria.timeUnit
      )
      oneTimeWorkRequestBuilder.setInputData(
        Data.Builder().putInt(MAX_RETRIES_ALLOWED, it.maxRetries).build()
      )
    }
    return oneTimeWorkRequestBuilder.build()
  }

  @PublishedApi
  internal inline fun <W : JsonSyncWorker> createPeriodicWorkRequest(
    periodicSyncConfiguration: PeriodicSyncConfiguration,
    clazz: Class<W>
  ): PeriodicWorkRequest {
    val periodicWorkRequestBuilder =
      PeriodicWorkRequest.Builder(
          clazz,
          periodicSyncConfiguration.repeat.interval,
          periodicSyncConfiguration.repeat.timeUnit
        )
        .setConstraints(periodicSyncConfiguration.syncConstraints)

    periodicSyncConfiguration.retryConfiguration?.let {
      periodicWorkRequestBuilder.setBackoffCriteria(
        it.backoffCriteria.backoffPolicy,
        it.backoffCriteria.backoffDelay,
        it.backoffCriteria.timeUnit
      )
      periodicWorkRequestBuilder.setInputData(
        Data.Builder().putInt(MAX_RETRIES_ALLOWED, it.maxRetries).build()
      )
    }
    return periodicWorkRequestBuilder.build()
  }
}

/** Defines different types of synchronisation workers: download and upload */
enum class SyncWorkType(val workerName: String) {
  DOWNLOAD_UPLOAD("fhir-engine-download-upload-worker"),
  DOWNLOAD("download"),
  UPLOAD("upload")
}
