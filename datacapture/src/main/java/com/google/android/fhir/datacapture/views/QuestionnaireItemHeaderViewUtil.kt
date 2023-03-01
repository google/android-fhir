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

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.TextView
import com.google.android.fhir.datacapture.hasHelpButton
import com.google.android.fhir.datacapture.localizedHelpSpanned
import com.google.android.fhir.datacapture.localizedTextSpanned
import com.google.android.material.card.MaterialCardView
import org.hl7.fhir.r4.model.Questionnaire

/**
 * Updates textview [questionTextView] with
 * [Questionnaire.QuestionnaireItemComponent.localizedTextSpanned] text and `*` if
 * [Questionnaire.QuestionnaireItemComponent.required] is true.
 */
internal fun updateQuestionText(
  questionTextView: TextView,
  questionnaireItem: Questionnaire.QuestionnaireItemComponent,
) {
  val builder = SpannableStringBuilder()
  questionnaireItem.localizedTextSpanned?.let { builder.append(it) }
  questionTextView.updateTextAndVisibility(builder)
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
internal fun headerViewVisibility(vararg view: TextView): Int {
  if (view.any { it.visibility == VISIBLE }) {
    return VISIBLE
  }
  return GONE
}

internal fun initHelpViews(
  helpButton: Button,
  helpCardView: MaterialCardView,
  helpTextView: TextView,
  questionnaireItem: Questionnaire.QuestionnaireItemComponent
) {
  helpButton.visibility =
    if (questionnaireItem.hasHelpButton) {
      VISIBLE
    } else {
      GONE
    }
  helpButton.setOnClickListener {
    helpCardView.visibility =
      when (helpCardView.visibility) {
        VISIBLE -> GONE
        else -> VISIBLE
      }
  }
  helpTextView.updateTextAndVisibility(questionnaireItem.localizedHelpSpanned)
}
