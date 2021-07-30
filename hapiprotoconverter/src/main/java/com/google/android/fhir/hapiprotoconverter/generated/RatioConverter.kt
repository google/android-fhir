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
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.QuantityConverter.toProto
import com.google.fhir.r4.core.Ratio
import com.google.fhir.r4.core.String

public object RatioConverter {
  public fun Ratio.toHapi(): org.hl7.fhir.r4.model.Ratio {
    val hapiValue = org.hl7.fhir.r4.model.Ratio()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setNumerator(numerator.toHapi())
    hapiValue.setDenominator(denominator.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Ratio.toProto(): Ratio {
    val protoValue =
      Ratio.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .setNumerator(numerator.toProto())
        .setDenominator(denominator.toProto())
        .build()
    return protoValue
  }
}
