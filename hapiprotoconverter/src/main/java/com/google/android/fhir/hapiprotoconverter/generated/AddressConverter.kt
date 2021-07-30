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

public object AddressConverter {
  public fun Address.toHapi(): org.hl7.fhir.r4.model.Address {
    val hapiValue = org.hl7.fhir.r4.model.Address()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setUse(
      org.hl7.fhir.r4.model.Address.AddressUse.valueOf(use.value.name.replace("_", ""))
    )
    hapiValue.setType(
      org.hl7.fhir.r4.model.Address.AddressType.valueOf(type.value.name.replace("_", ""))
    )
    hapiValue.setTextElement(text.toHapi())
    hapiValue.setLine(lineList.map { it.toHapi() })
    hapiValue.setCityElement(city.toHapi())
    hapiValue.setDistrictElement(district.toHapi())
    hapiValue.setStateElement(state.toHapi())
    hapiValue.setPostalCodeElement(postalCode.toHapi())
    hapiValue.setCountryElement(country.toHapi())
    hapiValue.setPeriod(period.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Address.toProto(): Address {
    val protoValue =
      Address.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .setUse(
          Address.UseCode.newBuilder()
            .setValue(AddressUseCode.Value.valueOf(use.toCode().replace("-", "_").toUpperCase()))
            .build()
        )
        .setType(
          Address.TypeCode.newBuilder()
            .setValue(AddressTypeCode.Value.valueOf(type.toCode().replace("-", "_").toUpperCase()))
            .build()
        )
        .setText(textElement.toProto())
        .addAllLine(line.map { it.toProto() })
        .setCity(cityElement.toProto())
        .setDistrict(districtElement.toProto())
        .setState(stateElement.toProto())
        .setPostalCode(postalCodeElement.toProto())
        .setCountry(countryElement.toProto())
        .setPeriod(period.toProto())
        .build()
    return protoValue
  }
}
