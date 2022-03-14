/*
 * Copyright 2021 Google LLC
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

package com.google.android.fhir.sync.remote

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.MediaTypes
import com.google.android.fhir.sync.DataSource
import okhttp3.RequestBody.Companion.toRequestBody
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Resource

internal class DataSourceImpl(private val fhirService: RemoteFhirService) : DataSource {

  override suspend fun download(path: String): Resource {
    return fhirService.download(path)
  }

  override suspend fun upload(bundle: Bundle): Resource {
    return fhirService.upload(bundle.toRequestPayload())
  }

  private fun Bundle.toRequestPayload() =
    FhirContext.forCached(FhirVersionEnum.R4)
      .newJsonParser()
      .encodeResourceToString(this)
      .toRequestBody(MediaTypes.MEDIA_TYPE_FHIR_JSON)
}
