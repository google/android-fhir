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
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.localizedFlyoverSpanned
import com.google.android.fhir.datacapture.localizedInstructionsSpanned
import com.google.android.fhir.datacapture.localizedPrefixSpanned
import com.google.android.fhir.datacapture.validation.ValidationResult
import com.google.android.material.divider.MaterialDivider
import org.hl7.fhir.r4.model.Questionnaire

/**
 * This view is a container that contains the question and answer obtained from
 * questionnaireItemViewItem [QuestionnaireItemViewItem]. It is used in review mode to review the
 * answer of the question.
 */
internal object QuestionnaireItemSimpleQuestionAnswerDisplayViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_simple_question_answer_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var flyOverTextView: TextView
      private lateinit var notAnsweredView: View
      private lateinit var answerView: TextView
      private lateinit var divider: MaterialDivider
      private lateinit var prefix: TextView
      private lateinit var question: TextView
      private lateinit var hint: TextView
      private lateinit var header: ConstraintLayout
      override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

      override fun init(itemView: View) {
        header = itemView.findViewById(R.id.header)
        flyOverTextView = itemView.findViewById(R.id.flyover_text_view)
        divider = itemView.findViewById(R.id.text_divider)
        prefix = itemView.findViewById(R.id.prefix)
        question = itemView.findViewById(R.id.question)
        hint = itemView.findViewById(R.id.hint)
        notAnsweredView = itemView.findViewById(R.id.not_answered_view)
        answerView = itemView.findViewById(R.id.answer_text_view)
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        prefix.updateTextAndVisibility(
          questionnaireItemViewItem.questionnaireItem.localizedPrefixSpanned
        )
        updateQuestionText(question, questionnaireItemViewItem.questionnaireItem)
        hint.updateTextAndVisibility(
          questionnaireItemViewItem.questionnaireItem.localizedInstructionsSpanned
        )
        header.visibility = headerViewVisibility(prefix, question, hint)

        val localizedFlyoverSpanned =
          questionnaireItemViewItem.questionnaireItem.localizedFlyoverSpanned
        flyOverTextView.apply {
          visibility =
            if (localizedFlyoverSpanned.isNullOrEmpty()) {
              GONE
            } else {
              VISIBLE
            }
          text = localizedFlyoverSpanned
        }

        when (questionnaireItemViewItem.questionnaireItem.type) {
          Questionnaire.QuestionnaireItemType.GROUP,
          Questionnaire.QuestionnaireItemType.DISPLAY -> {
            notAnsweredView.visibility = GONE
            answerView.visibility = GONE
          }
          else -> {
            if (questionnaireItemViewItem.hasAnswer) {
              notAnsweredView.visibility = GONE
              answerView.visibility = VISIBLE
              answerView.text = questionnaireItemViewItem.answerString(answerView.context)
            } else {
              notAnsweredView.visibility = VISIBLE
              answerView.visibility = GONE
              notAnsweredView.findViewById<TextView>(R.id.error_text_view).text =
                questionnaireItemViewItem.answerString(answerView.context)
            }
          }
        }

        divider.visibility =
          if (header.visibility == VISIBLE ||
              flyOverTextView.visibility == VISIBLE ||
              answerView.visibility == VISIBLE ||
              notAnsweredView.visibility == VISIBLE
          ) {
            VISIBLE
          } else {
            GONE
          }
      }

      override fun displayValidationResult(validationResult: ValidationResult) {}

      override fun setReadOnly(isReadOnly: Boolean) {}
    }
}
