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
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Type

public object RiskAssessmentConverter {
  private fun RiskAssessment.OccurrenceX.riskAssessmentOccurrenceToHapi(): Type {
    if (this.getDateTime() != DateTime.newBuilder().defaultInstanceForType) {
      return (this.getDateTime()).toHapi()
    }
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for RiskAssessment.occurrence[x]")
  }

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

  private fun RiskAssessment.Prediction.WhenX.riskAssessmentPredictionWhenToHapi(): Type {
    if (this.getPeriod() != Period.newBuilder().defaultInstanceForType) {
      return (this.getPeriod()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType) {
      return (this.getRange()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for RiskAssessment.prediction.when[x]")
  }

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

  public fun RiskAssessment.toHapi(): org.hl7.fhir.r4.model.RiskAssessment {
    val hapiValue = org.hl7.fhir.r4.model.RiskAssessment()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setIdentifier(identifierList.map { it.toHapi() })
    hapiValue.setBasedOn(basedOn.toHapi())
    hapiValue.setParent(parent.toHapi())
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.RiskAssessment.RiskAssessmentStatus.valueOf(
        status.value.name.replace("_", "")
      )
    )
    hapiValue.setMethod(method.toHapi())
    hapiValue.setCode(code.toHapi())
    hapiValue.setSubject(subject.toHapi())
    hapiValue.setEncounter(encounter.toHapi())
    hapiValue.setOccurrence(occurrence.riskAssessmentOccurrenceToHapi())
    hapiValue.setCondition(condition.toHapi())
    hapiValue.setPerformer(performer.toHapi())
    hapiValue.setReasonCode(reasonCodeList.map { it.toHapi() })
    hapiValue.setReasonReference(reasonReferenceList.map { it.toHapi() })
    hapiValue.setBasis(basisList.map { it.toHapi() })
    hapiValue.setPrediction(predictionList.map { it.toHapi() })
    hapiValue.setMitigationElement(mitigation.toHapi())
    hapiValue.setNote(noteList.map { it.toHapi() })
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.RiskAssessment.toProto(): RiskAssessment {
    val protoValue =
      RiskAssessment.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setText(text.toProto())
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .addAllIdentifier(identifier.map { it.toProto() })
        .setBasedOn(basedOn.toProto())
        .setParent(parent.toProto())
        .setStatus(
          RiskAssessment.StatusCode.newBuilder()
            .setValue(
              ObservationStatusCode.Value.valueOf(status.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setMethod(method.toProto())
        .setCode(code.toProto())
        .setSubject(subject.toProto())
        .setEncounter(encounter.toProto())
        .setOccurrence(occurrence.riskAssessmentOccurrenceToProto())
        .setCondition(condition.toProto())
        .setPerformer(performer.toProto())
        .addAllReasonCode(reasonCode.map { it.toProto() })
        .addAllReasonReference(reasonReference.map { it.toProto() })
        .addAllBasis(basis.map { it.toProto() })
        .addAllPrediction(prediction.map { it.toProto() })
        .setMitigation(mitigationElement.toProto())
        .addAllNote(note.map { it.toProto() })
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.RiskAssessment.RiskAssessmentPredictionComponent.toProto():
    RiskAssessment.Prediction {
    val protoValue =
      RiskAssessment.Prediction.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setOutcome(outcome.toProto())
        .setProbability(probability.riskAssessmentPredictionProbabilityToProto())
        .setQualitativeRisk(qualitativeRisk.toProto())
        .setRelativeRisk(relativeRiskElement.toProto())
        .setWhen(`when`.riskAssessmentPredictionWhenToProto())
        .setRationale(rationaleElement.toProto())
        .build()
    return protoValue
  }

  private fun RiskAssessment.Prediction.toHapi():
    org.hl7.fhir.r4.model.RiskAssessment.RiskAssessmentPredictionComponent {
    val hapiValue = org.hl7.fhir.r4.model.RiskAssessment.RiskAssessmentPredictionComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setOutcome(outcome.toHapi())
    hapiValue.setProbability(probability.riskAssessmentPredictionProbabilityToHapi())
    hapiValue.setQualitativeRisk(qualitativeRisk.toHapi())
    hapiValue.setRelativeRiskElement(relativeRisk.toHapi())
    hapiValue.setWhen(`when`.riskAssessmentPredictionWhenToHapi())
    hapiValue.setRationaleElement(rationale.toHapi())
    return hapiValue
  }
}
