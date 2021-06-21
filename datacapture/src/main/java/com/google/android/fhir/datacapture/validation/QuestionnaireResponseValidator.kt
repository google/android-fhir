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
    questionnaireResponseItemList: List<QuestionnaireResponse.QuestionnaireResponseItemComponent>
  ): Map<String, List<ValidationResult>> {
    val questionnaireItemListIterator = questionnaireItemList.iterator()
    val questionnaireResponseItemListIterator = questionnaireResponseItemList.iterator()
    while (questionnaireItemListIterator.hasNext() &&
      questionnaireResponseItemListIterator.hasNext()) {
      val questionnaireItem = questionnaireItemListIterator.next()
      val questionnaireResponseItem = questionnaireResponseItemListIterator.next()
      linkIdToValidationResultMap[questionnaireItem.linkId] = mutableListOf()
      linkIdToValidationResultMap[questionnaireItem.linkId]?.add(
        QuestionnaireResponseItemValidator.validate(questionnaireItem, questionnaireResponseItem)
      )
      if (questionnaireItem.hasNestedItemsWithinAnswers) {
        // TODO(https://github.com/google/android-fhir/issues/487): Validates all answers.
        validate(questionnaireItem.item, questionnaireResponseItem.answer[0].item)
      }
      validate(questionnaireItem.item, questionnaireResponseItem.item)
    }
    return linkIdToValidationResultMap
  }
}
