package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.String

public fun Period.toHapi(): org.hl7.fhir.r4.model.Period {
  val hapiValue = org.hl7.fhir.r4.model.Period()
  hapiValue.id = id.value 
  hapiValue.setExtension(extensionList.map{it.toHapi()})
  hapiValue.setStartElement(start.toHapi())
  hapiValue.setEndElement(end.toHapi())
  return hapiValue
}

public fun org.hl7.fhir.r4.model.Period.toProto(): Period {
  val protoValue = Period.newBuilder()
  .setId(String.newBuilder().setValue(id))
  .addAllExtension(extension.map{it.toProto()})
  .setStart(startElement.toProto())
  .setEnd(endElement.toProto())
  .build()
  return protoValue
}
