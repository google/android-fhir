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
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.MetaConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Binary
import com.google.fhir.r4.core.Id
import kotlin.jvm.JvmStatic

object BinaryConverter {
  @JvmStatic
  fun Binary.toHapi(): org.hl7.fhir.r4.model.Binary {
    val hapiValue = org.hl7.fhir.r4.model.Binary()
    hapiValue.id = id.value
    if (hasMeta()) {
      hapiValue.meta = meta.toHapi()
    }
    if (hasImplicitRules()) {
      hapiValue.implicitRulesElement = implicitRules.toHapi()
    }
    hapiValue.contentType = contentType.value.hapiCodeCheck()
    if (hasSecurityContext()) {
      hapiValue.securityContext = securityContext.toHapi()
    }
    if (hasData()) {
      hapiValue.dataElement = data.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.Binary.toProto(): Binary {
    val protoValue = Binary.newBuilder().setId(Id.newBuilder().setValue(id))
    if (hasMeta()) {
      protoValue.meta = meta.toProto()
    }
    if (hasImplicitRules()) {
      protoValue.implicitRules = implicitRulesElement.toProto()
    }
    protoValue.contentType =
      Binary.ContentTypeCode.newBuilder().setValue(contentType.protoCodeCheck()).build()
    if (hasSecurityContext()) {
      protoValue.securityContext = securityContext.toProto()
    }
    if (hasData()) {
      protoValue.data = dataElement.toProto()
    }
    return protoValue.build()
  }
}
