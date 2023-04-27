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

package com.google.android.fhir.datacapture.validation

import android.content.Context
import com.google.android.fhir.datacapture.enablement.EnablementEvaluator
import com.google.android.fhir.datacapture.extensions.packRepeatedGroups
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Type

object QuestionnaireResponseValidator {

  /**
   * Validates [QuestionnaireResponse] using the constraints defined in the [Questionnaire].
   * - Each item in the [QuestionnaireResponse] must have a corresponding item in the
   * [Questionnaire] with the same `linkId` and `type`
   * - The order of items in the [QuestionnaireResponse] must be the same as the order of the items
   * in the [Questionnaire]
   * -
   * [Items nested under group](http://www.hl7.org/fhir/questionnaireresponse-definitions.html#QuestionnaireResponse.item.item)
   * and
   * [items nested under answer](http://www.hl7.org/fhir/questionnaireresponse-definitions.html#QuestionnaireResponse.item.answer.item)
   * should follow the same rules recursively
   *
   * Note that although all the items in the [Questionnaire] SHOULD be included in the
   * [QuestionnaireResponse], we do not throw an exception for missing items. This allows the
   * [QuestionnaireResponse] to not include items that are not enabled due to `enableWhen`.
   *
   * @throws IllegalArgumentException if `questionnaireResponse` does not match `questionnaire`'s
   * URL (if specified)
   * @throws IllegalArgumentException if there is no questionnaire item with the same `linkId` as a
   * questionnaire response item
   * @throws IllegalArgumentException if the questionnaire response items are out of order
   * @throws IllegalArgumentException if multiple answers are provided for a non-repeat
   * questionnaire item
   *
   * See http://www.hl7.org/fhir/questionnaireresponse.html#link for more information.
   *
   * @return a map[linkIdToValidationResultMap] of linkIds to list of ValidationResult
   */
  fun validateQuestionnaireResponse(
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse,
    context: Context
  ): Map<String, List<ValidationResult>> {
    require(
      questionnaireResponse.questionnaire == null ||
        questionnaire.url == questionnaireResponse.questionnaire
    ) {
      "Mismatching Questionnaire ${questionnaire.url} and QuestionnaireResponse (for Questionnaire ${questionnaireResponse.questionnaire})"
    }

    val linkIdToValidationResultMap = mutableMapOf<String, MutableList<ValidationResult>>()

    validateQuestionnaireResponseItems(
      questionnaire.item,
      questionnaireResponse.item,
      context,
      EnablementEvaluator(questionnaireResponse),
      linkIdToValidationResultMap,
    )

    return linkIdToValidationResultMap
  }

  private fun validateQuestionnaireResponseItems(
    questionnaireItemList: List<Questionnaire.QuestionnaireItemComponent>,
    questionnaireResponseItemList: List<QuestionnaireResponse.QuestionnaireResponseItemComponent>,
    context: Context,
    enablementEvaluator: EnablementEvaluator,
    linkIdToValidationResultMap: MutableMap<String, MutableList<ValidationResult>>,
  ): Map<String, List<ValidationResult>> {
    val questionnaireItemListIterator = questionnaireItemList.iterator()
    val questionnaireResponseItemListIterator = questionnaireResponseItemList.iterator()

    while (questionnaireResponseItemListIterator.hasNext()) {
      val questionnaireResponseItem = questionnaireResponseItemListIterator.next()
      var questionnaireItem: Questionnaire.QuestionnaireItemComponent?
      do {
        require(questionnaireItemListIterator.hasNext()) {
          "Missing questionnaire item for questionnaire response item ${questionnaireResponseItem.linkId}"
        }
        questionnaireItem = questionnaireItemListIterator.next()
      } while (questionnaireItem!!.linkId != questionnaireResponseItem.linkId)

      val enabled = enablementEvaluator.evaluate(questionnaireItem, questionnaireResponseItem)

      if (enabled) {
        validateQuestionnaireResponseItem(
          questionnaireItem,
          questionnaireResponseItem,
          context,
          enablementEvaluator,
          linkIdToValidationResultMap,
        )
      }
    }
    return linkIdToValidationResultMap
  }

  private fun validateQuestionnaireResponseItem(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent,
    context: Context,
    enablementEvaluator: EnablementEvaluator,
    linkIdToValidationResultMap: MutableMap<String, MutableList<ValidationResult>>
  ): Map<String, List<ValidationResult>> {

    when (checkNotNull(questionnaireItem.type) { "Questionnaire item must have type" }) {
      Questionnaire.QuestionnaireItemType.DISPLAY,
      Questionnaire.QuestionnaireItemType.NULL -> Unit
      Questionnaire.QuestionnaireItemType.GROUP ->
        // Nested items under group
        // http://www.hl7.org/fhir/questionnaireresponse-definitions.html#QuestionnaireResponse.item.item
        validateQuestionnaireResponseItems(
          questionnaireItem.item,
          questionnaireResponseItem.item,
          context,
          enablementEvaluator,
          linkIdToValidationResultMap,
        )
      else -> {
        require(questionnaireItem.repeats || questionnaireResponseItem.answer.size <= 1) {
          "Multiple answers for non-repeat questionnaire item ${questionnaireItem.linkId}"
        }

        questionnaireResponseItem.answer.forEach {
          validateQuestionnaireResponseItems(
            questionnaireItem.item,
            it.item,
            context,
            enablementEvaluator,
            linkIdToValidationResultMap,
          )
        }

        linkIdToValidationResultMap[questionnaireItem.linkId] = mutableListOf()
        linkIdToValidationResultMap[questionnaireItem.linkId]?.add(
          QuestionnaireResponseItemValidator.validate(
            questionnaireItem,
            questionnaireResponseItem.answer,
            context
          )
        )
      }
    }
    return linkIdToValidationResultMap
  }

  /**
   * Checks that the [QuestionnaireResponse] is structurally consistent with the [Questionnaire].
   * - Each item in the [QuestionnaireResponse] must have a corresponding item in the
   * [Questionnaire] with the same `linkId` and `type`
   * - The order of items in the [QuestionnaireResponse] must be the same as the order of the items
   * in the [Questionnaire]
   * -
   * [Items nested under group](http://www.hl7.org/fhir/questionnaireresponse-definitions.html#QuestionnaireResponse.item.item)
   * and
   * [items nested under answer](http://www.hl7.org/fhir/questionnaireresponse-definitions.html#QuestionnaireResponse.item.answer.item)
   * should follow the same rules recursively
   *
   * Note that although all the items in the [Questionnaire] SHOULD be included in the
   * [QuestionnaireResponse], we do not throw an exception for missing items. This allows the
   * [QuestionnaireResponse] to not include items that are not enabled due to `enableWhen`.
   *
   * @throws IllegalArgumentException if `questionnaireResponse` does not match `questionnaire`'s
   * URL (if specified)
   * @throws IllegalArgumentException if there is no questionnaire item with the same `linkId` as a
   * questionnaire response item
   * @throws IllegalArgumentException if the questionnaire response items are out of order
   * @throws IllegalArgumentException if the type of a questionnaire response item does not match
   * that of the questionnaire item
   * @throws IllegalArgumentException if multiple answers are provided for a non-repeat
   * questionnaire item
   *
   * See http://www.hl7.org/fhir/questionnaireresponse.html#link for more information.
   */
  fun checkQuestionnaireResponse(
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse
  ) {
    require(
      questionnaireResponse.questionnaire == null ||
        questionnaire.url == questionnaireResponse.questionnaire
    ) {
      "Mismatching Questionnaire ${questionnaire.url} and QuestionnaireResponse (for Questionnaire ${questionnaireResponse.questionnaire})"
    }
    checkQuestionnaireResponseItems(
      questionnaire.item,
      questionnaireResponse.copy().apply { packRepeatedGroups() }.item
    )
  }

  private fun checkQuestionnaireResponseItems(
    questionnaireItemList: List<Questionnaire.QuestionnaireItemComponent>,
    questionnaireResponseItemList: List<QuestionnaireResponse.QuestionnaireResponseItemComponent>
  ) {
    val questionnaireItemIterator = questionnaireItemList.iterator()
    val questionnaireResponseInputItemIterator = questionnaireResponseItemList.iterator()

    while (questionnaireResponseInputItemIterator.hasNext()) {
      val questionnaireResponseItem = questionnaireResponseInputItemIterator.next()
      var questionnaireItem: Questionnaire.QuestionnaireItemComponent?
      do {
        require(questionnaireItemIterator.hasNext()) {
          "Missing questionnaire item for questionnaire response item ${questionnaireResponseItem.linkId}"
        }
        questionnaireItem = questionnaireItemIterator.next()
      } while (questionnaireItem!!.linkId != questionnaireResponseItem.linkId)

      checkQuestionnaireResponseItem(questionnaireItem, questionnaireResponseItem)
    }
  }

  private fun checkQuestionnaireResponseItem(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    questionnaireResponseItem: QuestionnaireResponse.QuestionnaireResponseItemComponent,
  ) {
    when (checkNotNull(questionnaireItem.type) { "Questionnaire item must have type" }) {
      Questionnaire.QuestionnaireItemType.DISPLAY,
      Questionnaire.QuestionnaireItemType.NULL -> Unit
      Questionnaire.QuestionnaireItemType.GROUP ->
        // Nested items under group
        // http://www.hl7.org/fhir/questionnaireresponse-definitions.html#QuestionnaireResponse.item.item
        checkQuestionnaireResponseItems(questionnaireItem.item, questionnaireResponseItem.item)
      else -> {
        require(questionnaireItem.repeats || questionnaireResponseItem.answer.size <= 1) {
          "Multiple answers for non-repeat questionnaire item ${questionnaireItem.linkId}"
        }
        questionnaireResponseItem.answer.forEach {
          checkQuestionnaireResponseAnswerItem(questionnaireItem, it)
        }
      }
    }
  }

  private fun checkQuestionnaireResponseAnswerItem(
    questionnaireItem: Questionnaire.QuestionnaireItemComponent,
    answerItem: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent
  ) {
    if (answerItem.hasValue()) {
      checkQuestionnaireResponseAnswerItemType(
        questionnaireItem.linkId,
        questionnaireItem.type,
        answerItem.value
      )
    }
    // Nested items under answer
    // http://www.hl7.org/fhir/questionnaireresponse-definitions.html#QuestionnaireResponse.item.answer.item
    checkQuestionnaireResponseItems(questionnaireItem.item, answerItem.item)
  }

  private fun checkQuestionnaireResponseAnswerItemType(
    linkId: String,
    questionnaireItemType: Questionnaire.QuestionnaireItemType,
    value: Type
  ) {
    val answerType = value.fhirType()
    when (questionnaireItemType) {
      Questionnaire.QuestionnaireItemType.BOOLEAN ->
        require(answerType == "boolean") {
          "Mismatching question type $questionnaireItemType and answer type $answerType for $linkId"
        }
      Questionnaire.QuestionnaireItemType.DECIMAL ->
        require(answerType == "decimal") {
          "Mismatching question type $questionnaireItemType and answer type $answerType for $linkId"
        }
      Questionnaire.QuestionnaireItemType.INTEGER ->
        require(answerType == "integer") {
          "Mismatching question type $questionnaireItemType and answer type $answerType for $linkId"
        }
      Questionnaire.QuestionnaireItemType.DATE ->
        require(answerType == "date") {
          "Mismatching question type $questionnaireItemType and answer type $answerType for $linkId"
        }
      Questionnaire.QuestionnaireItemType.DATETIME ->
        require(answerType == "dateTime") {
          "Mismatching question type $questionnaireItemType and answer type $answerType for $linkId"
        }
      Questionnaire.QuestionnaireItemType.TIME ->
        require(answerType == "time") {
          "Mismatching question type $questionnaireItemType and answer type $answerType for $linkId"
        }
      Questionnaire.QuestionnaireItemType.STRING ->
        require(answerType == "string") {
          "Mismatching question type $questionnaireItemType and answer type $answerType for $linkId"
        }
      Questionnaire.QuestionnaireItemType.TEXT ->
        require(answerType == "string") {
          "Mismatching question type $questionnaireItemType and answer type $answerType for $linkId"
        }
      Questionnaire.QuestionnaireItemType.URL ->
        require(answerType == "url") {
          "Mismatching question type $questionnaireItemType and answer type $answerType for $linkId"
        }
      Questionnaire.QuestionnaireItemType.CHOICE,
      Questionnaire.QuestionnaireItemType.OPENCHOICE ->
        require(answerType == "Coding" || answerType == "string") {
          "Mismatching question type $questionnaireItemType and answer type $answerType for $linkId"
        }
      Questionnaire.QuestionnaireItemType.ATTACHMENT ->
        require(answerType == "Attachment") {
          "Mismatching question type $questionnaireItemType and answer type $answerType for $linkId"
        }
      Questionnaire.QuestionnaireItemType.REFERENCE ->
        require(answerType == "Reference") {
          "Mismatching question type $questionnaireItemType and answer type $answerType for $linkId"
        }
      Questionnaire.QuestionnaireItemType.QUANTITY ->
        require(answerType == "Quantity") {
          "Mismatching question type $questionnaireItemType and answer type $answerType for $linkId"
        }
      else -> Unit
    }
  }
}
