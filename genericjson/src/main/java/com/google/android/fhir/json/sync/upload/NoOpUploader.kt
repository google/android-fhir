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

package com.google.android.fhir.json.sync.upload

import com.google.android.fhir.json.db.impl.dao.SquashedLocalChange
import com.google.android.fhir.json.sync.ResourceSyncException
import com.google.android.fhir.json.sync.UploadResult
import com.google.android.fhir.json.sync.Uploader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** [Uploader] implementation to work with Fhir [Bundle]. */
internal class NoOpUploader() : Uploader {

  override suspend fun upload(
    localChanges: List<SquashedLocalChange>,
  ): Flow<UploadResult> = flow {
    emit(UploadResult.Failure(ResourceSyncException(Exception("Not Implemented"))))
  }
}
