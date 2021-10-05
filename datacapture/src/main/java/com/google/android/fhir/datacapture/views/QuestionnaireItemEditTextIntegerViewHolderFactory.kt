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

package com.google.android.fhir.datacapture.views

import android.content.Context
import android.text.InputType
import com.google.android.fhir.datacapture.validation.MaxValueConstraintValidator
import com.google.android.fhir.datacapture.validation.MinValueConstraintValidator
import com.google.android.fhir.datacapture.validation.ValidationResult
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireItemEditTextIntegerViewHolderFactory :
  QuestionnaireItemEditTextViewHolderFactory() {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object :
      QuestionnaireItemEditTextViewHolderDelegate(
        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED,
        true
      ) {
      override fun getValue(
        text: String
      ): QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent? {
        return text.toIntOrNull()?.let {
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(IntegerType(it))
        }
      }

      override fun getText(
        answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent?
      ): String {
        return answer?.valueIntegerType?.value?.toString() ?: ""
      }

      override fun getValidationResult(context: Context): ValidationResult {
        val minValueValidator =
          MinValueConstraintValidator.validate(
            questionnaireItemViewItem.questionnaireItem,
            questionnaireItemViewItem.questionnaireResponseItem,
            context
          )
        val maxValueValidator =
          MaxValueConstraintValidator.validate(
            questionnaireItemViewItem.questionnaireItem,
            questionnaireItemViewItem.questionnaireResponseItem,
            context
          )

        val isValid = minValueValidator.isValid && maxValueValidator.isValid
        return ValidationResult(
          isValid,
          listOf(minValueValidator.message ?: "", maxValueValidator.message ?: "")
        )
      }
    }
}
