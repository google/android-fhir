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
import android.text.Spanned
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.hasHelpButton
import com.google.android.fhir.datacapture.localizedHelpSpanned
import com.google.android.fhir.datacapture.localizedInstructionsSpanned
import com.google.android.fhir.datacapture.localizedPrefixSpanned
import com.google.android.fhir.datacapture.localizedTextSpanned
import com.google.android.material.card.MaterialCardView
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
  private var errorTextView: TextView = findViewById(R.id.error_text_at_header)

  fun bind(questionnaireItem: Questionnaire.QuestionnaireItemComponent) {
    prefix.updateTextAndVisibility(questionnaireItem.localizedPrefixSpanned)
    question.updateTextAndVisibility(questionnaireItem.localizedTextSpanned)
    hint.updateTextAndVisibility(questionnaireItem.localizedInstructionsSpanned)
    initHelpButton(this, questionnaireItem)
    //   Make the entire view GONE if there is nothing to show. This is to avoid an empty row in the
    // questionnaire.
    visibility = getViewGroupVisibility(prefix, question, hint)
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

internal fun TextView.updateTextAndVisibility(localizedText: Spanned? = null) {
  text = localizedText
  visibility =
    if (localizedText.isNullOrEmpty()) {
      GONE
    } else {
      VISIBLE
    }
}

/** Returns [VISIBLE] if any of the [view] is visible, else returns [GONE]. */
internal fun getViewGroupVisibility(vararg view: TextView): Int {
  if (view.any { it.visibility == VISIBLE }) {
    return VISIBLE
  }
  return GONE
}

internal fun initHelpButton(
  view: View,
  questionnaireItem: Questionnaire.QuestionnaireItemComponent
) {
  val helpButton = view.findViewById<Button>(R.id.helpButton)
  helpButton.visibility =
    if (questionnaireItem.hasHelpButton) {
      VISIBLE
    } else {
      GONE
    }
  val helpCardView = view.findViewById<MaterialCardView>(R.id.helpCardView)
  var isHelpCardViewVisible = false
  helpButton.setOnClickListener {
    if (isHelpCardViewVisible) {
      isHelpCardViewVisible = false
      helpCardView.visibility = GONE
    } else {
      isHelpCardViewVisible = true
      helpCardView.visibility = VISIBLE
    }
  }

  view
    .findViewById<TextView>(R.id.helpText)
    .updateTextAndVisibility(questionnaireItem.localizedHelpSpanned)
}
