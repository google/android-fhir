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
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.localizedFlyoverSpanned
import com.google.android.fhir.datacapture.views.HeaderView
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
      private lateinit var header: HeaderView
      private lateinit var flyOverTextView: TextView
      private lateinit var answerTextView: TextView
      private lateinit var divider: MaterialDivider
      override lateinit var questionnaireViewItem: QuestionnaireViewItem

      override fun init(itemView: View) {
        header = itemView.findViewById(R.id.header)
        flyOverTextView = itemView.findViewById(R.id.flyover_text_view)
        answerTextView = itemView.findViewById(R.id.answer_text_view)
        divider = itemView.findViewById(R.id.text_divider)
      }

      override fun bind(questionnaireViewItem: QuestionnaireViewItem) {
        header.bind(questionnaireViewItem.questionnaireItem)
        val localizedFlyoverSpanned =
          questionnaireViewItem.questionnaireItem.localizedFlyoverSpanned
        flyOverTextView.apply {
          visibility =
            if (localizedFlyoverSpanned.isNullOrEmpty()) {
              GONE
            } else {
              VISIBLE
            }
          text = localizedFlyoverSpanned
        }

        answerTextView.apply {
          visibility =
            when (questionnaireViewItem.questionnaireItem.type) {
              Questionnaire.QuestionnaireItemType.GROUP,
              Questionnaire.QuestionnaireItemType.DISPLAY -> GONE
              else -> VISIBLE
            }
          text = questionnaireViewItem.answerString(context)
        }

        divider.visibility =
          if (header.visibility == VISIBLE ||
              flyOverTextView.visibility == VISIBLE ||
              answerTextView.visibility == VISIBLE
          ) {
            VISIBLE
          } else {
            GONE
          }
      }

      override fun setReadOnly(isReadOnly: Boolean) {}
    }
}
