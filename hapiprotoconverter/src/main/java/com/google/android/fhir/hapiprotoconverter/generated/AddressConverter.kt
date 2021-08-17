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

import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.fhir.r4.core.Address
import com.google.fhir.r4.core.AddressTypeCode
import com.google.fhir.r4.core.AddressUseCode
import com.google.fhir.r4.core.String

object AddressConverter {
  fun Address.toHapi(): org.hl7.fhir.r4.model.Address {
    val hapiValue = org.hl7.fhir.r4.model.Address()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    hapiValue.use =
      org.hl7.fhir.r4.model.Address.AddressUse.valueOf(
        use.value.name.hapiCodeCheck().replace("_", "")
      )
    hapiValue.type =
      org.hl7.fhir.r4.model.Address.AddressType.valueOf(
        type.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasText()) {
      hapiValue.textElement = text.toHapi()
    }
    if (lineCount > 0) {
      hapiValue.line = lineList.map { it.toHapi() }
    }
    if (hasCity()) {
      hapiValue.cityElement = city.toHapi()
    }
    if (hasDistrict()) {
      hapiValue.districtElement = district.toHapi()
    }
    if (hasState()) {
      hapiValue.stateElement = state.toHapi()
    }
    if (hasPostalCode()) {
      hapiValue.postalCodeElement = postalCode.toHapi()
    }
    if (hasCountry()) {
      hapiValue.countryElement = country.toHapi()
    }
    if (hasPeriod()) {
      hapiValue.period = period.toHapi()
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.Address.toProto(): Address {
    val protoValue = Address.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    protoValue.use =
      Address.UseCode.newBuilder()
        .setValue(
          AddressUseCode.Value.valueOf(
            use.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    protoValue.type =
      Address.TypeCode.newBuilder()
        .setValue(
          AddressTypeCode.Value.valueOf(
            type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    if (hasText()) {
      protoValue.text = textElement.toProto()
    }
    if (hasLine()) {
      protoValue.addAllLine(line.map { it.toProto() })
    }
    if (hasCity()) {
      protoValue.city = cityElement.toProto()
    }
    if (hasDistrict()) {
      protoValue.district = districtElement.toProto()
    }
    if (hasState()) {
      protoValue.state = stateElement.toProto()
    }
    if (hasPostalCode()) {
      protoValue.postalCode = postalCodeElement.toProto()
    }
    if (hasCountry()) {
      protoValue.country = countryElement.toProto()
    }
    if (hasPeriod()) {
      protoValue.period = period.toProto()
    }
    return protoValue.build()
  }
}
