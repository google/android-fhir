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

package com.google.android.fhir.datacapture.extensions

import android.content.Context
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import org.hl7.fhir.r4.model.Questionnaire

/**
 * Returns [R.string.required] if [QuestionnaireViewItem.showRequiredText] and
 * [Questionnaire.QuestionnaireItemComponent.required] is true, or [R.string.optional_text] if
 * [QuestionnaireViewItem.showOptionalText] is true.
 */
internal fun getRequiredOrOptionalText(
  questionnaireViewItem: QuestionnaireViewItem,
  context: Context
) =
  when {
    (questionnaireViewItem.questionnaireItem.required &&
      questionnaireViewItem.questionViewTextConfiguration.showRequiredText) -> {
      context.getString(R.string.required)
    }
    (!questionnaireViewItem.questionnaireItem.required &&
      questionnaireViewItem.questionViewTextConfiguration.showOptionalText) -> {
      context.getString(R.string.optional_helper_text)
    }
    else -> {
      null
    }
  }

/**
 * Returns the validation error message. If [Questionnaire.QuestionnaireItemComponent.required] is
 * true, the error message starts with `Required` text and the rest of the error message is placed
 * on the next line.
 */
internal fun getValidationErrorMessage(
  context: Context,
  questionnaireViewItem: QuestionnaireViewItem,
  validationResult: ValidationResult
): String? {
  return when (validationResult) {
    is NotValidated,
    Valid -> null
    is Invalid -> {
      val validationMessage = validationResult.getSingleStringValidationMessage()
      if (questionnaireViewItem.questionnaireItem.required &&
          questionnaireViewItem.questionViewTextConfiguration.showRequiredText
      ) {
        context.getString(R.string.required_text_and_new_line) + validationMessage
      } else {
        validationMessage
      }
    }
  }
}
