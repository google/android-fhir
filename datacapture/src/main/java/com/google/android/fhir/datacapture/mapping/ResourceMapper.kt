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

import android.text.TextUtils
import java.lang.reflect.Method
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
import java.lang.reflect.ParameterizedType

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
    val resource = Class.forName("org.hl7.fhir.r4.model.$className").newInstance() as Resource

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
        // create a class for questionnaire item of type group and add to the resource
        val innerClass: Class<*> =
          Class.forName(
            "${questionnaireItem.inferPropertyResourceClass}"
          )
        val type: Type = innerClass.newInstance() as Type

        createInnerClassObject(type, questionnaireItem.item, questionnaireResponseItem.item)

        resource
          .javaClass
          .getMethod("add${targetFieldName.capitalize()}", innerClass)
          .invoke(resource, type)
        continue
      }

      // get answer from questionnaireResponse or from initial value in questionnaire
      val ans = extractQuestionAnswer(questionnaireResponseItem, questionnaireItem) ?: continue

      val itemComponentClassNameToExpressionMap =
        questionnaireItem
          .itemComponentContextNameToExpressionMap // gets item type e.g. AdministrativeGender for
      // gender
      if (itemComponentClassNameToExpressionMap.isEmpty()) {
        // this is a low level type e.g. StringType
        questionnaireItem.type.getClass()?.let {
          resource
            .javaClass
            .getMethod("set${targetFieldName.capitalize()}Element", it)
            .invoke(resource, ans)
        }
      } else {
        // this is a high level type e.g. AdministrativeGender
        val dataTypeClass: Class<*> =
          Class.forName(
            "org.hl7.fhir.r4.model." + itemComponentClassNameToExpressionMap.values.first()
          )
        val fromCodeMethod: Method = dataTypeClass.getDeclaredMethod("fromCode", String::class.java)

        resource
          .javaClass
          .getMethod(
            "set${targetFieldName.capitalize()}",
            Class.forName(
              "org.hl7.fhir.r4.model." + itemComponentClassNameToExpressionMap.values.first()
            )
          )
          .invoke(resource, fromCodeMethod.invoke(dataTypeClass, ans.toString()))
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

    // get answer from questionnaireResponse or from initial value in questionnaire
    val answer = extractQuestionAnswer(questionnaireResponseItem, questionnaireItem) ?: continue

    val itemComponentClassNameToExpressionMap =
      questionnaireItem.itemComponentContextNameToExpressionMap
    if (itemComponentClassNameToExpressionMap.isEmpty()) {
      // call the set methods by providing the low level data types: StringType, DateType etc
      try {
        questionnaireItem.type.getClass()?.let {
          type
            .javaClass
            .getMethod("set${targetFieldName.capitalize()}Element", it)
            .invoke(type, answer)
        }
      } catch (e: NoSuchMethodException) {
        // some set methods expect a list of objects
        questionnaireItem.type.getClass()?.let {
          type
            .javaClass
            .getMethod("set${targetFieldName.capitalize()}", List::class.java)
            .invoke(type, listOf(answer))
        }
      }
    } else {
      // call the set methods by providing data type defined defined for the field e.g.
      // ContactPointSystem
      val dataTypeClass: Class<*> =
        Class.forName(
          "org.hl7.fhir.r4.model." + itemComponentClassNameToExpressionMap.values.first()
        )
      val fromCodeMethod: Method = dataTypeClass.getDeclaredMethod("fromCode", String::class.java)

      type
        .javaClass
        .getMethod(
          "set${targetFieldName.capitalize()}",
          Class.forName(
            "org.hl7.fhir.r4.model." + itemComponentClassNameToExpressionMap.values.first()
          )
        )
        .invoke(type, fromCodeMethod.invoke(dataTypeClass, answer.toString()))
    }
  }
}

private fun extractQuestionAnswer(
  questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent,
  questionnaireItem: Questionnaire.QuestionnaireItemComponent
) =
  if (!questionnaireResponseItem.answer.isEmpty()) questionnaireResponseItem.answer.first().value
  else if (!questionnaireItem.initial.isEmpty()) questionnaireItem.initial.first().value else null

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

private val Questionnaire.QuestionnaireItemComponent.inferPropertyResourceClass: String? get() {
  val pathParts = this.definition.split("#")
  if (pathParts.size >= 2) {
    val modelAndField = pathParts[1].split(".")
    if (modelAndField.size >= 2 && !TextUtils.isEmpty(modelAndField[0]) && !TextUtils.isEmpty(modelAndField[1])) {
      val declaredFields = Class.forName("org.hl7.fhir.r4.model.${modelAndField.get(0)}")
              .declaredFields

      for (declaredField in declaredFields)
        if (declaredField.name.equals(modelAndField[1])) return (declaredField.genericType as ParameterizedType).actualTypeArguments[0].typeName
    }
  }

  return null
}

/**
 * See
 * [Extension: item extraction context](http://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-itemExtractionContext.html)
 * .
 */
private const val ITEM_CONTEXT_EXTENSION_URL: String =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemContext"
