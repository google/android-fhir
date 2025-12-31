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
import com.google.android.fhir.datacapture.extensions.FhirR4String
import com.google.android.fhir.datacapture.extensions.StringAnswerValue
import com.google.fhir.model.r4.QuestionnaireResponse

internal val EditTextPhoneNumberViewFactory =
  EditTextViewFactoryDelegate(
    KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
    uiInputText = { it.answers.singleOrNull()?.value?.asString()?.value?.value ?: "" },
    handleInput = { inputText, questionnaireViewItem ->
      val input =
        inputText.let {
          if (it.isEmpty()) {
            null
          } else {
            QuestionnaireResponse.Item.Answer(
              value = StringAnswerValue(value = FhirR4String(value = it)),
            )
          }
        }

      if (input != null) {
        questionnaireViewItem.setAnswer(input)
      } else {
        questionnaireViewItem.clearAnswer()
      }
    },
    validationMessageStringRes = Res.string.decimal_format_validation_error_msg,
  )
