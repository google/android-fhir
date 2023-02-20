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

import android.icu.number.NumberFormatter
import android.icu.text.DecimalFormat
import android.os.Build
import android.text.Editable
import android.text.InputType
import androidx.annotation.RequiresApi
import com.google.android.fhir.datacapture.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Locale
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
        if (input.isEmpty()) {
          questionnaireItemViewItem.clearAnswer()
          return
        }

        val inputInteger = input.toIntOrNull()
        if (inputInteger != null) {
          questionnaireItemViewItem.setAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(IntegerType(input))
          )
        } else {
          questionnaireItemViewItem.setDraftAnswer(input)
        }
      }

      override fun updateUI(
        questionnaireItemViewItem: QuestionnaireItemViewItem,
        textInputEditText: TextInputEditText,
        textInputLayout: TextInputLayout,
      ) {
        val answer =
          questionnaireItemViewItem.answers.singleOrNull()?.valueIntegerType?.value?.toString()
        val draftAnswer = questionnaireItemViewItem.draftAnswer?.toString()

        // Update the text
        val text = answer ?: draftAnswer
        if ((text != textInputEditText.text.toString())) {
          textInputEditText.setText(text)
        }

        // Update error message if draft answer present
        if (draftAnswer != null) {
          textInputLayout.error =
            textInputEditText.context.getString(
              R.string.integer_format_validation_error_msg,
              formatInteger(Int.MIN_VALUE),
              formatInteger(Int.MAX_VALUE)
            )
        }
      }
    }

  private fun formatInteger(value: Int): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      numberFormatter.format(value).toString()
    } else {
      decimalFormat.format(value)
    }
  }

  private val numberFormatter
    @RequiresApi(Build.VERSION_CODES.R) get() = NumberFormatter.withLocale(Locale.getDefault())

  private val decimalFormat
    get() = DecimalFormat.getInstance(Locale.getDefault())
}
