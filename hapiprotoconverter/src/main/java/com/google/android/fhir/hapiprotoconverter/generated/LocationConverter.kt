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

public object LocationConverter {
  public fun Location.toHapi(): org.hl7.fhir.r4.model.Location {
    val hapiValue = org.hl7.fhir.r4.model.Location()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.Location.LocationStatus.valueOf(status.value.name.replace("_", ""))
    )
    hapiValue.setOperationalStatus(operationalStatus.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setAlias(aliasList.map { it.toHapi() })
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setMode(
      org.hl7.fhir.r4.model.Location.LocationMode.valueOf(mode.value.name.replace("_", ""))
    )
    hapiValue.setType(typeList.map { it.toHapi() })
    hapiValue.setTelecom(telecomList.map { it.toHapi() })
    hapiValue.setAddress(address.toHapi())
    hapiValue.setPhysicalType(physicalType.toHapi())
    hapiValue.setPosition(position.toHapi())
    hapiValue.setManagingOrganization(managingOrganization.toHapi())
    hapiValue.setPartOf(partOf.toHapi())
    hapiValue.setHoursOfOperation(hoursOfOperationList.map { it.toHapi() })
    hapiValue.setAvailabilityExceptionsElement(availabilityExceptions.toHapi())
    hapiValue.setEndpoint(endpointList.map { it.toHapi() })
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Location.toProto(): Location {
    val protoValue =
      Location.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setStatus(
          Location.StatusCode.newBuilder()
            .setValue(
              LocationStatusCode.Value.valueOf(status.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setOperationalStatus(operationalStatus.toProto())
        .setName(nameElement.toProto())
        .addAllAlias(alias.map { it.toProto() })
        .setDescription(descriptionElement.toProto())
        .setMode(
          Location.ModeCode.newBuilder()
            .setValue(LocationModeCode.Value.valueOf(mode.toCode().replace("-", "_").toUpperCase()))
            .build()
        )
        .addAllType(type.map { it.toProto() })
        .addAllTelecom(telecom.map { it.toProto() })
        .setAddress(address.toProto())
        .setPhysicalType(physicalType.toProto())
        .setPosition(position.toProto())
        .setManagingOrganization(managingOrganization.toProto())
        .setPartOf(partOf.toProto())
        .addAllHoursOfOperation(hoursOfOperation.map { it.toProto() })
        .setAvailabilityExceptions(availabilityExceptionsElement.toProto())
        .addAllEndpoint(endpoint.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Location.LocationPositionComponent.toProto():
    Location.Position {
    val protoValue =
      Location.Position.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setLongitude(longitudeElement.toProto())
        .setLatitude(latitudeElement.toProto())
        .setAltitude(altitudeElement.toProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Location.LocationHoursOfOperationComponent.toProto():
    Location.HoursOfOperation {
    val protoValue =
      Location.HoursOfOperation.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllDaysOfWeek(
          daysOfWeek.map {
            Location.HoursOfOperation.DaysOfWeekCode.newBuilder()
              .setValue(
                DaysOfWeekCode.Value.valueOf(it.value.toCode().replace("-", "_").toUpperCase())
              )
              .build()
          }
        )
        .setAllDay(allDayElement.toProto())
        .setOpeningTime(openingTimeElement.toProto())
        .setClosingTime(closingTimeElement.toProto())
        .build()
    return protoValue
  }

  private fun Location.Position.toHapi(): org.hl7.fhir.r4.model.Location.LocationPositionComponent {
    val hapiValue = org.hl7.fhir.r4.model.Location.LocationPositionComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setLongitudeElement(longitude.toHapi())
    hapiValue.setLatitudeElement(latitude.toHapi())
    hapiValue.setAltitudeElement(altitude.toHapi())
    return hapiValue
  }

  private fun Location.HoursOfOperation.toHapi():
    org.hl7.fhir.r4.model.Location.LocationHoursOfOperationComponent {
    val hapiValue = org.hl7.fhir.r4.model.Location.LocationHoursOfOperationComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    daysOfWeekList.map {
      hapiValue.addDaysOfWeek(
        org.hl7.fhir.r4.model.Location.DaysOfWeek.valueOf(it.value.name.replace("_", ""))
      )
    }
    hapiValue.setAllDayElement(allDay.toHapi())
    hapiValue.setOpeningTimeElement(openingTime.toHapi())
    hapiValue.setClosingTimeElement(closingTime.toHapi())
    return hapiValue
  }
}
