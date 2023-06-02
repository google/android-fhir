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

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.extensions.getHeaderViewVisibility
import com.google.android.fhir.datacapture.extensions.initHelpViews
import com.google.android.fhir.datacapture.extensions.localizedInstructionsSpanned
import com.google.android.fhir.datacapture.extensions.localizedPrefixSpanned
import com.google.android.fhir.datacapture.extensions.updateTextAndVisibility

internal class GroupHeaderView(context: Context, attrs: AttributeSet?) :
  LinearLayout(context, attrs) {

  init {
    LayoutInflater.from(context).inflate(R.layout.group_type_header_view, this, true)
  }

  private val prefix = findViewById<TextView>(R.id.prefix)
  private val question = findViewById<TextView>(R.id.question)
  private val hint = findViewById<TextView>(R.id.hint)

  fun bind(questionnaireViewItem: QuestionnaireViewItem) {
    initHelpViews(
      helpButton = findViewById(R.id.helpButton),
      helpCardView = findViewById(R.id.helpCardView),
      helpTextView = findViewById(R.id.helpText),
      questionnaireItem = questionnaireViewItem.questionnaireItem
    )
    prefix.updateTextAndVisibility(questionnaireViewItem.questionnaireItem.localizedPrefixSpanned)
    // CQF expression takes precedence over static question text
    question.updateTextAndVisibility(questionnaireViewItem.questionText)
    hint.updateTextAndVisibility(
      questionnaireViewItem.enabledDisplayItems.localizedInstructionsSpanned
    )
    visibility = getHeaderViewVisibility(prefix, question, hint)
  }
}
