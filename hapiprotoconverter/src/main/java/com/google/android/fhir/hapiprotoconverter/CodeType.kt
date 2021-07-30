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

import com.google.fhir.r4.core.ElementDefinition
import com.google.fhir.r4.core.String
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec

private val specialValueSet =
  listOf(
    "http://hl7.org/fhir/ValueSet/mimetypes|4.0.1",
    "http://hl7.org/fhir/ValueSet/currencies|4.0.1"
  )

private const val uriCommon =
  "http://hl7.org/fhir/StructureDefinition/elementdefinition-isCommonBinding"

internal fun handleCodeType(
  element: ElementDefinition,
  hapiBuilder: FunSpec.Builder,
  protoBuilder: FunSpec.Builder,
  protoName: kotlin.String,
  backboneElementMap: MutableMap<kotlin.String, CompositeCodegen.BackBoneElementData>
) {

  val isSingle = element.max.value == "1"
  val isCommon =
    element.binding.extensionList.any { it.url.value == uriCommon && it.value.boolean.value }

  if (!specialValueSet.contains(element.binding.valueSet.value)) {
    if (isSingle) {
      // if enum isSingle
      protoBuilder.addStatement(
        "${singleMethodTemplate}(%T.newBuilder().setValue(%T.valueOf(%L.toCode().replace(\"-\", \"_\").toUpperCase())).build())",
        element.getProtoMethodName(),
        // Using this just to make sure codes are present in hapi and fhir protos TODO change to
        element.getProtoCodeClass(
          protoName,
          backboneElementMap[element.path.value.substringBeforeLast(".")]
        ),
        // KotlinPoet.ClassName
        Class.forName(getEnumNameFromElement(element).reflectionName()),
        element.getHapiFieldName()
      )
      hapiBuilder.addStatement(
        "hapiValue${singleMethodTemplate}(%T.valueOf(%L.value.name.replace(\"_\",\"\")))",
        element.getHapiMethodName(),
        element.getHapiCodeClass(isCommon),
        element.getProtoFieldName()
      )
    } else {
      // handle case when enum is repeated
      protoBuilder.addStatement(
        "${multipleMethodTemplate}(%L.map{%T.newBuilder().setValue(%T.valueOf(it.value.toCode().replace(\"-\", \"_\").toUpperCase())).build()})",
        element.getProtoMethodName(),
       element.getHapiFieldName(),
        element.getProtoCodeClass(
          protoName,
          backboneElementMap[element.path.value.substringBeforeLast(".")]
        ),
        Class.forName(getEnumNameFromElement(element).reflectionName())
      )
      hapiBuilder.addStatement(
        "%L.map{hapiValue.add%L(%T.valueOf(it.value.name.replace(\"_\",\"\")))}",
        element.getProtoFieldName(isRepeated = true),
        element.getHapiMethodName(),
        element.getHapiCodeClass(isCommon)
      )
    }
  }
  // Element is in special valueSet
  else {
    if (isSingle) {
      protoBuilder.addStatement(
        "${singleMethodTemplate}(%T.newBuilder().setValue(%L).build())",
        element.getProtoMethodName(),
        // Using this just to make sure codes are present in hapi and fhir protos TODO change to
        element.getProtoCodeClass(
          protoName,
          backboneElementMap[element.path.value.substringBeforeLast(".")]
        ),
        // KotlinPoet.ClassName
        element.getHapiFieldName()
      )
      hapiBuilder.addStatement(
        "hapiValue${singleMethodTemplate}(%L.value)",
        element.getHapiMethodName(),
        element.getProtoFieldName()
      )
    }

    else {
      protoBuilder.addStatement(
        "${multipleMethodTemplate}(%L.map{%T.newBuilder().setValue(it.value).build()})",
        element.getProtoMethodName(),
        element.getHapiFieldName(),
        element.getProtoCodeClass(
          protoName,
          backboneElementMap[element.path.value.substringBeforeLast(".")]
        )
      )

      hapiBuilder.addStatement(
        "%L.map{hapiValue.add%L(it.value)}",
        element.getProtoFieldName(isRepeated = true),
        element.getHapiMethodName()
      )
    }
  }
}

private fun getEnumNameFromElement(element: ElementDefinition): ClassName {
  if (element.hasBinding() && element.binding.hasValueSet()) {
    // TODO handle code system renames

    if (CompositeCodegen.valueSetUrlMap[element.binding.valueSet.value.split("|").first()]!!
        .hasCompose() &&
        CompositeCodegen.valueSetUrlMap[element.binding.valueSet.value.split("|").first()]!!
          .compose
          .includeList
          .size == 1 &&
        CompositeCodegen.valueSetUrlMap[element.binding.valueSet.value.split("|").first()]!!
          .compose
          .includeList
          .single()
          .conceptList
          .isEmpty()
    ) {
      return ClassName(
        protoPackage,
        getCodeSystemName(
          CompositeCodegen.valueSetUrlMap[element.binding.valueSet.value.split("|").first()]!!.name
        ),
        "Value"
      )
    }
    return ClassName(
      protoPackage,
      getValueSetName(
        CompositeCodegen.valueSetUrlMap[element.binding.valueSet.value.split("|").first()]!!.name
      ),
      "Value"
    )
  }
  throw IllegalArgumentException("element is not in valueSetUrlMap")
}

private fun getCodeSystemName(name: String): kotlin.String {
  val filteredName =
    name.value
      .split("-")
      .joinToString("") { it.capitalizeFirst() }
      .replace("[^A-Za-z0-9]".toRegex(), "")
      .capitalizeFirst()

  if (filteredName.endsWith("Codes")) {
    return filteredName.substring(0, filteredName.length - 1)
  }
  return if (filteredName.endsWith("Code", ignoreCase = true)) filteredName else "${filteredName}Code"
}

private fun getValueSetName(name: String): kotlin.String {
  val filteredName =
    name.value
      .split("-")
      .joinToString("") { it.capitalizeFirst() }
      .replace("[^A-Za-z0-9]", "")
      .capitalizeFirst()
  if (filteredName.endsWith("ValueSets",ignoreCase = true)) {
    return filteredName.substring(0, filteredName.length - 1)
  }
  return if (filteredName.endsWith("ValueSet",ignoreCase = true)) filteredName else "${filteredName}ValueSet"
}
