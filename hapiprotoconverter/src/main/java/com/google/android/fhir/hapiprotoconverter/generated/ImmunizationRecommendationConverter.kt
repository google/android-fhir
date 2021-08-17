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
import com.google.fhir.r4.core.ImmunizationRecommendation
import com.google.fhir.r4.core.ImmunizationRecommendation.Recommendation
import com.google.fhir.r4.core.PositiveInt
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.PositiveIntType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

object ImmunizationRecommendationConverter {
  private fun ImmunizationRecommendation.Recommendation.DoseNumberX.immunizationRecommendationRecommendationDoseNumberToHapi():
    Type {
    if (this.positiveInt != PositiveInt.newBuilder().defaultInstanceForType) {
      return (this.positiveInt).toHapi()
    }
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for ImmunizationRecommendation.recommendation.doseNumber[x]"
    )
  }

  private fun Type.immunizationRecommendationRecommendationDoseNumberToProto():
    ImmunizationRecommendation.Recommendation.DoseNumberX {
    val protoValue = ImmunizationRecommendation.Recommendation.DoseNumberX.newBuilder()
    if (this is PositiveIntType) {
      protoValue.positiveInt = this.toProto()
    }
    if (this is StringType) {
      protoValue.stringValue = this.toProto()
    }
    return protoValue.build()
  }

  private fun ImmunizationRecommendation.Recommendation.SeriesDosesX.immunizationRecommendationRecommendationSeriesDosesToHapi():
    Type {
    if (this.positiveInt != PositiveInt.newBuilder().defaultInstanceForType) {
      return (this.positiveInt).toHapi()
    }
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for ImmunizationRecommendation.recommendation.seriesDoses[x]"
    )
  }

  private fun Type.immunizationRecommendationRecommendationSeriesDosesToProto():
    ImmunizationRecommendation.Recommendation.SeriesDosesX {
    val protoValue = ImmunizationRecommendation.Recommendation.SeriesDosesX.newBuilder()
    if (this is PositiveIntType) {
      protoValue.positiveInt = this.toProto()
    }
    if (this is StringType) {
      protoValue.stringValue = this.toProto()
    }
    return protoValue.build()
  }

  fun ImmunizationRecommendation.toHapi(): org.hl7.fhir.r4.model.ImmunizationRecommendation {
    val hapiValue = org.hl7.fhir.r4.model.ImmunizationRecommendation()
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
    if (hasPatient()) {
      hapiValue.patient = patient.toHapi()
    }
    if (hasDate()) {
      hapiValue.dateElement = date.toHapi()
    }
    if (hasAuthority()) {
      hapiValue.authority = authority.toHapi()
    }
    if (recommendationCount > 0) {
      hapiValue.recommendation = recommendationList.map { it.toHapi() }
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.ImmunizationRecommendation.toProto(): ImmunizationRecommendation {
    val protoValue = ImmunizationRecommendation.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasPatient()) {
      protoValue.patient = patient.toProto()
    }
    if (hasDate()) {
      protoValue.date = dateElement.toProto()
    }
    if (hasAuthority()) {
      protoValue.authority = authority.toProto()
    }
    if (hasRecommendation()) {
      protoValue.addAllRecommendation(recommendation.map { it.toProto() })
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.ImmunizationRecommendation.ImmunizationRecommendationRecommendationComponent.toProto():
    ImmunizationRecommendation.Recommendation {
    val protoValue =
      ImmunizationRecommendation.Recommendation.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasVaccineCode()) {
      protoValue.addAllVaccineCode(vaccineCode.map { it.toProto() })
    }
    if (hasTargetDisease()) {
      protoValue.targetDisease = targetDisease.toProto()
    }
    if (hasContraindicatedVaccineCode()) {
      protoValue.addAllContraindicatedVaccineCode(contraindicatedVaccineCode.map { it.toProto() })
    }
    if (hasForecastStatus()) {
      protoValue.forecastStatus = forecastStatus.toProto()
    }
    if (hasForecastReason()) {
      protoValue.addAllForecastReason(forecastReason.map { it.toProto() })
    }
    if (hasDateCriterion()) {
      protoValue.addAllDateCriterion(dateCriterion.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.description = descriptionElement.toProto()
    }
    if (hasSeries()) {
      protoValue.series = seriesElement.toProto()
    }
    if (hasDoseNumber()) {
      protoValue.doseNumber = doseNumber.immunizationRecommendationRecommendationDoseNumberToProto()
    }
    if (hasSeriesDoses()) {
      protoValue.seriesDoses =
        seriesDoses.immunizationRecommendationRecommendationSeriesDosesToProto()
    }
    if (hasSupportingImmunization()) {
      protoValue.addAllSupportingImmunization(supportingImmunization.map { it.toProto() })
    }
    if (hasSupportingPatientInformation()) {
      protoValue.addAllSupportingPatientInformation(
        supportingPatientInformation.map { it.toProto() }
      )
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.ImmunizationRecommendation.ImmunizationRecommendationRecommendationDateCriterionComponent.toProto():
    ImmunizationRecommendation.Recommendation.DateCriterion {
    val protoValue =
      ImmunizationRecommendation.Recommendation.DateCriterion.newBuilder()
        .setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasCode()) {
      protoValue.code = code.toProto()
    }
    if (hasValue()) {
      protoValue.value = valueElement.toProto()
    }
    return protoValue.build()
  }

  private fun ImmunizationRecommendation.Recommendation.toHapi():
    org.hl7.fhir.r4.model.ImmunizationRecommendation.ImmunizationRecommendationRecommendationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ImmunizationRecommendation
        .ImmunizationRecommendationRecommendationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (vaccineCodeCount > 0) {
      hapiValue.vaccineCode = vaccineCodeList.map { it.toHapi() }
    }
    if (hasTargetDisease()) {
      hapiValue.targetDisease = targetDisease.toHapi()
    }
    if (contraindicatedVaccineCodeCount > 0) {
      hapiValue.contraindicatedVaccineCode = contraindicatedVaccineCodeList.map { it.toHapi() }
    }
    if (hasForecastStatus()) {
      hapiValue.forecastStatus = forecastStatus.toHapi()
    }
    if (forecastReasonCount > 0) {
      hapiValue.forecastReason = forecastReasonList.map { it.toHapi() }
    }
    if (dateCriterionCount > 0) {
      hapiValue.dateCriterion = dateCriterionList.map { it.toHapi() }
    }
    if (hasDescription()) {
      hapiValue.descriptionElement = description.toHapi()
    }
    if (hasSeries()) {
      hapiValue.seriesElement = series.toHapi()
    }
    if (hasDoseNumber()) {
      hapiValue.doseNumber = doseNumber.immunizationRecommendationRecommendationDoseNumberToHapi()
    }
    if (hasSeriesDoses()) {
      hapiValue.seriesDoses =
        seriesDoses.immunizationRecommendationRecommendationSeriesDosesToHapi()
    }
    if (supportingImmunizationCount > 0) {
      hapiValue.supportingImmunization = supportingImmunizationList.map { it.toHapi() }
    }
    if (supportingPatientInformationCount > 0) {
      hapiValue.supportingPatientInformation = supportingPatientInformationList.map { it.toHapi() }
    }
    return hapiValue
  }

  private fun ImmunizationRecommendation.Recommendation.DateCriterion.toHapi():
    org.hl7.fhir.r4.model.ImmunizationRecommendation.ImmunizationRecommendationRecommendationDateCriterionComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ImmunizationRecommendation
        .ImmunizationRecommendationRecommendationDateCriterionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasCode()) {
      hapiValue.code = code.toHapi()
    }
    if (hasValue()) {
      hapiValue.valueElement = value.toHapi()
    }
    return hapiValue
  }
}
