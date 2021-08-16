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

import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.Base64BinaryConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.Base64BinaryConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Device
import com.google.fhir.r4.core.Device.DeviceName
import com.google.fhir.r4.core.Device.UdiCarrier
import com.google.fhir.r4.core.DeviceNameTypeCode
import com.google.fhir.r4.core.FHIRDeviceStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.UDIEntryTypeCode
import kotlin.jvm.JvmStatic

object DeviceConverter {
  @JvmStatic
  fun Device.toHapi(): org.hl7.fhir.r4.model.Device {
    val hapiValue = org.hl7.fhir.r4.model.Device()
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
    if (hasDefinition()) {
      hapiValue.definition = definition.toHapi()
    }
    if (udiCarrierCount > 0) {
      hapiValue.udiCarrier = udiCarrierList.map { it.toHapi() }
    }
    hapiValue.status =
      org.hl7.fhir.r4.model.Device.FHIRDeviceStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (statusReasonCount > 0) {
      hapiValue.statusReason = statusReasonList.map { it.toHapi() }
    }
    if (hasDistinctIdentifier()) {
      hapiValue.distinctIdentifierElement = distinctIdentifier.toHapi()
    }
    if (hasManufacturer()) {
      hapiValue.manufacturerElement = manufacturer.toHapi()
    }
    if (hasManufactureDate()) {
      hapiValue.manufactureDateElement = manufactureDate.toHapi()
    }
    if (hasExpirationDate()) {
      hapiValue.expirationDateElement = expirationDate.toHapi()
    }
    if (hasLotNumber()) {
      hapiValue.lotNumberElement = lotNumber.toHapi()
    }
    if (hasSerialNumber()) {
      hapiValue.serialNumberElement = serialNumber.toHapi()
    }
    if (deviceNameCount > 0) {
      hapiValue.deviceName = deviceNameList.map { it.toHapi() }
    }
    if (hasModelNumber()) {
      hapiValue.modelNumberElement = modelNumber.toHapi()
    }
    if (hasPartNumber()) {
      hapiValue.partNumberElement = partNumber.toHapi()
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (specializationCount > 0) {
      hapiValue.specialization = specializationList.map { it.toHapi() }
    }
    if (versionCount > 0) {
      hapiValue.version = versionList.map { it.toHapi() }
    }
    if (propertyCount > 0) {
      hapiValue.property = propertyList.map { it.toHapi() }
    }
    if (hasPatient()) {
      hapiValue.patient = patient.toHapi()
    }
    if (hasOwner()) {
      hapiValue.owner = owner.toHapi()
    }
    if (contactCount > 0) {
      hapiValue.contact = contactList.map { it.toHapi() }
    }
    if (hasLocation()) {
      hapiValue.location = location.toHapi()
    }
    if (hasUrl()) {
      hapiValue.urlElement = url.toHapi()
    }
    if (noteCount > 0) {
      hapiValue.note = noteList.map { it.toHapi() }
    }
    if (safetyCount > 0) {
      hapiValue.safety = safetyList.map { it.toHapi() }
    }
    if (hasParent()) {
      hapiValue.parent = parent.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.Device.toProto(): Device {
    val protoValue = Device.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasDefinition()) {
      protoValue.definition = definition.toProto()
    }
    if (hasUdiCarrier()) {
      protoValue.addAllUdiCarrier(udiCarrier.map { it.toProto() })
    }
    protoValue.status =
      Device.StatusCode.newBuilder()
        .setValue(
          FHIRDeviceStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasStatusReason()) {
      protoValue.addAllStatusReason(statusReason.map { it.toProto() })
    }
    if (hasDistinctIdentifier()) {
      protoValue.distinctIdentifier = distinctIdentifierElement.toProto()
    }
    if (hasManufacturer()) {
      protoValue.manufacturer = manufacturerElement.toProto()
    }
    if (hasManufactureDate()) {
      protoValue.manufactureDate = manufactureDateElement.toProto()
    }
    if (hasExpirationDate()) {
      protoValue.expirationDate = expirationDateElement.toProto()
    }
    if (hasLotNumber()) {
      protoValue.lotNumber = lotNumberElement.toProto()
    }
    if (hasSerialNumber()) {
      protoValue.serialNumber = serialNumberElement.toProto()
    }
    if (hasDeviceName()) {
      protoValue.addAllDeviceName(deviceName.map { it.toProto() })
    }
    if (hasModelNumber()) {
      protoValue.modelNumber = modelNumberElement.toProto()
    }
    if (hasPartNumber()) {
      protoValue.partNumber = partNumberElement.toProto()
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasSpecialization()) {
      protoValue.addAllSpecialization(specialization.map { it.toProto() })
    }
    if (hasVersion()) {
      protoValue.addAllVersion(version.map { it.toProto() })
    }
    if (hasProperty()) {
      protoValue.addAllProperty(property.map { it.toProto() })
    }
    if (hasPatient()) {
      protoValue.patient = patient.toProto()
    }
    if (hasOwner()) {
      protoValue.owner = owner.toProto()
    }
    if (hasContact()) {
      protoValue.addAllContact(contact.map { it.toProto() })
    }
    if (hasLocation()) {
      protoValue.location = location.toProto()
    }
    if (hasUrl()) {
      protoValue.url = urlElement.toProto()
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    if (hasSafety()) {
      protoValue.addAllSafety(safety.map { it.toProto() })
    }
    if (hasParent()) {
      protoValue.parent = parent.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Device.DeviceUdiCarrierComponent.toProto(): Device.UdiCarrier {
    val protoValue = Device.UdiCarrier.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDeviceIdentifier()) {
      protoValue.deviceIdentifier = deviceIdentifierElement.toProto()
    }
    if (hasIssuer()) {
      protoValue.issuer = issuerElement.toProto()
    }
    if (hasJurisdiction()) {
      protoValue.jurisdiction = jurisdictionElement.toProto()
    }
    if (hasCarrierAIDC()) {
      protoValue.carrierAidc = carrierAIDCElement.toProto()
    }
    if (hasCarrierHRF()) {
      protoValue.carrierHrf = carrierHRFElement.toProto()
    }
    protoValue.entryType =
      Device.UdiCarrier.EntryTypeCode.newBuilder()
        .setValue(
          UDIEntryTypeCode.Value.valueOf(
            entryType.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Device.DeviceDeviceNameComponent.toProto(): Device.DeviceName {
    val protoValue = Device.DeviceName.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    protoValue.type =
      Device.DeviceName.TypeCode.newBuilder()
        .setValue(
          DeviceNameTypeCode.Value.valueOf(
            type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Device.DeviceSpecializationComponent.toProto():
    Device.Specialization {
    val protoValue = Device.Specialization.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSystemType()) {
      protoValue.systemType = systemType.toProto()
    }
    if (hasVersion()) {
      protoValue.version = versionElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Device.DeviceVersionComponent.toProto(): Device.Version {
    val protoValue = Device.Version.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasComponent()) {
      protoValue.component = component.toProto()
    }
    if (hasValue()) {
      protoValue.value = valueElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Device.DevicePropertyComponent.toProto(): Device.Property {
    val protoValue = Device.Property.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasValueQuantity()) {
      protoValue.addAllValueQuantity(valueQuantity.map { it.toProto() })
    }
    if (hasValueCode()) {
      protoValue.addAllValueCode(valueCode.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Device.UdiCarrier.toHapi(): org.hl7.fhir.r4.model.Device.DeviceUdiCarrierComponent {
    val hapiValue = org.hl7.fhir.r4.model.Device.DeviceUdiCarrierComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasDeviceIdentifier()) {
      hapiValue.deviceIdentifierElement = deviceIdentifier.toHapi()
    }
    if (hasIssuer()) {
      hapiValue.issuerElement = issuer.toHapi()
    }
    if (hasJurisdiction()) {
      hapiValue.jurisdictionElement = jurisdiction.toHapi()
    }
    if (hasCarrierAidc()) {
      hapiValue.carrierAIDCElement = carrierAidc.toHapi()
    }
    if (hasCarrierHrf()) {
      hapiValue.carrierHRFElement = carrierHrf.toHapi()
    }
    hapiValue.entryType =
      org.hl7.fhir.r4.model.Device.UDIEntryType.valueOf(
        entryType.value.name.hapiCodeCheck().replace("_", "")
      )
    return hapiValue
  }

  @JvmStatic
  private fun Device.DeviceName.toHapi(): org.hl7.fhir.r4.model.Device.DeviceDeviceNameComponent {
    val hapiValue = org.hl7.fhir.r4.model.Device.DeviceDeviceNameComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    hapiValue.type =
      org.hl7.fhir.r4.model.Device.DeviceNameType.valueOf(
        type.value.name.hapiCodeCheck().replace("_", "")
      )
    return hapiValue
  }

  @JvmStatic
  private fun Device.Specialization.toHapi():
    org.hl7.fhir.r4.model.Device.DeviceSpecializationComponent {
    val hapiValue = org.hl7.fhir.r4.model.Device.DeviceSpecializationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasSystemType()) {
      hapiValue.systemType = systemType.toHapi()
    }
    if (hasVersion()) {
      hapiValue.versionElement = version.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun Device.Version.toHapi(): org.hl7.fhir.r4.model.Device.DeviceVersionComponent {
    val hapiValue = org.hl7.fhir.r4.model.Device.DeviceVersionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasComponent()) {
      hapiValue.component = component.toHapi()
    }
    if (hasValue()) {
      hapiValue.valueElement = value.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun Device.Property.toHapi(): org.hl7.fhir.r4.model.Device.DevicePropertyComponent {
    val hapiValue = org.hl7.fhir.r4.model.Device.DevicePropertyComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (valueQuantityCount > 0) {
      hapiValue.valueQuantity = valueQuantityList.map { it.toHapi() }
    }
    if (valueCodeCount > 0) {
      hapiValue.valueCode = valueCodeList.map { it.toHapi() }
    }
    return hapiValue
  }
}
