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
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IdentifierConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Reference
import com.google.fhir.r4.core.String

object ReferenceConverter {
  fun Reference.toHapi(): org.hl7.fhir.r4.model.Reference {
    val hapiValue = org.hl7.fhir.r4.model.Reference()
    if (hasId()) {
      hapiValue.id = id.value
    }
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (hasType()) {
      hapiValue.typeElement = type.toHapi()
    }
    if (hasIdentifier()) {
      hapiValue.identifier = identifier.toHapi()
    }
    if (hasDisplay()) {
      hapiValue.displayElement = display.toHapi()
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.Reference.toProto(): Reference {
    val protoValue = Reference.newBuilder()
    if (hasId()) {
      protoValue.setId(String.newBuilder().setValue(id))
    }
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasType()) {
      protoValue.type = typeElement.toProto()
    }
    if (hasIdentifier()) {
      protoValue.identifier = identifier.toProto()
    }
    if (hasDisplay()) {
      protoValue.display = displayElement.toProto()
    }
    return protoValue.build()
  }
}
