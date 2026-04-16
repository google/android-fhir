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

import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow

object Sync {

  /**
   * Starts a one time sync job using the provided [scheduler].
   *
   * Use the returned [Flow] to get updates of the sync job.
   *
   * @param scheduler the [SyncScheduler] to use for scheduling.
   * @param retryConfiguration configuration to guide the retry mechanism, or `null` to stop retry.
   * @return a [Flow] of [CurrentSyncJobStatus]
   */
  @PublishedApi
  internal suspend fun oneTimeSync(
    scheduler: SyncScheduler,
    retryConfiguration: RetryConfiguration? = defaultRetryConfiguration,
  ): Flow<CurrentSyncJobStatus> = scheduler.runOneTimeSync(retryConfiguration)

  /**
   * Starts a periodic sync job using the provided [scheduler].
   *
   * Use the returned [Flow] to get updates of the sync job.
   *
   * @param scheduler the [SyncScheduler] to use for scheduling.
   * @param config configuration to determine the sync frequency and retry mechanism
   * @return a [Flow] of [PeriodicSyncJobStatus]
   */
  @PublishedApi
  internal suspend fun periodicSync(
    scheduler: SyncScheduler,
    config: PeriodicSyncConfiguration,
  ): Flow<PeriodicSyncJobStatus> = scheduler.schedulePeriodicSync(config)

  suspend fun cancelOneTimeSync(scheduler: SyncScheduler) = scheduler.cancelOneTimeSync()

  suspend fun cancelPeriodicSync(scheduler: SyncScheduler) = scheduler.cancelPeriodicSync()

  /** Gets the timestamp of the last sync job. */
  internal suspend fun getLastSyncTimestamp(stateStore: FhirDataStore): Instant? =
    stateStore.readLastSyncTimestamp()
}
