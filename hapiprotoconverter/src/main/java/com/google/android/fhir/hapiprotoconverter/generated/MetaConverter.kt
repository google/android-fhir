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

import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IdConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Meta
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic

public object MetaConverter {
  @JvmStatic
  public fun Meta.toHapi(): org.hl7.fhir.r4.model.Meta {
    val hapiValue = org.hl7.fhir.r4.model.Meta()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setVersionIdElement(versionId.toHapi())
    hapiValue.setLastUpdatedElement(lastUpdated.toHapi())
    hapiValue.setSourceElement(source.toHapi())
    hapiValue.setProfile(profileList.map { it.toHapi() })
    hapiValue.setSecurity(securityList.map { it.toHapi() })
    hapiValue.setTag(tagList.map { it.toHapi() })
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.Meta.toProto(): Meta {
    val protoValue =
      Meta.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .setVersionId(versionIdElement.toProto())
        .setLastUpdated(lastUpdatedElement.toProto())
        .setSource(sourceElement.toProto())
        .addAllProfile(profile.map { it.toProto() })
        .addAllSecurity(security.map { it.toProto() })
        .addAllTag(tag.map { it.toProto() })
        .build()
    return protoValue
  }
}
