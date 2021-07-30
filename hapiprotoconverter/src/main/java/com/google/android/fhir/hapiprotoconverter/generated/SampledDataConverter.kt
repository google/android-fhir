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
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PositiveIntConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.fhir.r4.core.SampledData
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic
import org.hl7.fhir.r4.model.SimpleQuantity

public object SampledDataConverter {
  @JvmStatic
  public fun SampledData.toHapi(): org.hl7.fhir.r4.model.SampledData {
    val hapiValue = org.hl7.fhir.r4.model.SampledData()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setOrigin(origin.toHapi())
    hapiValue.setPeriodElement(period.toHapi())
    hapiValue.setFactorElement(factor.toHapi())
    hapiValue.setLowerLimitElement(lowerLimit.toHapi())
    hapiValue.setUpperLimitElement(upperLimit.toHapi())
    hapiValue.setDimensionsElement(dimensions.toHapi())
    hapiValue.setDataElement(data.toHapi())
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.SampledData.toProto(): SampledData {
    val protoValue =
      SampledData.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .setOrigin((origin as SimpleQuantity).toProto())
        .setPeriod(periodElement.toProto())
        .setFactor(factorElement.toProto())
        .setLowerLimit(lowerLimitElement.toProto())
        .setUpperLimit(upperLimitElement.toProto())
        .setDimensions(dimensionsElement.toProto())
        .setData(dataElement.toProto())
        .build()
    return protoValue
  }
}
