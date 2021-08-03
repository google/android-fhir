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
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
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
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setClinicalStatus(clinicalStatus.toHapi())
    hapiValue.setVerificationStatus(verificationStatus.toHapi())
    hapiValue.setType(
      org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceType.valueOf(
        type.value.name.replace("_", "")
      )
    )
    categoryList.forEach {
      hapiValue.addCategory(
        org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceCategory.valueOf(
          it.value.name.replace("_", "")
        )
      )
    }
    hapiValue.setCriticality(
      org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceCriticality.valueOf(
        criticality.value.name.replace("_", "")
      )
    )
    hapiValue.setCode(code.toHapi())
    hapiValue.setPatient(patient.toHapi())
    hapiValue.setEncounter(encounter.toHapi())
    hapiValue.setOnset(onset.allergyIntoleranceOnsetToHapi())
    hapiValue.setRecordedDateElement(recordedDate.toHapi())
    hapiValue.setRecorder(recorder.toHapi())
    hapiValue.setAsserter(asserter.toHapi())
    hapiValue.setLastOccurrenceElement(lastOccurrence.toHapi())
    hapiValue.setNote(noteList.map { it.toHapi() })
    hapiValue.setReaction(reactionList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.AllergyIntolerance.toProto(): AllergyIntolerance {
    val protoValue =
      AllergyIntolerance.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setLanguage(languageElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setClinicalStatus(clinicalStatus.toProto())
        .setVerificationStatus(verificationStatus.toProto())
        .setType(
          AllergyIntolerance.TypeCode.newBuilder()
            .setValue(
              AllergyIntoleranceTypeCode.Value.valueOf(
                type.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .addAllCategory(
          category.map {
            AllergyIntolerance.CategoryCode.newBuilder()
              .setValue(
                AllergyIntoleranceCategoryCode.Value.valueOf(
                  it.value.toCode().replace("-", "_").toUpperCase()
                )
              )
              .build()
          }
        )
        .setCriticality(
          AllergyIntolerance.CriticalityCode.newBuilder()
            .setValue(
              AllergyIntoleranceCriticalityCode.Value.valueOf(
                criticality.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setCode(code.toProto())
        .setPatient(patient.toProto())
        .setEncounter(encounter.toProto())
        .setOnset(onset.allergyIntoleranceOnsetToProto())
        .setRecordedDate(recordedDateElement.toProto())
        .setRecorder(recorder.toProto())
        .setAsserter(asserter.toProto())
        .setLastOccurrence(lastOccurrenceElement.toProto())
        .addAllNote(note.map { it.toProto() })
        .addAllReaction(reaction.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceReactionComponent.toProto():
    AllergyIntolerance.Reaction {
    val protoValue =
      AllergyIntolerance.Reaction.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setSubstance(substance.toProto())
        .addAllManifestation(manifestation.map { it.toProto() })
        .setDescription(descriptionElement.toProto())
        .setOnset(onsetElement.toProto())
        .setSeverity(
          AllergyIntolerance.Reaction.SeverityCode.newBuilder()
            .setValue(
              AllergyIntoleranceSeverityCode.Value.valueOf(
                severity.toCode().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setExposureRoute(exposureRoute.toProto())
        .addAllNote(note.map { it.toProto() })
        .build()
    return protoValue
  }

  @JvmStatic
  private fun AllergyIntolerance.Reaction.toHapi():
    org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceReactionComponent {
    val hapiValue = org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceReactionComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setSubstance(substance.toHapi())
    hapiValue.setManifestation(manifestationList.map { it.toHapi() })
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setOnsetElement(onset.toHapi())
    hapiValue.setSeverity(
      org.hl7.fhir.r4.model.AllergyIntolerance.AllergyIntoleranceSeverity.valueOf(
        severity.value.name.replace("_", "")
      )
    )
    hapiValue.setExposureRoute(exposureRoute.toHapi())
    hapiValue.setNote(noteList.map { it.toHapi() })
    return hapiValue
  }
}
