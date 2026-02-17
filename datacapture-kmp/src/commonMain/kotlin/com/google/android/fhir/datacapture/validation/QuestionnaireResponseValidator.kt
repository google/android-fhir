/*
 * Copyright 2022-2026 Google LLC
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

import com.google.android.fhir.datacapture.XFhirQueryResolver
import com.google.android.fhir.datacapture.enablement.EnablementEvaluator
import com.google.android.fhir.datacapture.extensions.packRepeatedGroups
import com.google.android.fhir.datacapture.fhirpath.ExpressionEvaluator
import com.google.fhir.model.r4.Questionnaire
import com.google.fhir.model.r4.QuestionnaireResponse
import com.google.fhir.model.r4.Resource

object QuestionnaireResponseValidator {

  /**
   * Validates [QuestionnaireResponse] using the constraints defined in the [Questionnaire].
   * - Each item in the [QuestionnaireResponse] must have a corresponding item in the
   *   [Questionnaire] with the same `linkId` and `type`
   * - The order of items in the [QuestionnaireResponse] must be the same as the order of the items
   *   in the
   *   [Questionnaire] - [Items nested under group](http://www.hl7.org/fhir/questionnaireresponse-definitions.html#QuestionnaireResponse.item.item)
   *   and
   *   [items nested under answer](http://www.hl7.org/fhir/questionnaireresponse-definitions.html#QuestionnaireResponse.item.answer.item)
   *   should follow the same rules recursively
   *
   * Note that although all the items in the [Questionnaire] SHOULD be included in the
   * [QuestionnaireResponse], we do not throw an exception for missing items. This allows the
   * [QuestionnaireResponse] to not include items that are not enabled due to `enableWhen`.
   *
   * @return a map of linkIds to list of ValidationResult
   * @throws IllegalArgumentException if `questionnaireResponse` does not match `questionnaire`'s
   *   URL (if specified)
   * @throws IllegalArgumentException if there is no questionnaire item with the same `linkId` as a
   *   questionnaire response item
   * @throws IllegalArgumentException if the questionnaire response items are out of order
   * @throws IllegalArgumentException if multiple answers are provided for a non-repeat
   *   questionnaire item
   *
   * See http://www.hl7.org/fhir/questionnaireresponse.html#link for more information.
   */
  suspend fun validateQuestionnaireResponse(
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse,
    questionnaireItemParentMap: Map<Questionnaire.Item, Questionnaire.Item> = mapOf(),
    launchContextMap: Map<String, Resource>? = mapOf(),
    xFhirQueryResolver: XFhirQueryResolver? = null,
  ): Map<String, List<ValidationResult>> {
    require(
      questionnaireResponse.questionnaire?.value == null ||
        questionnaire.url?.value == questionnaireResponse.questionnaire?.value,
    ) {
      "Mismatching Questionnaire ${questionnaire.url?.value} and QuestionnaireResponse (for Questionnaire ${questionnaireResponse.questionnaire?.value})"
    }

    val enablementEvaluator =
      EnablementEvaluator(
        questionnaire,
        questionnaireResponse,
        questionnaireItemParentMap,
        launchContextMap,
        xFhirQueryResolver,
      )
    val questionnaireResponseItemValidator =
      QuestionnaireResponseItemValidator(
        ExpressionEvaluator(
          questionnaire,
          questionnaireResponse,
          questionnaireItemParentMap,
          launchContextMap,
        ),
      )
    val linkIdToValidationResultMap = mutableMapOf<String, MutableList<ValidationResult>>()

    validateQuestionnaireResponseItems(
      questionnaire.item,
      questionnaireResponse.item,
      enablementEvaluator,
      questionnaireResponseItemValidator,
      linkIdToValidationResultMap,
    )

    return linkIdToValidationResultMap
  }

  private suspend fun validateQuestionnaireResponseItems(
    questionnaireItemList: List<Questionnaire.Item>,
    questionnaireResponseItemList: List<QuestionnaireResponse.Item>,
    enablementEvaluator: EnablementEvaluator,
    questionnaireResponseItemValidator: QuestionnaireResponseItemValidator,
    linkIdToValidationResultMap: MutableMap<String, MutableList<ValidationResult>>,
  ): Map<String, List<ValidationResult>> {
    val questionnaireItemListIterator = questionnaireItemList.iterator()
    val questionnaireResponseItemListIterator = questionnaireResponseItemList.iterator()

    while (questionnaireResponseItemListIterator.hasNext()) {
      val questionnaireResponseItem = questionnaireResponseItemListIterator.next()
      var questionnaireItem: Questionnaire.Item?
      do {
        require(questionnaireItemListIterator.hasNext()) {
          "Missing questionnaire item for questionnaire response item${questionnaireResponseItem.linkId}"
        }
        questionnaireItem = questionnaireItemListIterator.next()
      } while (questionnaireItem.linkId != questionnaireResponseItem.linkId)

      val enabled =
        enablementEvaluator.evaluate(
          questionnaireItem,
          questionnaireResponseItem,
        )

      if (enabled) {
        validateQuestionnaireResponseItem(
          questionnaireItem,
          questionnaireResponseItem,
          enablementEvaluator,
          questionnaireResponseItemValidator,
          linkIdToValidationResultMap,
        )
      }
    }
    return linkIdToValidationResultMap
  }

  private suspend fun validateQuestionnaireResponseItem(
    questionnaireItem: Questionnaire.Item,
    questionnaireResponseItem: QuestionnaireResponse.Item,
    enablementEvaluator: EnablementEvaluator,
    questionnaireResponseItemValidator: QuestionnaireResponseItemValidator,
    linkIdToValidationResultMap: MutableMap<String, MutableList<ValidationResult>>,
  ): Map<String, List<ValidationResult>> {
    checkNotNull(questionnaireItem.type) { "Questionnaire item must have type" }
    when {
      questionnaireItem.type.value == Questionnaire.QuestionnaireItemType.Display -> Unit
      (questionnaireItem.type.value == Questionnaire.QuestionnaireItemType.Group &&
        questionnaireItem.repeats?.value == false) ->
        // Nested items under group
        // http://www.hl7.org/fhir/questionnaireresponse-definitions.html#QuestionnaireResponse.item.item
        validateQuestionnaireResponseItems(
          questionnaireItem.item,
          questionnaireResponseItem.item,
          enablementEvaluator,
          questionnaireResponseItemValidator,
          linkIdToValidationResultMap,
        )
      else -> {
        require(
          questionnaireItem.repeats?.value == true || questionnaireResponseItem.answer.size <= 1,
        ) {
          "Multiple answers for non-repeat questionnaire item ${questionnaireItem.linkId}"
        }

        questionnaireResponseItem.answer.forEach {
          validateQuestionnaireResponseItems(
            questionnaireItem.item,
            it.item,
            enablementEvaluator,
            questionnaireResponseItemValidator,
            linkIdToValidationResultMap,
          )
        }

        if (questionnaireItem.linkId.value != null) {
          linkIdToValidationResultMap[questionnaireItem.linkId.value!!] = mutableListOf()
          linkIdToValidationResultMap[questionnaireItem.linkId.value]?.add(
            questionnaireResponseItemValidator.validate(
              questionnaireItem,
              questionnaireResponseItem,
            ),
          )
        }
      }
    }
    return linkIdToValidationResultMap
  }

  /**
   * Checks that the [QuestionnaireResponse] is structurally consistent with the [Questionnaire].
   * - Each item in the [QuestionnaireResponse] must have a corresponding item in the
   *   [Questionnaire] with the same `linkId` and `type`
   * - The order of items in the [QuestionnaireResponse] must be the same as the order of the items
   *   in the
   *   [Questionnaire] - [Items nested under group](http://www.hl7.org/fhir/questionnaireresponse-definitions.html#QuestionnaireResponse.item.item)
   *   and
   *   [items nested under answer](http://www.hl7.org/fhir/questionnaireresponse-definitions.html#QuestionnaireResponse.item.answer.item)
   *   should follow the same rules recursively
   *
   * Note that although all the items in the [Questionnaire] SHOULD be included in the
   * [QuestionnaireResponse], we do not throw an exception for missing items. This allows the
   * [QuestionnaireResponse] to not include items that are not enabled due to `enableWhen`.
   *
   * @throws IllegalArgumentException if `questionnaireResponse` does not match `questionnaire`'s
   *   URL (if specified)
   * @throws IllegalArgumentException if there is no questionnaire item with the same `linkId` as a
   *   questionnaire response item
   * @throws IllegalArgumentException if the questionnaire response items are out of order
   * @throws IllegalArgumentException if the type of a questionnaire response item does not match
   *   that of the questionnaire item
   * @throws IllegalArgumentException if multiple answers are provided for a non-repeat
   *   questionnaire item
   *
   * See http://www.hl7.org/fhir/questionnaireresponse.html#link for more information.
   */
  fun checkQuestionnaireResponse(
    questionnaire: Questionnaire,
    questionnaireResponse: QuestionnaireResponse,
  ) {
    require(
      questionnaireResponse.questionnaire?.value == null ||
        questionnaire.url?.value == questionnaireResponse.questionnaire?.value,
    ) {
      "Mismatching Questionnaire ${questionnaire.url?.value} and QuestionnaireResponse (for Questionnaire ${questionnaireResponse.questionnaire?.value})"
    }
    checkQuestionnaireResponseItems(
      questionnaire.item,
      questionnaireResponse.toBuilder().apply { packRepeatedGroups(questionnaire) }.build().item,
    )
  }

  private fun checkQuestionnaireResponseItems(
    questionnaireItemList: List<Questionnaire.Item>,
    questionnaireResponseItemList: List<QuestionnaireResponse.Item>,
  ) {
    val questionnaireItemIterator = questionnaireItemList.iterator()
    val questionnaireResponseInputItemIterator = questionnaireResponseItemList.iterator()

    while (questionnaireResponseInputItemIterator.hasNext()) {
      val questionnaireResponseItem = questionnaireResponseInputItemIterator.next()
      var questionnaireItem: Questionnaire.Item?
      do {
        require(questionnaireItemIterator.hasNext()) {
          "Missing questionnaire item for questionnaire response item ${questionnaireResponseItem.linkId}"
        }
        questionnaireItem = questionnaireItemIterator.next()
      } while (questionnaireItem.linkId != questionnaireResponseItem.linkId)

      checkQuestionnaireResponseItem(questionnaireItem, questionnaireResponseItem)
    }
  }

  private fun checkQuestionnaireResponseItem(
    questionnaireItem: Questionnaire.Item,
    questionnaireResponseItem: QuestionnaireResponse.Item,
  ) {
    checkNotNull(questionnaireItem.type) { "Questionnaire item must have type" }

    when {
      questionnaireItem.type.value == Questionnaire.QuestionnaireItemType.Display -> Unit
      (questionnaireItem.type.value == Questionnaire.QuestionnaireItemType.Group &&
        questionnaireItem.repeats?.value == false) ->
        // Nested items under group
        // http://www.hl7.org/fhir/questionnaireresponse-definitions.html#QuestionnaireResponse.item.item
        checkQuestionnaireResponseItems(questionnaireItem.item, questionnaireResponseItem.item)
      else -> {
        require(
          questionnaireItem.repeats?.value == true || questionnaireResponseItem.answer.size <= 1,
        ) {
          "Multiple answers for non-repeat questionnaire item ${questionnaireItem.linkId}"
        }
        questionnaireResponseItem.answer.forEach {
          checkQuestionnaireResponseAnswerItem(questionnaireItem, it)
        }
      }
    }
  }

  private fun checkQuestionnaireResponseAnswerItem(
    questionnaireItem: Questionnaire.Item,
    answerItem: QuestionnaireResponse.Item.Answer,
  ) {
    if (answerItem.value != null) {
      checkQuestionnaireResponseAnswerItemType(
        questionnaireItem.linkId.value,
        questionnaireItem.type.value,
        answerItem.value,
      )
    }
    // Nested items under answer
    // http://www.hl7.org/fhir/questionnaireresponse-definitions.html#QuestionnaireResponse.item.answer.item
    checkQuestionnaireResponseItems(questionnaireItem.item, answerItem.item)
  }

  private fun checkQuestionnaireResponseAnswerItemType(
    linkId: String?,
    questionnaireItemType: Questionnaire.QuestionnaireItemType?,
    value: QuestionnaireResponse.Item.Answer.Value?,
  ) {
    if (value == null) return
    val answerType = value::class.simpleName
    when (questionnaireItemType) {
      Questionnaire.QuestionnaireItemType.Boolean ->
        require(answerType == "Boolean") {
          "Mismatching question type $questionnaireItemType and answer type $answerType for $linkId"
        }
      Questionnaire.QuestionnaireItemType.Decimal ->
        require(answerType == "Decimal") {
          "Mismatching question type $questionnaireItemType and answer type $answerType for $linkId"
        }
      Questionnaire.QuestionnaireItemType.Integer ->
        require(answerType == "Integer") {
          "Mismatching question type $questionnaireItemType and answer type $answerType for $linkId"
        }
      Questionnaire.QuestionnaireItemType.Date ->
        require(answerType == "Date") {
          "Mismatching question type $questionnaireItemType and answer type $answerType for $linkId"
        }
      Questionnaire.QuestionnaireItemType.DateTime ->
        require(answerType == "DateTime") {
          "Mismatching question type $questionnaireItemType and answer type $answerType for $linkId"
        }
      Questionnaire.QuestionnaireItemType.Time ->
        require(answerType == "Time") {
          "Mismatching question type $questionnaireItemType and answer type $answerType for $linkId"
        }
      Questionnaire.QuestionnaireItemType.String ->
        require(answerType == "String") {
          "Mismatching question type $questionnaireItemType and answer type $answerType for $linkId"
        }
      Questionnaire.QuestionnaireItemType.Text ->
        require(answerType == "String") {
          "Mismatching question type $questionnaireItemType and answer type $answerType for $linkId"
        }
      Questionnaire.QuestionnaireItemType.Url ->
        require(answerType == "Url") {
          "Mismatching question type $questionnaireItemType and answer type $answerType for $linkId"
        }
      Questionnaire.QuestionnaireItemType.Choice,
      Questionnaire.QuestionnaireItemType.Open_Choice, ->
        require(answerType == "Coding" || answerType == "String") {
          "Mismatching question type $questionnaireItemType and answer type $answerType for $linkId"
        }
      Questionnaire.QuestionnaireItemType.Attachment ->
        require(answerType == "Attachment") {
          "Mismatching question type $questionnaireItemType and answer type $answerType for $linkId"
        }
      Questionnaire.QuestionnaireItemType.Reference ->
        require(answerType == "Reference") {
          "Mismatching question type $questionnaireItemType and answer type $answerType for $linkId"
        }
      Questionnaire.QuestionnaireItemType.Quantity ->
        require(answerType == "Quantity") {
          "Mismatching question type $questionnaireItemType and answer type $answerType for $linkId"
        }
      else -> Unit
    }
  }
}
