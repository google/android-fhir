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

import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DecimalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.SimpleQuantity
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic

object SimpleQuantityConverter {
  @JvmStatic
  fun SimpleQuantity.toHapi(): org.hl7.fhir.r4.model.SimpleQuantity {
    val hapiValue = org.hl7.fhir.r4.model.SimpleQuantity()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (hasValue()) {
      hapiValue.valueElement = value.toHapi()
    }
    if (hasUnit()) {
      hapiValue.unitElement = unit.toHapi()
    }
    if (hasSystem()) {
      hapiValue.systemElement = system.toHapi()
    }
    if (hasCode()) {
      hapiValue.codeElement = code.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.SimpleQuantity.toProto(): SimpleQuantity {
    val protoValue = SimpleQuantity.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasValue()) {
      protoValue.value = valueElement.toProto()
    }
    if (hasUnit()) {
      protoValue.unit = unitElement.toProto()
    }
    if (hasSystem()) {
      protoValue.system = systemElement.toProto()
    }
    if (hasCode()) {
      protoValue.code = codeElement.toProto()
    }
    return protoValue.build()
  }
}
