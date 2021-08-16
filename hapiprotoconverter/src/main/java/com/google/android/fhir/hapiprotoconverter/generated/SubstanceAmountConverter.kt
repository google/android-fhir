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
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.RangeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.fhir.r4.core.Quantity
import com.google.fhir.r4.core.Range
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.SubstanceAmount
import java.lang.IllegalArgumentException
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

public object SubstanceAmountConverter {
  @JvmStatic
  private fun SubstanceAmount.AmountX.substanceAmountAmountToHapi(): Type {
    if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType) {
      return (this.getRange()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType) {
      return (this.getStringValue()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for SubstanceAmount.amount[x]")
  }

  @JvmStatic
  private fun Type.substanceAmountAmountToProto(): SubstanceAmount.AmountX {
    val protoValue = SubstanceAmount.AmountX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Quantity) {
      protoValue.setQuantity(this.toProto())
    }
    if (this is org.hl7.fhir.r4.model.Range) {
      protoValue.setRange(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  public fun SubstanceAmount.toHapi(): org.hl7.fhir.r4.model.SubstanceAmount {
    val hapiValue = org.hl7.fhir.r4.model.SubstanceAmount()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (modifierExtensionCount > 0) {
      hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    }
    if (hasAmount()) {
      hapiValue.setAmount(amount.substanceAmountAmountToHapi())
    }
    if (hasAmountType()) {
      hapiValue.setAmountType(amountType.toHapi())
    }
    if (hasAmountText()) {
      hapiValue.setAmountTextElement(amountText.toHapi())
    }
    if (hasReferenceRange()) {
      hapiValue.setReferenceRange(referenceRange.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.SubstanceAmount.toProto(): SubstanceAmount {
    val protoValue = SubstanceAmount.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasAmount()) {
      protoValue.setAmount(amount.substanceAmountAmountToProto())
    }
    if (hasAmountType()) {
      protoValue.setAmountType(amountType.toProto())
    }
    if (hasAmountText()) {
      protoValue.setAmountText(amountTextElement.toProto())
    }
    if (hasReferenceRange()) {
      protoValue.setReferenceRange(referenceRange.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.SubstanceAmount.SubstanceAmountReferenceRangeComponent.toProto():
    SubstanceAmount.ReferenceRange {
    val protoValue =
      SubstanceAmount.ReferenceRange.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasLowLimit()) {
      protoValue.setLowLimit(lowLimit.toProto())
    }
    if (hasHighLimit()) {
      protoValue.setHighLimit(highLimit.toProto())
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun SubstanceAmount.ReferenceRange.toHapi():
    org.hl7.fhir.r4.model.SubstanceAmount.SubstanceAmountReferenceRangeComponent {
    val hapiValue = org.hl7.fhir.r4.model.SubstanceAmount.SubstanceAmountReferenceRangeComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    if (hasLowLimit()) {
      hapiValue.setLowLimit(lowLimit.toHapi())
    }
    if (hasHighLimit()) {
      hapiValue.setHighLimit(highLimit.toHapi())
    }
    return hapiValue
  }
}
