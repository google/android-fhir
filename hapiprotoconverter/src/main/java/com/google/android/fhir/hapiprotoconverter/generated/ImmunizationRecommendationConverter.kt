package com.google.android.fhir.hapiprotoconverter.generated

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
import com.google.fhir.r4.core.PositiveInt
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.PositiveIntType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

public object ImmunizationRecommendationConverter {
  public
      fun ImmunizationRecommendation.Recommendation.DoseNumberX.immunizationRecommendationRecommendationDoseNumberToHapi():
      Type {
    if (this.getPositiveInt() != PositiveInt.newBuilder().defaultInstanceForType ) {
      return (this.getPositiveInt()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType ) {
      return (this.getStringValue()).toHapi()
    }
    throw
        IllegalArgumentException("Invalid Type for ImmunizationRecommendation.recommendation.doseNumber[x]")
  }

  public fun Type.immunizationRecommendationRecommendationDoseNumberToProto():
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

  public
      fun ImmunizationRecommendation.Recommendation.SeriesDosesX.immunizationRecommendationRecommendationSeriesDosesToHapi():
      Type {
    if (this.getPositiveInt() != PositiveInt.newBuilder().defaultInstanceForType ) {
      return (this.getPositiveInt()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType ) {
      return (this.getStringValue()).toHapi()
    }
    throw
        IllegalArgumentException("Invalid Type for ImmunizationRecommendation.recommendation.seriesDoses[x]")
  }

  public fun Type.immunizationRecommendationRecommendationSeriesDosesToProto():
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

  public fun ImmunizationRecommendation.toHapi(): org.hl7.fhir.r4.model.ImmunizationRecommendation {
    val hapiValue = org.hl7.fhir.r4.model.ImmunizationRecommendation()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setPatient(patient.toHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setAuthority(authority.toHapi())
    hapiValue.setRecommendation(recommendationList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.ImmunizationRecommendation.toProto():
      ImmunizationRecommendation {
    val protoValue = ImmunizationRecommendation.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setLanguage(languageElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setPatient(patient.toProto())
    .setDate(dateElement.toProto())
    .setAuthority(authority.toProto())
    .addAllRecommendation(recommendation.map{it.toProto()})
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.ImmunizationRecommendation.ImmunizationRecommendationRecommendationComponent.toProto():
      ImmunizationRecommendation.Recommendation {
    val protoValue = ImmunizationRecommendation.Recommendation.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllVaccineCode(vaccineCode.map{it.toProto()})
    .setTargetDisease(targetDisease.toProto())
    .addAllContraindicatedVaccineCode(contraindicatedVaccineCode.map{it.toProto()})
    .setForecastStatus(forecastStatus.toProto())
    .addAllForecastReason(forecastReason.map{it.toProto()})
    .addAllDateCriterion(dateCriterion.map{it.toProto()})
    .setDescription(descriptionElement.toProto())
    .setSeries(seriesElement.toProto())
    .setDoseNumber(doseNumber.immunizationRecommendationRecommendationDoseNumberToProto())
    .setSeriesDoses(seriesDoses.immunizationRecommendationRecommendationSeriesDosesToProto())
    .addAllSupportingImmunization(supportingImmunization.map{it.toProto()})
    .addAllSupportingPatientInformation(supportingPatientInformation.map{it.toProto()})
    .build()
    return protoValue
  }

  public
      fun org.hl7.fhir.r4.model.ImmunizationRecommendation.ImmunizationRecommendationRecommendationDateCriterionComponent.toProto():
      ImmunizationRecommendation.Recommendation.DateCriterion {
    val protoValue = ImmunizationRecommendation.Recommendation.DateCriterion.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setCode(code.toProto())
    .setValue(valueElement.toProto())
    .build()
    return protoValue
  }

  public fun ImmunizationRecommendation.Recommendation.toHapi():
      org.hl7.fhir.r4.model.ImmunizationRecommendation.ImmunizationRecommendationRecommendationComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.ImmunizationRecommendation.ImmunizationRecommendationRecommendationComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setVaccineCode(vaccineCodeList.map{it.toHapi()})
    hapiValue.setTargetDisease(targetDisease.toHapi())
    hapiValue.setContraindicatedVaccineCode(contraindicatedVaccineCodeList.map{it.toHapi()})
    hapiValue.setForecastStatus(forecastStatus.toHapi())
    hapiValue.setForecastReason(forecastReasonList.map{it.toHapi()})
    hapiValue.setDateCriterion(dateCriterionList.map{it.toHapi()})
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setSeriesElement(series.toHapi())
    hapiValue.setDoseNumber(doseNumber.immunizationRecommendationRecommendationDoseNumberToHapi())
    hapiValue.setSeriesDoses(seriesDoses.immunizationRecommendationRecommendationSeriesDosesToHapi())
    hapiValue.setSupportingImmunization(supportingImmunizationList.map{it.toHapi()})
    hapiValue.setSupportingPatientInformation(supportingPatientInformationList.map{it.toHapi()})
    return hapiValue
  }

  public fun ImmunizationRecommendation.Recommendation.DateCriterion.toHapi():
      org.hl7.fhir.r4.model.ImmunizationRecommendation.ImmunizationRecommendationRecommendationDateCriterionComponent {
    val hapiValue =
        org.hl7.fhir.r4.model.ImmunizationRecommendation.ImmunizationRecommendationRecommendationDateCriterionComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setCode(code.toHapi())
    hapiValue.setValueElement(value.toHapi())
    return hapiValue
  }
}
