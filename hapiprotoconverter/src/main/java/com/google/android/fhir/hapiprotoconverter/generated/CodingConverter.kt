package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Coding
import com.google.fhir.r4.core.String

public fun Coding.toHapi(): org.hl7.fhir.r4.model.Coding {
  val hapiValue = org.hl7.fhir.r4.model.Coding()
  hapiValue.id = id.value 
  hapiValue.setExtension(extensionList.map{it.toHapi()})
  hapiValue.setSystemElement(system.toHapi())
  hapiValue.setVersionElement(version.toHapi())
  hapiValue.setCodeElement(code.toHapi())
  hapiValue.setDisplayElement(display.toHapi())
  hapiValue.setUserSelectedElement(userSelected.toHapi())
  return hapiValue
}

public fun org.hl7.fhir.r4.model.Coding.toProto(): Coding {
  val protoValue = Coding.newBuilder()
  .setId(String.newBuilder().setValue(id))
  .addAllExtension(extension.map{it.toProto()})
  .setSystem(systemElement.toProto())
  .setVersion(versionElement.toProto())
  .setCode(codeElement.toProto())
  .setDisplay(displayElement.toProto())
  .setUserSelected(userSelectedElement.toProto())
  .build()
  return protoValue
}
