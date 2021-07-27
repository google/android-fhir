package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Expression
import com.google.fhir.r4.core.String

public fun Expression.toHapi(): org.hl7.fhir.r4.model.Expression {
  val hapiValue = org.hl7.fhir.r4.model.Expression()
  hapiValue.id = id.value 
  hapiValue.setExtension(extensionList.map{it.toHapi()})
  hapiValue.setDescriptionElement(description.toHapi())
  hapiValue.setNameElement(name.toHapi())
  hapiValue.setLanguageElement(language.toHapi())
  hapiValue.setReferenceElement(reference.toHapi())
  return hapiValue
}

public fun org.hl7.fhir.r4.model.Expression.toProto(): Expression {
  val protoValue = Expression.newBuilder()
  .setId(String.newBuilder().setValue(id))
  .addAllExtension(extension.map{it.toProto()})
  .setDescription(descriptionElement.toProto())
  .setName(nameElement.toProto())
  .setLanguage(languageElement.toProto())
  .setReference(referenceElement.toProto())
  .build()
  return protoValue
}
