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
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toProto
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
import com.google.fhir.r4.core.DateTime
import com.google.fhir.r4.core.Decimal
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.ObservationStatusCode
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.Range
import com.google.fhir.r4.core.RiskAssessment
import com.google.fhir.r4.core.RiskAssessment.Prediction
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Type

public object RiskAssessmentConverter {
  @JvmStatic
  private fun RiskAssessment.OccurrenceX.riskAssessmentOccurrenceToHapi(): Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for RiskAssessment.occurrence[x]")
  }

  @JvmStatic
  private fun Type.riskAssessmentOccurrenceToProto(): RiskAssessment.OccurrenceX {
    val protoValue = RiskAssessment.OccurrenceX.newBuilder()
    if (this is DateTimeType) {
      protoValue.setDateTime(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun RiskAssessment.Prediction.ProbabilityX.riskAssessmentPredictionProbabilityToHapi():
    Type {
    if (this.getDecimal() != Decimal.newBuilder().defaultInstanceForType) {
      return (this.getDecimal()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType) {
      return (this.getRange()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for RiskAssessment.prediction.probability[x]")
  }

  @JvmStatic
  private fun Type.riskAssessmentPredictionProbabilityToProto():
    RiskAssessment.Prediction.ProbabilityX {
    val protoValue = RiskAssessment.Prediction.ProbabilityX.newBuilder()
    if (this is DecimalType) {
      protoValue.setDecimal(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun RiskAssessment.Prediction.WhenX.riskAssessmentPredictionWhenToHapi(): Type {
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType) {
      return (this.getRange()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for RiskAssessment.prediction.when[x]")
  }

  @JvmStatic
  private fun Type.riskAssessmentPredictionWhenToProto(): RiskAssessment.Prediction.WhenX {
    val protoValue = RiskAssessment.Prediction.WhenX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Period) {
      protoValue.setPeriod(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun RiskAssessment.toHapi(): org.hl7.fhir.r4.model.RiskAssessment {
    val hapiValue = org.hl7.fhir.r4.model.RiskAssessment()
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
    if (hasBasedOn()) {
      hapiValue.setBasedOn(basedOn.toHapi())
    }
    if (hasParent()) {
      hapiValue.setParent(parent.toHapi())
    }
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.RiskAssessment.RiskAssessmentStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasMethod()) {
      hapiValue.setMethod(method.toHapi())
    }
    if (hasCode()) {
      hapiValue.setCode(code.toHapi())
    }
    if (hasSubject()) {
      hapiValue.setSubject(subject.toHapi())
    }
    if (hasEncounter()) {
      hapiValue.setEncounter(encounter.toHapi())
    }
    if (hasOccurrence()) {
      hapiValue.setOccurrence(occurrence.riskAssessmentOccurrenceToHapi())
    }
    if (hasCondition()) {
      hapiValue.setCondition(condition.toHapi())
    }
    if (hasPerformer()) {
      hapiValue.setPerformer(performer.toHapi())
    }
    if (reasonCodeCount > 0) {
      hapiValue.setReasonCode(reasonCodeList.map { it.toHapi() })
    }
    if (reasonReferenceCount > 0) {
      hapiValue.setReasonReference(reasonReferenceList.map { it.toHapi() })
    }
    if (basisCount > 0) {
      hapiValue.setBasis(basisList.map { it.toHapi() })
    }
    if (predictionCount > 0) {
      hapiValue.setPrediction(predictionList.map { it.toHapi() })
    }
    if (hasMitigation()) {
      hapiValue.setMitigationElement(mitigation.toHapi())
    }
    if (noteCount > 0) {
      hapiValue.setNote(noteList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.RiskAssessment.toProto(): RiskAssessment {
    val protoValue = RiskAssessment.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasBasedOn()) {
      protoValue.setBasedOn(basedOn.toProto())
    }
    if (hasParent()) {
      protoValue.setParent(parent.toProto())
    }
    protoValue.setStatus(
      RiskAssessment.StatusCode.newBuilder()
        .setValue(
          ObservationStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasMethod()) {
      protoValue.setMethod(method.toProto())
    }
    if (hasCode()) {
      protoValue.setCode(code.toProto())
    }
    if (hasSubject()) {
      protoValue.setSubject(subject.toProto())
    }
    if (hasEncounter()) {
      protoValue.setEncounter(encounter.toProto())
    }
    if (hasOccurrence()) {
      protoValue.setOccurrence(occurrence.riskAssessmentOccurrenceToProto())
    }
    if (hasCondition()) {
      protoValue.setCondition(condition.toProto())
    }
    if (hasPerformer()) {
      protoValue.setPerformer(performer.toProto())
    }
    if (hasReasonCode()) {
      protoValue.addAllReasonCode(reasonCode.map { it.toProto() })
    }
    if (hasReasonReference()) {
      protoValue.addAllReasonReference(reasonReference.map { it.toProto() })
    }
    if (hasBasis()) {
      protoValue.addAllBasis(basis.map { it.toProto() })
    }
    if (hasPrediction()) {
      protoValue.addAllPrediction(prediction.map { it.toProto() })
    }
    if (hasMitigation()) {
      protoValue.setMitigation(mitigationElement.toProto())
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.RiskAssessment.RiskAssessmentPredictionComponent.toProto():
    RiskAssessment.Prediction {
    val protoValue = RiskAssessment.Prediction.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasOutcome()) {
      protoValue.setOutcome(outcome.toProto())
    }
    if (hasProbability()) {
      protoValue.setProbability(probability.riskAssessmentPredictionProbabilityToProto())
    }
    if (hasQualitativeRisk()) {
      protoValue.setQualitativeRisk(qualitativeRisk.toProto())
    }
    if (hasRelativeRisk()) {
      protoValue.setRelativeRisk(relativeRiskElement.toProto())
    }
    if (hasWhen()) {
      protoValue.setWhen(`when`.riskAssessmentPredictionWhenToProto())
    }
    if (hasRationale()) {
      protoValue.setRationale(rationaleElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun RiskAssessment.Prediction.toHapi():
    org.hl7.fhir.r4.model.RiskAssessment.RiskAssessmentPredictionComponent {
    val hapiValue = org.hl7.fhir.r4.model.RiskAssessment.RiskAssessmentPredictionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasOutcome()) {
      hapiValue.setOutcome(outcome.toHapi())
    }
    if (hasProbability()) {
      hapiValue.setProbability(probability.riskAssessmentPredictionProbabilityToHapi())
    }
    if (hasQualitativeRisk()) {
      hapiValue.setQualitativeRisk(qualitativeRisk.toHapi())
    }
    if (hasRelativeRisk()) {
      hapiValue.setRelativeRiskElement(relativeRisk.toHapi())
    }
    if (hasWhen()) {
      hapiValue.setWhen(`when`.riskAssessmentPredictionWhenToHapi())
    }
    if (hasRationale()) {
      hapiValue.setRationaleElement(rationale.toHapi())
    }
    return hapiValue
  }
}
