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

import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
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

public object MedicinalProductConverter {
  @JvmStatic
  private fun MedicinalProduct.SpecialDesignation.IndicationX.medicinalProductSpecialDesignationIndicationToHapi():
    Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
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
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun MedicinalProduct.toHapi(): org.hl7.fhir.r4.model.MedicinalProduct {
    val hapiValue = org.hl7.fhir.r4.model.MedicinalProduct()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setType(type.toHapi())
    hapiValue.setDomain(domain.toHapi())
    hapiValue.setCombinedPharmaceuticalDoseForm(combinedPharmaceuticalDoseForm.toHapi())
    hapiValue.setLegalStatusOfSupply(legalStatusOfSupply.toHapi())
    hapiValue.setAdditionalMonitoringIndicator(additionalMonitoringIndicator.toHapi())
    hapiValue.setSpecialMeasures(specialMeasuresList.map { it.toHapi() })
    hapiValue.setPaediatricUseIndicator(paediatricUseIndicator.toHapi())
    hapiValue.setProductClassification(productClassificationList.map { it.toHapi() })
    hapiValue.setMarketingStatus(marketingStatusList.map { it.toHapi() })
    hapiValue.setPharmaceuticalProduct(pharmaceuticalProductList.map { it.toHapi() })
    hapiValue.setPackagedMedicinalProduct(packagedMedicinalProductList.map { it.toHapi() })
    hapiValue.setAttachedDocument(attachedDocumentList.map { it.toHapi() })
    hapiValue.setMasterFile(masterFileList.map { it.toHapi() })
    hapiValue.setContact(contactList.map { it.toHapi() })
    hapiValue.setClinicalTrial(clinicalTrialList.map { it.toHapi() })
    hapiValue.setName(nameList.map { it.toHapi() })
    hapiValue.setCrossReference(crossReferenceList.map { it.toHapi() })
    hapiValue.setManufacturingBusinessOperation(
      manufacturingBusinessOperationList.map { it.toHapi() }
    )
    hapiValue.setSpecialDesignation(specialDesignationList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.MedicinalProduct.toProto(): MedicinalProduct {
    val protoValue =
      MedicinalProduct.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setLanguage(languageElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setType(type.toProto())
        .setDomain(domain.toProto())
        .setCombinedPharmaceuticalDoseForm(combinedPharmaceuticalDoseForm.toProto())
        .setLegalStatusOfSupply(legalStatusOfSupply.toProto())
        .setAdditionalMonitoringIndicator(additionalMonitoringIndicator.toProto())
        .addAllSpecialMeasures(specialMeasures.map { it.toProto() })
        .setPaediatricUseIndicator(paediatricUseIndicator.toProto())
        .addAllProductClassification(productClassification.map { it.toProto() })
        .addAllMarketingStatus(marketingStatus.map { it.toProto() })
        .addAllPharmaceuticalProduct(pharmaceuticalProduct.map { it.toProto() })
        .addAllPackagedMedicinalProduct(packagedMedicinalProduct.map { it.toProto() })
        .addAllAttachedDocument(attachedDocument.map { it.toProto() })
        .addAllMasterFile(masterFile.map { it.toProto() })
        .addAllContact(contact.map { it.toProto() })
        .addAllClinicalTrial(clinicalTrial.map { it.toProto() })
        .addAllName(name.map { it.toProto() })
        .addAllCrossReference(crossReference.map { it.toProto() })
        .addAllManufacturingBusinessOperation(manufacturingBusinessOperation.map { it.toProto() })
        .addAllSpecialDesignation(specialDesignation.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductNameComponent.toProto():
    MedicinalProduct.Name {
    val protoValue =
      MedicinalProduct.Name.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setProductName(productNameElement.toProto())
        .addAllNamePart(namePart.map { it.toProto() })
        .addAllCountryLanguage(countryLanguage.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductNameNamePartComponent.toProto():
    MedicinalProduct.Name.NamePart {
    val protoValue =
      MedicinalProduct.Name.NamePart.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setPart(partElement.toProto())
        .setType(type.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductNameCountryLanguageComponent.toProto():
    MedicinalProduct.Name.CountryLanguage {
    val protoValue =
      MedicinalProduct.Name.CountryLanguage.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCountry(country.toProto())
        .setJurisdiction(jurisdiction.toProto())
        .setLanguage(language.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductManufacturingBusinessOperationComponent.toProto():
    MedicinalProduct.ManufacturingBusinessOperation {
    val protoValue =
      MedicinalProduct.ManufacturingBusinessOperation.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setOperationType(operationType.toProto())
        .setAuthorisationReferenceNumber(authorisationReferenceNumber.toProto())
        .setEffectiveDate(effectiveDateElement.toProto())
        .setConfidentialityIndicator(confidentialityIndicator.toProto())
        .addAllManufacturer(manufacturer.map { it.toProto() })
        .setRegulator(regulator.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductSpecialDesignationComponent.toProto():
    MedicinalProduct.SpecialDesignation {
    val protoValue =
      MedicinalProduct.SpecialDesignation.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setType(type.toProto())
        .setIntendedUse(intendedUse.toProto())
        .setIndication(indication.medicinalProductSpecialDesignationIndicationToProto())
        .setStatus(status.toProto())
        .setDate(dateElement.toProto())
        .setSpecies(species.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun MedicinalProduct.Name.toHapi():
    org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductNameComponent {
    val hapiValue = org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductNameComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setProductNameElement(productName.toHapi())
    hapiValue.setNamePart(namePartList.map { it.toHapi() })
    hapiValue.setCountryLanguage(countryLanguageList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  private fun MedicinalProduct.Name.NamePart.toHapi():
    org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductNameNamePartComponent {
    val hapiValue = org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductNameNamePartComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setPartElement(part.toHapi())
    hapiValue.setType(type.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun MedicinalProduct.Name.CountryLanguage.toHapi():
    org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductNameCountryLanguageComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductNameCountryLanguageComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCountry(country.toHapi())
    hapiValue.setJurisdiction(jurisdiction.toHapi())
    hapiValue.setLanguage(language.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun MedicinalProduct.ManufacturingBusinessOperation.toHapi():
    org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductManufacturingBusinessOperationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProduct
        .MedicinalProductManufacturingBusinessOperationComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setOperationType(operationType.toHapi())
    hapiValue.setAuthorisationReferenceNumber(authorisationReferenceNumber.toHapi())
    hapiValue.setEffectiveDateElement(effectiveDate.toHapi())
    hapiValue.setConfidentialityIndicator(confidentialityIndicator.toHapi())
    hapiValue.setManufacturer(manufacturerList.map { it.toHapi() })
    hapiValue.setRegulator(regulator.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun MedicinalProduct.SpecialDesignation.toHapi():
    org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductSpecialDesignationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProduct.MedicinalProductSpecialDesignationComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setType(type.toHapi())
    hapiValue.setIntendedUse(intendedUse.toHapi())
    hapiValue.setIndication(indication.medicinalProductSpecialDesignationIndicationToHapi())
    hapiValue.setStatus(status.toHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setSpecies(species.toHapi())
    return hapiValue
  }
}
