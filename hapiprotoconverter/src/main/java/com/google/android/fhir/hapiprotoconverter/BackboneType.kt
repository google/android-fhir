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
import com.squareup.kotlinpoet.FunSpec

internal fun handleBackBoneElementTypes(
  element: ElementDefinition,
  hapiBuilder: FunSpec.Builder,
  protoBuilder: FunSpec.Builder,
  backboneElementMap: MutableMap<String, BackBoneElementData>
) {
  val (toProtoBuilder, toHapiBuilder) =
    getHapiProtoConverterFuncPair(
      "",
      element.getBackBoneProtoClass(
        backboneElementMap[element.path.value.substringBeforeLast(".")]
      ),
      element.getBackBoneHapiClass(backboneElementMap[element.path.value.substringBeforeLast(".")])
    )

  if (element.max.value == "1") {
    protoBuilder.addStatement(
      "$singleMethodTemplate(${if (element.typeList.first().profileList.isNotEmpty()) "( %L as %T )" else "%L%L"}.toProto())",
      element.getProtoMethodName(),
      element.getHapiFieldName(),
      if (element.typeList.first().profileList.isNotEmpty())
        element.typeList.first().normalizeType()
      else ""
    )
    hapiBuilder.addStatement(
      "hapiValue$singleMethodTemplate(%L.toHapi())",
      element.getHapiMethodName(),
      element.getProtoFieldName()
    )
  } else {

    protoBuilder.addStatement(
      "$multipleMethodTemplate(%L.map{${if (element.typeList.first().profileList.isNotEmpty()) "( it as %T  )" else "it%L"}.toProto()})",
      element.getProtoMethodName(),
      element.getHapiFieldName(),
      if (element.typeList.first().profileList.isNotEmpty())
        element.typeList.first().normalizeType()
      else ""
    )

    hapiBuilder.addStatement(
      "hapiValue$singleMethodTemplate(%L.map{it.toHapi()})",
      element.getHapiMethodName(),
      element.getProtoFieldName(isRepeated = true),
    )
  }

  backboneElementMap[element.path.value] =
    BackBoneElementData(
      toProtoBuilder.addStatement(
        "val protoValue = %T.newBuilder()",
        element.getBackBoneProtoClass(
          backboneElementMap[element.path.value.substringBeforeLast(".")]
        )
      ),
      element
        .getBackBoneProtoClass(backboneElementMap[element.path.value.substringBeforeLast(".")])
        .canonicalName
        .removePrefix("$protoPackage."),
      toHapiBuilder.addStatement(
        "val hapiValue = %T()",
        element.getBackBoneHapiClass(
          backboneElementMap[element.path.value.substringBeforeLast(".")]
        )
      ),
      element
        .getBackBoneHapiClass(backboneElementMap[element.path.value.substringBeforeLast(".")])
        .canonicalName
        .removePrefix("$hapiPackage.")
    )
}
