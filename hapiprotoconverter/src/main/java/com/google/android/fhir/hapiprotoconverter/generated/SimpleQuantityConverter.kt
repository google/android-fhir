package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.SimpleQuantity
import com.google.fhir.r4.core.String

public fun SimpleQuantity.toHapi(): org.hl7.fhir.r4.model.SimpleQuantity {
  val hapiValue = org.hl7.fhir.r4.model.SimpleQuantity()
  hapiValue.id = id.value 
  hapiValue.setExtension(extensionList.map{it.toHapi()})
  hapiValue.setValueElement(value.toHapi())
  hapiValue.setUnitElement(unit.toHapi())
  hapiValue.setSystemElement(system.toHapi())
  hapiValue.setCodeElement(code.toHapi())
  return hapiValue
}

public fun org.hl7.fhir.r4.model.SimpleQuantity.toProto(): SimpleQuantity {
  val protoValue = SimpleQuantity.newBuilder()
  .setId(String.newBuilder().setValue(id))
  .addAllExtension(extension.map{it.toProto()})
  .setValue(valueElement.toProto())
  .setUnit(unitElement.toProto())
  .setSystem(systemElement.toProto())
  .setCode(codeElement.toProto())
  .build()
  return protoValue
}
