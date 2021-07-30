// contains functions that convert between the hapi and proto representations of code
package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Code
import org.hl7.fhir.r4.model.CodeType

public object CodeConverter {
  /**
   * returns the proto Code equivalent of the hapi CodeType
   */
  public fun CodeType.toProto(): Code {
    val protoValue = Code.newBuilder()
    if (value!=null) protoValue.setValue(value)
    return protoValue.build()
  }

  /**
   * returns the hapi CodeType equivalent of the proto Code
   */
  public fun Code.toHapi(): CodeType {
    val hapiValue = CodeType()
    hapiValue.value = value
    return hapiValue
  }
}
