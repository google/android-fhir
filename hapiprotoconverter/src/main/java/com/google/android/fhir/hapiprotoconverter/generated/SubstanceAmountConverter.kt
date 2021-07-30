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
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

public object SubstanceAmountConverter {
  private fun SubstanceAmount.AmountX.substanceAmountAmountToHapi(): Type {
    if (this.getQuantity() != Quantity.newBuilder().defaultInstanceForType ) {
      return (this.getQuantity()).toHapi()
    }
    if (this.getRange() != Range.newBuilder().defaultInstanceForType ) {
      return (this.getRange()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType ) {
      return (this.getStringValue()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for SubstanceAmount.amount[x]")
  }

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

  public fun SubstanceAmount.toHapi(): org.hl7.fhir.r4.model.SubstanceAmount {
    val hapiValue = org.hl7.fhir.r4.model.SubstanceAmount()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setModifierExtension(modifierExtensionList.map{it.toHapi()})
    hapiValue.setAmount(amount.substanceAmountAmountToHapi())
    hapiValue.setAmountType(amountType.toHapi())
    hapiValue.setAmountTextElement(amountText.toHapi())
    hapiValue.setReferenceRange(referenceRange.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.SubstanceAmount.toProto(): SubstanceAmount {
    val protoValue = SubstanceAmount.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .addAllModifierExtension(modifierExtension.map{it.toProto()})
    .setAmount(amount.substanceAmountAmountToProto())
    .setAmountType(amountType.toProto())
    .setAmountText(amountTextElement.toProto())
    .setReferenceRange(referenceRange.toProto())
    .build()
    return protoValue
  }

  private
      fun org.hl7.fhir.r4.model.SubstanceAmount.SubstanceAmountReferenceRangeComponent.toProto():
      SubstanceAmount.ReferenceRange {
    val protoValue = SubstanceAmount.ReferenceRange.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .setLowLimit(lowLimit.toProto())
    .setHighLimit(highLimit.toProto())
    .build()
    return protoValue
  }

  private fun SubstanceAmount.ReferenceRange.toHapi():
      org.hl7.fhir.r4.model.SubstanceAmount.SubstanceAmountReferenceRangeComponent {
    val hapiValue = org.hl7.fhir.r4.model.SubstanceAmount.SubstanceAmountReferenceRangeComponent()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setLowLimit(lowLimit.toHapi())
    hapiValue.setHighLimit(highLimit.toHapi())
    return hapiValue
  }
}
