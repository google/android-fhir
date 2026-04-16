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

package com.example.sdckmpdemo.sync

import kotlinx.coroutines.flow.StateFlow

sealed class SyncUiState {
  data object Idle : SyncUiState()

  data object Syncing : SyncUiState()

  data class Completed(val timestamp: String) : SyncUiState()

  data class Error(val message: String) : SyncUiState()
}

interface SyncManager {
  val syncState: StateFlow<SyncUiState>
  val isSyncAvailable: Boolean

  fun triggerSync()
}

expect fun createSyncManager(platformContext: Any = Unit): SyncManager
