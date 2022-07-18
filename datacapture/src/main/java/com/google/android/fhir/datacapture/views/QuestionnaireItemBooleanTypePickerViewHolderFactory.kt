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

import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.validation.getSingleStringValidationMessage
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireItemBooleanTypePickerViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_boolean_type_picker_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var header: QuestionnaireItemHeaderView
      private lateinit var radioGroup: RadioGroup
      private lateinit var yesRadioButton: RadioButton
      private lateinit var noRadioButton: RadioButton
      private lateinit var error: TextView

      override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

      override fun init(itemView: View) {
        header = itemView.findViewById(R.id.header)
        radioGroup = itemView.findViewById(R.id.radio_group)
        yesRadioButton = itemView.findViewById(R.id.yes_radio_button)
        noRadioButton = itemView.findViewById(R.id.no_radio_button)
        error = itemView.findViewById(R.id.error)
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        this.questionnaireItemViewItem = questionnaireItemViewItem
        val questionnaireItem = questionnaireItemViewItem.questionnaireItem

        header.bind(questionnaireItem)

        when (questionnaireItemViewItem.answers.singleOrNull()?.valueBooleanType?.value) {
          true -> {
            yesRadioButton.isChecked = true
            noRadioButton.isChecked = false
          }
          false -> {
            yesRadioButton.isChecked = false
            noRadioButton.isChecked = true
          }
          null -> {
            yesRadioButton.isChecked = false
            noRadioButton.isChecked = false
          }
        }

        yesRadioButton.setOnClickListener {
          if (questionnaireItemViewItem.answers.singleOrNull()?.valueBooleanType?.booleanValue() ==
              true
          ) {
            questionnaireItemViewItem.clearAnswer()
            radioGroup.clearCheck()
          } else {
            questionnaireItemViewItem.setAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(true)
              }
            )
          }
        }

        noRadioButton.setOnClickListener {
          if (questionnaireItemViewItem.answers.singleOrNull()?.valueBooleanType?.booleanValue() ==
              false
          ) {
            questionnaireItemViewItem.clearAnswer()
            radioGroup.clearCheck()
          } else {
            questionnaireItemViewItem.setAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(false)
              }
            )
          }
        }
      }

      override fun displayValidationResult(validationResult: ValidationResult) {
        error.text =
          if (validationResult.getSingleStringValidationMessage() == "") null
          else validationResult.getSingleStringValidationMessage()
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        for (i in 0 until radioGroup.childCount) {
          val view = radioGroup.getChildAt(i)
          view.isEnabled = !isReadOnly
        }
      }
    }
}
