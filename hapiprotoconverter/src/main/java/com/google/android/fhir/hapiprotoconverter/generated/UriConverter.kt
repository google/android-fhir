package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Uri
import org.hl7.fhir.r4.model.UriType

/**
 * contains functions that convert between the hapi and proto representations of uri
 */
public object UriConverter {
  /**
   * returns the proto Uri equivalent of the hapi UriType
   */
  public fun UriType.toProto(): Uri {
    val protoValue = Uri.newBuilder()
    if (value!=null) protoValue.setValue(value)
    return protoValue.build()
  }

  /**
   * returns the hapi UriType equivalent of the proto Uri
   */
  public fun Uri.toHapi(): UriType {
    val hapiValue = UriType()
    hapiValue.value = value
    return hapiValue
  }
}
