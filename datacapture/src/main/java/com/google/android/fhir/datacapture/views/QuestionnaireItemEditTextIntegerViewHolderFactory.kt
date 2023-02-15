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
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireItemEditTextIntegerViewHolderFactory :
  QuestionnaireItemEditTextViewHolderFactory(
    R.layout.questionnaire_item_edit_text_single_line_view
  ) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object :
      QuestionnaireItemEditTextViewHolderDelegate(
        InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED,
      ) {
      override fun handleInput(
        editable: Editable,
        questionnaireItemViewItem: QuestionnaireItemViewItem
      ) {
        val input = editable.toString()
        val inputInteger = input.toIntOrNull()
        if (inputInteger != null) {
          questionnaireItemViewItem.setAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(IntegerType(input))
          )
        } else {
          questionnaireItemViewItem.updatePartialAnswer(input)
        }
      }

      override fun updateUI(
        questionnaireItemViewItem: QuestionnaireItemViewItem,
        textInputEditText: TextInputEditText,
        textInputLayout: TextInputLayout,
      ) {
        val answer =
          questionnaireItemViewItem.answers.singleOrNull()?.valueIntegerType?.value?.toString()
        val partialAnswer = questionnaireItemViewItem.partialAnswer?.toString()

        // Update the text
        val text = answer ?: partialAnswer
        if ((text != textInputEditText.text.toString())) {
          textInputEditText.setText(text)
        }

        // Update error message if partial answer present
        if (partialAnswer != null) {
          textInputLayout.error = "Invalid input" // TODO(Jing): put this in xml.
        }
      }
    }
}
