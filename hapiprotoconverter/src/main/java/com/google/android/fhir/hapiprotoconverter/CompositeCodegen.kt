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
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.StructureDefinition
import com.google.fhir.r4.core.ValueSet
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import java.io.File

object CompositeCodegen {

  val valueSetUrlMap = mutableMapOf<kotlin.String, ValueSet>()
  val profileUrlMap = mutableMapOf<kotlin.String, StructureDefinition>()

  /**
   * @param def structure definition of the resource/complex-type that needs to be generated
   * @param outLocation file where the converter object will be generated
   */
  fun generate(def: StructureDefinition, outLocation: File? = null) {

    val hapiName = def.id.value.capitalizeFirst()
    val protoName = def.id.value.capitalizeFirst()

    val hapiClass = ClassName(hapiPackage, hapiName)
    val protoClass = ClassName(protoPackage, protoName)

    val fileBuilder = FileSpec.builder(converterPackage, "${protoName}Converter")
    val complexConverterClass = ClassName(converterPackage, "${protoName}Converter")
    val complexConverter = TypeSpec.objectBuilder(complexConverterClass)

    // list of functions that the object will contain
    val functionsList = mutableListOf<FunSpec>()

    // map of backbone element names to a pair of functions that convert them to hapi and protobufs
    // respectively
    val backboneElementMap = mutableMapOf<kotlin.String, BackBoneElementData>()

    // function builder that will convert to proto from a hapi value
    val toProtoBuilder =
      FunSpec.builder("toProto")
        .addAnnotation(JvmStatic::class)
        .receiver(hapiClass)
        .returns(protoClass)
        .addStatement("val protoValue = %T.newBuilder()", protoClass)

    // function builder that will convert to hapi from a proto value
    val toHapiBuilder =
      FunSpec.builder("toHapi")
        .addAnnotation(JvmStatic::class)
        .receiver(protoClass)
        .returns(hapiClass)
        .addStatement("val hapiValue = %T()", hapiClass)

    // inner function to get protoBuilder from the name of the element (used to get backbone
    // builder or base)
    fun getToProtoBuilder(element: ElementDefinition): FunSpec.Builder {
      if (backboneElementMap.containsKey(element.path.value.substringBeforeLast("."))) {
        return backboneElementMap[element.path.value.substringBeforeLast(".")]!!.protoBuilder
      }
      return toProtoBuilder
    }
    // inner function to get hapiBuilder from the name of the element ( used to get backbone builder
    // or base)
    fun getToHapiBuilder(element: ElementDefinition): FunSpec.Builder {
      if (backboneElementMap.containsKey(element.path.value.substringBeforeLast("."))) {
        return backboneElementMap[element.path.value.substringBeforeLast(".")]!!.hapiBuilder
      }
      return toHapiBuilder
    }

    // Iterate over elementList
    for (element in def.snapshot.elementList) {
      // ignore elements that have 0 as the max value
      if (element.max.value == "0") {
        continue
      }

      // TODO handle this
      if (element.base.path.value == "DomainResource.contained") {
        continue
      }

      // TODO handle this
      if (element.typeList.all { it.normalizeType() == "Resource" }) {
        continue
      }

      if (element.typeList.all { it.normalizeType() == "Xhtml" }) {
        continue
      }

      // name of the element
      val elementName = element.getElementName()
      // if the name is itself skip
      // TODO handle this better
      if (elementName.equals(def.type.value, ignoreCase = true)) {
        continue
      }
      // handle choice type
      if (element.typeList.size > 1) {
        functionsList.addAll(
          handleChoiceType(
            element,
            getToHapiBuilder(element),
            getToProtoBuilder(element),
            fileBuilder,
            backboneElementMap
          )
        )
        continue
      }
      // handle id separately
      if (elementName.lowerCaseFirst() == "id") {
        getToProtoBuilder(element)
          .addStatement(
            ".setId(%T.newBuilder().setValue(id))",
            if (element.base.path.value == "Resource.id") Id::class else String::class
          )
        getToHapiBuilder(element).addStatement("hapiValue.id = id.value ")
        continue
      }
      // handle contentReference type
      if (element.contentReference.value != "") {
        val isSingle = element.max.value == "1"
        if (isSingle) {

          getToProtoBuilder(element)
            .addStatement(
              "$singleMethodTemplate(%L.toProto())",
              element.getProtoMethodName().capitalizeFirst(),
              element.getHapiMethodName().lowerCaseFirst().checkForKotlinKeyWord()
            )

          getToHapiBuilder(element)
            .addStatement(
              "hapiValue$singleMethodTemplate(%L.toHapi())",
              element.getHapiMethodName().capitalizeFirst(),
              element.getProtoMethodName().lowerCaseFirst().checkForKotlinKeyWord()
            )
        } else {

          getToProtoBuilder(element)
            .addStatement(
              "$multipleMethodTemplate(%L.map{it.toProto()})",
              element.getProtoMethodName().capitalizeFirst(),
              element.getHapiMethodName().lowerCaseFirst().checkForKotlinKeyWord()
            )

          getToHapiBuilder(element)
            .addStatement(
              "hapiValue$singleMethodTemplate(%L.map{it.toHapi()})",
              element.getHapiMethodName().capitalizeFirst(),
              element.getProtoMethodName().lowerCaseFirst() + "List"
            )
        }
        continue
      }
      // Handle the case when it is a backbone element
      if (element.typeList.single().normalizeType() == "BackboneElement" ||
          element.typeList.single().normalizeType() == "Element"
      ) {

        handleBackBoneElementTypes(
          element,
          getToHapiBuilder(element),
          getToProtoBuilder(element),
          backboneElementMap
        )
      }
      // check if it is an enum
      else if (element.typeList.single().normalizeType() == "Code" &&
          element.binding != ElementDefinition.ElementDefinitionBinding.getDefaultInstance()
      ) {
        if (ignoreValueSet.contains(element.binding.valueSet.value)) {
          continue
        }
        handleCodeType(
          element,
          getToHapiBuilder(element),
          getToProtoBuilder(element),
          protoName,
          backboneElementMap
        )
      } else {
        // element will be either another resource or a datatype
        handleOtherType(element, getToHapiBuilder(element), getToProtoBuilder(element), fileBuilder)
      }
    }
    toProtoBuilder.addStatement(".build()")
    toProtoBuilder.addStatement("return protoValue")
    toHapiBuilder.addStatement("return hapiValue")
    functionsList.add(toHapiBuilder.build())
    functionsList.add(toProtoBuilder.build())
    // TODO there is definitely a better way to do this just can't figure it out at the time
    functionsList.addAll(
      backboneElementMap.values.map {
        it.protoBuilder.addStatement(".build()").addStatement("return protoValue").build()
      }
    )
    functionsList.addAll(
      backboneElementMap.values.map { it.hapiBuilder.addStatement("return hapiValue").build() }
    )
    functionsList.forEach { complexConverter.addFunction(it) }
    fileBuilder.addType(complexConverter.build()).build().writeTo(outLocation!!)
  }
}

data class BackBoneElementData(
  val protoBuilder: FunSpec.Builder,
  val protoName: kotlin.String,
  val hapiBuilder: FunSpec.Builder,
  val hapiName: kotlin.String
)

data class HapiProtoFunPair(val protoBuilder: FunSpec.Builder, val hapiBuilder: FunSpec.Builder)

internal fun getHapiProtoConverterFuncPair(
  funcName: kotlin.String,
  protoClass: ClassName,
  hapiClass: ClassName
): HapiProtoFunPair {

  return HapiProtoFunPair(
    protoBuilder =
      FunSpec.builder("${funcName}ToProto".lowerCaseFirst())
        .addAnnotation(JvmStatic::class)
        .receiver(hapiClass)
        .returns(protoClass)
        .addModifiers(KModifier.PRIVATE),
    hapiBuilder =
      FunSpec.builder("${funcName}ToHapi".lowerCaseFirst())
        .addAnnotation(JvmStatic::class)
        .receiver(protoClass)
        .returns(hapiClass)
        .addModifiers(KModifier.PRIVATE)
  )
}
