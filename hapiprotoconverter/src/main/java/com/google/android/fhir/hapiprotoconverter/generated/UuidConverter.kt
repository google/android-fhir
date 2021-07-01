package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Uuid
import org.hl7.fhir.r4.model.UuidType

public object UuidConverter {
  public fun UuidType.toProto(): Uuid {
    val protoValue = Uuid.newBuilder()
    .setValue(value)
    .build()
    return protoValue
  }

  public fun Uuid.toHapi(): UuidType {
    val hapiValue = UuidType()
    hapiValue.value = value
    return hapiValue
  }
}
