package com.google.android.fhir.hapiprotoconverter.generated

import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.fhir.r4.core.Annotation
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String
import java.lang.IllegalArgumentException
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

public object AnnotationConverter {
  private fun Annotation.AuthorX.annotationAuthorToHapi(): Type {
    if (this.getReference() != Reference.newBuilder().defaultInstanceForType ) {
      return (this.getReference()).toHapi()
    }
    if (this.getStringValue() != String.newBuilder().defaultInstanceForType ) {
      return (this.getStringValue()).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Annotation.author[x]")
  }

  private fun Type.annotationAuthorToProto(): Annotation.AuthorX {
    val protoValue = Annotation.AuthorX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.setReference(this.toProto())
    }
    if (this is StringType) {
      protoValue.setStringValue(this.toProto())
    }
    return protoValue.build()
  }

  public fun Annotation.toHapi(): org.hl7.fhir.r4.model.Annotation {
    val hapiValue = org.hl7.fhir.r4.model.Annotation()
    hapiValue.id = id.value 
    hapiValue.setExtension(extensionList.map{it.toHapi()})
    hapiValue.setAuthor(author.annotationAuthorToHapi())
    hapiValue.setTimeElement(time.toHapi())
    hapiValue.setTextElement(text.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Annotation.toProto(): Annotation {
    val protoValue = Annotation.newBuilder()
    .setId(String.newBuilder().setValue(id))
    .addAllExtension(extension.map{it.toProto()})
    .setAuthor(author.annotationAuthorToProto())
    .setTime(timeElement.toProto())
    .setText(textElement.toProto())
    .build()
    return protoValue
  }
}
