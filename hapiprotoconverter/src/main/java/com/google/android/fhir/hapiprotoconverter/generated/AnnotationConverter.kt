/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

object AnnotationConverter {
  @JvmStatic
  private fun Annotation.AuthorX.annotationAuthorToHapi(): Type {
    if (this.reference != Reference.newBuilder().defaultInstanceForType) {
      return (this.reference).toHapi()
    }
    if (this.stringValue != String.newBuilder().defaultInstanceForType) {
      return (this.stringValue).toHapi()
    }
    throw IllegalArgumentException("Invalid Type for Annotation.author[x]")
  }

  @JvmStatic
  private fun Type.annotationAuthorToProto(): Annotation.AuthorX {
    val protoValue = Annotation.AuthorX.newBuilder()
    if (this is org.hl7.fhir.r4.model.Reference) {
      protoValue.reference = this.toProto()
    }
    if (this is StringType) {
      protoValue.stringValue = this.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  fun Annotation.toHapi(): org.hl7.fhir.r4.model.Annotation {
    val hapiValue = org.hl7.fhir.r4.model.Annotation()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (hasAuthor()) {
      hapiValue.author = author.annotationAuthorToHapi()
    }
    if (hasTime()) {
      hapiValue.timeElement = time.toHapi()
    }
    if (hasText()) {
      hapiValue.textElement = text.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.Annotation.toProto(): Annotation {
    val protoValue = Annotation.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasAuthor()) {
      protoValue.author = author.annotationAuthorToProto()
    }
    if (hasTime()) {
      protoValue.time = timeElement.toProto()
    }
    if (hasText()) {
      protoValue.text = textElement.toProto()
    }
    return protoValue.build()
  }
}
