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

import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeableConceptConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.PeriodConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ReferenceConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.UriConverter.toProto
import com.google.fhir.r4.core.Identifier
import com.google.fhir.r4.core.IdentifierUseCode
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic

object IdentifierConverter {
  @JvmStatic
  fun Identifier.toHapi(): org.hl7.fhir.r4.model.Identifier {
    val hapiValue = org.hl7.fhir.r4.model.Identifier()
    hapiValue.id = id.value
    if (extensionCount > 0) {
        hapiValue.extension = extensionList.map { it.toHapi() }
    }
      hapiValue.use = org.hl7.fhir.r4.model.Identifier.IdentifierUse.valueOf(
          use.value.name.hapiCodeCheck().replace("_", "")
      )
    if (hasType()) {
        hapiValue.type = type.toHapi()
    }
    if (hasSystem()) {
        hapiValue.systemElement = system.toHapi()
    }
    if (hasValue()) {
        hapiValue.valueElement = value.toHapi()
    }
    if (hasPeriod()) {
        hapiValue.period = period.toHapi()
    }
    if (hasAssigner()) {
        hapiValue.assigner = assigner.toHapi()
    }
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.Identifier.toProto(): Identifier {
    val protoValue = Identifier.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
      protoValue.use = Identifier.UseCode.newBuilder()
          .setValue(
              IdentifierUseCode.Value.valueOf(
                  use.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
              )
          )
          .build()
    if (hasType()) {
        protoValue.type = type.toProto()
    }
    if (hasSystem()) {
        protoValue.system = systemElement.toProto()
    }
    if (hasValue()) {
        protoValue.value = valueElement.toProto()
    }
    if (hasPeriod()) {
        protoValue.period = period.toProto()
    }
    if (hasAssigner()) {
        protoValue.assigner = assigner.toProto()
    }
    return protoValue.build()
  }
}
