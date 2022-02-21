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

import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.google.android.fhir.datacapture.ChoiceOrientationTypes
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.choiceOrientation
import com.google.android.fhir.datacapture.displayString
import com.google.android.fhir.datacapture.localizedPrefixSpanned
import com.google.android.fhir.datacapture.localizedTextSpanned
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.fhir.datacapture.validation.getSingleStringValidationMessage
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireItemRadioGroupViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_radio_group_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var prefixTextView: TextView
      private lateinit var questionTextView: TextView
      private lateinit var radioGroup: ConstraintLayout
      private lateinit var flow: Flow
      private lateinit var errorTextView: TextView
      override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

      override fun init(itemView: View) {
        prefixTextView = itemView.findViewById(R.id.prefix_text_view)
        questionTextView = itemView.findViewById(R.id.question_text_view)
        radioGroup = itemView.findViewById(R.id.radio_group)
        flow = itemView.findViewById(R.id.flow)
        errorTextView = itemView.findViewById(R.id.error_text_view)
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        if (!questionnaireItemViewItem.questionnaireItem.prefix.isNullOrEmpty()) {
          prefixTextView.visibility = View.VISIBLE
          prefixTextView.text = questionnaireItemViewItem.questionnaireItem.localizedPrefixSpanned
        } else {
          prefixTextView.visibility = View.GONE
        }
        val questionnaireItem = questionnaireItemViewItem.questionnaireItem
        questionTextView.text = questionnaireItem.localizedTextSpanned
        // Keep the Flow layout which is the first child
        radioGroup.removeViews(1, radioGroup.childCount - 1)
        flow.referencedIds = IntArray(0)
        val choiceOrientation =
          questionnaireItem.choiceOrientation ?: ChoiceOrientationTypes.VERTICAL
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
        errorTextView.text =
          if (validationResult.getSingleStringValidationMessage() == "") null
          else validationResult.getSingleStringValidationMessage()
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        // By default, radio button starts from index 1
        for (i in 1 until radioGroup.childCount) {
          val view = radioGroup.getChildAt(i)
          view.isEnabled = !isReadOnly
        }
      }
      private fun populateViewWithAnswerOption(
        answerOption: Questionnaire.QuestionnaireItemAnswerOptionComponent,
        choiceOrientation: ChoiceOrientationTypes,
        index: Int
      ) {
        val radioButton =
          RadioButton(radioGroup.context, null, R.attr.radioButtonStyleQuestionnaire).apply {
            id = index + 1
            text = answerOption.displayString
            layoutParams =
              ViewGroup.LayoutParams(
                if (choiceOrientation == ChoiceOrientationTypes.HORIZONTAL)
                  ViewGroup.LayoutParams.WRAP_CONTENT
                else ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
              )
            isChecked = questionnaireItemViewItem.isAnswerOptionSelected(answerOption)
            setOnCheckedChangeListener { checkedButton, isChecked ->
              when (isChecked) {
                true -> {
                  // if-else block to prevent over-writing of "items" nested within "answer"
                  if (questionnaireItemViewItem.questionnaireResponseItem.answer.size > 0) {
                    questionnaireItemViewItem.questionnaireResponseItem.answer.apply {
                      this[0].value = answerOption.value
                    }
                  } else {
                    questionnaireItemViewItem.questionnaireResponseItem.answer.apply {
                      clear()
                      add(
                        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                          value = answerOption.value
                        }
                      )
                    }
                  }

                  val buttons = radioGroup.children.asIterable().filterIsInstance<RadioButton>()
                  buttons.forEach { button -> uncheckIfNotButtonId(checkedButton.id, button) }

                  onAnswerChanged(context)
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
    }
}
