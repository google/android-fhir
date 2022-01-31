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

package com.google.android.fhir.sync.bundle

import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.Binary
import org.hl7.fhir.r4.model.Bundle

internal class HttpPutForCreateEntryComponent(private val jsonParser: IParser) :
  HttpVerbBasedBundleEntryComponent(Bundle.HTTPVerb.PUT) {
  override fun getEntryResource(localChange: LocalChangeEntity): IBaseResource {
    return jsonParser.parseResource(localChange.payload)
  }
}

internal class HttpPatchForUpdateEntryComponent :
  HttpVerbBasedBundleEntryComponent(Bundle.HTTPVerb.PATCH) {
  override fun getEntryResource(localChange: LocalChangeEntity): IBaseResource {
    return Binary().apply {
      contentType = "application/json-patch+json"
      data = localChange.payload.toByteArray()
    }
  }
}

internal class HttpDeleteEntryComponent :
  HttpVerbBasedBundleEntryComponent(Bundle.HTTPVerb.DELETE) {
  override fun getEntryResource(localChange: LocalChangeEntity): IBaseResource? = null
}
