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
    if (identifierCount > 0) {
      hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    }
    if (partOfCount > 0) {
      hapiValue.setPartOf(partOfList.map { it.toHapi() })
    }
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.MedicationDispense.MedicationDispenseStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasStatusReason()) {
      hapiValue.setStatusReason(statusReason.medicationDispenseStatusReasonToHapi())
    }
    if (hasCategory()) {
      hapiValue.setCategory(category.toHapi())
    }
    if (hasMedication()) {
      hapiValue.setMedication(medication.medicationDispenseMedicationToHapi())
    }
    if (hasSubject()) {
      hapiValue.setSubject(subject.toHapi())
    }
    if (hasContext()) {
      hapiValue.setContext(context.toHapi())
    }
    if (supportingInformationCount > 0) {
      hapiValue.setSupportingInformation(supportingInformationList.map { it.toHapi() })
    }
    if (performerCount > 0) {
      hapiValue.setPerformer(performerList.map { it.toHapi() })
    }
    if (hasLocation()) {
      hapiValue.setLocation(location.toHapi())
    }
    if (authorizingPrescriptionCount > 0) {
      hapiValue.setAuthorizingPrescription(authorizingPrescriptionList.map { it.toHapi() })
    }
    if (hasType()) {
      hapiValue.setType(type.toHapi())
    }
    if (hasQuantity()) {
      hapiValue.setQuantity(quantity.toHapi())
    }
    if (hasDaysSupply()) {
      hapiValue.setDaysSupply(daysSupply.toHapi())
    }
    if (hasWhenPrepared()) {
      hapiValue.setWhenPreparedElement(whenPrepared.toHapi())
    }
    if (hasWhenHandedOver()) {
      hapiValue.setWhenHandedOverElement(whenHandedOver.toHapi())
    }
    if (hasDestination()) {
      hapiValue.setDestination(destination.toHapi())
    }
    if (receiverCount > 0) {
      hapiValue.setReceiver(receiverList.map { it.toHapi() })
    }
    if (noteCount > 0) {
      hapiValue.setNote(noteList.map { it.toHapi() })
    }
    if (dosageInstructionCount > 0) {
      hapiValue.setDosageInstruction(dosageInstructionList.map { it.toHapi() })
    }
    if (hasSubstitution()) {
      hapiValue.setSubstitution(substitution.toHapi())
    }
    if (detectedIssueCount > 0) {
      hapiValue.setDetectedIssue(detectedIssueList.map { it.toHapi() })
    }
    if (eventHistoryCount > 0) {
      hapiValue.setEventHistory(eventHistoryList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.MedicationDispense.toProto(): MedicationDispense {
    val protoValue = MedicationDispense.newBuilder().setId(Id.newBuilder().setValue(id))
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
      protoValue.addAllIdentifier(identifier.map { it.toProto() })
    }
    if (hasPartOf()) {
      protoValue.addAllPartOf(partOf.map { it.toProto() })
    }
    protoValue.setStatus(
      MedicationDispense.StatusCode.newBuilder()
        .setValue(
          MedicationDispenseStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasStatusReason()) {
      protoValue.setStatusReason(statusReason.medicationDispenseStatusReasonToProto())
    }
    if (hasCategory()) {
      protoValue.setCategory(category.toProto())
    }
    if (hasMedication()) {
      protoValue.setMedication(medication.medicationDispenseMedicationToProto())
    }
    if (hasSubject()) {
      protoValue.setSubject(subject.toProto())
    }
    if (hasContext()) {
      protoValue.setContext(context.toProto())
    }
    if (hasSupportingInformation()) {
      protoValue.addAllSupportingInformation(supportingInformation.map { it.toProto() })
    }
    if (hasPerformer()) {
      protoValue.addAllPerformer(performer.map { it.toProto() })
    }
    if (hasLocation()) {
      protoValue.setLocation(location.toProto())
    }
    if (hasAuthorizingPrescription()) {
      protoValue.addAllAuthorizingPrescription(authorizingPrescription.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasQuantity()) {
      protoValue.setQuantity((quantity as SimpleQuantity).toProto())
    }
    if (hasDaysSupply()) {
      protoValue.setDaysSupply((daysSupply as SimpleQuantity).toProto())
    }
    if (hasWhenPrepared()) {
      protoValue.setWhenPrepared(whenPreparedElement.toProto())
    }
    if (hasWhenHandedOver()) {
      protoValue.setWhenHandedOver(whenHandedOverElement.toProto())
    }
    if (hasDestination()) {
      protoValue.setDestination(destination.toProto())
    }
    if (hasReceiver()) {
      protoValue.addAllReceiver(receiver.map { it.toProto() })
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    if (hasDosageInstruction()) {
      protoValue.addAllDosageInstruction(dosageInstruction.map { it.toProto() })
    }
    if (hasSubstitution()) {
      protoValue.setSubstitution(substitution.toProto())
    }
    if (hasDetectedIssue()) {
      protoValue.addAllDetectedIssue(detectedIssue.map { it.toProto() })
    }
    if (hasEventHistory()) {
      protoValue.addAllEventHistory(eventHistory.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationDispense.MedicationDispensePerformerComponent.toProto():
    MedicationDispense.Performer {
    val protoValue =
      MedicationDispense.Performer.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasFunction()) {
      protoValue.setFunction(function.toProto())
    }
    if (hasActor()) {
      protoValue.setActor(actor.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationDispense.MedicationDispenseSubstitutionComponent.toProto():
    MedicationDispense.Substitution {
    val protoValue =
      MedicationDispense.Substitution.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasWasSubstituted()) {
      protoValue.setWasSubstituted(wasSubstitutedElement.toProto())
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasReason()) {
      protoValue.addAllReason(reason.map { it.toProto() })
    }
    if (hasResponsibleParty()) {
      protoValue.addAllResponsibleParty(responsibleParty.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun MedicationDispense.Performer.toHapi():
    org.hl7.fhir.r4.model.MedicationDispense.MedicationDispensePerformerComponent {
    val hapiValue = org.hl7.fhir.r4.model.MedicationDispense.MedicationDispensePerformerComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasFunction()) {
      hapiValue.setFunction(function.toHapi())
    }
    if (hasActor()) {
      hapiValue.setActor(actor.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  private fun MedicationDispense.Substitution.toHapi():
    org.hl7.fhir.r4.model.MedicationDispense.MedicationDispenseSubstitutionComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationDispense.MedicationDispenseSubstitutionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasWasSubstituted()) {
      hapiValue.setWasSubstitutedElement(wasSubstituted.toHapi())
    }
    if (hasType()) {
      hapiValue.setType(type.toHapi())
    }
    if (reasonCount > 0) {
      hapiValue.setReason(reasonList.map { it.toHapi() })
    }
    if (responsiblePartyCount > 0) {
      hapiValue.setResponsibleParty(responsiblePartyList.map { it.toHapi() })
    }
    return hapiValue
  }
}
