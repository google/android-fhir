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

public object MedicinalProductInteractionConverter {
  @JvmStatic
  private fun MedicinalProductInteraction.Interactant.ItemX.medicinalProductInteractionInteractantItemToHapi():
    Type {
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
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
      protoValue.setReference(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun MedicinalProductInteraction.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductInteraction {
    val hapiValue = org.hl7.fhir.r4.model.MedicinalProductInteraction()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setSubject(subjectList.map { it.toHapi() })
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setInteractant(interactantList.map { it.toHapi() })
    hapiValue.setType(type.toHapi())
    hapiValue.setEffect(effect.toHapi())
    hapiValue.setIncidence(incidence.toHapi())
    hapiValue.setManagement(management.toHapi())
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.MedicinalProductInteraction.toProto():
    MedicinalProductInteraction {
    val protoValue =
      MedicinalProductInteraction.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllSubject(subject.map { it.toProto() })
        .setDescription(descriptionElement.toProto())
        .addAllInteractant(interactant.map { it.toProto() })
        .setType(type.toProto())
        .setEffect(effect.toProto())
        .setIncidence(incidence.toProto())
        .setManagement(management.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicinalProductInteraction.MedicinalProductInteractionInteractantComponent.toProto():
    MedicinalProductInteraction.Interactant {
    val protoValue =
      MedicinalProductInteraction.Interactant.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setItem(item.medicinalProductInteractionInteractantItemToProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun MedicinalProductInteraction.Interactant.toHapi():
    org.hl7.fhir.r4.model.MedicinalProductInteraction.MedicinalProductInteractionInteractantComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicinalProductInteraction
        .MedicinalProductInteractionInteractantComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setItem(item.medicinalProductInteractionInteractantItemToHapi())
    return hapiValue
  }
}
