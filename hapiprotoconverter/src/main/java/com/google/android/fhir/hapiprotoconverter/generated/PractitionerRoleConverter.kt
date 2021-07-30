package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.TimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.DaysOfWeekCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.PractitionerRole
import com.google.fhir.r4.core.PractitionerRole.AvailableTime
import com.google.fhir.r4.core.String

public object PractitionerRoleConverter {
  public fun PractitionerRole.toHapi(): org.hl7.fhir.r4.model.PractitionerRole {
    val hapiValue = org.hl7.fhir.r4.model.PractitionerRole()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setActiveElement(active.toHapi())
    hapiValue.setPeriod(period.toHapi())
    hapiValue.setPractitioner(practitioner.toHapi())
    hapiValue.setOrganization(organization.toHapi())
    hapiValue.setCode(codeList.map{it.toHapi()})
    hapiValue.setSpecialty(specialtyList.map{it.toHapi()})
    hapiValue.setLocation(locationList.map{it.toHapi()})
    hapiValue.setHealthcareService(healthcareServiceList.map{it.toHapi()})
    hapiValue.setTelecom(telecomList.map{it.toHapi()})
    hapiValue.setAvailableTime(availableTimeList.map{it.toHapi()})
    hapiValue.setNotAvailable(notAvailableList.map{it.toHapi()})
    hapiValue.setAvailabilityExceptionsElement(availabilityExceptions.toHapi())
    hapiValue.setEndpoint(endpointList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.PractitionerRole.toProto(): PractitionerRole {
    val protoValue = PractitionerRole.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setActive(activeElement.toProto())
    .setPeriod(period.toProto())
    .setPractitioner(practitioner.toProto())
    .setOrganization(organization.toProto())
    .addAllCode(code.map{it.toProto()})
    .addAllSpecialty(specialty.map{it.toProto()})
    .addAllLocation(location.map{it.toProto()})
    .addAllHealthcareService(healthcareService.map{it.toProto()})
    .addAllTelecom(telecom.map{it.toProto()})
    .addAllAvailableTime(availableTime.map{it.toProto()})
    .addAllNotAvailable(notAvailable.map{it.toProto()})
    .setAvailabilityExceptions(availabilityExceptionsElement.toProto())
    .addAllEndpoint(endpoint.map{it.toProto()})
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.PractitionerRole.PractitionerRoleAvailableTimeComponent.toProto():
      PractitionerRole.AvailableTime {
    val protoValue = PractitionerRole.AvailableTime.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllDaysOfWeek(daysOfWeek.map{PractitionerRole.AvailableTime.DaysOfWeekCode.newBuilder().setValue(DaysOfWeekCode.Value.valueOf(it.value.toCode().replace("-",
        "_").toUpperCase())).build()})
    .setAllDay(allDayElement.toProto())
    .setAvailableStartTime(availableStartTimeElement.toProto())
    .setAvailableEndTime(availableEndTimeElement.toProto())
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.PractitionerRole.PractitionerRoleNotAvailableComponent.toProto():
      PractitionerRole.NotAvailable {
    val protoValue = PractitionerRole.NotAvailable.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setDescription(descriptionElement.toProto())
    .setDuring(during.toProto())
    .build()
    return protoValue
  }

  private fun PractitionerRole.AvailableTime.toHapi():
      org.hl7.fhir.r4.model.PractitionerRole.PractitionerRoleAvailableTimeComponent {
    val hapiValue = org.hl7.fhir.r4.model.PractitionerRole.PractitionerRoleAvailableTimeComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    daysOfWeekList.map{hapiValue.addDaysOfWeek(org.hl7.fhir.r4.model.PractitionerRole.DaysOfWeek.valueOf(it.value.name.replace("_","")))}
    hapiValue.setAllDayElement(allDay.toHapi())
    hapiValue.setAvailableStartTimeElement(availableStartTime.toHapi())
    hapiValue.setAvailableEndTimeElement(availableEndTime.toHapi())
    return hapiValue
  }

  private fun PractitionerRole.NotAvailable.toHapi():
      org.hl7.fhir.r4.model.PractitionerRole.PractitionerRoleNotAvailableComponent {
    val hapiValue = org.hl7.fhir.r4.model.PractitionerRole.PractitionerRoleNotAvailableComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setDuring(during.toHapi())
    return hapiValue
  }
}
