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

import com.google.common.base.Ascii
import com.google.fhir.r4.core.ElementDefinition
import com.google.fhir.r4.core.Extension
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.StructureDefinition
import com.google.fhir.r4.core.ValueSet
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.TypeSpec
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern

object CompositeCodegen {

  val valueSetUrlMap = mutableMapOf<kotlin.String, ValueSet>()
  val profileUrlMap = mutableMapOf<kotlin.String, StructureDefinition>()
  private val ACRONYM_PATTERN: Pattern = Pattern.compile("([A-Z])([A-Z]+)(?![a-z])")

  // TODO handle these
  private val ignoreValueSet =
    listOf(
      "http://hl7.org/fhir/ValueSet/languages",
      "http://hl7.org/fhir/ValueSet/expression-language",
      "http://hl7.org/fhir/ValueSet/all-types|4.0.1",
      "http://hl7.org/fhir/ValueSet/guide-parameter-code|4.0.1",
      "http://hl7.org/fhir/ValueSet/resource-types|4.0.1",
      "http://hl7.org/fhir/ValueSet/defined-types|4.0.1",
      "http://hl7.org/fhir/ValueSet/medication-statement-status|4.0.1"
    )
  // TODO handle these
  private val specialValueSet =
    listOf(
      "http://hl7.org/fhir/ValueSet/mimetypes|4.0.1",
      "http://hl7.org/fhir/ValueSet/currencies|4.0.1"
    )
  // Map of valueSet url that are renamed in Fhir protos
  private val CODE_SYSTEM_RENAMES =
    mapOf(
      "http://hl7.org/fhir/secondary-finding" to "ObservationSecondaryFindingCode",
      "http://terminology.hl7.org/CodeSystem/composition-altcode-kind" to
        "CompositionAlternativeCodeKindCode",
      "http://hl7.org/fhir/contract-security-classification" to
        "ContractResourceSecurityClassificationCode",
      "http://hl7.org/fhir/device-definition-status" to "FHIRDeviceDefinitionStatusCode",
      "http://hl7.org/fhir/CodeSystem/medication-statement-status" to
        "MedicationStatementStatusCodes"
    )
  // proto package that contains the structures
  internal const val protoPackage = "com.google.fhir.r4.core"
  // hapi packages that contains teh structures
  internal const val hapiPackage = "org.hl7.fhir.r4.model"
  // uri that specifies if a valueset is in the the common Enumerations Class
  private const val uriCommon =
    "http://hl7.org/fhir/StructureDefinition/elementdefinition-isCommonBinding"
  private const val uriBindingName =
    "http://hl7.org/fhir/StructureDefinition/elementdefinition-bindingName"
  private const val FHIRPATH_TYPE_PREFIX = "http://hl7.org/fhirpath/"
  private const val FHIR_TYPE_EXTENSION_URL =
    "http://hl7.org/fhir/StructureDefinition/structuredefinition-fhir-type"

  // will be used in backbone elements
  private const val explicitTypeName =
    "http://hl7.org/fhir/StructureDefinition/structuredefinition-explicit-type-name"
  // package that contains all converters
  private const val converterPackage = "com.google.android.fhir.hapiprotoconverter.generated"
  // template for when the max value of an element is 1 ( in protos )
  private const val singleMethodTemplate = ".set%L"
  // template for when the max value of an element is > 1 ( in protos )
  private const val multipleMethodTemplate = ".addAll%L"
  // class where all common enums are in Hapi
  private val commonEnumClass = ClassName(hapiPackage, "Enumerations")

  private val RESERVED_FIELD_NAMES_JAVA =
    listOf("assert", "for", "hasAnswer", "package", "string", "class")
  private val RESERVED_FIELD_NAME_KOTLIN = listOf("when", "for")

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
        .receiver(hapiClass)
        .returns(protoClass)
        .addStatement("val protoValue = %T.newBuilder()", protoClass)

    // function builder that will convert to hapi from a proto value
    val toHapiBuilder =
      FunSpec.builder("toHapi")
        .receiver(protoClass)
        .returns(hapiClass)
        .addStatement("val hapiValue = %T()", hapiClass)

    // inner function to get protoBuilder from the name of the element ( used to get backbone
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
      // Igenore elements that have 0 as the max value
      if (element.max.value == "0") {
        continue
      }
      // TODO handle this
      if (element.base.path.value == "DomainResource.contained") {
        continue
      }

      if (element.typeList.all { normalizeType(it) == "Resource" }) {
        continue
      }
      // name of the element
      val elementName = element.getElementName()
      // if the name is itself skip
      if (elementName.capitalizeFirst() == def.type.value.capitalizeFirst()) {
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
      if (normalizeType(element.typeList.single()) == "BackboneElement" ||
          normalizeType(element.typeList.single()) == "Element"
      ) {

        handleBackBoneElementTypes(
          element,
          getToHapiBuilder(element),
          getToProtoBuilder(element),
          backboneElementMap,
          fileBuilder
        )
      }
      // check if it is an enum
      else if (normalizeType(element.typeList.single()) == "Code" &&
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
        // TODO handle this
        if (normalizeType(element.typeList.single()).lowerCaseFirst() == "xhtml") {
          continue
        }
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

  // TODO fix this and improve code
  private fun getEnumNameFromElement(element: ElementDefinition): ClassName {
    if (element.hasBinding() && element.binding.hasValueSet()) {
      // TODO handle code system renames

      if (valueSetUrlMap[element.binding.valueSet.value.split("|").first()]!!.hasCompose() &&
          valueSetUrlMap[element.binding.valueSet.value.split("|").first()]!!
            .compose
            .includeList
            .size == 1 &&
          valueSetUrlMap[element.binding.valueSet.value.split("|").first()]!!
            .compose
            .includeList
            .single()
            .conceptList
            .isEmpty()
      ) {
        return ClassName(
          protoPackage,
          getCodeSystemName(
            valueSetUrlMap[element.binding.valueSet.value.split("|").first()]!!.name
          ),
          "Value"
        )
      }
      return ClassName(
        protoPackage,
        getValueSetName(valueSetUrlMap[element.binding.valueSet.value.split("|").first()]!!.name),
        "Value"
      )
    }
    throw IllegalArgumentException("element is not in valueSetUrlMap")
  }
  /**
   */
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
    return if (filteredName.endsWith("Code")) filteredName else filteredName + "Code"
  }

  private fun getValueSetName(name: String): kotlin.String {
    val filteredName =
      name
        .value
        .split("-")
        .joinToString("") { it.capitalizeFirst() }
        .replace("[^A-Za-z0-9]", "")
        .capitalizeFirst()
    if (filteredName.endsWith("ValueSets")) {
      return filteredName.substring(0, filteredName.length - 1)
    }
    return if (filteredName.endsWith("ValueSet")) filteredName else filteredName + "ValueSet"
  }

  private fun normalizeType(type: ElementDefinition.TypeRef): kotlin.String {

    val code: kotlin.String = type.code.value

    if (code.startsWith(FHIRPATH_TYPE_PREFIX)) {
      for (extension in type.extensionList) {
        if (extension.url.value.equals(FHIR_TYPE_EXTENSION_URL)) {
          return extension.value.url.value.capitalizeFirst()
        }
      }
    }
    if (type.profileCount == 0 || type.getProfile(0).value.isEmpty()) {
      return code.capitalizeFirst()
    }
    val profileUrl = type.getProfile(0).value

    if (profileUrlMap[profileUrl] != null) {
      return profileUrlMap[profileUrl]!!.name.value
    }
    throw java.lang.IllegalArgumentException("Unable to deduce typename for profile: $profileUrl")
  }
  /** @returns the name of [element] */
  private fun ElementDefinition.getElementName(): kotlin.String {
    return if (hasExtension(explicitTypeName)) {
      getExtension(explicitTypeName).value.stringValue.value
    } else {
      id.value.split(".").last().capitalizeFirst()
    }
  }

  private fun handleOtherType(
    element: ElementDefinition,
    hapiBuilder: FunSpec.Builder,
    protoBuilder: FunSpec.Builder,
    fileBuilder: FileSpec.Builder
  ) {
    val isSingle = element.max.value == "1"
    val elementNameProto = element.getProtoMethodName()
    val elementNameHapi = element.getHapiMethodName()
    val toProto =
      MemberName(
        ClassName(
          converterPackage,
          "${normalizeType(element.typeList.single()).capitalizeFirst()}Converter"
        ),
        "toProto"
      )
    val toHapi =
      MemberName(
        ClassName(
          converterPackage,
          "${normalizeType(element.typeList.single()).capitalizeFirst()}Converter"
        ),
        "toHapi"
      )
    fileBuilder.addImport(toProto.enclosingClassName!!, toProto.simpleName)
    fileBuilder.addImport(toHapi.enclosingClassName!!, toHapi.simpleName)
    if (isSingle) {
      protoBuilder.addStatement(
        "$singleMethodTemplate(${if (element.typeList.first().profileList.isNotEmpty()) "( %L as %T )" else "%L%L"}.toProto())",
        elementNameProto.capitalizeFirst(),
        (elementNameHapi.lowerCaseFirst() +
            if (normalizeType(element.typeList.single()).lowerCaseFirst() in
                GeneratorUtils.primitiveTypeList
            )
              "Element"
            else "")
          .checkForKotlinKeyWord(),
        if (element.typeList.first().profileList.isNotEmpty())
          ClassName(hapiPackage, normalizeType(element.typeList.first()))
        else ""
      )
      hapiBuilder.addStatement(
        "hapiValue$singleMethodTemplate(%L.toHapi())",
        elementNameHapi.capitalizeFirst() +
          if (normalizeType(element.typeList.single()).lowerCaseFirst() in
              GeneratorUtils.primitiveTypeList
          )
            "Element"
          else "",
        elementNameProto.lowerCaseFirst().checkForKotlinKeyWord(),
      )
    } else {
      protoBuilder.addStatement(
        "$multipleMethodTemplate(%L.map{${if (element.typeList.first().profileList.isNotEmpty()) "( it as %T  )" else "it%L"}.toProto()})",
        elementNameProto.capitalizeFirst(),
        elementNameHapi.lowerCaseFirst().checkForKotlinKeyWord(),
        if (element.typeList.first().profileList.isNotEmpty())
          ClassName(hapiPackage, normalizeType(element.typeList.first()))
        else ""
      )
      hapiBuilder.addStatement(
        "hapiValue$singleMethodTemplate(%L.map{it.toHapi()})",
        elementNameHapi.capitalizeFirst(),
        elementNameProto.lowerCaseFirst() + "List"
      )
    }
  }
  /**
   * @param element element definition of a choice type
   * @param hapiBuilder hapi FunctionBuilder of type that contains the choice type
   * @param protoBuilder proto FunctionBuilder of type that contains the choice type
   * @param fileBuilder FileBuilder for the file that will contain [hapiBuilder] & [protoBuilder]
   *
   * @returns a list of FunctionBuilders that convert [element] from hapi to proto and vice versa
   */
  private fun handleChoiceType(
    element: ElementDefinition,
    hapiBuilder: FunSpec.Builder,
    protoBuilder: FunSpec.Builder,
    fileBuilder: FileSpec.Builder,
    backboneElementMap: MutableMap<kotlin.String, BackBoneElementData>
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
      element.getChoiceTypeProtoClass(
        backboneElementMap[element.path.value.substringBeforeLast(".")]
      )
    )

    for (type in element.typeList) {
      // TODO handle this
      if (normalizeType(type).lowerCaseFirst() == "meta") {
        continue
      }
      elementToProtoBuilder.beginControlFlow(
        "if (this is %T)",
        ClassName(
          hapiPackage,
          normalizeType(type) +
            if (normalizeType(type).lowerCaseFirst() in GeneratorUtils.primitiveTypeList) "Type"
            else ""
        )
      )
      val toProto =
        MemberName(
          ClassName(converterPackage, "${normalizeType(type).capitalizeFirst()}Converter"),
          "toProto"
        )
      val toHapi =
        MemberName(
          ClassName(converterPackage, "${normalizeType(type).capitalizeFirst()}Converter"),
          "toHapi"
        )
      fileBuilder.addImport(toProto.enclosingClassName!!, toProto.simpleName)
      fileBuilder.addImport(toHapi.enclosingClassName!!, toHapi.simpleName)

      elementToProtoBuilder.addStatement(
        "protoValue$singleMethodTemplate(this.toProto())",
        if (normalizeType(type) == "String") "StringValue" else type.code.value.capitalizeFirst()
      )
      elementToProtoBuilder.endControlFlow()

      elementToHapiBuilder.beginControlFlow(
        "if (this.get%L() != %T.newBuilder().defaultInstanceForType )",
        if (normalizeType(type) == "String") "StringValue" else type.code.value.capitalizeFirst(),
        ClassName(protoPackage, normalizeType(type))
      )
      elementToHapiBuilder.addStatement(
        "return (this.get%L()).toHapi()",
        if (normalizeType(type) == "String") "StringValue" else type.code.value.capitalizeFirst()
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
      element
        .getProtoMethodName()
        .removeSuffix(choiceTypeSuffixStructureDefinition)
        .capitalizeFirst(),
      element
        .getHapiMethodName()
        .removeSuffix(choiceTypeSuffixStructureDefinition)
        .lowerCaseFirst()
        .checkForKotlinKeyWord(),
      elementToProtoBuilder.build()
    )
    hapiBuilder.addStatement(
      "hapiValue$singleMethodTemplate(%L.%N())",
      element
        .getHapiMethodName()
        .removeSuffix(choiceTypeSuffixStructureDefinition)
        .capitalizeFirst(),
      element
        .getProtoMethodName()
        .removeSuffix(choiceTypeSuffixStructureDefinition)
        .lowerCaseFirst()
        .checkForKotlinKeyWord(),
      elementToHapiBuilder.build()
    )

    return listOf(elementToHapiBuilder.build(), elementToProtoBuilder.build())
  }

  private fun handleBackBoneElementTypes(
    element: ElementDefinition,
    hapiBuilder: FunSpec.Builder,
    protoBuilder: FunSpec.Builder,
    backboneElementMap: MutableMap<kotlin.String, BackBoneElementData>,
    fileBuilder: FileSpec.Builder
  ) {
    val (toProtoBuilder, toHapiBuilder) =
      getHapiProtoConverterFuncPair(
        "",
        element.getBackBoneProtoClass(
          backboneElementMap[element.path.value.substringBeforeLast(".")]
        ),
        element.getBackBoneHapiClass(
          backboneElementMap[element.path.value.substringBeforeLast(".")]
        )
      )
    // create a new entry in the backbone element map
    val isSingle = element.max.value == "1"
    val elementNameProto = element.getProtoMethodName()
    val elementNameHapi = element.getHapiMethodName()
    //    val toProto = MemberName(ClassName(converterPackage,
    // "${normalizeType(element.typeList.single()).capitalizeFirst()}Converter"),"toProto")
    //    val toHapi = MemberName(ClassName(converterPackage,
    // "${normalizeType(element.typeList.single()).capitalizeFirst()}Converter"),"toHapi")
    //    fileBuilder.addImport(toProto.enclosingClassName!!,toProto.simpleName)
    //    fileBuilder.addImport(toHapi.enclosingClassName!!,toHapi.simpleName)

    if (isSingle) {
      protoBuilder.addStatement(
        "$singleMethodTemplate(${if (element.typeList.first().profileList.isNotEmpty()) "( %L as %T )" else "%L%L"}.toProto())",
        elementNameProto.capitalizeFirst(),
        elementNameHapi.lowerCaseFirst().checkForKotlinKeyWord(),
        if (element.typeList.first().profileList.isNotEmpty())
          normalizeType(element.typeList.first())
        else ""
      )
      hapiBuilder.addStatement(
        "hapiValue$singleMethodTemplate(%L.toHapi())",
        elementNameHapi.capitalizeFirst(),
        elementNameProto.lowerCaseFirst().checkForKotlinKeyWord()
      )
    } else {

      protoBuilder.addStatement(
        "$multipleMethodTemplate(%L.map{${if (element.typeList.first().profileList.isNotEmpty()) "( it as %T  )" else "it%L"}.toProto()})",
        elementNameProto.capitalizeFirst(),
        elementNameHapi.lowerCaseFirst().checkForKotlinKeyWord(),
        if (element.typeList.first().profileList.isNotEmpty())
          normalizeType(element.typeList.first())
        else ""
      )

      hapiBuilder.addStatement(
        "hapiValue$singleMethodTemplate(%L.map{it.toHapi()})",
        elementNameHapi.capitalizeFirst(),
        elementNameProto.lowerCaseFirst() + "List"
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

  private fun handleCodeType(
    element: ElementDefinition,
    hapiBuilder: FunSpec.Builder,
    protoBuilder: FunSpec.Builder,
    protoName: kotlin.String,
    backboneElementMap: MutableMap<kotlin.String, BackBoneElementData>
  ) {

    val isSingle = element.max.value == "1"
    val elementNameProto = element.getProtoMethodName()
    val elementNameHapi = element.getHapiMethodName()
    val isCommon =
      element.binding.extensionList.any { it.url.value == uriCommon && it.value.boolean.value }

    if (!specialValueSet.contains(element.binding.valueSet.value)) {
      if (isSingle) {
        // if enum isSingle
        protoBuilder.addStatement(
          "$singleMethodTemplate(%T.newBuilder().setValue(%T.valueOf(%L.toCode().replace(\"-\", \"_\").toUpperCase())).build())",
          elementNameHapi.capitalizeFirst(),
          // Using this just to make sure codes are present in hapi and fhir protos TODO change to
          element.getProtoCodeClass(
            protoName,
            backboneElementMap[element.path.value.substringBeforeLast(".")]
          ),
          // KotlinPoet.ClassName
          Class.forName(getEnumNameFromElement(element).reflectionName()),
          elementNameHapi.lowerCaseFirst().checkForKotlinKeyWord()
        )
        hapiBuilder.addStatement(
          "hapiValue$singleMethodTemplate(%T.valueOf(%L.value.name.replace(\"_\",\"\")))",
          elementNameProto.capitalizeFirst(),
          element.getHapiCodeClass(isCommon),
          elementNameProto.lowerCaseFirst()
        )
      } else {
        // handle case when enum is repeated
        protoBuilder.addStatement(
          "$multipleMethodTemplate(%L.map{%T.newBuilder().setValue(%T.valueOf(it.value.toCode().replace(\"-\", \"_\").toUpperCase())).build()})",
          elementNameHapi.capitalizeFirst(),
          elementNameHapi.lowerCaseFirst().checkForKotlinKeyWord(),
          element.getProtoCodeClass(
            protoName,
            backboneElementMap[element.path.value.substringBeforeLast(".")]
          ),
          Class.forName(getEnumNameFromElement(element).reflectionName())
        )
        hapiBuilder.addStatement(
          "%L.map{hapiValue.add%L(%T.valueOf(it.value.name.replace(\"_\",\"\")))}",
          elementNameProto.lowerCaseFirst() + "List",
          elementNameProto.capitalizeFirst(),
          element.getHapiCodeClass(isCommon)
        )
      }
    } else {
      if (isSingle) {
        protoBuilder.addStatement(
          "$singleMethodTemplate(%T.newBuilder().setValue(%L).build())",
          elementNameHapi.capitalizeFirst(),
          // Using this just to make sure codes are present in hapi and fhir protos TODO change to
          element.getProtoCodeClass(
            protoName,
            backboneElementMap[element.path.value.substringBeforeLast(".")]
          ),
          // KotlinPoet.ClassName
          elementNameHapi.lowerCaseFirst()
        )
        hapiBuilder.addStatement(
          "hapiValue$singleMethodTemplate(%L.value)",
          elementNameProto.capitalizeFirst(),
          elementNameProto.lowerCaseFirst()
        )
      } else {
        protoBuilder.addStatement(
          "$multipleMethodTemplate(%L.map{%T.newBuilder().setValue(it.value).build()})",
          elementNameHapi.capitalizeFirst(),
          elementNameHapi.lowerCaseFirst(),
          element.getProtoCodeClass(
            protoName,
            backboneElementMap[element.path.value.substringBeforeLast(".")]
          )
        )

        hapiBuilder.addStatement(
          "%L.map{hapiValue.add%L(it.value)}",
          elementNameProto.lowerCaseFirst() + "List",
          elementNameProto.capitalizeFirst()
        )
      }
    }
  }
  private fun ElementDefinition.getElementMethodName(): kotlin.String {
    return this.path.value.substringAfterLast(".")
  }

  private fun ElementDefinition.getProtoMethodName(): kotlin.String {
    val value =
      if (getElementMethodName().lowerCaseFirst() in listOf("class", "assert", "for"))
        "${getElementMethodName()}Value"
      else getElementMethodName()

    return value.resolveAcronyms()
  }

  fun kotlin.String.resolveAcronyms(): kotlin.String {

    val matcher: Matcher = ACRONYM_PATTERN.matcher(this)
    val sb = StringBuffer()
    while (matcher.find()) {
      matcher.appendReplacement(sb, matcher.group(1) + Ascii.toLowerCase(matcher.group(2)))
    }
    matcher.appendTail(sb)
    return sb.toString()
  }

  private fun ElementDefinition.getHapiMethodName(): kotlin.String {
    return if (getElementMethodName().lowerCaseFirst() in listOf("class"))
      "${getElementMethodName()}_"
    else getElementMethodName()
  }

  private fun ElementDefinition.getBackBoneProtoClass(data: BackBoneElementData?): ClassName {
    return ClassName(
        protoPackage,
        data?.protoName
          ?: this.path.value.substringBeforeLast(".").split(".").joinToString(".") {
            it.capitalizeFirst()
          }
      )
      .nestedClass(
        if (this.getElementName().lowerCaseFirst() == "code") "CodeType"
        else this.getElementName().capitalizeFirst()
      )
  }

  private fun ElementDefinition.getBackBoneHapiClass(data: BackBoneElementData?): ClassName {
    return ClassName(hapiPackage, base.path.value.split(".").first().capitalizeFirst())
      .nestedClass(
        if (this.hasExtension(explicitTypeName)) {
          "${this.getElementName()}Component"
        } else {
          "${ data?.hapiName?.removeSuffix("Component")?.removePrefix(base.path.value.split(".").first().capitalizeFirst())
            ?: this.base.path.value.split(".").dropLast(1)
              .joinToString("") { it.capitalizeFirst() }
          }${this.getElementName()}Component"
        }
      )
  }

  private fun ElementDefinition.getProtoCodeClass(
    outerDataTypeName: kotlin.String,
    data: BackBoneElementData?
  ): ClassName {
    return ClassName(
        protoPackage,
        data?.protoName
          ?: (listOf(outerDataTypeName) + this.path.value.split(".").drop(1).dropLast(1))
            .joinToString(".") { it.capitalizeFirst() }
      )
      .nestedClass(
        when {
          binding.valueSet.value in CODE_SYSTEM_RENAMES.keys ->
            CODE_SYSTEM_RENAMES[binding.valueSet.value]!!
          this.getElementName().lowerCaseFirst() == "code" -> "CodeType"
          else ->
            this.getElementName().capitalizeFirst() +
              if (this.getElementName().capitalizeFirst().endsWith("Code", ignoreCase = true)) ""
              else "Code"
        }
      )
  }

  private fun ElementDefinition.getHapiCodeClass(isCommon: Boolean): ClassName {
    return (if (isCommon) commonEnumClass
      else ClassName(hapiPackage, base.path.value.split(".").first().capitalizeFirst()))
      .nestedClass(binding.extensionList[0].value.stringValue.value.capitalizeFirst())
  }

  private fun ElementDefinition.getChoiceTypeProtoClass(data: BackBoneElementData?): ClassName {
    return ClassName(
      protoPackage,
      if (data != null)
        listOf(
          data.protoName,
          path
            .value
            .split(".")
            .last()
            .replace(choiceTypeSuffixStructureDefinition, choiceTypeSuffixProto)
            .capitalizeFirst()
        )
      else
        path
          .value
          .replace(choiceTypeSuffixStructureDefinition, choiceTypeSuffixProto)
          .split(".")
          .map { it.capitalizeFirst() }
    )
  }

  private fun ElementDefinition.getChoiceTypeHapiClass(): ClassName {
    return ClassName(hapiPackage, "Type")
  }

  private fun ElementDefinition.hasExtension(uri: kotlin.String): Boolean {
    return extensionList.any { it.url.value == uri }
  }
  private fun ElementDefinition.getExtension(uri: kotlin.String): Extension {
    return extensionList.single { it.url.value == uri }
  }
  private fun getHapiProtoConverterFuncPair(
    funcName: kotlin.String,
    protoClass: ClassName,
    hapiClass: ClassName
  ): Pair<FunSpec.Builder, FunSpec.Builder> {

    return FunSpec.builder("${funcName}ToProto".lowerCaseFirst())
      .receiver(hapiClass)
      .returns(protoClass)
      .addModifiers(KModifier.PRIVATE) to
      FunSpec.builder("${funcName}ToHapi".lowerCaseFirst())
        .receiver(protoClass)
        .returns(hapiClass)
        .addModifiers(KModifier.PRIVATE)
  }

  private fun CharSequence.lowerCaseFirst() =
    (if (this[0].isUpperCase()) this[0] + 32 else this[0]).toChar().toString() + this.drop(1)

  private fun CharSequence.capitalizeFirst() =
    (if (this[0].isLowerCase()) this[0] - 32 else this[0]).toChar().toString() + this.drop(1)

  private const val choiceTypeSuffixStructureDefinition = "[x]"
  private const val choiceTypeSuffixProto = "X"
  private data class BackBoneElementData(
    val protoBuilder: FunSpec.Builder,
    val protoName: kotlin.String,
    val hapiBuilder: FunSpec.Builder,
    val hapiName: kotlin.String
  )

  private fun kotlin.String.checkForKotlinKeyWord(): kotlin.String {
    if (this.lowerCaseFirst() in RESERVED_FIELD_NAME_KOTLIN) {
      return "`$this`"
    }
    return this
  }
}
