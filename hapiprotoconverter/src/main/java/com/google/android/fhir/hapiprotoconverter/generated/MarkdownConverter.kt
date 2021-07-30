// contains functions that convert between the hapi and proto representations of markdown
package com.google.android.fhir.hapiprotoconverter.generated

import com.google.fhir.r4.core.Markdown
import org.hl7.fhir.r4.model.MarkdownType

public object MarkdownConverter {
  /**
   * returns the proto Markdown equivalent of the hapi MarkdownType
   */
  public fun MarkdownType.toProto(): Markdown {
    val protoValue = Markdown.newBuilder()
    if (value!=null) protoValue.setValue(value)
    return protoValue.build()
  }

  /**
   * returns the hapi MarkdownType equivalent of the proto Markdown
   */
  public fun Markdown.toHapi(): MarkdownType {
    val hapiValue = MarkdownType()
    hapiValue.value = value
    return hapiValue
  }
}
