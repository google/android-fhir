// contains functions that convert between the hapi and proto representations of boolean
package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Boolean
import org.hl7.fhir.r4.model.BooleanType

public object BooleanConverter {
  /**
   * returns the proto Boolean equivalent of the hapi BooleanType
   */
  public fun BooleanType.toProto(): Boolean {
    val protoValue = Boolean.newBuilder()
    if (value!=null) protoValue.setValue(value)
    return protoValue.build()
  }

  /**
   * returns the hapi BooleanType equivalent of the proto Boolean
   */
  public fun Boolean.toHapi(): BooleanType {
    val hapiValue = BooleanType()
    hapiValue.value = value
    return hapiValue
  }
}
