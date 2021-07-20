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

import com.google.fhir.common.JsonFormat
import com.google.fhir.r4.core.ElementDefinition
import com.google.fhir.r4.core.Extension
import com.google.fhir.r4.core.Id
import com.google.fhir.r4.core.String
import com.google.fhir.r4.core.StructureDefinition
import com.google.fhir.r4.core.StructureDefinitionKindCode
import com.google.fhir.r4.core.ValueSet
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.File
import kotlin.math.exp

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

  private const val protoPackage = "com.google.fhir.r4.core"

  const val hapiPackage = "org.hl7.fhir.r4.model"

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

  private const val converterPackage = "com.google.android.fhir.hapiprotoconverter.generated"
  private const val singleMethodTemplate = ".set%L"
  private const val multipleMethodTemplate = ".addAll%L"

  private val commonEnumClass = ClassName(hapiPackage, "Enumerations")

  fun generate(def: StructureDefinition, outLocation: File? = null) {
    // TODO support backbone elements properly
    // TODO handle Money.currency
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
    val backboneElementMap = mutableMapOf<kotlin.String, Pair<FunSpec.Builder, FunSpec.Builder>>()

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
        return backboneElementMap[element.path.value.substringBeforeLast(".")]!!.first
      }
      return toProtoBuilder
    }

    // inner function to get hapiBuilder from the name of the element ( used to get backbone builder
    // or base)
    fun getToHapiBuilder(element: ElementDefinition): FunSpec.Builder {
      if (backboneElementMap.containsKey(element.path.value.substringBeforeLast("."))) {
        return backboneElementMap[element.path.value.substringBeforeLast(".")]!!.second
      }
      return toHapiBuilder
    }

    // Iterate over elementList
    for (element in def.snapshot.elementList) {

      if (element.max.value == "0") {
        continue
      }
      // TODO handle this
      if (element.base.path.value == "DomainResource.contained"){
        continue
      }
      // name of the element // TODO change logic
      val elementName = getElementName(element)
      // if the name is itself skip
      if (elementName.capitalizeFirst() == def.type.value) {
        continue
      }
      if (element.typeList.size > 1) {
        functionsList.addAll(
          handleChoiceType(
            element,
            getToHapiBuilder(element),
            getToProtoBuilder(element),
            fileBuilder
          )
        )
        continue
      }
      if (elementName == "id") {
        getToProtoBuilder(element)
          .addStatement(".setId(%T.newBuilder().setValue(id))", if (element.base.path.value == "Resource.id") Id::class else String::class)
        getToHapiBuilder(element).addStatement("hapiValue.id = id.value ")
        continue
      }
      if (element.contentReference.value != "") {
        val isSingle = element.max.value == "1"
        if (isSingle) {
          getToProtoBuilder(element)
            .addStatement(
              "$singleMethodTemplate(%L.toProto())",
              getElementName(element).capitalizeFirst(),
              getElementName(element)
            )
          getToHapiBuilder(element)
            .addStatement(
              "hapiValue$singleMethodTemplate(%L.toHapi())",
              getElementName(element).capitalizeFirst(),
              getElementName(element)
            )
        } else {
          getToProtoBuilder(element)
            .addStatement(
              "$multipleMethodTemplate(%L.map{it.toProto()})",
              getElementName(element).capitalizeFirst(),
              getElementName(element)
            )
          getToHapiBuilder(element)
            .addStatement(
              "hapiValue$singleMethodTemplate(%L.map{it.toHapi()})",
              getElementName(element).capitalizeFirst(),
              getElementName(element) + "List"
            )
        }
        //
        // org.hl7.fhir.r4.model.Invoice().setTotalPriceComponent().totalPriceComponent
        //
        // Invoice.newBuilder().addAllTotalPriceComponent().build().totalPriceComponentList
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
        handleOtherType(
          element,
          getToHapiBuilder(element),
          getToProtoBuilder(element),
          fileBuilder,
          hapiName
        )
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
        it.first.addStatement(".build()").addStatement("return protoValue").build()
      }
    )
    functionsList.addAll(
      backboneElementMap.values.map { it.second.addStatement("return hapiValue").build() }
    )
    fileBuilder
      .addType(
        TypeSpec.objectBuilder(complexConverterClass.simpleName).addFunctions(functionsList).build()
      )
      .build()
      .writeTo(outLocation!!)
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

  private fun getElementName(element: ElementDefinition): kotlin.String {
    // TODO change logic
    return if (element.hasExtension(explicitTypeName) ) {
      element.getExtension(explicitTypeName).value.stringValue.value.lowerCaseFirst()
    } else {
      element.id.value.split(".").last().lowerCaseFirst()
    }
  }

  private fun handleOtherType(
    element: ElementDefinition,
    hapiBuilder: FunSpec.Builder,
    protoBuilder: FunSpec.Builder,
    fileBuilder: FileSpec.Builder,
    hapiName: kotlin.String
  ) {
    val isSingle = element.max.value == "1"
    val elementName = getElementName(element)
    if (isSingle) {
      protoBuilder.addStatement(
        "$singleMethodTemplate(${if (element.typeList.first().profileList.isNotEmpty()) "( %L as %T )" else "%L%L"}.toProto())",
        elementName.capitalizeFirst(),
        elementName +
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
        elementName,
      )
    } else {
      protoBuilder.addStatement(
        "$multipleMethodTemplate(%L.map{${if (element.typeList.first().profileList.isNotEmpty()) "( it as %T  )" else "it%L"}.toProto()})",
        elementName.capitalizeFirst(),
        elementName,
        if (element.typeList.first().profileList.isNotEmpty())
          ClassName(hapiPackage, normalizeType(element.typeList.first()))
        else ""
      )
      hapiBuilder.addStatement(
        "hapiValue$singleMethodTemplate(%L.map{it.toHapi()})",
        elementName.capitalizeFirst(),
        elementName + "List",
      )
    }
    // add respective imports to the file
    if (normalizeType(element.typeList.single()).capitalizeFirst() != hapiName) {
      fileBuilder.addImport(
        ClassName(converterPackage, "${normalizeType(element.typeList.single())}Converter"),
        "toProto"
      )
      fileBuilder.addImport(
        ClassName(converterPackage, "${normalizeType(element.typeList.single())}Converter"),
        "toHapi"
      )
    }
  }

  private fun handleChoiceType(
    element: ElementDefinition,
    hapiBuilder: FunSpec.Builder,
    protoBuilder: FunSpec.Builder,
    fileBuilder: FileSpec.Builder
  ): List<FunSpec> {
    val (elementToProtoBuilder, elementToHapiBuilder) =
      getHapiProtoConverterFuncPair(
        element.path.value.split(".").joinToString("") { it.capitalizeFirst() }.removeSuffix(choiceTypeSuffixStructureDefinition),
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

      // add imports if it is not the same element
      if (normalizeType(type) != element.id.value.split(".").last().capitalizeFirst()) {

        fileBuilder.addImport(
          ClassName(converterPackage, "${normalizeType(type)}Converter"),
          "toProto"
        )
        fileBuilder.addImport(
          ClassName(converterPackage, "${normalizeType(type)}Converter"),
          "toHapi"
        )
      }
    }
    elementToProtoBuilder.addStatement("return protoValue.build()")

    elementToHapiBuilder.addStatement(
      "throw %T(%S)",
      IllegalArgumentException::class,
      element.path.value
    )
    protoBuilder.addStatement(
      "$singleMethodTemplate(%L.%N())",
      getElementName(element).removeSuffix(choiceTypeSuffixStructureDefinition).capitalizeFirst(),
      getElementName(element).removeSuffix(choiceTypeSuffixStructureDefinition),
      elementToProtoBuilder.build()
    )
    hapiBuilder.addStatement(
      "hapiValue$singleMethodTemplate(%L.%N())",
      getElementName(element).removeSuffix(choiceTypeSuffixStructureDefinition).capitalizeFirst(),
      getElementName(element).removeSuffix(choiceTypeSuffixStructureDefinition),
      elementToHapiBuilder.build()
    )

    return listOf(elementToHapiBuilder.build(), elementToProtoBuilder.build())
  }

  private fun handleBackBoneElementTypes(
    element: ElementDefinition,
    hapiBuilder: FunSpec.Builder,
    protoBuilder: FunSpec.Builder,
    backboneElementMap: MutableMap<kotlin.String, Pair<FunSpec.Builder, FunSpec.Builder>>
  ) {
    // create a new entry in the backbone element map
    val isSingle = element.max.value == "1"
    val elementName = getElementName(element)

    if (isSingle) {
      protoBuilder.addStatement(
        "$singleMethodTemplate(${if (element.typeList.first().profileList.isNotEmpty()) "( %L as %T )" else "%L%L"}.toProto())",
        elementName.capitalizeFirst(),
        elementName,
        if (element.typeList.first().profileList.isNotEmpty())
          normalizeType(element.typeList.first())
        else ""
      )
      hapiBuilder.addStatement(
        "hapiValue$singleMethodTemplate(%L.toHapi())",
        elementName.capitalizeFirst(),
        elementName
      )
    } else {

      protoBuilder.addStatement(
        "$multipleMethodTemplate(%L.map{${if (element.typeList.first().profileList.isNotEmpty()) "( it as %T  )" else "it%L"}.toProto()})",
        elementName.capitalizeFirst(),
        elementName,
        if (element.typeList.first().profileList.isNotEmpty())
          normalizeType(element.typeList.first())
        else ""
      )

      hapiBuilder.addStatement(
        "hapiValue$singleMethodTemplate(%L.map{it.toHapi()})",
        elementName.capitalizeFirst(),
        elementName + "List"
      )
    }

    val (toProtoBuilder, toHapiBuilder) =
      getHapiProtoConverterFuncPair(
        "",
        element.getBackBoneProtoClass(),
        element.getBackBoneHapiClass()
      )
    backboneElementMap[element.path.value] =
      toProtoBuilder.addStatement(
        "val protoValue = %T.newBuilder()",
        element.getBackBoneProtoClass()
      ) to toHapiBuilder.addStatement("val hapiValue = %T()", element.getBackBoneHapiClass())
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
      element.binding.extensionList.any {
        it.url.value == uriCommon && it.value.boolean.value
      }

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
          elementName
        )
        hapiBuilder.addStatement(
          "hapiValue$singleMethodTemplate(%T.valueOf(%L.value.name.replace(\"_\",\"\")))",
          elementName.capitalizeFirst(),
          element.getHapiCodeClass(isCommon),
          // TODO change this
          elementName
        )
      } else {
        // handle case when enum is repeated
        protoBuilder.addStatement(
          "$multipleMethodTemplate(%L.map{%T.newBuilder().setValue(%T.valueOf(it.value.toCode().replace(\"-\", \"_\").toUpperCase())).build()})",
          elementName.capitalizeFirst(),
          elementName,
          element.getProtoCodeClass(protoName),
          Class.forName(getEnumNameFromElement(element).reflectionName())
        )
        hapiBuilder.addStatement(
          "%L.map{hapiValue.add%L(%T.valueOf(it.value.name.replace(\"_\",\"\")))}",
          elementName + "List",
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
          elementName
        )
        hapiBuilder.addStatement(
          "hapiValue$singleMethodTemplate(%L.value)",
          elementName.capitalizeFirst(),
          elementName
        )
      } else {
        protoBuilder.addStatement(
          "$multipleMethodTemplate(%L.map{%T.newBuilder().setValue(it).build()})",
          elementName.capitalizeFirst(),
          elementName,
          element.getProtoCodeClass(protoName)
        )

        hapiBuilder.addStatement(
          "%L.map{hapiValue.add%L(it)}",
          elementName + "List",
          elementName.capitalizeFirst()
        )
      }
    }
  }

  private fun ElementDefinition.getBackBoneProtoClass(): ClassName {
    return ClassName(
        protoPackage,
        this.base.path.value.split(".").dropLast(1).map { it.capitalizeFirst() }
      )
      .nestedClass(getElementName(this).capitalizeFirst())
  }

  private fun ElementDefinition.getBackBoneHapiClass(): ClassName {
    return ClassName(hapiPackage, base.path.value.split(".").first().capitalizeFirst())
      .nestedClass(
        if (this.hasExtension(explicitTypeName)){
          "${getElementName(this).capitalizeFirst()}Component"
        } else {
          "${
            this.base.path.value.split(".").dropLast(1)
              .joinToString("") { it.capitalizeFirst() }
          }${getElementName(this).capitalizeFirst()}Component"
        })

  }

  private fun ElementDefinition.getProtoCodeClass(outerDataTypeName: kotlin.String): ClassName {
    return ClassName(
        protoPackage,
        listOf(outerDataTypeName) +
          this.path.value.split(".").drop(1).dropLast(1).map { it.capitalizeFirst() }
      )
      .nestedClass(getElementName(this).capitalizeFirst() + "Code")
  }

  private fun ElementDefinition.getHapiCodeClass(isCommon: Boolean): ClassName {
    return (if (isCommon) commonEnumClass
      else ClassName(hapiPackage, base.path.value.split(".").first().capitalizeFirst()))
      .nestedClass(binding.extensionList[0].value.stringValue.value)
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

  private fun ElementDefinition.hasExtension(uri: kotlin.String) : Boolean {
    return extensionList.any{it.url.value ==  uri}
  }
  private fun ElementDefinition.getExtension(uri : kotlin.String) : Extension {
    return extensionList.single { it.url.value == uri }
  }
  private fun getHapiProtoConverterFuncPair(
    funcName: kotlin.String,
    protoClass: ClassName,
    hapiClass: ClassName
  ): Pair<FunSpec.Builder, FunSpec.Builder> {

    return FunSpec.builder("${funcName}ToProto".lowerCaseFirst())
      .receiver(hapiClass)
      .returns(protoClass) to
      FunSpec.builder("${funcName}ToHapi".lowerCaseFirst()).receiver(protoClass).returns(hapiClass)
  }

  private fun CharSequence.lowerCaseFirst() =
    (if (this[0].isUpperCase()) this[0] + 32 else this[0]).toChar().toString() + this.drop(1)

  private fun CharSequence.capitalizeFirst() =
    (if (this[0].isLowerCase()) this[0] - 32 else this[0]).toChar().toString() + this.drop(1)

  private const val choiceTypeSuffixStructureDefinition = "[x]"
  private const val choiceTypeSuffixProto = "X"
}

// Ignore code after this
fun main() {
  File("C:\\Users\\Aditya\\Desktop\\fhir-spec\\site").listFiles()!!
    .filter {
      it.name.startsWith("valueset-") &&
        !it.name.startsWith("valueset-extensions-") &&
        !it.name.equals("valueset-questionnaire.canonical.json") &&
        !it.name.equals("valueset-questionnaire.json") &&
        it.name.endsWith(".json")
    }
    .forEach {
      try {
        val def = JsonFormat.getParser().merge(it.inputStream().reader(), ValueSet.newBuilder())
        CompositeCodegen.valueSetUrlMap[def.url.value] = def.build()
      } catch (e: Exception) {
        println(it.name)
      }
    }

  File("hapiprotoconverter\\sampledata\\").listFiles()!!
    .filter { it.name.endsWith(".profile.json") && !it.name.endsWith("-genetics.profile.json") }
    .forEach {
      val def =
        JsonFormat.getParser().merge(it.inputStream().reader(), StructureDefinition.newBuilder())
      CompositeCodegen.profileUrlMap[def.url.value] = def.build()
    }
  CompositeCodegen.profileUrlMap.values  //.filter { it.name.value == "Extension"}
    .forEach { def ->
    if ((  // def.kind.value == StructureDefinitionKindCode.Value.COMPLEX_TYPE
//              ||
      def.kind.value == StructureDefinitionKindCode.Value.RESOURCE
              )
      && !def.abstract.value
    // &&
    // def.status.value == PublicationStatusCode.Value.ACTIVE
    ) {
      try {
        CompositeCodegen.generate(def, File("hapiprotoconverter\\src\\main\\java"))
      } catch (e: Exception) {
        println(def.name.value)
      }
    }
  }
}

fun main1() {
  File("C:\\Users\\Aditya\\Desktop\\fhir-spec\\site").listFiles()!!
    .filter { !it.name.endsWith(".json") }
    .forEach { it.delete() }
}

// Types that don't or rather work aren't generated
// Attachment - mimeType
// ElementDefinition - mimeType
// Expression - ExpressionLanguage
// Signature - mimeType
// TODO handle conversion simpleQuantity to Quantity



// profiles that cannot be generated ( apart from -genetics.json profiles)
// Profile for Catalog
// CDS Hooks GuidanceResponse
// CDS Hooks Service PlanDefinition
// Clinical Document
// TODO - Composition
// CQF-Questionnaire
// DataElement constraint on ElementDefinition data type
// Family member history for genetics analysis
// Profile for HLA Genotyping Results
// TODO MessageDefinition - Done
