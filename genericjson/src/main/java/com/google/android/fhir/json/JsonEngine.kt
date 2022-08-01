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

package com.google.android.fhir.json

import com.google.android.fhir.json.db.impl.dao.LocalChangeToken
import com.google.android.fhir.json.db.impl.dao.SquashedLocalChange
import com.google.android.fhir.json.sync.ConflictResolver
import java.time.OffsetDateTime
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject

/** The FHIR Engine interface that handles the local storage of FHIR resources. */
interface JsonEngine {
  /**
   * Creates one or more FHIR [resource]s in the local storage.
   *
   * @return the logical IDs of the newly created resources.
   */
  suspend fun create(vararg resource: JSONObject): List<String>

  /** Loads a FHIR resource given the class and the logical ID. */
  suspend fun get(id: String): JSONObject

  /** Updates a FHIR [resource] in the local storage. */
  suspend fun update(vararg resource: JSONObject)

  /** Removes a FHIR resource given the class and the logical ID. */
  suspend fun delete(id: String)

  /**
   * Synchronizes the [upload] result in the database. [upload] operation may result in multiple
   * calls to the server to upload the data. Result of each call will be emitted by [upload] and the
   * api caller should [Flow.collect] it.
   */
  suspend fun syncUpload(
    upload: (suspend (List<SquashedLocalChange>) -> Flow<Pair<LocalChangeToken, JSONObject>>)
  )

  /**
   * Synchronizes the [download] result in the database. The database will be updated to reflect the
   * result of the [download] operation.
   */
  suspend fun syncDownload(
    conflictResolver: ConflictResolver,
    download: suspend () -> Flow<List<JSONObject>>
  )

  /** Returns the timestamp when data was last synchronized. */
  suspend fun getLastSyncTimeStamp(): OffsetDateTime?
}
