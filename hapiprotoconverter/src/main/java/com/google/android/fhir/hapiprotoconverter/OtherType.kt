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

internal fun handleOtherType(
  element: ElementDefinition,
  hapiBuilder: FunSpec.Builder,
  protoBuilder: FunSpec.Builder,
  fileBuilder: FileSpec.Builder
) {
  val isSingle = element.max.value == "1"
  val toProto =
    MemberName(
      ClassName(
        converterPackage,
        "${element.typeList.first().normalizeType().capitalizeFirst()}Converter"
      ),
      "toProto"
    )
  val toHapi =
    MemberName(
      ClassName(
        converterPackage,
        "${element.typeList.first().normalizeType().capitalizeFirst()}Converter"
      ),
      "toHapi"
    )
  fileBuilder.addImport(toProto.enclosingClassName!!, toProto.simpleName)
  fileBuilder.addImport(toHapi.enclosingClassName!!, toHapi.simpleName)

  if (isSingle) {
    protoBuilder.beginControlFlow("if (has%L())", element.getHapiMethodName())
    protoBuilder.addStatement(
      "protoValue$singleMethodTemplate(${if (element.typeList.first().profileList.isNotEmpty()) "( %L as %T )" else "%L%L"}.toProto())",
      element.getProtoMethodName(),
      element.getHapiFieldName(
        isPrimitive =
          element.typeList.single().normalizeType().lowerCaseFirst() in primitiveTypeList
      ),
      if (element.typeList.first().profileList.isNotEmpty())
        ClassName(hapiPackage, element.typeList.first().normalizeType())
      else ""
    )
    protoBuilder.endControlFlow()
    hapiBuilder.beginControlFlow("if (has%L())", element.getProtoMethodName())
    hapiBuilder.addStatement(
      "hapiValue$singleMethodTemplate(%L.toHapi())",
      element.getHapiMethodName(
        isPrimitive =
          element.typeList.single().normalizeType().lowerCaseFirst() in primitiveTypeList
      ),
      element.getProtoFieldName(),
    )
    hapiBuilder.endControlFlow()
  } else {
    protoBuilder.beginControlFlow("if (has%L())", element.getHapiMethodName())
    protoBuilder.addStatement(
      "protoValue$multipleMethodTemplate(%L.map{${if (element.typeList.first().profileList.isNotEmpty()) "( it as %T  )" else "it%L"}.toProto()})",
      element.getProtoMethodName(),
      element.getHapiFieldName(),
      if (element.typeList.first().profileList.isNotEmpty())
        ClassName(hapiPackage, element.typeList.first().normalizeType())
      else ""
    )
    protoBuilder.endControlFlow()

    hapiBuilder.beginControlFlow("if (%LCount > 0)", element.getProtoFieldName())
    hapiBuilder.addStatement(
      "hapiValue$singleMethodTemplate(%L.map{it.toHapi()})",
      element.getHapiMethodName(),
      element.getProtoFieldName(isRepeated = true)
    )
    hapiBuilder.endControlFlow()
  }
}
