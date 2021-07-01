package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.PositiveInt
import org.hl7.fhir.r4.model.PositiveIntType

public object PositiveIntConverter {
  public fun PositiveIntType.toProto(): PositiveInt {
    val protoValue = PositiveInt.newBuilder()
    .setValue(value)
    .build()
    return protoValue
  }

  public fun PositiveInt.toHapi(): PositiveIntType {
    val hapiValue = PositiveIntType()
    hapiValue.value = value
    return hapiValue
  }
}
