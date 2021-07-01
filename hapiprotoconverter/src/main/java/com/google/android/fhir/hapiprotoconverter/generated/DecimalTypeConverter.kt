package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Decimal
import org.hl7.fhir.r4.model.DecimalType

public object DecimalConverter {
  public fun DecimalType.toProto(): Decimal {
    val protoValue = Decimal.newBuilder()
    .setValue(valueAsString)
    .build()
    return protoValue
  }

  public fun Decimal.toHapi(): DecimalType {
    val hapiValue = DecimalType()
    hapiValue.valueAsString = value
    return hapiValue
  }
}
