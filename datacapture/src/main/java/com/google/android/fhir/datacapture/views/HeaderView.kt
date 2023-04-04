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
import com.google.android.fhir.datacapture.extensions.localizedTextSpanned
import com.google.android.fhir.datacapture.extensions.updateTextAndVisibility

/** View for the prefix, question, and hint of a questionnaire item. */
internal class HeaderView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

  init {
    LayoutInflater.from(context).inflate(R.layout.header_view, this, true)
  }

  private val prefix = findViewById<TextView>(R.id.prefix)
  private val question = findViewById<TextView>(R.id.question)
  private val hint = findViewById<TextView>(R.id.hint)
  private val errorTextView = findViewById<TextView>(R.id.error_text_at_header)

  fun bind(questionnaireViewItem: QuestionnaireViewItem) {
    initHelpViews(
      helpButton = findViewById(R.id.helpButton),
      helpCardView = findViewById(R.id.helpCardView),
      helpTextView = findViewById(R.id.helpText),
      questionnaireItem = questionnaireViewItem.questionnaireItem
    )
    prefix.updateTextAndVisibility(questionnaireViewItem.questionnaireItem.localizedPrefixSpanned)
    question.updateTextAndVisibility(questionnaireViewItem.questionnaireItem.localizedTextSpanned)
    hint.updateTextAndVisibility(
      questionnaireViewItem.enabledDisplayItems.localizedInstructionsSpanned
    )
    // Make the entire view GONE if there is nothing to show. This is to avoid an empty row in the
    // questionnaire.
    visibility = getHeaderViewVisibility(prefix, question, hint)
  }

  /**
   * Shows an error in the header,and widgets could either use this or use another view (i.e.
   * TextInputLayout's error field) to display error.
   */
  fun showErrorText(errorText: String? = null, isErrorTextVisible: Boolean = true) {
    errorTextView.visibility =
      when (isErrorTextVisible) {
        true -> {
          VISIBLE
        }
        false -> {
          GONE
        }
      }
    errorTextView.text = errorText
  }
}
