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
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.ObservationStatusCode
import com.google.fhir.r4.core.RiskAssessment
import com.google.fhir.r4.core.RiskAssessment.Prediction
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Range
import org.hl7.fhir.r4.model.Type

object RiskAssessmentConverter {
  private fun RiskAssessment.OccurrenceX.riskAssessmentOccurrenceToHapi(): Type {
    if (hasDateTime()) {
      return (this.dateTime).toHapi()
    }
    if (hasPeriod()) {
      return (this.period).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for RiskAssessment.occurrence[x]")
  }

  private fun Type.riskAssessmentOccurrenceToProto(): RiskAssessment.OccurrenceX {
    val protoValue = RiskAssessment.OccurrenceX.newBuilder()
    if (this is DateTimeType) {
      protoValue.dateTime = this.toProto()
    }
    if (this is Period) {
      protoValue.period = this.toProto()
    }
    return protoValue.build()
  }

  private fun RiskAssessment.Prediction.ProbabilityX.riskAssessmentPredictionProbabilityToHapi():
    Type {
    if (hasDecimal()) {
      return (this.decimal).toHapi()
    }
    if (hasRange()) {
      return (this.range).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for RiskAssessment.prediction.probability[x]")
  }

  private fun Type.riskAssessmentPredictionProbabilityToProto():
    RiskAssessment.Prediction.ProbabilityX {
    val protoValue = RiskAssessment.Prediction.ProbabilityX.newBuilder()
    if (this is DecimalType) {
      protoValue.decimal = this.toProto()
    }
    if (this is Range) {
      protoValue.range = this.toProto()
    }
    return protoValue.build()
  }

  private fun RiskAssessment.Prediction.WhenX.riskAssessmentPredictionWhenToHapi(): Type {
    if (hasPeriod()) {
      return (this.period).toHapi()
    }
    if (hasRange()) {
      return (this.range).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for RiskAssessment.prediction.when[x]")
  }

  private fun Type.riskAssessmentPredictionWhenToProto(): RiskAssessment.Prediction.WhenX {
    val protoValue = RiskAssessment.Prediction.WhenX.newBuilder()
    if (this is Period) {
      protoValue.period = this.toProto()
    }
    if (this is Range) {
      protoValue.range = this.toProto()
    }
    return protoValue.build()
  }

  fun RiskAssessment.toHapi(): org.hl7.fhir.r4.model.RiskAssessment {
    val hapiValue = org.hl7.fhir.r4.model.RiskAssessment()
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
    if (hasBasedOn()) {
      hapiValue.basedOn = basedOn.toHapi()
    }
    if (hasParent()) {
      hapiValue.parent = parent.toHapi()
    }
    if (hasStatus()) {
      hapiValue.status =
        org.hl7.fhir.r4.model.RiskAssessment.RiskAssessmentStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
        )
    }
    if (hasMethod()) {
      hapiValue.method = method.toHapi()
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (hasSubject()) {
      hapiValue.subject = subject.toHapi()
    }
    if (hasEncounter()) {
      hapiValue.encounter = encounter.toHapi()
    }
    if (hasOccurrence()) {
      hapiValue.occurrence = occurrence.riskAssessmentOccurrenceToHapi()
    }
    if (hasCondition()) {
      hapiValue.condition = condition.toHapi()
    }
    if (hasPerformer()) {
      hapiValue.performer = performer.toHapi()
    }
    if (reasonCodeCount > 0) {
      hapiValue.reasonCode = reasonCodeList.map { it.toHapi() }
    }
    if (reasonReferenceCount > 0) {
      hapiValue.reasonReference = reasonReferenceList.map { it.toHapi() }
    }
    if (basisCount > 0) {
      hapiValue.basis = basisList.map { it.toHapi() }
    }
    if (predictionCount > 0) {
      hapiValue.prediction = predictionList.map { it.toHapi() }
    }
    if (hasMitigation()) {
      hapiValue.mitigationElement = mitigation.toHapi()
    }
    if (noteCount > 0) {
      hapiValue.note = noteList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.RiskAssessment.toProto(): RiskAssessment {
    val protoValue = RiskAssessment.newBuilder()
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
    if (hasBasedOn()) {
      protoValue.basedOn = basedOn.toProto()
    }
    if (hasParent()) {
      protoValue.parent = parent.toProto()
    }
    if (hasStatus()) {
      protoValue.status =
        RiskAssessment.StatusCode.newBuilder()
          .setValue(
            ObservationStatusCode.Value.valueOf(
              status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
    }
    if (hasMethod()) {
      protoValue.method = method.toProto()
    }
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    if (hasSubject()) {
      protoValue.subject = subject.toProto()
    }
    if (hasEncounter()) {
      protoValue.encounter = encounter.toProto()
    }
    if (hasOccurrence()) {
      protoValue.occurrence = occurrence.riskAssessmentOccurrenceToProto()
    }
    if (hasCondition()) {
      protoValue.condition = condition.toProto()
    }
    if (hasPerformer()) {
      protoValue.performer = performer.toProto()
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
      protoValue.mitigation = mitigationElement.toProto()
    }
    if (hasNote()) {
      protoValue.addAllNote(note.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.RiskAssessment.RiskAssessmentPredictionComponent.toProto():
    RiskAssessment.Prediction {
    val protoValue = RiskAssessment.Prediction.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasOutcome()) {
      protoValue.outcome = outcome.toProto()
    }
    if (hasProbability()) {
      protoValue.probability = probability.riskAssessmentPredictionProbabilityToProto()
    }
    if (hasQualitativeRisk()) {
      protoValue.qualitativeRisk = qualitativeRisk.toProto()
    }
    if (hasRelativeRisk()) {
      protoValue.relativeRisk = relativeRiskElement.toProto()
    }
    if (hasWhen()) {
      protoValue.setWhen(`when`.riskAssessmentPredictionWhenToProto())
    }
    if (hasRationale()) {
      protoValue.rationale = rationaleElement.toProto()
    }
    return protoValue.build()
  }

  private fun RiskAssessment.Prediction.toHapi():
    org.hl7.fhir.r4.model.RiskAssessment.RiskAssessmentPredictionComponent {
    val hapiValue = org.hl7.fhir.r4.model.RiskAssessment.RiskAssessmentPredictionComponent()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasOutcome()) {
      hapiValue.outcome = outcome.toHapi()
    }
    if (hasProbability()) {
      hapiValue.probability = probability.riskAssessmentPredictionProbabilityToHapi()
    }
    if (hasQualitativeRisk()) {
      hapiValue.qualitativeRisk = qualitativeRisk.toHapi()
    }
    if (hasRelativeRisk()) {
      hapiValue.relativeRiskElement = relativeRisk.toHapi()
    }
    if (hasWhen()) {
      hapiValue.setWhen(`when`.riskAssessmentPredictionWhenToHapi())
    }
    if (hasRationale()) {
      hapiValue.rationaleElement = rationale.toHapi()
    }
    return hapiValue
  }
}
