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

package com.google.android.fhir.datacapture.contrib.views

import android.text.Editable
import android.text.InputType
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.views.QuestionnaireItemEditTextViewHolderDelegate
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewHolderDelegate
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewHolderFactory
import com.google.android.fhir.datacapture.views.QuestionnaireItemViewItem
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.StringType

object QuestionnaireItemPhoneNumberViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_edit_text_single_line_view) {
  override fun getQuestionnaireItemViewHolderDelegate(): QuestionnaireItemViewHolderDelegate =
    object : QuestionnaireItemEditTextViewHolderDelegate(InputType.TYPE_CLASS_PHONE) {

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
        return text.let {
          if (it.isEmpty()) {
            null
          } else {
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(StringType(it))
          }
        }
      }

      override fun updateUI(
        questionnaireItemViewItem: QuestionnaireItemViewItem,
        textInputEditText: TextInputEditText,
        textInputLayout: TextInputLayout,
      ) {
        val text =
          questionnaireItemViewItem.answers.singleOrNull()?.valueStringType?.value?.toString() ?: ""
        if (text != textInputEditText.text.toString()) {
          textInputEditText.setText(text)
        }
      }
    }
}
