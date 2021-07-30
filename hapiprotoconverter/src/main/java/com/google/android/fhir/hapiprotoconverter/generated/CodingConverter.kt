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

import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.BooleanConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Coding
import com.google.fhir.r4.core.String

public object CodingConverter {
  public fun Coding.toHapi(): org.hl7.fhir.r4.model.Coding {
    val hapiValue = org.hl7.fhir.r4.model.Coding()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setSystemElement(system.toHapi())
    hapiValue.setVersionElement(version.toHapi())
    hapiValue.setCodeElement(code.toHapi())
    hapiValue.setDisplayElement(display.toHapi())
    hapiValue.setUserSelectedElement(userSelected.toHapi())
    return hapiValue
  }

  public fun org.hl7.fhir.r4.model.Coding.toProto(): Coding {
    val protoValue =
      Coding.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .setSystem(systemElement.toProto())
        .setVersion(versionElement.toProto())
        .setCode(codeElement.toProto())
        .setDisplay(displayElement.toProto())
        .setUserSelected(userSelectedElement.toProto())
        .build()
    return protoValue
  }
}
