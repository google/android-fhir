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
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.sync.Result.Success

/** A WorkManager Worker that handles periodic sync. */
abstract class FhirSyncWorker(appContext: Context, workerParams: WorkerParameters) :
  CoroutineWorker(appContext, workerParams) {

  abstract fun getFhirEngine(): FhirEngine
  abstract fun getDataSource(): DataSource
  abstract fun getSyncData(): ResourceSyncParams

  override suspend fun doWork(): Result {
    val result = FhirSynchronizer(getFhirEngine(), getDataSource(), getSyncData()).synchronize()
    /**
     * In case of failure, we can check if its worth retrying and do retry based on
     * [RetryConfiguration.maxRetries] set by user.
     */
    val retries = inputData.getInt(MAX_RETRIES_ALLOWED, 0)
    return when {
      result is Success -> {
        Result.success()
      }
      retries > runAttemptCount -> {
        Result.retry()
      }
      else -> {
        Result.failure()
      }
    }
  }
}
