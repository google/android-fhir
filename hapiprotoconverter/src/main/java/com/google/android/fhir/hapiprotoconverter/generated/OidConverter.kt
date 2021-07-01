package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Oid
import org.hl7.fhir.r4.model.OidType

public object OidConverter {
  public fun OidType.toProto(): Oid {
    val protoValue = Oid.newBuilder()
    .setValue(value)
    .build()
    return protoValue
  }

  public fun Oid.toHapi(): OidType {
    val hapiValue = OidType()
    hapiValue.value = value
    return hapiValue
  }
}
