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

import android_fhir.datacapture_kmp.generated.resources.Res
import android_fhir.datacapture_kmp.generated.resources.decimal_format_validation_error_msg
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.google.fhir.model.r4b.Decimal
import com.google.fhir.model.r4b.QuestionnaireResponse

internal object EditTextDecimalViewHolderFactory : QuestionnaireItemComposeViewHolderFactory {

  override fun getQuestionnaireItemViewHolderDelegate():
    QuestionnaireItemComposeViewHolderDelegate =
    EditTextViewHolderDelegate(
      KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
      uiInputText = {
        val questionnaireItemViewItemDecimalAnswer = it.answers.singleOrNull()?.value?.asDecimal()
        val draftAnswer = it.draftAnswer?.toString()

        when {
          questionnaireItemViewItemDecimalAnswer != null && draftAnswer.isNullOrEmpty() -> ""
          questionnaireItemViewItemDecimalAnswer != null ->
            questionnaireItemViewItemDecimalAnswer.value.toString()
          else -> draftAnswer
        }
      },
      handleInput = { inputText, questionnaireViewItem ->
        inputText.toDoubleOrNull()?.let {
          questionnaireViewItem.setAnswer(
            QuestionnaireResponse.Item.Answer(
              value = QuestionnaireResponse.Item.Answer.Value.Decimal(Decimal(it.toString())),
            ),
          )
        }
          ?: questionnaireViewItem.setDraftAnswer(inputText)
      },
      validationMessageStringRes = Res.string.decimal_format_validation_error_msg,
    )
}
