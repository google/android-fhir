/*
 * Copyright 2023 Google LLC
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

package com.google.android.fhir.index

import ca.uhn.fhir.context.FhirContext
import org.fhir.ucum.UcumEssenceService
import org.hl7.fhir.r4.context.SimpleWorkerContext
import org.hl7.fhir.r4.hapi.ctx.HapiWorkerContext
import org.hl7.fhir.r4.model.Resource

/**
 * Merges the caching system in the HAPIWorker with the Ucum Support in the SimpleWorker
 *
 * TODO: Ideally we update the upstream's HapiWorkerContext to support ucumServices.
 */
class DualHapiWorkerContext : SimpleWorkerContext() {
  val hapi =
    HapiWorkerContext(
      FhirContext.forR4Cached(),
      FhirContext.forR4Cached().validationSupport,
    )

  init {
    // TODO: get this service from Common: UnitConverter.ucumService
    this.ucumService = UcumEssenceService(this::class.java.getResourceAsStream("/ucum-essence.xml"))
  }

  override fun <T : Resource?> fetchResourceWithException(class_: Class<T>?, uri: String?): T {
    return hapi.fetchResourceWithException(class_, uri)
      ?: super.fetchResourceWithException(class_, uri)
  }
}
