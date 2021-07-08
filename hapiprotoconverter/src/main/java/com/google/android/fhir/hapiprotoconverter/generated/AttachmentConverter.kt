package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.Base64BinaryConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.Base64BinaryConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UrlConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UrlConverter.toProto
import com.google.fhir.r4.core.Attachment
import com.google.fhir.r4.core.String

public object AttachmentConverter {
  public fun Attachment.toHapi(): org.hl7.fhir.r4.model.Attachment {
    val hapiValue = org.hl7.fhir.r4.model.Attachment()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setContentType(contentType.value)
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setDataElement(data.toHapi())
    hapiValue.setUrlElement(url.toHapi())
    hapiValue.setSizeElement(size.toHapi())
    hapiValue.setHashElement(hash.toHapi())
    hapiValue.setTitleElement(title.toHapi())
    hapiValue.setCreationElement(creation.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Attachment.toProto(): Attachment {
    val protoValue = Attachment.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .setContentType(Attachment.ContentTypeCode.newBuilder().setValue(contentType).build())
    .setLanguage(languageElement.toProto())
    .setData(dataElement.toProto())
    .setUrl(urlElement.toProto())
    .setSize(sizeElement.toProto())
    .setHash(hashElement.toProto())
    .setTitle(titleElement.toProto())
    .setCreation(creationElement.toProto())
    .build()
    return protoValue
  }
}
