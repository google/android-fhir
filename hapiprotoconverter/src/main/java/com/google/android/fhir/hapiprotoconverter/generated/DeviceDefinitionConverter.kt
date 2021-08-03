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
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setUdiDeviceIdentifier(udiDeviceIdentifierList.map { it.toHapi() })
    hapiValue.setManufacturer(manufacturer.deviceDefinitionManufacturerToHapi())
    hapiValue.setDeviceName(deviceNameList.map { it.toHapi() })
    hapiValue.setModelNumberElement(modelNumber.toHapi())
    hapiValue.setType(type.toHapi())
    hapiValue.setSpecialization(specializationList.map { it.toHapi() })
    hapiValue.setVersion(versionList.map { it.toHapi() })
    hapiValue.setSafety(safetyList.map { it.toHapi() })
    hapiValue.setShelfLifeStorage(shelfLifeStorageList.map { it.toHapi() })
    hapiValue.setPhysicalCharacteristics(physicalCharacteristics.toHapi())
    hapiValue.setLanguageCode(languageCodeList.map { it.toHapi() })
    hapiValue.setCapability(capabilityList.map { it.toHapi() })
    hapiValue.setProperty(propertyList.map { it.toHapi() })
    hapiValue.setOwner(owner.toHapi())
    hapiValue.setContact(contactList.map { it.toHapi() })
    hapiValue.setUrlElement(url.toHapi())
    hapiValue.setOnlineInformationElement(onlineInformation.toHapi())
    hapiValue.setNote(noteList.map { it.toHapi() })
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setParentDevice(parentDevice.toHapi())
    hapiValue.setMaterial(materialList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.DeviceDefinition.toProto(): DeviceDefinition {
    val protoValue =
      DeviceDefinition.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setLanguage(languageElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .addAllUdiDeviceIdentifier(udiDeviceIdentifier.map { it.toProto() })
        .setManufacturer(manufacturer.deviceDefinitionManufacturerToProto())
        .addAllDeviceName(deviceName.map { it.toProto() })
        .setModelNumber(modelNumberElement.toProto())
        .setType(type.toProto())
        .addAllSpecialization(specialization.map { it.toProto() })
        .addAllVersion(version.map { it.toProto() })
        .addAllSafety(safety.map { it.toProto() })
        .addAllShelfLifeStorage(shelfLifeStorage.map { it.toProto() })
        .setPhysicalCharacteristics(physicalCharacteristics.toProto())
        .addAllLanguageCode(languageCode.map { it.toProto() })
        .addAllCapability(capability.map { it.toProto() })
        .addAllProperty(property.map { it.toProto() })
        .setOwner(owner.toProto())
        .addAllContact(contact.map { it.toProto() })
        .setUrl(urlElement.toProto())
        .setOnlineInformation(onlineInformationElement.toProto())
        .addAllNote(note.map { it.toProto() })
        .setQuantity(quantity.toProto())
        .setParentDevice(parentDevice.toProto())
        .addAllMaterial(material.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionUdiDeviceIdentifierComponent.toProto():
    DeviceDefinition.UdiDeviceIdentifier {
    val protoValue =
      DeviceDefinition.UdiDeviceIdentifier.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setDeviceIdentifier(deviceIdentifierElement.toProto())
        .setIssuer(issuerElement.toProto())
        .setJurisdiction(jurisdictionElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionDeviceNameComponent.toProto():
    DeviceDefinition.DeviceName {
    val protoValue =
      DeviceDefinition.DeviceName.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setName(nameElement.toProto())
        .setType(
          DeviceDefinition.DeviceName.TypeCode.newBuilder()
            .setValue(
              DeviceNameTypeCode.Value.valueOf(type.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionSpecializationComponent.toProto():
    DeviceDefinition.Specialization {
    val protoValue =
      DeviceDefinition.Specialization.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setSystemType(systemTypeElement.toProto())
        .setVersion(versionElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionCapabilityComponent.toProto():
    DeviceDefinition.Capability {
    val protoValue =
      DeviceDefinition.Capability.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setType(type.toProto())
        .addAllDescription(description.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionPropertyComponent.toProto():
    DeviceDefinition.Property {
    val protoValue =
      DeviceDefinition.Property.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setType(type.toProto())
        .addAllValueQuantity(valueQuantity.map { it.toProto() })
        .addAllValueCode(valueCode.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionMaterialComponent.toProto():
    DeviceDefinition.Material {
    val protoValue =
      DeviceDefinition.Material.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setSubstance(substance.toProto())
        .setAlternate(alternateElement.toProto())
        .setAllergenicIndicator(allergenicIndicatorElement.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun DeviceDefinition.UdiDeviceIdentifier.toHapi():
    org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionUdiDeviceIdentifierComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionUdiDeviceIdentifierComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setDeviceIdentifierElement(deviceIdentifier.toHapi())
    hapiValue.setIssuerElement(issuer.toHapi())
    hapiValue.setJurisdictionElement(jurisdiction.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun DeviceDefinition.DeviceName.toHapi():
    org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionDeviceNameComponent {
    val hapiValue = org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionDeviceNameComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setType(
      org.hl7.fhir.r4.model.DeviceDefinition.DeviceNameType.valueOf(
        type.value.name.replace("_", "")
      )
    )
    return hapiValue
  }

  @JvmStatic
  private fun DeviceDefinition.Specialization.toHapi():
    org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionSpecializationComponent {
    val hapiValue = org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionSpecializationComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setSystemTypeElement(systemType.toHapi())
    hapiValue.setVersionElement(version.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun DeviceDefinition.Capability.toHapi():
    org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionCapabilityComponent {
    val hapiValue = org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionCapabilityComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setType(type.toHapi())
    hapiValue.setDescription(descriptionList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun DeviceDefinition.Property.toHapi():
    org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionPropertyComponent {
    val hapiValue = org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionPropertyComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setType(type.toHapi())
    hapiValue.setValueQuantity(valueQuantityList.map { it.toHapi() })
    hapiValue.setValueCode(valueCodeList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun DeviceDefinition.Material.toHapi():
    org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionMaterialComponent {
    val hapiValue = org.hl7.fhir.r4.model.DeviceDefinition.DeviceDefinitionMaterialComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setSubstance(substance.toHapi())
    hapiValue.setAlternateElement(alternate.toHapi())
    hapiValue.setAllergenicIndicatorElement(allergenicIndicator.toHapi())
    return hapiValue
  }
}
