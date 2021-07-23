package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Integer
import org.hl7.fhir.r4.model.IntegerType

/**
 * contains functions that convert between the hapi and proto representations of integer
 */
public object IntegerConverter {
  /**
   * returns the proto Integer equivalent of the hapi IntegerType
   */
  public fun IntegerType.toProto(): Integer {
    val protoValue = Integer.newBuilder()
    if (value!=null) protoValue.setValue(value)
    return protoValue.build()
  }

  /**
   * returns the hapi IntegerType equivalent of the proto Integer
   */
  public fun Integer.toHapi(): IntegerType {
    val hapiValue = IntegerType()
    hapiValue.value = value
    return hapiValue
  }
}
