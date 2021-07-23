package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Base64Binary
import com.google.fhir.shaded.protobuf.ByteString
import org.hl7.fhir.r4.model.Base64BinaryType

/**
 * contains functions that convert between the hapi and proto representations of base64Binary
 */
public object Base64BinaryConverter {
  /**
   * returns the proto Base64Binary equivalent of the hapi Base64BinaryType
   */
  public fun Base64BinaryType.toProto(): Base64Binary {
    val protoValue = Base64Binary.newBuilder()
    if ( valueAsString!=null) protoValue.setValue(ByteString.copyFromUtf8( valueAsString))
    return protoValue.build()
  }

  /**
   * returns the hapi Base64BinaryType equivalent of the proto Base64Binary
   */
  public fun Base64Binary.toHapi(): Base64BinaryType {
    val hapiValue = Base64BinaryType()
    hapiValue.valueAsString = value.toStringUtf8()
    return hapiValue
  }
}
