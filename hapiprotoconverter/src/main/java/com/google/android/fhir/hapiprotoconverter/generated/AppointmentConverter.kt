package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
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
import com.google.fhir.r4.core.AppointmentStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.ParticipantRequiredCode
import com.google.fhir.r4.core.ParticipationStatusCode
import com.google.fhir.r4.core.String

public object AppointmentConverter {
  public fun Appointment.toHapi(): org.hl7.fhir.r4.model.Appointment {
    val hapiValue = org.hl7.fhir.r4.model.Appointment()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setStatus(org.hl7.fhir.r4.model.Appointment.AppointmentStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setCancelationReason(cancelationReason.toHapi())
    hapiValue.setServiceCategory(serviceCategoryList.map{it.toHapi()})
    hapiValue.setServiceType(serviceTypeList.map{it.toHapi()})
    hapiValue.setSpecialty(specialtyList.map{it.toHapi()})
    hapiValue.setAppointmentType(appointmentType.toHapi())
    hapiValue.setReasonCode(reasonCodeList.map{it.toHapi()})
    hapiValue.setReasonReference(reasonReferenceList.map{it.toHapi()})
    hapiValue.setPriorityElement(priority.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setSupportingInformation(supportingInformationList.map{it.toHapi()})
    hapiValue.setStartElement(start.toHapi())
    hapiValue.setEndElement(end.toHapi())
    hapiValue.setMinutesDurationElement(minutesDuration.toHapi())
    hapiValue.setSlot(slotList.map{it.toHapi()})
    hapiValue.setCreatedElement(created.toHapi())
    hapiValue.setCommentElement(comment.toHapi())
    hapiValue.setPatientInstructionElement(patientInstruction.toHapi())
    hapiValue.setBasedOn(basedOnList.map{it.toHapi()})
    hapiValue.setParticipant(participantList.map{it.toHapi()})
    hapiValue.setRequestedPeriod(requestedPeriodList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Appointment.toProto(): Appointment {
    val protoValue = Appointment.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setStatus(Appointment.StatusCode.newBuilder().setValue(AppointmentStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setCancelationReason(cancelationReason.toProto())
    .addAllServiceCategory(serviceCategory.map{it.toProto()})
    .addAllServiceType(serviceType.map{it.toProto()})
    .addAllSpecialty(specialty.map{it.toProto()})
    .setAppointmentType(appointmentType.toProto())
    .addAllReasonCode(reasonCode.map{it.toProto()})
    .addAllReasonReference(reasonReference.map{it.toProto()})
    .setPriority(priorityElement.toProto())
    .setDescription(descriptionElement.toProto())
    .addAllSupportingInformation(supportingInformation.map{it.toProto()})
    .setStart(startElement.toProto())
    .setEnd(endElement.toProto())
    .setMinutesDuration(minutesDurationElement.toProto())
    .addAllSlot(slot.map{it.toProto()})
    .setCreated(createdElement.toProto())
    .setComment(commentElement.toProto())
    .setPatientInstruction(patientInstructionElement.toProto())
    .addAllBasedOn(basedOn.map{it.toProto()})
    .addAllParticipant(participant.map{it.toProto()})
    .addAllRequestedPeriod(requestedPeriod.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Appointment.AppointmentParticipantComponent.toProto():
      Appointment.Participant {
    val protoValue = Appointment.Participant.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllType(type.map{it.toProto()})
    .setActor(actor.toProto())
    .setRequired(Appointment.Participant.RequiredCode.newBuilder().setValue(ParticipantRequiredCode.Value.valueOf(required.toCode().replace("-",
        "_").toUpperCase())).build())
    .setStatus(Appointment.Participant.StatusCode.newBuilder().setValue(ParticipationStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setPeriod(period.toProto())
    .build()
    return protoValue
  }

  public fun Appointment.Participant.toHapi():
      org.hl7.fhir.r4.model.Appointment.AppointmentParticipantComponent {
    val hapiValue = org.hl7.fhir.r4.model.Appointment.AppointmentParticipantComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(typeList.map{it.toHapi()})
    hapiValue.setActor(actor.toHapi())
    hapiValue.setRequired(org.hl7.fhir.r4.model.Appointment.ParticipantRequired.valueOf(required.value.name.replace("_","")))
    hapiValue.setStatus(org.hl7.fhir.r4.model.Appointment.ParticipationStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setPeriod(period.toHapi())
    return hapiValue
  }
}
