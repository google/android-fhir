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

import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.DateTimeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.fhir.r4.core.Period
import com.google.fhir.r4.core.String

public object PeriodConverter {
  public fun Period.toHapi(): org.hl7.fhir.r4.model.Period {
    val hapiValue = org.hl7.fhir.r4.model.Period()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setStartElement(start.toHapi())
    hapiValue.setEndElement(end.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Period.toProto(): Period {
    val protoValue =
      Period.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .setStart(startElement.toProto())
        .setEnd(endElement.toProto())
        .build()
    return protoValue
  }
}
