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

import android.icu.number.NumberFormatter
import android.icu.text.DecimalFormat
import android.os.Build
import android.text.InputType
import androidx.annotation.RequiresApi
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.getValidationErrorMessage
import java.util.Locale
import org.hl7.fhir.r4.model.IntegerType
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object EditTextIntegerViewHolderFactory :
  EditTextViewHolderFactory(R.layout.edit_text_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    QuestionnaireItemEditTextViewHolderDelegate(
      InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED,
      uiInputText = {
        val answer = it.answers.singleOrNull()?.valueIntegerType?.value?.toString()
        val draftAnswer = it.draftAnswer?.toString()
        when {
          answer.isNullOrEmpty() && draftAnswer.isNullOrEmpty() -> ""
          answer?.toIntOrNull() != null -> answer
          else -> draftAnswer
        }
      },
      uiValidationMessage = { questionnaireViewItem, context ->
        if (questionnaireViewItem.draftAnswer != null) {
          context.getString(
            R.string.integer_format_validation_error_msg,
            formatInteger(Int.MIN_VALUE),
            formatInteger(Int.MAX_VALUE),
          )
        } else {
          getValidationErrorMessage(
            context,
            questionnaireViewItem,
            questionnaireViewItem.validationResult,
          )
        }
      },
      handleInput = { inputText, questionnaireViewItem ->
        val input = inputText

        if (input.isEmpty()) {
          questionnaireViewItem.clearAnswer()
        } else if (input.toIntOrNull() != null) {
          questionnaireViewItem.setAnswer(
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(IntegerType(input)),
          )
        } else {
          questionnaireViewItem.setDraftAnswer(input)
        }
      },
    )

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
