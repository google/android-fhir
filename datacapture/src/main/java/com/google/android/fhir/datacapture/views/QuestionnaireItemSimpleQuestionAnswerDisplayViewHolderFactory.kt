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

import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.localizedTextSpanned
import com.google.android.fhir.datacapture.validation.ValidationResult
import org.hl7.fhir.r4.model.Questionnaire

/**
 * This view is a container that contains the question and answer obtained from
 * questionnaireResponseItemViewItem [QuestionnaireResponseItemViewItem].
 *
 * Question which are not answered are shown as Not Answered.
 */
internal object QuestionnaireItemSimpleQuestionAnswerDisplayViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_simple_question_answer_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var questionTextView: TextView
      private lateinit var answerTextView: TextView
      private lateinit var divider: View
      override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

      override fun init(itemView: View) {
        questionTextView = itemView.findViewById(R.id.question_text_view)
        answerTextView = itemView.findViewById(R.id.answer_text_view)
        divider = itemView.findViewById(R.id.text_divider)
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        when (questionnaireItemViewItem.questionnaireItem.type) {
          Questionnaire.QuestionnaireItemType.DISPLAY,
          Questionnaire.QuestionnaireItemType.GROUP -> {
            if (!questionnaireItemViewItem.questionnaireItem.localizedTextSpanned.isNullOrEmpty()) {
              questionTextView.apply {
                visibility = View.VISIBLE
                setTypeface(this.typeface, Typeface.BOLD)
                text = questionnaireItemViewItem.questionnaireItem.localizedTextSpanned
              }
              divider.visibility = View.VISIBLE
            }
          }
          else -> {
            questionTextView.apply {
              visibility = View.VISIBLE
              text = questionnaireItemViewItem.questionnaireItem.localizedTextSpanned
            }
            answerTextView.apply {
              visibility = View.VISIBLE
              text = questionnaireItemViewItem.answerString
            }
            divider.visibility = View.VISIBLE
          }
        }
      }

      override fun displayValidationResult(validationResult: ValidationResult) {}

      override fun setReadOnly(isReadOnly: Boolean) {}
    }
}
