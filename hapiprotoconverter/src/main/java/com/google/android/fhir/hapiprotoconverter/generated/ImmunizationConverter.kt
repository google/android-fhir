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
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Immunization
import com.google.fhir.r4.core.Immunization.ProtocolApplied
import com.google.fhir.r4.core.ImmunizationStatusCodesValueSet
import com.google.fhir.r4.core.PositiveInt
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.PositiveIntType
import org.hl7.fhir.r4.model.SimpleQuantity
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

object ImmunizationConverter {
  @JvmStatic
  private fun Immunization.OccurrenceX.immunizationOccurrenceToHapi(): Type {
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Immunization.occurrence[x]")
  }

  @JvmStatic
  private fun Type.immunizationOccurrenceToProto(): Immunization.OccurrenceX {
    val protoValue = Immunization.OccurrenceX.newBuilder()
    if (this is DateTimeType) {
      protoValue.dateTime = this.toProto()
    }
    if (this is StringType) {
      protoValue.stringValue = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Immunization.ProtocolApplied.DoseNumberX.immunizationProtocolAppliedDoseNumberToHapi():
    Type {
    if (this.positiveInt != PositiveInt.newBuilder().defaultInstanceForType) {
      return (this.positiveInt).toHapi()
    }
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Immunization.protocolApplied.doseNumber[x]")
  }

  @JvmStatic
  private fun Type.immunizationProtocolAppliedDoseNumberToProto():
    Immunization.ProtocolApplied.DoseNumberX {
    val protoValue = Immunization.ProtocolApplied.DoseNumberX.newBuilder()
    if (this is PositiveIntType) {
      protoValue.positiveInt = this.toProto()
    }
    if (this is StringType) {
      protoValue.stringValue = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Immunization.ProtocolApplied.SeriesDosesX.immunizationProtocolAppliedSeriesDosesToHapi():
    Type {
    if (this.positiveInt != PositiveInt.newBuilder().defaultInstanceForType) {
      return (this.positiveInt).toHapi()
    }
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Immunization.protocolApplied.seriesDoses[x]")
  }

  @JvmStatic
  private fun Type.immunizationProtocolAppliedSeriesDosesToProto():
    Immunization.ProtocolApplied.SeriesDosesX {
    val protoValue = Immunization.ProtocolApplied.SeriesDosesX.newBuilder()
    if (this is PositiveIntType) {
      protoValue.positiveInt = this.toProto()
    }
    if (this is StringType) {
      protoValue.stringValue = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun Immunization.toHapi(): org.hl7.fhir.r4.model.Immunization {
    val hapiValue = org.hl7.fhir.r4.model.Immunization()
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
    hapiValue.status =
      org.hl7.fhir.r4.model.Immunization.ImmunizationStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasStatusReason()) {
      hapiValue.statusReason = statusReason.toHapi()
    }
    if (hasVaccineCode()) {
      hapiValue.vaccineCode = vaccineCode.toHapi()
    }
    if (hasPatient()) {
      hapiValue.patient = patient.toHapi()
    }
    if (hasEncounter()) {
      hapiValue.encounter = encounter.toHapi()
    }
    if (hasOccurrence()) {
      hapiValue.occurrence = occurrence.immunizationOccurrenceToHapi()
    }
    if (hasRecorded()) {
      hapiValue.recordedElement = recorded.toHapi()
    }
    if (hasPrimarySource()) {
      hapiValue.primarySourceElement = primarySource.toHapi()
    }
    if (hasReportOrigin()) {
      hapiValue.reportOrigin = reportOrigin.toHapi()
    }
    if (hasLocation()) {
      hapiValue.location = location.toHapi()
    }
    if (hasManufacturer()) {
      hapiValue.manufacturer = manufacturer.toHapi()
    }
    if (hasLotNumber()) {
      hapiValue.lotNumberElement = lotNumber.toHapi()
    }
    if (hasExpirationDate()) {
      hapiValue.expirationDateElement = expirationDate.toHapi()
    }
    if (hasSite()) {
      hapiValue.site = site.toHapi()
    }
    if (hasRoute()) {
      hapiValue.route = route.toHapi()
    }
    if (hasDoseQuantity()) {
      hapiValue.doseQuantity = doseQuantity.toHapi()
    }
    if (performerCount > 0) {
      hapiValue.performer = performerList.map { it.toHapi() }
    }
    if (noteCount > 0) {
      hapiValue.note = noteList.map { it.toHapi() }
    }
    if (reasonCodeCount > 0) {
      hapiValue.reasonCode = reasonCodeList.map { it.toHapi() }
    }
    if (reasonReferenceCount > 0) {
      hapiValue.reasonReference = reasonReferenceList.map { it.toHapi() }
    }
    if (hasIsSubpotent()) {
      hapiValue.isSubpotentElement = isSubpotent.toHapi()
    }
    if (subpotentReasonCount > 0) {
      hapiValue.subpotentReason = subpotentReasonList.map { it.toHapi() }
    }
    if (educationCount > 0) {
      hapiValue.education = educationList.map { it.toHapi() }
    }
    if (programEligibilityCount > 0) {
      hapiValue.programEligibility = programEligibilityList.map { it.toHapi() }
    }
    if (hasFundingSource()) {
      hapiValue.fundingSource = fundingSource.toHapi()
    }
    if (reactionCount > 0) {
      hapiValue.reaction = reactionList.map { it.toHapi() }
    }
    if (protocolAppliedCount > 0) {
      hapiValue.protocolApplied = protocolAppliedList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.Immunization.toProto(): Immunization {
    val protoValue = Immunization.newBuilder().setId(Id.newBuilder().setValue(id))
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
    protoValue.status =
      Immunization.StatusCode.newBuilder()
        .setValue(
          ImmunizationStatusCodesValueSet.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasStatusReason()) {
      protoValue.statusReason = statusReason.toProto()
    }
    if (hasVaccineCode()) {
      protoValue.vaccineCode = vaccineCode.toProto()
    }
    if (hasPatient()) {
      protoValue.patient = patient.toProto()
    }
    if (hasEncounter()) {
      protoValue.encounter = encounter.toProto()
    }
    if (hasOccurrence()) {
      protoValue.occurrence = occurrence.immunizationOccurrenceToProto()
    }
    if (hasRecorded()) {
      protoValue.recorded = recordedElement.toProto()
    }
    if (hasPrimarySource()) {
      protoValue.primarySource = primarySourceElement.toProto()
    }
    if (hasReportOrigin()) {
      protoValue.reportOrigin = reportOrigin.toProto()
    }
    if (hasLocation()) {
      protoValue.location = location.toProto()
    }
    if (hasManufacturer()) {
      protoValue.manufacturer = manufacturer.toProto()
    }
    if (hasLotNumber()) {
      protoValue.lotNumber = lotNumberElement.toProto()
    }
    if (hasExpirationDate()) {
      protoValue.expirationDate = expirationDateElement.toProto()
    }
    if (hasSite()) {
      protoValue.site = site.toProto()
    }
    if (hasRoute()) {
      protoValue.route = route.toProto()
    }
    if (hasDoseQuantity()) {
      protoValue.doseQuantity = (doseQuantity as SimpleQuantity).toProto()
    }
    if (hasPerformer()) {
      protoValue.addAllPerformer(performer.map { it.toProto() })
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    if (hasReasonCode()) {
      protoValue.addAllReasonCode(reasonCode.map { it.toProto() })
    }
    if (hasReasonReference()) {
      protoValue.addAllReasonReference(reasonReference.map { it.toProto() })
    }
    if (hasIsSubpotent()) {
      protoValue.isSubpotent = isSubpotentElement.toProto()
    }
    if (hasSubpotentReason()) {
      protoValue.addAllSubpotentReason(subpotentReason.map { it.toProto() })
    }
    if (hasEducation()) {
      protoValue.addAllEducation(education.map { it.toProto() })
    }
    if (hasProgramEligibility()) {
      protoValue.addAllProgramEligibility(programEligibility.map { it.toProto() })
    }
    if (hasFundingSource()) {
      protoValue.fundingSource = fundingSource.toProto()
    }
    if (hasReaction()) {
      protoValue.addAllReaction(reaction.map { it.toProto() })
    }
    if (hasProtocolApplied()) {
      protoValue.addAllProtocolApplied(protocolApplied.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Immunization.ImmunizationPerformerComponent.toProto():
    Immunization.Performer {
    val protoValue = Immunization.Performer.newBuilder().setId(String.newBuilder().setValue(id))
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

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Immunization.ImmunizationEducationComponent.toProto():
    Immunization.Education {
    val protoValue = Immunization.Education.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDocumentType()) {
      protoValue.documentType = documentTypeElement.toProto()
    }
    if (hasReference()) {
      protoValue.reference = referenceElement.toProto()
    }
    if (hasPublicationDate()) {
      protoValue.publicationDate = publicationDateElement.toProto()
    }
    if (hasPresentationDate()) {
      protoValue.presentationDate = presentationDateElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Immunization.ImmunizationReactionComponent.toProto():
    Immunization.Reaction {
    val protoValue = Immunization.Reaction.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasDate()) {
      protoValue.date = dateElement.toProto()
    }
    if (hasDetail()) {
      protoValue.detail = detail.toProto()
    }
    if (hasReported()) {
      protoValue.reported = reportedElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Immunization.ImmunizationProtocolAppliedComponent.toProto():
    Immunization.ProtocolApplied {
    val protoValue =
      Immunization.ProtocolApplied.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSeries()) {
      protoValue.series = seriesElement.toProto()
    }
    if (hasAuthority()) {
      protoValue.authority = authority.toProto()
    }
    if (hasTargetDisease()) {
      protoValue.addAllTargetDisease(targetDisease.map { it.toProto() })
    }
    if (hasDoseNumber()) {
      protoValue.doseNumber = doseNumber.immunizationProtocolAppliedDoseNumberToProto()
    }
    if (hasSeriesDoses()) {
      protoValue.seriesDoses = seriesDoses.immunizationProtocolAppliedSeriesDosesToProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Immunization.Performer.toHapi():
    org.hl7.fhir.r4.model.Immunization.ImmunizationPerformerComponent {
    val hapiValue = org.hl7.fhir.r4.model.Immunization.ImmunizationPerformerComponent()
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

  @JvmStatic
  private fun Immunization.Education.toHapi():
    org.hl7.fhir.r4.model.Immunization.ImmunizationEducationComponent {
    val hapiValue = org.hl7.fhir.r4.model.Immunization.ImmunizationEducationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasDocumentType()) {
      hapiValue.documentTypeElement = documentType.toHapi()
    }
    if (hasReference()) {
      hapiValue.referenceElement = reference.toHapi()
    }
    if (hasPublicationDate()) {
      hapiValue.publicationDateElement = publicationDate.toHapi()
    }
    if (hasPresentationDate()) {
      hapiValue.presentationDateElement = presentationDate.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun Immunization.Reaction.toHapi():
    org.hl7.fhir.r4.model.Immunization.ImmunizationReactionComponent {
    val hapiValue = org.hl7.fhir.r4.model.Immunization.ImmunizationReactionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasDate()) {
      hapiValue.dateElement = date.toHapi()
    }
    if (hasDetail()) {
      hapiValue.detail = detail.toHapi()
    }
    if (hasReported()) {
      hapiValue.reportedElement = reported.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun Immunization.ProtocolApplied.toHapi():
    org.hl7.fhir.r4.model.Immunization.ImmunizationProtocolAppliedComponent {
    val hapiValue = org.hl7.fhir.r4.model.Immunization.ImmunizationProtocolAppliedComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasSeries()) {
      hapiValue.seriesElement = series.toHapi()
    }
    if (hasAuthority()) {
      hapiValue.authority = authority.toHapi()
    }
    if (targetDiseaseCount > 0) {
      hapiValue.targetDisease = targetDiseaseList.map { it.toHapi() }
    }
    if (hasDoseNumber()) {
      hapiValue.doseNumber = doseNumber.immunizationProtocolAppliedDoseNumberToHapi()
    }
    if (hasSeriesDoses()) {
      hapiValue.seriesDoses = seriesDoses.immunizationProtocolAppliedSeriesDosesToHapi()
    }
    return hapiValue
  }
}
