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
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.hasHelpButton
import com.google.android.fhir.datacapture.localizedHelpSpanned
import com.google.android.fhir.datacapture.localizedTextSpanned
import com.google.android.material.card.MaterialCardView
import org.hl7.fhir.r4.model.Questionnaire

/**
 * Updates textview [R.id.question] with
 * [Questionnaire.QuestionnaireItemComponent.localizedTextSpanned] text and `*` if
 * [Questionnaire.QuestionnaireItemComponent.required] is true. And applies [R.attr.colorError] to
 * `*`.
 */
internal fun updateQuestionText(
  questionTextView: TextView,
  questionnaireItem: Questionnaire.QuestionnaireItemComponent,
) {
  val builder = SpannableStringBuilder()
  questionnaireItem.localizedTextSpanned?.let { builder.append(it) }
  if (questionnaireItem.required) {
    builder.appendWithSpan(
      questionTextView.context.applicationContext.getString(R.string.space_asterisk),
      questionTextView.context.getColorFromAttr(R.attr.colorError)
    )
  }
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

internal fun initHelpButton(
  helpButton: Button,
  helpCardView: MaterialCardView,
  helpText: TextView,
  questionnaireItem: Questionnaire.QuestionnaireItemComponent
) {
  helpButton.visibility =
    if (questionnaireItem.hasHelpButton) {
      VISIBLE
    } else {
      GONE
    }
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
  helpText.updateTextAndVisibility(questionnaireItem.localizedHelpSpanned)
}

private fun SpannableStringBuilder.appendWithSpan(value: String, @ColorInt color: Int) {
  val start = length
  append(value)
  val end = length
  setSpan(ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}

/**
 * Reads @ColorInt value from android resource attribute. e.g [attrColor] value is
 * [R.attr.colorError] to get the colorInt.
 */
@ColorInt
internal fun Context.getColorFromAttr(@AttrRes attrColor: Int): Int {
  val typedValue = TypedValue()
  theme.resolveAttribute(attrColor, typedValue, true)
  return typedValue.data
}
