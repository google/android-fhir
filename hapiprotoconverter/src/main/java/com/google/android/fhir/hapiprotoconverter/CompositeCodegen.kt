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
import com.google.fhir.r4.core.Extension
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.StructureDefinition
import com.google.fhir.r4.core.ValueSet
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import java.io.File

object CompositeCodegen {

  val valueSetUrlMap = mutableMapOf<kotlin.String, ValueSet>()
  val profileUrlMap = mutableMapOf<kotlin.String, StructureDefinition>()
  private val ignoreValueSet =
    listOf(
      "http://hl7.org/fhir/ValueSet/languages",
      // todo remove MimeTypeCode$Value & languages
      // TODO handle fhirALLVALUES
      "http://hl7.org/fhir/ValueSet/expression-language",
      "http://hl7.org/fhir/ValueSet/all-types|4.0.1"
    )

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
  private val RESERVED_FIELD_NAME_KOTLIN = listOf("when")

  /**
   * @param def structure definition of the resource/complex-type that needs to be generated
   * @param outLocation file where the converter object will be generated
   */
  fun generate(def: StructureDefinition, outLocation: File? = null) {
    // TODO Map all value sets instead of using toCode() and valueOf() ( currently works )
    // TODO raise issue on kotlin poet repository about nested class imports while using import
    // aliases (currently works)

    // hapi name of the Resource / datatype
    val hapiName = def.id.value.capitalizeFirst()
    // proto name of the Resource / datatype
    val protoName = def.id.value.capitalizeFirst()
    // hapi name of the Resource / datatype
    val hapiClass = ClassName(hapiPackage, hapiName)
    // hapi name of the Resource / datatype
    val protoClass = ClassName(protoPackage, protoName)
    // file builder for file that contains the converter object
    val fileBuilder = FileSpec.builder(converterPackage, "${protoName}Converter")
    // the class (object) that will contain the convert functions
    val complexConverterClass = ClassName(converterPackage, "${protoName}Converter")

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
      // name of the element
      val elementName = getElementName(element)
      // if the name is itself skip
      if (elementName.capitalizeFirst() == def.type.value.capitalizeFirst()) {
        continue
      }
      // handle choice type
      if (element.typeList.size > 1) {
        functionsList.addAll(
          handleChoiceType(element, getToHapiBuilder(element), getToProtoBuilder(element))
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
              getElementName(element).capitalizeFirst(),
              getElementName(element).lowerCaseFirst()
            )

          getToHapiBuilder(element)
            .addStatement(
              "hapiValue$singleMethodTemplate(%L.toHapi())",
              getElementName(element).capitalizeFirst(),
              getElementName(element).lowerCaseFirst()
            )
        } else {

          getToProtoBuilder(element)
            .addStatement(
              "$multipleMethodTemplate(%L.map{it.toProto()})",
              getElementName(element).capitalizeFirst(),
              getElementName(element).lowerCaseFirst()
            )

          getToHapiBuilder(element)
            .addStatement(
              "hapiValue$singleMethodTemplate(%L.map{it.toHapi()})",
              getElementName(element).capitalizeFirst(),
              getElementName(element).lowerCaseFirst() + "List"
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
          backboneElementMap
        )
      }
      // check if it is an enum
      else if (normalizeType(element.typeList.single()) == "Code" &&
          element.binding != ElementDefinition.ElementDefinitionBinding.getDefaultInstance() &&
          !ignoreValueSet.contains(element.binding.valueSet.value)
      ) {
        handleCodeType(element, getToHapiBuilder(element), getToProtoBuilder(element), protoName)
      } else {
        // element will be either another resource or a datatype
        handleOtherType(element, getToHapiBuilder(element), getToProtoBuilder(element))
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
    functionsList.forEach { fileBuilder.addFunction(it) }
    fileBuilder.build().writeTo(outLocation!!)
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
  /**
   * @param element the element definition of an element
   * @returns the name of [element]
   */
  private fun getElementName(element: ElementDefinition): kotlin.String {
    return if (element.hasExtension(explicitTypeName)) {
      element.getExtension(explicitTypeName).value.stringValue.value
    } else {
      element.id.value.split(".").last().capitalizeFirst()
    }
  }

  private fun handleOtherType(
    element: ElementDefinition,
    hapiBuilder: FunSpec.Builder,
    protoBuilder: FunSpec.Builder
  ) {
    val isSingle = element.max.value == "1"
    val elementName = getElementName(element)
    if (isSingle) {
      protoBuilder.addStatement(
        "$singleMethodTemplate(${if (element.typeList.first().profileList.isNotEmpty()) "( %L as %T )" else "%L%L"}.toProto())",
        elementName.capitalizeFirst(),
        elementName.lowerCaseFirst() +
          if (normalizeType(element.typeList.single()).lowerCaseFirst() in
              GeneratorUtils.primitiveTypeList
          )
            "Element"
          else "",
        if (element.typeList.first().profileList.isNotEmpty())
          ClassName(hapiPackage, normalizeType(element.typeList.first()))
        else ""
      )
      hapiBuilder.addStatement(
        "hapiValue$singleMethodTemplate(%L.toHapi())",
        elementName.capitalizeFirst() +
          if (normalizeType(element.typeList.single()).lowerCaseFirst() in
              GeneratorUtils.primitiveTypeList
          )
            "Element"
          else "",
        elementName.lowerCaseFirst(),
      )
    } else {
      protoBuilder.addStatement(
        "$multipleMethodTemplate(%L.map{${if (element.typeList.first().profileList.isNotEmpty()) "( it as %T  )" else "it%L"}.toProto()})",
        elementName.capitalizeFirst(),
        elementName.lowerCaseFirst(),
        if (element.typeList.first().profileList.isNotEmpty())
          ClassName(hapiPackage, normalizeType(element.typeList.first()))
        else ""
      )
      hapiBuilder.addStatement(
        "hapiValue$singleMethodTemplate(%L.map{it.toHapi()})",
        elementName.capitalizeFirst(),
        elementName.lowerCaseFirst() + "List",
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
    protoBuilder: FunSpec.Builder
  ): List<FunSpec> {
    val (elementToProtoBuilder, elementToHapiBuilder) =
      getHapiProtoConverterFuncPair(
        element
          .path
          .value
          .split(".")
          .joinToString("") { it.capitalizeFirst() }
          .removeSuffix(choiceTypeSuffixStructureDefinition),
        element.getChoiceTypeProtoClass(),
        element.getChoiceTypeHapiClass()
      )
    elementToProtoBuilder.addStatement(
      "val protoValue = %T.newBuilder()",
      element.getChoiceTypeProtoClass()
    )

    for (type in element.typeList) {
      elementToProtoBuilder.beginControlFlow(
        "if (this is %T)",
        ClassName(
          hapiPackage,
          normalizeType(type) +
            if (normalizeType(type).lowerCaseFirst() in GeneratorUtils.primitiveTypeList) "Type"
            else ""
        )
      )
      elementToProtoBuilder.addStatement(
        "protoValue$singleMethodTemplate(this.toProto())",
        if (normalizeType(type) == "String") "StringValue" else type.code.value.capitalizeFirst(),
      )
      elementToProtoBuilder.endControlFlow()

      elementToHapiBuilder.beginControlFlow(
        "if (this.get%L() != %T.newBuilder().defaultInstanceForType )",
        if (normalizeType(type) == "String") "StringValue" else type.code.value.capitalizeFirst(),
        ClassName(protoPackage, normalizeType(type))
      )
      elementToHapiBuilder.addStatement(
        "return (this.get%L()).toHapi()",
        if (normalizeType(type) == "String") "StringValue" else type.code.value.capitalizeFirst(),
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
      getElementName(element).removeSuffix(choiceTypeSuffixStructureDefinition).capitalizeFirst(),
      getElementName(element).removeSuffix(choiceTypeSuffixStructureDefinition).lowerCaseFirst(),
      elementToProtoBuilder.build()
    )
    hapiBuilder.addStatement(
      "hapiValue$singleMethodTemplate(%L.%N())",
      getElementName(element).removeSuffix(choiceTypeSuffixStructureDefinition).capitalizeFirst(),
      getElementName(element).removeSuffix(choiceTypeSuffixStructureDefinition).lowerCaseFirst(),
      elementToHapiBuilder.build()
    )

    return listOf(elementToHapiBuilder.build(), elementToProtoBuilder.build())
  }

  private fun handleBackBoneElementTypes(
    element: ElementDefinition,
    hapiBuilder: FunSpec.Builder,
    protoBuilder: FunSpec.Builder,
    backboneElementMap: MutableMap<kotlin.String, BackBoneElementData>
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
    val elementName = getElementName(element)
    if (isSingle) {
      protoBuilder.addStatement(
        "$singleMethodTemplate(${if (element.typeList.first().profileList.isNotEmpty()) "( %L as %T )" else "%L%L"}.toProto())",
        elementName.capitalizeFirst(),
        elementName.lowerCaseFirst(),
        if (element.typeList.first().profileList.isNotEmpty())
          normalizeType(element.typeList.first())
        else ""
      )
      hapiBuilder.addStatement(
        "hapiValue$singleMethodTemplate(%L.toHapi())",
        elementName.capitalizeFirst(),
        elementName.lowerCaseFirst()
      )
    } else {

      protoBuilder.addStatement(
        "$multipleMethodTemplate(%L.map{${if (element.typeList.first().profileList.isNotEmpty()) "( it as %T  )" else "it%L"}.toProto()})",
        elementName.capitalizeFirst(),
        elementName.lowerCaseFirst(),
        if (element.typeList.first().profileList.isNotEmpty())
          normalizeType(element.typeList.first())
        else ""
      )

      hapiBuilder.addStatement(
        "hapiValue$singleMethodTemplate(%L.map{it.toHapi()})",
        elementName.capitalizeFirst(),
        elementName.lowerCaseFirst() + "List"
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
    protoName: kotlin.String
  ) {

    val isSingle = element.max.value == "1"
    val elementName = getElementName(element)
    val isCommon =
      element.binding.extensionList.any { it.url.value == uriCommon && it.value.boolean.value }

    if (!specialValueSet.contains(element.binding.valueSet.value)) {
      if (isSingle) {
        // if enum isSingle
        protoBuilder.addStatement(
          "$singleMethodTemplate(%T.newBuilder().setValue(%T.valueOf(%L.toCode().replace(\"-\", \"_\").toUpperCase())).build())",
          elementName.capitalizeFirst(),
          // Using this just to make sure codes are present in hapi and fhir protos TODO change to
          element.getProtoCodeClass(protoName),
          // KotlinPoet.ClassName
          Class.forName(getEnumNameFromElement(element).reflectionName()),
          elementName.lowerCaseFirst()
        )
        hapiBuilder.addStatement(
          "hapiValue$singleMethodTemplate(%T.valueOf(%L.value.name.replace(\"_\",\"\")))",
          elementName.capitalizeFirst(),
          element.getHapiCodeClass(isCommon),
          elementName.lowerCaseFirst()
        )
      } else {
        // handle case when enum is repeated
        protoBuilder.addStatement(
          "$multipleMethodTemplate(%L.map{%T.newBuilder().setValue(%T.valueOf(it.value.toCode().replace(\"-\", \"_\").toUpperCase())).build()})",
          elementName.capitalizeFirst(),
          elementName.lowerCaseFirst(),
          element.getProtoCodeClass(protoName),
          Class.forName(getEnumNameFromElement(element).reflectionName())
        )
        hapiBuilder.addStatement(
          "%L.map{hapiValue.add%L(%T.valueOf(it.value.name.replace(\"_\",\"\")))}",
          elementName.lowerCaseFirst() + "List",
          elementName.capitalizeFirst(),
          element.getHapiCodeClass(isCommon)
        )
      }
    } else {
      if (isSingle) {
        protoBuilder.addStatement(
          "$singleMethodTemplate(%T.newBuilder().setValue(%L).build())",
          elementName.capitalizeFirst(),
          // Using this just to make sure codes are present in hapi and fhir protos TODO change to
          element.getProtoCodeClass(protoName),
          // KotlinPoet.ClassName
          elementName.lowerCaseFirst()
        )
        hapiBuilder.addStatement(
          "hapiValue$singleMethodTemplate(%L.value)",
          elementName.capitalizeFirst(),
          elementName.lowerCaseFirst()
        )
      } else {
        protoBuilder.addStatement(
          "$multipleMethodTemplate(%L.map{%T.newBuilder().setValue(it).build()})",
          elementName.capitalizeFirst(),
          elementName.lowerCaseFirst(),
          element.getProtoCodeClass(protoName)
        )

        hapiBuilder.addStatement(
          "%L.map{hapiValue.add%L(it)}",
          elementName.lowerCaseFirst() + "List",
          elementName.capitalizeFirst()
        )
      }
    }
  }

  private fun ElementDefinition.getBackBoneProtoClass(data: BackBoneElementData?): ClassName {
    return ClassName(protoPackage, data?.protoName ?: this.path.value.substringBeforeLast("."))
      .nestedClass(getElementName(this))
  }

  private fun ElementDefinition.getBackBoneHapiClass(data: BackBoneElementData?): ClassName {
    return ClassName(hapiPackage, base.path.value.split(".").first().capitalizeFirst())
      .nestedClass(
        if (this.hasExtension(explicitTypeName)) {
          "${getElementName(this)}Component"
        } else {
          "${ data?.hapiName?.removeSuffix("Component")?.removePrefix(base.path.value.split(".").first().capitalizeFirst())
            ?: this.base.path.value.split(".").dropLast(1)
              .joinToString("") { it.capitalizeFirst() }
          }${getElementName(this)}Component"
        }
      )
  }

  private fun ElementDefinition.getProtoCodeClass(outerDataTypeName: kotlin.String): ClassName {
    return ClassName(
        protoPackage,
        listOf(outerDataTypeName) +
          this.path.value.split(".").drop(1).dropLast(1).map { it.capitalizeFirst() }
      )
      .nestedClass(
        if (binding.valueSet.value in CODE_SYSTEM_RENAMES.keys)
          CODE_SYSTEM_RENAMES[binding.valueSet.value]!!
        else
          getElementName(this).capitalizeFirst() +
            if (getElementName(this).capitalizeFirst().endsWith("Code", ignoreCase = true)) ""
            else "Code"
      )
  }

  private fun ElementDefinition.getHapiCodeClass(isCommon: Boolean): ClassName {
    return (if (isCommon) commonEnumClass
      else ClassName(hapiPackage, base.path.value.split(".").first().capitalizeFirst()))
      .nestedClass(binding.extensionList[0].value.stringValue.value.capitalizeFirst())
  }

  private fun ElementDefinition.getChoiceTypeProtoClass(): ClassName {
    return ClassName(
      protoPackage,
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
}
