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
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.MedicationKnowledge
import com.google.fhir.r4.core.MedicationKnowledge.AdministrationGuidelines
import com.google.fhir.r4.core.MedicationKnowledge.AdministrationGuidelines.PatientCharacteristics
import com.google.fhir.r4.core.MedicationKnowledge.DrugCharacteristic
import com.google.fhir.r4.core.MedicationKnowledge.Ingredient
import com.google.fhir.r4.core.MedicationKnowledge.Regulatory
import com.google.fhir.r4.core.MedicationKnowledgeStatusCode
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.Base64BinaryType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.SimpleQuantity
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

object MedicationKnowledgeConverter {
  private fun MedicationKnowledge.Ingredient.ItemX.medicationKnowledgeIngredientItemToHapi(): Type {
    if (hasCodeableConcept()) {
      return (this.codeableConcept).toHapi()
    }
    if (hasReference()) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for MedicationKnowledge.ingredient.item[x]")
  }

  private fun Type.medicationKnowledgeIngredientItemToProto():
    MedicationKnowledge.Ingredient.ItemX {
    val protoValue = MedicationKnowledge.Ingredient.ItemX.newBuilder()
    if (this is CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  private fun MedicationKnowledge.AdministrationGuidelines.IndicationX.medicationKnowledgeAdministrationGuidelinesIndicationToHapi():
    Type {
    if (hasCodeableConcept()) {
      return (this.codeableConcept).toHapi()
    }
    if (hasReference()) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for MedicationKnowledge.administrationGuidelines.indication[x]"
    )
  }

  private fun Type.medicationKnowledgeAdministrationGuidelinesIndicationToProto():
    MedicationKnowledge.AdministrationGuidelines.IndicationX {
    val protoValue = MedicationKnowledge.AdministrationGuidelines.IndicationX.newBuilder()
    if (this is CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  private fun MedicationKnowledge.AdministrationGuidelines.PatientCharacteristics.CharacteristicX.medicationKnowledgeAdministrationGuidelinesPatientCharacteristicsCharacteristicToHapi():
    Type {
    if (hasCodeableConcept()) {
      return (this.codeableConcept).toHapi()
    }
    if (hasQuantity()) {
      return (this.quantity).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for MedicationKnowledge.administrationGuidelines.patientCharacteristics.characteristic[x]"
    )
  }

  private fun Type.medicationKnowledgeAdministrationGuidelinesPatientCharacteristicsCharacteristicToProto():
    MedicationKnowledge.AdministrationGuidelines.PatientCharacteristics.CharacteristicX {
    val protoValue =
      MedicationKnowledge.AdministrationGuidelines.PatientCharacteristics.CharacteristicX
        .newBuilder()
    if (this is CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is SimpleQuantity) {
      protoValue.quantity = this.toProto()
    }
    return protoValue.build()
  }

  private fun MedicationKnowledge.DrugCharacteristic.ValueX.medicationKnowledgeDrugCharacteristicValueToHapi():
    Type {
    if (hasCodeableConcept()) {
      return (this.codeableConcept).toHapi()
    }
    if (hasStringValue()) {
      return (this.stringValue).toHapi()
    }
    if (hasQuantity()) {
      return (this.quantity).toHapi()
    }
    if (hasBase64Binary()) {
      return (this.base64Binary).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for MedicationKnowledge.drugCharacteristic.value[x]"
    )
  }

  private fun Type.medicationKnowledgeDrugCharacteristicValueToProto():
    MedicationKnowledge.DrugCharacteristic.ValueX {
    val protoValue = MedicationKnowledge.DrugCharacteristic.ValueX.newBuilder()
    if (this is CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is StringType) {
      protoValue.stringValue = this.toProto()
    }
    if (this is SimpleQuantity) {
      protoValue.quantity = this.toProto()
    }
    if (this is Base64BinaryType) {
      protoValue.base64Binary = this.toProto()
    }
    return protoValue.build()
  }

  fun MedicationKnowledge.toHapi(): org.hl7.fhir.r4.model.MedicationKnowledge {
    val hapiValue = org.hl7.fhir.r4.model.MedicationKnowledge()
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
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (hasStatus()) {
      hapiValue.status =
        org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasManufacturer()) {
      hapiValue.manufacturer = manufacturer.toHapi()
    }
    if (hasDoseForm()) {
      hapiValue.doseForm = doseForm.toHapi()
    }
    if (hasAmount()) {
      hapiValue.amount = amount.toHapi()
    }
    if (synonymCount > 0) {
      hapiValue.synonym = synonymList.map { it.toHapi() }
    }
    if (relatedMedicationKnowledgeCount > 0) {
      hapiValue.relatedMedicationKnowledge = relatedMedicationKnowledgeList.map { it.toHapi() }
    }
    if (associatedMedicationCount > 0) {
      hapiValue.associatedMedication = associatedMedicationList.map { it.toHapi() }
    }
    if (productTypeCount > 0) {
      hapiValue.productType = productTypeList.map { it.toHapi() }
    }
    if (monographCount > 0) {
      hapiValue.monograph = monographList.map { it.toHapi() }
    }
    if (ingredientCount > 0) {
      hapiValue.ingredient = ingredientList.map { it.toHapi() }
    }
    if (hasPreparationInstruction()) {
      hapiValue.preparationInstructionElement = preparationInstruction.toHapi()
    }
    if (intendedRouteCount > 0) {
      hapiValue.intendedRoute = intendedRouteList.map { it.toHapi() }
    }
    if (costCount > 0) {
      hapiValue.cost = costList.map { it.toHapi() }
    }
    if (monitoringProgramCount > 0) {
      hapiValue.monitoringProgram = monitoringProgramList.map { it.toHapi() }
    }
    if (administrationGuidelinesCount > 0) {
      hapiValue.administrationGuidelines = administrationGuidelinesList.map { it.toHapi() }
    }
    if (medicineClassificationCount > 0) {
      hapiValue.medicineClassification = medicineClassificationList.map { it.toHapi() }
    }
    if (hasPackaging()) {
      hapiValue.packaging = packaging.toHapi()
    }
    if (drugCharacteristicCount > 0) {
      hapiValue.drugCharacteristic = drugCharacteristicList.map { it.toHapi() }
    }
    if (contraindicationCount > 0) {
      hapiValue.contraindication = contraindicationList.map { it.toHapi() }
    }
    if (regulatoryCount > 0) {
      hapiValue.regulatory = regulatoryList.map { it.toHapi() }
    }
    if (kineticsCount > 0) {
      hapiValue.kinetics = kineticsList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.MedicationKnowledge.toProto(): MedicationKnowledge {
    val protoValue = MedicationKnowledge.newBuilder()
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
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    if (hasStatus()) {
      protoValue.status =
        MedicationKnowledge.StatusCode.newBuilder()
          .setValue(
            MedicationKnowledgeStatusCode.Value.valueOf(
              status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasManufacturer()) {
      protoValue.manufacturer = manufacturer.toProto()
    }
    if (hasDoseForm()) {
      protoValue.doseForm = doseForm.toProto()
    }
    if (hasAmount()) {
      protoValue.amount = (amount as SimpleQuantity).toProto()
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
      protoValue.preparationInstruction = preparationInstructionElement.toProto()
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
      protoValue.packaging = packaging.toProto()
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

  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRelatedMedicationKnowledgeComponent.toProto():
    MedicationKnowledge.RelatedMedicationKnowledge {
    val protoValue = MedicationKnowledge.RelatedMedicationKnowledge.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasReference()) {
      protoValue.addAllReference(reference.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMonographComponent.toProto():
    MedicationKnowledge.Monograph {
    val protoValue = MedicationKnowledge.Monograph.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasSource()) {
      protoValue.source = source.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeIngredientComponent.toProto():
    MedicationKnowledge.Ingredient {
    val protoValue = MedicationKnowledge.Ingredient.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasItem()) {
      protoValue.item = item.medicationKnowledgeIngredientItemToProto()
    }
    if (hasIsActive()) {
      protoValue.isActive = isActiveElement.toProto()
    }
    if (hasStrength()) {
      protoValue.strength = strength.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeCostComponent.toProto():
    MedicationKnowledge.Cost {
    val protoValue = MedicationKnowledge.Cost.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasSource()) {
      protoValue.source = sourceElement.toProto()
    }
    if (hasCost()) {
      protoValue.cost = cost.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMonitoringProgramComponent.toProto():
    MedicationKnowledge.MonitoringProgram {
    val protoValue = MedicationKnowledge.MonitoringProgram.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasName()) {
      protoValue.name = nameElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeAdministrationGuidelinesComponent.toProto():
    MedicationKnowledge.AdministrationGuidelines {
    val protoValue = MedicationKnowledge.AdministrationGuidelines.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
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
      protoValue.indication =
        indication.medicationKnowledgeAdministrationGuidelinesIndicationToProto()
    }
    if (hasPatientCharacteristics()) {
      protoValue.addAllPatientCharacteristics(patientCharacteristics.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeAdministrationGuidelinesDosageComponent.toProto():
    MedicationKnowledge.AdministrationGuidelines.Dosage {
    val protoValue = MedicationKnowledge.AdministrationGuidelines.Dosage.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasDosage()) {
      protoValue.addAllDosage(dosage.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeAdministrationGuidelinesPatientCharacteristicsComponent.toProto():
    MedicationKnowledge.AdministrationGuidelines.PatientCharacteristics {
    val protoValue =
      MedicationKnowledge.AdministrationGuidelines.PatientCharacteristics.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCharacteristic()) {
      protoValue.characteristic =
        characteristic
          .medicationKnowledgeAdministrationGuidelinesPatientCharacteristicsCharacteristicToProto()
    }
    if (hasValue()) {
      protoValue.addAllValue(value.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMedicineClassificationComponent.toProto():
    MedicationKnowledge.MedicineClassification {
    val protoValue = MedicationKnowledge.MedicineClassification.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasClassification()) {
      protoValue.addAllClassification(classification.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgePackagingComponent.toProto():
    MedicationKnowledge.Packaging {
    val protoValue = MedicationKnowledge.Packaging.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasQuantity()) {
      protoValue.quantity = (quantity as SimpleQuantity).toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeDrugCharacteristicComponent.toProto():
    MedicationKnowledge.DrugCharacteristic {
    val protoValue = MedicationKnowledge.DrugCharacteristic.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasValue()) {
      protoValue.value = value.medicationKnowledgeDrugCharacteristicValueToProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryComponent.toProto():
    MedicationKnowledge.Regulatory {
    val protoValue = MedicationKnowledge.Regulatory.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasRegulatoryAuthority()) {
      protoValue.regulatoryAuthority = regulatoryAuthority.toProto()
    }
    if (hasSubstitution()) {
      protoValue.addAllSubstitution(substitution.map { it.toProto() })
    }
    if (hasSchedule()) {
      protoValue.addAllSchedule(schedule.map { it.toProto() })
    }
    if (hasMaxDispense()) {
      protoValue.maxDispense = maxDispense.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatorySubstitutionComponent.toProto():
    MedicationKnowledge.Regulatory.Substitution {
    val protoValue = MedicationKnowledge.Regulatory.Substitution.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasAllowed()) {
      protoValue.allowed = allowedElement.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryScheduleComponent.toProto():
    MedicationKnowledge.Regulatory.Schedule {
    val protoValue = MedicationKnowledge.Regulatory.Schedule.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSchedule()) {
      protoValue.schedule = schedule.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryMaxDispenseComponent.toProto():
    MedicationKnowledge.Regulatory.MaxDispense {
    val protoValue = MedicationKnowledge.Regulatory.MaxDispense.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasQuantity()) {
      protoValue.quantity = (quantity as SimpleQuantity).toProto()
    }
    if (hasPeriod()) {
      protoValue.period = period.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeKineticsComponent.toProto():
    MedicationKnowledge.Kinetics {
    val protoValue = MedicationKnowledge.Kinetics.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasAreaUnderCurve()) {
      protoValue.addAllAreaUnderCurve(areaUnderCurve.map { (it as SimpleQuantity).toProto() })
    }
    if (hasLethalDose50()) {
      protoValue.addAllLethalDose50(lethalDose50.map { (it as SimpleQuantity).toProto() })
    }
    if (hasHalfLifePeriod()) {
      protoValue.halfLifePeriod = halfLifePeriod.toProto()
    }
    return protoValue.build()
  }

  private fun MedicationKnowledge.RelatedMedicationKnowledge.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRelatedMedicationKnowledgeComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge
        .MedicationKnowledgeRelatedMedicationKnowledgeComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (referenceCount > 0) {
      hapiValue.reference = referenceList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun MedicationKnowledge.Monograph.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMonographComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMonographComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasSource()) {
      hapiValue.source = source.toHapi()
    }
    return hapiValue
  }

  private fun MedicationKnowledge.Ingredient.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeIngredientComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeIngredientComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasItem()) {
      hapiValue.item = item.medicationKnowledgeIngredientItemToHapi()
    }
    if (hasIsActive()) {
      hapiValue.isActiveElement = isActive.toHapi()
    }
    if (hasStrength()) {
      hapiValue.strength = strength.toHapi()
    }
    return hapiValue
  }

  private fun MedicationKnowledge.Cost.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeCostComponent {
    val hapiValue = org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeCostComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasSource()) {
      hapiValue.sourceElement = source.toHapi()
    }
    if (hasCost()) {
      hapiValue.cost = cost.toHapi()
    }
    return hapiValue
  }

  private fun MedicationKnowledge.MonitoringProgram.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMonitoringProgramComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMonitoringProgramComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasName()) {
      hapiValue.nameElement = name.toHapi()
    }
    return hapiValue
  }

  private fun MedicationKnowledge.AdministrationGuidelines.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeAdministrationGuidelinesComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge
        .MedicationKnowledgeAdministrationGuidelinesComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (dosageCount > 0) {
      hapiValue.dosage = dosageList.map { it.toHapi() }
    }
    if (hasIndication()) {
      hapiValue.indication =
        indication.medicationKnowledgeAdministrationGuidelinesIndicationToHapi()
    }
    if (patientCharacteristicsCount > 0) {
      hapiValue.patientCharacteristics = patientCharacteristicsList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun MedicationKnowledge.AdministrationGuidelines.Dosage.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeAdministrationGuidelinesDosageComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge
        .MedicationKnowledgeAdministrationGuidelinesDosageComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (dosageCount > 0) {
      hapiValue.dosage = dosageList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun MedicationKnowledge.AdministrationGuidelines.PatientCharacteristics.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeAdministrationGuidelinesPatientCharacteristicsComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge
        .MedicationKnowledgeAdministrationGuidelinesPatientCharacteristicsComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCharacteristic()) {
      hapiValue.characteristic =
        characteristic
          .medicationKnowledgeAdministrationGuidelinesPatientCharacteristicsCharacteristicToHapi()
    }
    if (valueCount > 0) {
      hapiValue.value = valueList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun MedicationKnowledge.MedicineClassification.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMedicineClassificationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMedicineClassificationComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (classificationCount > 0) {
      hapiValue.classification = classificationList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun MedicationKnowledge.Packaging.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgePackagingComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgePackagingComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasQuantity()) {
      hapiValue.quantity = quantity.toHapi()
    }
    return hapiValue
  }

  private fun MedicationKnowledge.DrugCharacteristic.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeDrugCharacteristicComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeDrugCharacteristicComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasValue()) {
      hapiValue.value = value.medicationKnowledgeDrugCharacteristicValueToHapi()
    }
    return hapiValue
  }

  private fun MedicationKnowledge.Regulatory.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasRegulatoryAuthority()) {
      hapiValue.regulatoryAuthority = regulatoryAuthority.toHapi()
    }
    if (substitutionCount > 0) {
      hapiValue.substitution = substitutionList.map { it.toHapi() }
    }
    if (scheduleCount > 0) {
      hapiValue.schedule = scheduleList.map { it.toHapi() }
    }
    if (hasMaxDispense()) {
      hapiValue.maxDispense = maxDispense.toHapi()
    }
    return hapiValue
  }

  private fun MedicationKnowledge.Regulatory.Substitution.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatorySubstitutionComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatorySubstitutionComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasAllowed()) {
      hapiValue.allowedElement = allowed.toHapi()
    }
    return hapiValue
  }

  private fun MedicationKnowledge.Regulatory.Schedule.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryScheduleComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryScheduleComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasSchedule()) {
      hapiValue.schedule = schedule.toHapi()
    }
    return hapiValue
  }

  private fun MedicationKnowledge.Regulatory.MaxDispense.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryMaxDispenseComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryMaxDispenseComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasQuantity()) {
      hapiValue.quantity = quantity.toHapi()
    }
    if (hasPeriod()) {
      hapiValue.period = period.toHapi()
    }
    return hapiValue
  }

  private fun MedicationKnowledge.Kinetics.toHapi():
    org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeKineticsComponent {
    val hapiValue = org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeKineticsComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (areaUnderCurveCount > 0) {
      hapiValue.areaUnderCurve = areaUnderCurveList.map { it.toHapi() }
    }
    if (lethalDose50Count > 0) {
      hapiValue.lethalDose50 = lethalDose50List.map { it.toHapi() }
    }
    if (hasHalfLifePeriod()) {
      hapiValue.halfLifePeriod = halfLifePeriod.toHapi()
    }
    return hapiValue
  }
}
