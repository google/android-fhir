package com.google.android.fhir.hapiprotoconverter.generated

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
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.FHIRSubstanceStatusCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.Substance
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.SimpleQuantity
import org.hl7.fhir.r4.model.Type

public object SubstanceConverter {
  public fun Substance.Ingredient.SubstanceX.substanceIngredientSubstanceToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Substance.ingredient.substance[x]")
  }

  public fun Type.substanceIngredientSubstanceToProto(): Substance.Ingredient.SubstanceX {
    val protoValue = Substance.Ingredient.SubstanceX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  public fun Substance.toHapi(): org.hl7.fhir.r4.model.Substance {
    val hapiValue = org.hl7.fhir.r4.model.Substance()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setStatus(org.hl7.fhir.r4.model.Substance.FHIRSubstanceStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setCategory(categoryList.map{it.toHapi()})
    hapiValue.setCode(code.toHapi())
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setInstance(instanceList.map{it.toHapi()})
    hapiValue.setIngredient(ingredientList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Substance.toProto(): Substance {
    val protoValue = Substance.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setStatus(Substance.StatusCode.newBuilder().setValue(FHIRSubstanceStatusCode.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .addAllCategory(category.map{it.toProto()})
    .setCode(code.toProto())
    .setDescription(descriptionElement.toProto())
    .addAllInstance(instance.map{it.toProto()})
    .addAllIngredient(ingredient.map{it.toProto()})
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Substance.SubstanceInstanceComponent.toProto():
      Substance.Instance {
    val protoValue = Substance.Instance.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setIdentifier(identifier.toProto())
    .setExpiry(expiryElement.toProto())
    .setQuantity(( quantity as SimpleQuantity ).toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Substance.SubstanceIngredientComponent.toProto():
      Substance.Ingredient {
    val protoValue = Substance.Ingredient.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setQuantity(quantity.toProto())
    .setSubstance(substance.substanceIngredientSubstanceToProto())
    .build()
    return protoValue
  }

  public fun Substance.Instance.toHapi():
      org.hl7.fhir.r4.model.Substance.SubstanceInstanceComponent {
    val hapiValue = org.hl7.fhir.r4.model.Substance.SubstanceInstanceComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifier.toHapi())
    hapiValue.setExpiryElement(expiry.toHapi())
    hapiValue.setQuantity(quantity.toHapi())
    return hapiValue
  }

  public fun Substance.Ingredient.toHapi():
      org.hl7.fhir.r4.model.Substance.SubstanceIngredientComponent {
    val hapiValue = org.hl7.fhir.r4.model.Substance.SubstanceIngredientComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setSubstance(substance.substanceIngredientSubstanceToHapi())
    return hapiValue
  }
}
