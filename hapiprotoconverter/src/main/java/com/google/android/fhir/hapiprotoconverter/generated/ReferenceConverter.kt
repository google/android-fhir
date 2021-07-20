package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String

public object ReferenceConverter {
  public fun Reference.toHapi(): org.hl7.fhir.r4.model.Reference {
    val hapiValue = org.hl7.fhir.r4.model.Reference()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    //hapiValue.setReferenceElement(reference.toHapi())
    hapiValue.setTypeElement(type.toHapi())
    hapiValue.setIdentifier(identifier.toHapi())
    hapiValue.setDisplayElement(display.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Reference.toProto(): Reference {
    val protoValue = Reference.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    //.setReference(referenceElement.toProto())
    .setType(typeElement.toProto())
    .setIdentifier(identifier.toProto())
    .setDisplay(displayElement.toProto())
    .build()
    return protoValue
  }
}
