package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Boolean
import org.hl7.fhir.r4.model.BooleanType

public object BooleanConverter {
  public fun BooleanType.toProto(): Boolean {
    val protoValue = Boolean.newBuilder()
    .setValue(value)
    .build()
    return protoValue
  }

  public fun Boolean.toHapi(): BooleanType {
    val hapiValue = BooleanType()
    hapiValue.value = value
    return hapiValue
  }
}
