package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Canonical
import org.hl7.fhir.r4.model.CanonicalType

public object CanonicalConverter {
  public fun CanonicalType.toProto(): Canonical {
    val protoValue = Canonical.newBuilder()
    .setValue(value)
    .build()
    return protoValue
  }

  public fun Canonical.toHapi(): CanonicalType {
    val hapiValue = CanonicalType()
    hapiValue.value = value
    return hapiValue
  }
}
