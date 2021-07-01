package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.String
import org.hl7.fhir.r4.model.StringType

public object StringConverter {
  public fun StringType.toProto(): String {
    val protoValue = String.newBuilder()
    .setValue(value)
    .build()
    return protoValue
  }

  public fun String.toHapi(): StringType {
    val hapiValue = StringType()
    hapiValue.value = value
    return hapiValue
  }
}
