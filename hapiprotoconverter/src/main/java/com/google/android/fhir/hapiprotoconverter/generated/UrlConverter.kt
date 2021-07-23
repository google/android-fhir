package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Url
import org.hl7.fhir.r4.model.UrlType

/**
 * contains functions that convert between the hapi and proto representations of url
 */
public object UrlConverter {
  /**
   * returns the proto Url equivalent of the hapi UrlType
   */
  public fun UrlType.toProto(): Url {
    val protoValue = Url.newBuilder()
    if (value!=null) protoValue.setValue(value)
    return protoValue.build()
  }

  /**
   * returns the hapi UrlType equivalent of the proto Url
   */
  public fun Url.toHapi(): UrlType {
    val hapiValue = UrlType()
    hapiValue.value = value
    return hapiValue
  }
}
