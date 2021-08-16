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

import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toProto
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
import com.google.fhir.r4.core.HealthcareService
import com.google.fhir.r4.core.HealthcareService.AvailableTime
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic

object HealthcareServiceConverter {
  @JvmStatic
  fun HealthcareService.toHapi(): org.hl7.fhir.r4.model.HealthcareService {
    val hapiValue = org.hl7.fhir.r4.model.HealthcareService()
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
    if (hasActive()) {
      hapiValue.activeElement = active.toHapi()
    }
    if (hasProvidedBy()) {
      hapiValue.providedBy = providedBy.toHapi()
    }
    if (categoryCount > 0) {
      hapiValue.category = categoryList.map { it.toHapi() }
    }
    if (typeCount > 0) {
      hapiValue.type = typeList.map { it.toHapi() }
    }
    if (specialtyCount > 0) {
      hapiValue.specialty = specialtyList.map { it.toHapi() }
    }
    if (locationCount > 0) {
      hapiValue.location = locationList.map { it.toHapi() }
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (hasComment()) {
      hapiValue.commentElement = comment.toHapi()
    }
    if (hasExtraDetails()) {
      hapiValue.extraDetailsElement = extraDetails.toHapi()
    }
    if (hasPhoto()) {
      hapiValue.photo = photo.toHapi()
    }
    if (telecomCount > 0) {
      hapiValue.telecom = telecomList.map { it.toHapi() }
    }
    if (coverageAreaCount > 0) {
      hapiValue.coverageArea = coverageAreaList.map { it.toHapi() }
    }
    if (serviceProvisionCodeCount > 0) {
      hapiValue.serviceProvisionCode = serviceProvisionCodeList.map { it.toHapi() }
    }
    if (eligibilityCount > 0) {
      hapiValue.eligibility = eligibilityList.map { it.toHapi() }
    }
    if (programCount > 0) {
      hapiValue.program = programList.map { it.toHapi() }
    }
    if (characteristicCount > 0) {
      hapiValue.characteristic = characteristicList.map { it.toHapi() }
    }
    if (communicationCount > 0) {
      hapiValue.communication = communicationList.map { it.toHapi() }
    }
    if (referralMethodCount > 0) {
      hapiValue.referralMethod = referralMethodList.map { it.toHapi() }
    }
    if (hasAppointmentRequired()) {
      hapiValue.appointmentRequiredElement = appointmentRequired.toHapi()
    }
    if (availableTimeCount > 0) {
      hapiValue.availableTime = availableTimeList.map { it.toHapi() }
    }
    if (notAvailableCount > 0) {
      hapiValue.notAvailable = notAvailableList.map { it.toHapi() }
    }
    if (hasAvailabilityExceptions()) {
      hapiValue.availabilityExceptionsElement = availabilityExceptions.toHapi()
    }
    if (endpointCount > 0) {
      hapiValue.endpoint = endpointList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.HealthcareService.toProto(): HealthcareService {
    val protoValue = HealthcareService.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasActive()) {
      protoValue.active = activeElement.toProto()
    }
    if (hasProvidedBy()) {
      protoValue.providedBy = providedBy.toProto()
    }
    if (hasCategory()) {
      protoValue.addAllCategory(category.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.addAllType(type.map { it.toProto() })
    }
    if (hasSpecialty()) {
      protoValue.addAllSpecialty(specialty.map { it.toProto() })
    }
    if (hasLocation()) {
      protoValue.addAllLocation(location.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasComment()) {
      protoValue.comment = commentElement.toProto()
    }
    if (hasExtraDetails()) {
      protoValue.extraDetails = extraDetailsElement.toProto()
    }
    if (hasPhoto()) {
      protoValue.photo = photo.toProto()
    }
    if (hasTelecom()) {
      protoValue.addAllTelecom(telecom.map { it.toProto() })
    }
    if (hasCoverageArea()) {
      protoValue.addAllCoverageArea(coverageArea.map { it.toProto() })
    }
    if (hasServiceProvisionCode()) {
      protoValue.addAllServiceProvisionCode(serviceProvisionCode.map { it.toProto() })
    }
    if (hasEligibility()) {
      protoValue.addAllEligibility(eligibility.map { it.toProto() })
    }
    if (hasProgram()) {
      protoValue.addAllProgram(program.map { it.toProto() })
    }
    if (hasCharacteristic()) {
      protoValue.addAllCharacteristic(characteristic.map { it.toProto() })
    }
    if (hasCommunication()) {
      protoValue.addAllCommunication(communication.map { it.toProto() })
    }
    if (hasReferralMethod()) {
      protoValue.addAllReferralMethod(referralMethod.map { it.toProto() })
    }
    if (hasAppointmentRequired()) {
      protoValue.appointmentRequired = appointmentRequiredElement.toProto()
    }
    if (hasAvailableTime()) {
      protoValue.addAllAvailableTime(availableTime.map { it.toProto() })
    }
    if (hasNotAvailable()) {
      protoValue.addAllNotAvailable(notAvailable.map { it.toProto() })
    }
    if (hasAvailabilityExceptions()) {
      protoValue.availabilityExceptions = availabilityExceptionsElement.toProto()
    }
    if (hasEndpoint()) {
      protoValue.addAllEndpoint(endpoint.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceEligibilityComponent.toProto():
    HealthcareService.Eligibility {
    val protoValue =
      HealthcareService.Eligibility.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    if (hasComment()) {
      protoValue.comment = commentElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceAvailableTimeComponent.toProto():
    HealthcareService.AvailableTime {
    val protoValue =
      HealthcareService.AvailableTime.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.addAllDaysOfWeek(
      daysOfWeek.map {
        HealthcareService.AvailableTime.DaysOfWeekCode.newBuilder()
          .setValue(
            DaysOfWeekCode.Value.valueOf(
              it.value.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
      }
    )
    if (hasAllDay()) {
      protoValue.allDay = allDayElement.toProto()
    }
    if (hasAvailableStartTime()) {
      protoValue.availableStartTime = availableStartTimeElement.toProto()
    }
    if (hasAvailableEndTime()) {
      protoValue.availableEndTime = availableEndTimeElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceNotAvailableComponent.toProto():
    HealthcareService.NotAvailable {
    val protoValue =
      HealthcareService.NotAvailable.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasDuring()) {
      protoValue.during = during.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun HealthcareService.Eligibility.toHapi():
    org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceEligibilityComponent {
    val hapiValue = org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceEligibilityComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (hasComment()) {
      hapiValue.commentElement = comment.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun HealthcareService.AvailableTime.toHapi():
    org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceAvailableTimeComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceAvailableTimeComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    daysOfWeekList.forEach {
      hapiValue.addDaysOfWeek(
        org.hl7.fhir.r4.model.HealthcareService.DaysOfWeek.valueOf(
          it.value.name.hapiCodeCheck().replace("_", "")
        )
      )
    }
    if (hasAllDay()) {
      hapiValue.allDayElement = allDay.toHapi()
    }
    if (hasAvailableStartTime()) {
      hapiValue.availableStartTimeElement = availableStartTime.toHapi()
    }
    if (hasAvailableEndTime()) {
      hapiValue.availableEndTimeElement = availableEndTime.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun HealthcareService.NotAvailable.toHapi():
    org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceNotAvailableComponent {
    val hapiValue = org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceNotAvailableComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (hasDuring()) {
      hapiValue.during = during.toHapi()
    }
    return hapiValue
  }
}
