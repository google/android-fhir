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
import org.hl7.fhir.r4.model.PositiveIntType
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

public object ImmunizationEvaluationConverter {
  private fun ImmunizationEvaluation.DoseNumberX.immunizationEvaluationDoseNumberToHapi(): Type {
    if (this.getPositiveInt() != PositiveInt.newBuilder().defaultInstanceForType ) {
      return (this.getPositiveInt()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType ) {
      return (this.getStringValue()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ImmunizationEvaluation.doseNumber[x]")
  }

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

  private fun ImmunizationEvaluation.SeriesDosesX.immunizationEvaluationSeriesDosesToHapi(): Type {
    if (this.getPositiveInt() != PositiveInt.newBuilder().defaultInstanceForType ) {
      return (this.getPositiveInt()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType ) {
      return (this.getStringValue()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for ImmunizationEvaluation.seriesDoses[x]")
  }

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

  public fun ImmunizationEvaluation.toHapi(): org.hl7.fhir.r4.model.ImmunizationEvaluation {
    val hapiValue = org.hl7.fhir.r4.model.ImmunizationEvaluation()
    hapiValue.id = id.value 
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setText(text.toHapi())
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setIdentifier(identifierList.map{it.toHapi()})
    hapiValue.setStatus(org.hl7.fhir.r4.model.ImmunizationEvaluation.ImmunizationEvaluationStatus.valueOf(status.value.name.replace("_","")))
    hapiValue.setPatient(patient.toHapi())
    hapiValue.setDateElement(date.toHapi())
    hapiValue.setAuthority(authority.toHapi())
    hapiValue.setTargetDisease(targetDisease.toHapi())
    hapiValue.setImmunizationEvent(immunizationEvent.toHapi())
    hapiValue.setDoseStatus(doseStatus.toHapi())
    hapiValue.setDoseStatusReason(doseStatusReasonList.map{it.toHapi()})
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setSeriesElement(series.toHapi())
    hapiValue.setDoseNumber(doseNumber.immunizationEvaluationDoseNumberToHapi())
    hapiValue.setSeriesDoses(seriesDoses.immunizationEvaluationSeriesDosesToHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.ImmunizationEvaluation.toProto(): ImmunizationEvaluation {
    val protoValue = ImmunizationEvaluation.newBuilder()
    .setId(Id.newBuilder().setValue(id))
    .setMeta(meta.toProto())
    .setImplicitRules(implicitRulesElement.toProto())
    .setText(text.toProto())
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .addAllIdentifier(identifier.map{it.toProto()})
    .setStatus(ImmunizationEvaluation.StatusCode.newBuilder().setValue(ImmunizationEvaluationStatusCodesValueSet.Value.valueOf(status.toCode().replace("-",
        "_").toUpperCase())).build())
    .setPatient(patient.toProto())
    .setDate(dateElement.toProto())
    .setAuthority(authority.toProto())
    .setTargetDisease(targetDisease.toProto())
    .setImmunizationEvent(immunizationEvent.toProto())
    .setDoseStatus(doseStatus.toProto())
    .addAllDoseStatusReason(doseStatusReason.map{it.toProto()})
    .setDescription(descriptionElement.toProto())
    .setSeries(seriesElement.toProto())
    .setDoseNumber(doseNumber.immunizationEvaluationDoseNumberToProto())
    .setSeriesDoses(seriesDoses.immunizationEvaluationSeriesDosesToProto())
    .build()
    return protoValue
  }
}
