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
import kotlin.jvm.JvmStatic

public object AddressConverter {
  @JvmStatic
  public fun Address.toHapi(): org.hl7.fhir.r4.model.Address {
    val hapiValue = org.hl7.fhir.r4.model.Address()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    hapiValue.setUse(
      org.hl7.fhir.r4.model.Address.AddressUse.valueOf(
        use.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    hapiValue.setType(
      org.hl7.fhir.r4.model.Address.AddressType.valueOf(
        type.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasText()) {
      hapiValue.setTextElement(text.toHapi())
    }
    if (lineCount > 0) {
      hapiValue.setLine(lineList.map { it.toHapi() })
    }
    if (hasCity()) {
      hapiValue.setCityElement(city.toHapi())
    }
    if (hasDistrict()) {
      hapiValue.setDistrictElement(district.toHapi())
    }
    if (hasState()) {
      hapiValue.setStateElement(state.toHapi())
    }
    if (hasPostalCode()) {
      hapiValue.setPostalCodeElement(postalCode.toHapi())
    }
    if (hasCountry()) {
      hapiValue.setCountryElement(country.toHapi())
    }
    if (hasPeriod()) {
      hapiValue.setPeriod(period.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.Address.toProto(): Address {
    val protoValue = Address.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    protoValue.setUse(
      Address.UseCode.newBuilder()
        .setValue(
          AddressUseCode.Value.valueOf(
            use.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    protoValue.setType(
      Address.TypeCode.newBuilder()
        .setValue(
          AddressTypeCode.Value.valueOf(
            type.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    )
    if (hasText()) {
      protoValue.setText(textElement.toProto())
    }
    if (hasLine()) {
      protoValue.addAllLine(line.map { it.toProto() })
    }
    if (hasCity()) {
      protoValue.setCity(cityElement.toProto())
    }
    if (hasDistrict()) {
      protoValue.setDistrict(districtElement.toProto())
    }
    if (hasState()) {
      protoValue.setState(stateElement.toProto())
    }
    if (hasPostalCode()) {
      protoValue.setPostalCode(postalCodeElement.toProto())
    }
    if (hasCountry()) {
      protoValue.setCountry(countryElement.toProto())
    }
    if (hasPeriod()) {
      protoValue.setPeriod(period.toProto())
    }
    return protoValue.build()
  }
}
