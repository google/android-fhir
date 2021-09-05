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

package com.google.android.fhir.index

import ca.uhn.fhir.context.FhirContext
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.SearchParameter

object IndexGenerator {

  private val spHashMap: HashMap<String, CodeBlock.Builder> = HashMap()

  fun generate(bundle: Bundle) {
    for (entry in bundle.entry) {

      val sp = entry.resource as SearchParameter
      // TODO handle search parameters with empty expressions eg DomainResource.text
      if (sp.expression.isNullOrEmpty()) continue

      for (path in sp.expression.getPathListFromExpression()) {

        val hmKey = path.key

        if (!spHashMap.containsKey(hmKey)) {
          spHashMap[hmKey] = CodeBlock.builder().add("%S -> listOf(", hmKey)
        }
        spHashMap[hmKey]!!.add(
          "%T(%S,%T.%L,%S),\n",
          SearchParamDef::class,
          sp.name,
          Enumerations.SearchParamType::class,
          sp.type.toCode().toUpperCase(),
          path.value
        )
      }
    }

    val fileSpec = FileSpec.builder(this::class.java.`package`!!.name, "ResourceIndexingGenerated")
    val func =
      FunSpec.builder("getSearchParamList")
        .addParameter("resource", Resource::class)
        .returns(List::class.parameterizedBy(SearchParamDef::class))
        .addModifiers(KModifier.INTERNAL)
        .beginControlFlow("return when (resource.fhirType())")
    for (resource in spHashMap.keys) {
      val resourceClass = ClassName("org.hl7.fhir.r4.model", resource.toHapiName())
      // just to check if the class exists
      try {
        Class.forName(resourceClass.reflectionName())
      } catch (e: ClassNotFoundException) {
        // TODO handle alias and name (InsurancePlan)
        println("Class not found $resource ")
        continue
      }

      func.addCode(spHashMap[resource]!!.add(")\n").build())
    }
    func.addStatement("else -> emptyList()").endControlFlow()
    fileSpec.addFunction(func.build()).build().writeTo(File("engine\\src\\main\\java"))
    println(File("").absolutePath)
  }

  private fun String.getPathListFromExpression(): Map<String, String> {
    return split("|")
      .groupBy { splitString -> splitString.split(".").first().trim().removePrefix("(") }
      .mapValues { it.value.joinToString(" | ") { join -> join.trim() } }
  }

  private fun String.toHapiName() = if (this == "List") "ListResource" else this
}

fun main() {
  val sp = File("engine\\src\\main\\res\\search-parameters.json")
  val bundle =
    FhirContext.forR4().newJsonParser().parseResource(Bundle::class.java, sp.inputStream())
  IndexGenerator.generate(bundle)
}
