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

import com.google.fhir.common.JsonFormat
import com.google.fhir.r4.core.StructureDefinition
import com.google.fhir.r4.core.StructureDefinitionKindCode
import com.google.fhir.r4.core.ValueSet
import com.squareup.kotlinpoet.ClassName
import java.io.File

fun main() {
  // TODO change this
  File("hapiprotoconverter\\sampledata\\").listFiles()!!
    .filter {
      it.name.startsWith("valueset-") &&
        !it.name.startsWith("valueset-extensions-") &&
        !it.name.equals("valueset-questionnaire.canonical.json") &&
        !it.name.equals("valueset-questionnaire.json") &&
        it.name.endsWith(".json")
    }
    .forEach {
      try {
        val def = JsonFormat.getParser().merge(it.inputStream().reader(), ValueSet.newBuilder())
        CompositeCodegen.valueSetUrlMap[def.url.value] = def.build()
      } catch (e: Exception) {
        println(it.name)
      }
    }
  // TODO move this to a different folder?
  File("hapiprotoconverter\\sampledata\\").listFiles()!!
    .filter { it.name.endsWith(".profile.json") }
    .forEach {
      val def =
        JsonFormat.getParser().merge(it.inputStream().reader(), StructureDefinition.newBuilder())
      CompositeCodegen.profileUrlMap[def.url.value] = def.build()
    }
  CompositeCodegen.profileUrlMap.values.forEach { def ->
    if ((def.kind.value == StructureDefinitionKindCode.Value.COMPLEX_TYPE ||
        def.kind.value == StructureDefinitionKindCode.Value.RESOURCE) && !def.abstract.value
    ) {
      try {
        Class.forName(ClassName(hapiPackage, def.id.value.capitalizeFirst()).reflectionName())

        Class.forName(ClassName(protoPackage, def.id.value.capitalizeFirst()).reflectionName())
        CompositeCodegen.generate(def, File("hapiprotoconverter\\src\\main\\java"))
      } catch (e: Exception) {
        if (e is ClassNotFoundException) {
          println("${def.id.value} Class not found")
        } else {
          //          throw e
          println(def.name.value)
        }
      }
    }
  }
}

// profiles that cannot be generated ( apart from -genetics.json profilesi )
// Profile for Catalog
// CDS Hooks GuidanceResponse
// CDS Hooks Service PlanDefinition
// Clinical Document
// TODO - Composition
// CQF-Questionnaire
// DataElement constraint on ElementDefinition data type
// Family member history for genetics analysis
// Profile for HLA Genotyping Results
// TODO MessageDefinition - Done
