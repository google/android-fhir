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

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.ContentTypes
import com.google.android.fhir.LocalChange
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.Binary
import org.hl7.fhir.r4.model.Bundle

internal object HttpPutForCreateEntryComponentGenerator :
  HttpVerbBasedBundleEntryComponentGenerator(Bundle.HTTPVerb.PUT) {
  override fun getEntryResource(localChange: LocalChange): IBaseResource {
    return FhirContext.forCached(FhirVersionEnum.R4)
      .newJsonParser()
      .parseResource(localChange.payload)
  }
}

internal object HttpPatchForUpdateEntryComponentGenerator :
  HttpVerbBasedBundleEntryComponentGenerator(Bundle.HTTPVerb.PATCH) {
  override fun getEntryResource(localChange: LocalChange): IBaseResource {
    return Binary().apply {
      contentType = ContentTypes.APPLICATION_JSON_PATCH
      data = localChange.payload.toByteArray()
    }
  }
}

internal object HttpDeleteEntryComponentGenerator :
  HttpVerbBasedBundleEntryComponentGenerator(Bundle.HTTPVerb.DELETE) {
  override fun getEntryResource(localChange: LocalChange): IBaseResource? = null
}
