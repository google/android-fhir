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
import org.hl7.fhir.r4.model.SimpleQuantity

object SampledDataConverter {
  fun SampledData.toHapi(): org.hl7.fhir.r4.model.SampledData {
    val hapiValue = org.hl7.fhir.r4.model.SampledData()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (hasOrigin()) {
      hapiValue.origin = origin.toHapi()
    }
    if (hasPeriod()) {
      hapiValue.periodElement = period.toHapi()
    }
    if (hasFactor()) {
      hapiValue.factorElement = factor.toHapi()
    }
    if (hasLowerLimit()) {
      hapiValue.lowerLimitElement = lowerLimit.toHapi()
    }
    if (hasUpperLimit()) {
      hapiValue.upperLimitElement = upperLimit.toHapi()
    }
    if (hasDimensions()) {
      hapiValue.dimensionsElement = dimensions.toHapi()
    }
    if (hasData()) {
      hapiValue.dataElement = data.toHapi()
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.SampledData.toProto(): SampledData {
    val protoValue = SampledData.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasOrigin()) {
      protoValue.origin = (origin as SimpleQuantity).toProto()
    }
    if (hasPeriod()) {
      protoValue.period = periodElement.toProto()
    }
    if (hasFactor()) {
      protoValue.factor = factorElement.toProto()
    }
    if (hasLowerLimit()) {
      protoValue.lowerLimit = lowerLimitElement.toProto()
    }
    if (hasUpperLimit()) {
      protoValue.upperLimit = upperLimitElement.toProto()
    }
    if (hasDimensions()) {
      protoValue.dimensions = dimensionsElement.toProto()
    }
    if (hasData()) {
      protoValue.data = dataElement.toProto()
    }
    return protoValue.build()
  }
}
