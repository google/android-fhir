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
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.MemberName

/**
 * @param element element definition of a choice type
 * @param hapiBuilder hapi FunctionBuilder of type that contains the choice type
 * @param protoBuilder proto FunctionBuilder of type that contains the choice type
 * @param fileBuilder FileBuilder for the file that will contain [hapiBuilder] & [protoBuilder]
 *
 * @returns a list of FunctionBuilders that convert [element] from hapi to proto and vice versa
 */
internal fun handleChoiceType(
  element: ElementDefinition,
  hapiBuilder: FunSpec.Builder,
  protoBuilder: FunSpec.Builder,
  fileBuilder: FileSpec.Builder,
  backboneElementMap: MutableMap<String, BackBoneElementData>
): List<FunSpec> {
  val (elementToProtoBuilder, elementToHapiBuilder) =
    getHapiProtoConverterFuncPair(
      element
        .path
        .value
        .split(".")
        .joinToString("") { it.capitalizeFirst() }
        .removeSuffix(choiceTypeSuffixStructureDefinition),
      element.getChoiceTypeProtoClass(
        backboneElementMap[element.path.value.substringBeforeLast(".")]
      ),
      element.getChoiceTypeHapiClass()
    )
  elementToProtoBuilder.addStatement(
    "val protoValue = %T.newBuilder()",
    element.getChoiceTypeProtoClass(backboneElementMap[element.path.value.substringBeforeLast(".")])
  )

  for (type in element.typeList) {
    // TODO handle this
    if (type.normalizeType().lowerCaseFirst() == "meta") {
      continue
    }
    elementToProtoBuilder.beginControlFlow(
      "if (this is %T)",
      ClassName(
        hapiPackage,
        type.normalizeType() +
          if (type.normalizeType().lowerCaseFirst() in primitiveTypeList) "Type" else ""
      )
    )
    val toProto =
      MemberName(
        ClassName(converterPackage, "${type.normalizeType().capitalizeFirst()}Converter"),
        "toProto"
      )
    val toHapi =
      MemberName(
        ClassName(converterPackage, "${type.normalizeType().capitalizeFirst()}Converter"),
        "toHapi"
      )

    fileBuilder.addImport(toProto.enclosingClassName!!, toProto.simpleName)
    fileBuilder.addImport(toHapi.enclosingClassName!!, toHapi.simpleName)

    elementToProtoBuilder.addStatement(
      "protoValue$singleMethodTemplate(this.toProto())",
      if (type.normalizeType() == "String") "StringValue" else type.code.value.capitalizeFirst()
    )
    elementToProtoBuilder.endControlFlow()

    elementToHapiBuilder.beginControlFlow(
      "if (this.get%L() != %T.newBuilder().defaultInstanceForType )",
      if (type.normalizeType() == "String") "StringValue" else type.code.value.capitalizeFirst(),
      ClassName(protoPackage, type.normalizeType())
    )
    elementToHapiBuilder.addStatement(
      "return (this.get%L()).toHapi()",
      if (type.normalizeType() == "String") "StringValue" else type.code.value.capitalizeFirst()
    )
    elementToHapiBuilder.endControlFlow()
  }
  elementToProtoBuilder.addStatement("return protoValue.build()")

  elementToHapiBuilder.addStatement(
    "throw %T(%S)",
    IllegalArgumentException::class,
    "Invalid Type for ${element.path.value}"
  )
  protoBuilder.addStatement(
    "$singleMethodTemplate(%L.%N())",
    element.getProtoMethodName(),
    element.getHapiFieldName(),
    elementToProtoBuilder.build()
  )
  hapiBuilder.addStatement(
    "hapiValue$singleMethodTemplate(%L.%N())",
    element.getHapiMethodName(),
    element.getProtoFieldName(),
    elementToHapiBuilder.build()
  )

  return listOf(elementToHapiBuilder.build(), elementToProtoBuilder.build())
}
