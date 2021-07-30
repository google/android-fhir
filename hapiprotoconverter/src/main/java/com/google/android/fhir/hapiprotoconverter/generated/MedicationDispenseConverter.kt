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

import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DosageConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DosageConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.MedicationDispense
import com.google.fhir.r4.core.MedicationDispenseStatusCode
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.SimpleQuantity
import org.hl7.fhir.r4.model.Type

public object MedicationDispenseConverter {
  @JvmStatic
  private fun MedicationDispense.StatusReasonX.medicationDispenseStatusReasonToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for MedicationDispense.statusReason[x]")
  }

  @JvmStatic
  private fun Type.medicationDispenseStatusReasonToProto(): MedicationDispense.StatusReasonX {
    val protoValue = MedicationDispense.StatusReasonX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun MedicationDispense.MedicationX.medicationDispenseMedicationToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for MedicationDispense.medication[x]")
  }

  @JvmStatic
  private fun Type.medicationDispenseMedicationToProto(): MedicationDispense.MedicationX {
    val protoValue = MedicationDispense.MedicationX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun MedicationDispense.toHapi(): org.hl7.fhir.r4.model.MedicationDispense {
    val hapiValue = org.hl7.fhir.r4.model.MedicationDispense()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setPartOf(partOfList.map { it.toHapi() })
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.MedicationDispense.MedicationDispenseStatus.valueOf(
        status.value.name.replace("_", "")
      )
    )
    hapiValue.setStatusReason(statusReason.medicationDispenseStatusReasonToHapi())
    hapiValue.setCategory(category.toHapi())
    hapiValue.setMedication(medication.medicationDispenseMedicationToHapi())
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setContext(context.toHapi())
    hapiValue.setSupportingInformation(supportingInformationList.map { it.toHapi() })
    hapiValue.setPerformer(performerList.map { it.toHapi() })
    hapiValue.setLocation(location.toHapi())
    hapiValue.setAuthorizingPrescription(authorizingPrescriptionList.map { it.toHapi() })
    hapiValue.setType(type.toHapi())
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setDaysSupply(daysSupply.toHapi())
    hapiValue.setWhenPreparedElement(whenPrepared.toHapi())
    hapiValue.setWhenHandedOverElement(whenHandedOver.toHapi())
    hapiValue.setDestination(destination.toHapi())
    hapiValue.setReceiver(receiverList.map { it.toHapi() })
    hapiValue.setNote(noteList.map { it.toHapi() })
    hapiValue.setDosageInstruction(dosageInstructionList.map { it.toHapi() })
    hapiValue.setSubstitution(substitution.toHapi())
    hapiValue.setDetectedIssue(detectedIssueList.map { it.toHapi() })
    hapiValue.setEventHistory(eventHistoryList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.MedicationDispense.toProto(): MedicationDispense {
    val protoValue =
      MedicationDispense.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .addAllPartOf(partOf.map { it.toProto() })
        .setStatus(
          MedicationDispense.StatusCode.newBuilder()
            .setValue(
              MedicationDispenseStatusCode.Value.valueOf(
                status.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setStatusReason(statusReason.medicationDispenseStatusReasonToProto())
        .setCategory(category.toProto())
        .setMedication(medication.medicationDispenseMedicationToProto())
        .setSubject(subject.toProto())
        .setContext(context.toProto())
        .addAllSupportingInformation(supportingInformation.map { it.toProto() })
        .addAllPerformer(performer.map { it.toProto() })
        .setLocation(location.toProto())
        .addAllAuthorizingPrescription(authorizingPrescription.map { it.toProto() })
        .setType(type.toProto())
        .setQuantity((quantity as SimpleQuantity).toProto())
        .setDaysSupply((daysSupply as SimpleQuantity).toProto())
        .setWhenPrepared(whenPreparedElement.toProto())
        .setWhenHandedOver(whenHandedOverElement.toProto())
        .setDestination(destination.toProto())
        .addAllReceiver(receiver.map { it.toProto() })
        .addAllNote(note.map { it.toProto() })
        .addAllDosageInstruction(dosageInstruction.map { it.toProto() })
        .setSubstitution(substitution.toProto())
        .addAllDetectedIssue(detectedIssue.map { it.toProto() })
        .addAllEventHistory(eventHistory.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationDispense.MedicationDispensePerformerComponent.toProto():
    MedicationDispense.Performer {
    val protoValue =
      MedicationDispense.Performer.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setFunction(function.toProto())
        .setActor(actor.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationDispense.MedicationDispenseSubstitutionComponent.toProto():
    MedicationDispense.Substitution {
    val protoValue =
      MedicationDispense.Substitution.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setWasSubstituted(wasSubstitutedElement.toProto())
        .setType(type.toProto())
        .addAllReason(reason.map { it.toProto() })
        .addAllResponsibleParty(responsibleParty.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun MedicationDispense.Performer.toHapi():
    org.hl7.fhir.r4.model.MedicationDispense.MedicationDispensePerformerComponent {
    val hapiValue = org.hl7.fhir.r4.model.MedicationDispense.MedicationDispensePerformerComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setFunction(function.toHapi())
    hapiValue.setActor(actor.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun MedicationDispense.Substitution.toHapi():
    org.hl7.fhir.r4.model.MedicationDispense.MedicationDispenseSubstitutionComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationDispense.MedicationDispenseSubstitutionComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setWasSubstitutedElement(wasSubstituted.toHapi())
    hapiValue.setType(type.toHapi())
    hapiValue.setReason(reasonList.map { it.toHapi() })
    hapiValue.setResponsibleParty(responsiblePartyList.map { it.toHapi() })
    return hapiValue
  }
}
