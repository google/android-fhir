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

import com.google.android.fhir.workflow.activity.resource.event.EventStatus.NOTDONE
import com.google.android.fhir.workflow.activity.resource.request.CPGMedicationRequest
import java.util.UUID
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.MedicationDispense
import org.hl7.fhir.r4.model.MedicationRequest
import org.hl7.fhir.r4.model.Reference

class CPGMedicationDispenseEvent(override val resource: MedicationDispense) :
  CPGOrderMedicationEvent<MedicationDispense>(resource, MedicationDispenseEventEventMapper) {

  override fun setStatus(status: EventStatus, reason: String?) {
    resource.status =
      MedicationDispense.MedicationDispenseStatus.fromCode(mapper.mapStatusToCode(status))
    resource.statusReason = reason?.let { CodeableConcept(Coding().setCode(it)) }
  }

  override fun getStatusCode() = resource.status?.toCode()

  override fun setBasedOn(reference: Reference) {
    resource.addAuthorizingPrescription(reference)
  }

  override fun getBasedOn(): Reference? = resource.authorizingPrescription.lastOrNull()

  override fun copy() = CPGMedicationDispenseEvent(resource.copy())

  companion object {

    fun from(request: CPGMedicationRequest): CPGMedicationDispenseEvent {
      return CPGMedicationDispenseEvent(
        MedicationDispense().apply {
          id = UUID.randomUUID().toString()
          status = MedicationDispense.MedicationDispenseStatus.PREPARATION

          if (request.resource.hasCategory() && request.resource.category.size == 1) {
            // Only set category if single, otherwise let application fill it in.
            category = request.resource.category.first()
          }

          medication = request.resource.medication
          subject = request.resource.subject
          context = request.resource.encounter
          if (request.resource.hasSubstitution()) {
            substitution = request.resource.substitution.toMedicationDispenseSubstitutionComponent()
          }
          note = request.resource.note
          dosageInstruction = request.resource.dosageInstruction
          detectedIssue = request.resource.detectedIssue
          eventHistory = request.resource.eventHistory
        },
      )
    }

    private fun MedicationRequest.MedicationRequestSubstitutionComponent
      .toMedicationDispenseSubstitutionComponent() =
      MedicationDispense.MedicationDispenseSubstitutionComponent().apply {
        id = this@toMedicationDispenseSubstitutionComponent.id
        extension = this@toMedicationDispenseSubstitutionComponent.extension
        modifierExtension = this@toMedicationDispenseSubstitutionComponent.modifierExtension
        allowed = this@toMedicationDispenseSubstitutionComponent.allowed
        if (this@toMedicationDispenseSubstitutionComponent.hasReason()) {
          addReason(this@toMedicationDispenseSubstitutionComponent.reason)
        }
      }
  }
}

private object MedicationDispenseEventEventMapper : EventStatusCodeMapperImpl() {
  override fun mapCodeToStatus(code: String?): EventStatus {
    return when (code) {
      "cancelled" -> NOTDONE
      else -> super.mapCodeToStatus(code)
    }
  }

  override fun mapStatusToCode(status: EventStatus): String? {
    return when (status) {
      NOTDONE -> "cancelled"
      else -> super.mapStatusToCode(status)
    }
  }
}
