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
import org.hl7.fhir.r4.model.Base64BinaryType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

public object MedicationKnowledgeConverter {
  private fun MedicationKnowledge.Ingredient.ItemX.medicationKnowledgeIngredientItemToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for MedicationKnowledge.ingredient.item[x]")
  }

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

  private
      fun MedicationKnowledge.AdministrationGuidelines.IndicationX.medicationKnowledgeAdministrationGuidelinesIndicationToHapi():
      Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    throw
        IllegalArgumentException("Invalid Type for MedicationKnowledge.administrationGuidelines.indication[x]")
  }

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

  private
      fun MedicationKnowledge.AdministrationGuidelines.PatientCharacteristics.CharacteristicX.medicationKnowledgeAdministrationGuidelinesPatientCharacteristicsCharacteristicToHapi():
      Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getQuantity() != SimpleQuantity.newBuilder().defaultInstanceForType ) {
      return (this.getQuantity()).toHapi()
    }
    throw
        IllegalArgumentException("Invalid Type for MedicationKnowledge.administrationGuidelines.patientCharacteristics.characteristic[x]")
  }

  private
      fun Type.medicationKnowledgeAdministrationGuidelinesPatientCharacteristicsCharacteristicToProto():
      MedicationKnowledge.AdministrationGuidelines.PatientCharacteristics.CharacteristicX {
    val protoValue =
        MedicationKnowledge.AdministrationGuidelines.PatientCharacteristics.CharacteristicX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.SimpleQuantity) {
      protoValue.setQuantity(this.toProto())
    }
    return protoValue.build()
  }

  private
      fun MedicationKnowledge.DrugCharacteristic.ValueX.medicationKnowledgeDrugCharacteristicValueToHapi():
      Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType ) {
      return (this.getStringValue()).toHapi()
    }
    if (this.getQuantity() != SimpleQuantity.newBuilder().defaultInstanceForType ) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getBase64Binary() != Base64Binary.newBuilder().defaultInstanceForType ) {
      return (this.getBase64Binary()).toHapi()
    }
    throw
        IllegalArgumentException("Invalid Type for MedicationKnowledge.drugCharacteristic.value[x]")
  }

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

  public fun MedicationKnowledge.toHapi(): org.hl7.fhir.r4.model.MedicationKnowledge {
    val hapiValue = org.hl7.fhir.r4.model.MedicationKnowledge()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCode(code.toHapi())
    hapiValue.setStatus(org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setManufacturer(manufacturer.toHapi())
    hapiValue.setDoseForm(doseForm.toHapi())
    hapiValue.setAmount(amount.toHapi())
    hapiValue.setSynonym(synonymList.map{it.toHapi()})
    hapiValue.setRelatedMedicationKnowledge(relatedMedicationKnowledgeList.map{it.toHapi()})
    hapiValue.setAssociatedMedication(associatedMedicationList.map{it.toHapi()})
    hapiValue.setProductType(productTypeList.map{it.toHapi()})
    hapiValue.setMonograph(monographList.map{it.toHapi()})
    hapiValue.setIngredient(ingredientList.map{it.toHapi()})
    hapiValue.setPreparationInstructionElement(preparationInstruction.toHapi())
    hapiValue.setIntendedRoute(intendedRouteList.map{it.toHapi()})
    hapiValue.setCost(costList.map{it.toHapi()})
    hapiValue.setMonitoringProgram(monitoringProgramList.map{it.toHapi()})
    hapiValue.setAdministrationGuidelines(administrationGuidelinesList.map{it.toHapi()})
    hapiValue.setMedicineClassification(medicineClassificationList.map{it.toHapi()})
    hapiValue.setPackaging(packaging.toHapi())
    hapiValue.setDrugCharacteristic(drugCharacteristicList.map{it.toHapi()})
    hapiValue.setContraindication(contraindicationList.map{it.toHapi()})
    hapiValue.setRegulatory(regulatoryList.map{it.toHapi()})
    hapiValue.setKinetics(kineticsList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.MedicationKnowledge.toProto(): MedicationKnowledge {
    val protoValue = MedicationKnowledge.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setCode(code.toProto())
    .setStatus(MedicationKnowledge.StatusCode.newBuilder().setValue(MedicationKnowledgeStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setManufacturer(manufacturer.toProto())
    .setDoseForm(doseForm.toProto())
    .setAmount(( amount as org.hl7.fhir.r4.model.SimpleQuantity ).toProto())
    .addAllSynonym(synonym.map{it.toProto()})
    .addAllRelatedMedicationKnowledge(relatedMedicationKnowledge.map{it.toProto()})
    .addAllAssociatedMedication(associatedMedication.map{it.toProto()})
    .addAllProductType(productType.map{it.toProto()})
    .addAllMonograph(monograph.map{it.toProto()})
    .addAllIngredient(ingredient.map{it.toProto()})
    .setPreparationInstruction(preparationInstructionElement.toProto())
    .addAllIntendedRoute(intendedRoute.map{it.toProto()})
    .addAllCost(cost.map{it.toProto()})
    .addAllMonitoringProgram(monitoringProgram.map{it.toProto()})
    .addAllAdministrationGuidelines(administrationGuidelines.map{it.toProto()})
    .addAllMedicineClassification(medicineClassification.map{it.toProto()})
    .setPackaging(packaging.toProto())
    .addAllDrugCharacteristic(drugCharacteristic.map{it.toProto()})
    .addAllContraindication(contraindication.map{it.toProto()})
    .addAllRegulatory(regulatory.map{it.toProto()})
    .addAllKinetics(kinetics.map{it.toProto()})
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRelatedMedicationKnowledgeComponent.toProto():
      MedicationKnowledge.RelatedMedicationKnowledge {
    val protoValue = MedicationKnowledge.RelatedMedicationKnowledge.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(type.toProto())
    .addAllReference(reference.map{it.toProto()})
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMonographComponent.toProto():
      MedicationKnowledge.Monograph {
    val protoValue = MedicationKnowledge.Monograph.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(type.toProto())
    .setSource(source.toProto())
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeIngredientComponent.toProto():
      MedicationKnowledge.Ingredient {
    val protoValue = MedicationKnowledge.Ingredient.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setItem(item.medicationKnowledgeIngredientItemToProto())
    .setIsActive(isActiveElement.toProto())
    .setStrength(strength.toProto())
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeCostComponent.toProto():
      MedicationKnowledge.Cost {
    val protoValue = MedicationKnowledge.Cost.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(type.toProto())
    .setSource(sourceElement.toProto())
    .setCost(cost.toProto())
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMonitoringProgramComponent.toProto():
      MedicationKnowledge.MonitoringProgram {
    val protoValue = MedicationKnowledge.MonitoringProgram.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(type.toProto())
    .setName(nameElement.toProto())
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeAdministrationGuidelinesComponent.toProto():
      MedicationKnowledge.AdministrationGuidelines {
    val protoValue = MedicationKnowledge.AdministrationGuidelines.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllDosage(dosage.map{it.toProto()})
    .setIndication(indication.medicationKnowledgeAdministrationGuidelinesIndicationToProto())
    .addAllPatientCharacteristics(patientCharacteristics.map{it.toProto()})
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeAdministrationGuidelinesDosageComponent.toProto():
      MedicationKnowledge.AdministrationGuidelines.Dosage {
    val protoValue = MedicationKnowledge.AdministrationGuidelines.Dosage.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(type.toProto())
    .addAllDosage(dosage.map{it.toProto()})
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeAdministrationGuidelinesPatientCharacteristicsComponent.toProto():
      MedicationKnowledge.AdministrationGuidelines.PatientCharacteristics {
    val protoValue =
        MedicationKnowledge.AdministrationGuidelines.PatientCharacteristics.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setCharacteristic(characteristic.medicationKnowledgeAdministrationGuidelinesPatientCharacteristicsCharacteristicToProto())
    .addAllValue(value.map{it.toProto()})
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMedicineClassificationComponent.toProto():
      MedicationKnowledge.MedicineClassification {
    val protoValue = MedicationKnowledge.MedicineClassification.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(type.toProto())
    .addAllClassification(classification.map{it.toProto()})
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgePackagingComponent.toProto():
      MedicationKnowledge.Packaging {
    val protoValue = MedicationKnowledge.Packaging.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(type.toProto())
    .setQuantity(( quantity as org.hl7.fhir.r4.model.SimpleQuantity ).toProto())
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeDrugCharacteristicComponent.toProto():
      MedicationKnowledge.DrugCharacteristic {
    val protoValue = MedicationKnowledge.DrugCharacteristic.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(type.toProto())
    .setValue(value.medicationKnowledgeDrugCharacteristicValueToProto())
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryComponent.toProto():
      MedicationKnowledge.Regulatory {
    val protoValue = MedicationKnowledge.Regulatory.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setRegulatoryAuthority(regulatoryAuthority.toProto())
    .addAllSubstitution(substitution.map{it.toProto()})
    .addAllSchedule(schedule.map{it.toProto()})
    .setMaxDispense(maxDispense.toProto())
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatorySubstitutionComponent.toProto():
      MedicationKnowledge.Regulatory.Substitution {
    val protoValue = MedicationKnowledge.Regulatory.Substitution.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(type.toProto())
    .setAllowed(allowedElement.toProto())
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryScheduleComponent.toProto():
      MedicationKnowledge.Regulatory.Schedule {
    val protoValue = MedicationKnowledge.Regulatory.Schedule.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setSchedule(schedule.toProto())
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryMaxDispenseComponent.toProto():
      MedicationKnowledge.Regulatory.MaxDispense {
    val protoValue = MedicationKnowledge.Regulatory.MaxDispense.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setQuantity(( quantity as org.hl7.fhir.r4.model.SimpleQuantity ).toProto())
    .setPeriod(period.toProto())
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeKineticsComponent.toProto():
      MedicationKnowledge.Kinetics {
    val protoValue = MedicationKnowledge.Kinetics.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllAreaUnderCurve(areaUnderCurve.map{( it as org.hl7.fhir.r4.model.SimpleQuantity 
        ).toProto()})
    .addAllLethalDose50(lethalDose50.map{( it as org.hl7.fhir.r4.model.SimpleQuantity  ).toProto()})
    .setHalfLifePeriod(halfLifePeriod.toProto())
    .build()
    return protoValue
  }

  private fun MedicationKnowledge.RelatedMedicationKnowledge.toHapi():
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRelatedMedicationKnowledgeComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRelatedMedicationKnowledgeComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setReference(referenceList.map{it.toHapi()})
    return hapiValue
  }

  private fun MedicationKnowledge.Monograph.toHapi():
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMonographComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMonographComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setSource(source.toHapi())
    return hapiValue
  }

  private fun MedicationKnowledge.Ingredient.toHapi():
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeIngredientComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeIngredientComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setItem(item.medicationKnowledgeIngredientItemToHapi())
    hapiValue.setIsActiveElement(isActive.toHapi())
    hapiValue.setStrength(strength.toHapi())
    return hapiValue
  }

  private fun MedicationKnowledge.Cost.toHapi():
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeCostComponent {
    val hapiValue = org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeCostComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setSourceElement(source.toHapi())
    hapiValue.setCost(cost.toHapi())
    return hapiValue
  }

  private fun MedicationKnowledge.MonitoringProgram.toHapi():
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMonitoringProgramComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMonitoringProgramComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setNameElement(name.toHapi())
    return hapiValue
  }

  private fun MedicationKnowledge.AdministrationGuidelines.toHapi():
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeAdministrationGuidelinesComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeAdministrationGuidelinesComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setDosage(dosageList.map{it.toHapi()})
    hapiValue.setIndication(indication.medicationKnowledgeAdministrationGuidelinesIndicationToHapi())
    hapiValue.setPatientCharacteristics(patientCharacteristicsList.map{it.toHapi()})
    return hapiValue
  }

  private fun MedicationKnowledge.AdministrationGuidelines.Dosage.toHapi():
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeAdministrationGuidelinesDosageComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeAdministrationGuidelinesDosageComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setDosage(dosageList.map{it.toHapi()})
    return hapiValue
  }

  private fun MedicationKnowledge.AdministrationGuidelines.PatientCharacteristics.toHapi():
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeAdministrationGuidelinesPatientCharacteristicsComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeAdministrationGuidelinesPatientCharacteristicsComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCharacteristic(characteristic.medicationKnowledgeAdministrationGuidelinesPatientCharacteristicsCharacteristicToHapi())
    hapiValue.setValue(valueList.map{it.toHapi()})
    return hapiValue
  }

  private fun MedicationKnowledge.MedicineClassification.toHapi():
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMedicineClassificationComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeMedicineClassificationComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setClassification(classificationList.map{it.toHapi()})
    return hapiValue
  }

  private fun MedicationKnowledge.Packaging.toHapi():
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgePackagingComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgePackagingComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setQuantity(quantity.toHapi())
    return hapiValue
  }

  private fun MedicationKnowledge.DrugCharacteristic.toHapi():
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeDrugCharacteristicComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeDrugCharacteristicComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setValue(value.medicationKnowledgeDrugCharacteristicValueToHapi())
    return hapiValue
  }

  private fun MedicationKnowledge.Regulatory.toHapi():
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setRegulatoryAuthority(regulatoryAuthority.toHapi())
    hapiValue.setSubstitution(substitutionList.map{it.toHapi()})
    hapiValue.setSchedule(scheduleList.map{it.toHapi()})
    hapiValue.setMaxDispense(maxDispense.toHapi())
    return hapiValue
  }

  private fun MedicationKnowledge.Regulatory.Substitution.toHapi():
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatorySubstitutionComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatorySubstitutionComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setAllowedElement(allowed.toHapi())
    return hapiValue
  }

  private fun MedicationKnowledge.Regulatory.Schedule.toHapi():
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryScheduleComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryScheduleComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSchedule(schedule.toHapi())
    return hapiValue
  }

  private fun MedicationKnowledge.Regulatory.MaxDispense.toHapi():
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryMaxDispenseComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeRegulatoryMaxDispenseComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setPeriod(period.toHapi())
    return hapiValue
  }

  private fun MedicationKnowledge.Kinetics.toHapi():
      org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeKineticsComponent {
    val hapiValue = org.hl7.fhir.r4.model.MedicationKnowledge.MedicationKnowledgeKineticsComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setAreaUnderCurve(areaUnderCurveList.map{it.toHapi()})
    hapiValue.setLethalDose50(lethalDose50List.map{it.toHapi()})
    hapiValue.setHalfLifePeriod(halfLifePeriod.toHapi())
    return hapiValue
  }
}
