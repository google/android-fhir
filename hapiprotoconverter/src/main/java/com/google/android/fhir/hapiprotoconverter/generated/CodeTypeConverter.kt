package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Code
import org.hl7.fhir.r4.model.CodeType

public object CodeConverter {
  public fun CodeType.toProto(): Code {
    val protoValue = Code.newBuilder()
    .setValue(value)
    .build()
    return protoValue
  }

  public fun Code.toHapi(): CodeType {
    val hapiValue = CodeType()
    hapiValue.value = value
    return hapiValue
  }
}
