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

import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
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
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.MedicinalProductIngredient
import com.google.fhir.r4.core.MedicinalProductIngredient.SpecifiedSubstance
import com.google.fhir.r4.core.MedicinalProductIngredient.SpecifiedSubstance.Strength
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic

public object MedicinalProductIngredientConverter {
  @JvmStatic
  public fun MedicinalProductIngredient.toHapi(): org.hl7.fhir.r4.model.MedicinalProductIngredient {
    val hapiValue = org.hl7.fhir.r4.model.MedicinalProductIngredient()
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
    if (hasIdentifier()) {
      hapiValue.setIdentifier(identifier.toHapi())
    }
    if (hasRole()) {
      hapiValue.setRole(role.toHapi())
    }
    if (hasAllergenicIndicator()) {
      hapiValue.setAllergenicIndicatorElement(allergenicIndicator.toHapi())
    }
    if (manufacturerCount > 0) {
      hapiValue.setManufacturer(manufacturerList.map { it.toHapi() })
    }
    if (specifiedSubstanceCount > 0) {
      hapiValue.setSpecifiedSubstance(specifiedSubstanceList.map { it.toHapi() })
    }
    if (hasSubstance()) {
      hapiValue.setSubstance(substance.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.MedicinalProductIngredient.toProto():
    MedicinalProductIngredient {
    val protoValue = MedicinalProductIngredient.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasIdentifier()) {
      protoValue.setIdentifier(identifier.toProto())
    }
    if (hasRole()) {
      protoValue.setRole(role.toProto())
    }
    if (hasAllergenicIndicator()) {
      protoValue.setAllergenicIndicator(allergenicIndicatorElement.toProto())
    }
    if (hasManufacturer()) {
      protoValue.addAllManufacturer(manufacturer.map { it.toProto() })
    }
    if (hasSpecifiedSubstance()) {
      protoValue.addAllSpecifiedSubstance(specifiedSubstance.map { it.toProto() })
    }
    if (hasSubstance()) {
      protoValue.setSubstance(substance.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicinalProductIngredient.MedicinalProductIngredientSpecifiedSubstanceComponent.toProto():
    MedicinalProductIngredient.SpecifiedSubstance {
    val protoValue =
      MedicinalProductIngredient.SpecifiedSubstance.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.setCode(code.toProto())
    }
    if (hasGroup()) {
      protoValue.setGroup(group.toProto())
    }
    if (hasConfidentiality()) {
      protoValue.setConfidentiality(confidentiality.toProto())
    }
    if (hasStrength()) {
      protoValue.addAllStrength(strength.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicinalProductIngredient.MedicinalProductIngredientSpecifiedSubstanceStrengthComponent.toProto():
    MedicinalProductIngredient.SpecifiedSubstance.Strength {
    val protoValue =
      MedicinalProductIngredient.SpecifiedSubstance.Strength.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasPresentation()) {
      protoValue.setPresentation(presentation.toProto())
    }
    if (hasPresentationLowLimit()) {
      protoValue.setPresentationLowLimit(presentationLowLimit.toProto())
    }
    if (hasConcentration()) {
      protoValue.setConcentration(concentration.toProto())
    }
    if (hasConcentrationLowLimit()) {
      protoValue.setConcentrationLowLimit(concentrationLowLimit.toProto())
    }
    if (hasMeasurementPoint()) {
      protoValue.setMeasurementPoint(measurementPointElement.toProto())
    }
    if (hasCountry()) {
      protoValue.addAllCountry(country.map { it.toProto() })
    }
    if (hasReferenceStrength()) {
      protoValue.addAllReferenceStrength(referenceStrength.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicinalProductIngredient.MedicinalProductIngredientSpecifiedSubstanceStrengthReferenceStrengthComponent.toProto():
    MedicinalProductIngredient.SpecifiedSubstance.Strength.ReferenceStrength {
    val protoValue =
      MedicinalProductIngredient.SpecifiedSubstance.Strength.ReferenceStrength.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSubstance()) {
      protoValue.setSubstance(substance.toProto())
    }
    if (hasStrength()) {
      protoValue.setStrength(strength.toProto())
    }
    if (hasStrengthLowLimit()) {
      protoValue.setStrengthLowLimit(strengthLowLimit.toProto())
    }
    if (hasMeasurementPoint()) {
      protoValue.setMeasurementPoint(measurementPointElement.toProto())
    }
    if (hasCountry()) {
      protoValue.addAllCountry(country.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicinalProductIngredient.MedicinalProductIngredientSubstanceComponent.toProto():
    MedicinalProductIngredient.Substance {
    val protoValue =
      MedicinalProductIngredient.Substance.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.setCode(code.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun MedicinalProductIngredient.SpecifiedSubstance.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductIngredient.MedicinalProductIngredientSpecifiedSubstanceComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductIngredient
        .MedicinalProductIngredientSpecifiedSubstanceComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasCode()) {
      hapiValue.setCode(code.toHapi())
    }
    if (hasGroup()) {
      hapiValue.setGroup(group.toHapi())
    }
    if (hasConfidentiality()) {
      hapiValue.setConfidentiality(confidentiality.toHapi())
    }
    if (strengthCount > 0) {
      hapiValue.setStrength(strengthList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun MedicinalProductIngredient.SpecifiedSubstance.Strength.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductIngredient.MedicinalProductIngredientSpecifiedSubstanceStrengthComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductIngredient
        .MedicinalProductIngredientSpecifiedSubstanceStrengthComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasPresentation()) {
      hapiValue.setPresentation(presentation.toHapi())
    }
    if (hasPresentationLowLimit()) {
      hapiValue.setPresentationLowLimit(presentationLowLimit.toHapi())
    }
    if (hasConcentration()) {
      hapiValue.setConcentration(concentration.toHapi())
    }
    if (hasConcentrationLowLimit()) {
      hapiValue.setConcentrationLowLimit(concentrationLowLimit.toHapi())
    }
    if (hasMeasurementPoint()) {
      hapiValue.setMeasurementPointElement(measurementPoint.toHapi())
    }
    if (countryCount > 0) {
      hapiValue.setCountry(countryList.map { it.toHapi() })
    }
    if (referenceStrengthCount > 0) {
      hapiValue.setReferenceStrength(referenceStrengthList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun MedicinalProductIngredient.SpecifiedSubstance.Strength.ReferenceStrength.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductIngredient.MedicinalProductIngredientSpecifiedSubstanceStrengthReferenceStrengthComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductIngredient
        .MedicinalProductIngredientSpecifiedSubstanceStrengthReferenceStrengthComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasSubstance()) {
      hapiValue.setSubstance(substance.toHapi())
    }
    if (hasStrength()) {
      hapiValue.setStrength(strength.toHapi())
    }
    if (hasStrengthLowLimit()) {
      hapiValue.setStrengthLowLimit(strengthLowLimit.toHapi())
    }
    if (hasMeasurementPoint()) {
      hapiValue.setMeasurementPointElement(measurementPoint.toHapi())
    }
    if (countryCount > 0) {
      hapiValue.setCountry(countryList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  private fun MedicinalProductIngredient.Substance.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductIngredient.MedicinalProductIngredientSubstanceComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductIngredient
        .MedicinalProductIngredientSubstanceComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasCode()) {
      hapiValue.setCode(code.toHapi())
    }
    return hapiValue
  }
}
