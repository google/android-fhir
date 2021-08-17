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
import com.google.fhir.r4.core.Money
import com.google.fhir.r4.core.String

object MoneyConverter {
  fun Money.toHapi(): org.hl7.fhir.r4.model.Money {
    val hapiValue = org.hl7.fhir.r4.model.Money()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (hasValue()) {
      hapiValue.valueElement = value.toHapi()
    }
    if (hasCurrency()) {
      hapiValue.currency = currency.value.hapiCodeCheck()
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.Money.toProto(): Money {
    val protoValue = Money.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasValue()) {
      protoValue.value = valueElement.toProto()
    }
    if (hasCurrency()) {
      protoValue.currency =
        Money.CurrencyCode.newBuilder().setValue(currency.protoCodeCheck()).build()
    }
    return protoValue.build()
  }
}
