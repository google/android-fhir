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

import com.google.android.fhir.hapiprotoconverter.generated.Base64BinaryConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.Base64BinaryConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DosageConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DosageConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DurationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DurationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MoneyConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MoneyConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RatioConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RatioConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Base64Binary
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.MedicationKnowledge
import com.google.fhir.r4.core.MedicationKnowledge.AdministrationGuidelines
import com.google.fhir.r4.core.MedicationKnowledge.AdministrationGuidelines.PatientCharacteristics
import com.google.fhir.r4.core.MedicationKnowledge.DrugCharacteristic
import com.google.fhir.r4.core.MedicationKnowledge.Ingredient
import com.google.fhir.r4.core.MedicationKnowledge.Regulatory
import com.google.fhir.r4.core.MedicationKnowledgeStatusCode
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.SimpleQuantity
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Base64BinaryType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

public object MedicationKnowledgeConverter {
  @JvmStatic
  private fun MedicationKnowledge.Ingredient.ItemX.medicationKnowledgeIngredientItemToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for MedicationKnowledge.ingredient.item[x]")
  }

  @JvmStatic
  private fun Type.medicationKnowledgeIngredientItemToProto():
    MedicationKnowledge.Ingredient.ItemX {
    val protoValue = MedicationKnowledge.Ingredient.ItemX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun MedicationKnowledge.AdministrationGuidelines.IndicationX.medicationKnowledgeAdministrationGuidelinesIndicationToHapi():
    Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for MedicationKnowledge.administrationGuidelines.indication[x]"
    )
  }

  @JvmStatic
  private fun Type.medicationKnowledgeAdministrationGuidelinesIndicationToProto():
    MedicationKnowledge.AdministrationGuidelines.IndicationX {
    val protoValue = MedicationKnowledge.AdministrationGuidelines.IndicationX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun MedicationKnowledge.AdministrationGuidelines.PatientCharacteristics.CharacteristicX.medicationKnowledgeAdministrationGuidelinesPatientCharacteristicsCharacteristicToHapi():
    Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getQuantity() != SimpleQuantity.newBuilder().defaultInstanceForType) {
      return (this.getQuantity()).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for MedicationKnowledge.administrationGuidelines.patientCharacteristics.characteristic[x]"
    )
  }

  @JvmStatic
  private fun Type.medicationKnowledgeAdministrationGuidelinesPatientCharacteristicsCharacteristicToProto():
    MedicationKnowledge.AdministrationGuidelines.PatientCharacteristics.CharacteristicX {
    val protoValue =
      MedicationKnowledge.AdministrationGuidelines.PatientCharacteristics.CharacteristicX
        .newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.SimpleQuantity) {
      protoValue.setQuantity(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun MedicationKnowledge.DrugCharacteristic.ValueX.medicationKnowledgeDrugCharacteristicValueToHapi():
    Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    if (this.getQuantity() != SimpleQuantity.newBuilder().defaultInstanceForType) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getBase64Binary() != Base64Binary.newBuilder().defaultInstanceForType) {
      return (this.getBase64Binary()).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for MedicationKnowledge.drugCharacteristic.value[x]"
    )
  }

  @JvmStatic
  private fun Type.medicationKnowledgeDrugCharacteristicValueToProto():
    MedicationKnowledge.DrugCharacteristic.ValueX {
    val protoValue = MedicationKnowledge.DrugCharacteristic.ValueX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.SimpleQuantity) {
      protoValue.setQuantity(this.toProto())
    }
    if (this is Base64BinaryType) {
      protoValue.setBase64Binary(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun MedicationKnowledge.toHapi(): org.hl7.fhir.r4.model.MedicationKnowledge {
    val hapiValue = org.hl7.fhir.r4.model.MedicationKnowledge()
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
    if (hasCode()) {
      hapiValue.setCode(code.toHapi())
    }
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasManufacturer()) {
      hapiValue.setManufacturer(manufacturer.toHapi())
    }
    if (hasDoseForm()) {
      hapiValue.setDoseForm(doseForm.toHapi())
    }
    if (hasAmount()) {
      hapiValue.setAmount(amount.toHapi())
    }
    if (synonymCount > 0) {
      hapiValue.setSynonym(synonymList.map { it.toHapi() })
    }
    if (relatedMedicationKnowledgeCount > 0) {
      hapiValue.setRelatedMedicationKnowledge(relatedMedicationKnowledgeList.map { it.toHapi() })
    }
    if (associatedMedicationCount > 0) {
      hapiValue.setAssociatedMedication(associatedMedicationList.map { it.toHapi() })
    }
    if (productTypeCount > 0) {
      hapiValue.setProductType(productTypeList.map { it.toHapi() })
    }
    if (monographCount > 0) {
      hapiValue.setMonograph(monographList.map { it.toHapi() })
    }
    if (ingredientCount > 0) {
      hapiValue.setIngredient(ingredientList.map { it.toHapi() })
    }
    if (hasPreparationInstruction()) {
      hapiValue.setPreparationInstructionElement(preparationInstruction.toHapi())
    }
    if (intendedRouteCount > 0) {
      hapiValue.setIntendedRoute(intendedRouteList.map { it.toHapi() })
    }
    if (costCount > 0) {
      hapiValue.setCost(costList.map { it.toHapi() })
    }
    if (monitoringProgramCount > 0) {
      hapiValue.setMonitoringProgram(monitoringProgramList.map { it.toHapi() })
    }
    if (administrationGuidelinesCount > 0) {
      hapiValue.setAdministrationGuidelines(administrationGuidelinesList.map { it.toHapi() })
    }
    if (medicineClassificationCount > 0) {
      hapiValue.setMedicineClassification(medicineClassificationList.map { it.toHapi() })
    }
    if (hasPackaging()) {
      hapiValue.setPackaging(packaging.toHapi())
    }
    if (drugCharacteristicCount > 0) {
      hapiValue.setDrugCharacteristic(drugCharacteristicList.map { it.toHapi() })
    }
    if (contraindicationCount > 0) {
      hapiValue.setContraindication(contraindicationList.map { it.toHapi() })
    }
    if (regulatoryCount > 0) {
      hapiValue.setRegulatory(regulatoryList.map { it.toHapi() })
    }
    if (kineticsCount > 0) {
      hapiValue.setKinetics(kineticsList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.MedicationKnowledge.toProto(): MedicationKnowledge {
    val protoValue = MedicationKnowledge.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasCode()) {
      protoValue.setCode(code.toProto())
    }
    protoValue.setStatus(
      MedicationKnowledge.StatusCode.newBuilder()
        .setValue(
          MedicationKnowledgeStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasManufacturer()) {
      protoValue.setManufacturer(manufacturer.toProto())
    }
    if (hasDoseForm()) {
      protoValue.setDoseForm(doseForm.toProto())
    }
    if (hasAmount()) {
      protoValue.setAmount((amount as org.hl7.fhir.r4.model.SimpleQuantity).toProto())
    }
    if (hasSynonym()) {
      protoValue.addAllSynonym(synonym.map { it.toProto() })
    }
    if (hasRelatedMedicationKnowledge()) {
      protoValue.addAllRelatedMedicationKnowledge(relatedMedicationKnowledge.map { it.toProto() })
    }
    if (hasAssociatedMedication()) {
      protoValue.addAllAssociatedMedication(associatedMedication.map { it.toProto() })
    }
    if (hasProductType()) {
      protoValue.addAllProductType(productType.map { it.toProto() })
    }
    if (hasMonograph()) {
      protoValue.addAllMonograph(monograph.map { it.toProto() })
    }
    if (hasIngredient()) {
      protoValue.addAllIngredient(ingredient.map { it.toProto() })
    }
    if (hasPreparationInstruction()) {
      protoValue.setPreparationInstruction(preparationInstructionElement.toProto())
    }
    if (hasIntendedRoute()) {
      protoValue.addAllIntendedRoute(intendedRoute.map { it.toProto() })
    }
    if (hasCost()) {
      protoValue.addAllCost(cost.map { it.toProto() })
    }
    if (hasMonitoringProgram()) {
      protoValue.addAllMonitoringProgram(monitoringProgram.map { it.toProto() })
    }
    if (hasAdministrationGuidelines()) {
      protoValue.addAllAdministrationGuidelines(administrationGuidelines.map { it.toProto() })
    }
    if (hasMedicineClassification()) {
      protoValue.addAllMedicineClassification(medicineClassification.map { it.toProto() })
    }
    if (hasPackaging()) {
      protoValue.setPackaging(packaging.toProto())
    }
    if (hasDrugCharacteristic()) {
      protoValue.addAllDrugCharacteristic(drugCharacteristic.map { it.toProto() })
    }
    if (hasContraindication()) {
      protoValue.addAllContraindication(contraindication.map { it.toProto() })
    }
    if (hasRegulatory()) {
      protoValue.addAllRegulatory(regulatory.map { it.toProto() })
    }
    if (hasKinetics()) {
      protoValue.addAllKinetics(kinetics.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRelatedMedicationKnowledgeComponent.toProto():
    MedicationKnowledge.RelatedMedicationKnowledge {
    val protoValue =
      MedicationKnowledge.RelatedMedicationKnowledge.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasReference()) {
      protoValue.addAllReference(reference.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMonographComponent.toProto():
    MedicationKnowledge.Monograph {
    val protoValue =
      MedicationKnowledge.Monograph.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasSource()) {
      protoValue.setSource(source.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeIngredientComponent.toProto():
    MedicationKnowledge.Ingredient {
    val protoValue =
      MedicationKnowledge.Ingredient.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasItem()) {
      protoValue.setItem(item.medicationKnowledgeIngredientItemToProto())
    }
    if (hasIsActive()) {
      protoValue.setIsActive(isActiveElement.toProto())
    }
    if (hasStrength()) {
      protoValue.setStrength(strength.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeCostComponent.toProto():
    MedicationKnowledge.Cost {
    val protoValue = MedicationKnowledge.Cost.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasSource()) {
      protoValue.setSource(sourceElement.toProto())
    }
    if (hasCost()) {
      protoValue.setCost(cost.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMonitoringProgramComponent.toProto():
    MedicationKnowledge.MonitoringProgram {
    val protoValue =
      MedicationKnowledge.MonitoringProgram.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasName()) {
      protoValue.setName(nameElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeAdministrationGuidelinesComponent.toProto():
    MedicationKnowledge.AdministrationGuidelines {
    val protoValue =
      MedicationKnowledge.AdministrationGuidelines.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDosage()) {
      protoValue.addAllDosage(dosage.map { it.toProto() })
    }
    if (hasIndication()) {
      protoValue.setIndication(
        indication.medicationKnowledgeAdministrationGuidelinesIndicationToProto()
      )
    }
    if (hasPatientCharacteristics()) {
      protoValue.addAllPatientCharacteristics(patientCharacteristics.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeAdministrationGuidelinesDosageComponent.toProto():
    MedicationKnowledge.AdministrationGuidelines.Dosage {
    val protoValue =
      MedicationKnowledge.AdministrationGuidelines.Dosage.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasDosage()) {
      protoValue.addAllDosage(dosage.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeAdministrationGuidelinesPatientCharacteristicsComponent.toProto():
    MedicationKnowledge.AdministrationGuidelines.PatientCharacteristics {
    val protoValue =
      MedicationKnowledge.AdministrationGuidelines.PatientCharacteristics.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCharacteristic()) {
      protoValue.setCharacteristic(
        characteristic
          .medicationKnowledgeAdministrationGuidelinesPatientCharacteristicsCharacteristicToProto()
      )
    }
    if (hasValue()) {
      protoValue.addAllValue(value.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMedicineClassificationComponent.toProto():
    MedicationKnowledge.MedicineClassification {
    val protoValue =
      MedicationKnowledge.MedicineClassification.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasClassification()) {
      protoValue.addAllClassification(classification.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgePackagingComponent.toProto():
    MedicationKnowledge.Packaging {
    val protoValue =
      MedicationKnowledge.Packaging.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasQuantity()) {
      protoValue.setQuantity((quantity as org.hl7.fhir.r4.model.SimpleQuantity).toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeDrugCharacteristicComponent.toProto():
    MedicationKnowledge.DrugCharacteristic {
    val protoValue =
      MedicationKnowledge.DrugCharacteristic.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasValue()) {
      protoValue.setValue(value.medicationKnowledgeDrugCharacteristicValueToProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryComponent.toProto():
    MedicationKnowledge.Regulatory {
    val protoValue =
      MedicationKnowledge.Regulatory.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasRegulatoryAuthority()) {
      protoValue.setRegulatoryAuthority(regulatoryAuthority.toProto())
    }
    if (hasSubstitution()) {
      protoValue.addAllSubstitution(substitution.map { it.toProto() })
    }
    if (hasSchedule()) {
      protoValue.addAllSchedule(schedule.map { it.toProto() })
    }
    if (hasMaxDispense()) {
      protoValue.setMaxDispense(maxDispense.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatorySubstitutionComponent.toProto():
    MedicationKnowledge.Regulatory.Substitution {
    val protoValue =
      MedicationKnowledge.Regulatory.Substitution.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasAllowed()) {
      protoValue.setAllowed(allowedElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryScheduleComponent.toProto():
    MedicationKnowledge.Regulatory.Schedule {
    val protoValue =
      MedicationKnowledge.Regulatory.Schedule.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSchedule()) {
      protoValue.setSchedule(schedule.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryMaxDispenseComponent.toProto():
    MedicationKnowledge.Regulatory.MaxDispense {
    val protoValue =
      MedicationKnowledge.Regulatory.MaxDispense.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasQuantity()) {
      protoValue.setQuantity((quantity as org.hl7.fhir.r4.model.SimpleQuantity).toProto())
    }
    if (hasPeriod()) {
      protoValue.setPeriod(period.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeKineticsComponent.toProto():
    MedicationKnowledge.Kinetics {
    val protoValue =
      MedicationKnowledge.Kinetics.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasAreaUnderCurve()) {
      protoValue.addAllAreaUnderCurve(
        areaUnderCurve.map { (it as org.hl7.fhir.r4.model.SimpleQuantity).toProto() }
      )
    }
    if (hasLethalDose50()) {
      protoValue.addAllLethalDose50(
        lethalDose50.map { (it as org.hl7.fhir.r4.model.SimpleQuantity).toProto() }
      )
    }
    if (hasHalfLifePeriod()) {
      protoValue.setHalfLifePeriod(halfLifePeriod.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun MedicationKnowledge.RelatedMedicationKnowledge.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRelatedMedicationKnowledgeComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge
        .MedicationKnowledgeRelatedMedicationKnowledgeComponent()
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
    if (referenceCount > 0) {
      hapiValue.setReference(referenceList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun MedicationKnowledge.Monograph.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMonographComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMonographComponent()
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
    if (hasSource()) {
      hapiValue.setSource(source.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun MedicationKnowledge.Ingredient.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeIngredientComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeIngredientComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasItem()) {
      hapiValue.setItem(item.medicationKnowledgeIngredientItemToHapi())
    }
    if (hasIsActive()) {
      hapiValue.setIsActiveElement(isActive.toHapi())
    }
    if (hasStrength()) {
      hapiValue.setStrength(strength.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun MedicationKnowledge.Cost.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeCostComponent {
    val hapiValue = org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeCostComponent()
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
    if (hasSource()) {
      hapiValue.setSourceElement(source.toHapi())
    }
    if (hasCost()) {
      hapiValue.setCost(cost.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun MedicationKnowledge.MonitoringProgram.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMonitoringProgramComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMonitoringProgramComponent()
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
    if (hasName()) {
      hapiValue.setNameElement(name.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun MedicationKnowledge.AdministrationGuidelines.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeAdministrationGuidelinesComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge
        .MedicationKnowledgeAdministrationGuidelinesComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (dosageCount > 0) {
      hapiValue.setDosage(dosageList.map { it.toHapi() })
    }
    if (hasIndication()) {
      hapiValue.setIndication(
        indication.medicationKnowledgeAdministrationGuidelinesIndicationToHapi()
      )
    }
    if (patientCharacteristicsCount > 0) {
      hapiValue.setPatientCharacteristics(patientCharacteristicsList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun MedicationKnowledge.AdministrationGuidelines.Dosage.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeAdministrationGuidelinesDosageComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge
        .MedicationKnowledgeAdministrationGuidelinesDosageComponent()
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
    if (dosageCount > 0) {
      hapiValue.setDosage(dosageList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun MedicationKnowledge.AdministrationGuidelines.PatientCharacteristics.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeAdministrationGuidelinesPatientCharacteristicsComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge
        .MedicationKnowledgeAdministrationGuidelinesPatientCharacteristicsComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasCharacteristic()) {
      hapiValue.setCharacteristic(
        characteristic
          .medicationKnowledgeAdministrationGuidelinesPatientCharacteristicsCharacteristicToHapi()
      )
    }
    if (valueCount > 0) {
      hapiValue.setValue(valueList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun MedicationKnowledge.MedicineClassification.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMedicineClassificationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMedicineClassificationComponent()
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
    if (classificationCount > 0) {
      hapiValue.setClassification(classificationList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun MedicationKnowledge.Packaging.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgePackagingComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgePackagingComponent()
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
    if (hasQuantity()) {
      hapiValue.setQuantity(quantity.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun MedicationKnowledge.DrugCharacteristic.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeDrugCharacteristicComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeDrugCharacteristicComponent()
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
    if (hasValue()) {
      hapiValue.setValue(value.medicationKnowledgeDrugCharacteristicValueToHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun MedicationKnowledge.Regulatory.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasRegulatoryAuthority()) {
      hapiValue.setRegulatoryAuthority(regulatoryAuthority.toHapi())
    }
    if (substitutionCount > 0) {
      hapiValue.setSubstitution(substitutionList.map { it.toHapi() })
    }
    if (scheduleCount > 0) {
      hapiValue.setSchedule(scheduleList.map { it.toHapi() })
    }
    if (hasMaxDispense()) {
      hapiValue.setMaxDispense(maxDispense.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun MedicationKnowledge.Regulatory.Substitution.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatorySubstitutionComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatorySubstitutionComponent()
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
    if (hasAllowed()) {
      hapiValue.setAllowedElement(allowed.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun MedicationKnowledge.Regulatory.Schedule.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryScheduleComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryScheduleComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasSchedule()) {
      hapiValue.setSchedule(schedule.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun MedicationKnowledge.Regulatory.MaxDispense.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryMaxDispenseComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryMaxDispenseComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasQuantity()) {
      hapiValue.setQuantity(quantity.toHapi())
    }
    if (hasPeriod()) {
      hapiValue.setPeriod(period.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun MedicationKnowledge.Kinetics.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeKineticsComponent {
    val hapiValue = org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeKineticsComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (areaUnderCurveCount > 0) {
      hapiValue.setAreaUnderCurve(areaUnderCurveList.map { it.toHapi() })
    }
    if (lethalDose50Count > 0) {
      hapiValue.setLethalDose50(lethalDose50List.map { it.toHapi() })
    }
    if (hasHalfLifePeriod()) {
      hapiValue.setHalfLifePeriod(halfLifePeriod.toHapi())
    }
    return hapiValue
  }
}
