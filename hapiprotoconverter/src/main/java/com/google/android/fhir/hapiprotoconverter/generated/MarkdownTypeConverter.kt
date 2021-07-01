package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Markdown
import org.hl7.fhir.r4.model.MarkdownType

public object MarkdownConverter {
  public fun MarkdownType.toProto(): Markdown {
    val protoValue = Markdown.newBuilder()
    .setValue(value)
    .build()
    return protoValue
  }

  public fun Markdown.toHapi(): MarkdownType {
    val hapiValue = MarkdownType()
    hapiValue.value = value
    return hapiValue
  }
}
