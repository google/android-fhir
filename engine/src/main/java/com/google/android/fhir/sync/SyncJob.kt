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

import androidx.work.WorkInfo
import com.google.android.fhir.FhirEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

interface SyncJob {
  fun <W : FhirSyncWorker> poll(
    periodicSyncConfiguration: PeriodicSyncConfiguration,
    clazz: Class<W>
  ): Flow<State>

  suspend fun run(
    fhirEngine: FhirEngine,
    dataSource: DataSource,
    resourceSyncParams: ResourceSyncParams,
    subscribeTo: MutableSharedFlow<State>?
  ): Result

  fun workInfoFlow(): Flow<WorkInfo>
  fun stateFlow(): Flow<State>
}
