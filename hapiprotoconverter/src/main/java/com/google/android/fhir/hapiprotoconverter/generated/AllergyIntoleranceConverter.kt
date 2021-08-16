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

import com.google.android.fhir.hapiprotoconverter.generated.AgeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AgeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AnnotationConverter.toProto
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
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Age
import com.google.fhir.r4.core.AllergyIntolerance
import com.google.fhir.r4.core.AllergyIntolerance.Reaction
import com.google.fhir.r4.core.AllergyIntoleranceCategoryCode
import com.google.fhir.r4.core.AllergyIntoleranceCriticalityCode
import com.google.fhir.r4.core.AllergyIntoleranceSeverityCode
import com.google.fhir.r4.core.AllergyIntoleranceTypeCode
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.Range
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

object AllergyIntoleranceConverter {
  @JvmStatic
  private fun AllergyIntolerance.OnsetX.allergyIntoleranceOnsetToHapi(): Type {
    if (this.dateTime != DateTime.newBuilder().defaultInstanceForType) {
      return (this.dateTime).toHapi()
    }
    if (this.age != Age.newBuilder().defaultInstanceForType) {
      return (this.age).toHapi()
    }
    if (this.period != Period.newBuilder().defaultInstanceForType) {
      return (this.period).toHapi()
    }
    if (this.range != Range.newBuilder().defaultInstanceForType) {
      return (this.range).toHapi()
    }
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for AllergyIntolerance.onset[x]")
  }

  @JvmStatic
  private fun Type.allergyIntoleranceOnsetToProto(): AllergyIntolerance.OnsetX {
    val protoValue = AllergyIntolerance.OnsetX.newBuilder()
    if (this is DateTimeType) {
      protoValue.dateTime = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Age) {
      protoValue.age = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.period = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.range = this.toProto()
    }
    if (this is StringType) {
      protoValue.stringValue = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun AllergyIntolerance.toHapi(): org.hl7.fhir.r4.model.AllergyIntolerance {
    val hapiValue = org.hl7.fhir.r4.model.AllergyIntolerance()
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
    if (hasClinicalStatus()) {
      hapiValue.clinicalStatus = clinicalStatus.toHapi()
    }
    if (hasVerificationStatus()) {
      hapiValue.verificationStatus = verificationStatus.toHapi()
    }
    hapiValue.type =
      org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceType.valueOf(
        type.value.name.hapiCodeCheck().replace("_", "")
      )
    categoryList.forEach {
      hapiValue.addCategory(
        org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceCategory.valueOf(
          it.value.name.hapiCodeCheck().replace("_", "")
        )
      )
    }
    hapiValue.criticality =
      org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceCriticality.valueOf(
        criticality.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (hasPatient()) {
      hapiValue.patient = patient.toHapi()
    }
    if (hasEncounter()) {
      hapiValue.encounter = encounter.toHapi()
    }
    if (hasOnset()) {
      hapiValue.onset = onset.allergyIntoleranceOnsetToHapi()
    }
    if (hasRecordedDate()) {
      hapiValue.recordedDateElement = recordedDate.toHapi()
    }
    if (hasRecorder()) {
      hapiValue.recorder = recorder.toHapi()
    }
    if (hasAsserter()) {
      hapiValue.asserter = asserter.toHapi()
    }
    if (hasLastOccurrence()) {
      hapiValue.lastOccurrenceElement = lastOccurrence.toHapi()
    }
    if (noteCount > 0) {
      hapiValue.note = noteList.map { it.toHapi() }
    }
    if (reactionCount > 0) {
      hapiValue.reaction = reactionList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.AllergyIntolerance.toProto(): AllergyIntolerance {
    val protoValue = AllergyIntolerance.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasClinicalStatus()) {
      protoValue.clinicalStatus = clinicalStatus.toProto()
    }
    if (hasVerificationStatus()) {
      protoValue.verificationStatus = verificationStatus.toProto()
    }
    protoValue.type =
      AllergyIntolerance.TypeCode.newBuilder()
        .setValue(
          AllergyIntoleranceTypeCode.Value.valueOf(
            type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    protoValue.addAllCategory(
      category.map {
        AllergyIntolerance.CategoryCode.newBuilder()
          .setValue(
            AllergyIntoleranceCategoryCode.Value.valueOf(
              it.value.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
      }
    )
    protoValue.criticality =
      AllergyIntolerance.CriticalityCode.newBuilder()
        .setValue(
          AllergyIntoleranceCriticalityCode.Value.valueOf(
            criticality.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    if (hasPatient()) {
      protoValue.patient = patient.toProto()
    }
    if (hasEncounter()) {
      protoValue.encounter = encounter.toProto()
    }
    if (hasOnset()) {
      protoValue.onset = onset.allergyIntoleranceOnsetToProto()
    }
    if (hasRecordedDate()) {
      protoValue.recordedDate = recordedDateElement.toProto()
    }
    if (hasRecorder()) {
      protoValue.recorder = recorder.toProto()
    }
    if (hasAsserter()) {
      protoValue.asserter = asserter.toProto()
    }
    if (hasLastOccurrence()) {
      protoValue.lastOccurrence = lastOccurrenceElement.toProto()
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    if (hasReaction()) {
      protoValue.addAllReaction(reaction.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceReactionComponent.toProto():
    AllergyIntolerance.Reaction {
    val protoValue =
      AllergyIntolerance.Reaction.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSubstance()) {
      protoValue.substance = substance.toProto()
    }
    if (hasManifestation()) {
      protoValue.addAllManifestation(manifestation.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasOnset()) {
      protoValue.onset = onsetElement.toProto()
    }
    protoValue.severity =
      AllergyIntolerance.Reaction.SeverityCode.newBuilder()
        .setValue(
          AllergyIntoleranceSeverityCode.Value.valueOf(
            severity.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasExposureRoute()) {
      protoValue.exposureRoute = exposureRoute.toProto()
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun AllergyIntolerance.Reaction.toHapi():
    org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceReactionComponent {
    val hapiValue = org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceReactionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasSubstance()) {
      hapiValue.substance = substance.toHapi()
    }
    if (manifestationCount > 0) {
      hapiValue.manifestation = manifestationList.map { it.toHapi() }
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (hasOnset()) {
      hapiValue.onsetElement = onset.toHapi()
    }
    hapiValue.severity =
      org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceSeverity.valueOf(
        severity.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasExposureRoute()) {
      hapiValue.exposureRoute = exposureRoute.toHapi()
    }
    if (noteCount > 0) {
      hapiValue.note = noteList.map { it.toHapi() }
    }
    return hapiValue
  }
}
