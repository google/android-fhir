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
import kotlin.jvm.JvmStatic

public object HumanNameConverter {
  @JvmStatic
  public fun HumanName.toHapi(): org.hl7.fhir.r4.model.HumanName {
    val hapiValue = org.hl7.fhir.r4.model.HumanName()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.setExtension(extensionList.map { it.toHapi() })
    }
    hapiValue.setUse(
      org.hl7.fhir.r4.model.HumanName.NameUse.valueOf(
        use.value.name.hapiCodeCheck().replace("_", "")
      )
    )
    if (hasText()) {
      hapiValue.setTextElement(text.toHapi())
    }
    if (hasFamily()) {
      hapiValue.setFamilyElement(family.toHapi())
    }
    if (givenCount > 0) {
      hapiValue.setGiven(givenList.map { it.toHapi() })
    }
    if (prefixCount > 0) {
      hapiValue.setPrefix(prefixList.map { it.toHapi() })
    }
    if (suffixCount > 0) {
      hapiValue.setSuffix(suffixList.map { it.toHapi() })
    }
    if (hasPeriod()) {
      hapiValue.setPeriod(period.toHapi())
    }
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.HumanName.toProto(): HumanName {
    val protoValue = HumanName.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    protoValue.setUse(
      HumanName.UseCode.newBuilder()
        .setValue(
          NameUseCode.Value.valueOf(use.toCode().protoCodeCheck().replace("-", "_").toUpperCase())
        )
        .build()
    )
    if (hasText()) {
      protoValue.setText(textElement.toProto())
    }
    if (hasFamily()) {
      protoValue.setFamily(familyElement.toProto())
    }
    if (hasGiven()) {
      protoValue.addAllGiven(given.map { it.toProto() })
    }
    if (hasPrefix()) {
      protoValue.addAllPrefix(prefix.map { it.toProto() })
    }
    if (hasSuffix()) {
      protoValue.addAllSuffix(suffix.map { it.toProto() })
    }
    if (hasPeriod()) {
      protoValue.setPeriod(period.toProto())
    }
    return protoValue.build()
  }
}
