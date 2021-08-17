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
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Medication
import com.google.fhir.r4.core.Medication.Ingredient
import com.google.fhir.r4.core.MedicationStatusCode
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Type

object MedicationConverter {
  private fun Medication.Ingredient.ItemX.medicationIngredientItemToHapi(): Type {
    if (hasCodeableConcept()) {
      return (this.codeableConcept).toHapi()
    }
    if (hasReference()) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Medication.ingredient.item[x]")
  }

  private fun Type.medicationIngredientItemToProto(): Medication.Ingredient.ItemX {
    val protoValue = Medication.Ingredient.ItemX.newBuilder()
    if (this is CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  fun Medication.toHapi(): org.hl7.fhir.r4.model.Medication {
    val hapiValue = org.hl7.fhir.r4.model.Medication()
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
    if (identifierCount > 0) {
      hapiValue.identifier = identifierList.map { it.toHapi() }
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (hasStatus()) {
      hapiValue.status =
        org.hl7.fhir.r4.model.Medication.MedicationStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasManufacturer()) {
      hapiValue.manufacturer = manufacturer.toHapi()
    }
    if (hasForm()) {
      hapiValue.form = form.toHapi()
    }
    if (hasAmount()) {
      hapiValue.amount = amount.toHapi()
    }
    if (ingredientCount > 0) {
      hapiValue.ingredient = ingredientList.map { it.toHapi() }
    }
    if (hasBatch()) {
      hapiValue.batch = batch.toHapi()
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.Medication.toProto(): Medication {
    val protoValue = Medication.newBuilder()
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
    if (hasIdentifier()) {
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    if (hasStatus()) {
      protoValue.status =
        Medication.StatusCode.newBuilder()
          .setValue(
            MedicationStatusCode.Value.valueOf(
              status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasManufacturer()) {
      protoValue.manufacturer = manufacturer.toProto()
    }
    if (hasForm()) {
      protoValue.form = form.toProto()
    }
    if (hasAmount()) {
      protoValue.amount = amount.toProto()
    }
    if (hasIngredient()) {
      protoValue.addAllIngredient(ingredient.map { it.toProto() })
    }
    if (hasBatch()) {
      protoValue.batch = batch.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Medication.MedicationIngredientComponent.toProto():
    Medication.Ingredient {
    val protoValue = Medication.Ingredient.newBuilder()
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
      protoValue.item = item.medicationIngredientItemToProto()
    }
    if (hasIsActive()) {
      protoValue.isActive = isActiveElement.toProto()
    }
    if (hasStrength()) {
      protoValue.strength = strength.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Medication.MedicationBatchComponent.toProto():
    Medication.Batch {
    val protoValue = Medication.Batch.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasLotNumber()) {
      protoValue.lotNumber = lotNumberElement.toProto()
    }
    if (hasExpirationDate()) {
      protoValue.expirationDate = expirationDateElement.toProto()
    }
    return protoValue.build()
  }

  private fun Medication.Ingredient.toHapi():
    org.hl7.fhir.r4.model.Medication.MedicationIngredientComponent {
    val hapiValue = org.hl7.fhir.r4.model.Medication.MedicationIngredientComponent()
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
      hapiValue.item = item.medicationIngredientItemToHapi()
    }
    if (hasIsActive()) {
      hapiValue.isActiveElement = isActive.toHapi()
    }
    if (hasStrength()) {
      hapiValue.strength = strength.toHapi()
    }
    return hapiValue
  }

  private fun Medication.Batch.toHapi(): org.hl7.fhir.r4.model.Medication.MedicationBatchComponent {
    val hapiValue = org.hl7.fhir.r4.model.Medication.MedicationBatchComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasLotNumber()) {
      hapiValue.lotNumberElement = lotNumber.toHapi()
    }
    if (hasExpirationDate()) {
      hapiValue.expirationDateElement = expirationDate.toHapi()
    }
    return hapiValue
  }
}
