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

package com.google.android.fhir.datacapture.views.factories

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.ChoiceOrientationTypes
import com.google.android.fhir.datacapture.extensions.choiceOrientation
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.HeaderView
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object BooleanChoiceViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.boolean_choice_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var header: HeaderView
      private lateinit var radioGroup: ConstraintLayout
      private lateinit var yesRadioButton: RadioButton
      private lateinit var noRadioButton: RadioButton
      private lateinit var flow: Flow

      override lateinit var questionnaireViewItem: QuestionnaireViewItem

      override fun init(itemView: View) {
        header = itemView.findViewById(R.id.header)
        radioGroup = itemView.findViewById(R.id.radio_constraint_layout)
        yesRadioButton = itemView.findViewById(R.id.yes_radio_button)
        noRadioButton = itemView.findViewById(R.id.no_radio_button)
        flow = itemView.findViewById(R.id.flow)
      }

      override fun bind(questionnaireViewItem: QuestionnaireViewItem) {
        this.questionnaireViewItem = questionnaireViewItem
        header.bind(questionnaireViewItem)
        header.showRequiredOrOptionalTextInHeaderView(questionnaireViewItem)
        val choiceOrientation =
          questionnaireViewItem.questionnaireItem.choiceOrientation
            ?: ChoiceOrientationTypes.VERTICAL
        with(flow) {
          when (choiceOrientation) {
            ChoiceOrientationTypes.HORIZONTAL -> {
              setOrientation(Flow.HORIZONTAL)
              setWrapMode(Flow.WRAP_CHAIN)
            }
            ChoiceOrientationTypes.VERTICAL -> {
              setOrientation(Flow.VERTICAL)
              setWrapMode(Flow.WRAP_NONE)
            }
          }
        }

        yesRadioButton.setLayoutParamsByOrientation(choiceOrientation)
        noRadioButton.setLayoutParamsByOrientation(choiceOrientation)

        when (questionnaireViewItem.answers.singleOrNull()?.valueBooleanType?.value) {
          true -> {
            yesRadioButton.isChecked = true
            noRadioButton.isChecked = false
          }
          false -> {
            noRadioButton.isChecked = true
            yesRadioButton.isChecked = false
          }
          null -> {
            yesRadioButton.isChecked = false
            noRadioButton.isChecked = false
          }
        }

        yesRadioButton.setOnClickListener {
          if (questionnaireViewItem.answers.singleOrNull()?.valueBooleanType?.booleanValue() == true
          ) {
            questionnaireViewItem.clearAnswer()
            yesRadioButton.isChecked = false
          } else {
            questionnaireViewItem.setAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(true)
              }
            )
          }
        }

        noRadioButton.setOnClickListener {
          if (questionnaireViewItem.answers.singleOrNull()?.valueBooleanType?.booleanValue() ==
              false
          ) {
            questionnaireViewItem.clearAnswer()
            noRadioButton.isChecked = false
          } else {
            questionnaireViewItem.setAnswer(
              QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                value = BooleanType(false)
              }
            )
          }
        }

        displayValidationResult(questionnaireViewItem.validationResult)
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        for (i in 0 until radioGroup.childCount) {
          val view = radioGroup.getChildAt(i)
          view.isEnabled = !isReadOnly
        }
      }

      private fun RadioButton.setLayoutParamsByOrientation(
        choiceOrientation: ChoiceOrientationTypes
      ) {
        layoutParams =
          LinearLayout.LayoutParams(
            when (choiceOrientation) {
              ChoiceOrientationTypes.HORIZONTAL -> /* width= */ 0
              ChoiceOrientationTypes.VERTICAL -> ViewGroup.LayoutParams.MATCH_PARENT
            },
            ViewGroup.LayoutParams.WRAP_CONTENT,
            /* weight= */ 1.0f
          )
      }

      private fun displayValidationResult(validationResult: ValidationResult) {
        when (validationResult) {
          is NotValidated,
          Valid -> header.showErrorText(isErrorTextVisible = false)
          is Invalid -> {
            header.showErrorText(errorText = validationResult.getSingleStringValidationMessage())
          }
        }
      }
    }
}
