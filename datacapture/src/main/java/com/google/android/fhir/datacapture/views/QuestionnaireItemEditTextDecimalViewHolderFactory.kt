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

package com.google.android.fhir.datacapture.views

import android.text.Editable
import android.text.InputType
import com.google.android.fhir.datacapture.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireItemEditTextDecimalViewHolderFactory :
  QuestionnaireItemEditTextViewHolderFactory(
    R.layout.questionnaire_item_edit_text_single_line_view
  ) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemEditTextViewHolderDelegate(DECIMAL_INPUT_TYPE) {
      override fun handleInput(
        editable: Editable,
        questionnaireItemViewItem: QuestionnaireItemViewItem
      ) {
        val input = getValue(editable.toString())
        if (input != null) {
          questionnaireItemViewItem.setAnswer(input)
        } else {
          questionnaireItemViewItem.clearAnswer()
        }
      }

      private fun getValue(
        text: String
      ): QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent? {
        return text.toDoubleOrNull()?.let {
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
            .setValue(DecimalType(it.toString()))
        }
      }

      override fun updateUI(
        questionnaireItemViewItem: QuestionnaireItemViewItem,
        textInputEditText: TextInputEditText,
        textInputLayout: TextInputLayout,
      ) {
        val text =
          questionnaireItemViewItem.answers.singleOrNull()?.valueDecimalType?.value?.toString()
            ?: ""
        if (isTextUpdatesRequired(text, textInputEditText.text.toString())) {
          textInputEditText.setText(text)
        }
      }

      fun isTextUpdatesRequired(answerText: String, inputText: String): Boolean {
        if (answerText.isEmpty() && inputText.isEmpty()) {
          return false
        }
        if (answerText.isEmpty() || inputText.isEmpty()) {
          return true
        }
        // Avoid shifting focus by updating text field if the values are the same
        return answerText.toDouble() != inputText.toDouble()
      }
    }
}

const val DECIMAL_INPUT_TYPE = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
