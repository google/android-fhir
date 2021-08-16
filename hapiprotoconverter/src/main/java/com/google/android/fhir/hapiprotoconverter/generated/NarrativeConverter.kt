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
import com.google.fhir.r4.core.Narrative
import com.google.fhir.r4.core.NarrativeStatusCode
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic

object NarrativeConverter {
  @JvmStatic
  fun Narrative.toHapi(): org.hl7.fhir.r4.model.Narrative {
    val hapiValue = org.hl7.fhir.r4.model.Narrative()
    hapiValue.id = id.value
    if (extensionCount > 0) {
      hapiValue.extension = extensionList.map { it.toHapi() }
    }
    hapiValue.status =
      org.hl7.fhir.r4.model.Narrative.NarrativeStatus.valueOf(
        status.value.name.hapiCodeCheck().replace("_", "")
      )
    return hapiValue
  }

  @JvmStatic
  fun org.hl7.fhir.r4.model.Narrative.toProto(): Narrative {
    val protoValue = Narrative.newBuilder().setId(String.newBuilder().setValue(id))
    if (hasExtension()) {
      protoValue.addAllExtension(extension.map { it.toProto() })
    }
    protoValue.status =
      Narrative.StatusCode.newBuilder()
        .setValue(
          NarrativeStatusCode.Value.valueOf(
            status.toCode().protoCodeCheck().replace("-", "_").toUpperCase()
          )
        )
        .build()
    return protoValue.build()
  }
}
