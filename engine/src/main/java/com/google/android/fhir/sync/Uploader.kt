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

import com.google.android.fhir.LocalChange
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import kotlinx.coroutines.flow.Flow
import org.hl7.fhir.r4.model.Resource

/** Module for uploading local changes to a [DataSource]. */
internal interface Uploader {

  /**
   * Uploads the local changes to the [DataSource]. Particular implementations should take care of
   * transforming the [LocalChange]s to particular network operations. If [ProgressCallback] is
   * provided it also reports the intermediate progress
   */
  suspend fun upload(localChanges: List<LocalChange>): Flow<UploadState>
}

internal sealed class UploadState {
  data class Started(val total: Int) : UploadState()
  data class Success(
    val localChangeToken: LocalChangeToken,
    val resource: Resource,
    val total: Int,
    val completed: Int
  ) : UploadState()
  data class Failure(val syncError: ResourceSyncException) : UploadState()
}
