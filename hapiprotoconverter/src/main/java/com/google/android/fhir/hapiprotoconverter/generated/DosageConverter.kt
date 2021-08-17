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
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Type

object DosageConverter {
  private fun Dosage.AsNeededX.dosageAsNeededToHapi(): Type {
    if (this.boolean != Boolean.newBuilder().defaultInstanceForType) {
      return (this.boolean).toHapi()
    }
    if (this.codeableConcept != CodeableConcept.newBuilder().defaultInstanceForType) {
      return (this.codeableConcept).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Dosage.asNeeded[x]")
  }

  private fun Type.dosageAsNeededToProto(): Dosage.AsNeededX {
    val protoValue = Dosage.AsNeededX.newBuilder()
    if (this is BooleanType) {
      protoValue.boolean = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.CodeableConcept) {
      protoValue.codeableConcept = this.toProto()
    }
    return protoValue.build()
  }

  private fun Dosage.DoseAndRate.DoseX.dosageDoseAndRateDoseToHapi(): Type {
    if (this.range != Range.newBuilder().defaultInstanceForType) {
      return (this.range).toHapi()
    }
    if (this.quantity != SimpleQuantity.newBuilder().defaultInstanceForType) {
      return (this.quantity).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Dosage.doseAndRate.dose[x]")
  }

  private fun Type.dosageDoseAndRateDoseToProto(): Dosage.DoseAndRate.DoseX {
    val protoValue = Dosage.DoseAndRate.DoseX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.range = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.SimpleQuantity) {
      protoValue.quantity = this.toProto()
    }
    return protoValue.build()
  }

  private fun Dosage.DoseAndRate.RateX.dosageDoseAndRateRateToHapi(): Type {
    if (this.ratio != Ratio.newBuilder().defaultInstanceForType) {
      return (this.ratio).toHapi()
    }
    if (this.range != Range.newBuilder().defaultInstanceForType) {
      return (this.range).toHapi()
    }
    if (this.quantity != SimpleQuantity.newBuilder().defaultInstanceForType) {
      return (this.quantity).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Dosage.doseAndRate.rate[x]")
  }

  private fun Type.dosageDoseAndRateRateToProto(): Dosage.DoseAndRate.RateX {
    val protoValue = Dosage.DoseAndRate.RateX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Ratio) {
      protoValue.ratio = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.range = this.toProto()
    }
    if (this is org.hl7.fhir.r4.model.SimpleQuantity) {
      protoValue.quantity = this.toProto()
    }
    return protoValue.build()
  }

  fun Dosage.toHapi(): org.hl7.fhir.r4.model.Dosage {
    val hapiValue = org.hl7.fhir.r4.model.Dosage()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasSequence()) {
      hapiValue.sequenceElement = sequence.toHapi()
    }
    if (hasText()) {
      hapiValue.textElement = text.toHapi()
    }
    if (additionalInstructionCount > 0) {
      hapiValue.additionalInstruction = additionalInstructionList.map { it.toHapi() }
    }
    if (hasPatientInstruction()) {
      hapiValue.patientInstructionElement = patientInstruction.toHapi()
    }
    if (hasTiming()) {
      hapiValue.timing = timing.toHapi()
    }
    if (hasAsNeeded()) {
      hapiValue.asNeeded = asNeeded.dosageAsNeededToHapi()
    }
    if (hasSite()) {
      hapiValue.site = site.toHapi()
    }
    if (hasRoute()) {
      hapiValue.route = route.toHapi()
    }
    if (hasMethod()) {
      hapiValue.method = method.toHapi()
    }
    if (doseAndRateCount > 0) {
      hapiValue.doseAndRate = doseAndRateList.map { it.toHapi() }
    }
    if (hasMaxDosePerPeriod()) {
      hapiValue.maxDosePerPeriod = maxDosePerPeriod.toHapi()
    }
    if (hasMaxDosePerAdministration()) {
      hapiValue.maxDosePerAdministration = maxDosePerAdministration.toHapi()
    }
    if (hasMaxDosePerLifetime()) {
      hapiValue.maxDosePerLifetime = maxDosePerLifetime.toHapi()
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.Dosage.toProto(): Dosage {
    val protoValue = Dosage.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasSequence()) {
      protoValue.sequence = sequenceElement.toProto()
    }
    if (hasText()) {
      protoValue.text = textElement.toProto()
    }
    if (hasAdditionalInstruction()) {
      protoValue.addAllAdditionalInstruction(additionalInstruction.map { it.toProto() })
    }
    if (hasPatientInstruction()) {
      protoValue.patientInstruction = patientInstructionElement.toProto()
    }
    if (hasTiming()) {
      protoValue.timing = timing.toProto()
    }
    if (hasAsNeeded()) {
      protoValue.asNeeded = asNeeded.dosageAsNeededToProto()
    }
    if (hasSite()) {
      protoValue.site = site.toProto()
    }
    if (hasRoute()) {
      protoValue.route = route.toProto()
    }
    if (hasMethod()) {
      protoValue.method = method.toProto()
    }
    if (hasDoseAndRate()) {
      protoValue.addAllDoseAndRate(doseAndRate.map { it.toProto() })
    }
    if (hasMaxDosePerPeriod()) {
      protoValue.maxDosePerPeriod = maxDosePerPeriod.toProto()
    }
    if (hasMaxDosePerAdministration()) {
      protoValue.maxDosePerAdministration =
        (maxDosePerAdministration as org.hl7.fhir.r4.model.SimpleQuantity).toProto()
    }
    if (hasMaxDosePerLifetime()) {
      protoValue.maxDosePerLifetime =
        (maxDosePerLifetime as org.hl7.fhir.r4.model.SimpleQuantity).toProto()
    }
    return protoValue.build()
  }

  private fun org.hl7.fhir.r4.model.Dosage.DosageDoseAndRateComponent.toProto():
    Dosage.DoseAndRate {
    val protoValue = Dosage.DoseAndRate.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = type.toProto()
    }
    if (hasDose()) {
      protoValue.dose = dose.dosageDoseAndRateDoseToProto()
    }
    if (hasRate()) {
      protoValue.rate = rate.dosageDoseAndRateRateToProto()
    }
    return protoValue.build()
  }

  private fun Dosage.DoseAndRate.toHapi(): org.hl7.fhir.r4.model.Dosage.DosageDoseAndRateComponent {
    val hapiValue = org.hl7.fhir.r4.model.Dosage.DosageDoseAndRateComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.type = type.toHapi()
    }
    if (hasDose()) {
      hapiValue.dose = dose.dosageDoseAndRateDoseToHapi()
    }
    if (hasRate()) {
      hapiValue.rate = rate.dosageDoseAndRateRateToHapi()
    }
    return hapiValue
  }
}
