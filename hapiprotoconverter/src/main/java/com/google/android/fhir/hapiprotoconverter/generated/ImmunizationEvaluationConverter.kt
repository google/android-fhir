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

object ImmunizationEvaluationConverter {
  @JvmStatic
  private fun ImmunizationEvaluation.DoseNumberX.immunizationEvaluationDoseNumberToHapi(): Type {
    if (this.positiveInt != PositiveInt.newBuilder().defaultInstanceForType) {
      return (this.positiveInt).toHapi()
    }
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ImmunizationEvaluation.doseNumber[x]")
  }

  @JvmStatic
  private fun Type.immunizationEvaluationDoseNumberToProto(): ImmunizationEvaluation.DoseNumberX {
    val protoValue = ImmunizationEvaluation.DoseNumberX.newBuilder()
    if (this is PositiveIntType) {
        protoValue.positiveInt = this.toProto()
    }
    if (this is StringType) {
        protoValue.stringValue = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ImmunizationEvaluation.SeriesDosesX.immunizationEvaluationSeriesDosesToHapi(): Type {
    if (this.positiveInt != PositiveInt.newBuilder().defaultInstanceForType) {
      return (this.positiveInt).toHapi()
    }
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ImmunizationEvaluation.seriesDoses[x]")
  }

  @JvmStatic
  private fun Type.immunizationEvaluationSeriesDosesToProto(): ImmunizationEvaluation.SeriesDosesX {
    val protoValue = ImmunizationEvaluation.SeriesDosesX.newBuilder()
    if (this is PositiveIntType) {
        protoValue.positiveInt = this.toProto()
    }
    if (this is StringType) {
        protoValue.stringValue = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun ImmunizationEvaluation.toHapi(): org.hl7.fhir.r4.model.ImmunizationEvaluation {
    val hapiValue = org.hl7.fhir.r4.model.ImmunizationEvaluation()
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
      hapiValue.status = org.hl7.fhir.r4.model.ImmunizationEvaluation.ImmunizationEvaluationStatus.valueOf(
          status.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasPatient()) {
        hapiValue.patient = patient.toHapi()
    }
    if (hasDate()) {
        hapiValue.dateElement = date.toHapi()
    }
    if (hasAuthority()) {
        hapiValue.authority = authority.toHapi()
    }
    if (hasTargetDisease()) {
        hapiValue.targetDisease = targetDisease.toHapi()
    }
    if (hasImmunizationEvent()) {
        hapiValue.immunizationEvent = immunizationEvent.toHapi()
    }
    if (hasDoseStatus()) {
        hapiValue.doseStatus = doseStatus.toHapi()
    }
    if (doseStatusReasonCount > 0) {
        hapiValue.doseStatusReason = doseStatusReasonList.map { it.toHapi() }
    }
    if (hasDescription()) {
        hapiValue.descriptionElement = description.toHapi()
    }
    if (hasSeries()) {
        hapiValue.seriesElement = series.toHapi()
    }
    if (hasDoseNumber()) {
        hapiValue.doseNumber = doseNumber.immunizationEvaluationDoseNumberToHapi()
    }
    if (hasSeriesDoses()) {
        hapiValue.seriesDoses = seriesDoses.immunizationEvaluationSeriesDosesToHapi()
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.ImmunizationEvaluation.toProto(): ImmunizationEvaluation {
    val protoValue = ImmunizationEvaluation.newBuilder().setId(Id.newBuilder().setValue(id))
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
      protoValue.status = ImmunizationEvaluation.StatusCode.newBuilder()
          .setValue(
              ImmunizationEvaluationStatusCodesValueSet.Value.valueOf(
                  status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasPatient()) {
        protoValue.patient = patient.toProto()
    }
    if (hasDate()) {
        protoValue.date = dateElement.toProto()
    }
    if (hasAuthority()) {
        protoValue.authority = authority.toProto()
    }
    if (hasTargetDisease()) {
        protoValue.targetDisease = targetDisease.toProto()
    }
    if (hasImmunizationEvent()) {
        protoValue.immunizationEvent = immunizationEvent.toProto()
    }
    if (hasDoseStatus()) {
        protoValue.doseStatus = doseStatus.toProto()
    }
    if (hasDoseStatusReason()) {
      protoValue.addAllDoseStatusReason(doseStatusReason.map { it.toProto() })
    }
    if (hasDescription()) {
        protoValue.description = descriptionElement.toProto()
    }
    if (hasSeries()) {
        protoValue.series = seriesElement.toProto()
    }
    if (hasDoseNumber()) {
        protoValue.doseNumber = doseNumber.immunizationEvaluationDoseNumberToProto()
    }
    if (hasSeriesDoses()) {
        protoValue.seriesDoses = seriesDoses.immunizationEvaluationSeriesDosesToProto()
    }
    return protoValue.build()
  }
}
