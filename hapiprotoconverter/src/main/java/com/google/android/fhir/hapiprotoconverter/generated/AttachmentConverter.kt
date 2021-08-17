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

import com.google.android.fhir.hapiprotoconverter.generated.Base64BinaryConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.Base64BinaryConverter.toProto
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

object AttachmentConverter {
  fun Attachment.toHapi(): org.hl7.fhir.r4.model.Attachment {
    val hapiValue = org.hl7.fhir.r4.model.Attachment()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    hapiValue.contentType = contentType.value.hapiCodeCheck()
    if (hasData()) {
      hapiValue.dataElement = data.toHapi()
    }
    if (hasUrl()) {
      hapiValue.urlElement = url.toHapi()
    }
    if (hasSize()) {
      hapiValue.sizeElement = size.toHapi()
    }
    if (hasHash()) {
      hapiValue.hashElement = hash.toHapi()
    }
    if (hasTitle()) {
      hapiValue.titleElement = title.toHapi()
    }
    if (hasCreation()) {
      hapiValue.creationElement = creation.toHapi()
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.Attachment.toProto(): Attachment {
    val protoValue = Attachment.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    protoValue.contentType =
      Attachment.ContentTypeCode.newBuilder().setValue(contentType.protoCodeCheck()).build()
    if (hasData()) {
      protoValue.data = dataElement.toProto()
    }
    if (hasUrl()) {
      protoValue.url = urlElement.toProto()
    }
    if (hasSize()) {
      protoValue.size = sizeElement.toProto()
    }
    if (hasHash()) {
      protoValue.hash = hashElement.toProto()
    }
    if (hasTitle()) {
      protoValue.title = titleElement.toProto()
    }
    if (hasCreation()) {
      protoValue.creation = creationElement.toProto()
    }
    return protoValue.build()
  }
}
