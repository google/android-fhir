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

import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MarketingStatusConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MarketingStatusConverter.toProto
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
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.MedicinalProductPackaged
import com.google.fhir.r4.core.String

public object MedicinalProductPackagedConverter {
  public fun MedicinalProductPackaged.toHapi(): org.hl7.fhir.r4.model.MedicinalProductPackaged {
    val hapiValue = org.hl7.fhir.r4.model.MedicinalProductPackaged()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setSubject(subjectList.map { it.toHapi() })
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setLegalStatusOfSupply(legalStatusOfSupply.toHapi())
    hapiValue.setMarketingStatus(marketingStatusList.map { it.toHapi() })
    hapiValue.setMarketingAuthorization(marketingAuthorization.toHapi())
    hapiValue.setManufacturer(manufacturerList.map { it.toHapi() })
    hapiValue.setBatchIdentifier(batchIdentifierList.map { it.toHapi() })
    hapiValue.setPackageItem(packageItemList.map { it.toHapi() })
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.MedicinalProductPackaged.toProto(): MedicinalProductPackaged {
    val protoValue =
      MedicinalProductPackaged.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .addAllSubject(subject.map { it.toProto() })
        .setDescription(descriptionElement.toProto())
        .setLegalStatusOfSupply(legalStatusOfSupply.toProto())
        .addAllMarketingStatus(marketingStatus.map { it.toProto() })
        .setMarketingAuthorization(marketingAuthorization.toProto())
        .addAllManufacturer(manufacturer.map { it.toProto() })
        .addAllBatchIdentifier(batchIdentifier.map { it.toProto() })
        .addAllPackageItem(packageItem.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.MedicinalProductPackaged.MedicinalProductPackagedBatchIdentifierComponent.toProto():
    MedicinalProductPackaged.BatchIdentifier {
    val protoValue =
      MedicinalProductPackaged.BatchIdentifier.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setOuterPackaging(outerPackaging.toProto())
        .setImmediatePackaging(immediatePackaging.toProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.MedicinalProductPackaged.MedicinalProductPackagedPackageItemComponent.toProto():
    MedicinalProductPackaged.PackageItem {
    val protoValue =
      MedicinalProductPackaged.PackageItem.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setType(type.toProto())
        .setQuantity(quantity.toProto())
        .addAllMaterial(material.map { it.toProto() })
        .addAllAlternateMaterial(alternateMaterial.map { it.toProto() })
        .addAllDevice(device.map { it.toProto() })
        .addAllManufacturedItem(manufacturedItem.map { it.toProto() })
        .setPhysicalCharacteristics(physicalCharacteristics.toProto())
        .addAllOtherCharacteristics(otherCharacteristics.map { it.toProto() })
        .addAllShelfLifeStorage(shelfLifeStorage.map { it.toProto() })
        .addAllManufacturer(manufacturer.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun MedicinalProductPackaged.BatchIdentifier.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductPackaged.MedicinalProductPackagedBatchIdentifierComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductPackaged
        .MedicinalProductPackagedBatchIdentifierComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setOuterPackaging(outerPackaging.toHapi())
    hapiValue.setImmediatePackaging(immediatePackaging.toHapi())
    return hapiValue
  }

  private fun MedicinalProductPackaged.PackageItem.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductPackaged.MedicinalProductPackagedPackageItemComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductPackaged.MedicinalProductPackagedPackageItemComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setType(type.toHapi())
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setMaterial(materialList.map { it.toHapi() })
    hapiValue.setAlternateMaterial(alternateMaterialList.map { it.toHapi() })
    hapiValue.setDevice(deviceList.map { it.toHapi() })
    hapiValue.setManufacturedItem(manufacturedItemList.map { it.toHapi() })
    hapiValue.setPhysicalCharacteristics(physicalCharacteristics.toHapi())
    hapiValue.setOtherCharacteristics(otherCharacteristicsList.map { it.toHapi() })
    hapiValue.setShelfLifeStorage(shelfLifeStorageList.map { it.toHapi() })
    hapiValue.setManufacturer(manufacturerList.map { it.toHapi() })
    return hapiValue
  }
}
