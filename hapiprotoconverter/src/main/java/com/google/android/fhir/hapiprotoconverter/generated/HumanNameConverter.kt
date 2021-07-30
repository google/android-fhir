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
import com.google.fhir.r4.core.HumanName
import com.google.fhir.r4.core.NameUseCode
import com.google.fhir.r4.core.String

public object HumanNameConverter {
  public fun HumanName.toHapi(): org.hl7.fhir.r4.model.HumanName {
    val hapiValue = org.hl7.fhir.r4.model.HumanName()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setUse(
      org.hl7.fhir.r4.model.HumanName.NameUse.valueOf(use.value.name.replace("_", ""))
    )
    hapiValue.setTextElement(text.toHapi())
    hapiValue.setFamilyElement(family.toHapi())
    hapiValue.setGiven(givenList.map { it.toHapi() })
    hapiValue.setPrefix(prefixList.map { it.toHapi() })
    hapiValue.setSuffix(suffixList.map { it.toHapi() })
    hapiValue.setPeriod(period.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.HumanName.toProto(): HumanName {
    val protoValue =
      HumanName.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .setUse(
          HumanName.UseCode.newBuilder()
            .setValue(NameUseCode.Value.valueOf(use.toCode().replace("-", "_").toUpperCase()))
            .build()
        )
        .setText(textElement.toProto())
        .setFamily(familyElement.toProto())
        .addAllGiven(given.map { it.toProto() })
        .addAllPrefix(prefix.map { it.toProto() })
        .addAllSuffix(suffix.map { it.toProto() })
        .setPeriod(period.toProto())
        .build()
    return protoValue
  }
}
