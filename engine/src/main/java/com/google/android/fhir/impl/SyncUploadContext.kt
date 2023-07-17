/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir.impl

import com.google.android.fhir.LocalChange
import com.google.android.fhir.SyncStrategyTypes
import com.google.android.fhir.db.Database
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import kotlinx.coroutines.flow.Flow
import org.hl7.fhir.r4.model.Resource

class SyncUploadContext {
  private var syncStrategy: SyncStrategy = SequentialSyncStrategy()

  fun setSyncStrategy(syncType: SyncStrategyTypes) {
    syncStrategy =
      when (syncType) {
        SyncStrategyTypes.SEQUENTIAL -> SequentialSyncStrategy()
        SyncStrategyTypes.ALL -> AllLocalChangesSyncStrategy()
      }
  }

  suspend fun rearrangeSyncList(
    localChanges: List<LocalChange>,
    database: Database,
    collectAndEmitLocalChange:
      suspend (
        List<LocalChange>,
        suspend (List<LocalChange>) -> Flow<Pair<LocalChangeToken, Resource>>
      ) -> Unit,
    upload: suspend (List<LocalChange>) -> Flow<Pair<LocalChangeToken, Resource>>
  ) {
    syncStrategy.rearrangeSyncList(localChanges, database, collectAndEmitLocalChange, upload)
  }
}
