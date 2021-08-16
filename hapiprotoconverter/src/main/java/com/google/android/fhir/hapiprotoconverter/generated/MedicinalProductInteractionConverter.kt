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

import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.MedicinalProductInteraction
import com.google.fhir.r4.core.MedicinalProductInteraction.Interactant
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.Type

object MedicinalProductInteractionConverter {
  @JvmStatic
  private fun MedicinalProductInteraction.Interactant.ItemX.medicinalProductInteractionInteractantItemToHapi():
    Type {
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for MedicinalProductInteraction.interactant.item[x]"
    )
  }

  @JvmStatic
  private fun Type.medicinalProductInteractionInteractantItemToProto():
    MedicinalProductInteraction.Interactant.ItemX {
    val protoValue = MedicinalProductInteraction.Interactant.ItemX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun MedicinalProductInteraction.toHapi(): org.hl7.fhir.r4.model.MedicinalProductInteraction {
    val hapiValue = org.hl7.fhir.r4.model.MedicinalProductInteraction()
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
    if (subjectCount > 0) {
      hapiValue.subject = subjectList.map { it.toHapi() }
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (interactantCount > 0) {
      hapiValue.interactant = interactantList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasEffect()) {
      hapiValue.effect = effect.toHapi()
    }
    if (hasIncidence()) {
      hapiValue.incidence = incidence.toHapi()
    }
    if (hasManagement()) {
      hapiValue.management = management.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.MedicinalProductInteraction.toProto(): MedicinalProductInteraction {
    val protoValue = MedicinalProductInteraction.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasSubject()) {
      protoValue.addAllSubject(subject.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasInteractant()) {
      protoValue.addAllInteractant(interactant.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasEffect()) {
      protoValue.effect = effect.toProto()
    }
    if (hasIncidence()) {
      protoValue.incidence = incidence.toProto()
    }
    if (hasManagement()) {
      protoValue.management = management.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicinalProductInteraction.MedicinalProductInteractionInteractantComponent.toProto():
    MedicinalProductInteraction.Interactant {
    val protoValue =
      MedicinalProductInteraction.Interactant.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasItem()) {
      protoValue.item = item.medicinalProductInteractionInteractantItemToProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun MedicinalProductInteraction.Interactant.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductInteraction.MedicinalProductInteractionInteractantComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductInteraction
        .MedicinalProductInteractionInteractantComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasItem()) {
      hapiValue.item = item.medicinalProductInteractionInteractantItemToHapi()
    }
    return hapiValue
  }
}
