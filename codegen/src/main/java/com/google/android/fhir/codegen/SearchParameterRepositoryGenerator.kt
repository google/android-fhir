/*
 * Copyright 2021 Google LLC
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

/**
 * Generates the files `SearchParameterRepositoryGenerated.kt` and
 * `SearchParameterRepositoryGeneratedTestHelper.kt`.
 *
 * The search parameter definitions are in the file `codegen/src/main/res/search-parameters.json`.
 * This file should be kept up-to-date with the HL7 specifications at
 * `http://www.hl7.org/fhir/search-parameters.json` and the files
 * `SearchParameterRepositoryGenerated.kt` and `SearchParameterRepositoryGeneratedTestHelper.kt`.
 * should be regenerated to reflect any change.
 *
 * To do this, replace the content of the file `codegen/src/main/res/search-parameters.json` with
 * the content at `http://www.hl7.org/fhir/search-parameters.json` and run the `main` function in
 * the `codegen` module.
 */
object SearchParameterRepositoryGenerator {

  private const val indexPackage = "com.google.android.fhir.index"
  private const val hapiPackage = "org.hl7.fhir.r4.model"
  private const val generatedClassName = "SearchParameterRepository_Generated"
  private const val indexTestPackage = "com.google.android.fhir.index"
  private const val generatedTestHelperClassName = "SearchParameterRepositoryTestHelper_Generated"
  private const val generatedComment =
    "This File is Generated from com.google.android.fhir.codegen.SearchParameterRepositoryGenerator all changes to this file must be made through the aforementioned file only"

  private val searchParamMap: HashMap<String, CodeBlock.Builder> = HashMap()
  private val searchParamDefinitionClass = ClassName(indexPackage, "SearchParamDefinition")

  fun generate(bundle: Bundle, outputPath: String, testOutputPath: String) {
    for (entry in bundle.entry) {
      val searchParameter = entry.resource as SearchParameter
      if (searchParameter.expression.isNullOrEmpty()) continue

      for (path in getResourceToPathMap(searchParameter)) {
        val hashMapKey = path.key
        if (!searchParamMap.containsKey(hashMapKey)) {
          searchParamMap[hashMapKey] = CodeBlock.builder().add("%S -> listOf(", hashMapKey)
        }
        searchParamMap[hashMapKey]!!.add(
          "%T(%S,%T.%L,%S),\n",
          searchParamDefinitionClass,
          searchParameter.name,
          Enumerations.SearchParamType::class,
          searchParameter.type.toCode().uppercase(Locale.ROOT),
          path.value
        )
      }
    }

    val fileSpec = FileSpec.builder(indexPackage, generatedClassName)

    val function =
      FunSpec.builder("getSearchParamList")
        .addParameter("resource", Resource::class)
        .returns(
          ClassName("kotlin.collections", "List").parameterizedBy(searchParamDefinitionClass)
        )
        .addModifiers(KModifier.INTERNAL)
        .addKdoc(generatedComment)
        .beginControlFlow("return when (resource.fhirType())")

    // Helper function used in SearchParameterRepositoryGeneratedTest
    val testHelperFunctionCodeBlock =
      CodeBlock.builder().addStatement("val resourceList = listOf<%T>(", Resource::class.java)

    for (resource in searchParamMap.keys) {
      val resourceClass = ClassName(hapiPackage, resource.toHapiName())
      try {
        Class.forName(resourceClass.reflectionName())
      } catch (e: ClassNotFoundException) {
        println("Class not found $resource ")
        continue
      }
      function.addCode(searchParamMap[resource]!!.add(")\n").build())

      if (resource != "Resource") {
        testHelperFunctionCodeBlock.add("%T(),\n", resourceClass)
      }
    }
    function.addStatement("else -> emptyList()").endControlFlow()
    fileSpec.addFunction(function.build()).build().writeTo(File(outputPath))

    testHelperFunctionCodeBlock.add(")\n")
    testHelperFunctionCodeBlock.addStatement("return resourceList")
    val testFile =
      FileSpec.builder(indexTestPackage, generatedTestHelperClassName)
        .addFunction(
          FunSpec.builder("getAllResources")
            .addModifiers(KModifier.INTERNAL)
            .returns(List::class.parameterizedBy(Resource::class))
            .addCode(testHelperFunctionCodeBlock.build())
            .addKdoc(generatedComment)
            .build()
        )
        .build()
    testFile.writeTo(File(testOutputPath))
  }

  /**
   * @return the resource names mapped to their respective paths in the expression of [searchParam]
   *
   * @param searchParam the search parameter that needs to be mapped
   *
   * This is necessary because the path expressions are not necessarily grouped by resource type
   *
   * For example an expression of "AllergyIntolerance.code | AllergyIntolerance.reaction.substance |
   * Condition.code" will return "AllergyIntolerance" -> "AllergyIntolerance.code |
   * AllergyIntolerance.reaction.substance" , "Condition" -> "Condition.code"
   */
  private fun getResourceToPathMap(searchParam: SearchParameter): Map<String, String> {
    // the if block is added because of the issue https://jira.hl7.org/browse/FHIR-22724 and can be
    // removed once the issue is resolved
    return if (searchParam.base.size == 1) {
      mapOf(searchParam.base.single().valueAsString to searchParam.expression)
    } else {
      searchParam
        .expression
        .split("|")
        .groupBy { splitString -> splitString.split(".").first().trim().removePrefix("(") }
        .mapValues { it.value.joinToString(" | ") { join -> join.trim() } }
    }
  }

  private fun String.toHapiName() = if (this == "List") "ListResource" else this
}

private const val inputFilePath = "codegen/src/main/res/search-parameters.json"
private const val outputFilePath = "engine/src/main/java"
private const val testOutputPath = "engine/src/test/java"

/**
 * @param args The command line arguments. args[0] should contain the input file location. args[1]
 * should contains the output file location. If the arguments are absent the default values
 * [inputFilePath] & [outputFilePath] will be used. Useful when the runtime classpath is not the
 * root project directory. This function is called in codegen/gradle.build.kts .
 */
fun main(args: Array<String>) {
  val searchParamDef = File(args.getOrElse(0) { inputFilePath })
  val bundle =
    FhirContext.forR4()
      .newJsonParser()
      .parseResource(Bundle::class.java, searchParamDef.inputStream())
  SearchParameterRepositoryGenerator.generate(
    bundle,
    args.getOrElse(1) { outputFilePath },
    args.getOrElse(2) { testOutputPath }
  )
}
