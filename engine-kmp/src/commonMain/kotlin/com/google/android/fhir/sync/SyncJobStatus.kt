/*
 * Copyright 2022-2026 Google LLC
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

import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.serialization.Serializable

/**
 * Data class representing the state of a periodic synchronization operation. It is a combined state
 * of [WorkInfo.State] and [SyncJobStatus]. See [CurrentSyncJobStatus] and [LastSyncJobStatus] for
 * more details.
 *
 * @property lastSyncJobStatus The result of the last synchronization job [LastSyncJobStatus]. It
 *   only represents terminal states.
 * @property currentSyncJobStatus The current state of the synchronization job
 *   [CurrentSyncJobStatus].
 */
data class PeriodicSyncJobStatus(
  val lastSyncJobStatus: LastSyncJobStatus?,
  val currentSyncJobStatus: CurrentSyncJobStatus,
)

/**
 * Sealed class representing the result of a synchronization operation. These are terminal states of
 * the sync operation, representing [Succeeded] and [Failed].
 *
 * @property timestamp The timestamp when the synchronization result occurred.
 */
sealed class LastSyncJobStatus(val timestamp: Instant) {
  /** Represents a successful synchronization result. */
  class Succeeded(timestamp: Instant) : LastSyncJobStatus(timestamp)

  /** Represents a failed synchronization result. */
  class Failed(timestamp: Instant) : LastSyncJobStatus(timestamp)
}

/**
 * Sealed class representing different states of a synchronization operation. In Android for
 * example, it combines WorkInfo.State and [SyncJobStatus]. Enqueued state represents
 * WorkInfo.State.ENQUEUED where [SyncJobStatus] is not applicable. Running state is a combined
 * state of WorkInfo.State.ENQUEUED and [SyncJobStatus.Started] or [SyncJobStatus.InProgress].
 * Succeeded state is a combined state of WorkInfo.State.SUCCEEDED and [SyncJobStatus.Started] or
 * [SyncJobStatus.Succeeded]. Failed state is a combined state of WorkInfo.State.FAILED and
 * [SyncJobStatus.Failed]. Cancelled state represents WorkInfo.State.CANCELLED where [SyncJobStatus]
 * is not applicable.
 */
sealed class CurrentSyncJobStatus {
  /** State indicating that the synchronization operation is enqueued. */
  object Enqueued : CurrentSyncJobStatus()

  /**
   * State indicating that the synchronization operation is running.
   *
   * @param inProgressSyncJob The current status of the synchronization job.
   */
  data class Running(val inProgressSyncJob: SyncJobStatus) : CurrentSyncJobStatus()

  /**
   * State indicating that the synchronization operation succeeded.
   *
   * @param timestamp The timestamp when the synchronization result occurred.
   */
  class Succeeded(val timestamp: Instant) : CurrentSyncJobStatus()

  /**
   * State indicating that the synchronization operation failed.
   *
   * @param timestamp The timestamp when the synchronization result occurred.
   */
  class Failed(val timestamp: Instant) : CurrentSyncJobStatus()

  /** State indicating that the synchronization operation is canceled. */
  object Cancelled : CurrentSyncJobStatus()

  /** State indicating that the synchronization operation is blocked. */
  data object Blocked : CurrentSyncJobStatus()
}

/**
 * Sealed class representing different states of a synchronization operation. In Android, these
 * states do not represent WorkInfo.State, whereas [CurrentSyncJobStatus] combines WorkInfo.State]
 * and [SyncJobStatus] in one-time and periodic sync. For more details, see [CurrentSyncJobStatus]
 * and [PeriodicSyncJobStatus].
 */
@Serializable
sealed class SyncJobStatus {
  val timestamp: Instant = Clock.System.now()

  /** Sync job has been started on the client but the syncing is not necessarily in progress. */
  @Serializable @kotlinx.serialization.SerialName("Started") class Started : SyncJobStatus()

  /** Syncing in progress with the server. */
  @Serializable
  @kotlinx.serialization.SerialName("InProgress")
  data class InProgress(
    val syncOperation: SyncOperation,
    val total: Int = 0,
    val completed: Int = 0,
  ) : SyncJobStatus()

  /** Sync job finished successfully. */
  @Serializable @kotlinx.serialization.SerialName("Succeeded") class Succeeded : SyncJobStatus()

  /** Sync job failed. */
  @Serializable
  @kotlinx.serialization.SerialName("Failed")
  data class Failed(
    @kotlinx.serialization.Transient val exceptions: List<ResourceSyncException> = emptyList(),
  ) : SyncJobStatus()
}
