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

package com.google.android.fhir.codegen

import ca.uhn.fhir.context.FhirContext
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import java.util.Locale
import kotlin.collections.HashMap
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.SearchParameter

object IndexGenerator {

  private val spHashMap: HashMap<String, CodeBlock.Builder> = HashMap()
  private const val indexPackage = "com.google.android.fhir.index"
  private const val hapiPackage = "org.hl7.fhir.r4.model"
  private val spClass = ClassName(indexPackage, "SearchParamDef")

  fun generate(bundle: Bundle, outputPath: String) {
    for (entry in bundle.entry) {

      val sp = entry.resource as SearchParameter

      // TODO handle search parameters with empty expressions eg DomainResource.text
      if (sp.expression.isNullOrEmpty()) continue

      for (path in getPathListFromExpression(sp.expression)) {

        val hmKey = path.key

        if (!spHashMap.containsKey(hmKey)) {
          spHashMap[hmKey] = CodeBlock.builder().add("%S -> listOf(", hmKey)
        }

        spHashMap[hmKey]!!.add(
          "%T(%S,%T.%L,%S),\n",
          spClass,
          sp.name,
          Enumerations.SearchParamType::class,
          sp.type.toCode().uppercase(Locale.ROOT),
          path.value
        )
      }
    }

    val fileSpec = FileSpec.builder(indexPackage, "ResourceIndexingGenerated")

    val func =
      FunSpec.builder("getSearchParamList")
        .addParameter("resource", Resource::class)
        .returns(ClassName("kotlin.collections", "List").parameterizedBy(spClass))
        .addModifiers(KModifier.INTERNAL)
        .addKdoc(
          "This File is Generated from com.google.android.fhir.codegen.IndexGenerator all changes to this file must be made through the aforementioned file only"
        )
        .beginControlFlow("return when (resource.fhirType())")

    for (resource in spHashMap.keys) {
      val resourceClass = ClassName(hapiPackage, resource.toHapiName())
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
    fileSpec.addFunction(func.build()).build().writeTo(File(outputPath))
  }

  /**
   * returns the resource names mapped to their respective paths in the expression
   * @param expression an expression that contains the paths of a given search param
   *
   * For example an expression of "AllergyIntolerance.code | AllergyIntolerance.reaction.substance |
   * Condition.code" will return "AllergyIntolerance" -> "AllergyIntolerance.code |
   * AllergyIntolerance.reaction.substance , "Condition" -> "Condition.code"
   */
  private fun getPathListFromExpression(expression: String): Map<String, String> {
    return expression
      .split("|")
      .groupBy { splitString -> splitString.split(".").first().trim().removePrefix("(") }
      .mapValues { it.value.joinToString(" | ") { join -> join.trim() } }
  }

  private fun String.toHapiName() = if (this == "List") "ListResource" else this
}

fun main() {
  val spFilePath = "codegen\\src\\main\\res\\search-parameters.json"
  val outputPath = "engine\\src\\main\\java"
  val sp = File(spFilePath)
  val bundle =
    FhirContext.forR4().newJsonParser().parseResource(Bundle::class.java, sp.inputStream())
  IndexGenerator.generate(bundle, outputPath)
}
