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

sealed class SyncState {
  object Enqueued : SyncState()

  data class Running(val currentJob: SyncJobStatus) : SyncState()

  class Succeeded(val succeededJob: SyncJobStatus) : SyncState()

  data class Failed(val failedJob: SyncJobStatus) : SyncState()

  object Cancelled : SyncState()

  object Unknown : SyncState()
}

data class PeriodicSyncState(
  val lastJobState: Result? = null,
  val currentJobState: SyncState? = null,
)

sealed class Result(val timestamp: OffsetDateTime) {
  class Succeeded(timestamp: OffsetDateTime) : Result(timestamp)

  class Failed(val exceptions: List<ResourceSyncException>, timestamp: OffsetDateTime) :
    Result(timestamp)
}
