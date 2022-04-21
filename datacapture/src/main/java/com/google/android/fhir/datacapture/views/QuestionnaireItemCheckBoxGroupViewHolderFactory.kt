/*
 * Copyright 2021 Google LLC
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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.fhir.datacapture.ChoiceOrientationTypes
import com.google.android.fhir.datacapture.QuestionnaireFragment
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.choiceOrientation
import com.google.android.fhir.datacapture.optionExclusive
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.validation.getSingleStringValidationMessage
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireItemCheckBoxGroupViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_checkbox_group_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var header: QuestionnaireItemHeaderView
      private lateinit var checkboxGroup: ConstraintLayout
      private lateinit var flow: Flow
      private lateinit var error: TextView
      override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem
      override var fragment: QuestionnaireFragment? = null

      override fun init(itemView: View) {
        header = itemView.findViewById(R.id.header)
        checkboxGroup = itemView.findViewById(R.id.checkbox_group)
        flow = itemView.findViewById(R.id.checkbox_flow)
        error = itemView.findViewById(R.id.error)
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        val (questionnaireItem, _) = questionnaireItemViewItem
        val choiceOrientation =
          questionnaireItem.choiceOrientation ?: ChoiceOrientationTypes.VERTICAL

        header.bind(questionnaireItem)

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
        questionnaireItemViewItem
          .answerOption
          .map { answerOption -> View.generateViewId() to answerOption }
          .onEach { populateViewWithAnswerOption(it.first, it.second, choiceOrientation) }
          .map { it.first }
          .let { flow.referencedIds = it.toIntArray() }
      }

      override fun displayValidationResult(validationResult: ValidationResult) {
        error.text =
          if (validationResult.getSingleStringValidationMessage() == "") null
          else validationResult.getSingleStringValidationMessage()
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
          LayoutInflater.from(checkboxGroup.context)
            .inflate(R.layout.questionnaire_item_check_box_view, null)
        val checkbox =
          checkboxLayout.findViewById<CheckBox>(R.id.check_box).apply {
            id = viewId
            text = answerOption.valueCoding.display
            isChecked = questionnaireItemViewItem.isAnswerOptionSelected(answerOption)
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
                  questionnaireItemViewItem.addAnswer(
                    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                      value = answerOption.value
                    }
                  )
                  if (answerOption.optionExclusive) {
                    // if this answer option has optionExclusive extension, then deselect other
                    // answer options.
                    val optionExclusiveIndex = checkboxGroup.indexOfChild(it) - 1
                    for (i in 0 until questionnaireItemViewItem.answerOption.size) {
                      if (optionExclusiveIndex == i) {
                        continue
                      }
                      (checkboxGroup.getChildAt(i + 1) as CheckBox).isChecked = false
                      questionnaireItemViewItem.removeAnswer(
                        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                          value = questionnaireItemViewItem.answerOption[i].value
                        }
                      )
                    }
                  } else {
                    // deselect optionExclusive answer option.
                    for (i in 0 until questionnaireItemViewItem.answerOption.size) {
                      if (!questionnaireItemViewItem.answerOption[i].optionExclusive) {
                        continue
                      }
                      (checkboxGroup.getChildAt(i + 1) as CheckBox).isChecked = false
                      questionnaireItemViewItem.removeAnswer(
                        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                          value = questionnaireItemViewItem.answerOption[i].value
                        }
                      )
                    }
                  }
                }
                false -> {
                  questionnaireItemViewItem.removeAnswer(
                    QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                      value = answerOption.value
                    }
                  )
                }
              }

              onAnswerChanged(checkboxGroup.context)
            }
          }
        checkboxGroup.addView(checkbox)
        flow.addView(checkbox)
      }
    }
}
