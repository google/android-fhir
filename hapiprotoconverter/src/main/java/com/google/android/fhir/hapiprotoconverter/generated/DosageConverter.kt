package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RatioConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RatioConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.TimingConverter.toProto
import com.google.fhir.r4.core.Boolean
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.Dosage
import com.google.fhir.r4.core.Range
import com.google.fhir.r4.core.Ratio
import com.google.fhir.r4.core.SimpleQuantity
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Type

public object DosageConverter {
  public fun Dosage.AsNeededX.asNeededToHapi(): Type {
    if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType ) {
      return (this.getBoolean()).toHapi()
    }
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType ) {
      return (this.getCodeableConcept()).toHapi()
    }
    throw IllegalArgumentException("Dosage.asNeeded[x]")
  }

  public fun Type.asNeededToProto(): Dosage.AsNeededX {
    val protoValue = Dosage.AsNeededX.newBuilder()
    if (this is BooleanType) {
      protoValue.setBoolean(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    return protoValue.build()
  }

  public fun Dosage.DoseAndRate.DoseX.doseToHapi(): Type {
    if (this.getRange() != Range.newBuilder().defaultInstanceForType ) {
      return (this.getRange()).toHapi()
    }
    if (this.getQuantity() != SimpleQuantity.newBuilder().defaultInstanceForType ) {
      return (this.getQuantity()).toHapi()
    }
    throw IllegalArgumentException("Dosage.doseAndRate.dose[x]")
  }

  public fun Type.doseToProto(): Dosage.DoseAndRate.DoseX {
    val protoValue = Dosage.DoseAndRate.DoseX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.SimpleQuantity) {
      protoValue.setQuantity(this.toProto())
    }
    return protoValue.build()
  }

  public fun Dosage.DoseAndRate.RateX.rateToHapi(): Type {
    if (this.getRatio() != Ratio.newBuilder().defaultInstanceForType ) {
      return (this.getRatio()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType ) {
      return (this.getRange()).toHapi()
    }
    if (this.getQuantity() != SimpleQuantity.newBuilder().defaultInstanceForType ) {
      return (this.getQuantity()).toHapi()
    }
    throw IllegalArgumentException("Dosage.doseAndRate.rate[x]")
  }

  public fun Type.rateToProto(): Dosage.DoseAndRate.RateX {
    val protoValue = Dosage.DoseAndRate.RateX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Ratio) {
      protoValue.setRatio(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.SimpleQuantity) {
      protoValue.setQuantity(this.toProto())
    }
    return protoValue.build()
  }

  public fun Dosage.toHapi(): org.hl7.fhir.r4.model.Dosage {
    val hapiValue = org.hl7.fhir.r4.model.Dosage()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setSequenceElement(sequence.toHapi())
    hapiValue.setTextElement(text.toHapi())
    hapiValue.setAdditionalInstruction(additionalInstructionList.map{it.toHapi()})
    hapiValue.setPatientInstructionElement(patientInstruction.toHapi())
    hapiValue.setTiming(timing.toHapi())
    hapiValue.setAsNeeded(asNeeded.asNeededToHapi())
    hapiValue.setSite(site.toHapi())
    hapiValue.setRoute(route.toHapi())
    hapiValue.setMethod(method.toHapi())
    hapiValue.setDoseAndRate(doseAndRateList.map{it.toHapi()})
    hapiValue.setMaxDosePerPeriod(maxDosePerPeriod.toHapi())
    hapiValue.setMaxDosePerAdministration(maxDosePerAdministration.toHapi())
    hapiValue.setMaxDosePerLifetime(maxDosePerLifetime.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Dosage.toProto(): Dosage {
    val protoValue = Dosage.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setSequence(sequenceElement.toProto())
    .setText(textElement.toProto())
    .addAllAdditionalInstruction(additionalInstruction.map{it.toProto()})
    .setPatientInstruction(patientInstructionElement.toProto())
    .setTiming(timing.toProto())
    .setAsNeeded(asNeeded.asNeededToProto())
    .setSite(site.toProto())
    .setRoute(route.toProto())
    .setMethod(method.toProto())
    .addAllDoseAndRate(doseAndRate.map{it.toProto()})
    .setMaxDosePerPeriod(maxDosePerPeriod.toProto())
    .setMaxDosePerAdministration(( maxDosePerAdministration as org.hl7.fhir.r4.model.SimpleQuantity
        ).toProto())
    .setMaxDosePerLifetime(( maxDosePerLifetime as org.hl7.fhir.r4.model.SimpleQuantity ).toProto())
    .build()
    return protoValue
  }

  public fun org.hl7.fhir.r4.model.Dosage.DosageDoseAndRateComponent.toProto(): Dosage.DoseAndRate {
    val protoValue = Dosage.DoseAndRate.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .setType(type.toProto())
    .setDose(dose.doseToProto())
    .setRate(rate.rateToProto())
    .build()
    return protoValue
  }

  public fun Dosage.DoseAndRate.toHapi(): org.hl7.fhir.r4.model.Dosage.DosageDoseAndRateComponent {
    val hapiValue = org.hl7.fhir.r4.model.Dosage.DosageDoseAndRateComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setType(type.toHapi())
    hapiValue.setDose(dose.doseToHapi())
    hapiValue.setRate(rate.rateToHapi())
    return hapiValue
  }
}
