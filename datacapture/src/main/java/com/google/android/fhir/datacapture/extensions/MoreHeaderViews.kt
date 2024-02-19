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

package com.google.android.fhir.datacapture.extensions

import android.text.Spanned
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import org.hl7.fhir.r4.model.Questionnaire

internal fun TextView.updateTextAndVisibility(localizedText: Spanned? = null) {
  text = localizedText
  visibility =
    if (localizedText.isNullOrEmpty()) {
      GONE
    } else {
      VISIBLE
    }
}

/** Returns [VISIBLE] if any of the [view] is visible, [GONE] otherwise. */
internal fun getHeaderViewVisibility(vararg view: TextView): Int {
  if (view.any { it.visibility == VISIBLE }) {
    return VISIBLE
  }
  return GONE
}

/**
 * Initializes the text for [helpTextView] with instructions on how to use the feature, and sets the
 * visibility and click listener for the [helpButton] to allow users to access the help information
 * and toggles the visibility for view [helpCardView].
 */
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
