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
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.MedicinalProduct
import com.google.fhir.r4.core.MedicinalProduct.Name
import com.google.fhir.r4.core.MedicinalProduct.SpecialDesignation
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Type

object MedicinalProductConverter {
  @JvmStatic
  private fun MedicinalProduct.SpecialDesignation.IndicationX.medicinalProductSpecialDesignationIndicationToHapi():
    Type {
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for MedicinalProduct.specialDesignation.indication[x]"
    )
  }

  @JvmStatic
  private fun Type.medicinalProductSpecialDesignationIndicationToProto():
    MedicinalProduct.SpecialDesignation.IndicationX {
    val protoValue = MedicinalProduct.SpecialDesignation.IndicationX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun MedicinalProduct.toHapi(): org.hl7.fhir.r4.model.MedicinalProduct {
    val hapiValue = org.hl7.fhir.r4.model.MedicinalProduct()
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
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasDomain()) {
      hapiValue.domain = domain.toHapi()
    }
    if (hasCombinedPharmaceuticalDoseForm()) {
      hapiValue.combinedPharmaceuticalDoseForm = combinedPharmaceuticalDoseForm.toHapi()
    }
    if (hasLegalStatusOfSupply()) {
      hapiValue.legalStatusOfSupply = legalStatusOfSupply.toHapi()
    }
    if (hasAdditionalMonitoringIndicator()) {
      hapiValue.additionalMonitoringIndicator = additionalMonitoringIndicator.toHapi()
    }
    if (specialMeasuresCount > 0) {
      hapiValue.specialMeasures = specialMeasuresList.map { it.toHapi() }
    }
    if (hasPaediatricUseIndicator()) {
      hapiValue.paediatricUseIndicator = paediatricUseIndicator.toHapi()
    }
    if (productClassificationCount > 0) {
      hapiValue.productClassification = productClassificationList.map { it.toHapi() }
    }
    if (marketingStatusCount > 0) {
      hapiValue.marketingStatus = marketingStatusList.map { it.toHapi() }
    }
    if (pharmaceuticalProductCount > 0) {
      hapiValue.pharmaceuticalProduct = pharmaceuticalProductList.map { it.toHapi() }
    }
    if (packagedMedicinalProductCount > 0) {
      hapiValue.packagedMedicinalProduct = packagedMedicinalProductList.map { it.toHapi() }
    }
    if (attachedDocumentCount > 0) {
      hapiValue.attachedDocument = attachedDocumentList.map { it.toHapi() }
    }
    if (masterFileCount > 0) {
      hapiValue.masterFile = masterFileList.map { it.toHapi() }
    }
    if (contactCount > 0) {
      hapiValue.contact = contactList.map { it.toHapi() }
    }
    if (clinicalTrialCount > 0) {
      hapiValue.clinicalTrial = clinicalTrialList.map { it.toHapi() }
    }
    if (nameCount > 0) {
      hapiValue.name = nameList.map { it.toHapi() }
    }
    if (crossReferenceCount > 0) {
      hapiValue.crossReference = crossReferenceList.map { it.toHapi() }
    }
    if (manufacturingBusinessOperationCount > 0) {
      hapiValue.manufacturingBusinessOperation =
        manufacturingBusinessOperationList.map { it.toHapi() }
    }
    if (specialDesignationCount > 0) {
      hapiValue.specialDesignation = specialDesignationList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.MedicinalProduct.toProto(): MedicinalProduct {
    val protoValue = MedicinalProduct.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasDomain()) {
      protoValue.domain = domain.toProto()
    }
    if (hasCombinedPharmaceuticalDoseForm()) {
      protoValue.combinedPharmaceuticalDoseForm = combinedPharmaceuticalDoseForm.toProto()
    }
    if (hasLegalStatusOfSupply()) {
      protoValue.legalStatusOfSupply = legalStatusOfSupply.toProto()
    }
    if (hasAdditionalMonitoringIndicator()) {
      protoValue.additionalMonitoringIndicator = additionalMonitoringIndicator.toProto()
    }
    if (hasSpecialMeasures()) {
      protoValue.addAllSpecialMeasures(specialMeasures.map { it.toProto() })
    }
    if (hasPaediatricUseIndicator()) {
      protoValue.paediatricUseIndicator = paediatricUseIndicator.toProto()
    }
    if (hasProductClassification()) {
      protoValue.addAllProductClassification(productClassification.map { it.toProto() })
    }
    if (hasMarketingStatus()) {
      protoValue.addAllMarketingStatus(marketingStatus.map { it.toProto() })
    }
    if (hasPharmaceuticalProduct()) {
      protoValue.addAllPharmaceuticalProduct(pharmaceuticalProduct.map { it.toProto() })
    }
    if (hasPackagedMedicinalProduct()) {
      protoValue.addAllPackagedMedicinalProduct(packagedMedicinalProduct.map { it.toProto() })
    }
    if (hasAttachedDocument()) {
      protoValue.addAllAttachedDocument(attachedDocument.map { it.toProto() })
    }
    if (hasMasterFile()) {
      protoValue.addAllMasterFile(masterFile.map { it.toProto() })
    }
    if (hasContact()) {
      protoValue.addAllContact(contact.map { it.toProto() })
    }
    if (hasClinicalTrial()) {
      protoValue.addAllClinicalTrial(clinicalTrial.map { it.toProto() })
    }
    if (hasName()) {
      protoValue.addAllName(name.map { it.toProto() })
    }
    if (hasCrossReference()) {
      protoValue.addAllCrossReference(crossReference.map { it.toProto() })
    }
    if (hasManufacturingBusinessOperation()) {
      protoValue.addAllManufacturingBusinessOperation(
        manufacturingBusinessOperation.map { it.toProto() }
      )
    }
    if (hasSpecialDesignation()) {
      protoValue.addAllSpecialDesignation(specialDesignation.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductNameComponent.toProto():
    MedicinalProduct.Name {
    val protoValue = MedicinalProduct.Name.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasProductName()) {
      protoValue.productName = productNameElement.toProto()
    }
    if (hasNamePart()) {
      protoValue.addAllNamePart(namePart.map { it.toProto() })
    }
    if (hasCountryLanguage()) {
      protoValue.addAllCountryLanguage(countryLanguage.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductNameNamePartComponent.toProto():
    MedicinalProduct.Name.NamePart {
    val protoValue =
      MedicinalProduct.Name.NamePart.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasPart()) {
      protoValue.part = partElement.toProto()
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductNameCountryLanguageComponent.toProto():
    MedicinalProduct.Name.CountryLanguage {
    val protoValue =
      MedicinalProduct.Name.CountryLanguage.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCountry()) {
      protoValue.country = country.toProto()
    }
    if (hasJurisdiction()) {
      protoValue.jurisdiction = jurisdiction.toProto()
    }
    if (hasLanguage()) {
      protoValue.language = language.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductManufacturingBusinessOperationComponent.toProto():
    MedicinalProduct.ManufacturingBusinessOperation {
    val protoValue =
      MedicinalProduct.ManufacturingBusinessOperation.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasOperationType()) {
      protoValue.operationType = operationType.toProto()
    }
    if (hasAuthorisationReferenceNumber()) {
      protoValue.authorisationReferenceNumber = authorisationReferenceNumber.toProto()
    }
    if (hasEffectiveDate()) {
      protoValue.effectiveDate = effectiveDateElement.toProto()
    }
    if (hasConfidentialityIndicator()) {
      protoValue.confidentialityIndicator = confidentialityIndicator.toProto()
    }
    if (hasManufacturer()) {
      protoValue.addAllManufacturer(manufacturer.map { it.toProto() })
    }
    if (hasRegulator()) {
      protoValue.regulator = regulator.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductSpecialDesignationComponent.toProto():
    MedicinalProduct.SpecialDesignation {
    val protoValue =
      MedicinalProduct.SpecialDesignation.newBuilder().setId(String.newBuilder().setValue(id))
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
    if (hasIntendedUse()) {
      protoValue.intendedUse = intendedUse.toProto()
    }
    if (hasIndication()) {
      protoValue.indication = indication.medicinalProductSpecialDesignationIndicationToProto()
    }
    if (hasStatus()) {
      protoValue.status = status.toProto()
    }
    if (hasDate()) {
      protoValue.date = dateElement.toProto()
    }
    if (hasSpecies()) {
      protoValue.species = species.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun MedicinalProduct.Name.toHapi():
    org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductNameComponent {
    val hapiValue = org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductNameComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasProductName()) {
      hapiValue.productNameElement = productName.toHapi()
    }
    if (namePartCount > 0) {
      hapiValue.namePart = namePartList.map { it.toHapi() }
    }
    if (countryLanguageCount > 0) {
      hapiValue.countryLanguage = countryLanguageList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  private fun MedicinalProduct.Name.NamePart.toHapi():
    org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductNameNamePartComponent {
    val hapiValue = org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductNameNamePartComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasPart()) {
      hapiValue.partElement = part.toHapi()
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun MedicinalProduct.Name.CountryLanguage.toHapi():
    org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductNameCountryLanguageComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductNameCountryLanguageComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCountry()) {
      hapiValue.country = country.toHapi()
    }
    if (hasJurisdiction()) {
      hapiValue.jurisdiction = jurisdiction.toHapi()
    }
    if (hasLanguage()) {
      hapiValue.language = language.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun MedicinalProduct.ManufacturingBusinessOperation.toHapi():
    org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductManufacturingBusinessOperationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProduct
        .MedicinalProductManufacturingBusinessOperationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasOperationType()) {
      hapiValue.operationType = operationType.toHapi()
    }
    if (hasAuthorisationReferenceNumber()) {
      hapiValue.authorisationReferenceNumber = authorisationReferenceNumber.toHapi()
    }
    if (hasEffectiveDate()) {
      hapiValue.effectiveDateElement = effectiveDate.toHapi()
    }
    if (hasConfidentialityIndicator()) {
      hapiValue.confidentialityIndicator = confidentialityIndicator.toHapi()
    }
    if (manufacturerCount > 0) {
      hapiValue.manufacturer = manufacturerList.map { it.toHapi() }
    }
    if (hasRegulator()) {
      hapiValue.regulator = regulator.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun MedicinalProduct.SpecialDesignation.toHapi():
    org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductSpecialDesignationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductSpecialDesignationComponent()
    hapiValue.id = id.value
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
    if (hasIntendedUse()) {
      hapiValue.intendedUse = intendedUse.toHapi()
    }
    if (hasIndication()) {
      hapiValue.indication = indication.medicinalProductSpecialDesignationIndicationToHapi()
    }
    if (hasStatus()) {
      hapiValue.status = status.toHapi()
    }
    if (hasDate()) {
      hapiValue.dateElement = date.toHapi()
    }
    if (hasSpecies()) {
      hapiValue.species = species.toHapi()
    }
    return hapiValue
  }
}
