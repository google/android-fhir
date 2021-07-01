package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Id
import org.hl7.fhir.r4.model.IdType

public object IdConverter {
  public fun IdType.toProto(): Id {
    val protoValue = Id.newBuilder()
    .setValue(value)
    .build()
    return protoValue
  }

  public fun Id.toHapi(): IdType {
    val hapiValue = IdType()
    hapiValue.value = value
    return hapiValue
  }
}
