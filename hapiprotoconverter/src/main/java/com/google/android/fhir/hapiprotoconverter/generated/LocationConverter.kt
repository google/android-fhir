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

import com.google.android.fhir.hapiprotoconverter.generated.AddressConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AddressConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
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
import com.google.fhir.r4.core.Location
import com.google.fhir.r4.core.Location.HoursOfOperation
import com.google.fhir.r4.core.LocationModeCode
import com.google.fhir.r4.core.LocationStatusCode
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic

public object LocationConverter {
  @JvmStatic
  public fun Location.toHapi(): org.hl7.fhir.r4.model.Location {
    val hapiValue = org.hl7.fhir.r4.model.Location()
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
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.Location.LocationStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasOperationalStatus()) {
      hapiValue.setOperationalStatus(operationalStatus.toHapi())
    }
    if (hasName()) {
      hapiValue.setNameElement(name.toHapi())
    }
    if (aliasCount > 0) {
      hapiValue.setAlias(aliasList.map { it.toHapi() })
    }
    if (hasDescription()) {
      hapiValue.setDescriptionElement(description.toHapi())
    }
    hapiValue.setMode(
      org.hl7.fhir.r4.model.Location.LocationMode.valueOf(
        mode.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (typeCount > 0) {
      hapiValue.setType(typeList.map { it.toHapi() })
    }
    if (telecomCount > 0) {
      hapiValue.setTelecom(telecomList.map { it.toHapi() })
    }
    if (hasAddress()) {
      hapiValue.setAddress(address.toHapi())
    }
    if (hasPhysicalType()) {
      hapiValue.setPhysicalType(physicalType.toHapi())
    }
    if (hasPosition()) {
      hapiValue.setPosition(position.toHapi())
    }
    if (hasManagingOrganization()) {
      hapiValue.setManagingOrganization(managingOrganization.toHapi())
    }
    if (hasPartOf()) {
      hapiValue.setPartOf(partOf.toHapi())
    }
    if (hoursOfOperationCount > 0) {
      hapiValue.setHoursOfOperation(hoursOfOperationList.map { it.toHapi() })
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
  public fun org.hl7.fhir.r4.model.Location.toProto(): Location {
    val protoValue = Location.newBuilder().setId(Id.newBuilder().setValue(id))
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
    protoValue.setStatus(
      Location.StatusCode.newBuilder()
        .setValue(
          LocationStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasOperationalStatus()) {
      protoValue.setOperationalStatus(operationalStatus.toProto())
    }
    if (hasName()) {
      protoValue.setName(nameElement.toProto())
    }
    if (hasAlias()) {
      protoValue.addAllAlias(alias.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.setDescription(descriptionElement.toProto())
    }
    protoValue.setMode(
      Location.ModeCode.newBuilder()
        .setValue(
          LocationModeCode.Value.valueOf(
            mode.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasType()) {
      protoValue.addAllType(type.map { it.toProto() })
    }
    if (hasTelecom()) {
      protoValue.addAllTelecom(telecom.map { it.toProto() })
    }
    if (hasAddress()) {
      protoValue.setAddress(address.toProto())
    }
    if (hasPhysicalType()) {
      protoValue.setPhysicalType(physicalType.toProto())
    }
    if (hasPosition()) {
      protoValue.setPosition(position.toProto())
    }
    if (hasManagingOrganization()) {
      protoValue.setManagingOrganization(managingOrganization.toProto())
    }
    if (hasPartOf()) {
      protoValue.setPartOf(partOf.toProto())
    }
    if (hasHoursOfOperation()) {
      protoValue.addAllHoursOfOperation(hoursOfOperation.map { it.toProto() })
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
  private fun org.hl7.fhir.r4.model.Location.LocationPositionComponent.toProto():
    Location.Position {
    val protoValue = Location.Position.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasLongitude()) {
      protoValue.setLongitude(longitudeElement.toProto())
    }
    if (hasLatitude()) {
      protoValue.setLatitude(latitudeElement.toProto())
    }
    if (hasAltitude()) {
      protoValue.setAltitude(altitudeElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Location.LocationHoursOfOperationComponent.toProto():
    Location.HoursOfOperation {
    val protoValue = Location.HoursOfOperation.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.addAllDaysOfWeek(
      daysOfWeek.map {
        Location.HoursOfOperation.DaysOfWeekCode.newBuilder()
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
    if (hasOpeningTime()) {
      protoValue.setOpeningTime(openingTimeElement.toProto())
    }
    if (hasClosingTime()) {
      protoValue.setClosingTime(closingTimeElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Location.Position.toHapi(): org.hl7.fhir.r4.model.Location.LocationPositionComponent {
    val hapiValue = org.hl7.fhir.r4.model.Location.LocationPositionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasLongitude()) {
      hapiValue.setLongitudeElement(longitude.toHapi())
    }
    if (hasLatitude()) {
      hapiValue.setLatitudeElement(latitude.toHapi())
    }
    if (hasAltitude()) {
      hapiValue.setAltitudeElement(altitude.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun Location.HoursOfOperation.toHapi():
    org.hl7.fhir.r4.model.Location.LocationHoursOfOperationComponent {
    val hapiValue = org.hl7.fhir.r4.model.Location.LocationHoursOfOperationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    daysOfWeekList.forEach {
      hapiValue.addDaysOfWeek(
        org.hl7.fhir.r4.model.Location.DaysOfWeek.valueOf(
          it.value.name.hapiCodeCheck().replace("_", "")
        )
      )
    }
    if (hasAllDay()) {
      hapiValue.setAllDayElement(allDay.toHapi())
    }
    if (hasOpeningTime()) {
      hapiValue.setOpeningTimeElement(openingTime.toHapi())
    }
    if (hasClosingTime()) {
      hapiValue.setClosingTimeElement(closingTime.toHapi())
    }
    return hapiValue
  }
}
