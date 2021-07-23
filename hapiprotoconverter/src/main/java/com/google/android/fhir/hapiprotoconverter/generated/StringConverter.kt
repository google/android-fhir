package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.String
import org.hl7.fhir.r4.model.StringType
/**
   * returns the proto String equivalent of the hapi StringType
   */
  public fun StringType.toProto(): String {
    val protoValue = String.newBuilder()
    if (value!=null) protoValue.setValue(value)
    return protoValue.build()
  }

  /**
   * returns the hapi StringType equivalent of the proto String
   */
  public fun String.toHapi(): StringType {
    val hapiValue = StringType()
    hapiValue.value = value
    return hapiValue
  }
