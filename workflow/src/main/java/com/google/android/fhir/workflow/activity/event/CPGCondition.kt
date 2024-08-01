/*
 * Copyright 2024 Google LLC
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

package com.google.android.fhir.workflow.activity.event

import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.Reference

class CPGCondition(override val resource: Condition) : CPGEventResource<Condition>(resource) {
  // clinical status http://hl7.org/fhir/ValueSet/condition-clinical
  override fun setStatus(status: EventStatus) {
    resource.clinicalStatus = CodeableConcept()
  }

  override fun getStatus(): EventStatus {
    TODO("Not yet implemented")
  }

  override fun setBasedOn(reference: Reference) {
    TODO("Not yet implemented")
  }

  override fun getBasedOn(): Reference? {
    TODO("Not yet implemented")
  }

  //
}
