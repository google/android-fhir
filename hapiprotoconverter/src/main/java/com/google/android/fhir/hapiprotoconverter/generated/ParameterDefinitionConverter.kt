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

import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CanonicalConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.CodeConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.ExtensionConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.IntegerConverter.toProto
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toHapi
import com.google.android.fhir.hapiprotoconverter.generated.StringConverter.toProto
import com.google.fhir.r4.core.FHIRAllTypesValueSet
import com.google.fhir.r4.core.OperationParameterUseCode
import com.google.fhir.r4.core.ParameterDefinition
import com.google.fhir.r4.core.String
import kotlin.jvm.JvmStatic

public object ParameterDefinitionConverter {
  @JvmStatic
  public fun ParameterDefinition.toHapi(): org.hl7.fhir.r4.model.ParameterDefinition {
    val hapiValue = org.hl7.fhir.r4.model.ParameterDefinition()
    hapiValue.id = id.value
    hapiValue.setExtension(extensionList.map { it.toHapi() })
    hapiValue.setNameElement(name.toHapi())
    hapiValue.setUse(
      org.hl7.fhir.r4.model.ParameterDefinition.ParameterUse.valueOf(
        use
          .value
          .name
          .apply {
            if (equals("INVALID_UNINITIALIZED", true) || equals("UNRECOGNIZED", true)) "NULL"
            else this
          }
          .replace("_", "")
      )
    )
    hapiValue.setMinElement(min.toHapi())
    hapiValue.setMaxElement(max.toHapi())
    hapiValue.setDocumentationElement(documentation.toHapi())
    hapiValue.setType(type.value.name)
    hapiValue.setProfileElement(profile.toHapi())
    return hapiValue
  }

  @JvmStatic
  public fun org.hl7.fhir.r4.model.ParameterDefinition.toProto(): ParameterDefinition {
    val protoValue =
      ParameterDefinition.newBuilder()
        .setId(String.newBuilder().setValue(id))
        .addAllExtension(extension.map { it.toProto() })
        .setName(nameElement.toProto())
        .setUse(
          ParameterDefinition.UseCode.newBuilder()
            .setValue(
              OperationParameterUseCode.Value.valueOf(
                use
                  .toCode()
                  .apply { if (equals("NULL", true)) "INVALID_UNINITIALIZED" else this }
                  .replace("-", "_")
                  .toUpperCase()
              )
            )
            .build()
        )
        .setMin(minElement.toProto())
        .setMax(maxElement.toProto())
        .setDocumentation(documentationElement.toProto())
        .setType(
          ParameterDefinition.TypeCode.newBuilder()
            .setValue(FHIRAllTypesValueSet.Value.valueOf(type))
            .build()
        )
        .setProfile(profileElement.toProto())
        .build()
    return protoValue
  }
}
