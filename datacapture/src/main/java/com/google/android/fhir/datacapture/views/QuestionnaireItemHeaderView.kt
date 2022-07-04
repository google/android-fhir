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
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.localizedHintSpanned
import com.google.android.fhir.datacapture.localizedPrefixSpanned
import com.google.android.fhir.datacapture.localizedTextSpanned
import org.hl7.fhir.r4.model.Questionnaire

/** View for the prefix, question, and hint of a questionnaire item. */
internal class QuestionnaireItemHeaderView(context: Context, attrs: AttributeSet?) :
  LinearLayout(context, attrs) {

  init {
    LayoutInflater.from(context).inflate(R.layout.questionnaire_item_header, this, true)
  }

  private var prefix: TextView = findViewById(R.id.prefix)
  private var question: TextView = findViewById(R.id.question)
  private var hint: TextView = findViewById(R.id.hint)

  fun bind(questionnaireItem: Questionnaire.QuestionnaireItemComponent) {
    val localizedPrefixSpanned = questionnaireItem.localizedPrefixSpanned
    prefix.visibility =
      if (localizedPrefixSpanned.isNullOrEmpty()) {
        View.GONE
      } else {
        View.VISIBLE
      }
    prefix.text = localizedPrefixSpanned

    val localizedTextSpanned = questionnaireItem.localizedTextSpanned
    question.visibility =
      if (localizedTextSpanned.isNullOrEmpty()) {
        View.GONE
      } else {
        View.VISIBLE
      }
    question.text = localizedTextSpanned

    val localizedHintSpanned = questionnaireItem.localizedHintSpanned
    hint.visibility =
      if (localizedHintSpanned.isNullOrEmpty()) {
        View.GONE
      } else {
        View.VISIBLE
      }
    hint.text = localizedHintSpanned
    //   Make the entire view GONE if there is nothing to show. This is to avoid an empty row in the
    // questionnaire.
    visibility =
      if (question.visibility == VISIBLE ||
          prefix.visibility == VISIBLE ||
          hint.visibility == VISIBLE
      ) {
        VISIBLE
      } else {
        GONE
      }
  }
}
