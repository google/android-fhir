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
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DosageConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DosageConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DurationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DurationConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.NarrativeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.MedicationRequest
import com.google.fhir.r4.core.MedicationRequest.DispenseRequest
import com.google.fhir.r4.core.MedicationRequest.Substitution
import com.google.fhir.r4.core.MedicationRequestIntentCode
import com.google.fhir.r4.core.MedicationrequestStatusCode
import com.google.fhir.r4.core.RequestPriorityCode
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.SimpleQuantity
import org.hl7.fhir.r4.model.Type

object MedicationRequestConverter {
  private fun MedicationRequest.ReportedX.medicationRequestReportedToHapi(): Type {
    if (hasBoolean()) {
      return (this.boolean).toHapi()
    }
    if (hasReference()) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for MedicationRequest.reported[x]")
  }

  private fun Type.medicationRequestReportedToProto(): MedicationRequest.ReportedX {
    val protoValue = MedicationRequest.ReportedX.newBuilder()
    if (this is BooleanType) {
      protoValue.boolean = this.toProto()
    }
    if (this is Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  private fun MedicationRequest.MedicationX.medicationRequestMedicationToHapi(): Type {
    if (hasCodeableConcept()) {
      return (this.codeableConcept).toHapi()
    }
    if (hasReference()) {
      return (this.reference).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for MedicationRequest.medication[x]")
  }

  private fun Type.medicationRequestMedicationToProto(): MedicationRequest.MedicationX {
    val protoValue = MedicationRequest.MedicationX.newBuilder()
    if (this is CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    if (this is Reference) {
      protoValue.reference = this.toProto()
    }
    return protoValue.build()
  }

  private fun MedicationRequest.Substitution.AllowedX.medicationRequestSubstitutionAllowedToHapi():
    Type {
    if (hasBoolean()) {
      return (this.boolean).toHapi()
    }
    if (hasCodeableConcept()) {
      return (this.codeableConcept).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for MedicationRequest.substitution.allowed[x]")
  }

  private fun Type.medicationRequestSubstitutionAllowedToProto():
    MedicationRequest.Substitution.AllowedX {
    val protoValue = MedicationRequest.Substitution.AllowedX.newBuilder()
    if (this is BooleanType) {
      protoValue.boolean = this.toProto()
    }
    if (this is CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    return protoValue.build()
  }

  fun MedicationRequest.toHapi(): org.hl7.fhir.r4.model.MedicationRequest {
    val hapiValue = org.hl7.fhir.r4.model.MedicationRequest()
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
    if (hasStatus()) {
      hapiValue.status =
        org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasStatusReason()) {
      hapiValue.statusReason = statusReason.toHapi()
    }
    if (hasIntent()) {
      hapiValue.intent =
        org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestIntent.valueOf(
          intent.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (categoryCount > 0) {
      hapiValue.category = categoryList.map { it.toHapi() }
    }
    if (hasPriority()) {
      hapiValue.priority =
        org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestPriority.valueOf(
          priority.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasDoNotPerform()) {
      hapiValue.doNotPerformElement = doNotPerform.toHapi()
    }
    if (hasReported()) {
      hapiValue.reported = reported.medicationRequestReportedToHapi()
    }
    if (hasMedication()) {
      hapiValue.medication = medication.medicationRequestMedicationToHapi()
    }
    if (hasSubject()) {
      hapiValue.subject = subject.toHapi()
    }
    if (hasEncounter()) {
      hapiValue.encounter = encounter.toHapi()
    }
    if (supportingInformationCount > 0) {
      hapiValue.supportingInformation = supportingInformationList.map { it.toHapi() }
    }
    if (hasAuthoredOn()) {
      hapiValue.authoredOnElement = authoredOn.toHapi()
    }
    if (hasRequester()) {
      hapiValue.requester = requester.toHapi()
    }
    if (hasPerformer()) {
      hapiValue.performer = performer.toHapi()
    }
    if (hasPerformerType()) {
      hapiValue.performerType = performerType.toHapi()
    }
    if (hasRecorder()) {
      hapiValue.recorder = recorder.toHapi()
    }
    if (reasonCodeCount > 0) {
      hapiValue.reasonCode = reasonCodeList.map { it.toHapi() }
    }
    if (reasonReferenceCount > 0) {
      hapiValue.reasonReference = reasonReferenceList.map { it.toHapi() }
    }
    if (instantiatesCanonicalCount > 0) {
      hapiValue.instantiatesCanonical = instantiatesCanonicalList.map { it.toHapi() }
    }
    if (instantiatesUriCount > 0) {
      hapiValue.instantiatesUri = instantiatesUriList.map { it.toHapi() }
    }
    if (basedOnCount > 0) {
      hapiValue.basedOn = basedOnList.map { it.toHapi() }
    }
    if (hasGroupIdentifier()) {
      hapiValue.groupIdentifier = groupIdentifier.toHapi()
    }
    if (hasCourseOfTherapyType()) {
      hapiValue.courseOfTherapyType = courseOfTherapyType.toHapi()
    }
    if (insuranceCount > 0) {
      hapiValue.insurance = insuranceList.map { it.toHapi() }
    }
    if (noteCount > 0) {
      hapiValue.note = noteList.map { it.toHapi() }
    }
    if (dosageInstructionCount > 0) {
      hapiValue.dosageInstruction = dosageInstructionList.map { it.toHapi() }
    }
    if (hasDispenseRequest()) {
      hapiValue.dispenseRequest = dispenseRequest.toHapi()
    }
    if (hasSubstitution()) {
      hapiValue.substitution = substitution.toHapi()
    }
    if (hasPriorPrescription()) {
      hapiValue.priorPrescription = priorPrescription.toHapi()
    }
    if (detectedIssueCount > 0) {
      hapiValue.detectedIssue = detectedIssueList.map { it.toHapi() }
    }
    if (eventHistoryCount > 0) {
      hapiValue.eventHistory = eventHistoryList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.MedicationRequest.toProto(): MedicationRequest {
    val protoValue = MedicationRequest.newBuilder()
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
    if (hasStatus()) {
      protoValue.status =
        MedicationRequest.StatusCode.newBuilder()
          .setValue(
            MedicationrequestStatusCode.Value.valueOf(
              status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasStatusReason()) {
      protoValue.statusReason = statusReason.toProto()
    }
    if (hasIntent()) {
      protoValue.intent =
        MedicationRequest.IntentCode.newBuilder()
          .setValue(
            MedicationRequestIntentCode.Value.valueOf(
              intent.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasCategory()) {
      protoValue.addAllCategory(category.map { it.toProto() })
    }
    if (hasPriority()) {
      protoValue.priority =
        MedicationRequest.PriorityCode.newBuilder()
          .setValue(
            RequestPriorityCode.Value.valueOf(
              priority.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasDoNotPerform()) {
      protoValue.doNotPerform = doNotPerformElement.toProto()
    }
    if (hasReported()) {
      protoValue.reported = reported.medicationRequestReportedToProto()
    }
    if (hasMedication()) {
      protoValue.medication = medication.medicationRequestMedicationToProto()
    }
    if (hasSubject()) {
      protoValue.subject = subject.toProto()
    }
    if (hasEncounter()) {
      protoValue.encounter = encounter.toProto()
    }
    if (hasSupportingInformation()) {
      protoValue.addAllSupportingInformation(supportingInformation.map { it.toProto() })
    }
    if (hasAuthoredOn()) {
      protoValue.authoredOn = authoredOnElement.toProto()
    }
    if (hasRequester()) {
      protoValue.requester = requester.toProto()
    }
    if (hasPerformer()) {
      protoValue.performer = performer.toProto()
    }
    if (hasPerformerType()) {
      protoValue.performerType = performerType.toProto()
    }
    if (hasRecorder()) {
      protoValue.recorder = recorder.toProto()
    }
    if (hasReasonCode()) {
      protoValue.addAllReasonCode(reasonCode.map { it.toProto() })
    }
    if (hasReasonReference()) {
      protoValue.addAllReasonReference(reasonReference.map { it.toProto() })
    }
    if (hasInstantiatesCanonical()) {
      protoValue.addAllInstantiatesCanonical(instantiatesCanonical.map { it.toProto() })
    }
    if (hasInstantiatesUri()) {
      protoValue.addAllInstantiatesUri(instantiatesUri.map { it.toProto() })
    }
    if (hasBasedOn()) {
      protoValue.addAllBasedOn(basedOn.map { it.toProto() })
    }
    if (hasGroupIdentifier()) {
      protoValue.groupIdentifier = groupIdentifier.toProto()
    }
    if (hasCourseOfTherapyType()) {
      protoValue.courseOfTherapyType = courseOfTherapyType.toProto()
    }
    if (hasInsurance()) {
      protoValue.addAllInsurance(insurance.map { it.toProto() })
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    if (hasDosageInstruction()) {
      protoValue.addAllDosageInstruction(dosageInstruction.map { it.toProto() })
    }
    if (hasDispenseRequest()) {
      protoValue.dispenseRequest = dispenseRequest.toProto()
    }
    if (hasSubstitution()) {
      protoValue.substitution = substitution.toProto()
    }
    if (hasPriorPrescription()) {
      protoValue.priorPrescription = priorPrescription.toProto()
    }
    if (hasDetectedIssue()) {
      protoValue.addAllDetectedIssue(detectedIssue.map { it.toProto() })
    }
    if (hasEventHistory()) {
      protoValue.addAllEventHistory(eventHistory.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestDispenseRequestComponent.toProto():
    MedicationRequest.DispenseRequest {
    val protoValue = MedicationRequest.DispenseRequest.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasInitialFill()) {
      protoValue.initialFill = initialFill.toProto()
    }
    if (hasDispenseInterval()) {
      protoValue.dispenseInterval = dispenseInterval.toProto()
    }
    if (hasValidityPeriod()) {
      protoValue.validityPeriod = validityPeriod.toProto()
    }
    if (hasNumberOfRepeatsAllowed()) {
      protoValue.numberOfRepeatsAllowed = numberOfRepeatsAllowedElement.toProto()
    }
    if (hasQuantity()) {
      protoValue.quantity = (quantity as SimpleQuantity).toProto()
    }
    if (hasExpectedSupplyDuration()) {
      protoValue.expectedSupplyDuration = expectedSupplyDuration.toProto()
    }
    if (hasPerformer()) {
      protoValue.performer = performer.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestDispenseRequestInitialFillComponent.toProto():
    MedicationRequest.DispenseRequest.InitialFill {
    val protoValue = MedicationRequest.DispenseRequest.InitialFill.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasQuantity()) {
      protoValue.quantity = (quantity as SimpleQuantity).toProto()
    }
    if (hasDuration()) {
      protoValue.duration = duration.toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestSubstitutionComponent.toProto():
    MedicationRequest.Substitution {
    val protoValue = MedicationRequest.Substitution.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasAllowed()) {
      protoValue.allowed = allowed.medicationRequestSubstitutionAllowedToProto()
    }
    if (hasReason()) {
      protoValue.reason = reason.toProto()
    }
    return protoValue.build()
  }

  private fun MedicationRequest.DispenseRequest.toHapi():
    org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestDispenseRequestComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestDispenseRequestComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasInitialFill()) {
      hapiValue.initialFill = initialFill.toHapi()
    }
    if (hasDispenseInterval()) {
      hapiValue.dispenseInterval = dispenseInterval.toHapi()
    }
    if (hasValidityPeriod()) {
      hapiValue.validityPeriod = validityPeriod.toHapi()
    }
    if (hasNumberOfRepeatsAllowed()) {
      hapiValue.numberOfRepeatsAllowedElement = numberOfRepeatsAllowed.toHapi()
    }
    if (hasQuantity()) {
      hapiValue.quantity = quantity.toHapi()
    }
    if (hasExpectedSupplyDuration()) {
      hapiValue.expectedSupplyDuration = expectedSupplyDuration.toHapi()
    }
    if (hasPerformer()) {
      hapiValue.performer = performer.toHapi()
    }
    return hapiValue
  }

  private fun MedicationRequest.DispenseRequest.InitialFill.toHapi():
    org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestDispenseRequestInitialFillComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestDispenseRequestInitialFillComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasQuantity()) {
      hapiValue.quantity = quantity.toHapi()
    }
    if (hasDuration()) {
      hapiValue.duration = duration.toHapi()
    }
    return hapiValue
  }

  private fun MedicationRequest.Substitution.toHapi():
    org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestSubstitutionComponent {
    val hapiValue = org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestSubstitutionComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasAllowed()) {
      hapiValue.allowed = allowed.medicationRequestSubstitutionAllowedToHapi()
    }
    if (hasReason()) {
      hapiValue.reason = reason.toHapi()
    }
    return hapiValue
  }
}
