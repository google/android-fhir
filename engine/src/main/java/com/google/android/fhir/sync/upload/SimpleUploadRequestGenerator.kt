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

package com.google.android.fhir.sync.upload

import com.google.android.fhir.LocalChange
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.sync.UploadRequest
import com.google.android.fhir.sync.UploadRequestGenerator

/**
 * Generates list of [PatchUploadRequest]/[PostUploadRequest]/[PutUploadRequest]/
 * [DeleteUploadRequest] with [LocalChangeToken]s associated with the resources present in the
 * transaction bundle.
 */
open class SimpleUploadRequestGenerator(
  private val useETagForUpload: Boolean,
  private val getUploadRequestForLocalChangeType:
    (type: LocalChange.Type, useETagForUpload: Boolean) -> HttpVerbBasedUploadRequestGenerator
) : UploadRequestGenerator {
  override fun generateUploadRequests(localChanges: List<LocalChange>): List<UploadRequest> {
    return localChanges.map { localChange ->
      getUploadRequestForLocalChangeType(localChange.type, useETagForUpload)
        .getUploadRequest(localChange)
    }
  }

  companion object Factory {

    fun getPutForCreateAndPatchForUpdateUploadRequestGenerator(
      useETagForUpload: Boolean
    ): SimpleUploadRequestGenerator {
      return SimpleUploadRequestGenerator(
        useETagForUpload,
        this::putForCreateAndPatchForUpdateRequestMapper
      )
    }

    private fun putForCreateAndPatchForUpdateRequestMapper(
      type: LocalChange.Type,
      useETagForUpload: Boolean
    ): HttpVerbBasedUploadRequestGenerator {
      return when (type) {
        LocalChange.Type.INSERT -> HttpPutForCreateUploadRequestGenerator(useETagForUpload)
        LocalChange.Type.UPDATE -> HttpPatchForUpdateUploadRequestGenerator(useETagForUpload)
        LocalChange.Type.DELETE -> HttpDeleteUploadRequestGenerator(useETagForUpload)
      }
    }
  }
}
