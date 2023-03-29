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
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.getHeaderViewVisibility
import com.google.android.fhir.datacapture.extensions.localizedFlyoverSpanned
import com.google.android.fhir.datacapture.extensions.localizedInstructionsSpanned
import com.google.android.fhir.datacapture.extensions.localizedPrefixSpanned
import com.google.android.fhir.datacapture.extensions.localizedTextSpanned
import com.google.android.fhir.datacapture.extensions.updateTextAndVisibility
import com.google.android.fhir.datacapture.validation.Invalid
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
import com.google.android.material.divider.MaterialDivider
import org.hl7.fhir.r4.model.Questionnaire

/**
 * This view is a container that contains the question and answer obtained from
 * questionnaireItemViewItem [QuestionnaireViewItem].
 */
internal object ReviewViewHolderFactory : QuestionnaireItemViewHolderFactory(R.layout.review_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var header: ConstraintLayout
      private lateinit var flyOverTextView: TextView
      private lateinit var errorView: View
      private lateinit var answerView: TextView
      private lateinit var divider: MaterialDivider
      private lateinit var prefix: TextView
      private lateinit var question: TextView
      private lateinit var hint: TextView
      override lateinit var questionnaireViewItem: QuestionnaireViewItem

      override fun init(itemView: View) {
        header = itemView.findViewById(R.id.header)
        flyOverTextView = itemView.findViewById(R.id.flyover_text_view)
        divider = itemView.findViewById(R.id.text_divider)
        prefix = itemView.findViewById(R.id.prefix)
        question = itemView.findViewById(R.id.question)
        hint = itemView.findViewById(R.id.hint)
        errorView = itemView.findViewById(R.id.error_view)
        answerView = itemView.findViewById(R.id.answer_text_view)
      }

      override fun bind(questionnaireViewItem: QuestionnaireViewItem) {
        prefix.updateTextAndVisibility(
          questionnaireViewItem.questionnaireItem.localizedPrefixSpanned
        )
        question.updateTextAndVisibility(
          questionnaireViewItem.questionnaireItem.localizedTextSpanned
        )
        hint.updateTextAndVisibility(
          questionnaireViewItem.enabledDisplayItems.localizedInstructionsSpanned
        )
        header.visibility = getHeaderViewVisibility(prefix, question, hint)

        val localizedFlyoverSpanned =
          questionnaireViewItem.enabledDisplayItems.localizedFlyoverSpanned
        flyOverTextView.apply {
          visibility =
            if (localizedFlyoverSpanned.isNullOrEmpty()) {
              GONE
            } else {
              VISIBLE
            }
          text = localizedFlyoverSpanned
        }
        when (questionnaireViewItem.questionnaireItem.type) {
          Questionnaire.QuestionnaireItemType.GROUP,
          Questionnaire.QuestionnaireItemType.DISPLAY -> {
            errorView.visibility = GONE
            answerView.visibility = GONE
          }
          else -> {
            answerView.text = questionnaireViewItem.answerString(answerView.context)
            answerView.visibility = VISIBLE
            if (questionnaireViewItem.validationResult is Invalid) {
              errorView.findViewById<TextView>(R.id.error_text_view).text =
                questionnaireViewItem.validationResult.getSingleStringValidationMessage()
              errorView.visibility = VISIBLE
            } else {
              errorView.visibility = GONE
            }
          }
        }

        divider.visibility =
          if (header.visibility == VISIBLE ||
              flyOverTextView.visibility == VISIBLE ||
              answerView.visibility == VISIBLE ||
              errorView.visibility == VISIBLE
          ) {
            VISIBLE
          } else {
            GONE
          }
      }

      override fun setReadOnly(isReadOnly: Boolean) {}
    }
}
