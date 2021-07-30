// contains functions that convert between the hapi and proto representations of canonical
package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Canonical
import org.hl7.fhir.r4.model.CanonicalType

public object CanonicalConverter {
  /**
   * returns the proto Canonical equivalent of the hapi CanonicalType
   */
  public fun CanonicalType.toProto(): Canonical {
    val protoValue = Canonical.newBuilder()
    if (value!=null) protoValue.setValue(value)
    return protoValue.build()
  }

  /**
   * returns the hapi CanonicalType equivalent of the proto Canonical
   */
  public fun Canonical.toHapi(): CanonicalType {
    val hapiValue = CanonicalType()
    hapiValue.value = value
    return hapiValue
  }
}
