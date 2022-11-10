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
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.fhir.datacapture.ChoiceOrientationTypes
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.choiceOrientation
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireItemBooleanTypePickerViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_boolean_type_picker_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var header: QuestionnaireItemHeaderView
      private lateinit var radioGroup: ConstraintLayout
      private lateinit var yesRadioButton: RadioButton
      private lateinit var noRadioButton: RadioButton
      private lateinit var error: TextView
      private lateinit var flow: Flow

      override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

      override fun init(itemView: View) {
        header = itemView.findViewById(R.id.header)
        radioGroup = itemView.findViewById(R.id.radio_group)
        yesRadioButton = itemView.findViewById(R.id.yes_radio_button)
        noRadioButton = itemView.findViewById(R.id.no_radio_button)
        error = itemView.findViewById(R.id.error)
        flow = itemView.findViewById(R.id.flow)
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        this.questionnaireItemViewItem = questionnaireItemViewItem
        val questionnaireItem = questionnaireItemViewItem.questionnaireItem
        header.bind(questionnaireItem)
        val choiceOrientation =
          questionnaireItem.choiceOrientation ?: ChoiceOrientationTypes.VERTICAL
        if (choiceOrientation == ChoiceOrientationTypes.HORIZONTAL) {
          with(flow) {
            setOrientation(Flow.HORIZONTAL)
            setWrapMode(Flow.WRAP_CHAIN)
          }
        }
        yesRadioButton.setLayoutParamsByOrientation()
        noRadioButton.setLayoutParamsByOrientation()

        yesRadioButton.isChecked = false
        noRadioButton.isChecked = false

        when (questionnaireItemViewItem.answers.singleOrNull()?.valueBooleanType?.value) {
          true -> {
            yesRadioButton.isChecked = true
          }
          false -> {
            noRadioButton.isChecked = true
          }
        }

        yesRadioButton.setOnClickListener {
          if (questionnaireItemViewItem.answers.singleOrNull()?.valueBooleanType?.booleanValue() ==
              true
          ) {
            questionnaireItemViewItem.clearAnswer()
            yesRadioButton.isChecked = false
            //            radioGroup.clearCheck()
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
            noRadioButton.isChecked = false
            //            radioGroup.clearCheck()
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
        when (validationResult) {
          is NotValidated,
          Valid -> header.showErrorText(isErrorTextVisible = false)
          is Invalid -> {
            header.showErrorText(errorText = validationResult.getSingleStringValidationMessage())
          }
        }
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        for (i in 0 until radioGroup.childCount) {
          val view = radioGroup.getChildAt(i)
          view.isEnabled = !isReadOnly
        }
      }

      private fun RadioButton.setLayoutParamsByOrientation() {
        val choiceOrientation =
          questionnaireItemViewItem.questionnaireItem.choiceOrientation
            ?: ChoiceOrientationTypes.VERTICAL
        layoutParams =
          ViewGroup.LayoutParams(
            when (choiceOrientation) {
              ChoiceOrientationTypes.HORIZONTAL -> ViewGroup.LayoutParams.WRAP_CONTENT
              ChoiceOrientationTypes.VERTICAL -> ViewGroup.LayoutParams.MATCH_PARENT
            },
            ViewGroup.LayoutParams.WRAP_CONTENT
          )
      }
    }
}
