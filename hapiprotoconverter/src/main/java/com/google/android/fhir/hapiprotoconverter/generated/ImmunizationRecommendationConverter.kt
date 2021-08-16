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
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.PositiveIntType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

public object ImmunizationRecommendationConverter {
  @JvmStatic
  private fun ImmunizationRecommendation.Recommendation.DoseNumberX.immunizationRecommendationRecommendationDoseNumberToHapi():
    Type {
    if (this.getPositiveInt() != PositiveInt.newBuilder().defaultInstanceForType) {
      return (this.getPositiveInt()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for ImmunizationRecommendation.recommendation.doseNumber[x]"
    )
  }

  @JvmStatic
  private fun Type.immunizationRecommendationRecommendationDoseNumberToProto():
    ImmunizationRecommendation.Recommendation.DoseNumberX {
    val protoValue = ImmunizationRecommendation.Recommendation.DoseNumberX.newBuilder()
    if (this is PositiveIntType) {
      protoValue.setPositiveInt(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ImmunizationRecommendation.Recommendation.SeriesDosesX.immunizationRecommendationRecommendationSeriesDosesToHapi():
    Type {
    if (this.getPositiveInt() != PositiveInt.newBuilder().defaultInstanceForType) {
      return (this.getPositiveInt()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    throw IllegalArgumentException(
      "Invalid Type for ImmunizationRecommendation.recommendation.seriesDoses[x]"
    )
  }

  @JvmStatic
  private fun Type.immunizationRecommendationRecommendationSeriesDosesToProto():
    ImmunizationRecommendation.Recommendation.SeriesDosesX {
    val protoValue = ImmunizationRecommendation.Recommendation.SeriesDosesX.newBuilder()
    if (this is PositiveIntType) {
      protoValue.setPositiveInt(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun ImmunizationRecommendation.toHapi(): org.hl7.fhir.r4.model.ImmunizationRecommendation {
    val hapiValue = org.hl7.fhir.r4.model.ImmunizationRecommendation()
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
    if (hasPatient()) {
      hapiValue.setPatient(patient.toHapi())
    }
    if (hasDate()) {
      hapiValue.setDateElement(date.toHapi())
    }
    if (hasAuthority()) {
      hapiValue.setAuthority(authority.toHapi())
    }
    if (recommendationCount > 0) {
      hapiValue.setRecommendation(recommendationList.map { it.toHapi() })
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.ImmunizationRecommendation.toProto():
    ImmunizationRecommendation {
    val protoValue = ImmunizationRecommendation.newBuilder().setId(Id.newBuilder().setValue(id))
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
    if (hasPatient()) {
      protoValue.setPatient(patient.toProto())
    }
    if (hasDate()) {
      protoValue.setDate(dateElement.toProto())
    }
    if (hasAuthority()) {
      protoValue.setAuthority(authority.toProto())
    }
    if (hasRecommendation()) {
      protoValue.addAllRecommendation(recommendation.map { it.toProto() })
    }
    return protoValue.build()
  }

  @JvmStatic
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
      protoValue.setTargetDisease(targetDisease.toProto())
    }
    if (hasContraindicatedVaccineCode()) {
      protoValue.addAllContraindicatedVaccineCode(contraindicatedVaccineCode.map { it.toProto() })
    }
    if (hasForecastStatus()) {
      protoValue.setForecastStatus(forecastStatus.toProto())
    }
    if (hasForecastReason()) {
      protoValue.addAllForecastReason(forecastReason.map { it.toProto() })
    }
    if (hasDateCriterion()) {
      protoValue.addAllDateCriterion(dateCriterion.map { it.toProto() })
    }
    if (hasDescription()) {
      protoValue.setDescription(descriptionElement.toProto())
    }
    if (hasSeries()) {
      protoValue.setSeries(seriesElement.toProto())
    }
    if (hasDoseNumber()) {
      protoValue.setDoseNumber(
        doseNumber.immunizationRecommendationRecommendationDoseNumberToProto()
      )
    }
    if (hasSeriesDoses()) {
      protoValue.setSeriesDoses(
        seriesDoses.immunizationRecommendationRecommendationSeriesDosesToProto()
      )
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

  @JvmStatic
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
      protoValue.setCode(code.toProto())
    }
    if (hasValue()) {
      protoValue.setValue(valueElement.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun ImmunizationRecommendation.Recommendation.toHapi():
    org.hl7.fhir.r4.model.ImmunizationRecommendation.ImmunizationRecommendationRecommendationComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ImmunizationRecommendation
        .ImmunizationRecommendationRecommendationComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (vaccineCodeCount > 0) {
      hapiValue.setVaccineCode(vaccineCodeList.map { it.toHapi() })
    }
    if (hasTargetDisease()) {
      hapiValue.setTargetDisease(targetDisease.toHapi())
    }
    if (contraindicatedVaccineCodeCount > 0) {
      hapiValue.setContraindicatedVaccineCode(contraindicatedVaccineCodeList.map { it.toHapi() })
    }
    if (hasForecastStatus()) {
      hapiValue.setForecastStatus(forecastStatus.toHapi())
    }
    if (forecastReasonCount > 0) {
      hapiValue.setForecastReason(forecastReasonList.map { it.toHapi() })
    }
    if (dateCriterionCount > 0) {
      hapiValue.setDateCriterion(dateCriterionList.map { it.toHapi() })
    }
    if (hasDescription()) {
      hapiValue.setDescriptionElement(description.toHapi())
    }
    if (hasSeries()) {
      hapiValue.setSeriesElement(series.toHapi())
    }
    if (hasDoseNumber()) {
      hapiValue.setDoseNumber(doseNumber.immunizationRecommendationRecommendationDoseNumberToHapi())
    }
    if (hasSeriesDoses()) {
      hapiValue.setSeriesDoses(
        seriesDoses.immunizationRecommendationRecommendationSeriesDosesToHapi()
      )
    }
    if (supportingImmunizationCount > 0) {
      hapiValue.setSupportingImmunization(supportingImmunizationList.map { it.toHapi() })
    }
    if (supportingPatientInformationCount > 0) {
      hapiValue.setSupportingPatientInformation(
        supportingPatientInformationList.map { it.toHapi() }
      )
    }
    return hapiValue
  }

  @JvmStatic
  private fun ImmunizationRecommendation.Recommendation.DateCriterion.toHapi():
    org.hl7.fhir.r4.model.ImmunizationRecommendation.ImmunizationRecommendationRecommendationDateCriterionComponent {
    val hapiValue =
      org.hl7.fhir.r4.model.ImmunizationRecommendation
        .ImmunizationRecommendationRecommendationDateCriterionComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasCode()) {
      hapiValue.setCode(code.toHapi())
    }
    if (hasValue()) {
      hapiValue.setValueElement(value.toHapi())
    }
    return hapiValue
  }
}
