package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Xhtml
import org.hl7.fhir.r4.model.XhtmlType

/**
 * contains functions that convert between the hapi and proto representations of xhtml
 */
public object XhtmlConverter {
  /**
   * returns the proto Xhtml equivalent of the hapi XhtmlType
   */
  public fun XhtmlType.toProto(): Xhtml {
    val protoValue = Xhtml.newBuilder()
    if (value!=null) protoValue.setValue(value.value)
    return protoValue.build()
  }

  /**
   * returns the hapi XhtmlType equivalent of the proto Xhtml
   */
  public fun Xhtml.toHapi(): XhtmlType {
    val hapiValue = XhtmlType()
//    hapiValue.value = value
    return hapiValue
  }
}
