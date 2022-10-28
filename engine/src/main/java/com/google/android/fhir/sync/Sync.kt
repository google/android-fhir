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

package com.google.android.fhir.sync

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineProvider
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
    fhirEngine: FhirEngine,
    downloadManager: DownloadWorkManager,
    downloadWorkManagerModified: DownloadWorkManagerModified,
    uploadConfiguration: UploadConfiguration = UploadConfiguration(),
    resolver: ConflictResolver
  ): Result {
    return FhirEngineProvider.getDataSource(context)?.let {
      FhirSynchronizer(
          context,
          fhirEngine,
          it,
          downloadManager,
          downloadWorkManagerModified,
          conflictResolver = resolver
        )
        .synchronize(SyncWorkType.DOWNLOAD_UPLOAD)
    }
      ?: Result.Error(
        listOf(
          ResourceSyncException(
            ResourceType.Bundle,
            IllegalStateException(
              "FhirEngineConfiguration.ServerConfiguration is not set. Call FhirEngineProvider.init to initialize with appropriate configuration."
            )
          )
        )
      )
  }

  /**
   * Starts a one time sync based on [FhirSyncWorker]. In case of a failure, [RetryConfiguration]
   * will guide the retry mechanism. Caller can set [retryConfiguration] to [null] to stop retry.
   */
  inline fun <reified W : FhirSyncWorker> oneTimeSync(
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
   * Starts a periodic sync based on [FhirSyncWorker]. It takes [PeriodicSyncConfiguration] to
   * determine the sync frequency and [RetryConfiguration] to guide the retry mechanism. Caller can
   * set [retryConfiguration] to [null] to stop retry.
   */
  inline fun <reified W : FhirSyncWorker> periodicSync(
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
  internal inline fun <W : FhirSyncWorker> createOneTimeWorkRequest(
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
  internal inline fun <W : FhirSyncWorker> createPeriodicWorkRequest(
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
