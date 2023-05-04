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

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.views.QuestionnaireViewItem
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

/**
 * Appends the optional text [R.string.optional_text] to the [localizedText], and assign the
 * resulting text to the [textView].
 */
internal fun appendOptionalText(textView: TextView, localizedText: Spanned? = null) {
  val builder = SpannableStringBuilder()
  localizedText?.let { builder.append(it) }
  builder.appendWithSpan(
    textView.context.applicationContext.getString(R.string.optional_text),
    textView.context.getColorFromAttr(R.attr.colorOutline)
  )
  textView.updateTextAndVisibility(builder)
}

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

/**
 * Updates textview [R.id.question] with
 * [Questionnaire.QuestionnaireItemComponent.localizedTextSpanned] text and `*` if
 * [Questionnaire.QuestionnaireItemComponent.required] is true.
 */
internal fun appendAsteriskToQuestionText(
  questionTextView: TextView,
  questionnaireViewItem: QuestionnaireViewItem
) {
  val builder = SpannableStringBuilder()
  questionnaireViewItem.questionnaireItem.localizedTextSpanned?.let {
    builder.append(questionnaireViewItem.questionnaireItem.localizedTextSpanned)
  }
  if (questionnaireViewItem.showAsterisk &&
      questionnaireViewItem.questionnaireItem.required &&
      !questionnaireViewItem.questionnaireItem.localizedTextSpanned.isNullOrEmpty()
  ) {
    builder.append(questionTextView.context.applicationContext.getString(R.string.space_asterisk))
  }
  questionTextView.updateTextAndVisibility(builder)
}

private fun SpannableStringBuilder.appendWithSpan(value: String, @ColorInt color: Int) {
  val start = length
  append(value)
  val end = length
  setSpan(ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}

@ColorInt
private fun Context.getColorFromAttr(@AttrRes attrColor: Int): Int {
  val typedValue = TypedValue()
  theme.resolveAttribute(attrColor, typedValue, true)
  return typedValue.data
}
