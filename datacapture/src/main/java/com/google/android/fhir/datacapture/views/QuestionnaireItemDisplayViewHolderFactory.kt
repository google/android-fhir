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
import com.google.android.fhir.datacapture.localizedPrefixSpanned
import com.google.android.fhir.datacapture.localizedTextSpanned
import com.google.android.fhir.datacapture.validation.ValidationResult

internal object QuestionnaireItemDisplayViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_display_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var prefixTextView: TextView
      private lateinit var displayTextView: TextView
      override lateinit var questionnaireItemViewItem: QuestionnaireItemViewItem

      override fun init(itemView: View) {
        prefixTextView = itemView.findViewById(R.id.prefix_text_view)
        displayTextView = itemView.findViewById(R.id.display_text_view)
      }

      override fun bind(
        questionnaireItemViewItem: QuestionnaireItemViewItem,
        isRepeatGroup: Boolean
      ) {
        if (!questionnaireItemViewItem.questionnaireItem.prefix.isNullOrEmpty()) {
          prefixTextView.visibility = View.VISIBLE
          prefixTextView.text = questionnaireItemViewItem.questionnaireItem.localizedPrefixSpanned
        } else {
          prefixTextView.visibility = View.GONE
        }
        displayTextView.text = questionnaireItemViewItem.questionnaireItem.localizedTextSpanned

        displayTextView.visibility =
          if (displayTextView.text.isEmpty()) {
            View.GONE
          } else {
            View.VISIBLE
          }
      }

      override fun displayValidationResult(validationResult: ValidationResult) {
        // Display type questions have no user input to be validated
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        // Display type questions have no user input
      }
    }
}
