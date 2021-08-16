/*
 * Copyright 2020 Google LLC
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

package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Appointment
import com.google.fhir.r4.core.Appointment.Participant
import com.google.fhir.r4.core.AppointmentStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.ParticipantRequiredCode
import com.google.fhir.r4.core.ParticipationStatusCode
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic

object AppointmentConverter {
  @JvmStatic
  fun Appointment.toHapi(): org.hl7.fhir.r4.model.Appointment {
    val hapiValue = org.hl7.fhir.r4.model.Appointment()
    hapiValue.id = id.value
    if (hasMeta()) {
        hapiValue.meta = meta.toHapi()
    }
    if (hasImplicitRules()) {
        hapiValue.implicitRulesElement = implicitRules.toHapi()
    }
    if (hasText()) {
        hapiValue.text = text.toHapi()
    }
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (identifierCount > 0) {
        hapiValue.identifier = identifierList.map { it.toHapi() }
    }
      hapiValue.status = org.hl7.fhir.r4.model.Appointment.AppointmentStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasCancelationReason()) {
        hapiValue.cancelationReason = cancelationReason.toHapi()
    }
    if (serviceCategoryCount > 0) {
        hapiValue.serviceCategory = serviceCategoryList.map { it.toHapi() }
    }
    if (serviceTypeCount > 0) {
        hapiValue.serviceType = serviceTypeList.map { it.toHapi() }
    }
    if (specialtyCount > 0) {
        hapiValue.specialty = specialtyList.map { it.toHapi() }
    }
    if (hasAppointmentType()) {
        hapiValue.appointmentType = appointmentType.toHapi()
    }
    if (reasonCodeCount > 0) {
        hapiValue.reasonCode = reasonCodeList.map { it.toHapi() }
    }
    if (reasonReferenceCount > 0) {
        hapiValue.reasonReference = reasonReferenceList.map { it.toHapi() }
    }
    if (hasPriority()) {
        hapiValue.priorityElement = priority.toHapi()
    }
    if (hasDescription()) {
        hapiValue.descriptionElement = description.toHapi()
    }
    if (supportingInformationCount > 0) {
        hapiValue.supportingInformation = supportingInformationList.map { it.toHapi() }
    }
    if (hasStart()) {
        hapiValue.startElement = start.toHapi()
    }
    if (hasEnd()) {
        hapiValue.endElement = end.toHapi()
    }
    if (hasMinutesDuration()) {
        hapiValue.minutesDurationElement = minutesDuration.toHapi()
    }
    if (slotCount > 0) {
        hapiValue.slot = slotList.map { it.toHapi() }
    }
    if (hasCreated()) {
        hapiValue.createdElement = created.toHapi()
    }
    if (hasComment()) {
        hapiValue.commentElement = comment.toHapi()
    }
    if (hasPatientInstruction()) {
        hapiValue.patientInstructionElement = patientInstruction.toHapi()
    }
    if (basedOnCount > 0) {
        hapiValue.basedOn = basedOnList.map { it.toHapi() }
    }
    if (participantCount > 0) {
        hapiValue.participant = participantList.map { it.toHapi() }
    }
    if (requestedPeriodCount > 0) {
        hapiValue.requestedPeriod = requestedPeriodList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.Appointment.toProto(): Appointment {
    val protoValue = Appointment.newBuilder().setId(Id.newBuilder().setValue(id))
    if (hasMeta()) {
        protoValue.meta = meta.toProto()
    }
    if (hasImplicitRules()) {
        protoValue.implicitRules = implicitRulesElement.toProto()
    }
    if (hasText()) {
        protoValue.text = text.toProto()
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasIdentifier()) {
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
      protoValue.status = Appointment.StatusCode.newBuilder()
          .setValue(
              AppointmentStatusCode.Value.valueOf(
                  status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasCancelationReason()) {
        protoValue.cancelationReason = cancelationReason.toProto()
    }
    if (hasServiceCategory()) {
      protoValue.addAllServiceCategory(serviceCategory.map { it.toProto() })
    }
    if (hasServiceType()) {
      protoValue.addAllServiceType(serviceType.map { it.toProto() })
    }
    if (hasSpecialty()) {
      protoValue.addAllSpecialty(specialty.map { it.toProto() })
    }
    if (hasAppointmentType()) {
        protoValue.appointmentType = appointmentType.toProto()
    }
    if (hasReasonCode()) {
      protoValue.addAllReasonCode(reasonCode.map { it.toProto() })
    }
    if (hasReasonReference()) {
      protoValue.addAllReasonReference(reasonReference.map { it.toProto() })
    }
    if (hasPriority()) {
        protoValue.priority = priorityElement.toProto()
    }
    if (hasDescription()) {
        protoValue.description = descriptionElement.toProto()
    }
    if (hasSupportingInformation()) {
      protoValue.addAllSupportingInformation(supportingInformation.map { it.toProto() })
    }
    if (hasStart()) {
        protoValue.start = startElement.toProto()
    }
    if (hasEnd()) {
        protoValue.end = endElement.toProto()
    }
    if (hasMinutesDuration()) {
        protoValue.minutesDuration = minutesDurationElement.toProto()
    }
    if (hasSlot()) {
      protoValue.addAllSlot(slot.map { it.toProto() })
    }
    if (hasCreated()) {
        protoValue.created = createdElement.toProto()
    }
    if (hasComment()) {
        protoValue.comment = commentElement.toProto()
    }
    if (hasPatientInstruction()) {
        protoValue.patientInstruction = patientInstructionElement.toProto()
    }
    if (hasBasedOn()) {
      protoValue.addAllBasedOn(basedOn.map { it.toProto() })
    }
    if (hasParticipant()) {
      protoValue.addAllParticipant(participant.map { it.toProto() })
    }
    if (hasRequestedPeriod()) {
      protoValue.addAllRequestedPeriod(requestedPeriod.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Appointment.AppointmentParticipantComponent.toProto():
    Appointment.Participant {
    val protoValue = Appointment.Participant.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.addAllType(type.map { it.toProto() })
    }
    if (hasActor()) {
        protoValue.actor = actor.toProto()
    }
      protoValue.required = Appointment.Participant.RequiredCode.newBuilder()
          .setValue(
              ParticipantRequiredCode.Value.valueOf(
                  required.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
      protoValue.status = Appointment.Participant.StatusCode.newBuilder()
          .setValue(
              ParticipationStatusCode.Value.valueOf(
                  status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasPeriod()) {
        protoValue.period = period.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Appointment.Participant.toHapi():
    org.hl7.fhir.r4.model.Appointment.AppointmentParticipantComponent {
    val hapiValue = org.hl7.fhir.r4.model.Appointment.AppointmentParticipantComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
        hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (typeCount > 0) {
        hapiValue.type = typeList.map { it.toHapi() }
    }
    if (hasActor()) {
        hapiValue.actor = actor.toHapi()
    }
      hapiValue.required = org.hl7.fhir.r4.model.Appointment.ParticipantRequired.valueOf(
          required.value.name.hapiCodeCheck().replace("_", "")
      )
      hapiValue.status = org.hl7.fhir.r4.model.Appointment.ParticipationStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasPeriod()) {
        hapiValue.period = period.toHapi()
    }
    return hapiValue
  }
}
