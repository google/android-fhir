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

public object AllergyIntoleranceConverter {
  @JvmStatic
  private fun AllergyIntolerance.OnsetX.allergyIntoleranceOnsetToHapi(): Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getAge() != Age.newBuilder().defaultInstanceForType) {
      return (this.getAge()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType) {
      return (this.getRange()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for AllergyIntolerance.onset[x]")
  }

  @JvmStatic
  private fun Type.allergyIntoleranceOnsetToProto(): AllergyIntolerance.OnsetX {
    val protoValue = AllergyIntolerance.OnsetX.newBuilder()
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Age) {
      protoValue.setAge(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun AllergyIntolerance.toHapi(): org.hl7.fhir.r4.model.AllergyIntolerance {
    val hapiValue = org.hl7.fhir.r4.model.AllergyIntolerance()
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
    if (hasClinicalStatus()) {
      hapiValue.setClinicalStatus(clinicalStatus.toHapi())
    }
    if (hasVerificationStatus()) {
      hapiValue.setVerificationStatus(verificationStatus.toHapi())
    }
    hapiValue.setType(
      org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceType.valueOf(
        type.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    categoryList.forEach {
      hapiValue.addCategory(
        org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceCategory.valueOf(
          it.value.name.hapiCodeCheck().replace("_", "")
        )
      )
    }
    hapiValue.setCriticality(
      org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceCriticality.valueOf(
        criticality.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasCode()) {
      hapiValue.setCode(code.toHapi())
    }
    if (hasPatient()) {
      hapiValue.setPatient(patient.toHapi())
    }
    if (hasEncounter()) {
      hapiValue.setEncounter(encounter.toHapi())
    }
    if (hasOnset()) {
      hapiValue.setOnset(onset.allergyIntoleranceOnsetToHapi())
    }
    if (hasRecordedDate()) {
      hapiValue.setRecordedDateElement(recordedDate.toHapi())
    }
    if (hasRecorder()) {
      hapiValue.setRecorder(recorder.toHapi())
    }
    if (hasAsserter()) {
      hapiValue.setAsserter(asserter.toHapi())
    }
    if (hasLastOccurrence()) {
      hapiValue.setLastOccurrenceElement(lastOccurrence.toHapi())
    }
    if (noteCount > 0) {
      hapiValue.setNote(noteList.map { it.toHapi() })
    }
    if (reactionCount > 0) {
      hapiValue.setReaction(reactionList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.AllergyIntolerance.toProto(): AllergyIntolerance {
    val protoValue = AllergyIntolerance.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasClinicalStatus()) {
      protoValue.setClinicalStatus(clinicalStatus.toProto())
    }
    if (hasVerificationStatus()) {
      protoValue.setVerificationStatus(verificationStatus.toProto())
    }
    protoValue.setType(
      AllergyIntolerance.TypeCode.newBuilder()
        .setValue(
          AllergyIntoleranceTypeCode.Value.valueOf(
            type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
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
    protoValue.setCriticality(
      AllergyIntolerance.CriticalityCode.newBuilder()
        .setValue(
          AllergyIntoleranceCriticalityCode.Value.valueOf(
            criticality.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasCode()) {
      protoValue.setCode(code.toProto())
    }
    if (hasPatient()) {
      protoValue.setPatient(patient.toProto())
    }
    if (hasEncounter()) {
      protoValue.setEncounter(encounter.toProto())
    }
    if (hasOnset()) {
      protoValue.setOnset(onset.allergyIntoleranceOnsetToProto())
    }
    if (hasRecordedDate()) {
      protoValue.setRecordedDate(recordedDateElement.toProto())
    }
    if (hasRecorder()) {
      protoValue.setRecorder(recorder.toProto())
    }
    if (hasAsserter()) {
      protoValue.setAsserter(asserter.toProto())
    }
    if (hasLastOccurrence()) {
      protoValue.setLastOccurrence(lastOccurrenceElement.toProto())
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
      protoValue.setSubstance(substance.toProto())
    }
    if (hasManifestation()) {
      protoValue.addAllManifestation(manifestation.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.setDescription(descriptionElement.toProto())
    }
    if (hasOnset()) {
      protoValue.setOnset(onsetElement.toProto())
    }
    protoValue.setSeverity(
      AllergyIntolerance.Reaction.SeverityCode.newBuilder()
        .setValue(
          AllergyIntoleranceSeverityCode.Value.valueOf(
            severity.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasExposureRoute()) {
      protoValue.setExposureRoute(exposureRoute.toProto())
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
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasSubstance()) {
      hapiValue.setSubstance(substance.toHapi())
    }
    if (manifestationCount > 0) {
      hapiValue.setManifestation(manifestationList.map { it.toHapi() })
    }
    if (hasDescription()) {
      hapiValue.setDescriptionElement(description.toHapi())
    }
    if (hasOnset()) {
      hapiValue.setOnsetElement(onset.toHapi())
    }
    hapiValue.setSeverity(
      org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceSeverity.valueOf(
        severity.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasExposureRoute()) {
      hapiValue.setExposureRoute(exposureRoute.toHapi())
    }
    if (noteCount > 0) {
      hapiValue.setNote(noteList.map { it.toHapi() })
    }
    return hapiValue
  }
}
