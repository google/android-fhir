/*
 * Copyright 2020 Google LLC
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
import com.google.android.fhir.datacapture.localizedPrefix
import com.google.android.fhir.datacapture.localizedText
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.validation.getSingleStringValidationMessage
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireItemBooleanTypePickerViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_boolean_type_picker_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var prefixTextView: TextView
      private lateinit var questionTextView: TextView
      private lateinit var radioGroup: RadioGroup
      private lateinit var yesRadioButton: RadioButton
      private lateinit var noRadioButton: RadioButton
      private lateinit var errorTextView: TextView

      override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

      override fun init(itemView: View) {
        yesRadioButton = itemView.findViewById(R.id.yes_radio_button)
        noRadioButton = itemView.findViewById(R.id.no_radio_button)
        prefixTextView = itemView.findViewById(R.id.prefix)
        radioGroup = itemView.findViewById(R.id.radio_group_main)
        questionTextView = itemView.findViewById(R.id.question_text_view)
        errorTextView = itemView.findViewById(R.id.error_text_view)
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        this.questionnaireItemViewItem = questionnaireItemViewItem
        val (questionnaireItem, questionnaireResponseItem) = questionnaireItemViewItem
        questionTextView.text = questionnaireItem.localizedText

        if (!questionnaireItem.prefix.isNullOrEmpty()) {
          prefixTextView.visibility = View.VISIBLE
          prefixTextView.text = questionnaireItem.localizedPrefix
        } else {
          prefixTextView.visibility = View.GONE
        }

        when (questionnaireItemViewItem.singleAnswerOrNull?.valueBooleanType?.value) {
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
          if (questionnaireResponseItem.answer.singleOrNull()?.valueBooleanType?.booleanValue() ==
              true
          ) {
            questionnaireResponseItem.answer.clear()
            radioGroup.clearCheck()
          } else {
            questionnaireResponseItem.answer.clear()
            questionnaireResponseItem.answer.add(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(true)
              }
            )
          }
          onAnswerChanged(radioGroup.context)
        }

        noRadioButton.setOnClickListener {
          if (questionnaireResponseItem.answer.singleOrNull()?.valueBooleanType?.booleanValue() ==
              false
          ) {
            questionnaireResponseItem.answer.clear()
            radioGroup.clearCheck()
          } else {
            questionnaireResponseItem.answer.clear()
            questionnaireResponseItem.answer.add(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(false)
              }
            )
          }
          onAnswerChanged(radioGroup.context)
        }
      }

      override fun displayValidationResult(validationResult: ValidationResult) {
        errorTextView.text =
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
