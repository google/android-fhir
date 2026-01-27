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
import android_fhir.datacapture_kmp.generated.resources.integer_format_validation_error_msg
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.google.android.fhir.datacapture.NumberFormatter
import com.google.fhir.model.r4.Integer as FhirInteger
import com.google.fhir.model.r4.QuestionnaireResponse

internal val EditTextIntegerViewFactory =
  EditTextViewFactoryDelegate(
    KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
    uiInputText = {
      val answer = it.answers.singleOrNull()?.value?.asInteger()?.value?.value?.toString()
      val draftAnswer = it.draftAnswer?.toString()
      when {
        answer.isNullOrEmpty() && draftAnswer.isNullOrEmpty() -> ""
        answer?.toIntOrNull() != null -> answer
        else -> draftAnswer
      }
    },
    handleInput = { inputText, questionnaireViewItem ->
      if (inputText.isEmpty()) {
        questionnaireViewItem.clearAnswer()
      } else if (inputText.toIntOrNull() != null) {
        questionnaireViewItem.setAnswer(
          QuestionnaireResponse.Item.Answer(
            value =
              QuestionnaireResponse.Item.Answer.Value.Integer(
                FhirInteger(value = inputText.toInt()),
              ),
          ),
        )
      } else {
        questionnaireViewItem.setDraftAnswer(inputText)
      }
    },
    validationMessageStringRes = Res.string.integer_format_validation_error_msg,
    validationMessageStringResArgs =
      arrayOf(
        NumberFormatter.formatInteger(Int.MIN_VALUE),
        NumberFormatter.formatInteger(Int.MAX_VALUE),
      ),
  )
