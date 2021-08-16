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

import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.AttachmentConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MarkdownConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UrlConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UrlConverter.toProto
import com.google.fhir.r4.core.RelatedArtifact
import com.google.fhir.r4.core.RelatedArtifactTypeCode
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic

public object RelatedArtifactConverter {
  @JvmStatic
  public fun RelatedArtifact.toHapi(): org.hl7.fhir.r4.model.RelatedArtifact {
    val hapiValue = org.hl7.fhir.r4.model.RelatedArtifact()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    hapiValue.setType(
      org.hl7.fhir.r4.model.RelatedArtifact.RelatedArtifactType.valueOf(
        type.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasLabel()) {
      hapiValue.setLabelElement(label.toHapi())
    }
    if (hasDisplay()) {
      hapiValue.setDisplayElement(display.toHapi())
    }
    if (hasCitation()) {
      hapiValue.setCitationElement(citation.toHapi())
    }
    if (hasUrl()) {
      hapiValue.setUrlElement(url.toHapi())
    }
    if (hasDocument()) {
      hapiValue.setDocument(document.toHapi())
    }
    if (hasResource()) {
      hapiValue.setResourceElement(resource.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.RelatedArtifact.toProto(): RelatedArtifact {
    val protoValue = RelatedArtifact.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    protoValue.setType(
      RelatedArtifact.TypeCode.newBuilder()
        .setValue(
          RelatedArtifactTypeCode.Value.valueOf(
            type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasLabel()) {
      protoValue.setLabel(labelElement.toProto())
    }
    if (hasDisplay()) {
      protoValue.setDisplay(displayElement.toProto())
    }
    if (hasCitation()) {
      protoValue.setCitation(citationElement.toProto())
    }
    if (hasUrl()) {
      protoValue.setUrl(urlElement.toProto())
    }
    if (hasDocument()) {
      protoValue.setDocument(document.toProto())
    }
    if (hasResource()) {
      protoValue.setResource(resourceElement.toProto())
    }
    return protoValue.build()
  }
}
