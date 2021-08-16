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
import com.google.fhir.r4.core.Condition
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

object ConditionConverter {
  @JvmStatic
  private fun Condition.OnsetX.conditionOnsetToHapi(): Type {
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
    throw IllegalArgumentException("Invalid Type for Condition.onset[x]")
  }

  @JvmStatic
  private fun Type.conditionOnsetToProto(): Condition.OnsetX {
    val protoValue = Condition.OnsetX.newBuilder()
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
  private fun Condition.AbatementX.conditionAbatementToHapi(): Type {
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
    throw IllegalArgumentException("Invalid Type for Condition.abatement[x]")
  }

  @JvmStatic
  private fun Type.conditionAbatementToProto(): Condition.AbatementX {
    val protoValue = Condition.AbatementX.newBuilder()
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
  fun Condition.toHapi(): org.hl7.fhir.r4.model.Condition {
    val hapiValue = org.hl7.fhir.r4.model.Condition()
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
    if (categoryCount > 0) {
      hapiValue.category = categoryList.map { it.toHapi() }
    }
    if (hasSeverity()) {
      hapiValue.severity = severity.toHapi()
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (bodySiteCount > 0) {
      hapiValue.bodySite = bodySiteList.map { it.toHapi() }
    }
    if (hasSubject()) {
      hapiValue.subject = subject.toHapi()
    }
    if (hasEncounter()) {
      hapiValue.encounter = encounter.toHapi()
    }
    if (hasOnset()) {
      hapiValue.onset = onset.conditionOnsetToHapi()
    }
    if (hasAbatement()) {
      hapiValue.abatement = abatement.conditionAbatementToHapi()
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
    if (stageCount > 0) {
      hapiValue.stage = stageList.map { it.toHapi() }
    }
    if (evidenceCount > 0) {
      hapiValue.evidence = evidenceList.map { it.toHapi() }
    }
    if (noteCount > 0) {
      hapiValue.note = noteList.map { it.toHapi() }
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.Condition.toProto(): Condition {
    val protoValue = Condition.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasCategory()) {
      protoValue.addAllCategory(category.map { it.toProto() })
    }
    if (hasSeverity()) {
      protoValue.severity = severity.toProto()
    }
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    if (hasBodySite()) {
      protoValue.addAllBodySite(bodySite.map { it.toProto() })
    }
    if (hasSubject()) {
      protoValue.subject = subject.toProto()
    }
    if (hasEncounter()) {
      protoValue.encounter = encounter.toProto()
    }
    if (hasOnset()) {
      protoValue.onset = onset.conditionOnsetToProto()
    }
    if (hasAbatement()) {
      protoValue.abatement = abatement.conditionAbatementToProto()
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
    if (hasStage()) {
      protoValue.addAllStage(stage.map { it.toProto() })
    }
    if (hasEvidence()) {
      protoValue.addAllEvidence(evidence.map { it.toProto() })
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Condition.ConditionStageComponent.toProto(): Condition.Stage {
    val protoValue = Condition.Stage.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSummary()) {
      protoValue.summary = summary.toProto()
    }
    if (hasAssessment()) {
      protoValue.addAllAssessment(assessment.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Condition.ConditionEvidenceComponent.toProto():
    Condition.Evidence {
    val protoValue = Condition.Evidence.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.addAllCode(code.map { it.toProto() })
    }
    if (hasDetail()) {
      protoValue.addAllDetail(detail.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Condition.Stage.toHapi(): org.hl7.fhir.r4.model.Condition.ConditionStageComponent {
    val hapiValue = org.hl7.fhir.r4.model.Condition.ConditionStageComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasSummary()) {
      hapiValue.summary = summary.toHapi()
    }
    if (assessmentCount > 0) {
      hapiValue.assessment = assessmentList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun Condition.Evidence.toHapi():
    org.hl7.fhir.r4.model.Condition.ConditionEvidenceComponent {
    val hapiValue = org.hl7.fhir.r4.model.Condition.ConditionEvidenceComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (codeCount > 0) {
      hapiValue.code = codeList.map { it.toHapi() }
    }
    if (detailCount > 0) {
      hapiValue.detail = detailList.map { it.toHapi() }
    }
    return hapiValue
  }
}
