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

import com.google.android.fhir.workflow.activity.resource.request.CPGMedicationRequest
import java.util.UUID
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.MedicationAdministration
import org.hl7.fhir.r4.model.MedicationDispense
import org.hl7.fhir.r4.model.MedicationRequest
import org.hl7.fhir.r4.model.MedicationStatement
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource

abstract class CPGEventResourceForOrderMedication<out R : Resource>(override val resource: R) :
  CPGEventResource<R>(resource) {

  companion object {

    fun from(request: CPGMedicationRequest, eventClass: Class<*>) =
      when (eventClass) {
        CPGMedicationDispenseEvent::class.java -> CPGMedicationDispenseEvent.from(request)
        CPGMedicationAdministrationEvent::class.java ->
          CPGMedicationAdministrationEvent.from(request)

        CPGMedicationStatementEvent::class.java -> CPGMedicationStatementEvent.from(request)
        else -> throw IllegalArgumentException(" Unknown Event type $eventClass")
      }
  }
}

class CPGMedicationDispenseEvent(override val resource: MedicationDispense) :
  CPGEventResourceForOrderMedication<MedicationDispense>(resource) {

  override fun setStatus(status: EventStatus, reason: String?) {
    resource.status = MedicationDispense.MedicationDispenseStatus.fromCode(status.code)
    resource.statusReason = reason?.let { CodeableConcept(Coding().setCode(it)) }
  }

  override fun getStatus() = EventStatus.of(resource.status.toCode())

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

    private fun MedicationRequest.MedicationRequestSubstitutionComponent.toMedicationDispenseSubstitutionComponent() =
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

class CPGMedicationAdministrationEvent(override val resource: MedicationAdministration) :
  CPGEventResourceForOrderMedication<MedicationAdministration>(resource) {
  override fun setStatus(status: EventStatus, reason: String?) {
    resource.status = MedicationAdministration.MedicationAdministrationStatus.fromCode(status.code)
    resource.statusReason = reason?.let { listOf(CodeableConcept(Coding().setCode(it))) }
  }

  override fun getStatus() = EventStatus.of(resource.status.toCode())

  override fun setBasedOn(reference: Reference) {
    resource.request = reference
  }

  override fun getBasedOn(): Reference = resource.request

  override fun copy() = CPGMedicationAdministrationEvent(resource.copy())

  companion object {
    fun from(request: CPGMedicationRequest): CPGMedicationAdministrationEvent {
      return CPGMedicationAdministrationEvent(
        MedicationAdministration().apply {
          id = UUID.randomUUID().toString()
          status = MedicationAdministration.MedicationAdministrationStatus.UNKNOWN
          subject = request.resource.subject
          medication = request.resource.medication
          this.request = request.asReference()
          medication = request.resource.medication
        },
      )
    }
  }
}

class CPGMedicationStatementEvent(override val resource: MedicationStatement) :
  CPGEventResourceForOrderMedication<MedicationStatement>(resource) {
  override fun setStatus(status: EventStatus, reason: String?) {
    resource.status = MedicationStatement.MedicationStatementStatus.fromCode(status.code)
  }

  override fun getStatus() = EventStatus.of(resource.status.toCode())

  override fun setBasedOn(reference: Reference) {
    resource.addBasedOn(reference)
  }

  override fun getBasedOn(): Reference? = resource.basedOn.firstOrNull()

  override fun copy() = CPGMedicationStatementEvent(resource.copy())

  companion object {
    fun from(request: CPGMedicationRequest): CPGMedicationStatementEvent {
      return CPGMedicationStatementEvent(
        MedicationStatement().apply {
          id = UUID.randomUUID().toString()
          status = MedicationStatement.MedicationStatementStatus.UNKNOWN
          subject = request.resource.subject
          medication = request.resource.medication
          addBasedOn(request.asReference())
          addDerivedFrom(request.asReference())
          informationSource = request.asReference()
          medication = request.resource.medication
        },
      )
    }
  }
}
