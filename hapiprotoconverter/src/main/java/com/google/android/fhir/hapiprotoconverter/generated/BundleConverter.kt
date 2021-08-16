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
import kotlin.jvm.JvmStatic

object BundleConverter {
  @JvmStatic
  fun Bundle.toHapi(): org.hl7.fhir.r4.model.Bundle {
    val hapiValue = org.hl7.fhir.r4.model.Bundle()
    hapiValue.id = id.value
    if (hasMeta()) {
      hapiValue.meta = meta.toHapi()
    }
    if (hasImplicitRules()) {
      hapiValue.implicitRulesElement = implicitRules.toHapi()
    }
    if (hasIdentifier()) {
      hapiValue.identifier = identifier.toHapi()
    }
    hapiValue.type =
      org.hl7.fhir.r4.model.Bundle.BundleType.valueOf(
        type.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasTimestamp()) {
      hapiValue.timestampElement = timestamp.toHapi()
    }
    if (hasTotal()) {
      hapiValue.totalElement = total.toHapi()
    }
    if (linkCount > 0) {
      hapiValue.link = linkList.map { it.toHapi() }
    }
    if (entryCount > 0) {
      hapiValue.entry = entryList.map { it.toHapi() }
    }
    if (hasSignature()) {
      hapiValue.signature = signature.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.Bundle.toProto(): Bundle {
    val protoValue = Bundle.newBuilder().setId(Id.newBuilder().setValue(id))
    if (hasMeta()) {
      protoValue.meta = meta.toProto()
    }
    if (hasImplicitRules()) {
      protoValue.implicitRules = implicitRulesElement.toProto()
    }
    if (hasIdentifier()) {
      protoValue.identifier = identifier.toProto()
    }
    protoValue.type =
      Bundle.TypeCode.newBuilder()
        .setValue(
          BundleTypeCode.Value.valueOf(
            type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasTimestamp()) {
      protoValue.timestamp = timestampElement.toProto()
    }
    if (hasTotal()) {
      protoValue.total = totalElement.toProto()
    }
    if (hasLink()) {
      protoValue.addAllLink(link.map { it.toProto() })
    }
    if (hasEntry()) {
      protoValue.addAllEntry(entry.map { it.toProto() })
    }
    if (hasSignature()) {
      protoValue.signature = signature.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Bundle.BundleLinkComponent.toProto(): Bundle.Link {
    val protoValue = Bundle.Link.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasRelation()) {
      protoValue.relation = relationElement.toProto()
    }
    if (hasUrl()) {
      protoValue.url = urlElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Bundle.BundleEntryComponent.toProto(): Bundle.Entry {
    val protoValue = Bundle.Entry.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasFullUrl()) {
      protoValue.fullUrl = fullUrlElement.toProto()
    }
    if (hasSearch()) {
      protoValue.search = search.toProto()
    }
    if (hasRequest()) {
      protoValue.request = request.toProto()
    }
    if (hasResponse()) {
      protoValue.response = response.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Bundle.BundleEntrySearchComponent.toProto():
    Bundle.Entry.Search {
    val protoValue = Bundle.Entry.Search.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.mode =
      Bundle.Entry.Search.ModeCode.newBuilder()
        .setValue(
          SearchEntryModeCode.Value.valueOf(
            mode.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasScore()) {
      protoValue.score = scoreElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Bundle.BundleEntryRequestComponent.toProto():
    Bundle.Entry.Request {
    val protoValue = Bundle.Entry.Request.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    protoValue.method =
      Bundle.Entry.Request.MethodCode.newBuilder()
        .setValue(
          HTTPVerbCode.Value.valueOf(
            method.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasUrl()) {
      protoValue.url = urlElement.toProto()
    }
    if (hasIfNoneMatch()) {
      protoValue.ifNoneMatch = ifNoneMatchElement.toProto()
    }
    if (hasIfModifiedSince()) {
      protoValue.ifModifiedSince = ifModifiedSinceElement.toProto()
    }
    if (hasIfMatch()) {
      protoValue.ifMatch = ifMatchElement.toProto()
    }
    if (hasIfNoneExist()) {
      protoValue.ifNoneExist = ifNoneExistElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun org.hl7.fhir.r4.model.Bundle.BundleEntryResponseComponent.toProto():
    Bundle.Entry.Response {
    val protoValue = Bundle.Entry.Response.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasModifierExtension()) {
      protoValue.addAllModifierExtension(modifierExtension.map { it.toProto() })
    }
    if (hasStatus()) {
      protoValue.status = statusElement.toProto()
    }
    if (hasLocation()) {
      protoValue.location = locationElement.toProto()
    }
    if (hasEtag()) {
      protoValue.etag = etagElement.toProto()
    }
    if (hasLastModified()) {
      protoValue.lastModified = lastModifiedElement.toProto()
    }
    return protoValue.build()
  }

  @JvmStatic
  private fun Bundle.Link.toHapi(): org.hl7.fhir.r4.model.Bundle.BundleLinkComponent {
    val hapiValue = org.hl7.fhir.r4.model.Bundle.BundleLinkComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasRelation()) {
      hapiValue.relationElement = relation.toHapi()
    }
    if (hasUrl()) {
      hapiValue.urlElement = url.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun Bundle.Entry.toHapi(): org.hl7.fhir.r4.model.Bundle.BundleEntryComponent {
    val hapiValue = org.hl7.fhir.r4.model.Bundle.BundleEntryComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasFullUrl()) {
      hapiValue.fullUrlElement = fullUrl.toHapi()
    }
    if (hasSearch()) {
      hapiValue.search = search.toHapi()
    }
    if (hasRequest()) {
      hapiValue.request = request.toHapi()
    }
    if (hasResponse()) {
      hapiValue.response = response.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun Bundle.Entry.Search.toHapi():
    org.hl7.fhir.r4.model.Bundle.BundleEntrySearchComponent {
    val hapiValue = org.hl7.fhir.r4.model.Bundle.BundleEntrySearchComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    hapiValue.mode =
      org.hl7.fhir.r4.model.Bundle.SearchEntryMode.valueOf(
        mode.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasScore()) {
      hapiValue.scoreElement = score.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun Bundle.Entry.Request.toHapi():
    org.hl7.fhir.r4.model.Bundle.BundleEntryRequestComponent {
    val hapiValue = org.hl7.fhir.r4.model.Bundle.BundleEntryRequestComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    hapiValue.method =
      org.hl7.fhir.r4.model.Bundle.HTTPVerb.valueOf(
        method.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasUrl()) {
      hapiValue.urlElement = url.toHapi()
    }
    if (hasIfNoneMatch()) {
      hapiValue.ifNoneMatchElement = ifNoneMatch.toHapi()
    }
    if (hasIfModifiedSince()) {
      hapiValue.ifModifiedSinceElement = ifModifiedSince.toHapi()
    }
    if (hasIfMatch()) {
      hapiValue.ifMatchElement = ifMatch.toHapi()
    }
    if (hasIfNoneExist()) {
      hapiValue.ifNoneExistElement = ifNoneExist.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  private fun Bundle.Entry.Response.toHapi():
    org.hl7.fhir.r4.model.Bundle.BundleEntryResponseComponent {
    val hapiValue = org.hl7.fhir.r4.model.Bundle.BundleEntryResponseComponent()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (modifierExtensionCount > 0) {
      hapiValue.modifierExtension = modifierExtensionList.map { it.toHapi() }
    }
    if (hasStatus()) {
      hapiValue.statusElement = status.toHapi()
    }
    if (hasLocation()) {
      hapiValue.locationElement = location.toHapi()
    }
    if (hasEtag()) {
      hapiValue.etagElement = etag.toHapi()
    }
    if (hasLastModified()) {
      hapiValue.lastModifiedElement = lastModified.toHapi()
    }
    return hapiValue
  }
}
