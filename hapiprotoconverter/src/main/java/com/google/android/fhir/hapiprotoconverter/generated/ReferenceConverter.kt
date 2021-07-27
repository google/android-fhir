package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String

public fun Reference.toHapi(): org.hl7.fhir.r4.model.Reference {
  val hapiValue = org.hl7.fhir.r4.model.Reference()
  hapiValue.id = id.value 
  hapiValue.setExtension(extensionList.map{it.toHapi()})
  hapiValue.setTypeElement(type.toHapi())
  hapiValue.setIdentifier(identifier.toHapi())
  hapiValue.setDisplayElement(display.toHapi())
  return hapiValue
}

public fun org.hl7.fhir.r4.model.Reference.toProto(): Reference {
  val protoValue = Reference.newBuilder()
  .setId(String.newBuilder().setValue(id))
  .addAllExtension(extension.map{it.toProto()})
  .setType(typeElement.toProto())
  .setIdentifier(identifier.toProto())
  .setDisplay(displayElement.toProto())
  .build()
  return protoValue
}
