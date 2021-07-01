package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.UnsignedInt
import org.hl7.fhir.r4.model.UnsignedIntType

public object UnsignedIntConverter {
  public fun UnsignedIntType.toProto(): UnsignedInt {
    val protoValue = UnsignedInt.newBuilder()
    .setValue(value)
    .build()
    return protoValue
  }

  public fun UnsignedInt.toHapi(): UnsignedIntType {
    val hapiValue = UnsignedIntType()
    hapiValue.value = value
    return hapiValue
  }
}
