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
import androidx.work.WorkInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow

interface SyncJob {
  fun <W : PeriodicSyncWorker> poll(
    periodicSyncConfiguration: PeriodicSyncConfiguration,
    context: Context,
    clazz: Class<W>
  ): Flow<MutableList<WorkInfo>>

  suspend fun run(subscribeTo: MutableSharedFlow<State>?): Result
  fun workInfoFlowFor(uniqueWorkerName: String, context: Context): Flow<WorkInfo>
  fun stateFlowFor(uniqueWorkerName: String, context: Context): Flow<State>
}
