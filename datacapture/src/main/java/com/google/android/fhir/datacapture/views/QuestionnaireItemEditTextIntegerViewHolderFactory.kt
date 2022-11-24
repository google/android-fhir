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
      ): QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent? {
        return text.toBigIntegerOrNull()?.let {
          if (it > Int.MAX_VALUE.toBigInteger()) {
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(IntegerType(Int.MAX_VALUE))
          } else {
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent()
              .setValue(IntegerType(it.toInt()))
          }
        }
      }

      override fun getText(
        answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent?
      ): String {
        return answer?.valueIntegerType?.value?.toString() ?: ""
      }

      override fun isTextExceedLimit(editable: Editable?): Boolean {
        return editable.toString().toBigInteger() > Int.MAX_VALUE.toBigInteger()
      }

      override fun getTextDefaultLimit(): String {
        return Int.MAX_VALUE.toString()
      }
    }
}
