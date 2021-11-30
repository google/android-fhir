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

package com.google.android.fhir.datacapture.validation

import android.content.Context
import com.google.android.fhir.datacapture.hasNestedItemsWithinAnswers
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

object QuestionnaireResponseValidator {

  /** Maps linkId to [ValidationResult]. */
  private val linkIdToValidationResultMap = mutableMapOf<String, MutableList<ValidationResult>>()

  /**
   * Validates [questionnaireResponseItemList] using the constraints defined in the
   * [questionnaireItemList].
   */
  fun validate(
    questionnaireItemList: List<Questionnaire.QuestionnaireItemComponent>,
    questionnaireResponseItemList: List<QuestionnaireResponse.QuestionnaireResponseItemComponent>,
    context: Context
  ): Map<String, List<ValidationResult>> {
    /* TODO create an iterator for questionnaire item + questionnaire response item refer to the
    questionnaire view model */
    val questionnaireItemListIterator = questionnaireItemList.iterator()
    val questionnaireResponseItemListIterator = questionnaireResponseItemList.iterator()
    while (questionnaireItemListIterator.hasNext() &&
      questionnaireResponseItemListIterator.hasNext()) {
      val questionnaireItem = questionnaireItemListIterator.next()
      val questionnaireResponseItem = questionnaireResponseItemListIterator.next()
      linkIdToValidationResultMap[questionnaireItem.linkId] = mutableListOf()
      linkIdToValidationResultMap[questionnaireItem.linkId]?.add(
        QuestionnaireResponseItemValidator.validate(
          questionnaireItem,
          questionnaireResponseItem,
          context
        )
      )
      if (questionnaireItem.hasNestedItemsWithinAnswers) {
        // TODO(https://github.com/google/android-fhir/issues/487): Validates all answers.
        validate(questionnaireItem.item, questionnaireResponseItem.answer[0].item, context)
      }
      validate(questionnaireItem.item, questionnaireResponseItem.item, context)
    }
    return linkIdToValidationResultMap
  }

  /**
   * Traverse (DFS) through the list of questionnaire items and the list of questionnaire response
   * items and check if the linkId of the matching pairs of questionnaire item and questionnaire
   * response item are equal.
   */
  fun validateQuestionnaireResponseItems(
    questionnaireResponseInputItemList:
      List<QuestionnaireResponse.QuestionnaireResponseItemComponent>,
    questionnaireItemList: List<Questionnaire.QuestionnaireItemComponent>
  ) {
    val questionnaireResponseInputItemListIterator = questionnaireResponseInputItemList.iterator()
    val questionnaireItemListIterator = questionnaireItemList.iterator()

    while (questionnaireResponseInputItemListIterator.hasNext()) {
      // TODO: Validate type and item nesting within answers for repeated answers
      // https://github.com/google/android-fhir/issues/286
      val questionnaireResponseInputItem = questionnaireResponseInputItemListIterator.next()
      if (questionnaireItemListIterator.hasNext()) {
        val questionnaireItem = questionnaireItemListIterator.next()
        if (!questionnaireItem.linkId.equals(questionnaireResponseInputItem.linkId)) {
          throw IllegalArgumentException(
            "Mismatching linkIds for questionnaire item ${questionnaireItem.linkId} and " +
              "questionnaire response item ${questionnaireResponseInputItem.linkId}"
          )
        }
        if (questionnaireResponseInputItem.hasAnswer() &&
            questionnaireItem.type != Questionnaire.QuestionnaireItemType.GROUP
        ) {
          if (!questionnaireItem.repeats && questionnaireResponseInputItem.answer.size > 1) {
            throw IllegalArgumentException(
              "Multiple answers in ${questionnaireResponseInputItem.linkId} and repeats false in " +
                "questionnaire item ${questionnaireItem.linkId}"
            )
          }
          questionnaireResponseInputItem.answer.forEachIndexed {
            index,
            questionnaireResponseItemAnswerComponent ->
            if (questionnaireResponseItemAnswerComponent.hasValue()) {
              when (questionnaireItem.type) {
                Questionnaire.QuestionnaireItemType.BOOLEAN,
                Questionnaire.QuestionnaireItemType.DECIMAL,
                Questionnaire.QuestionnaireItemType.INTEGER,
                Questionnaire.QuestionnaireItemType.DATE,
                Questionnaire.QuestionnaireItemType.DATETIME,
                Questionnaire.QuestionnaireItemType.TIME,
                Questionnaire.QuestionnaireItemType.STRING,
                Questionnaire.QuestionnaireItemType.URL ->
                  if (!questionnaireResponseItemAnswerComponent
                      .value
                      .fhirType()
                      .equals(questionnaireItem.type.toCode())
                  ) {
                    throw IllegalArgumentException(
                      "Type mismatch for linkIds for questionnaire item ${questionnaireItem.linkId} and " +
                        "questionnaire response item ${questionnaireResponseInputItem.linkId}"
                    )
                  }
                else -> Unit // Check type for primitives only
              }
            }
            validateQuestionnaireResponseItems(
              questionnaireResponseItemAnswerComponent.item,
              questionnaireItem.item
            )
          }
        } else if (questionnaireResponseInputItem.hasItem()) {
          validateQuestionnaireResponseItems(
            questionnaireResponseInputItem.item,
            questionnaireItem.item
          )
        }
      } else {
        // Input response has more items
        throw IllegalArgumentException(
          "No matching questionnaire item for questionnaire response item ${questionnaireResponseInputItem.linkId}"
        )
      }
    }
  }
}
