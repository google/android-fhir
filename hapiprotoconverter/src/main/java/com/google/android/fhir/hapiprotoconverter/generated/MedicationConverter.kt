package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Medication
import com.google.fhir.r4.core.Medication.Ingredient
import com.google.fhir.r4.core.MedicationStatusCode
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.Type

public object MedicationConverter {
  private fun Medication.Ingredient.ItemX.medicationIngredientItemToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Medication.ingredient.item[x]")
  }

  private fun Type.medicationIngredientItemToProto(): Medication.Ingredient.ItemX {
    val protoValue = Medication.Ingredient.ItemX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  public fun Medication.toHapi(): org.hl7.fhir.r4.model.Medication {
    val hapiValue = org.hl7.fhir.r4.model.Medication()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setCode(code.toHapi())
    hapiValue.setStatus(org.hl7.fhir.r4.model.Medication.MedicationStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setManufacturer(manufacturer.toHapi())
    hapiValue.setForm(form.toHapi())
    hapiValue.setAmount(amount.toHapi())
    hapiValue.setIngredient(ingredientList.map{it.toHapi()})
    hapiValue.setBatch(batch.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Medication.toProto(): Medication {
    val protoValue = Medication.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setCode(code.toProto())
    .setStatus(Medication.StatusCode.newBuilder().setValue(MedicationStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setManufacturer(manufacturer.toProto())
    .setForm(form.toProto())
    .setAmount(amount.toProto())
    .addAllIngredient(ingredient.map{it.toProto()})
    .setBatch(batch.toProto())
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Medication.MedicationIngredientComponent.toProto():
      Medication.Ingredient {
    val protoValue = Medication.Ingredient.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setItem(item.medicationIngredientItemToProto())
    .setIsActive(isActiveElement.toProto())
    .setStrength(strength.toProto())
    .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Medication.MedicationBatchComponent.toProto():
      Medication.Batch {
    val protoValue = Medication.Batch.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setLotNumber(lotNumberElement.toProto())
    .setExpirationDate(expirationDateElement.toProto())
    .build()
    return protoValue
  }

  private fun Medication.Ingredient.toHapi():
      org.hl7.fhir.r4.model.Medication.MedicationIngredientComponent {
    val hapiValue = org.hl7.fhir.r4.model.Medication.MedicationIngredientComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setItem(item.medicationIngredientItemToHapi())
    hapiValue.setIsActiveElement(isActive.toHapi())
    hapiValue.setStrength(strength.toHapi())
    return hapiValue
  }

  private fun Medication.Batch.toHapi(): org.hl7.fhir.r4.model.Medication.MedicationBatchComponent {
    val hapiValue = org.hl7.fhir.r4.model.Medication.MedicationBatchComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setLotNumberElement(lotNumber.toHapi())
    hapiValue.setExpirationDateElement(expirationDate.toHapi())
    return hapiValue
  }
}
