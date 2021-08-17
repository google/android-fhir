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
import org.hl7.fhir.r4.model.SimpleQuantity
import org.hl7.fhir.r4.model.Type

object MedicationDispenseConverter {
  private fun MedicationDispense.StatusReasonX.medicationDispenseStatusReasonToHapi(): Type {
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for MedicationDispense.statusReason[x]")
  }

  private fun Type.medicationDispenseStatusReasonToProto(): MedicationDispense.StatusReasonX {
    val protoValue = MedicationDispense.StatusReasonX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  private fun MedicationDispense.MedicationX.medicationDispenseMedicationToHapi(): Type {
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for MedicationDispense.medication[x]")
  }

  private fun Type.medicationDispenseMedicationToProto(): MedicationDispense.MedicationX {
    val protoValue = MedicationDispense.MedicationX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  fun MedicationDispense.toHapi(): org.hl7.fhir.r4.model.MedicationDispense {
    val hapiValue = org.hl7.fhir.r4.model.MedicationDispense()
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
    if (identifierCount > 0) {
      hapiValue.identifier = identifierList.map { it.toHapi() }
    }
    if (partOfCount > 0) {
      hapiValue.partOf = partOfList.map { it.toHapi() }
    }
    hapiValue.status =
      org.hl7.fhir.r4.model.MedicationDispense.MedicationDispenseStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasStatusReason()) {
      hapiValue.statusReason = statusReason.medicationDispenseStatusReasonToHapi()
    }
    if (hasCategory()) {
      hapiValue.category = category.toHapi()
    }
    if (hasMedication()) {
      hapiValue.medication = medication.medicationDispenseMedicationToHapi()
    }
    if (hasSubject()) {
      hapiValue.subject = subject.toHapi()
    }
    if (hasContext()) {
      hapiValue.context = context.toHapi()
    }
    if (supportingInformationCount > 0) {
      hapiValue.supportingInformation = supportingInformationList.map { it.toHapi() }
    }
    if (performerCount > 0) {
      hapiValue.performer = performerList.map { it.toHapi() }
    }
    if (hasLocation()) {
      hapiValue.location = location.toHapi()
    }
    if (authorizingPrescriptionCount > 0) {
      hapiValue.authorizingPrescription = authorizingPrescriptionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasQuantity()) {
      hapiValue.quantity = quantity.toHapi()
    }
    if (hasDaysSupply()) {
      hapiValue.daysSupply = daysSupply.toHapi()
    }
    if (hasWhenPrepared()) {
      hapiValue.whenPreparedElement = whenPrepared.toHapi()
    }
    if (hasWhenHandedOver()) {
      hapiValue.whenHandedOverElement = whenHandedOver.toHapi()
    }
    if (hasDestination()) {
      hapiValue.destination = destination.toHapi()
    }
    if (receiverCount > 0) {
      hapiValue.receiver = receiverList.map { it.toHapi() }
    }
    if (noteCount > 0) {
      hapiValue.note = noteList.map { it.toHapi() }
    }
    if (dosageInstructionCount > 0) {
      hapiValue.dosageInstruction = dosageInstructionList.map { it.toHapi() }
    }
    if (hasSubstitution()) {
      hapiValue.substitution = substitution.toHapi()
    }
    if (detectedIssueCount > 0) {
      hapiValue.detectedIssue = detectedIssueList.map { it.toHapi() }
    }
    if (eventHistoryCount > 0) {
      hapiValue.eventHistory = eventHistoryList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.MedicationDispense.toProto(): MedicationDispense {
    val protoValue = MedicationDispense.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasPartOf()) {
      protoValue.addAllPartOf(partOf.map { it.toProto() })
    }
    protoValue.status =
      MedicationDispense.StatusCode.newBuilder()
        .setValue(
          MedicationDispenseStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasStatusReason()) {
      protoValue.statusReason = statusReason.medicationDispenseStatusReasonToProto()
    }
    if (hasCategory()) {
      protoValue.category = category.toProto()
    }
    if (hasMedication()) {
      protoValue.medication = medication.medicationDispenseMedicationToProto()
    }
    if (hasSubject()) {
      protoValue.subject = subject.toProto()
    }
    if (hasContext()) {
      protoValue.context = context.toProto()
    }
    if (hasSupportingInformation()) {
      protoValue.addAllSupportingInformation(supportingInformation.map { it.toProto() })
    }
    if (hasPerformer()) {
      protoValue.addAllPerformer(performer.map { it.toProto() })
    }
    if (hasLocation()) {
      protoValue.location = location.toProto()
    }
    if (hasAuthorizingPrescription()) {
      protoValue.addAllAuthorizingPrescription(authorizingPrescription.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasQuantity()) {
      protoValue.quantity = (quantity as SimpleQuantity).toProto()
    }
    if (hasDaysSupply()) {
      protoValue.daysSupply = (daysSupply as SimpleQuantity).toProto()
    }
    if (hasWhenPrepared()) {
      protoValue.whenPrepared = whenPreparedElement.toProto()
    }
    if (hasWhenHandedOver()) {
      protoValue.whenHandedOver = whenHandedOverElement.toProto()
    }
    if (hasDestination()) {
      protoValue.destination = destination.toProto()
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
      protoValue.substitution = substitution.toProto()
    }
    if (hasDetectedIssue()) {
      protoValue.addAllDetectedIssue(detectedIssue.map { it.toProto() })
    }
    if (hasEventHistory()) {
      protoValue.addAllEventHistory(eventHistory.map { it.toProto() })
    }
    return protoValue.build()
  }

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
      protoValue.function = function.toProto()
    }
    if (hasActor()) {
      protoValue.actor = actor.toProto()
    }
    return protoValue.build()
  }

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
      protoValue.wasSubstituted = wasSubstitutedElement.toProto()
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasReason()) {
      protoValue.addAllReason(reason.map { it.toProto() })
    }
    if (hasResponsibleParty()) {
      protoValue.addAllResponsibleParty(responsibleParty.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun MedicationDispense.Performer.toHapi():
    org.hl7.fhir.r4.model.MedicationDispense.MedicationDispensePerformerComponent {
    val hapiValue = org.hl7.fhir.r4.model.MedicationDispense.MedicationDispensePerformerComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasFunction()) {
      hapiValue.function = function.toHapi()
    }
    if (hasActor()) {
      hapiValue.actor = actor.toHapi()
    }
    return hapiValue
  }

  private fun MedicationDispense.Substitution.toHapi():
    org.hl7.fhir.r4.model.MedicationDispense.MedicationDispenseSubstitutionComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationDispense.MedicationDispenseSubstitutionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasWasSubstituted()) {
      hapiValue.wasSubstitutedElement = wasSubstituted.toHapi()
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (reasonCount > 0) {
      hapiValue.reason = reasonList.map { it.toHapi() }
    }
    if (responsiblePartyCount > 0) {
      hapiValue.responsibleParty = responsiblePartyList.map { it.toHapi() }
    }
    return hapiValue
  }
}
