package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Attachment
import com.google.fhir.r4.core.String

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
