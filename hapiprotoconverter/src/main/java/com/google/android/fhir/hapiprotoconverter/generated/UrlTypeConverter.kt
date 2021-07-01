package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Url
import org.hl7.fhir.r4.model.UrlType

public object UrlConverter {
  public fun UrlType.toProto(): Url {
    val protoValue = Url.newBuilder()
    .setValue(value)
    .build()
    return protoValue
  }

  public fun Url.toHapi(): UrlType {
    val hapiValue = UrlType()
    hapiValue.value = value
    return hapiValue
  }
}
