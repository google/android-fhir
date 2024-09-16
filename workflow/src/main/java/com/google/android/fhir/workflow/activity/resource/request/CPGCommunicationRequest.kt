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

package com.google.android.fhir.workflow.activity.resource.request

import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.CommunicationRequest
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.StringType

class CPGCommunicationRequest(override val resource: CommunicationRequest) :
  CPGRequestResource<CommunicationRequest>(resource) {
  override fun setIntent(intent: Intent) {
    if (resource.hasExtension("http://hl7.org/fhir/StructureDefinition/request-intent")) {
      resource
        .getExtensionByUrl("http://hl7.org/fhir/StructureDefinition/request-intent")
        .setValue(StringType(intent.code))
    } else {
      resource.addExtension(
        "http://hl7.org/fhir/StructureDefinition/request-intent",
        StringType(intent.code),
      )
    }
  }

  override fun getIntent() =
    resource.getExtensionByUrl("http://hl7.org/fhir/StructureDefinition/request-intent")?.let {
      Intent.of(it.value?.primitiveValue())
    }
      ?: Intent.of(null)

  override fun setStatus(status: Status, reason: String?) {
    resource.status = CommunicationRequest.CommunicationRequestStatus.fromCode(status.string)
    resource.statusReason = reason?.let { CodeableConcept(Coding().setCode(it)) }
  }

  override fun getStatus() = Status.of(resource.status.toCode())

  override fun setBasedOn(reference: Reference) {
    resource.addBasedOn(reference)
  }

  override fun getBasedOn() = resource.basedOn.lastOrNull()

  override fun copy() = CPGCommunicationRequest(resource.copy())
}
