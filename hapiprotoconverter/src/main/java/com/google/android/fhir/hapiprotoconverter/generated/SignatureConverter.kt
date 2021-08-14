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

public object SignatureConverter {
  @JvmStatic
  public fun Signature.toHapi(): org.hl7.fhir.r4.model.Signature {
    val hapiValue = org.hl7.fhir.r4.model.Signature()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setType(typeList.map { it.toHapi() })
    hapiValue.setWhenElement(`when`.toHapi())
    hapiValue.setWho(who.toHapi())
    hapiValue.setOnBehalfOf(onBehalfOf.toHapi())
    hapiValue.setTargetFormat(targetFormat.value.hapiCodeCheck())
    hapiValue.setSigFormat(sigFormat.value.hapiCodeCheck())
    hapiValue.setDataElement(data.toHapi())
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.Signature.toProto(): Signature {
    val protoValue =
      Signature.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .addAllType(type.map { it.toProto() })
        .setWhen(whenElement.toProto())
        .setWho(who.toProto())
        .setOnBehalfOf(onBehalfOf.toProto())
        .setTargetFormat(
          Signature.TargetFormatCode.newBuilder().setValue(targetFormat.protoCodeCheck()).build()
        )
        .setSigFormat(
          Signature.SigFormatCode.newBuilder().setValue(sigFormat.protoCodeCheck()).build()
        )
        .setData(dataElement.toProto())
        .build()
    return protoValue
  }
}
