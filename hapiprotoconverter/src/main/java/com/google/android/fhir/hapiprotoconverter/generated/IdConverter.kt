// contains functions that convert between the hapi and proto representations of id
package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Id
import org.hl7.fhir.r4.model.IdType

public object IdConverter {
  /**
   * returns the proto Id equivalent of the hapi IdType
   */
  public fun IdType.toProto(): Id {
    val protoValue = Id.newBuilder()
    if (value!=null) protoValue.setValue(value)
    return protoValue.build()
  }

  /**
   * returns the hapi IdType equivalent of the proto Id
   */
  public fun Id.toHapi(): IdType {
    val hapiValue = IdType()
    hapiValue.value = value
    return hapiValue
  }
}
