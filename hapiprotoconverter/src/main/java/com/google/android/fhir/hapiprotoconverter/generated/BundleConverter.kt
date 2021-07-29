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

import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SignatureConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SignatureConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UnsignedIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Bundle
import com.google.fhir.r4.core.Bundle.Entry
import com.google.fhir.r4.core.Bundle.Entry.Request
import com.google.fhir.r4.core.Bundle.Entry.Search
import com.google.fhir.r4.core.BundleTypeCode
import com.google.fhir.r4.core.HTTPVerbCode
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.SearchEntryModeCode
import com.google.fhir.r4.core.String

public object BundleConverter {
  public fun Bundle.toHapi(): org.hl7.fhir.r4.model.Bundle {
    val hapiValue = org.hl7.fhir.r4.model.Bundle()
    hapiValue.id = id.value
    hapiValue.setMeta(meta.toHapi())
    hapiValue.setImplicitRulesElement(implicitRules.toHapi())
    hapiValue.setIdentifier(identifier.toHapi())
    hapiValue.setType(
      org.hl7.fhir.r4.model.Bundle.BundleType.valueOf(type.value.name.replace("_", ""))
    )
    hapiValue.setTimestampElement(timestamp.toHapi())
    hapiValue.setTotalElement(total.toHapi())
    hapiValue.setLink(linkList.map { it.toHapi() })
    hapiValue.setEntry(entryList.map { it.toHapi() })
    hapiValue.setSignature(signature.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Bundle.toProto(): Bundle {
    val protoValue =
      Bundle.newBuilder()
        .setId(Id.newBuilder().setValue(id))
        .setMeta(meta.toProto())
        .setImplicitRules(implicitRulesElement.toProto())
        .setIdentifier(identifier.toProto())
        .setType(
          Bundle.TypeCode.newBuilder()
            .setValue(BundleTypeCode.Value.valueOf(type.toCode().replace("-", "_").toUpperCase()))
            .build()
        )
        .setTimestamp(timestampElement.toProto())
        .setTotal(totalElement.toProto())
        .addAllLink(link.map { it.toProto() })
        .addAllEntry(entry.map { it.toProto() })
        .setSignature(signature.toProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Bundle.BundleLinkComponent.toProto(): Bundle.Link {
    val protoValue =
      Bundle.Link.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setRelation(relationElement.toProto())
        .setUrl(urlElement.toProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Bundle.BundleEntryComponent.toProto(): Bundle.Entry {
    val protoValue =
      Bundle.Entry.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setFullUrl(fullUrlElement.toProto())
        .setSearch(search.toProto())
        .setRequest(request.toProto())
        .setResponse(response.toProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Bundle.BundleEntrySearchComponent.toProto():
    Bundle.Entry.Search {
    val protoValue =
      Bundle.Entry.Search.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setMode(
          Bundle.Entry.Search.ModeCode.newBuilder()
            .setValue(
              SearchEntryModeCode.Value.valueOf(mode.toCode().replace("-", "_").toUpperCase())
            )
            .build()
        )
        .setScore(scoreElement.toProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Bundle.BundleEntryRequestComponent.toProto():
    Bundle.Entry.Request {
    val protoValue =
      Bundle.Entry.Request.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setMethod(
          Bundle.Entry.Request.MethodCode.newBuilder()
            .setValue(HTTPVerbCode.Value.valueOf(method.toCode().replace("-", "_").toUpperCase()))
            .build()
        )
        .setUrl(urlElement.toProto())
        .setIfNoneMatch(ifNoneMatchElement.toProto())
        .setIfModifiedSince(ifModifiedSinceElement.toProto())
        .setIfMatch(ifMatchElement.toProto())
        .setIfNoneExist(ifNoneExistElement.toProto())
        .build()
    return protoValue
  }

  private fun org.hl7.fhir.r4.model.Bundle.BundleEntryResponseComponent.toProto():
    Bundle.Entry.Response {
    val protoValue =
      Bundle.Entry.Response.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllModifierExtension(modifierExtension.map { it.toProto() })
        .setStatus(statusElement.toProto())
        .setLocation(locationElement.toProto())
        .setEtag(etagElement.toProto())
        .setLastModified(lastModifiedElement.toProto())
        .build()
    return protoValue
  }

  private fun Bundle.Link.toHapi(): org.hl7.fhir.r4.model.Bundle.BundleLinkComponent {
    val hapiValue = org.hl7.fhir.r4.model.Bundle.BundleLinkComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setRelationElement(relation.toHapi())
    hapiValue.setUrlElement(url.toHapi())
    return hapiValue
  }

  private fun Bundle.Entry.toHapi(): org.hl7.fhir.r4.model.Bundle.BundleEntryComponent {
    val hapiValue = org.hl7.fhir.r4.model.Bundle.BundleEntryComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setFullUrlElement(fullUrl.toHapi())
    hapiValue.setSearch(search.toHapi())
    hapiValue.setRequest(request.toHapi())
    hapiValue.setResponse(response.toHapi())
    return hapiValue
  }

  private fun Bundle.Entry.Search.toHapi():
    org.hl7.fhir.r4.model.Bundle.BundleEntrySearchComponent {
    val hapiValue = org.hl7.fhir.r4.model.Bundle.BundleEntrySearchComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setMode(
      org.hl7.fhir.r4.model.Bundle.SearchEntryMode.valueOf(mode.value.name.replace("_", ""))
    )
    hapiValue.setScoreElement(score.toHapi())
    return hapiValue
  }

  private fun Bundle.Entry.Request.toHapi():
    org.hl7.fhir.r4.model.Bundle.BundleEntryRequestComponent {
    val hapiValue = org.hl7.fhir.r4.model.Bundle.BundleEntryRequestComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setMethod(
      org.hl7.fhir.r4.model.Bundle.HTTPVerb.valueOf(method.value.name.replace("_", ""))
    )
    hapiValue.setUrlElement(url.toHapi())
    hapiValue.setIfNoneMatchElement(ifNoneMatch.toHapi())
    hapiValue.setIfModifiedSinceElement(ifModifiedSince.toHapi())
    hapiValue.setIfMatchElement(ifMatch.toHapi())
    hapiValue.setIfNoneExistElement(ifNoneExist.toHapi())
    return hapiValue
  }

  private fun Bundle.Entry.Response.toHapi():
    org.hl7.fhir.r4.model.Bundle.BundleEntryResponseComponent {
    val hapiValue = org.hl7.fhir.r4.model.Bundle.BundleEntryResponseComponent()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setModifierExtension(modifierExtensionList.map { it.toHapi() })
    hapiValue.setStatusElement(status.toHapi())
    hapiValue.setLocationElement(location.toHapi())
    hapiValue.setEtagElement(etag.toHapi())
    hapiValue.setLastModifiedElement(lastModified.toHapi())
    return hapiValue
  }
}
