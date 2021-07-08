package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Coding
import com.google.fhir.r4.core.String

public object CodingConverter {
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
}
