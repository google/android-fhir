/*
 * Copyright 2020 Google LLC
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
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.fhir.FhirEngine

object Sync {
  /**
   * Does a one time sync based on [ResourceSyncParams]. Returns a [Result] that tells caller
   * whether process was Success or Failure. In case of failure, caller needs to take care of the
   * retry
   */
  suspend fun oneTimeSync(
    fhirEngine: FhirEngine,
    dataSource: DataSource,
    resourceSyncParams: ResourceSyncParams
  ): Result {
    return FhirSynchronizer(fhirEngine, dataSource, resourceSyncParams).synchronize()
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
        createOneTimeWorkRequest<W>(retryConfiguration)
      )
  }
  /**
   * Starts a periodic sync based on [FhirSyncWorker]. It takes [PeriodicSyncConfiguration] to
   * determine the sync frequency and [RetryConfiguration] to guide the retry mechanism. Caller can
   * set [retryConfiguration] to [null] to stop retry.
   */
  inline fun <reified W : FhirSyncWorker> periodicSync(
    context: Context,
    periodicSyncConfiguration: PeriodicSyncConfiguration,
  ) {

    WorkManager.getInstance(context)
      .enqueueUniquePeriodicWork(
        SyncWorkType.DOWNLOAD.workerName,
        ExistingPeriodicWorkPolicy.KEEP,
        createPeriodicWorkRequest<W>(periodicSyncConfiguration)
      )
  }

  @PublishedApi
  internal inline fun <reified W : FhirSyncWorker> createOneTimeWorkRequest(
    retryConfiguration: RetryConfiguration?
  ): OneTimeWorkRequest {
    val oneTimeWorkRequest = OneTimeWorkRequestBuilder<W>()
    retryConfiguration?.let {
      oneTimeWorkRequest.setBackoffCriteria(
        it.backoffCriteria.backoffPolicy,
        it.backoffCriteria.backoffDelay,
        it.backoffCriteria.timeUnit
      )
      oneTimeWorkRequest.setInputData(
        Data.Builder().putInt(MAX_RETRIES_ALLOWED, it.maxRetries).build()
      )
    }
    return oneTimeWorkRequest.build()
  }

  @PublishedApi
  internal inline fun <reified W : FhirSyncWorker> createPeriodicWorkRequest(
    periodicSyncConfiguration: PeriodicSyncConfiguration
  ): PeriodicWorkRequest {
    val periodicWorkRequestBuilder =
      PeriodicWorkRequestBuilder<W>(
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
  DOWNLOAD("download"),
  UPLOAD("upload")
}
