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
import com.google.fhir.r4.core.Dosage.DoseAndRate
import com.google.fhir.r4.core.Range
import com.google.fhir.r4.core.Ratio
import com.google.fhir.r4.core.SimpleQuantity
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Type

public object DosageConverter {
  @JvmStatic
  private fun Dosage.AsNeededX.dosageAsNeededToHapi(): Type {
    if (this.getBoolean() != Boolean.newBuilder().defaultInstanceForType) {
      return (this.getBoolean()).toHapi()
    }
    if (this.getCodeableConcept() != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.getCodeableConcept()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Dosage.asNeeded[x]")
  }

  @JvmStatic
  private fun Type.dosageAsNeededToProto(): Dosage.AsNeededX {
    val protoValue = Dosage.AsNeededX.newBuilder()
    if (this is BooleanType) {
      protoValue.setBoolean(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.setCodeableConcept(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Dosage.DoseAndRate.DoseX.dosageDoseAndRateDoseToHapi(): Type {
    if (this.getRange() != Range.newBuilder().defaultInstanceForType) {
      return (this.getRange()).toHapi()
    }
    if (this.getQuantity() != SimpleQuantity.newBuilder().defaultInstanceForType) {
      return (this.getQuantity()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Dosage.doseAndRate.dose[x]")
  }

  @JvmStatic
  private fun Type.dosageDoseAndRateDoseToProto(): Dosage.DoseAndRate.DoseX {
    val protoValue = Dosage.DoseAndRate.DoseX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.SimpleQuantity) {
      protoValue.setQuantity(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Dosage.DoseAndRate.RateX.dosageDoseAndRateRateToHapi(): Type {
    if (this.getRatio() != Ratio.newBuilder().defaultInstanceForType) {
      return (this.getRatio()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType) {
      return (this.getRange()).toHapi()
    }
    if (this.getQuantity() != SimpleQuantity.newBuilder().defaultInstanceForType) {
      return (this.getQuantity()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Dosage.doseAndRate.rate[x]")
  }

  @JvmStatic
  private fun Type.dosageDoseAndRateRateToProto(): Dosage.DoseAndRate.RateX {
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

  @JvmStatic
  public fun Dosage.toHapi(): org.hl7.fhir.r4.model.Dosage {
    val hapiValue = org.hl7.fhir.r4.model.Dosage()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasSequence()) {
      hapiValue.setSequenceElement(sequence.toHapi())
    }
    if (hasText()) {
      hapiValue.setTextElement(text.toHapi())
    }
    if (additionalInstructionCount > 0) {
      hapiValue.setAdditionalInstruction(additionalInstructionList.map { it.toHapi() })
    }
    if (hasPatientInstruction()) {
      hapiValue.setPatientInstructionElement(patientInstruction.toHapi())
    }
    if (hasTiming()) {
      hapiValue.setTiming(timing.toHapi())
    }
    if (hasAsNeeded()) {
      hapiValue.setAsNeeded(asNeeded.dosageAsNeededToHapi())
    }
    if (hasSite()) {
      hapiValue.setSite(site.toHapi())
    }
    if (hasRoute()) {
      hapiValue.setRoute(route.toHapi())
    }
    if (hasMethod()) {
      hapiValue.setMethod(method.toHapi())
    }
    if (doseAndRateCount > 0) {
      hapiValue.setDoseAndRate(doseAndRateList.map { it.toHapi() })
    }
    if (hasMaxDosePerPeriod()) {
      hapiValue.setMaxDosePerPeriod(maxDosePerPeriod.toHapi())
    }
    if (hasMaxDosePerAdministration()) {
      hapiValue.setMaxDosePerAdministration(maxDosePerAdministration.toHapi())
    }
    if (hasMaxDosePerLifetime()) {
      hapiValue.setMaxDosePerLifetime(maxDosePerLifetime.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.Dosage.toProto(): Dosage {
    val protoValue = Dosage.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSequence()) {
      protoValue.setSequence(sequenceElement.toProto())
    }
    if (hasText()) {
      protoValue.setText(textElement.toProto())
    }
    if (hasAdditionalInstruction()) {
      protoValue.addAllAdditionalInstruction(additionalInstruction.map { it.toProto() })
    }
    if (hasPatientInstruction()) {
      protoValue.setPatientInstruction(patientInstructionElement.toProto())
    }
    if (hasTiming()) {
      protoValue.setTiming(timing.toProto())
    }
    if (hasAsNeeded()) {
      protoValue.setAsNeeded(asNeeded.dosageAsNeededToProto())
    }
    if (hasSite()) {
      protoValue.setSite(site.toProto())
    }
    if (hasRoute()) {
      protoValue.setRoute(route.toProto())
    }
    if (hasMethod()) {
      protoValue.setMethod(method.toProto())
    }
    if (hasDoseAndRate()) {
      protoValue.addAllDoseAndRate(doseAndRate.map { it.toProto() })
    }
    if (hasMaxDosePerPeriod()) {
      protoValue.setMaxDosePerPeriod(maxDosePerPeriod.toProto())
    }
    if (hasMaxDosePerAdministration()) {
      protoValue.setMaxDosePerAdministration(
        (maxDosePerAdministration as org.hl7.fhir.r4.model.SimpleQuantity).toProto()
      )
    }
    if (hasMaxDosePerLifetime()) {
      protoValue.setMaxDosePerLifetime(
        (maxDosePerLifetime as org.hl7.fhir.r4.model.SimpleQuantity).toProto()
      )
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Dosage.DosageDoseAndRateComponent.toProto():
    Dosage.DoseAndRate {
    val protoValue = Dosage.DoseAndRate.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.setType(type.toProto())
    }
    if (hasDose()) {
      protoValue.setDose(dose.dosageDoseAndRateDoseToProto())
    }
    if (hasRate()) {
      protoValue.setRate(rate.dosageDoseAndRateRateToProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Dosage.DoseAndRate.toHapi(): org.hl7.fhir.r4.model.Dosage.DosageDoseAndRateComponent {
    val hapiValue = org.hl7.fhir.r4.model.Dosage.DosageDoseAndRateComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (hasType()) {
      hapiValue.setType(type.toHapi())
    }
    if (hasDose()) {
      hapiValue.setDose(dose.dosageDoseAndRateDoseToHapi())
    }
    if (hasRate()) {
      hapiValue.setRate(rate.dosageDoseAndRateRateToHapi())
    }
    return hapiValue
  }
}
