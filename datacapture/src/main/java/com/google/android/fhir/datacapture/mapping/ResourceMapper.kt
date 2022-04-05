/*
 * Copyright 2021 Google LLC
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

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport
import com.google.android.fhir.datacapture.DataCapture
import com.google.android.fhir.datacapture.createQuestionnaireResponseItem
import com.google.android.fhir.datacapture.targetStructureMap
import com.google.android.fhir.datacapture.utilities.toCodeType
import com.google.android.fhir.datacapture.utilities.toCoding
import com.google.android.fhir.datacapture.utilities.toIdType
import com.google.android.fhir.datacapture.utilities.toUriType
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.util.Locale
import org.hl7.fhir.r4.context.IWorkerContext
import org.hl7.fhir.r4.hapi.ctx.HapiWorkerContext
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Enumeration
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Parameters
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UriType
import org.hl7.fhir.r4.utils.FHIRPathEngine
import org.hl7.fhir.r4.utils.StructureMapUtilities

/**
 * Maps [QuestionnaireResponse] s to FHIR resources and vice versa.
 *
 * The process of converting [QuestionnaireResponse] s to other FHIR resources is called
 * [extraction](http://build.fhir.org/ig/HL7/sdc/extraction.html). The reverse process of converting
 * existing FHIR resources to [QuestionnaireResponse] s to be used to pre-fill the UI is called
 * [population](http://build.fhir.org/ig/HL7/sdc/populate.html).
 *
 * [Definition-based extraction](http://build.fhir.org/ig/HL7/sdc/extraction.html#definition-based-extraction)
 * and
 * [expression-based population](http://build.fhir.org/ig/HL7/sdc/populate.html#expression-based-population)
 * are used because these approaches are generic enough to work with any FHIR resource types, and at
 * the same time relatively easy to implement.
 *
 * WARNING: This is not production-ready.
 */
object ResourceMapper {

  private val fhirPathEngine: FHIRPathEngine =
    with(FhirContext.forCached(FhirVersionEnum.R4)) {
      FHIRPathEngine(HapiWorkerContext(this, DefaultProfileValidationSupport(this)))
    }

  /**
   * Extract a FHIR resource from the [questionnaire] and [questionnaireResponse].
   *
   * This method supports both Definition-based and StructureMap-based extraction.
   *
   * StructureMap-based extraction will be invoked if the [Questionnaire] declares a
   * targetStructureMap extension otherwise Definition-based extraction is used. StructureMap-based
   * extraction will fail and an empty [Bundle] will be returned if the [structureMapProvider] is
   * not passed.
   *
   * @param [structureMapProvider] The [IWorkerContext] may be used along with
   * [StructureMapUtilities] to parse the script and convert it into [StructureMap].
   *
   * @return [Bundle] containing the extracted [Resource]s or empty Bundle if the extraction fails.
   * An exception might also be thrown in a few cases
   */
  suspend fun extract(
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse,
    structureMapExtractionContext: StructureMapExtractionContext? = null
  ): Bundle {
    return if (questionnaire.targetStructureMap == null)
      extractByDefinition(questionnaire, questionnaireResponse)
    else if (structureMapExtractionContext != null) {
      extractByStructureMap(questionnaire, questionnaireResponse, structureMapExtractionContext)
    } else {
      Bundle()
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
    questionnaireResponse: QuestionnaireResponse
  ): Bundle {
    val rootResource: Resource? = questionnaire.createResource()
    val extractedResources = mutableListOf<Resource>()

    extractByDefinition(
      questionnaire.item,
      questionnaireResponse.item,
      rootResource,
      extractedResources
    )

    if (rootResource != null) {
      extractedResources += rootResource!!
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
    val structureMapProvider = structureMapExtractionContext.structureMapProvider ?: return Bundle()
    val simpleWorkerContext =
      DataCapture.getConfiguration(structureMapExtractionContext.context)
        .simpleWorkerContext
        .apply { setExpansionProfile(Parameters()) }
    val structureMap =
      structureMapProvider.let { it(questionnaire.targetStructureMap!!, simpleWorkerContext) }

    return Bundle().apply {
      StructureMapUtilities(
          simpleWorkerContext,
          structureMapExtractionContext.transformSupportServices
        )
        .transform(simpleWorkerContext, questionnaireResponse, structureMap, this)
    }
  }

  /**
   * Returns a `QuestionnaireResponse` to the [questionnaire] that is pre-filled from the
   * [resources] See http://build.fhir.org/ig/HL7/sdc/populate.html#expression-based-population.
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
    if (questionnaireItem.type != Questionnaire.QuestionnaireItemType.GROUP) {
      questionnaireItem.setInitialValueFromInitialExpression(resources = resources.asList())
    }

    populateInitialValues(questionnaireItem.item, *resources)
  }

  private fun getAnswers(
    contextResource: Resource?,
    resources: List<Resource>,
    expression: Expression
  ): MutableList<Base> {
    var extractedAnswers = mutableListOf<Base>()
    if (contextResource == null) {
      if (resources.isNotEmpty()) {
        // Using the first provided resource as a base resource inorder to fetch answers from the
        // functions
        extractedAnswers =
          fhirPathEngine.evaluate(resources[0], expression.expression.removePrefix("%"))
      }
    } else {
      // Using the resource extracted from provided expression as a base resource inorder to fetch
      // answers from the functions
      extractedAnswers =
        fhirPathEngine.evaluate(contextResource, expression.expression.removePrefix("%"))
    }
    return extractedAnswers
  }

  private fun getContextResource(expression: Expression, resources: List<Resource>): Resource? {
    val resourceType = expression.expression.substringBefore(".").removePrefix("%")
    return resources.firstOrNull {
      it.resourceType.name.lowercase().equals(resourceType.lowercase())
    }
  }

  private val Questionnaire.QuestionnaireItemComponent.initialExpression: Expression?
    get() {
      return this.extension.firstOrNull { it.url == ITEM_INITIAL_EXPRESSION_URL }?.let {
        it.value as Expression
      }
    }

  // extension function for evaluating provided initial expression and using the result as an
  // initial value of the QuestionnaireItemComponent
  private fun Questionnaire.QuestionnaireItemComponent.setInitialValueFromInitialExpression(
    resources: List<Resource>
  ) {
    this.initialExpression?.let {
      val contextResource = getContextResource(expression = it, resources = resources)

      val extractedAnswers =
        getAnswers(contextResource = contextResource, resources = resources, expression = it)
      extractedAnswers.firstOrNull()?.let { answer ->
        // Resetting QuestionnaireItemInitialComponent value using provided initialExpression
        this.initial =
          mutableListOf(
            Questionnaire.QuestionnaireItemInitialComponent().setValue(answer.asExpectedType())
          )
      }
    }
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
    extractionResult: MutableList<Resource>
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
          extractionResult
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
    extractionResult: MutableList<Resource>
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
          questionnaireItem.itemExtractionContextNameToExpressionPair != null ->
            // Extract a new resource for a new item extraction context
            extractResourceByDefinition(
              questionnaireItem,
              questionnaireResponseItem,
              extractionResult
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
              extractionResult
            )
          }
          else ->
            // Continue to traverse the descendants of the group item
            extractByDefinition(
              questionnaireItem.item,
              questionnaireResponseItem.item,
              extractionContext,
              extractionResult
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
            extractionContext
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
    extractionResult: MutableList<Resource>
  ) {
    val resource = questionnaireItem.createResource() as Resource
    extractByDefinition(
      questionnaireItem.item,
      questionnaireResponseItem.item,
      resource,
      extractionResult
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
    extractionResult: MutableList<Resource>
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
      extractionResult
    )
  }

  /**
   * Updates the field in [base] defined by the definition in [questionnaireItem] with the primitive
   * type value extracted from [questionnaireResponseItem].
   */
  private fun extractPrimitiveTypeValueByDefinition(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent,
    base: Base
  ) {
    val fieldName = getFieldNameByDefinition(questionnaireItem.definition)
    val definitionField = base.javaClass.getFieldOrNull(fieldName) ?: return
    if (questionnaireResponseItem.answer.isEmpty()) return
    if (definitionField.nonParameterizedType.isEnum) {
      updateFieldWithEnum(base, definitionField, questionnaireResponseItem.answer.first().value)
    } else {
      updateField(base, definitionField, questionnaireResponseItem.answer)
    }
  }
}

/**
 * Retrieves details about the target field defined in
 * [org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent.definition] for easier searching
 * of the setter methods and converting the answer to the expected parameter type.
 */
private fun getFieldNameByDefinition(definition: String): String {
  val last = definition.substringAfterLast(".")
  check(!last.isNullOrEmpty()) { "Invalid field definition: $definition" }
  return last
}

/**
 * Invokes the function to add a new value in the repeated field in the `Base` object, e.g.,
 * `addName` function for `name`.
 */
private fun Base.addRepeatedFieldValue(fieldName: String) =
  javaClass.getMethod("add${fieldName.capitalize(Locale.ROOT)}").invoke(this) as Base

/**
 * Invokes the function to get a value in the choice of data type field in the `Base` object, e.g.,
 * `getValueQuantity` for `valueQuantity`.
 */
private fun Base.getChoiceFieldValue(fieldName: String) =
  javaClass.getMethod("get${fieldName.capitalize(Locale.ROOT)}").invoke(this) as Base

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

  base
    .javaClass
    .getMethod("set${field.name.capitalize(Locale.ROOT)}", field.nonParameterizedType)
    .invoke(base, fromCodeMethod.invoke(dataTypeClass, stringValue))
}

private fun updateField(
  base: Base,
  field: Field,
  answers: List<QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent>
) {
  val answersOfFieldType =
    answers.map { wrapAnswerInFieldType(it.value, field) }.toCollection(mutableListOf())

  try {
    updateFieldWithAnswer(base, field, answersOfFieldType.first())
  } catch (e: NoSuchMethodException) {
    // some set methods expect a list of objects
    updateListFieldWithAnswer(base, field, answersOfFieldType)
  }
}

private fun updateFieldWithAnswer(base: Base, field: Field, answerValue: Base) {
  base
    .javaClass
    .getMethod("set${field.name.capitalize(Locale.ROOT)}Element", field.type)
    .invoke(base, answerValue)
}

private fun updateListFieldWithAnswer(base: Base, field: Field, answerValue: List<Base>) {
  base
    .javaClass
    .getMethod("set${field.name.capitalize(Locale.ROOT)}", field.type)
    .invoke(base, if (field.isParameterized && field.isList) answerValue else answerValue.first())
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
  }
  return answer
}

const val ITEM_INITIAL_EXPRESSION_URL: String =
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
 * such extension exists in the questionnaire, or null otherwise.
 */
private fun Questionnaire.createResource(): Resource? =
  itemExtractionContextExtensionNameToExpressionPair?.let {
    Class.forName("org.hl7.fhir.r4.model.${it.second}").newInstance() as Resource
  }
    ?: null

/**
 * [Pair] of name and expression for the item extraction context extension if one and only one such
 * extension exists, or null otherwise.
 */
private val Questionnaire.itemExtractionContextExtensionNameToExpressionPair
  get() = this.extension.itemExtractionContextExtensionNameToExpressionPair

/**
 * Returns a newly created [Resource] from the item extraction context extension if one and only one
 * such extension exists in the questionnaire item, or null otherwise.
 */
private fun Questionnaire.QuestionnaireItemComponent.createResource(): Resource? =
  itemExtractionContextNameToExpressionPair?.let {
    Class.forName("org.hl7.fhir.r4.model.${it!!.second}").newInstance() as Resource
  }
    ?: null

/**
 * [Pair] of name and expression for the item extraction context extension if one and only one such
 * extension exists, or null otherwise.
 */
private val Questionnaire.QuestionnaireItemComponent.itemExtractionContextNameToExpressionPair:
  Pair<String, String>?
  get() = this.extension.itemExtractionContextExtensionNameToExpressionPair

/**
 * [Pair] of name and expression for the item extraction context extension if one and only one such
 * extension exists, or null otherwise.
 */
private val List<Extension>.itemExtractionContextExtensionNameToExpressionPair
  get() =
    this.singleOrNull { it.url == ITEM_CONTEXT_EXTENSION_URL }?.let {
      val expression = it.value as Expression
      expression.name to expression.expression
    }

/**
 * URL for the
 * [item extraction context extension](http://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-itemExtractionContext.html)
 */
private const val ITEM_CONTEXT_EXTENSION_URL: String =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemExtractionContext"
