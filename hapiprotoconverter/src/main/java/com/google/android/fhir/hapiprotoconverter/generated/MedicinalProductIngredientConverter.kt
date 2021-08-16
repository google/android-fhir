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

object MedicinalProductIngredientConverter {
  @JvmStatic
  fun MedicinalProductIngredient.toHapi(): org.hl7.fhir.r4.model.MedicinalProductIngredient {
    val hapiValue = org.hl7.fhir.r4.model.MedicinalProductIngredient()
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
    if (hasIdentifier()) {
      hapiValue.identifier = identifier.toHapi()
    }
    if (hasRole()) {
      hapiValue.role = role.toHapi()
    }
    if (hasAllergenicIndicator()) {
      hapiValue.allergenicIndicatorElement = allergenicIndicator.toHapi()
    }
    if (manufacturerCount > 0) {
      hapiValue.manufacturer = manufacturerList.map { it.toHapi() }
    }
    if (specifiedSubstanceCount > 0) {
      hapiValue.specifiedSubstance = specifiedSubstanceList.map { it.toHapi() }
    }
    if (hasSubstance()) {
      hapiValue.substance = substance.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.MedicinalProductIngredient.toProto(): MedicinalProductIngredient {
    val protoValue = MedicinalProductIngredient.newBuilder().setId(Id.newBuilder().setValue(id))
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
      protoValue.identifier = identifier.toProto()
    }
    if (hasRole()) {
      protoValue.role = role.toProto()
    }
    if (hasAllergenicIndicator()) {
      protoValue.allergenicIndicator = allergenicIndicatorElement.toProto()
    }
    if (hasManufacturer()) {
      protoValue.addAllManufacturer(manufacturer.map { it.toProto() })
    }
    if (hasSpecifiedSubstance()) {
      protoValue.addAllSpecifiedSubstance(specifiedSubstance.map { it.toProto() })
    }
    if (hasSubstance()) {
      protoValue.substance = substance.toProto()
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
      protoValue.code = code.toProto()
    }
    if (hasGroup()) {
      protoValue.group = group.toProto()
    }
    if (hasConfidentiality()) {
      protoValue.confidentiality = confidentiality.toProto()
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
      protoValue.presentation = presentation.toProto()
    }
    if (hasPresentationLowLimit()) {
      protoValue.presentationLowLimit = presentationLowLimit.toProto()
    }
    if (hasConcentration()) {
      protoValue.concentration = concentration.toProto()
    }
    if (hasConcentrationLowLimit()) {
      protoValue.concentrationLowLimit = concentrationLowLimit.toProto()
    }
    if (hasMeasurementPoint()) {
      protoValue.measurementPoint = measurementPointElement.toProto()
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
      protoValue.substance = substance.toProto()
    }
    if (hasStrength()) {
      protoValue.strength = strength.toProto()
    }
    if (hasStrengthLowLimit()) {
      protoValue.strengthLowLimit = strengthLowLimit.toProto()
    }
    if (hasMeasurementPoint()) {
      protoValue.measurementPoint = measurementPointElement.toProto()
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
      protoValue.code = code.toProto()
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
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (hasGroup()) {
      hapiValue.group = group.toHapi()
    }
    if (hasConfidentiality()) {
      hapiValue.confidentiality = confidentiality.toHapi()
    }
    if (strengthCount > 0) {
      hapiValue.strength = strengthList.map { it.toHapi() }
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
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasPresentation()) {
      hapiValue.presentation = presentation.toHapi()
    }
    if (hasPresentationLowLimit()) {
      hapiValue.presentationLowLimit = presentationLowLimit.toHapi()
    }
    if (hasConcentration()) {
      hapiValue.concentration = concentration.toHapi()
    }
    if (hasConcentrationLowLimit()) {
      hapiValue.concentrationLowLimit = concentrationLowLimit.toHapi()
    }
    if (hasMeasurementPoint()) {
      hapiValue.measurementPointElement = measurementPoint.toHapi()
    }
    if (countryCount > 0) {
      hapiValue.country = countryList.map { it.toHapi() }
    }
    if (referenceStrengthCount > 0) {
      hapiValue.referenceStrength = referenceStrengthList.map { it.toHapi() }
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
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasSubstance()) {
      hapiValue.substance = substance.toHapi()
    }
    if (hasStrength()) {
      hapiValue.strength = strength.toHapi()
    }
    if (hasStrengthLowLimit()) {
      hapiValue.strengthLowLimit = strengthLowLimit.toHapi()
    }
    if (hasMeasurementPoint()) {
      hapiValue.measurementPointElement = measurementPoint.toHapi()
    }
    if (countryCount > 0) {
      hapiValue.country = countryList.map { it.toHapi() }
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
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    return hapiValue
  }
}
