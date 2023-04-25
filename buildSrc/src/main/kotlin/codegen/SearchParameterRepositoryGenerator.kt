/*
 * Copyright 2022 Google LLC
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

package codegen

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
internal data class SearchParamDefinition(
  val className: ClassName,
  val name: String,
  val paramTypeCode: String,
  val path: String
)

internal object SearchParameterRepositoryGenerator {

  private const val indexPackage = "com.google.android.fhir.index"
  private const val hapiPackage = "org.hl7.fhir.r4.model"
  private const val generatedClassName = "SearchParameterRepository_Generated"
  private const val indexTestPackage = "com.google.android.fhir.index"
  private const val generatedTestHelperClassName = "SearchParameterRepositoryTestHelper_Generated"
  private const val generatedComment =
    "This File is Generated from com.google.android.fhir.codegen.SearchParameterRepositoryGenerator all changes to this file must be made through the aforementioned file only"

  private val searchParamMap: HashMap<String, MutableList<SearchParamDefinition>> = HashMap()
  private val searchParamDefinitionClass = ClassName(indexPackage, "SearchParamDefinition")
  private val baseResourceSearchParameters = mutableListOf<SearchParamDefinition>()

  fun generate(bundle: Bundle, outputPath: File, testOutputPath: File) {
    for (entry in bundle.entry) {
      val searchParameter = entry.resource as SearchParameter
      if (searchParameter.expression.isNullOrEmpty()) continue

      for (path in getResourceToPathMap(searchParameter)) {
        val hashMapKey = path.key
        searchParamMap
          .getOrPut(hashMapKey) { mutableListOf() }
          .add(
            SearchParamDefinition(
              className = searchParamDefinitionClass,
              name = searchParameter.name,
              paramTypeCode = searchParameter.type.toCode().toUpperCase(Locale.US),
              path = path.value
            )
          )
        if (hashMapKey == "Resource")
          baseResourceSearchParameters.add(
            SearchParamDefinition(
              className = searchParamDefinitionClass,
              name = searchParameter.name,
              paramTypeCode = searchParameter.type.toCode().toUpperCase(Locale.US),
              path = path.value
            )
          )
      }
    }

    val fileSpec = FileSpec.builder(indexPackage, generatedClassName)

    val getSearchParamListFunction =
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
    searchParamMap.entries.forEach { (resource, definitions) ->
      // add search parameters defined in base -> Resource to this resource
      if (resource != "Resource") {
        definitions.addAll(
          baseResourceSearchParameters.map { baseSearchParam ->
            SearchParamDefinition(
              className = baseSearchParam.className,
              name = baseSearchParam.name,
              paramTypeCode = baseSearchParam.paramTypeCode,
              path = baseSearchParam.path.replace("Resource", resource)
            )
          }
        )
      }
      val resourceClass = ClassName(hapiPackage, resource.toHapiName())
      val klass =
        try {
          Class.forName(resourceClass.reflectionName())
        } catch (e: ClassNotFoundException) {
          println("Class not found $resource ")
          null
        }
      if (klass != null) {
        val resourceFunction =
          FunSpec.builder("get$resource")
            .apply {
              addModifiers(KModifier.PRIVATE)
              returns(
                ClassName("kotlin.collections", "List").parameterizedBy(searchParamDefinitionClass)
              )
              beginControlFlow("return buildList(capacity = %L)", definitions.size)
              definitions.forEach { definition ->
                addStatement(
                  "add(%T(%S, %T.%L, %S))",
                  definition.className,
                  definition.name,
                  Enumerations.SearchParamType::class,
                  definition.paramTypeCode,
                  definition.path
                )
              }
              endControlFlow() // end buildList
            }
            .build()
        fileSpec.addFunction(resourceFunction)
        getSearchParamListFunction.addStatement("%S -> %L()", resource, resourceFunction.name)
      }

      if (resource != "Resource") {
        testHelperFunctionCodeBlock.add("%T(),\n", resourceClass)
      }
    }

    getSearchParamListFunction.addStatement("else -> emptyList()").endControlFlow()
    fileSpec.addFunction(getSearchParamListFunction.build()).build().writeTo(outputPath)

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
    testFile.writeTo(testOutputPath)
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
      searchParam.expression
        .split("|")
        .groupBy { splitString -> splitString.split(".").first().trim().removePrefix("(") }
        .mapValues { it.value.joinToString(" | ") { join -> join.trim() } }
    }
  }

  private fun String.toHapiName() = if (this == "List") "ListResource" else this
}
