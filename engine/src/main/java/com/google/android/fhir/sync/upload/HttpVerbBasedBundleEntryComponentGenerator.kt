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
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Enumeration
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.UriType

/**
 * Abstract class for generating [Bundle.BundleEntryComponent] for a [SquashedLocalChange] to be
 * added to the [Bundle] based on [Bundle.HTTPVerb] supported by the Fhir server. Concrete
 * implementations of the class should provide implementation of [getEntryResource] to provide
 * [Resource] for the [LocalChangeEntity]. See [https://www.hl7.org/fhir/http.html#transaction] for
 * more info regarding the supported [Bundle.HTTPVerb].
 */
internal abstract class HttpVerbBasedBundleEntryComponentGenerator(
  private val httpVerb: Bundle.HTTPVerb,
  private val useETagForUpload: Boolean,
) {

  /**
   * Return [IBaseResource]? for the [LocalChangeEntity]. Implementation may return [null] when a
   * [Resource] may not be required in the request like in the case of a [Bundle.HTTPVerb.DELETE]
   * request.
   */
  protected abstract fun getEntryResource(localChange: LocalChange): IBaseResource?

  /**
   * Returns a [Bundle.BundleEntryComponent] for a [SquashedLocalChange] to be added to the [Bundle]
   * .
   */
  fun getEntry(squashedLocalChange: LocalChange): Bundle.BundleEntryComponent {
    return Bundle.BundleEntryComponent().apply {
      resource = getEntryResource(squashedLocalChange) as Resource?
      request = getEntryRequest(squashedLocalChange)
      fullUrl = request?.url
    }
  }

  private fun getEntryRequest(localChange: LocalChange) =
    Bundle.BundleEntryRequestComponent(
        Enumeration(Bundle.HTTPVerbEnumFactory()).apply { value = httpVerb },
        UriType("${localChange.resourceType}/${localChange.resourceId}")
      )
      .apply {
        if (useETagForUpload && !localChange.versionId.isNullOrEmpty()) {
          // FHIR supports weak Etag, See ETag section https://hl7.org/fhir/http.html#Http-Headers
          when (localChange.type) {
            LocalChange.Type.UPDATE,
            LocalChange.Type.DELETE -> ifMatch = "W/\"${localChange.versionId}\""
            LocalChange.Type.INSERT -> {}
          }
        }
      }
}
