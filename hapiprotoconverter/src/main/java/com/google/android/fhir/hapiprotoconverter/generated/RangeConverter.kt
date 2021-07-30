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

public object RangeConverter {
  public fun Range.toHapi(): org.hl7.fhir.r4.model.Range {
    val hapiValue = org.hl7.fhir.r4.model.Range()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setLow(low.toHapi())
    hapiValue.setHigh(high.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Range.toProto(): Range {
    val protoValue =
      Range.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .setLow((low as SimpleQuantity).toProto())
        .setHigh((high as SimpleQuantity).toProto())
        .build()
    return protoValue
  }
}
