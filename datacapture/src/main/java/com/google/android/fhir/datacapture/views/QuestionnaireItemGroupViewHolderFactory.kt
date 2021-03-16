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
import android.widget.TextView
import com.google.android.fhir.datacapture.R

internal object QuestionnaireItemGroupViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_group_header_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var groupHeader: TextView
      private lateinit var prefix: TextView

      override fun init(itemView: View) {
        groupHeader = itemView.findViewById(R.id.group_header)
        prefix = itemView.findViewById(R.id.prefix)
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        groupHeader.text = questionnaireItemViewItem.questionnaireItem.text.value
        groupHeader.visibility =
          if (groupHeader.text.isEmpty()) {
            View.GONE
          } else {
            View.VISIBLE
          }
        if (questionnaireItemViewItem.questionnaireItem.prefix.toString().isNotEmpty()) {
          prefix.visibility = View.VISIBLE
          prefix.text = questionnaireItemViewItem.questionnaireItem.prefix.value
        }
      }
    }
}
