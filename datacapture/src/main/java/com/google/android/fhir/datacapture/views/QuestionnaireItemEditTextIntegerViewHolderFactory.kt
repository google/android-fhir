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
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.Valid
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

      override fun getValue(
        text: String
      ): QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent {
        return text.toIntOrNull()?.let {
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(IntegerType(it))
        }
          ?: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
            .setValue(IntegerType())
      }

      override fun getText(
        answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent?
      ): String {
        return answer?.valueIntegerType?.value?.toString() ?: ""
      }

      override fun hasTextUpperLimit(): Boolean {
        return true
      }

      override fun validateTextUpperLimit(editable: Editable?) {
        editable.toString().toBigIntegerOrNull()?.let {
          if (it > Int.MAX_VALUE.toBigInteger()) {
            displayValidationResult(
              Invalid(
                listOf(
                  textInputLayout.context.getString(
                    R.string.integer_field_upper_limit_validation_error_msg
                  )
                )
              )
            )
          } else {
            displayValidationResult(Valid)
          }
        }
      }
    }
}
