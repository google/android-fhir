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

import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.StringType
import org.hl7.fhir.r4.model.Type

object ResourceMapperHAPI {

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
        val innerClass =
          Class.forName(
            "org.hl7.fhir.r4.model.${questionnaireItem.itemContextNameToExpressionMap2.values.first()}"
          )
        var type: Type = innerClass.newInstance() as Type

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

  private fun createInnerClassObject(
    type: Type,
    questionnaireItemComponent: MutableList<Questionnaire.QuestionnaireItemComponent>,
    questionnaireResponseItemComponent:
      MutableList<QuestionnaireResponse.QuestionnaireResponseItemComponent>
  ) {
    val questionnaireItemListIterator2 = questionnaireItemComponent.iterator()
    val questionnaireResponseItemListIterator2 = questionnaireResponseItemComponent.iterator()

    while (questionnaireItemListIterator2.hasNext() &&
      questionnaireResponseItemListIterator2.hasNext()) {
      val questionnaireItem2 = questionnaireItemListIterator2.next()
      val questionnaireResponseItem2 = questionnaireResponseItemListIterator2.next()

      val targetFieldName2 = questionnaireItem2.definition.substringAfterLast(".")

      try {
        questionnaireItem2.type.getClass()?.let {
          type
            .javaClass
            .getMethod("set${targetFieldName2.capitalize()}Element", it)
            .invoke(type, questionnaireResponseItem2.answer.first().value)
        }
      } catch (e: NoSuchMethodException) {
        questionnaireItem2.type.getClass()?.let {
          type
            .javaClass
            .getMethod("set${targetFieldName2.capitalize()}", List::class.java)
            .invoke(type, listOf(questionnaireResponseItem2.answer.first().value))
        }
      }
    }
  }
}

private fun Questionnaire.QuestionnaireItemType.getClass() =
  when (this) {
    Questionnaire.QuestionnaireItemType.DATE -> DateType::class.java
    Questionnaire.QuestionnaireItemType.BOOLEAN -> BooleanType::class.java
    Questionnaire.QuestionnaireItemType.STRING, Questionnaire.QuestionnaireItemType.TEXT ->
      StringType::class.java
    else -> null
  }

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

private val Questionnaire.QuestionnaireItemComponent.itemContextNameToExpressionMap2:
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

private const val ITEM_CONTEXT_EXTENSION_URL: String =
  "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-itemContext"
