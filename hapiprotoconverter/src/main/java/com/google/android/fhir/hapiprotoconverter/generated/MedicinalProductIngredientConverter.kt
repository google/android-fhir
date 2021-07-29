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

public object MedicinalProductIngredientConverter {
  public fun MedicinalProductIngredient.toHapi(): org.hl7.fhir.r4.model.MedicinalProductIngredient {
    val hapiValue = org.hl7.fhir.r4.model.MedicinalProductIngredient()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifier.toHapi())
    hapiValue.setRole(role.toHapi())
    hapiValue.setAllergenicIndicatorElement(allergenicIndicator.toHapi())
    hapiValue.setManufacturer(manufacturerList.map { it.toHapi() })
    hapiValue.setSpecifiedSubstance(specifiedSubstanceList.map { it.toHapi() })
    hapiValue.setSubstance(substance.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.MedicinalProductIngredient.toProto():
    MedicinalProductIngredient {
    val protoValue =
      MedicinalProductIngredient.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setIdentifier(identifier.toProto())
        .setRole(role.toProto())
        .setAllergenicIndicator(allergenicIndicatorElement.toProto())
        .addAllManufacturer(manufacturer.map { it.toProto() })
        .addAllSpecifiedSubstance(specifiedSubstance.map { it.toProto() })
        .setSubstance(substance.toProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.MedicinalProductIngredient.MedicinalProductIngredientSpecifiedSubstanceComponent.toProto():
    MedicinalProductIngredient.SpecifiedSubstance {
    val protoValue =
      MedicinalProductIngredient.SpecifiedSubstance.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCode(code.toProto())
        .setGroup(group.toProto())
        .setConfidentiality(confidentiality.toProto())
        .addAllStrength(strength.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.MedicinalProductIngredient.MedicinalProductIngredientSpecifiedSubstanceStrengthComponent.toProto():
    MedicinalProductIngredient.SpecifiedSubstance.Strength {
    val protoValue =
      MedicinalProductIngredient.SpecifiedSubstance.Strength.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setPresentation(presentation.toProto())
        .setPresentationLowLimit(presentationLowLimit.toProto())
        .setConcentration(concentration.toProto())
        .setConcentrationLowLimit(concentrationLowLimit.toProto())
        .setMeasurementPoint(measurementPointElement.toProto())
        .addAllCountry(country.map { it.toProto() })
        .addAllReferenceStrength(referenceStrength.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.MedicinalProductIngredient.MedicinalProductIngredientSpecifiedSubstanceStrengthReferenceStrengthComponent.toProto():
    MedicinalProductIngredient.SpecifiedSubstance.Strength.ReferenceStrength {
    val protoValue =
      MedicinalProductIngredient.SpecifiedSubstance.Strength.ReferenceStrength.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setSubstance(substance.toProto())
        .setStrength(strength.toProto())
        .setStrengthLowLimit(strengthLowLimit.toProto())
        .setMeasurementPoint(measurementPointElement.toProto())
        .addAllCountry(country.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.MedicinalProductIngredient.MedicinalProductIngredientSubstanceComponent.toProto():
    MedicinalProductIngredient.Substance {
    val protoValue =
      MedicinalProductIngredient.Substance.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setCode(code.toProto())
        .build()
    return protoValue
  }

  private fun MedicinalProductIngredient.SpecifiedSubstance.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductIngredient.MedicinalProductIngredientSpecifiedSubstanceComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductIngredient
        .MedicinalProductIngredientSpecifiedSubstanceComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCode(code.toHapi())
    hapiValue.setGroup(group.toHapi())
    hapiValue.setConfidentiality(confidentiality.toHapi())
    hapiValue.setStrength(strengthList.map { it.toHapi() })
    return hapiValue
  }

  private fun MedicinalProductIngredient.SpecifiedSubstance.Strength.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductIngredient.MedicinalProductIngredientSpecifiedSubstanceStrengthComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductIngredient
        .MedicinalProductIngredientSpecifiedSubstanceStrengthComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setPresentation(presentation.toHapi())
    hapiValue.setPresentationLowLimit(presentationLowLimit.toHapi())
    hapiValue.setConcentration(concentration.toHapi())
    hapiValue.setConcentrationLowLimit(concentrationLowLimit.toHapi())
    hapiValue.setMeasurementPointElement(measurementPoint.toHapi())
    hapiValue.setCountry(countryList.map { it.toHapi() })
    hapiValue.setReferenceStrength(referenceStrengthList.map { it.toHapi() })
    return hapiValue
  }

  private fun MedicinalProductIngredient.SpecifiedSubstance.Strength.ReferenceStrength.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductIngredient.MedicinalProductIngredientSpecifiedSubstanceStrengthReferenceStrengthComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductIngredient
        .MedicinalProductIngredientSpecifiedSubstanceStrengthReferenceStrengthComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setSubstance(substance.toHapi())
    hapiValue.setStrength(strength.toHapi())
    hapiValue.setStrengthLowLimit(strengthLowLimit.toHapi())
    hapiValue.setMeasurementPointElement(measurementPoint.toHapi())
    hapiValue.setCountry(countryList.map { it.toHapi() })
    return hapiValue
  }

  private fun MedicinalProductIngredient.Substance.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductIngredient.MedicinalProductIngredientSubstanceComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductIngredient
        .MedicinalProductIngredientSubstanceComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setCode(code.toHapi())
    return hapiValue
  }
}
