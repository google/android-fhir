/*
 * Copyright 2022-2023 Google LLC
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

sealed class SyncJobStatus {
  val timestamp: OffsetDateTime = OffsetDateTime.now()

  /** Sync job has been started on the client but the syncing is not necessarily in progress. */
  object Started : SyncJobStatus()

  /** Syncing in progress with the server. */
  data class InProgress(
    val syncOperation: SyncOperation,
    val total: Int = 0,
    val completed: Int = 0,
  ) : SyncJobStatus()

  /** Sync job finished successfully. */
  object Finished : SyncJobStatus()

  /** Sync job failed. */
  data class Failed(val exceptions: List<ResourceSyncException>) : SyncJobStatus()
}
