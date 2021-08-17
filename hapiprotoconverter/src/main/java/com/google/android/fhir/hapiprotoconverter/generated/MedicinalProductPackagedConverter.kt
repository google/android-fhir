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

object MedicinalProductPackagedConverter {
  fun MedicinalProductPackaged.toHapi(): org.hl7.fhir.r4.model.MedicinalProductPackaged {
    val hapiValue = org.hl7.fhir.r4.model.MedicinalProductPackaged()
    if (hasId()) {
      hapiValue.id = id.value
    }
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
    if (subjectCount > 0) {
      hapiValue.subject = subjectList.map { it.toHapi() }
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (hasLegalStatusOfSupply()) {
      hapiValue.legalStatusOfSupply = legalStatusOfSupply.toHapi()
    }
    if (marketingStatusCount > 0) {
      hapiValue.marketingStatus = marketingStatusList.map { it.toHapi() }
    }
    if (hasMarketingAuthorization()) {
      hapiValue.marketingAuthorization = marketingAuthorization.toHapi()
    }
    if (manufacturerCount > 0) {
      hapiValue.manufacturer = manufacturerList.map { it.toHapi() }
    }
    if (batchIdentifierCount > 0) {
      hapiValue.batchIdentifier = batchIdentifierList.map { it.toHapi() }
    }
    if (packageItemCount > 0) {
      hapiValue.packageItem = packageItemList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.MedicinalProductPackaged.toProto(): MedicinalProductPackaged {
    val protoValue = MedicinalProductPackaged.newBuilder()
    if (hasId()) {
      protoValue.setId(Id.newBuilder().setValue(id))
    }
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
    if (hasSubject()) {
      protoValue.addAllSubject(subject.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasLegalStatusOfSupply()) {
      protoValue.legalStatusOfSupply = legalStatusOfSupply.toProto()
    }
    if (hasMarketingStatus()) {
      protoValue.addAllMarketingStatus(marketingStatus.map { it.toProto() })
    }
    if (hasMarketingAuthorization()) {
      protoValue.marketingAuthorization = marketingAuthorization.toProto()
    }
    if (hasManufacturer()) {
      protoValue.addAllManufacturer(manufacturer.map { it.toProto() })
    }
    if (hasBatchIdentifier()) {
      protoValue.addAllBatchIdentifier(batchIdentifier.map { it.toProto() })
    }
    if (hasPackageItem()) {
      protoValue.addAllPackageItem(packageItem.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MedicinalProductPackaged.MedicinalProductPackagedBatchIdentifierComponent.toProto():
    MedicinalProductPackaged.BatchIdentifier {
    val protoValue = MedicinalProductPackaged.BatchIdentifier.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasOuterPackaging()) {
      protoValue.outerPackaging = outerPackaging.toProto()
    }
    if (hasImmediatePackaging()) {
      protoValue.immediatePackaging = immediatePackaging.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MedicinalProductPackaged.MedicinalProductPackagedPackageItemComponent.toProto():
    MedicinalProductPackaged.PackageItem {
    val protoValue = MedicinalProductPackaged.PackageItem.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
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
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasQuantity()) {
      protoValue.quantity = quantity.toProto()
    }
    if (hasMaterial()) {
      protoValue.addAllMaterial(material.map { it.toProto() })
    }
    if (hasAlternateMaterial()) {
      protoValue.addAllAlternateMaterial(alternateMaterial.map { it.toProto() })
    }
    if (hasDevice()) {
      protoValue.addAllDevice(device.map { it.toProto() })
    }
    if (hasManufacturedItem()) {
      protoValue.addAllManufacturedItem(manufacturedItem.map { it.toProto() })
    }
    if (hasPhysicalCharacteristics()) {
      protoValue.physicalCharacteristics = physicalCharacteristics.toProto()
    }
    if (hasOtherCharacteristics()) {
      protoValue.addAllOtherCharacteristics(otherCharacteristics.map { it.toProto() })
    }
    if (hasShelfLifeStorage()) {
      protoValue.addAllShelfLifeStorage(shelfLifeStorage.map { it.toProto() })
    }
    if (hasManufacturer()) {
      protoValue.addAllManufacturer(manufacturer.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun MedicinalProductPackaged.BatchIdentifier.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductPackaged.MedicinalProductPackagedBatchIdentifierComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductPackaged
        .MedicinalProductPackagedBatchIdentifierComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasOuterPackaging()) {
      hapiValue.outerPackaging = outerPackaging.toHapi()
    }
    if (hasImmediatePackaging()) {
      hapiValue.immediatePackaging = immediatePackaging.toHapi()
    }
    return hapiValue
  }

  private fun MedicinalProductPackaged.PackageItem.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductPackaged.MedicinalProductPackagedPackageItemComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductPackaged.MedicinalProductPackagedPackageItemComponent()
    if (hasId()) {
      hapiValue.id = id.value
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
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasQuantity()) {
      hapiValue.quantity = quantity.toHapi()
    }
    if (materialCount > 0) {
      hapiValue.material = materialList.map { it.toHapi() }
    }
    if (alternateMaterialCount > 0) {
      hapiValue.alternateMaterial = alternateMaterialList.map { it.toHapi() }
    }
    if (deviceCount > 0) {
      hapiValue.device = deviceList.map { it.toHapi() }
    }
    if (manufacturedItemCount > 0) {
      hapiValue.manufacturedItem = manufacturedItemList.map { it.toHapi() }
    }
    if (hasPhysicalCharacteristics()) {
      hapiValue.physicalCharacteristics = physicalCharacteristics.toHapi()
    }
    if (otherCharacteristicsCount > 0) {
      hapiValue.otherCharacteristics = otherCharacteristicsList.map { it.toHapi() }
    }
    if (shelfLifeStorageCount > 0) {
      hapiValue.shelfLifeStorage = shelfLifeStorageList.map { it.toHapi() }
    }
    if (manufacturerCount > 0) {
      hapiValue.manufacturer = manufacturerList.map { it.toHapi() }
    }
    return hapiValue
  }
}
