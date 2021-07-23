package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Uuid
import org.hl7.fhir.r4.model.UuidType

/**
 * contains functions that convert between the hapi and proto representations of uuid
 */
public object UuidConverter {
  /**
   * returns the proto Uuid equivalent of the hapi UuidType
   */
  public fun UuidType.toProto(): Uuid {
    val protoValue = Uuid.newBuilder()
    if (value!=null) protoValue.setValue(value)
    return protoValue.build()
  }

  /**
   * returns the hapi UuidType equivalent of the proto Uuid
   */
  public fun Uuid.toHapi(): UuidType {
    val hapiValue = UuidType()
    hapiValue.value = value
    return hapiValue
  }
}
