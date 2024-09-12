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

package com.google.android.fhir.workflow.activity.resource.event

import com.google.android.fhir.workflow.activity.resource.request.CPGCommunicationRequest
import java.util.UUID
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Communication
import org.hl7.fhir.r4.model.Reference

class CPGCommunicationEvent(override val resource: Communication) :
  CPGEventResource<Communication>(resource) {

  override fun setStatus(status: EventStatus, reason: String?) {
    resource.status = Communication.CommunicationStatus.fromCode(status.code)
    resource.statusReason = reason?.let { CodeableConcept(Coding().setCode(it)) }
  }

  override fun getStatus() = EventStatus.of(resource.status.toCode())

  override fun setBasedOn(reference: Reference) {
    resource.addBasedOn(reference)
  }

  override fun getBasedOn(): Reference? = resource.basedOn.lastOrNull()

  override fun copy() = CPGCommunicationEvent(resource.copy())

  companion object {
    fun from(request: CPGCommunicationRequest): CPGCommunicationEvent {
      return CPGCommunicationEvent(
        Communication().apply {
          id = UUID.randomUUID().toString()
          status = Communication.CommunicationStatus.PREPARATION
          category = request.resource.category
          priority =
            Communication.CommunicationPriority.fromCode(request.resource.priority?.toCode())
          medium = request.resource.medium
          subject = request.resource.subject
          about = request.resource.about
          encounter = request.resource.encounter
          recipient = request.resource.recipient
          sender = request.resource.sender
          reasonCode = request.resource.reasonCode
          reasonReference = request.resource.reasonReference
          request.resource.payload.forEach {
            addPayload(Communication.CommunicationPayloadComponent(it.content))
          }
        },
      )
    }
  }
}
