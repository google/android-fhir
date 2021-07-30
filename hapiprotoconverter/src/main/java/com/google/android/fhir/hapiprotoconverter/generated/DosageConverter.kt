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
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setSequenceElement(sequence.toHapi())
    hapiValue.setTextElement(text.toHapi())
    hapiValue.setAdditionalInstruction(additionalInstructionList.map { it.toHapi() })
    hapiValue.setPatientInstructionElement(patientInstruction.toHapi())
    hapiValue.setTiming(timing.toHapi())
    hapiValue.setAsNeeded(asNeeded.dosageAsNeededToHapi())
    hapiValue.setSite(site.toHapi())
    hapiValue.setRoute(route.toHapi())
    hapiValue.setMethod(method.toHapi())
    hapiValue.setDoseAndRate(doseAndRateList.map { it.toHapi() })
    hapiValue.setMaxDosePerPeriod(maxDosePerPeriod.toHapi())
    hapiValue.setMaxDosePerAdministration(maxDosePerAdministration.toHapi())
    hapiValue.setMaxDosePerLifetime(maxDosePerLifetime.toHapi())
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.Dosage.toProto(): Dosage {
    val protoValue =
      Dosage.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setSequence(sequenceElement.toProto())
        .setText(textElement.toProto())
        .addAllAdditionalInstruction(additionalInstruction.map { it.toProto() })
        .setPatientInstruction(patientInstructionElement.toProto())
        .setTiming(timing.toProto())
        .setAsNeeded(asNeeded.dosageAsNeededToProto())
        .setSite(site.toProto())
        .setRoute(route.toProto())
        .setMethod(method.toProto())
        .addAllDoseAndRate(doseAndRate.map { it.toProto() })
        .setMaxDosePerPeriod(maxDosePerPeriod.toProto())
        .setMaxDosePerAdministration(
          (maxDosePerAdministration as org.hl7.fhir.r4.model.SimpleQuantity).toProto()
        )
        .setMaxDosePerLifetime(
          (maxDosePerLifetime as org.hl7.fhir.r4.model.SimpleQuantity).toProto()
        )
        .build()
    return protoValue
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Dosage.DosageDoseAndRateComponent.toProto():
    Dosage.DoseAndRate {
    val protoValue =
      Dosage.DoseAndRate.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .setType(type.toProto())
        .setDose(dose.dosageDoseAndRateDoseToProto())
        .setRate(rate.dosageDoseAndRateRateToProto())
        .build()
    return protoValue
  }

  @JvmStatic
  private fun Dosage.DoseAndRate.toHapi(): org.hl7.fhir.r4.model.Dosage.DosageDoseAndRateComponent {
    val hapiValue = org.hl7.fhir.r4.model.Dosage.DosageDoseAndRateComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setType(type.toHapi())
    hapiValue.setDose(dose.dosageDoseAndRateDoseToHapi())
    hapiValue.setRate(rate.dosageDoseAndRateRateToHapi())
    return hapiValue
  }
}
