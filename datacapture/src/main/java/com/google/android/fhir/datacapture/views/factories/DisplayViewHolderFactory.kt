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
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.views.HeaderView
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem

internal object DisplayViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.display_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var header: HeaderView
      override lateinit var questionnaireViewItem: QuestionnaireViewItem

      override fun init(itemView: View) {
        header = itemView.findViewById(R.id.header)
      }

      override fun bind(questionnaireViewItem: QuestionnaireViewItem) {
        header.bind(questionnaireViewItem)
      }

      override fun setReadOnly(isReadOnly: Boolean) {
        // Display type questions have no user input
      }
    }
}
