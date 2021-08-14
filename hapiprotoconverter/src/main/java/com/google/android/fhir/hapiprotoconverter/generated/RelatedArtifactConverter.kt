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
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setType(
      org.hl7.fhir.r4.model.RelatedArtifact.RelatedArtifactType.valueOf(
        type.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    hapiValue.setLabelElement(label.toHapi())
    hapiValue.setDisplayElement(display.toHapi())
    hapiValue.setCitationElement(citation.toHapi())
    hapiValue.setUrlElement(url.toHapi())
    hapiValue.setDocument(document.toHapi())
    hapiValue.setResourceElement(resource.toHapi())
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.RelatedArtifact.toProto(): RelatedArtifact {
    val protoValue =
      RelatedArtifact.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .setType(
          RelatedArtifact.TypeCode.newBuilder()
            .setValue(
              RelatedArtifactTypeCode.Value.valueOf(
                type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
            )
            .build()
        )
        .setLabel(labelElement.toProto())
        .setDisplay(displayElement.toProto())
        .setCitation(citationElement.toProto())
        .setUrl(urlElement.toProto())
        .setDocument(document.toProto())
        .setResource(resourceElement.toProto())
        .build()
    return protoValue
  }
}
