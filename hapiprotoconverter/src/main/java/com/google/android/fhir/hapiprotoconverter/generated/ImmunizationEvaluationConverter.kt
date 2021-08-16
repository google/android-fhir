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
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.ImmunizationEvaluation
import com.google.fhir.r4.core.ImmunizationEvaluationStatusCodesValueSet
import com.google.fhir.r4.core.PositiveInt
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.PositiveIntType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

public object ImmunizationEvaluationConverter {
  @JvmStatic
  private fun ImmunizationEvaluation.DoseNumberX.immunizationEvaluationDoseNumberToHapi(): Type {
    if (this.getPositiveInt() != PositiveInt.newBuilder().defaultInstanceForType) {
      return (this.getPositiveInt()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ImmunizationEvaluation.doseNumber[x]")
  }

  @JvmStatic
  private fun Type.immunizationEvaluationDoseNumberToProto(): ImmunizationEvaluation.DoseNumberX {
    val protoValue = ImmunizationEvaluation.DoseNumberX.newBuilder()
    if (this is PositiveIntType) {
      protoValue.setPositiveInt(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ImmunizationEvaluation.SeriesDosesX.immunizationEvaluationSeriesDosesToHapi(): Type {
    if (this.getPositiveInt() != PositiveInt.newBuilder().defaultInstanceForType) {
      return (this.getPositiveInt()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ImmunizationEvaluation.seriesDoses[x]")
  }

  @JvmStatic
  private fun Type.immunizationEvaluationSeriesDosesToProto(): ImmunizationEvaluation.SeriesDosesX {
    val protoValue = ImmunizationEvaluation.SeriesDosesX.newBuilder()
    if (this is PositiveIntType) {
      protoValue.setPositiveInt(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun ImmunizationEvaluation.toHapi(): org.hl7.fhir.r4.model.ImmunizationEvaluation {
    val hapiValue = org.hl7.fhir.r4.model.ImmunizationEvaluation()
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
    hapiValue.setStatus(
      org.hl7.fhir.r4.model.ImmunizationEvaluation.ImmunizationEvaluationStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasPatient()) {
      hapiValue.setPatient(patient.toHapi())
    }
    if (hasDate()) {
      hapiValue.setDateElement(date.toHapi())
    }
    if (hasAuthority()) {
      hapiValue.setAuthority(authority.toHapi())
    }
    if (hasTargetDisease()) {
      hapiValue.setTargetDisease(targetDisease.toHapi())
    }
    if (hasImmunizationEvent()) {
      hapiValue.setImmunizationEvent(immunizationEvent.toHapi())
    }
    if (hasDoseStatus()) {
      hapiValue.setDoseStatus(doseStatus.toHapi())
    }
    if (doseStatusReasonCount > 0) {
      hapiValue.setDoseStatusReason(doseStatusReasonList.map { it.toHapi() })
    }
    if (hasDescription()) {
      hapiValue.setDescriptionElement(description.toHapi())
    }
    if (hasSeries()) {
      hapiValue.setSeriesElement(series.toHapi())
    }
    if (hasDoseNumber()) {
      hapiValue.setDoseNumber(doseNumber.immunizationEvaluationDoseNumberToHapi())
    }
    if (hasSeriesDoses()) {
      hapiValue.setSeriesDoses(seriesDoses.immunizationEvaluationSeriesDosesToHapi())
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.ImmunizationEvaluation.toProto(): ImmunizationEvaluation {
    val protoValue = ImmunizationEvaluation.newBuilder().setId(Id.newBuilder().setValue(id))
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
    protoValue.setStatus(
      ImmunizationEvaluation.StatusCode.newBuilder()
        .setValue(
          ImmunizationEvaluationStatusCodesValueSet.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasPatient()) {
      protoValue.setPatient(patient.toProto())
    }
    if (hasDate()) {
      protoValue.setDate(dateElement.toProto())
    }
    if (hasAuthority()) {
      protoValue.setAuthority(authority.toProto())
    }
    if (hasTargetDisease()) {
      protoValue.setTargetDisease(targetDisease.toProto())
    }
    if (hasImmunizationEvent()) {
      protoValue.setImmunizationEvent(immunizationEvent.toProto())
    }
    if (hasDoseStatus()) {
      protoValue.setDoseStatus(doseStatus.toProto())
    }
    if (hasDoseStatusReason()) {
      protoValue.addAllDoseStatusReason(doseStatusReason.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.setDescription(descriptionElement.toProto())
    }
    if (hasSeries()) {
      protoValue.setSeries(seriesElement.toProto())
    }
    if (hasDoseNumber()) {
      protoValue.setDoseNumber(doseNumber.immunizationEvaluationDoseNumberToProto())
    }
    if (hasSeriesDoses()) {
      protoValue.setSeriesDoses(seriesDoses.immunizationEvaluationSeriesDosesToProto())
    }
    return protoValue.build()
  }
}
