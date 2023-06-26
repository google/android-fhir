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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.ChoiceOrientationTypes
import com.google.android.fhir.datacapture.extensions.choiceOrientation
import com.google.android.fhir.datacapture.extensions.displayString
import com.google.android.fhir.datacapture.extensions.itemAnswerOptionImage
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.HeaderView
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object RadioGroupViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.radio_group_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var header: HeaderView
      private lateinit var radioGroup: ConstraintLayout
      private lateinit var flow: Flow
      override lateinit var questionnaireViewItem: QuestionnaireViewItem

      override fun init(itemView: View) {
        header = itemView.findViewById(R.id.header)
        radioGroup = itemView.findViewById(R.id.radio_group)
        flow = itemView.findViewById(R.id.flow)
      }

      override fun bind(questionnaireViewItem: QuestionnaireViewItem) {
        header.bind(questionnaireViewItem)
        header.showRequiredOrOptionalTextInHeaderView(questionnaireViewItem)
        // Keep the Flow layout which is the first child
        radioGroup.removeViews(1, radioGroup.childCount - 1)
        val choiceOrientation =
          questionnaireViewItem.questionnaireItem.choiceOrientation
            ?: ChoiceOrientationTypes.VERTICAL
        when (choiceOrientation) {
          ChoiceOrientationTypes.HORIZONTAL -> {
            flow.setOrientation(Flow.HORIZONTAL)
            flow.setWrapMode(Flow.WRAP_CHAIN)
          }
          ChoiceOrientationTypes.VERTICAL -> {
            flow.setOrientation(Flow.VERTICAL)
            flow.setWrapMode(Flow.WRAP_NONE)
          }
        }
        questionnaireViewItem.answerOption
          .map { answerOption -> View.generateViewId() to answerOption }
          .onEach { populateViewWithAnswerOption(it.first, it.second, choiceOrientation) }
          .map { it.first }
          .let { flow.referencedIds = it.toIntArray() }
        displayValidationResult(questionnaireViewItem.validationResult)
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

      override fun setReadOnly(isReadOnly: Boolean) {
        // The Flow layout has index 0. The radio button indices start from 1.
        for (i in 1 until radioGroup.childCount) {
          val view = radioGroup.getChildAt(i)
          view.isEnabled = !isReadOnly
        }
      }

      private fun populateViewWithAnswerOption(
        viewId: Int,
        answerOption: Questionnaire.QuestionnaireItemAnswerOptionComponent,
        choiceOrientation: ChoiceOrientationTypes
      ) {
        val radioButtonItem =
          LayoutInflater.from(radioGroup.context).inflate(R.layout.radio_button, null)
        var isCurrentlySelected = questionnaireViewItem.isAnswerOptionSelected(answerOption)
        val radioButton =
          radioButtonItem.findViewById<RadioButton>(R.id.radio_button).apply {
            id = viewId
            text = answerOption.value.displayString(header.context)
            setCompoundDrawablesRelative(
              answerOption.itemAnswerOptionImage(radioGroup.context),
              null,
              null,
              null
            )
            layoutParams =
              ViewGroup.LayoutParams(
                when (choiceOrientation) {
                  ChoiceOrientationTypes.HORIZONTAL -> ViewGroup.LayoutParams.WRAP_CONTENT
                  ChoiceOrientationTypes.VERTICAL -> ViewGroup.LayoutParams.MATCH_PARENT
                },
                ViewGroup.LayoutParams.WRAP_CONTENT
              )
            isChecked = isCurrentlySelected
            setOnClickListener { radioButton ->
              isCurrentlySelected = !isCurrentlySelected
              when (isCurrentlySelected) {
                true -> {
                  updateAnswer(answerOption)
                  val buttons = radioGroup.children.asIterable().filterIsInstance<RadioButton>()
                  buttons.forEach { button -> uncheckIfNotButtonId(radioButton.id, button) }
                }
                false -> {
                  questionnaireViewItem.clearAnswer()
                  (radioButton as RadioButton).isChecked = false
                }
              }
            }
          }
        radioGroup.addView(radioButton)
        flow.addView(radioButton)
      }

      private fun uncheckIfNotButtonId(checkedId: Int, button: RadioButton) {
        if (button.id != checkedId) button.isChecked = false
      }

      private fun updateAnswer(answerOption: Questionnaire.QuestionnaireItemAnswerOptionComponent) {
        questionnaireViewItem.setAnswer(
          QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
            value = answerOption.value
          }
        )
      }
    }
}
