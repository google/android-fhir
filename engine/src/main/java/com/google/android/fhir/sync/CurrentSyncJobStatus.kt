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

import java.time.OffsetDateTime

/**
 * Sealed class representing different states of a synchronization operation. It combines
 * [WorkInfo.State] and [SyncJobStatus]. Enqueued state represents [WorkInfo.State.ENQUEUED] where
 * [SyncJobStatus] is not applicable. Running state is a combined state of [WorkInfo.State.ENQUEUED]
 * and [SyncJobStatus.Started] or [SyncJobStatus.InProgress]. Succeeded state is a combined state of
 * [WorkInfo.State.SUCCEEDED] and [SyncJobStatus.Started] or [SyncJobStatus.Succeeded]. Failed state
 * is a combined state of [WorkInfo.State.FAILED] and [SyncJobStatus.Failed]. Cancelled state
 * represents [WorkInfo.State.CANCELLED] where [SyncJobStatus] is not applicable.
 */
sealed class CurrentSyncJobStatus {
  /** State indicating that the synchronization operation is enqueued. */
  object Enqueued : CurrentSyncJobStatus()

  /**
   * State indicating that the synchronization operation is running.
   *
   * @param currentJob The current status of the synchronization job.
   */
  data class Running(val currentJob: SyncJobStatus) : CurrentSyncJobStatus()

  /**
   * State indicating that the synchronization operation succeeded.
   *
   * @param succeededJob The status of the succeeded synchronization job.
   */
  class Succeeded(val succeededJob: SyncJobStatus) : CurrentSyncJobStatus()

  /**
   * State indicating that the synchronization operation failed.
   *
   * @param failedJob The status of the failed synchronization job.
   */
  data class Failed(val failedJob: SyncJobStatus) : CurrentSyncJobStatus()

  /** State indicating that the synchronization operation is canceled. */
  object Cancelled : CurrentSyncJobStatus()
}

/**
 * Data class representing the state of a periodic synchronization operation. It is a combined state
 * of [WorkInfo.State] and [SyncJobStatus]. See [CurrentSyncJobStatus] and [LastSyncJobStatus] for
 * more details.
 *
 * @property lastJobState The result of the last synchronization job [LastSyncJobStatus]. It only
 *   represents terminal states.
 * @property currentJobState The current state of the synchronization job [CurrentSyncJobStatus].
 */
data class PeriodicSyncState(
  val lastJobState: LastSyncJobStatus?,
  val currentJobState: CurrentSyncJobStatus,
)

/**
 * Sealed class representing the result of a synchronization operation. These are terminal states of
 * the sync operation, representing [Succeeded] and [Failed].
 *
 * @property timestamp The timestamp when the synchronization result occurred.
 */
sealed class LastSyncJobStatus(val timestamp: OffsetDateTime) {
  /** Represents a successful synchronization result. */
  class Succeeded(timestamp: OffsetDateTime) : LastSyncJobStatus(timestamp)

  /** Represents a failed synchronization result. */
  class Failed(timestamp: OffsetDateTime) : LastSyncJobStatus(timestamp)
}
