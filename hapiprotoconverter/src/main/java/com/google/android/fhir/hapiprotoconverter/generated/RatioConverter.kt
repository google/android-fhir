package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Ratio
import com.google.fhir.r4.core.String

public fun Ratio.toHapi(): org.hl7.fhir.r4.model.Ratio {
  val hapiValue = org.hl7.fhir.r4.model.Ratio()
  hapiValue.id = id.value 
  hapiValue.setExtension(extensionList.map{it.toHapi()})
  hapiValue.setNumerator(numerator.toHapi())
  hapiValue.setDenominator(denominator.toHapi())
  return hapiValue
}

public fun org.hl7.fhir.r4.model.Ratio.toProto(): Ratio {
  val protoValue = Ratio.newBuilder()
  .setId(String.newBuilder().setValue(id))
  .addAllExtension(extension.map{it.toProto()})
  .setNumerator(numerator.toProto())
  .setDenominator(denominator.toProto())
  .build()
  return protoValue
}
