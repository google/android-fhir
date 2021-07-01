package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Integer
import org.hl7.fhir.r4.model.IntegerType

public object IntegerConverter {
  public fun IntegerType.toProto(): Integer {
    val protoValue = Integer.newBuilder()
    .setValue(value)
    .build()
    return protoValue
  }

  public fun Integer.toHapi(): IntegerType {
    val hapiValue = IntegerType()
    hapiValue.value = value
    return hapiValue
  }
}
