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
import kotlinx.coroutines.flow.Flow

/**
 * Starts a one-time sync job using the specified [FhirSyncWorker] subclass.
 *
 * @param W The concrete [FhirSyncWorker] subclass to use for the sync job.
 * @param context The application [Context].
 * @param retryConfiguration Configuration to guide the retry mechanism, or `null` to disable retry.
 * @return A [Flow] of [CurrentSyncJobStatus] representing the sync job progress.
 */
suspend inline fun <reified W : FhirSyncWorker> Sync.oneTimeSync(
  context: Context,
  retryConfiguration: RetryConfiguration? = defaultRetryConfiguration,
): Flow<CurrentSyncJobStatus> {
  val dataStore = FhirDataStore(createDataStore(context))
  val scheduler = AndroidSyncScheduler(context, W::class.java, dataStore)
  return oneTimeSync(scheduler, retryConfiguration)
}

/**
 * Starts a periodic sync job using the specified [FhirSyncWorker] subclass.
 *
 * @param W The concrete [FhirSyncWorker] subclass to use for the sync job.
 * @param context The application [Context].
 * @param periodicSyncConfiguration Configuration to determine the sync frequency and retry
 *   mechanism.
 * @return A [Flow] of [PeriodicSyncJobStatus] representing the sync job progress.
 */
suspend inline fun <reified W : FhirSyncWorker> Sync.periodicSync(
  context: Context,
  periodicSyncConfiguration: PeriodicSyncConfiguration,
): Flow<PeriodicSyncJobStatus> {
  val dataStore = FhirDataStore(createDataStore(context))
  val scheduler = AndroidSyncScheduler(context, W::class.java, dataStore)
  return periodicSync(scheduler, periodicSyncConfiguration)
}

/**
 * Cancels a previously started one-time sync job for the specified [FhirSyncWorker] subclass.
 *
 * @param W The concrete [FhirSyncWorker] subclass whose sync job should be cancelled.
 * @param context The application [Context].
 */
suspend inline fun <reified W : FhirSyncWorker> Sync.cancelOneTimeSync(context: Context) {
  val dataStore = FhirDataStore(createDataStore(context))
  val scheduler = AndroidSyncScheduler(context, W::class.java, dataStore)
  cancelOneTimeSync(scheduler)
}

/**
 * Cancels a previously started periodic sync job for the specified [FhirSyncWorker] subclass.
 *
 * @param W The concrete [FhirSyncWorker] subclass whose sync job should be cancelled.
 * @param context The application [Context].
 */
suspend inline fun <reified W : FhirSyncWorker> Sync.cancelPeriodicSync(context: Context) {
  val dataStore = FhirDataStore(createDataStore(context))
  val scheduler = AndroidSyncScheduler(context, W::class.java, dataStore)
  cancelPeriodicSync(scheduler)
}
