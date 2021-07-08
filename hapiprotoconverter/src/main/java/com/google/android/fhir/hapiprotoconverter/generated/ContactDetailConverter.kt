package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactPointConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.fhir.r4.core.ContactDetail
import com.google.fhir.r4.core.String

public object ContactDetailConverter {
  public fun ContactDetail.toHapi(): org.hl7.fhir.r4.model.ContactDetail {
    val hapiValue = org.hl7.fhir.r4.model.ContactDetail()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setTelecom(telecomList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.ContactDetail.toProto(): ContactDetail {
    val protoValue = ContactDetail.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .setName(nameElement.toProto())
    .addAllTelecom(telecom.map{it.toProto()})
    .build()
    return protoValue
  }
}
