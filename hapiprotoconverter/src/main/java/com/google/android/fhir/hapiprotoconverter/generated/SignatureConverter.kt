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
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.InstantConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.fhir.r4.core.Signature
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic

object SignatureConverter {
  @JvmStatic
  fun Signature.toHapi(): org.hl7.fhir.r4.model.Signature {
    val hapiValue = org.hl7.fhir.r4.model.Signature()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (typeCount > 0) {
      hapiValue.type = typeList.map { it.toHapi() }
    }
    if (hasWhen()) {
      hapiValue.whenElement = `when`.toHapi()
    }
    if (hasWho()) {
      hapiValue.who = who.toHapi()
    }
    if (hasOnBehalfOf()) {
      hapiValue.onBehalfOf = onBehalfOf.toHapi()
    }
    hapiValue.targetFormat = targetFormat.value.hapiCodeCheck()
    hapiValue.sigFormat = sigFormat.value.hapiCodeCheck()
    if (hasData()) {
      hapiValue.dataElement = data.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.Signature.toProto(): Signature {
    val protoValue = Signature.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.addAllType(type.map { it.toProto() })
    }
    if (hasWhen()) {
      protoValue.setWhen(whenElement.toProto())
    }
    if (hasWho()) {
      protoValue.who = who.toProto()
    }
    if (hasOnBehalfOf()) {
      protoValue.onBehalfOf = onBehalfOf.toProto()
    }
    protoValue.targetFormat =
      Signature.TargetFormatCode.newBuilder().setValue(targetFormat.protoCodeCheck()).build()
    protoValue.sigFormat =
      Signature.SigFormatCode.newBuilder().setValue(sigFormat.protoCodeCheck()).build()
    if (hasData()) {
      protoValue.data = dataElement.toProto()
    }
    return protoValue.build()
  }
}
