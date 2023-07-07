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
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.sync.UploadRequest
import org.hl7.fhir.r4.model.Bundle

/**
 * Abstract class for generating [UploadRequest] for a [LocalChange] to be sent to the server based
 * on [Bundle.HTTPVerb] supported by the Fhir server. Concrete implementations of the class should
 * provide implementation of [getEntryResource] to provide [UploadRequest] for the [LocalChange].
 */
abstract class HttpVerbBasedUploadRequestGenerator(
  private val httpVerb: Bundle.HTTPVerb,
  private val useETagForUpload: Boolean,
) {

  /** Return [UploadRequest] for the [LocalChangeEntity]. */
  abstract fun getUploadRequest(localChange: LocalChange): UploadRequest

  fun addIfMatchHeader(
    localChange: LocalChange,
    headerMap: MutableMap<String, String>
  ): MutableMap<String, String> {
    if (useETagForUpload && !localChange.versionId.isNullOrEmpty()) {
      // FHIR supports weak Etag, See ETag section https://hl7.org/fhir/http.html#Http-Headers
      headerMap["If-Match"] = "W/\"${localChange.versionId}\""
    }
    return headerMap
  }
}
