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

/** Sealed class representing different states of a synchronization operation. */
sealed class SyncState {
  /** State indicating that the synchronization operation is enqueued. */
  object Enqueued : SyncState()

  /**
   * State indicating that the synchronization operation is running.
   *
   * @param currentJob The current status of the synchronization job.
   */
  data class Running(val currentJob: SyncJobStatus) : SyncState()

  /**
   * State indicating that the synchronization operation succeeded.
   *
   * @param succeededJob The status of the succeeded synchronization job.
   */
  class Succeeded(val succeededJob: SyncJobStatus) : SyncState()

  /**
   * State indicating that the synchronization operation failed.
   *
   * @param failedJob The status of the failed synchronization job.
   */
  data class Failed(val failedJob: SyncJobStatus) : SyncState()

  /** State indicating that the synchronization operation is canceled. */
  object Cancelled : SyncState()

  /** State indicating that the synchronization operation is in an unknown state. */
  object Unknown : SyncState()
}

/**
 * Data class representing the state of a periodic synchronization operation.
 *
 * @property lastJobState The result of the last synchronization job.
 * @property currentJobState The current state of the synchronization job.
 */
data class PeriodicSyncState(
  val lastJobState: Result? = null,
  val currentJobState: SyncState? = null,
)

/**
 * Sealed class representing the result of a synchronization operation.
 *
 * @property timestamp The timestamp when the synchronization result occurred.
 */
sealed class Result(val timestamp: OffsetDateTime) {
  /**
   * Represents a successful synchronization result.
   *
   * @property timestamp The timestamp when the synchronization succeeded.
   */
  class Succeeded(timestamp: OffsetDateTime) : Result(timestamp)

  /**
   * Represents a failed synchronization result.
   *
   * @property exceptions The list of exceptions that occurred during the synchronization failure.
   * @property timestamp The timestamp when the synchronization failed.
   */
  class Failed(val exceptions: List<ResourceSyncException>, timestamp: OffsetDateTime) :
    Result(timestamp)
}
