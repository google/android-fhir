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

import com.google.android.fhir.db.impl.dao.SquashedLocalChange
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Enumeration
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.UriType

/**
 * Creates [Bundle.BundleEntryComponent] for each [LocalChangeEntity] to be added to the [Bundle].
 */
internal abstract class HttpVerbBasedBundleEntryComponent(private val httpVerb: Bundle.HTTPVerb) {

  /** Should return [Resource] for the [LocalChangeEntity]. */
  abstract fun getEntryResource(localChange: LocalChangeEntity): IBaseResource?

  fun getEntry(squashedLocalChange: SquashedLocalChange): Bundle.BundleEntryComponent {
    return Bundle.BundleEntryComponent().apply {
      resource = getEntryResource(squashedLocalChange.localChange) as Resource?
      request = getEntryRequest(squashedLocalChange.localChange)
      fullUrl = request?.url
    }
  }

  private fun getEntryRequest(localChange: LocalChangeEntity) =
    Bundle.BundleEntryRequestComponent(
      Enumeration(Bundle.HTTPVerbEnumFactory()).apply { value = httpVerb },
      UriType("${localChange.resourceType}/${localChange.resourceId}")
    )
}
