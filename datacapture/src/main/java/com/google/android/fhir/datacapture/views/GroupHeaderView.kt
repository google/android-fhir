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
import com.google.android.fhir.datacapture.localizedInstructionsSpanned
import com.google.android.fhir.datacapture.localizedPrefixSpanned
import com.google.android.fhir.datacapture.localizedTextSpanned
import org.hl7.fhir.r4.model.Questionnaire

internal class GroupHeaderView(context: Context, attrs: AttributeSet?) :
  LinearLayout(context, attrs) {

  init {
    LayoutInflater.from(context).inflate(R.layout.group_type_header_view, this, true)
  }

  fun bind(questionnaireItem: Questionnaire.QuestionnaireItemComponent) {
    val prefix = findViewById<TextView>(R.id.prefix)
    val question = findViewById<TextView>(R.id.question)
    val hint = findViewById<TextView>(R.id.hint)
    initHelpButton(this, questionnaireItem)
    prefix.updateTextAndVisibility(questionnaireItem.localizedPrefixSpanned)
    question.updateTextAndVisibility(questionnaireItem.localizedTextSpanned)
    hint.updateTextAndVisibility(questionnaireItem.localizedInstructionsSpanned)
    visibility = getViewGroupVisibility(prefix, question, hint)
  }
}
