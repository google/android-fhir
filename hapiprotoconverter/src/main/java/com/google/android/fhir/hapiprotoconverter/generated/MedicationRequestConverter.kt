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
import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.MedicationRequest
import com.google.fhir.r4.core.MedicationRequest.DispenseRequest
import com.google.fhir.r4.core.MedicationRequest.Substitution
import com.google.fhir.r4.core.MedicationRequestIntentCode
import com.google.fhir.r4.core.MedicationrequestStatusCode
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.RequestPriorityCode
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.SimpleQuantity
import org.hl7.fhir.r4.model.Type

public object MedicationRequestConverter {
  @JvmStatic
  private fun MedicationRequest.ReportedX.medicationRequestReportedToHapi(): Type {
    if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType) {
      return (this.getBoolean()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for MedicationRequest.reported[x]")
  }

  @JvmStatic
  private fun Type.medicationRequestReportedToProto(): MedicationRequest.ReportedX {
    val protoValue = MedicationRequest.ReportedX.newBuilder()
    if (this is BooleanType) {
      protoValue.setBoolean(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun MedicationRequest.MedicationX.medicationRequestMedicationToHapi(): Type {
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType) {
      return (this.getReference()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for MedicationRequest.medication[x]")
  }

  @JvmStatic
  private fun Type.medicationRequestMedicationToProto(): MedicationRequest.MedicationX {
    val protoValue = MedicationRequest.MedicationX.newBuilder()
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun MedicationRequest.Substitution.AllowedX.medicationRequestSubstitutionAllowedToHapi():
    Type {
    if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType) {
      return (this.getBoolean()).toHapi()
    }
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for MedicationRequest.substitution.allowed[x]")
  }

  @JvmStatic
  private fun Type.medicationRequestSubstitutionAllowedToProto():
    MedicationRequest.Substitution.AllowedX {
    val protoValue = MedicationRequest.Substitution.AllowedX.newBuilder()
    if (this is BooleanType) {
      protoValue.setBoolean(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun MedicationRequest.toHapi(): org.hl7.fhir.r4.model.MedicationRequest {
    val hapiValue = org.hl7.fhir.r4.model.MedicationRequest()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestStatus.valueOf(
        status.value.name.replace("_", "")
      )
    )
    hapiValue.setStatusReason(statusReason.toHapi())
    hapiValue.setIntent(
      org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestIntent.valueOf(
        intent.value.name.replace("_", "")
      )
    )
    hapiValue.setCategory(categoryList.map { it.toHapi() })
    hapiValue.setPriority(
      org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestPriority.valueOf(
        priority.value.name.replace("_", "")
      )
    )
    hapiValue.setDoNotPerformElement(doNotPerform.toHapi())
    hapiValue.setReported(reported.medicationRequestReportedToHapi())
    hapiValue.setMedication(medication.medicationRequestMedicationToHapi())
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setEncounter(encounter.toHapi())
    hapiValue.setSupportingInformation(supportingInformationList.map { it.toHapi() })
    hapiValue.setAuthoredOnElement(authoredOn.toHapi())
    hapiValue.setRequester(requester.toHapi())
    hapiValue.setPerformer(performer.toHapi())
    hapiValue.setPerformerType(performerType.toHapi())
    hapiValue.setRecorder(recorder.toHapi())
    hapiValue.setReasonCode(reasonCodeList.map { it.toHapi() })
    hapiValue.setReasonReference(reasonReferenceList.map { it.toHapi() })
    hapiValue.setInstantiatesCanonical(instantiatesCanonicalList.map { it.toHapi() })
    hapiValue.setInstantiatesUri(instantiatesUriList.map { it.toHapi() })
    hapiValue.setBasedOn(basedOnList.map { it.toHapi() })
    hapiValue.setGroupIdentifier(groupIdentifier.toHapi())
    hapiValue.setCourseOfTherapyType(courseOfTherapyType.toHapi())
    hapiValue.setInsurance(insuranceList.map { it.toHapi() })
    hapiValue.setNote(noteList.map { it.toHapi() })
    hapiValue.setDosageInstruction(dosageInstructionList.map { it.toHapi() })
    hapiValue.setDispenseRequest(dispenseRequest.toHapi())
    hapiValue.setSubstitution(substitution.toHapi())
    hapiValue.setPriorPrescription(priorPrescription.toHapi())
    hapiValue.setDetectedIssue(detectedIssueList.map { it.toHapi() })
    hapiValue.setEventHistory(eventHistoryList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.MedicationRequest.toProto(): MedicationRequest {
    val protoValue =
      MedicationRequest.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setStatus(
          MedicationRequest.StatusCode.newBuilder()
            .setValue(
              MedicationrequestStatusCode.Value.valueOf(
                status.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setStatusReason(statusReason.toProto())
        .setIntent(
          MedicationRequest.IntentCode.newBuilder()
            .setValue(
              MedicationRequestIntentCode.Value.valueOf(
                intent.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .addAllCategory(category.map { it.toProto() })
        .setPriority(
          MedicationRequest.PriorityCode.newBuilder()
            .setValue(
              RequestPriorityCode.Value.valueOf(priority.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setDoNotPerform(doNotPerformElement.toProto())
        .setReported(reported.medicationRequestReportedToProto())
        .setMedication(medication.medicationRequestMedicationToProto())
        .setSubject(subject.toProto())
        .setEncounter(encounter.toProto())
        .addAllSupportingInformation(supportingInformation.map { it.toProto() })
        .setAuthoredOn(authoredOnElement.toProto())
        .setRequester(requester.toProto())
        .setPerformer(performer.toProto())
        .setPerformerType(performerType.toProto())
        .setRecorder(recorder.toProto())
        .addAllReasonCode(reasonCode.map { it.toProto() })
        .addAllReasonReference(reasonReference.map { it.toProto() })
        .addAllInstantiatesCanonical(instantiatesCanonical.map { it.toProto() })
        .addAllInstantiatesUri(instantiatesUri.map { it.toProto() })
        .addAllBasedOn(basedOn.map { it.toProto() })
        .setGroupIdentifier(groupIdentifier.toProto())
        .setCourseOfTherapyType(courseOfTherapyType.toProto())
        .addAllInsurance(insurance.map { it.toProto() })
        .addAllNote(note.map { it.toProto() })
        .addAllDosageInstruction(dosageInstruction.map { it.toProto() })
        .setDispenseRequest(dispenseRequest.toProto())
        .setSubstitution(substitution.toProto())
        .setPriorPrescription(priorPrescription.toProto())
        .addAllDetectedIssue(detectedIssue.map { it.toProto() })
        .addAllEventHistory(eventHistory.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestDispenseRequestComponent.toProto():
    MedicationRequest.DispenseRequest {
    val protoValue =
      MedicationRequest.DispenseRequest.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setInitialFill(initialFill.toProto())
        .setDispenseInterval(dispenseInterval.toProto())
        .setValidityPeriod(validityPeriod.toProto())
        .setNumberOfRepeatsAllowed(numberOfRepeatsAllowedElement.toProto())
        .setQuantity((quantity as SimpleQuantity).toProto())
        .setExpectedSupplyDuration(expectedSupplyDuration.toProto())
        .setPerformer(performer.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestDispenseRequestInitialFillComponent.toProto():
    MedicationRequest.DispenseRequest.InitialFill {
    val protoValue =
      MedicationRequest.DispenseRequest.InitialFill.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setQuantity((quantity as SimpleQuantity).toProto())
        .setDuration(duration.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestSubstitutionComponent.toProto():
    MedicationRequest.Substitution {
    val protoValue =
      MedicationRequest.Substitution.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setAllowed(allowed.medicationRequestSubstitutionAllowedToProto())
        .setReason(reason.toProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun MedicationRequest.DispenseRequest.toHapi():
    org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestDispenseRequestComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestDispenseRequestComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setInitialFill(initialFill.toHapi())
    hapiValue.setDispenseInterval(dispenseInterval.toHapi())
    hapiValue.setValidityPeriod(validityPeriod.toHapi())
    hapiValue.setNumberOfRepeatsAllowedElement(numberOfRepeatsAllowed.toHapi())
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setExpectedSupplyDuration(expectedSupplyDuration.toHapi())
    hapiValue.setPerformer(performer.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun MedicationRequest.DispenseRequest.InitialFill.toHapi():
    org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestDispenseRequestInitialFillComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestDispenseRequestInitialFillComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setQuantity(quantity.toHapi())
    hapiValue.setDuration(duration.toHapi())
    return hapiValue
  }

  @JvmStatic
  private fun MedicationRequest.Substitution.toHapi():
    org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestSubstitutionComponent {
    val hapiValue = org.hl7.fhir.r4.model.MedicationRequest.MedicationRequestSubstitutionComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setAllowed(allowed.medicationRequestSubstitutionAllowedToHapi())
    hapiValue.setReason(reason.toHapi())
    return hapiValue
  }
}
