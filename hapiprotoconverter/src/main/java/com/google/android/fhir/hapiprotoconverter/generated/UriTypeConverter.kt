package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Uri
import org.hl7.fhir.r4.model.UriType

public object UriConverter {
  public fun UriType.toProto(): Uri {
    val protoValue = Uri.newBuilder()
    .setValue(value)
    .build()
    return protoValue
  }

  public fun Uri.toHapi(): UriType {
    val hapiValue = UriType()
    hapiValue.value = value
    return hapiValue
  }
}
