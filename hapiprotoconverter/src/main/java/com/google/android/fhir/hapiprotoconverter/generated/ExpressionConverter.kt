package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Expression
import com.google.fhir.r4.core.String

public object ExpressionConverter {
  public fun Expression.toHapi(): org.hl7.fhir.r4.model.Expression {
    val hapiValue = org.hl7.fhir.r4.model.Expression()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setDescriptionElement(description.toHapi())
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setLanguageElement(language.toHapi())
    hapiValue.setExpressionElement(expression.toHapi())
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
    .setExpression(expressionElement.toProto())
    .setReference(referenceElement.toProto())
    .build()
    return protoValue
  }
}
