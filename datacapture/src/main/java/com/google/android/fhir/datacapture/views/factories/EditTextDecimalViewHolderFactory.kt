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

package com.google.android.fhir.datacapture.views.factories

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.getValidationErrorMessage
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object EditTextDecimalViewHolderFactory : QuestionnaireItemComposeViewHolderFactory {

  override fun getQuestionnaireItemViewHolderDelegate():
    QuestionnaireItemComposeViewHolderDelegate =
    EditTextViewHolderDelegate(
      KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
      uiInputText = {
        val questionnaireItemViewItemDecimalAnswer =
          it.answers.singleOrNull()?.valueDecimalType?.value?.toString()
        val draftAnswer = it.draftAnswer?.toString()

        when {
          questionnaireItemViewItemDecimalAnswer.isNullOrEmpty() && draftAnswer.isNullOrEmpty() ->
            ""
          questionnaireItemViewItemDecimalAnswer?.toDoubleOrNull() != null ->
            questionnaireItemViewItemDecimalAnswer
          else -> draftAnswer
        }
      },
      uiValidationMessage = { questionnaireViewItem, context ->
        if (questionnaireViewItem.draftAnswer != null) {
          context.getString(R.string.decimal_format_validation_error_msg)
        } else {
          getValidationErrorMessage(
            context,
            questionnaireViewItem,
            questionnaireViewItem.validationResult,
          )
        }
      },
      handleInput = { inputText, questionnaireViewItem ->
        inputText.toDoubleOrNull()?.let {
          questionnaireViewItem.setAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DecimalType(it.toString())),
          )
        }
          ?: questionnaireViewItem.setDraftAnswer(inputText)
      },
    )
}
