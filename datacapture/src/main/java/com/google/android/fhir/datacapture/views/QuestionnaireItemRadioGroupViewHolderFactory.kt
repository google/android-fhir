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
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.displayString
import com.google.android.fhir.datacapture.localizedPrefix
import com.google.android.fhir.datacapture.localizedText
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireItemRadioGroupViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_radio_group_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var prefixTextView: TextView
      private lateinit var radioHeader: TextView
      private lateinit var radioGroup: ConstraintLayout
      private lateinit var flow: Flow
      private lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

      override fun init(itemView: View) {
        prefixTextView = itemView.findViewById(R.id.prefix)
        radioGroup = itemView.findViewById(R.id.radio_group)
        flow = itemView.findViewById(R.id.flow)
        radioHeader = itemView.findViewById(R.id.radio_header)
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        this.questionnaireItemViewItem = questionnaireItemViewItem
        if (!questionnaireItemViewItem.questionnaireItem.prefix.isNullOrEmpty()) {
          prefixTextView.visibility = View.VISIBLE
          prefixTextView.text = questionnaireItemViewItem.questionnaireItem.localizedPrefix
        } else {
          prefixTextView.visibility = View.GONE
        }
        val (questionnaireItem, questionnaireResponseItem) = questionnaireItemViewItem
        val answer = questionnaireResponseItem.answer.singleOrNull()?.valueCoding
        radioHeader.text = questionnaireItem.localizedText
        var previousId = -1
        questionnaireItem.answerOption.forEach {
          val radioButton =
            RadioButton(radioGroup.context).apply {
              id = View.generateViewId()
              text = it.displayString
              layoutParams =
                ViewGroup.LayoutParams(
                  ViewGroup.LayoutParams.WRAP_CONTENT,
                  ViewGroup.LayoutParams.WRAP_CONTENT
                )
              isChecked = it.valueCoding.equalsDeep(answer)
              setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                  // if-else block to prevent over-writing of "items" nested within "answer"
                  if (questionnaireResponseItem.answer.size > 0) {
                    questionnaireResponseItem.answer.apply {
                      this[0].value = questionnaireItem.answerOption[buttonView.id - 1].value
                    }
                  } else {
                    questionnaireResponseItem.answer.apply {
                      clear()
                      add(
                        QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                          value = questionnaireItem.answerOption[buttonView.id - 1].value
                        }
                      )
                    }
                  }

                  questionnaireItemViewItem.questionnaireResponseItemChangedCallback()

                  // unchecks the previous RadioButton if it exist
                  if (previousId != -1) {
                    radioGroup.findViewById<RadioButton>(previousId).isChecked = !isChecked
                  }
                  previousId = buttonView.id
                }
              }
            }
          radioGroup.addView(radioButton)
          flow.addView(radioButton)
        }
      }
    }
}
