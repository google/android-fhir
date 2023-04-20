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

package com.google.android.fhir.datacapture.mapping

import com.google.android.fhir.datacapture.DataCapture
import com.google.android.fhir.datacapture.extensions.createQuestionnaireResponseItem
import com.google.android.fhir.datacapture.extensions.targetStructureMap
import com.google.android.fhir.datacapture.extensions.toCodeType
import com.google.android.fhir.datacapture.extensions.toCoding
import com.google.android.fhir.datacapture.extensions.toIdType
import com.google.android.fhir.datacapture.extensions.toUriType
import com.google.android.fhir.datacapture.fhirpath.fhirPathEngine
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.util.Locale
import org.hl7.fhir.r4.context.IWorkerContext
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.DomainResource
import org.hl7.fhir.r4.model.Enumeration
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Parameters
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.StructureDefinition
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UriType
import org.hl7.fhir.r4.utils.StructureMapUtilities
import timber.log.Timber

/**
 * Maps a [QuestionnaireResponse] to FHIR resources and vice versa.
 *
 * The process of converting [QuestionnaireResponse] s to other FHIR resources is called
 * [extraction](http://build.fhir.org/ig/HL7/sdc/extraction.html). The reverse process of converting
 * existing FHIR resources to [QuestionnaireResponse] s to be used to pre-fill the UI is called
 * [population](http://build.fhir.org/ig/HL7/sdc/populate.html).
 *
 * This class supports
 * [Definition-based extraction](http://build.fhir.org/ig/HL7/sdc/extraction.html#definition-based-extraction)
 * ,
 * [StructureMap-based extraction](http://hl7.org/fhir/uv/sdc/extraction.html#structuremap-based-extraction)
 * , and
 * [expression-based population](http://build.fhir.org/ig/HL7/sdc/populate.html#expression-based-population)
 * .
 *
 * See the [developer guide](https://github.com/google/android-fhir/wiki/SDCL%3A-Use-ResourceMapper)
 * for more information.
 */
object ResourceMapper {

  /**
   * Extract FHIR resources from a [questionnaire] and [questionnaireResponse].
   *
   * This method will perform
   * [StructureMap-based extraction](http://hl7.org/fhir/uv/sdc/extraction.html#structuremap-based-extraction)
   * if the [Questionnaire] specified by [questionnaire] includes a `targetStructureMap` extension.
   * In this case [structureMapExtractionContext] is required; extraction will fail and an empty
   * [Bundle] is returned if [structureMapExtractionContext] is not provided.
   *
   * Otherwise, this method will perform
   * [Definition-based extraction](http://hl7.org/fhir/uv/sdc/extraction.html#definition-based-extraction)
   * .
   *
   * @param questionnaire A [Questionnaire] with data extraction extensions.
   * @param questionnaireResponse A [QuestionnaireResponse] with answers for [questionnaire].
   * @param structureMapExtractionContext The [IWorkerContext] may be used along with
   * [StructureMapUtilities] to parse the script and convert it into [StructureMap].
   *
   * @return [Bundle] containing the extracted [Resource]s or empty Bundle if the extraction fails.
   * An exception might also be thrown in a few cases
   *
   * @throws IllegalArgumentException when Resource getting extracted does conform different profile
   * than standard FHIR profile and argument loadProfile callback Implementation is not provided to
   * load different profile
   */
  suspend fun extract(
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse,
    structureMapExtractionContext: StructureMapExtractionContext? = null,
    profileLoader: ProfileLoader? = null
  ): Bundle {

    return when {
      questionnaire.targetStructureMap == null ->
        extractByDefinition(
          questionnaire,
          questionnaireResponse,
          object : ProfileLoader {
            // Mutable map of key-canonical url as string for profile and
            // value-StructureDefinition of resource claims to conforms to.
            val structureDefinitionMap: MutableMap<String, StructureDefinition?> = hashMapOf()

            override fun loadProfile(url: CanonicalType): StructureDefinition? {
              if (profileLoader == null) {
                Timber.w(
                  "ProfileLoader implementation required to load StructureDefinition that this resource claims to conform to"
                )
                return null
              }

              structureDefinitionMap[url.toString()]?.also {
                return it
              }

              return profileLoader.loadProfile(url).also {
                structureDefinitionMap[url.toString()] = it
              }
            }
          }
        )
      structureMapExtractionContext != null -> {
        extractByStructureMap(questionnaire, questionnaireResponse, structureMapExtractionContext)
      }
      else -> {
        Bundle()
      }
    }
  }

  /**
   * Extracts FHIR resources from [questionnaireResponse] (as response to [questionnaire]) using
   * Definition-based extraction.
   *
   * See http://build.fhir.org/ig/HL7/sdc/extraction.html#definition-based-extraction for more on
   * Definition-based extraction.
   */
  private fun extractByDefinition(
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse,
    profileLoader: ProfileLoader
  ): Bundle {
    val rootResource: Resource? = questionnaire.createResource()
    val extractedResources = mutableListOf<Resource>()

    extractByDefinition(
      questionnaire.item,
      questionnaireResponse.item,
      rootResource,
      extractedResources,
      profileLoader
    )

    if (rootResource != null) {
      extractedResources += rootResource
    }

    return Bundle().apply {
      type = Bundle.BundleType.TRANSACTION
      entry = extractedResources.map { Bundle.BundleEntryComponent().apply { resource = it } }
    }
  }

  /**
   * Extracts FHIR resources from [questionnaireResponse] (as response to [questionnaire]) using
   * StructureMap-based extraction.
   *
   * @param structureMapProvider provides the referenced [StructureMap] either from persistence or a
   * remote service.
   *
   * @return a [Bundle] including the extraction results, or `null` if [structureMapProvider] is
   * missing.
   *
   * See http://build.fhir.org/ig/HL7/sdc/extraction.html#structuremap-based-extraction for more on
   * StructureMap-based extraction.
   */
  private suspend fun extractByStructureMap(
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse,
    structureMapExtractionContext: StructureMapExtractionContext
  ): Bundle {
    val structureMapProvider = structureMapExtractionContext.structureMapProvider
    val simpleWorkerContext =
      DataCapture.getConfiguration(structureMapExtractionContext.context)
        .simpleWorkerContext.apply { setExpansionProfile(Parameters()) }
    val structureMap = structureMapProvider(questionnaire.targetStructureMap!!, simpleWorkerContext)

    return Bundle().apply {
      StructureMapUtilities(
          simpleWorkerContext,
          structureMapExtractionContext.transformSupportServices
        )
        .transform(simpleWorkerContext, questionnaireResponse, structureMap, this)
    }
  }

  /**
   * Performs
   * [Expression-based population](http://build.fhir.org/ig/HL7/sdc/populate.html#expression-based-population)
   * and returns a [QuestionnaireResponse] for the [questionnaire] that is populated from the
   * [resources].
   */
  suspend fun populate(
    questionnaire: Questionnaire,
    vararg resources: Resource
  ): QuestionnaireResponse {
    populateInitialValues(questionnaire.item, *resources)
    return QuestionnaireResponse().apply {
      item = questionnaire.item.map { it.createQuestionnaireResponseItem() }
    }
  }

  private suspend fun populateInitialValues(
    questionnaireItems: List<Questionnaire.QuestionnaireItemComponent>,
    vararg resources: Resource
  ) {
    questionnaireItems.forEach { populateInitialValue(it, *resources) }
  }

  private suspend fun populateInitialValue(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    vararg resources: Resource
  ) {
    check(questionnaireItem.initial.isEmpty() || questionnaireItem.initialExpression == null) {
      "QuestionnaireItem item is not allowed to have both initial.value and initial expression. See rule at http://build.fhir.org/ig/HL7/sdc/expressions.html#initialExpression."
    }

    questionnaireItem.initialExpression
      ?.let {
        fhirPathEngine
          .evaluate(
            selectPopulationContext(resources.asList(), it),
            it.expression.removePrefix("%")
          )
          .singleOrNull()
      }
      ?.let {
        // Set initial value for the questionnaire item. Questionnaire items should not have both
        // initial value and initial expression.
        questionnaireItem.initial =
          mutableListOf(
            Questionnaire.QuestionnaireItemInitialComponent().setValue(it.asExpectedType())
          )
      }

    populateInitialValues(questionnaireItem.item, *resources)
  }

  /**
   * Returns the population context for the questionnaire/group.
   *
   * The resource of the same type as the expected type of the initial expression will be selected
   * first. Otherwise, the first resource in the list will be selected.
   *
   * TODO: rewrite this using the launch context and population context.
   */
  private fun selectPopulationContext(
    resources: List<Resource>,
    initialExpression: Expression
  ): Resource? {
    val resourceType = initialExpression.expression.substringBefore(".").removePrefix("%")
    return resources.singleOrNull { it.resourceType.name.lowercase() == resourceType.lowercase() }
      ?: resources.firstOrNull()
  }

  private val Questionnaire.QuestionnaireItemComponent.initialExpression: Expression?
    get() {
      return this.extension
        .firstOrNull { it.url == ITEM_INITIAL_EXPRESSION_URL }
        ?.let { it.value as Expression }
    }

  /**
   * Updates corresponding fields in [extractionContext] with answers in
   * [questionnaireResponseItemList]. The fields are defined in the definitions in
   * [questionnaireItemList].
   *
   * `extractionContext` can be a resource (e.g. Patient) or a complex value (e.g. HumanName).
   *
   * Handles nested questionnaire items recursively. New extraction contexts may be defined in the
   * recursion.
   */
  private fun extractByDefinition(
    questionnaireItemList: List<Questionnaire.QuestionnaireItemComponent>,
    questionnaireResponseItemList: List<QuestionnaireResponse.QuestionnaireResponseItemComponent>,
    extractionContext: Base?,
    extractionResult: MutableList<Resource>,
    profileLoader: ProfileLoader
  ) {
    val questionnaireItemListIterator = questionnaireItemList.iterator()
    val questionnaireResponseItemListIterator = questionnaireResponseItemList.iterator()
    while (questionnaireItemListIterator.hasNext() &&
      questionnaireResponseItemListIterator.hasNext()) {
      val currentQuestionnaireResponseItem = questionnaireResponseItemListIterator.next()
      var currentQuestionnaireItem = questionnaireItemListIterator.next()
      // Find the next questionnaire item with the same link ID. This is necessary because some
      // questionnaire items that are disabled might not have corresponding questionnaire response
      // items.
      while (questionnaireItemListIterator.hasNext() &&
        currentQuestionnaireItem.linkId != currentQuestionnaireResponseItem.linkId) {
        currentQuestionnaireItem = questionnaireItemListIterator.next()
      }
      if (currentQuestionnaireItem.linkId == currentQuestionnaireResponseItem.linkId) {
        extractByDefinition(
          currentQuestionnaireItem,
          currentQuestionnaireResponseItem,
          extractionContext,
          extractionResult,
          profileLoader
        )
      }
    }
  }

  /**
   * Updates corresponding field in [extractionContext] with answer in [questionnaireResponseItem].
   * The field is defined in the definition in [questionnaireItem].
   *
   * `extractionContext` can be a resource (e.g. Patient) or a complex value (e.g. HumanName).
   *
   * Handles nested questionnaire items recursively. New extraction contexts may be defined in the
   * recursion.
   */
  private fun extractByDefinition(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent,
    extractionContext: Base?,
    extractionResult: MutableList<Resource>,
    profileLoader: ProfileLoader
  ) {
    when (questionnaireItem.type) {
      Questionnaire.QuestionnaireItemType.GROUP ->
        // A group in a questionnaire can do one of the following three things:
        // 1) define a new resource (e.g. Patient) to extract using item extraction context
        // extension
        // 2) define a new complex value (e.g. HumanName) to extract using the `definition` field
        // (see http://www.hl7.org/fhir/datatypes.html#complex)
        // 3) simply group questions (e.g. for display reasons) without altering the extraction
        // semantics
        when {
          questionnaireItem.extension.itemExtractionContextExtensionValue != null ->
            // Extract a new resource for a new item extraction context
            extractResourceByDefinition(
              questionnaireItem,
              questionnaireResponseItem,
              extractionResult,
              profileLoader
            )
          questionnaireItem.definition != null -> {
            // Extract a new element (which is not a resource) e.g. HumanName, Quantity, etc
            check(extractionContext != null) {
              "No extraction context defined for ${questionnaireItem.definition}"
            }
            extractComplexTypeValueByDefinition(
              questionnaireItem,
              questionnaireResponseItem,
              extractionContext,
              extractionResult,
              profileLoader
            )
          }
          else ->
            // Continue to traverse the descendants of the group item
            extractByDefinition(
              questionnaireItem.item,
              questionnaireResponseItem.item,
              extractionContext,
              extractionResult,
              profileLoader
            )
        }
      else ->
        if (questionnaireItem.definition != null) {
          // Extract a new primitive value (see http://www.hl7.org/fhir/datatypes.html#primitive)
          check(extractionContext != null) {
            "No extraction context defined for ${questionnaireItem.definition}"
          }
          extractPrimitiveTypeValueByDefinition(
            questionnaireItem,
            questionnaireResponseItem,
            extractionContext,
            profileLoader
          )
        }
    }
  }

  /**
   * Creates a new resource using the item extraction context extension on the [questionnaireItem],
   * populates it with the answers nested in [questionnaireResponseItem], and append it to
   * [extractionResult].
   */
  private fun extractResourceByDefinition(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent,
    extractionResult: MutableList<Resource>,
    profileLoader: ProfileLoader
  ) {
    val resource = questionnaireItem.createResource() as Resource
    extractByDefinition(
      questionnaireItem.item,
      questionnaireResponseItem.item,
      resource,
      extractionResult,
      profileLoader
    )
    extractionResult += resource
  }

  /**
   * Creates a complex type value using the definition in [questionnaireItem], populate it with
   * answers nested in [questionnaireResponseItem], and update the corresponding field in [base]
   * with it.
   */
  private fun extractComplexTypeValueByDefinition(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent,
    base: Base,
    extractionResult: MutableList<Resource>,
    profileLoader: ProfileLoader
  ) {
    val fieldName = getFieldNameByDefinition(questionnaireItem.definition)
    val value =
      try {
        // Add a value in a repeated field if it can be done, e.g., by calling `Patient#addName`
        base.addRepeatedFieldValue(fieldName)
      } catch (e: NoSuchMethodException) {
        // If the above attempt to add a value in a repeated field failed, try to get a value in the
        // choice of data type field, e.g., by calling `Observation#getValueQuantity`
        base.getChoiceFieldValue(fieldName)
      }
    extractByDefinition(
      questionnaireItem.item,
      questionnaireResponseItem.item,
      value,
      extractionResult,
      profileLoader
    )
  }

  /**
   * Updates the field in [base] defined by the definition in [questionnaireItem] with the primitive
   * type value extracted from [questionnaireResponseItem].
   */
  private fun extractPrimitiveTypeValueByDefinition(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent,
    base: Base,
    profileLoader: ProfileLoader
  ) {
    if (questionnaireResponseItem.answer.isEmpty()) return

    // Set the primitive type value if the field exists
    val fieldName = getFieldNameByDefinition(questionnaireItem.definition)
    base.javaClass.getFieldOrNull(fieldName)?.let { field ->
      if (field.nonParameterizedType.isEnum) {
        updateFieldWithEnum(base, field, questionnaireResponseItem.answer.first().value)
      } else {
        updateField(base, field, questionnaireResponseItem.answer)
      }
      return
    }

    // If the primitive type value isn't a field, try to use the `setValue` method to set the
    // answer, e.g., `Observation#setValue`. We set the answer component of the questionnaire
    // response item directly as the value (e.g `StringType`).
    try {
      base.javaClass
        .getMethod("setValue", Type::class.java)
        .invoke(base, questionnaireResponseItem.answer.singleOrNull()?.value)
      return
    } catch (e: NoSuchMethodException) {
      // Do nothing
    }

    // Handle fields not present in base Java class but defined as extensions in profile e.g.
    // "http://fhir.org/guides/who/anc-cds/StructureDefinition/anc-patient#Patient.address.address-preferred"
    if (base.javaClass.getFieldOrNull(fieldName) == null) {
      // base url from definition
      val canonicalUrl =
        questionnaireItem.definition.substring(0, questionnaireItem.definition.lastIndexOf("#"))
      profileLoader.loadProfile(CanonicalType(canonicalUrl))?.let {
        // Example "definition":
        // "http://fhir.org/guides/who/anc-cds/StructureDefinition/anc-patient#Patient.address.address-preferred"
        //  extensionForType is "Patient.address"
        val extensionForType =
          questionnaireItem.definition.substring(
            questionnaireItem.definition.lastIndexOf("#") + 1,
            questionnaireItem.definition.lastIndexOf(".")
          )
        if (isExtensionSupportedByProfile(
            structureDefinition = it,
            extensionForType = extensionForType,
            fieldName = fieldName
          )
        ) {
          addDefinitionBasedCustomExtension(questionnaireItem, questionnaireResponseItem, base)
          return
        } else {
          Timber.w(
            "Extension for field '$fieldName' is not defined in StructureDefinition of ${base.fhirType()}, so field is ignored"
          )
        }
      }
    }
  }
}

private fun isExtensionSupportedByProfile(
  structureDefinition: StructureDefinition,
  extensionForType: String,
  fieldName: String
): Boolean {
  // Partial ElementDefinition from StructureDefinition to check extension is
  //  "id": "Patient.address.extension:address-preferred",
  //  "path": "Patient.address.extension",
  val listOfElementDefinition =
    structureDefinition.snapshot.element.filter { it.path.equals("$extensionForType.extension") }
  listOfElementDefinition.forEach {
    if (it.id.substringAfterLast(":").equals(fieldName)) {
      return true
    }
  }
  return false
}

/**
 * Adds custom extension for Resource.
 * @param questionnaireItem QuestionnaireItemComponent with details for extension
 * @param questionnaireResponseItem QuestionnaireResponseItemComponent for response value
 * @param base
 * - resource's Base class instance See
 * https://hapifhir.io/hapi-fhir/docs/model/profiles_and_extensions.html#extensions for more on
 * custom extensions
 */
private fun addDefinitionBasedCustomExtension(
  questionnaireItem: Questionnaire.QuestionnaireItemComponent,
  questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent,
  base: Base
) {
  if (base is Type) {
    // Create an extension
    val ext = Extension()
    ext.url = questionnaireItem.definition
    ext.setValue(questionnaireResponseItem.answer.first().value)
    // Add the extension to the resource
    base.addExtension(ext)
  }
  if (base is DomainResource) {
    // Create an extension
    val ext = Extension()
    ext.url = questionnaireItem.definition
    ext.setValue(questionnaireResponseItem.answer.first().value)
    // Add the extension to the resource
    base.addExtension(ext)
  }
}

/**
 * Retrieves details about the target field defined in
 * [org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent.definition] for easier searching
 * of the setter methods and converting the answer to the expected parameter type.
 */
private fun getFieldNameByDefinition(definition: String): String {
  val last = definition.substringAfterLast(".")
  check(last.isNotEmpty()) { "Invalid field definition: $definition" }
  return last
}

/**
 * Invokes the function to add a new value in the repeated field in the `Base` object, e.g.,
 * `addName` function for `name`.
 */
private fun Base.addRepeatedFieldValue(fieldName: String) =
  javaClass.getMethod("add${fieldName.capitalize()}").invoke(this) as Base

/**
 * Invokes the function to get a value in the choice of data type field in the `Base` object, e.g.,
 * `getValueQuantity` for `valueQuantity`.
 */
private fun Base.getChoiceFieldValue(fieldName: String) =
  javaClass.getMethod("get${fieldName.capitalize()}").invoke(this) as Base

/**
 * Updates a field of name [field.name] on this object with the generated enum from [value] using
 * the declared setter. [field] helps to determine the field class type. The enum is generated by
 * calling fromCode method on the enum class
 */
private fun updateFieldWithEnum(base: Base, field: Field, value: Base) {
  /**
   * We have a org.hl7.fhir.r4.model.Enumerations class which contains inner classes of code-names
   * and re-implements the classes in the org.hl7.fhir.r4.model.codesystems package The
   * inner-classes in the Enumerations package are valid and not dependent on the classes in the
   * codesystems package All enum classes in the org.hl7.fhir.r4.model package implement the
   * fromCode(), toCode() methods among others
   */
  val dataTypeClass: Class<*> = field.nonParameterizedType
  val fromCodeMethod: Method = dataTypeClass.getDeclaredMethod("fromCode", String::class.java)

  val stringValue = if (value is Coding) value.code else value.toString()

  base.javaClass
    .getMethod("set${field.name.capitalize()}", field.nonParameterizedType)
    .invoke(base, fromCodeMethod.invoke(dataTypeClass, stringValue))
}

/**
 * The api's used to updateField with answers are:
 *
 * * For Parameterized list of primitive type e.g HumanName.given of type List<StringType>
 * ```
 *     addGiven(String) - adds a new StringType to the list.
 * ```
 * * For any primitive value e.g for Patient.active which is of BooleanType
 * ```
 *     setActiveElement(BooleanType)
 * ```
 * * In case they fail,
 * ```
 *     setName(List<HumanName>) - replaces old list if any with the new list.
 * ```
 */
private fun updateField(
  base: Base,
  field: Field,
  answers: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>
) {
  val answersOfFieldType =
    answers.map { wrapAnswerInFieldType(it.value, field) }.toCollection(mutableListOf())

  try {
    if (field.isParameterized && field.isList) {
      addAnswerToListField(base, field, answersOfFieldType)
    } else {
      setFieldElementValue(base, field, answersOfFieldType.first())
    }
  } catch (e: NoSuchMethodException) {
    // some set methods expect a list of objects
    updateListFieldWithAnswer(base, field, answersOfFieldType)
  }
}

private fun setFieldElementValue(base: Base, field: Field, answerValue: Base) {
  base.javaClass
    .getMethod("set${field.name.capitalize()}Element", field.type)
    .invoke(base, answerValue)
}

private fun addAnswerToListField(base: Base, field: Field, answerValue: List<Base>) {
  base.javaClass
    .getMethod(
      "add${field.name.replaceFirstChar(Char::uppercase)}",
      answerValue.first().fhirType().replaceFirstChar(Char::uppercase).javaClass
    )
    .let { method -> answerValue.forEach { method.invoke(base, it.primitiveValue()) } }
}

private fun updateListFieldWithAnswer(base: Base, field: Field, answerValue: List<Base>) {
  base.javaClass
    .getMethod("set${field.name.capitalize()}", field.type)
    .invoke(base, if (field.isParameterized && field.isList) answerValue else answerValue.first())
}

private fun String.capitalize() = replaceFirstChar {
  if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
}

/**
 * This method enables us to perform an extra step to wrap the answer using the correct type. This
 * is useful in cases where a single question maps to a CompositeType such as [CodeableConcept] or
 * enum. Normally, composite types are mapped using group questions which provide direct alignment
 * to the type elements in the group questions.
 */
private fun wrapAnswerInFieldType(answer: Base, fieldType: Field): Base {
  when (fieldType.nonParameterizedType) {
    CodeableConcept::class.java -> {
      if (answer is Coding) {
        return CodeableConcept(answer).apply { text = answer.display }
      }
    }
    IdType::class.java -> {
      if (answer is StringType) {
        return answer.toIdType()
      }
    }
    CodeType::class.java -> {
      if (answer is Coding) {
        return answer.toCodeType()
      } else if (answer is StringType) {
        return answer.toCodeType()
      }
    }
    UriType::class.java -> {
      if (answer is StringType) {
        return answer.toUriType()
      }
    }
    DecimalType::class.java -> {
      if (answer is IntegerType) {
        return DecimalType(answer.asStringValue())
      }
    }
  }
  return answer
}

internal const val ITEM_INITIAL_EXPRESSION_URL: String =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-initialExpression"

private val Field.isList: Boolean
  get() = isParameterized && type == List::class.java

private val Field.isParameterized: Boolean
  get() = genericType is ParameterizedType

/** The non-parameterized type of this field (e.g. `String` for a field of type `List<String>`). */
private val Field.nonParameterizedType: Class<*>
  get() =
    if (isParameterized) (genericType as ParameterizedType).actualTypeArguments[0] as Class<*>
    else type

private fun Class<*>.getFieldOrNull(name: String): Field? {
  return try {
    getDeclaredField(name)
  } catch (ex: NoSuchFieldException) {
    superclass?.getFieldOrNull(name)
  }
}

/**
 * Returns a newly created [Resource] from the item extraction context extension if one and only one
 * such extension exists in the questionnaire, or null otherwise.
 */
private fun Questionnaire.createResource(): Resource? =
  this.extension.itemExtractionContextExtensionValue?.let {
    Class.forName("org.hl7.fhir.r4.model.$it").newInstance() as Resource
  }

/**
 * Returns the [Base] object as a [Type] as expected by
 * [Questionnaire.QuestionnaireItemAnswerOptionComponent.setValue]. Also,
 * [Questionnaire.QuestionnaireItemAnswerOptionComponent.setValue] only takes a certain [Type]
 * objects and throws exception otherwise. This extension function takes care of the conversion
 * based on the input and expected [Type].
 */
private fun Base.asExpectedType(): Type {
  return when (this) {
    is Enumeration<*> -> toCoding()
    is IdType -> StringType(idPart)
    else -> this as Type
  }
}

/**
 * Returns a newly created [Resource] from the item extraction context extension if one and only one
 * such extension exists in the questionnaire item, or null otherwise.
 */
private fun Questionnaire.QuestionnaireItemComponent.createResource(): Resource? =
  this.extension.itemExtractionContextExtensionValue?.let {
    Class.forName("org.hl7.fhir.r4.model.$it").newInstance() as Resource
  }

/**
 * The item extraction context extension value of type Expression or CodeType if one and only one
 * such extension exists or null otherwise. If there are multiple extensions exists, it will be
 * ignored. See
 * http://hl7.org/fhir/uv/sdc/STU3/StructureDefinition-sdc-questionnaire-itemExtractionContext.html
 */
private val List<Extension>.itemExtractionContextExtensionValue
  get() =
    this.singleOrNull { it.url == ITEM_CONTEXT_EXTENSION_URL }
      ?.let {
        when (it.value) {
          is Expression -> {
            // TODO update the existing resource
            val expression = it.value as Expression
            expression.expression
          }
          is CodeType -> {
            val code = it.value as CodeType
            code.value
          }
          else -> null
        }
      }

/**
 * URL for the
 * [item extraction context extension](http://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-itemExtractionContext.html)
 */
private const val ITEM_CONTEXT_EXTENSION_URL: String =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemExtractionContext"
