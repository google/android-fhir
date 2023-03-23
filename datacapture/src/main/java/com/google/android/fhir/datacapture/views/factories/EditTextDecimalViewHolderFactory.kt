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

package com.google.android.fhir.datacapture.views.factories

import android.text.Editable
import android.text.InputType
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object EditTextDecimalViewHolderFactory :
  EditTextViewHolderFactory(R.layout.edit_text_single_line_view) {

  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemEditTextViewHolderDelegate(DECIMAL_INPUT_TYPE) {
      override fun handleInput(editable: Editable, questionnaireViewItem: QuestionnaireViewItem) {
        editable.toString().toDoubleOrNull()?.let {
          questionnaireViewItem.setAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(DecimalType(it.toString()))
          )
        }
          ?: questionnaireViewItem.setDraftAnswer(editable.toString())
      }

      override fun updateUI(
        questionnaireViewItem: QuestionnaireViewItem,
        textInputEditText: TextInputEditText,
        textInputLayout: TextInputLayout,
      ) {
        val questionnaireItemViewItemDecimalAnswer =
          questionnaireViewItem.answers.singleOrNull()?.valueDecimalType?.value?.toString()

        val draftAnswer = questionnaireViewItem.draftAnswer?.toString()

        val decimalStringToDisplay = questionnaireItemViewItemDecimalAnswer ?: draftAnswer

        if (decimalStringToDisplay?.toDoubleOrNull() !=
            textInputEditText.text.toString().toDoubleOrNull()
        ) {
          textInputEditText.setText(decimalStringToDisplay)
        }
        // Update error message if draft answer present
        if (draftAnswer != null) {
          textInputLayout.error =
            textInputEditText.context.getString(R.string.decimal_format_validation_error_msg)
        }
      }
    }
}

const val DECIMAL_INPUT_TYPE = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
