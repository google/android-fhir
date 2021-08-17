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
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.SimpleQuantityConverter.toProto
import com.google.fhir.r4.core.Range
import com.google.fhir.r4.core.String
import org.hl7.fhir.r4.model.SimpleQuantity

object RangeConverter {
  fun Range.toHapi(): org.hl7.fhir.r4.model.Range {
    val hapiValue = org.hl7.fhir.r4.model.Range()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (hasLow()) {
      hapiValue.low = low.toHapi()
    }
    if (hasHigh()) {
      hapiValue.high = high.toHapi()
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.Range.toProto(): Range {
    val protoValue = Range.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasLow()) {
      protoValue.low = (low as SimpleQuantity).toProto()
    }
    if (hasHigh()) {
      protoValue.high = (high as SimpleQuantity).toProto()
    }
    return protoValue.build()
  }
}
