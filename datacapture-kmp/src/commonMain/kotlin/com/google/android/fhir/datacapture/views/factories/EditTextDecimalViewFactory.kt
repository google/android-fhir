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

import android_fhir.datacapture_kmp.generated.resources.Res
import android_fhir.datacapture_kmp.generated.resources.decimal_format_validation_error_msg
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.google.android.fhir.datacapture.extensions.DecimalAnswerValue
import com.google.fhir.model.r4.Decimal
import com.google.fhir.model.r4.QuestionnaireResponse
import com.ionspin.kotlin.bignum.decimal.toBigDecimal

internal val EditTextDecimalViewFactory =
  EditTextViewFactoryDelegate(
    KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
    uiInputText = {
      val questionnaireItemViewItemDecimalAnswer = it.answers.singleOrNull()?.value?.asDecimal()
      val draftAnswer = it.draftAnswer?.toString()

      when {
        questionnaireItemViewItemDecimalAnswer == null && draftAnswer.isNullOrEmpty() -> ""
        questionnaireItemViewItemDecimalAnswer != null ->
          questionnaireItemViewItemDecimalAnswer.value.value?.toStringExpanded()
        else -> draftAnswer
      }
    },
    handleInput = { inputText, questionnaireViewItem ->
      inputText.toDoubleOrNull()?.let {
        questionnaireViewItem.setAnswer(
          QuestionnaireResponse.Item.Answer(
            value = DecimalAnswerValue(Decimal(value = it.toBigDecimal())),
          ),
        )
      }
        ?: questionnaireViewItem.setDraftAnswer(inputText)
    },
    validationMessageStringRes = Res.string.decimal_format_validation_error_msg,
  )
