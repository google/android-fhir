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

import com.google.android.fhir.workflow.activity.request.CPGCommunicationRequest
import java.util.UUID
import org.hl7.fhir.r4.model.Immunization
import org.hl7.fhir.r4.model.Reference

class CPGImmunizationEvent(override val resource: Immunization) :
  CPGEventResource<Immunization>(resource) {
  override fun setStatus(status: EventStatus) {
    resource.status = Immunization.ImmunizationStatus.fromCode(status.code)
  }

  override fun getStatus() = EventStatus.of(resource.status.toCode())

  override fun setBasedOn(reference: Reference) {
    TODO(" Based on not present")
  }

  override fun getBasedOn(): Reference? = TODO(" Based on not present")

  companion object {
    fun from(request: CPGCommunicationRequest): CPGImmunizationEvent {
      return CPGImmunizationEvent(
        Immunization().apply {
          id = UUID.randomUUID().toString()
          status = Immunization.ImmunizationStatus.NOTDONE
          patient = request.resource.subject
        },
      )
    }
  }
}
