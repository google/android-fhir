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
import android.widget.CheckBox
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.ChoiceOrientationTypes
import com.google.android.fhir.datacapture.extensions.choiceOrientation
import com.google.android.fhir.datacapture.extensions.displayString
import com.google.android.fhir.datacapture.extensions.itemAnswerOptionImage
import com.google.android.fhir.datacapture.extensions.optionExclusive
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.validation.NotValidated
import com.google.android.fhir.datacapture.validation.Valid
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.views.HeaderView
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object CheckBoxGroupViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.checkbox_group_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var header: HeaderView
      private lateinit var checkboxGroup: ConstraintLayout
      private lateinit var flow: Flow
      override lateinit var questionnaireViewItem: QuestionnaireViewItem

      override fun init(itemView: View) {
        header = itemView.findViewById(R.id.header)
        checkboxGroup = itemView.findViewById(R.id.checkbox_group)
        flow = itemView.findViewById(R.id.checkbox_flow)
      }

      override fun bind(questionnaireViewItem: QuestionnaireViewItem) {
        header.bind(questionnaireViewItem)
        header.showRequiredOrOptionalTextInHeaderView(questionnaireViewItem)
        val choiceOrientation =
          questionnaireViewItem.questionnaireItem.choiceOrientation
            ?: ChoiceOrientationTypes.VERTICAL

        // Keep the Flow layout which is always the first child
        checkboxGroup.removeViews(1, checkboxGroup.childCount - 1)
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

      override fun setReadOnly(isReadOnly: Boolean) {
        // The Flow layout has index 0. The checkbox indices start from 1.
        for (i in 1 until checkboxGroup.childCount) {
          val view = checkboxGroup.getChildAt(i)
          view.isEnabled = !isReadOnly
        }
      }

      private fun populateViewWithAnswerOption(
        viewId: Int,
        answerOption: Questionnaire.QuestionnaireItemAnswerOptionComponent,
        choiceOrientation: ChoiceOrientationTypes
      ) {
        val checkboxLayout =
          LayoutInflater.from(checkboxGroup.context).inflate(R.layout.check_box_view, null)
        val checkbox =
          checkboxLayout.findViewById<CheckBox>(R.id.check_box).apply {
            id = viewId
            text = answerOption.value.displayString(header.context)
            setCompoundDrawablesRelative(
              answerOption.itemAnswerOptionImage(checkboxGroup.context),
              null,
              null,
              null
            )
            isChecked = questionnaireViewItem.isAnswerOptionSelected(answerOption)
            layoutParams =
              ViewGroup.LayoutParams(
                when (choiceOrientation) {
                  ChoiceOrientationTypes.HORIZONTAL -> ViewGroup.LayoutParams.WRAP_CONTENT
                  ChoiceOrientationTypes.VERTICAL -> ViewGroup.LayoutParams.MATCH_PARENT
                },
                ViewGroup.LayoutParams.WRAP_CONTENT
              )
            setOnClickListener {
              when (isChecked) {
                true -> {
                  val newAnswers = questionnaireViewItem.answers.toMutableList()
                  newAnswers +=
                    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                      value = answerOption.value
                    }

                  if (answerOption.optionExclusive) {
                    // if this answer option has optionExclusive extension, then deselect other
                    // answer options.
                    val optionExclusiveIndex = checkboxGroup.indexOfChild(it) - 1
                    for (i in 0 until questionnaireViewItem.answerOption.size) {
                      if (optionExclusiveIndex == i) {
                        continue
                      }
                      (checkboxGroup.getChildAt(i + 1) as CheckBox).isChecked = false
                      newAnswers.removeIf {
                        it.value.equalsDeep(questionnaireViewItem.answerOption[i].value)
                      }
                    }
                  } else {
                    // deselect optionExclusive answer option.
                    for (i in 0 until questionnaireViewItem.answerOption.size) {
                      if (!questionnaireViewItem.answerOption[i].optionExclusive) {
                        continue
                      }
                      (checkboxGroup.getChildAt(i + 1) as CheckBox).isChecked = false
                      newAnswers.removeIf {
                        it.value.equalsDeep(questionnaireViewItem.answerOption[i].value)
                      }
                    }
                  }
                  questionnaireViewItem.setAnswer(*newAnswers.toTypedArray())
                }
                false -> {
                  questionnaireViewItem.removeAnswer(
                    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                      value = answerOption.value
                    }
                  )
                }
              }
            }
          }
        checkboxGroup.addView(checkbox)
        flow.addView(checkbox)
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
