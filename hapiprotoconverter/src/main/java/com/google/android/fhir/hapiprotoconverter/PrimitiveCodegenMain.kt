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

package com.google.android.fhir.hapiprotoconverter

import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.StructureDefinition
import com.google.fhir.r4.core.StructureDefinitionKindCode
import java.io.File

fun main() {
  for (x in primitiveTypeList) {
    val file = File("hapiprotoconverter\\src\\main\\java")
    // This is temp will parse files
    PrimitiveCodegen.generate(
      StructureDefinition.newBuilder()
        .setId(Id.newBuilder().setValue(x))
        .setKind(
          StructureDefinition.KindCode.newBuilder()
            .setValue(StructureDefinitionKindCode.Value.PRIMITIVE_TYPE)
        )
        .build(),
      outLocation = file
    )
  }
}
