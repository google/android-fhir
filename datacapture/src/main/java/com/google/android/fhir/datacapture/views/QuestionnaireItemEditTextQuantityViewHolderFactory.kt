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

import android.text.InputType
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.validation.Invalid
import java.math.BigDecimal
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.QuestionnaireResponse

/**
 * Inherits from QuestionnaireItemEditTextViewHolderFactory as only the numeric part of the quantity
 * is being handled right now. Will use a separate layout to handle the unit in the quantity.
 */
internal object QuestionnaireItemEditTextQuantityViewHolderFactory :
  QuestionnaireItemEditTextViewHolderFactory(
    R.layout.questionnaire_item_edit_text_single_line_view
  ) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemEditTextViewHolderDelegate(QUANTITY_INPUT_TYPE) {
      override fun getValue(
        text: String
      ): QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent? {
        // https://build.fhir.org/ig/HL7/sdc/behavior.html#initial
        // read default unit from initial, as ideally quantity must specify a unit
        return text
          .takeIf { it.isNotBlank() }
          ?.let {
            val value = BigDecimal(text)
            val quantity =
              with(questionnaireItemViewItem.questionnaireItem) {
                if (this.hasInitial() && this.initialFirstRep.valueQuantity.hasCode())
                  this.initialFirstRep.valueQuantity.let { initial ->
                    Quantity().apply {
                      this.value = value
                      this.code = initial.code
                      this.system = initial.system
                    }
                  }
                else Quantity().apply { this.value = value }
              }
            QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().setValue(quantity)
          }
      }

      override fun getText(
        answer: QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent?
      ): String {
        return answer?.valueQuantity?.value?.toString() ?: ""
      }

      override fun isTextUpdatesRequired(answerText: String, inputText: String): Boolean {
        if (answerText.isEmpty() && inputText.isEmpty()) {
          return false
        }
        if (answerText.isEmpty() || inputText.isEmpty()) {
          return true
        }
        // Avoid shifting focus by updating text field if the values are the same
        return answerText.toDouble() != inputText.toDouble()
      }
    }
}

const val QUANTITY_INPUT_TYPE = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
