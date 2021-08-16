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
import com.google.android.fhir.hapiprotoconverter.generated.ProdCharacteristicConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ProdCharacteristicConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ProductShelfLifeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ProductShelfLifeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.DeviceDefinition
import com.google.fhir.r4.core.DeviceDefinition.DeviceName
import com.google.fhir.r4.core.DeviceNameTypeCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

public object DeviceDefinitionConverter {
  @JvmStatic
  private fun DeviceDefinition.ManufacturerX.deviceDefinitionManufacturerToHapi(): Type {
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for DeviceDefinition.manufacturer[x]")
  }

  @JvmStatic
  private fun Type.deviceDefinitionManufacturerToProto(): DeviceDefinition.ManufacturerX {
    val protoValue = DeviceDefinition.ManufacturerX.newBuilder()
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun DeviceDefinition.toHapi(): org.hl7.fhir.r4.model.DeviceDefinition {
    val hapiValue = org.hl7.fhir.r4.model.DeviceDefinition()
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
    if (udiDeviceIdentifierCount > 0) {
      hapiValue.setUdiDeviceIdentifier(udiDeviceIdentifierList.map { it.toHapi() })
    }
    if (hasManufacturer()) {
      hapiValue.setManufacturer(manufacturer.deviceDefinitionManufacturerToHapi())
    }
    if (deviceNameCount > 0) {
      hapiValue.setDeviceName(deviceNameList.map { it.toHapi() })
    }
    if (hasModelNumber()) {
      hapiValue.setModelNumberElement(modelNumber.toHapi())
    }
    if (hasType()) {
      hapiValue.setType(type.toHapi())
    }
    if (specializationCount > 0) {
      hapiValue.setSpecialization(specializationList.map { it.toHapi() })
    }
    if (versionCount > 0) {
      hapiValue.setVersion(versionList.map { it.toHapi() })
    }
    if (safetyCount > 0) {
      hapiValue.setSafety(safetyList.map { it.toHapi() })
    }
    if (shelfLifeStorageCount > 0) {
      hapiValue.setShelfLifeStorage(shelfLifeStorageList.map { it.toHapi() })
    }
    if (hasPhysicalCharacteristics()) {
      hapiValue.setPhysicalCharacteristics(physicalCharacteristics.toHapi())
    }
    if (languageCodeCount > 0) {
      hapiValue.setLanguageCode(languageCodeList.map { it.toHapi() })
    }
    if (capabilityCount > 0) {
      hapiValue.setCapability(capabilityList.map { it.toHapi() })
    }
    if (propertyCount > 0) {
      hapiValue.setProperty(propertyList.map { it.toHapi() })
    }
    if (hasOwner()) {
      hapiValue.setOwner(owner.toHapi())
    }
    if (contactCount > 0) {
      hapiValue.setContact(contactList.map { it.toHapi() })
    }
    if (hasUrl()) {
      hapiValue.setUrlElement(url.toHapi())
    }
    if (hasOnlineInformation()) {
      hapiValue.setOnlineInformationElement(onlineInformation.toHapi())
    }
    if (noteCount > 0) {
      hapiValue.setNote(noteList.map { it.toHapi() })
    }
    if (hasQuantity()) {
      hapiValue.setQuantity(quantity.toHapi())
    }
    if (hasParentDevice()) {
      hapiValue.setParentDevice(parentDevice.toHapi())
    }
    if (materialCount > 0) {
      hapiValue.setMaterial(materialList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.DeviceDefinition.toProto(): DeviceDefinition {
    val protoValue = DeviceDefinition.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasUdiDeviceIdentifier()) {
      protoValue.addAllUdiDeviceIdentifier(udiDeviceIdentifier.map { it.toProto() })
    }
    if (hasManufacturer()) {
      protoValue.setManufacturer(manufacturer.deviceDefinitionManufacturerToProto())
    }
    if (hasDeviceName()) {
      protoValue.addAllDeviceName(deviceName.map { it.toProto() })
    }
    if (hasModelNumber()) {
      protoValue.setModelNumber(modelNumberElement.toProto())
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasSpecialization()) {
      protoValue.addAllSpecialization(specialization.map { it.toProto() })
    }
    if (hasVersion()) {
      protoValue.addAllVersion(version.map { it.toProto() })
    }
    if (hasSafety()) {
      protoValue.addAllSafety(safety.map { it.toProto() })
    }
    if (hasShelfLifeStorage()) {
      protoValue.addAllShelfLifeStorage(shelfLifeStorage.map { it.toProto() })
    }
    if (hasPhysicalCharacteristics()) {
      protoValue.setPhysicalCharacteristics(physicalCharacteristics.toProto())
    }
    if (hasLanguageCode()) {
      protoValue.addAllLanguageCode(languageCode.map { it.toProto() })
    }
    if (hasCapability()) {
      protoValue.addAllCapability(capability.map { it.toProto() })
    }
    if (hasProperty()) {
      protoValue.addAllProperty(property.map { it.toProto() })
    }
    if (hasOwner()) {
      protoValue.setOwner(owner.toProto())
    }
    if (hasContact()) {
      protoValue.addAllContact(contact.map { it.toProto() })
    }
    if (hasUrl()) {
      protoValue.setUrl(urlElement.toProto())
    }
    if (hasOnlineInformation()) {
      protoValue.setOnlineInformation(onlineInformationElement.toProto())
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    if (hasQuantity()) {
      protoValue.setQuantity(quantity.toProto())
    }
    if (hasParentDevice()) {
      protoValue.setParentDevice(parentDevice.toProto())
    }
    if (hasMaterial()) {
      protoValue.addAllMaterial(material.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionUdiDeviceIdentifierComponent.toProto():
    DeviceDefinition.UdiDeviceIdentifier {
    val protoValue =
      DeviceDefinition.UdiDeviceIdentifier.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDeviceIdentifier()) {
      protoValue.setDeviceIdentifier(deviceIdentifierElement.toProto())
    }
    if (hasIssuer()) {
      protoValue.setIssuer(issuerElement.toProto())
    }
    if (hasJurisdiction()) {
      protoValue.setJurisdiction(jurisdictionElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionDeviceNameComponent.toProto():
    DeviceDefinition.DeviceName {
    val protoValue =
      DeviceDefinition.DeviceName.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.setName(nameElement.toProto())
    }
    protoValue.setType(
      DeviceDefinition.DeviceName.TypeCode.newBuilder()
        .setValue(
          DeviceNameTypeCode.Value.valueOf(
            type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionSpecializationComponent.toProto():
    DeviceDefinition.Specialization {
    val protoValue =
      DeviceDefinition.Specialization.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSystemType()) {
      protoValue.setSystemType(systemTypeElement.toProto())
    }
    if (hasVersion()) {
      protoValue.setVersion(versionElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionCapabilityComponent.toProto():
    DeviceDefinition.Capability {
    val protoValue =
      DeviceDefinition.Capability.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasDescription()) {
      protoValue.addAllDescription(description.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionPropertyComponent.toProto():
    DeviceDefinition.Property {
    val protoValue = DeviceDefinition.Property.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
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
  private fun org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionMaterialComponent.toProto():
    DeviceDefinition.Material {
    val protoValue = DeviceDefinition.Material.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSubstance()) {
      protoValue.setSubstance(substance.toProto())
    }
    if (hasAlternate()) {
      protoValue.setAlternate(alternateElement.toProto())
    }
    if (hasAllergenicIndicator()) {
      protoValue.setAllergenicIndicator(allergenicIndicatorElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun DeviceDefinition.UdiDeviceIdentifier.toHapi():
    org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionUdiDeviceIdentifierComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionUdiDeviceIdentifierComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasDeviceIdentifier()) {
      hapiValue.setDeviceIdentifierElement(deviceIdentifier.toHapi())
    }
    if (hasIssuer()) {
      hapiValue.setIssuerElement(issuer.toHapi())
    }
    if (hasJurisdiction()) {
      hapiValue.setJurisdictionElement(jurisdiction.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun DeviceDefinition.DeviceName.toHapi():
    org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionDeviceNameComponent {
    val hapiValue = org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionDeviceNameComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasName()) {
      hapiValue.setNameElement(name.toHapi())
    }
    hapiValue.setType(
      org.hl7.fhir.r4.model.DeviceDefinition.DeviceNameType.valueOf(
        type.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    return hapiValue
  }

  @JvmStatic
  private fun DeviceDefinition.Specialization.toHapi():
    org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionSpecializationComponent {
    val hapiValue = org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionSpecializationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasSystemType()) {
      hapiValue.setSystemTypeElement(systemType.toHapi())
    }
    if (hasVersion()) {
      hapiValue.setVersionElement(version.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun DeviceDefinition.Capability.toHapi():
    org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionCapabilityComponent {
    val hapiValue = org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionCapabilityComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasType()) {
      hapiValue.setType(type.toHapi())
    }
    if (descriptionCount > 0) {
      hapiValue.setDescription(descriptionList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun DeviceDefinition.Property.toHapi():
    org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionPropertyComponent {
    val hapiValue = org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionPropertyComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasType()) {
      hapiValue.setType(type.toHapi())
    }
    if (valueQuantityCount > 0) {
      hapiValue.setValueQuantity(valueQuantityList.map { it.toHapi() })
    }
    if (valueCodeCount > 0) {
      hapiValue.setValueCode(valueCodeList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun DeviceDefinition.Material.toHapi():
    org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionMaterialComponent {
    val hapiValue = org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionMaterialComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasSubstance()) {
      hapiValue.setSubstance(substance.toHapi())
    }
    if (hasAlternate()) {
      hapiValue.setAlternateElement(alternate.toHapi())
    }
    if (hasAllergenicIndicator()) {
      hapiValue.setAllergenicIndicatorElement(allergenicIndicator.toHapi())
    }
    return hapiValue
  }
}
