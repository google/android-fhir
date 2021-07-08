package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ContactDetailConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.fhir.r4.core.Contributor
import com.google.fhir.r4.core.ContributorTypeCode
import com.google.fhir.r4.core.String

public object ContributorConverter {
  public fun Contributor.toHapi(): org.hl7.fhir.r4.model.Contributor {
    val hapiValue = org.hl7.fhir.r4.model.Contributor()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setType(org.hl7.fhir.r4.model.Contributor.ContributorType.valueOf(type.value.name.replace("_","")))
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setContact(contactList.map{it.toHapi()})
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Contributor.toProto(): Contributor {
    val protoValue = Contributor.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .setType(Contributor.TypeCode.newBuilder().setValue(ContributorTypeCode.Value.valueOf(type.toCode().replace("-",
        "_").toUpperCase())).build())
    .setName(nameElement.toProto())
    .addAllContact(contact.map{it.toProto()})
    .build()
    return protoValue
  }
}
