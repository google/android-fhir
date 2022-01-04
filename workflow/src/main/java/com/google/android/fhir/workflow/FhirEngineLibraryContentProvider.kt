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

package com.google.android.fhir.workflow

import org.hl7.elm.r1.VersionedIdentifier
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.Library
import org.opencds.cqf.cql.evaluator.cql2elm.content.fhir.BaseFhirLibraryContentProvider
import org.opencds.cqf.cql.evaluator.fhir.adapter.r4.AdapterFactory

class FhirEngineLibraryContentProvider(adapterFactory: AdapterFactory) :
  BaseFhirLibraryContentProvider(adapterFactory) {
  val libs = mutableMapOf<String, Library>()

  override fun getLibrary(libraryIdentifier: VersionedIdentifier): IBaseResource {
    return libs[libraryIdentifier.id]!!
  }
}
