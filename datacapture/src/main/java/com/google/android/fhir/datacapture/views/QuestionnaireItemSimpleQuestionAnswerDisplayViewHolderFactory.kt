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
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.validation.ValidationResult

internal object QuestionnaireItemSimpleQuestionAnswerDisplayViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_simple_question_answer_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var questionTextView: TextView
      private lateinit var answerTextView: TextView
      override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

      override fun init(itemView: View) {
        questionTextView = itemView.findViewById(R.id.question_text_view)
        answerTextView = itemView.findViewById(R.id.answer_text_view)
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {}

      override fun displayValidationResult(validationResult: ValidationResult) {
        // Display type questions have no user input to be validated
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        // Display type questions have no user input
      }
    }
}
