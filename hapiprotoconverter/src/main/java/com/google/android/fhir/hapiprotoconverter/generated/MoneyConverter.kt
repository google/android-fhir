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

public object MoneyConverter {
  public fun Money.toHapi(): org.hl7.fhir.r4.model.Money {
    val hapiValue = org.hl7.fhir.r4.model.Money()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setValueElement(value.toHapi())
    hapiValue.setCurrency(currency.value)
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Money.toProto(): Money {
    val protoValue =
      Money.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .setValue(valueElement.toProto())
        .setCurrency(Money.CurrencyCode.newBuilder().setValue(currency).build())
        .build()
    return protoValue
  }
}
