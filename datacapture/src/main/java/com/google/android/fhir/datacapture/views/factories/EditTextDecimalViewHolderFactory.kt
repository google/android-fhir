/*
 * Copyright 2022-2025 Google LLC
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

import android.content.Context
import android.text.InputType
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.getValidationErrorMessage
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.material.textfield.TextInputEditText
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object EditTextDecimalViewHolderFactory :
  EditTextViewHolderFactory(R.layout.edit_text_single_line_view) {

  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemEditTextViewHolderDelegate(DECIMAL_INPUT_TYPE) {
      override suspend fun handleInput(
        inputText: String,
        questionnaireViewItem: QuestionnaireViewItem,
      ) {
        inputText.toDoubleOrNull()?.let {
          questionnaireViewItem.setAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DecimalType(it.toString())),
          )
        }
          ?: questionnaireViewItem.setDraftAnswer(inputText)
      }

      override fun updateInputTextUI(
        questionnaireViewItem: QuestionnaireViewItem,
        textInputEditText: TextInputEditText,
      ) {
        val questionnaireItemViewItemDecimalAnswer =
          questionnaireViewItem.answers.singleOrNull()?.valueDecimalType?.value?.toString()
        val draftAnswer = questionnaireViewItem.draftAnswer?.toString()

        if (questionnaireItemViewItemDecimalAnswer.isNullOrEmpty() && draftAnswer.isNullOrEmpty()) {
          textInputEditText.setText("")
        } else if (
          questionnaireItemViewItemDecimalAnswer?.toDoubleOrNull() !=
            textInputEditText.text.toString().toDoubleOrNull()
        ) {
          textInputEditText.setText(questionnaireItemViewItemDecimalAnswer)
        } else if (draftAnswer != null && draftAnswer != textInputEditText.text.toString()) {
          textInputEditText.setText(draftAnswer)
        }
      }

      override fun getValidationTextUIMessage(
        questionnaireViewItem: QuestionnaireViewItem,
        context: Context,
      ): String? {
        // Update error message if draft answer present
        return if (questionnaireViewItem.draftAnswer != null) {
          context.getString(R.string.decimal_format_validation_error_msg)
        } else {
          getValidationErrorMessage(
            context,
            questionnaireViewItem,
            questionnaireViewItem.validationResult,
          )
        }
      }
    }
}

const val DECIMAL_INPUT_TYPE = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
