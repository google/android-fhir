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

object LocationConverter {
  @JvmStatic
  fun Location.toHapi(): org.hl7.fhir.r4.model.Location {
    val hapiValue = org.hl7.fhir.r4.model.Location()
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
    hapiValue.status =
      org.hl7.fhir.r4.model.Location.LocationStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasOperationalStatus()) {
      hapiValue.operationalStatus = operationalStatus.toHapi()
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    if (aliasCount > 0) {
      hapiValue.alias = aliasList.map { it.toHapi() }
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    hapiValue.mode =
      org.hl7.fhir.r4.model.Location.LocationMode.valueOf(
        mode.value.name.hapiCodeCheck().replace("_", "")
      )
    if (typeCount > 0) {
      hapiValue.type = typeList.map { it.toHapi() }
    }
    if (telecomCount > 0) {
      hapiValue.telecom = telecomList.map { it.toHapi() }
    }
    if (hasAddress()) {
      hapiValue.address = address.toHapi()
    }
    if (hasPhysicalType()) {
      hapiValue.physicalType = physicalType.toHapi()
    }
    if (hasPosition()) {
      hapiValue.position = position.toHapi()
    }
    if (hasManagingOrganization()) {
      hapiValue.managingOrganization = managingOrganization.toHapi()
    }
    if (hasPartOf()) {
      hapiValue.partOf = partOf.toHapi()
    }
    if (hoursOfOperationCount > 0) {
      hapiValue.hoursOfOperation = hoursOfOperationList.map { it.toHapi() }
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
  fun org.hl7.fhir.r4.model.Location.toProto(): Location {
    val protoValue = Location.newBuilder().setId(Id.newBuilder().setValue(id))
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
    protoValue.status =
      Location.StatusCode.newBuilder()
        .setValue(
          LocationStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasOperationalStatus()) {
      protoValue.operationalStatus = operationalStatus.toProto()
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    if (hasAlias()) {
      protoValue.addAllAlias(alias.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    protoValue.mode =
      Location.ModeCode.newBuilder()
        .setValue(
          LocationModeCode.Value.valueOf(
            mode.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasType()) {
      protoValue.addAllType(type.map { it.toProto() })
    }
    if (hasTelecom()) {
      protoValue.addAllTelecom(telecom.map { it.toProto() })
    }
    if (hasAddress()) {
      protoValue.address = address.toProto()
    }
    if (hasPhysicalType()) {
      protoValue.physicalType = physicalType.toProto()
    }
    if (hasPosition()) {
      protoValue.position = position.toProto()
    }
    if (hasManagingOrganization()) {
      protoValue.managingOrganization = managingOrganization.toProto()
    }
    if (hasPartOf()) {
      protoValue.partOf = partOf.toProto()
    }
    if (hasHoursOfOperation()) {
      protoValue.addAllHoursOfOperation(hoursOfOperation.map { it.toProto() })
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
      protoValue.longitude = longitudeElement.toProto()
    }
    if (hasLatitude()) {
      protoValue.latitude = latitudeElement.toProto()
    }
    if (hasAltitude()) {
      protoValue.altitude = altitudeElement.toProto()
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
      protoValue.allDay = allDayElement.toProto()
    }
    if (hasOpeningTime()) {
      protoValue.openingTime = openingTimeElement.toProto()
    }
    if (hasClosingTime()) {
      protoValue.closingTime = closingTimeElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Location.Position.toHapi(): org.hl7.fhir.r4.model.Location.LocationPositionComponent {
    val hapiValue = org.hl7.fhir.r4.model.Location.LocationPositionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasLongitude()) {
      hapiValue.longitudeElement = longitude.toHapi()
    }
    if (hasLatitude()) {
      hapiValue.latitudeElement = latitude.toHapi()
    }
    if (hasAltitude()) {
      hapiValue.altitudeElement = altitude.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun Location.HoursOfOperation.toHapi():
    org.hl7.fhir.r4.model.Location.LocationHoursOfOperationComponent {
    val hapiValue = org.hl7.fhir.r4.model.Location.LocationHoursOfOperationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    daysOfWeekList.forEach {
      hapiValue.addDaysOfWeek(
        org.hl7.fhir.r4.model.Location.DaysOfWeek.valueOf(
          it.value.name.hapiCodeCheck().replace("_", "")
        )
      )
    }
    if (hasAllDay()) {
      hapiValue.allDayElement = allDay.toHapi()
    }
    if (hasOpeningTime()) {
      hapiValue.openingTimeElement = openingTime.toHapi()
    }
    if (hasClosingTime()) {
      hapiValue.closingTimeElement = closingTime.toHapi()
    }
    return hapiValue
  }
}
