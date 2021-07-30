package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.fhir.r4.core.Money
import com.google.fhir.r4.core.String

public object MoneyConverter {
  public fun Money.toHapi(): org.hl7.fhir.r4.model.Money {
    val hapiValue = org.hl7.fhir.r4.model.Money()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setValueElement(value.toHapi())
    hapiValue.setCurrency(currency.value)
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Money.toProto(): Money {
    val protoValue = Money.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .setValue(valueElement.toProto())
    .setCurrency(Money.CurrencyCode.newBuilder().setValue(currency).build())
    .build()
    return protoValue
  }
}
