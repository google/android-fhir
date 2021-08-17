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

import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodingConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.fhir.r4.core.CodeableConcept
import com.google.fhir.r4.core.String

object CodeableConceptConverter {
  fun CodeableConcept.toHapi(): org.hl7.fhir.r4.model.CodeableConcept {
    val hapiValue = org.hl7.fhir.r4.model.CodeableConcept()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    if (codingCount > 0) {
      hapiValue.coding = codingList.map { it.toHapi() }
    }
    if (hasText()) {
      hapiValue.textElement = text.toHapi()
    }
    return hapiValue
  }

  fun org.hl7.fhir.r4.model.CodeableConcept.toProto(): CodeableConcept {
    val protoValue = CodeableConcept.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    if (hasCoding()) {
      protoValue.addAllCoding(coding.map { it.toProto() })
    }
    if (hasText()) {
      protoValue.text = textElement.toProto()
    }
    return protoValue.build()
  }
}
