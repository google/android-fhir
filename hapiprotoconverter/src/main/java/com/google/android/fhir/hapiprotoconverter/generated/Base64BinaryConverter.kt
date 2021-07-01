package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Base64Binary
import com.google.fhir.shaded.protobuf.ByteString
import org.hl7.fhir.r4.model.Base64BinaryType

public object Base64BinaryConverter {
  public fun Base64BinaryType.toProto(): Base64Binary {
    val protoValue = Base64Binary.newBuilder()
    .setValue( ByteString.copyFrom((valueAsString).toByteArray()))
    .build()
    return protoValue
  }

  public fun Base64Binary.toHapi(): Base64BinaryType {
    val hapiValue = Base64BinaryType()
    hapiValue.value = value.toStringUtf8().toByteArray()
    return hapiValue
  }
}
