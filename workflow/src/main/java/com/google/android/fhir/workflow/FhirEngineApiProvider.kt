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

package com.google.android.fhir.workflow

import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.FhirEngine
import org.cqframework.cql.cql2elm.LibrarySourceProvider
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Library
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider
import org.opencds.cqf.cql.evaluator.fhir.adapter.r4.AdapterFactory
import org.opencds.cqf.cql.evaluator.fhir.dal.FhirDal

class FhirEngineApiProvider(fhirContext: FhirContext, fhirEngine: FhirEngine) :
  WorkflowApiProvider {

  private val fhirEngineTerminologyProvider = FhirEngineTerminologyProvider(fhirContext, fhirEngine)
  private val libraryContentProvider = FhirEngineLibraryContentProvider(AdapterFactory())
  private val fhirEngineRetrieveProvider =
    FhirEngineRetrieveProvider(fhirEngine).apply {
      terminologyProvider = fhirEngineTerminologyProvider
      isExpandValueSets = true
    }

  private val fhirEngineDal = FhirEngineDal(fhirEngine)

  override fun fhirDal(): FhirDal = fhirEngineDal

  override fun terminologyProvider(): TerminologyProvider = fhirEngineTerminologyProvider

  override fun libraryContentProvider(): LibrarySourceProvider = libraryContentProvider

  override fun retrieveProvider(): RetrieveProvider = fhirEngineRetrieveProvider

  fun loadLib(lib: Library) {
    if (lib.url != null) {
      fhirEngineDal.libs[lib.url] = lib
    }
    if (lib.name != null) {
      libraryContentProvider.libs[lib.name] = lib
    }
  }

  fun loadLibs(libBundle: Bundle) {
    for (entry in libBundle.entry) {
      loadLib(entry.resource as Library)
    }
  }
}
