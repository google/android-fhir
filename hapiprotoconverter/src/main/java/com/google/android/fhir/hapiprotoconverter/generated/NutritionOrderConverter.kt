package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.RatioConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RatioConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.NutritionOrder
import com.google.fhir.r4.core.Ratio
import com.google.fhir.r4.core.RequestIntentCode
import com.google.fhir.r4.core.RequestStatusCode
import com.google.fhir.r4.core.SimpleQuantity
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.Type

public object NutritionOrderConverter {
  public
      fun NutritionOrder.EnteralFormula.Administration.RateX.nutritionOrderEnteralFormulaAdministrationRateToHapi():
      Type {
    if (this.getQuantity() != SimpleQuantity.newBuilder().defaultInstanceForType ) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getRatio() != Ratio.newBuilder().defaultInstanceForType ) {
      return (this.getRatio()).toHapi()
    }
    throw
        IllegalArgumentException("Invalid Type for NutritionOrder.enteralFormula.administration.rate[x]")
  }

  public fun Type.nutritionOrderEnteralFormulaAdministrationRateToProto():
      NutritionOrder.EnteralFormula.Administration.RateX {
    val protoValue = NutritionOrder.EnteralFormula.Administration.RateX.newBuilder()
    if (this is org.hl7.fhir.r4.model.SimpleQuantity) {
      protoValue.setQuantity(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Ratio) {
      protoValue.setRatio(this.toProto())
    }
    return protoValue.build()
  }

  public fun NutritionOrder.toHapi(): org.hl7.fhir.r4.model.NutritionOrder {
    val hapiValue = org.hl7.fhir.r4.model.NutritionOrder()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setInstantiatesCanonical(instantiatesCanonicalList.map{it.toHapi()})
    hapiValue.setInstantiatesUri(instantiatesUriList.map{it.toHapi()})
    hapiValue.setInstantiates(instantiatesList.map{it.toHapi()})
    hapiValue.setStatus(org.hl7.fhir.r4.model.NutritionOrder.NutritionOrderStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setIntent(org.hl7.fhir.r4.model.NutritionOrder.NutritiionOrderIntent.valueOf(intent.value.name.replace("_","")))
    hapiValue.setPatient(patient.toHapi())
    hapiValue.setEncounter(encounter.toHapi())
    hapiValue.setDateTimeElement(dateTime.toHapi())
    hapiValue.setOrderer(orderer.toHapi())
    hapiValue.setAllergyIntolerance(allergyIntoleranceList.map{it.toHapi()})
    hapiValue.setFoodPreferenceModifier(foodPreferenceModifierList.map{it.toHapi()})
    hapiValue.setExcludeFoodModifier(excludeFoodModifierList.map{it.toHapi()})
    hapiValue.setOralDiet(oralDiet.toHapi())
    hapiValue.setSupplement(supplementList.map{it.toHapi()})
    hapiValue.setEnteralFormula(enteralFormula.toHapi())
    hapiValue.setNote(noteList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.NutritionOrder.toProto(): NutritionOrder {
    val protoValue = NutritionOrder.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .addAllInstantiatesCanonical(instantiatesCanonical.map{it.toProto()})
    .addAllInstantiatesUri(instantiatesUri.map{it.toProto()})
    .addAllInstantiates(instantiates.map{it.toProto()})
    .setStatus(NutritionOrder.StatusCode.newBuilder().setValue(RequestStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setIntent(NutritionOrder.IntentCode.newBuilder().setValue(RequestIntentCode.Value.valueOf(intent.toCode().replace("-",
        "_").toUpperCase())).build())
    .setPatient(patient.toProto())
    .setEncounter(encounter.toProto())
    .setDateTime(dateTimeElement.toProto())
    .setOrderer(orderer.toProto())
    .addAllAllergyIntolerance(allergyIntolerance.map{it.toProto()})
    .addAllFoodPreferenceModifier(foodPreferenceModifier.map{it.toProto()})
    .addAllExcludeFoodModifier(excludeFoodModifier.map{it.toProto()})
    .setOralDiet(oralDiet.toProto())
    .addAllSupplement(supplement.map{it.toProto()})
    .setEnteralFormula(enteralFormula.toProto())
    .addAllNote(note.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.NutritionOrder.NutritionOrderOralDietComponent.toProto():
      NutritionOrder.OralDiet {
    val protoValue = NutritionOrder.OralDiet.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllType(type.map{it.toProto()})
    .addAllSchedule(schedule.map{it.toProto()})
    .addAllNutrient(nutrient.map{it.toProto()})
    .addAllTexture(texture.map{it.toProto()})
    .addAllFluidConsistencyType(fluidConsistencyType.map{it.toProto()})
    .setInstruction(instructionElement.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.NutritionOrder.NutritionOrderOralDietNutrientComponent.toProto():
      NutritionOrder.OralDiet.Nutrient {
    val protoValue = NutritionOrder.OralDiet.Nutrient.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setModifier(modifier.toProto())
    .setAmount(( amount as org.hl7.fhir.r4.model.SimpleQuantity ).toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.NutritionOrder.NutritionOrderOralDietTextureComponent.toProto():
      NutritionOrder.OralDiet.Texture {
    val protoValue = NutritionOrder.OralDiet.Texture.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setModifier(modifier.toProto())
    .setFoodType(foodType.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.NutritionOrder.NutritionOrderSupplementComponent.toProto():
      NutritionOrder.Supplement {
    val protoValue = NutritionOrder.Supplement.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setType(type.toProto())
    .setProductName(productNameElement.toProto())
    .addAllSchedule(schedule.map{it.toProto()})
    .setQuantity(( quantity as org.hl7.fhir.r4.model.SimpleQuantity ).toProto())
    .setInstruction(instructionElement.toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.NutritionOrder.NutritionOrderEnteralFormulaComponent.toProto():
      NutritionOrder.EnteralFormula {
    val protoValue = NutritionOrder.EnteralFormula.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setBaseFormulaType(baseFormulaType.toProto())
    .setBaseFormulaProductName(baseFormulaProductNameElement.toProto())
    .setAdditiveType(additiveType.toProto())
    .setAdditiveProductName(additiveProductNameElement.toProto())
    .setCaloricDensity(( caloricDensity as org.hl7.fhir.r4.model.SimpleQuantity ).toProto())
    .setRouteofAdministration(routeofAdministration.toProto())
    .addAllAdministration(administration.map{it.toProto()})
    .setMaxVolumeToDeliver(( maxVolumeToDeliver as org.hl7.fhir.r4.model.SimpleQuantity ).toProto())
    .setAdministrationInstruction(administrationInstructionElement.toProto())
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.NutritionOrder.NutritionOrderEnteralFormulaAdministrationComponent.toProto():
      NutritionOrder.EnteralFormula.Administration {
    val protoValue = NutritionOrder.EnteralFormula.Administration.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setSchedule(schedule.toProto())
    .setQuantity(( quantity as org.hl7.fhir.r4.model.SimpleQuantity ).toProto())
    .setRate(rate.nutritionOrderEnteralFormulaAdministrationRateToProto())
    .build()
    return protoValue
  }

  public fun NutritionOrder.OralDiet.toHapi():
      org.hl7.fhir.r4.model.NutritionOrder.NutritionOrderOralDietComponent {
    val hapiValue = org.hl7.fhir.r4.model.NutritionOrder.NutritionOrderOralDietComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(typeList.map{it.toHapi()})
    hapiValue.setSchedule(scheduleList.map{it.toHapi()})
    hapiValue.setNutrient(nutrientList.map{it.toHapi()})
    hapiValue.setTexture(textureList.map{it.toHapi()})
    hapiValue.setFluidConsistencyType(fluidConsistencyTypeList.map{it.toHapi()})
    hapiValue.setInstructionElement(instruction.toHapi())
    return hapiValue
  }

  public fun NutritionOrder.OralDiet.Nutrient.toHapi():
      org.hl7.fhir.r4.model.NutritionOrder.NutritionOrderOralDietNutrientComponent {
    val hapiValue = org.hl7.fhir.r4.model.NutritionOrder.NutritionOrderOralDietNutrientComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setModifier(modifier.toHapi())
    hapiValue.setAmount(amount.toHapi())
    return hapiValue
  }

  public fun NutritionOrder.OralDiet.Texture.toHapi():
      org.hl7.fhir.r4.model.NutritionOrder.NutritionOrderOralDietTextureComponent {
    val hapiValue = org.hl7.fhir.r4.model.NutritionOrder.NutritionOrderOralDietTextureComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setModifier(modifier.toHapi())
    hapiValue.setFoodType(foodType.toHapi())
    return hapiValue
  }

  public fun NutritionOrder.Supplement.toHapi():
      org.hl7.fhir.r4.model.NutritionOrder.NutritionOrderSupplementComponent {
    val hapiValue = org.hl7.fhir.r4.model.NutritionOrder.NutritionOrderSupplementComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setProductNameElement(productName.toHapi())
    hapiValue.setSchedule(scheduleList.map{it.toHapi()})
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setInstructionElement(instruction.toHapi())
    return hapiValue
  }

  public fun NutritionOrder.EnteralFormula.toHapi():
      org.hl7.fhir.r4.model.NutritionOrder.NutritionOrderEnteralFormulaComponent {
    val hapiValue = org.hl7.fhir.r4.model.NutritionOrder.NutritionOrderEnteralFormulaComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setBaseFormulaType(baseFormulaType.toHapi())
    hapiValue.setBaseFormulaProductNameElement(baseFormulaProductName.toHapi())
    hapiValue.setAdditiveType(additiveType.toHapi())
    hapiValue.setAdditiveProductNameElement(additiveProductName.toHapi())
    hapiValue.setCaloricDensity(caloricDensity.toHapi())
    hapiValue.setRouteofAdministration(routeofAdministration.toHapi())
    hapiValue.setAdministration(administrationList.map{it.toHapi()})
    hapiValue.setMaxVolumeToDeliver(maxVolumeToDeliver.toHapi())
    hapiValue.setAdministrationInstructionElement(administrationInstruction.toHapi())
    return hapiValue
  }

  public fun NutritionOrder.EnteralFormula.Administration.toHapi():
      org.hl7.fhir.r4.model.NutritionOrder.NutritionOrderEnteralFormulaAdministrationComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.NutritionOrder.NutritionOrderEnteralFormulaAdministrationComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSchedule(schedule.toHapi())
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setRate(rate.nutritionOrderEnteralFormulaAdministrationRateToHapi())
    return hapiValue
  }
}
