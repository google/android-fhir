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

package com.google.android.fhir.datacapture.mapping

import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.TimeType
import org.hl7.fhir.r4.model.Type
import org.hl7.fhir.r4.model.UrlType

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
internal object ResourceMapper {

  /**
   * Extract a FHIR resource from the `questionnaire` and `questionnaireResponse`.
   *
   * This method assumes there is only one FHIR resource to be extracted from the given
   * `questionnaire` and `questionnaireResponse`.
   */
  fun extract(
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse
  ): Resource {
    val className = questionnaire.itemContextNameToExpressionMap.values.first()
    val resource =
      Class.forName("com.google.android.fhir.datacapture.model.$className").newInstance() as
        Resource

    var questionnaireItemListIterator = questionnaire.item.iterator()
    var questionnaireResponseItemListIterator = questionnaireResponse.item.iterator()
    while (questionnaireItemListIterator.hasNext() &&
      questionnaireResponseItemListIterator.hasNext()) {
      val questionnaireItem = questionnaireItemListIterator.next()
      val questionnaireResponseItem = questionnaireResponseItemListIterator.next()

      if (questionnaireItem.definition == null) {
        questionnaireItemListIterator = questionnaireItem.item.iterator()
        questionnaireResponseItemListIterator = questionnaireResponseItem.item.iterator()
        continue
      }

      val targetFieldName = questionnaireItem.definition.substringAfterLast(".")
      if (targetFieldName.isEmpty()) {
        questionnaireItemListIterator = questionnaireItem.item.iterator()
        questionnaireResponseItemListIterator = questionnaireResponseItem.item.iterator()
        continue
      }

      if (questionnaireItem.type == Questionnaire.QuestionnaireItemType.GROUP) {
        var innerClass: Class<*>
        try {
          innerClass =
            Class.forName(
              "com.google.android.fhir.datacapture.model.${questionnaireItem.itemComponentContextNameToExpressionMap.values.first()}"
            )
        } catch (e: ClassNotFoundException) {
          innerClass =
            Class.forName(
              "org.hl7.fhir.r4.model.${questionnaireItem.itemComponentContextNameToExpressionMap.values.first()}"
            )
        }

        val type: Type = innerClass.newInstance() as Type

        createInnerClassObject(type, questionnaireItem.item, questionnaireResponseItem.item)

        resource
          .javaClass
          .getMethod("add${targetFieldName.capitalize()}", innerClass)
          .invoke(resource, type)
        continue
      }

      questionnaireItem.type.getClass()?.let {
        resource
          .javaClass
          .getMethod("set${targetFieldName.capitalize()}Element", it)
          .invoke(resource, questionnaireResponseItem.answer.first().value)
      }
    }

    return resource
  }
}

/**
 * Creates an object of Type and Extracts values for fields from the corresponding questions and
 * answers in [questionnaireItemList] and [questionnaireResponseItemList] and invokes setters on the
 * object.
 */
private fun createInnerClassObject(
  type: Type,
  questionnaireItemComponent: MutableList<Questionnaire.QuestionnaireItemComponent>,
  questionnaireResponseItemComponent:
    MutableList<QuestionnaireResponse.QuestionnaireResponseItemComponent>
) {
  val questionnaireItemListIterator = questionnaireItemComponent.iterator()
  val questionnaireResponseItemListIterator = questionnaireResponseItemComponent.iterator()

  while (questionnaireItemListIterator.hasNext() &&
    questionnaireResponseItemListIterator.hasNext()) {
    val questionnaireItem = questionnaireItemListIterator.next()
    val questionnaireResponseItem = questionnaireResponseItemListIterator.next()

    val targetFieldName = questionnaireItem.definition.substringAfterLast(".")

    try {
      questionnaireItem.type.getClass()?.let {
        type
          .javaClass
          .getMethod("set${targetFieldName.capitalize()}Element", it)
          .invoke(type, questionnaireResponseItem.answer.first().value)
      }
    } catch (e: NoSuchMethodException) {
      questionnaireItem.type.getClass()?.let {
        type
          .javaClass
          .getMethod("set${targetFieldName.capitalize()}", List::class.java)
          .invoke(type, listOf(questionnaireResponseItem.answer.first().value))
      }
    } catch (e: NoSuchElementException) {
      questionnaireItem.type.getClass()?.let {
        type
          .javaClass
          .getMethod("set${targetFieldName.capitalize()}Element", it)
          .invoke(type, questionnaireItem.initial.first().value)
      }
    }
  }
}

/**
 * Returns the field name for the [Questionnaire.Item]'s definition.
 *
 * For example, if the definition URI is
 * `"http://hl7.org/fhir/StructureDefinition/Patient#Patient.birthDate"`, this function will return
 * `"birthDate"`.
 */
private val Questionnaire.QuestionnaireItemComponent.definitionFieldName
  get() = this.definition?.substringAfterLast(".")

/**
 * Returns the [Class] for the answer to the [Questionnaire.QuestionnaireItemComponent].
 *
 * Used to retrieve the method to invoke to set the field in the extracted FHIR resource.
 */
private fun Questionnaire.QuestionnaireItemType.getClass(): Class<out Base>? =
  when (this) {
    Questionnaire.QuestionnaireItemType.DATE -> DateType::class.java
    Questionnaire.QuestionnaireItemType.BOOLEAN -> BooleanType::class.java
    Questionnaire.QuestionnaireItemType.DECIMAL -> DecimalType::class.java
    Questionnaire.QuestionnaireItemType.INTEGER -> IntegerType::class.java
    Questionnaire.QuestionnaireItemType.DATETIME -> DateTimeType::class.java
    Questionnaire.QuestionnaireItemType.TIME -> TimeType::class.java
    Questionnaire.QuestionnaireItemType.STRING, Questionnaire.QuestionnaireItemType.TEXT ->
      StringType::class.java
    Questionnaire.QuestionnaireItemType.URL -> UrlType::class.java
    else -> null
  }

/**
 * The map from the `name`s to `expression`s in the
 * [item extraction context extension](http://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-itemExtractionContext.html)
 * s.
 */
private val Questionnaire.itemContextNameToExpressionMap: Map<String, String>
  get() {
    return this.extension
      .filter { it.url == ITEM_CONTEXT_EXTENSION_URL }
      .map {
        val expression = it.value as Expression
        expression.name to expression.expression
      }
      .toMap()
  }

/** Extract Type for a group questionnaire item from the extension field */
private val Questionnaire.QuestionnaireItemComponent.itemComponentContextNameToExpressionMap:
  Map<String, String>
  get() {
    return this.extension
      .filter { it.url == ITEM_CONTEXT_EXTENSION_URL }
      .map {
        val expression = it.value as Expression
        expression.name to expression.expression
      }
      .toMap()
  }

/**
 * See
 * [Extension: item extraction context](http://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-itemExtractionContext.html)
 * .
 */
private const val ITEM_CONTEXT_EXTENSION_URL: String =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemContext"
