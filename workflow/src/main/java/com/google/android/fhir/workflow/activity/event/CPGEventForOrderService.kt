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

import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Procedure
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource

abstract class CPGEventForOrderService<out R : Resource>(override val resource: R) :
  CPGEventResource<R>(resource)

class CPGProcedureEvent(override val resource: Procedure) :
  CPGEventForOrderService<Procedure>(resource) {
  override fun setStatus(status: EventStatus) {
    TODO("Not yet implemented")
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
}

class CPGObservationEvent(override val resource: Observation) :
  CPGEventForOrderService<Observation>(resource) {

  override fun setStatus(status: EventStatus) {
    TODO("Not yet implemented")
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
}
