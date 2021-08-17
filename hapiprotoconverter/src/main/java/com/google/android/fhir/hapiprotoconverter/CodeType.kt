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

private val hapiStringProtoCodeType =
  listOf(
    "http://hl7.org/fhir/ValueSet/defined-types|4.0.1",
    "http://hl7.org/fhir/ValueSet/all-types|4.0.1",
    "http://hl7.org/fhir/ValueSet/guide-parameter-code|4.0.1",
  )
private const val toHapiCheck = "hapiCodeCheck()"
private const val toProtoCheck = "protoCodeCheck()"
// Map of valueSet url that are renamed in Fhir protos
private val CODE_SYSTEM_RENAMES =
  mapOf(
    "http://hl7.org/fhir/secondary-finding" to "ObservationSecondaryFindingCode",
    "http://terminology.hl7.org/CodeSystem/composition-altcode-kind" to
      "CompositionAlternativeCodeKindCode",
    "http://hl7.org/fhir/contract-security-classification" to
      "ContractResourceSecurityClassificationCode",
    "http://hl7.org/fhir/device-definition-status" to "FHIRDeviceDefinitionStatusCode",
    "http://hl7.org/fhir/ValueSet/medication-statement-status" to "MedicationStatementStatusCodes"
  )

private const val resourceCode = "http://hl7.org/fhir/ValueSet/resource-types|4.0.1"
private val specialValueSet =
  listOf(
    "http://hl7.org/fhir/ValueSet/mimetypes|4.0.1",
    "http://hl7.org/fhir/ValueSet/currencies|4.0.1"
  )

private const val uriCommon =
  "http://hl7.org/fhir/StructureDefinition/elementdefinition-isCommonBinding"

// TODO Possible alternative -> MAP all code Types instead of relying on the valueOf method
internal fun handleCodeType(
  element: ElementDefinition,
  hapiBuilder: FunSpec.Builder,
  protoBuilder: FunSpec.Builder,
  protoName: kotlin.String,
  backboneElementMap: MutableMap<kotlin.String, BackBoneElementData>
) {
  val isSingle = element.max.value == "1"
  val isCommon =
    element.binding.extensionList.any { it.url.value == uriCommon && it.value.boolean.value }

  if (hapiStringProtoCodeType.contains(element.binding.valueSet.value) ||
      (element.binding.valueSet.value == resourceCode && isCommon)
  ) {
    if (isSingle) {
      protoBuilder
        .beginControlFlow("if (has%L())", element.getHapiMethodName())
        .addStatement(
          "protoValue$singleMethodTemplate(%T.newBuilder().setValue(%T.valueOf(%L)).build())",
          element.getProtoMethodName(),
          // Using this just to make sure codes are present in hapi and fhir protos
          element.getProtoCodeClass(
            protoName,
            backboneElementMap[element.path.value.substringBeforeLast(".")]
          ),
          // KotlinPoet.ClassName
          getProtoEnumNameFromElement(element),
          element.getHapiFieldName()
        )
        .endControlFlow()
      hapiBuilder
        .beginControlFlow("if (has%L())", element.getProtoMethodName())
        .addStatement(
          "hapiValue$singleMethodTemplate(%L.value.name)",
          element.getHapiMethodName(),
          element.getProtoFieldName()
        )
        .endControlFlow()
    } else {
      protoBuilder
        .beginControlFlow("if (has%L())", element.getHapiMethodName())
        .addStatement(
          "protoValue$multipleMethodTemplate(%L.map{%T.newBuilder().setValue(%T.valueOf(it.valueAsString.$toProtoCheck)).build()})",
          element.getProtoMethodName(),
          element.getHapiFieldName(),
          element.getProtoCodeClass(
            protoName,
            backboneElementMap[element.path.value.substringBeforeLast(".")]
          ),
          getProtoEnumNameFromElement(element)
        )
        .endControlFlow()
      hapiBuilder
        .beginControlFlow("if (%LCount > 0)", element.getProtoMethodName().lowerCaseFirst())
        .addStatement(
          "%L.forEach{hapiValue.add%L(it.value.name.$toHapiCheck)}",
          element.getProtoFieldName(isRepeated = true),
          element.getHapiMethodName(),
        )
        .endControlFlow()
    }
  } else if (specialValueSet.contains(element.binding.valueSet.value)) {
    if (isSingle) {
      protoBuilder
        .beginControlFlow("if (has%L())", element.getHapiMethodName())
        .addStatement(
          "protoValue$singleMethodTemplate(%T.newBuilder().setValue(%L.$toProtoCheck).build())",
          element.getProtoMethodName(),
          // Using this just to make sure codes are present in hapi and fhir protos
          element.getProtoCodeClass(
            protoName,
            backboneElementMap[element.path.value.substringBeforeLast(".")]
          ),
          // KotlinPoet.ClassName
          element.getHapiFieldName()
        )
        .endControlFlow()

      hapiBuilder
        .beginControlFlow("if (has%L())", element.getProtoMethodName())
        .addStatement(
          "hapiValue$singleMethodTemplate(%L.value.$toHapiCheck)",
          element.getHapiMethodName(),
          element.getProtoFieldName()
        )
        .endControlFlow()
    } else {
      protoBuilder
        .beginControlFlow("if (has%L())", element.getHapiMethodName())
        .addStatement(
          "protoValue$multipleMethodTemplate(%L.map{%T.newBuilder().setValue(it.value.$toProtoCheck).build()})",
          element.getProtoMethodName(),
          element.getHapiFieldName(),
          element.getProtoCodeClass(
            protoName,
            backboneElementMap[element.path.value.substringBeforeLast(".")]
          )
        )
        .endControlFlow()

      hapiBuilder
        .beginControlFlow("if (%LCount > 0)", element.getProtoMethodName().lowerCaseFirst())
        .addStatement(
          "%L.map{hapiValue.add%L(it.value.$toHapiCheck)}",
          element.getProtoFieldName(isRepeated = true),
          element.getHapiMethodName()
        )
        .endControlFlow()
    }
  } else {
    if (isSingle) {
      // if enum isSingle
      protoBuilder
        .beginControlFlow("if (has%L())", element.getHapiMethodName())
        .addStatement(
          "protoValue$singleMethodTemplate(%T.newBuilder().setValue(%T.valueOf(%L.toCode().$toProtoCheck.replace(\"-\", \"_\").toUpperCase())).build())",
          element.getProtoMethodName(),
          // Using this just to make sure codes are present in hapi and fhir protos
          element.getProtoCodeClass(
            protoName,
            backboneElementMap[element.path.value.substringBeforeLast(".")]
          ),
          // KotlinPoet.ClassName
          getProtoEnumNameFromElement(element),
          element.getHapiFieldName()
        )
        .endControlFlow()
      hapiBuilder
        .beginControlFlow("if (has%L())", element.getProtoMethodName())
        .addStatement(
          "hapiValue$singleMethodTemplate(%T.valueOf(%L.value.name.$toHapiCheck.replace(\"_\",\"\")))",
          element.getHapiMethodName(),
          element.getHapiCodeClass(isCommon),
          element.getProtoFieldName()
        )
        .endControlFlow()
    } else {
      // handle case when enum is repeated
      protoBuilder
        .beginControlFlow("if (has%L())", element.getHapiMethodName())
        .addStatement(
          "protoValue$multipleMethodTemplate(%L.map{%T.newBuilder().setValue(%T.valueOf(it.value.toCode().$toProtoCheck.replace(\"-\", \"_\").toUpperCase())).build()})",
          element.getProtoMethodName(),
          element.getHapiFieldName(),
          element.getProtoCodeClass(
            protoName,
            backboneElementMap[element.path.value.substringBeforeLast(".")]
          ),
          getProtoEnumNameFromElement(element)
        )
        .endControlFlow()
      hapiBuilder
        .beginControlFlow("if (%LCount > 0)", element.getProtoMethodName().lowerCaseFirst())
        .addStatement(
          "%L.forEach{hapiValue.add%L(%T.valueOf(it.value.name.$toHapiCheck.replace(\"_\",\"\")))}",
          element.getProtoFieldName(isRepeated = true),
          element.getHapiMethodName(),
          element.getHapiCodeClass(isCommon)
        )
        .endControlFlow()
    }
  }
  // Element is in special valueSet
}

private fun getProtoEnumNameFromElement(element: ElementDefinition): ClassName {

  if (element.binding.valueSet.value.split("|").first() in CODE_SYSTEM_RENAMES.keys) {
    return ClassName(
      protoPackage,
      CODE_SYSTEM_RENAMES[element.binding.valueSet.value.split("|").first()]!!,
      "Value"
    )
  }
  if (element.hasBinding() && element.binding.hasValueSet()) {

    val elementEntry =
      CompositeCodegen.valueSetUrlMap[element.binding.valueSet.value.split("|").first()]!!

    if (elementEntry.hasCompose() &&
        elementEntry.compose.includeList.size == 1 &&
        elementEntry.compose.includeList.single().conceptList.isEmpty()
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
    name
      .value
      .split("-")
      .joinToString("") { it.capitalizeFirst() }
      .replace("[^A-Za-z0-9]".toRegex(), "")
      .capitalizeFirst()

  if (filteredName.endsWith("Codes")) {
    return filteredName.substring(0, filteredName.length - 1)
  }
  return if (filteredName.endsWith("Code", ignoreCase = true)) filteredName
  else "${filteredName}Code"
}

private fun getValueSetName(name: String): kotlin.String {
  val filteredName =
    name
      .value
      .split("-")
      .joinToString("") { it.capitalizeFirst() }
      .replace("[^A-Za-z0-9]".toRegex(), "")
      .capitalizeFirst()
  if (filteredName.endsWith("ValueSets", ignoreCase = true)) {
    return filteredName.substring(0, filteredName.length - 1)
  }
  return if (filteredName.endsWith("ValueSet", ignoreCase = true)) filteredName
  else "${filteredName}ValueSet"
}
