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
import kotlin.jvm.JvmStatic

public object PractitionerRoleConverter {
  @JvmStatic
  public fun PractitionerRole.toHapi(): org.hl7.fhir.r4.model.PractitionerRole {
    val hapiValue = org.hl7.fhir.r4.model.PractitionerRole()
    hapiValue.id = id.value
    if (hasMeta()) {
      hapiValue.setMeta(meta.toHapi())
    }
    if (hasImplicitRules()) {
      hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    }
    if (hasText()) {
      hapiValue.setText(text.toHapi())
    }
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (identifierCount > 0) {
      hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    }
    if (hasActive()) {
      hapiValue.setActiveElement(active.toHapi())
    }
    if (hasPeriod()) {
      hapiValue.setPeriod(period.toHapi())
    }
    if (hasPractitioner()) {
      hapiValue.setPractitioner(practitioner.toHapi())
    }
    if (hasOrganization()) {
      hapiValue.setOrganization(organization.toHapi())
    }
    if (codeCount > 0) {
      hapiValue.setCode(codeList.map { it.toHapi() })
    }
    if (specialtyCount > 0) {
      hapiValue.setSpecialty(specialtyList.map { it.toHapi() })
    }
    if (locationCount > 0) {
      hapiValue.setLocation(locationList.map { it.toHapi() })
    }
    if (healthcareServiceCount > 0) {
      hapiValue.setHealthcareService(healthcareServiceList.map { it.toHapi() })
    }
    if (telecomCount > 0) {
      hapiValue.setTelecom(telecomList.map { it.toHapi() })
    }
    if (availableTimeCount > 0) {
      hapiValue.setAvailableTime(availableTimeList.map { it.toHapi() })
    }
    if (notAvailableCount > 0) {
      hapiValue.setNotAvailable(notAvailableList.map { it.toHapi() })
    }
    if (hasAvailabilityExceptions()) {
      hapiValue.setAvailabilityExceptionsElement(availabilityExceptions.toHapi())
    }
    if (endpointCount > 0) {
      hapiValue.setEndpoint(endpointList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.PractitionerRole.toProto(): PractitionerRole {
    val protoValue = PractitionerRole.newBuilder().setId(Id.newBuilder().setValue(id))
    if (hasMeta()) {
      protoValue.setMeta(meta.toProto())
    }
    if (hasImplicitRules()) {
      protoValue.setImplicitRules(implicitRulesElement.toProto())
    }
    if (hasText()) {
      protoValue.setText(text.toProto())
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
    if (hasActive()) {
      protoValue.setActive(activeElement.toProto())
    }
    if (hasPeriod()) {
      protoValue.setPeriod(period.toProto())
    }
    if (hasPractitioner()) {
      protoValue.setPractitioner(practitioner.toProto())
    }
    if (hasOrganization()) {
      protoValue.setOrganization(organization.toProto())
    }
    if (hasCode()) {
      protoValue.addAllCode(code.map { it.toProto() })
    }
    if (hasSpecialty()) {
      protoValue.addAllSpecialty(specialty.map { it.toProto() })
    }
    if (hasLocation()) {
      protoValue.addAllLocation(location.map { it.toProto() })
    }
    if (hasHealthcareService()) {
      protoValue.addAllHealthcareService(healthcareService.map { it.toProto() })
    }
    if (hasTelecom()) {
      protoValue.addAllTelecom(telecom.map { it.toProto() })
    }
    if (hasAvailableTime()) {
      protoValue.addAllAvailableTime(availableTime.map { it.toProto() })
    }
    if (hasNotAvailable()) {
      protoValue.addAllNotAvailable(notAvailable.map { it.toProto() })
    }
    if (hasAvailabilityExceptions()) {
      protoValue.setAvailabilityExceptions(availabilityExceptionsElement.toProto())
    }
    if (hasEndpoint()) {
      protoValue.addAllEndpoint(endpoint.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.PractitionerRole.PractitionerRoleAvailableTimeComponent.toProto():
    PractitionerRole.AvailableTime {
    val protoValue =
      PractitionerRole.AvailableTime.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.addAllDaysOfWeek(
      daysOfWeek.map {
        PractitionerRole.AvailableTime.DaysOfWeekCode.newBuilder()
          .setValue(
            DaysOfWeekCode.Value.valueOf(
              it.value.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
      }
    )
    if (hasAllDay()) {
      protoValue.setAllDay(allDayElement.toProto())
    }
    if (hasAvailableStartTime()) {
      protoValue.setAvailableStartTime(availableStartTimeElement.toProto())
    }
    if (hasAvailableEndTime()) {
      protoValue.setAvailableEndTime(availableEndTimeElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.PractitionerRole.PractitionerRoleNotAvailableComponent.toProto():
    PractitionerRole.NotAvailable {
    val protoValue =
      PractitionerRole.NotAvailable.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.setDescription(descriptionElement.toProto())
    }
    if (hasDuring()) {
      protoValue.setDuring(during.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun PractitionerRole.AvailableTime.toHapi():
    org.hl7.fhir.r4.model.PractitionerRole.PractitionerRoleAvailableTimeComponent {
    val hapiValue = org.hl7.fhir.r4.model.PractitionerRole.PractitionerRoleAvailableTimeComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    daysOfWeekList.forEach {
      hapiValue.addDaysOfWeek(
        org.hl7.fhir.r4.model.PractitionerRole.DaysOfWeek.valueOf(
          it.value.name.hapiCodeCheck().replace("_", "")
        )
      )
    }
    if (hasAllDay()) {
      hapiValue.setAllDayElement(allDay.toHapi())
    }
    if (hasAvailableStartTime()) {
      hapiValue.setAvailableStartTimeElement(availableStartTime.toHapi())
    }
    if (hasAvailableEndTime()) {
      hapiValue.setAvailableEndTimeElement(availableEndTime.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun PractitionerRole.NotAvailable.toHapi():
    org.hl7.fhir.r4.model.PractitionerRole.PractitionerRoleNotAvailableComponent {
    val hapiValue = org.hl7.fhir.r4.model.PractitionerRole.PractitionerRoleNotAvailableComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasDescription()) {
      hapiValue.setDescriptionElement(description.toHapi())
    }
    if (hasDuring()) {
      hapiValue.setDuring(during.toHapi())
    }
    return hapiValue
  }
}
