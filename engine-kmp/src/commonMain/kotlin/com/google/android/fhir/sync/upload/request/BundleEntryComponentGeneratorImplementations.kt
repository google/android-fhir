/*
 * Copyright 2023-2026 Google LLC
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

package com.google.android.fhir.sync.upload.request

import com.google.android.fhir.ContentTypes
import com.google.android.fhir.sync.upload.patch.Patch
import com.google.fhir.model.r4.Base64Binary
import com.google.fhir.model.r4.Binary
import com.google.fhir.model.r4.Bundle
import com.google.fhir.model.r4.Code
import com.google.fhir.model.r4.FhirR4Json
import com.google.fhir.model.r4.Resource
import kotlin.io.encoding.Base64

internal class HttpPutForCreateEntryComponentGenerator(useETagForUpload: Boolean) :
  BundleEntryComponentGenerator(Bundle.HTTPVerb.Put, useETagForUpload) {
  override fun getEntryResource(patch: Patch): Resource {
    return FhirR4Json().decodeFromString(patch.payload)
  }
}

internal class HttpPostForCreateEntryComponentGenerator(useETagForUpload: Boolean) :
  BundleEntryComponentGenerator(Bundle.HTTPVerb.Post, useETagForUpload) {
  override fun getEntryResource(patch: Patch): Resource {
    return FhirR4Json().decodeFromString(patch.payload)
  }
}

internal class HttpPatchForUpdateEntryComponentGenerator(useETagForUpload: Boolean) :
  BundleEntryComponentGenerator(Bundle.HTTPVerb.Patch, useETagForUpload) {
  override fun getEntryResource(patch: Patch): Resource {
    return Binary(
      contentType = Code(value = ContentTypes.APPLICATION_JSON_PATCH),
      data = Base64Binary(value = Base64.encode(patch.payload.encodeToByteArray())),
    )
  }
}

internal class HttpDeleteEntryComponentGenerator(useETagForUpload: Boolean) :
  BundleEntryComponentGenerator(Bundle.HTTPVerb.Delete, useETagForUpload) {
  override fun getEntryResource(patch: Patch): Resource? = null
}
