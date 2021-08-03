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
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
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

public object HealthcareServiceConverter {
  @JvmStatic
  public fun HealthcareService.toHapi(): org.hl7.fhir.r4.model.HealthcareService {
    val hapiValue = org.hl7.fhir.r4.model.HealthcareService()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setActiveElement(active.toHapi())
    hapiValue.setProvidedBy(providedBy.toHapi())
    hapiValue.setCategory(categoryList.map { it.toHapi() })
    hapiValue.setType(typeList.map { it.toHapi() })
    hapiValue.setSpecialty(specialtyList.map { it.toHapi() })
    hapiValue.setLocation(locationList.map { it.toHapi() })
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setCommentElement(comment.toHapi())
    hapiValue.setExtraDetailsElement(extraDetails.toHapi())
    hapiValue.setPhoto(photo.toHapi())
    hapiValue.setTelecom(telecomList.map { it.toHapi() })
    hapiValue.setCoverageArea(coverageAreaList.map { it.toHapi() })
    hapiValue.setServiceProvisionCode(serviceProvisionCodeList.map { it.toHapi() })
    hapiValue.setEligibility(eligibilityList.map { it.toHapi() })
    hapiValue.setProgram(programList.map { it.toHapi() })
    hapiValue.setCharacteristic(characteristicList.map { it.toHapi() })
    hapiValue.setCommunication(communicationList.map { it.toHapi() })
    hapiValue.setReferralMethod(referralMethodList.map { it.toHapi() })
    hapiValue.setAppointmentRequiredElement(appointmentRequired.toHapi())
    hapiValue.setAvailableTime(availableTimeList.map { it.toHapi() })
    hapiValue.setNotAvailable(notAvailableList.map { it.toHapi() })
    hapiValue.setAvailabilityExceptionsElement(availabilityExceptions.toHapi())
    hapiValue.setEndpoint(endpointList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.HealthcareService.toProto(): HealthcareService {
    val protoValue =
      HealthcareService.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setLanguage(languageElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setActive(activeElement.toProto())
        .setProvidedBy(providedBy.toProto())
        .addAllCategory(category.map { it.toProto() })
        .addAllType(type.map { it.toProto() })
        .addAllSpecialty(specialty.map { it.toProto() })
        .addAllLocation(location.map { it.toProto() })
        .setName(nameElement.toProto())
        .setComment(commentElement.toProto())
        .setExtraDetails(extraDetailsElement.toProto())
        .setPhoto(photo.toProto())
        .addAllTelecom(telecom.map { it.toProto() })
        .addAllCoverageArea(coverageArea.map { it.toProto() })
        .addAllServiceProvisionCode(serviceProvisionCode.map { it.toProto() })
        .addAllEligibility(eligibility.map { it.toProto() })
        .addAllProgram(program.map { it.toProto() })
        .addAllCharacteristic(characteristic.map { it.toProto() })
        .addAllCommunication(communication.map { it.toProto() })
        .addAllReferralMethod(referralMethod.map { it.toProto() })
        .setAppointmentRequired(appointmentRequiredElement.toProto())
        .addAllAvailableTime(availableTime.map { it.toProto() })
        .addAllNotAvailable(notAvailable.map { it.toProto() })
        .setAvailabilityExceptions(availabilityExceptionsElement.toProto())
        .addAllEndpoint(endpoint.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceEligibilityComponent.toProto():
    HealthcareService.Eligibility {
    val protoValue =
      HealthcareService.Eligibility.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCode(code.toProto())
        .setComment(commentElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceAvailableTimeComponent.toProto():
    HealthcareService.AvailableTime {
    val protoValue =
      HealthcareService.AvailableTime.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllDaysOfWeek(
          daysOfWeek.map {
            HealthcareService.AvailableTime.DaysOfWeekCode.newBuilder()
              .setValue(
                DaysOfWeekCode.Value.valueOf(it.value.toCode().replace("-", "_").toUpperCase())
              )
              .build()
          }
        )
        .setAllDay(allDayElement.toProto())
        .setAvailableStartTime(availableStartTimeElement.toProto())
        .setAvailableEndTime(availableEndTimeElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceNotAvailableComponent.toProto():
    HealthcareService.NotAvailable {
    val protoValue =
      HealthcareService.NotAvailable.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setDescription(descriptionElement.toProto())
        .setDuring(during.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun HealthcareService.Eligibility.toHapi():
    org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceEligibilityComponent {
    val hapiValue = org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceEligibilityComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCode(code.toHapi())
    hapiValue.setCommentElement(comment.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun HealthcareService.AvailableTime.toHapi():
    org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceAvailableTimeComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceAvailableTimeComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    daysOfWeekList.forEach {
      hapiValue.addDaysOfWeek(
        org.hl7.fhir.r4.model.HealthcareService.DaysOfWeek.valueOf(it.value.name.replace("_", ""))
      )
    }
    hapiValue.setAllDayElement(allDay.toHapi())
    hapiValue.setAvailableStartTimeElement(availableStartTime.toHapi())
    hapiValue.setAvailableEndTimeElement(availableEndTime.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun HealthcareService.NotAvailable.toHapi():
    org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceNotAvailableComponent {
    val hapiValue = org.hl7.fhir.r4.model.HealthcareService.HealthcareServiceNotAvailableComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setDuring(during.toHapi())
    return hapiValue
  }
}
