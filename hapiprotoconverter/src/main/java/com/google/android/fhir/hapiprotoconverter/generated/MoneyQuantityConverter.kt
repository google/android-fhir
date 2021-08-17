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
import com.google.fhir.r4.core.MoneyQuantity
import com.google.fhir.r4.core.QuantityComparatorCode
import com.google.fhir.r4.core.String
import org.hl7.fhir.r4.model.Quantity

object MoneyQuantityConverter {
  fun MoneyQuantity.toHapi(): org.hl7.fhir.r4.model.MoneyQuantity {
    val hapiValue = org.hl7.fhir.r4.model.MoneyQuantity()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (hasValue()) {
      hapiValue.valueElement = value.toHapi()
    }
    if (hasComparator()) {
      hapiValue.comparator =
        Quantity.QuantityComparator.valueOf(comparator.value.name.hapiCodeCheck().replace("_", ""))
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

  fun org.hl7.fhir.r4.model.MoneyQuantity.toProto(): MoneyQuantity {
    val protoValue = MoneyQuantity.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasValue()) {
      protoValue.value = valueElement.toProto()
    }
    if (hasComparator()) {
      protoValue.comparator =
        MoneyQuantity.ComparatorCode.newBuilder()
          .setValue(
            QuantityComparatorCode.Value.valueOf(
              comparator.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
            )
          )
          .build()
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
