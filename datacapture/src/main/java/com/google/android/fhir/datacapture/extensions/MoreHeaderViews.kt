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

import android.graphics.Color
import android.graphics.PorterDuff
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.fhir.datacapture.R
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

internal fun TextView.setIconifiedText(localizedPrefix: Spanned? = null,
                                       localizedText: Spanned? = null, @DrawableRes iconResId: Int,
                              helpCardView: MaterialCardView,
                              helpTextView: TextView,
                              questionnaireItem: Questionnaire.QuestionnaireItemComponent) {
  text = if(!localizedPrefix.isNullOrEmpty() && !localizedText.isNullOrEmpty())
    TextUtils.concat(localizedPrefix, localizedText)
    else localizedText
  visibility =
    if (text.isNullOrEmpty() && !questionnaireItem.hasHelpButton) {
      GONE
    } else {
      VISIBLE
    }
  val clickableSpan = object : ClickableSpan() {
    override fun onClick(widget: View) {
      helpCardView.visibility =
        when (helpCardView.visibility) {
          VISIBLE -> GONE
          else -> VISIBLE
        }
    }
  }
  SpannableStringBuilder("$text#").apply {
    val attrs = context.obtainStyledAttributes(intArrayOf(R.attr.questionnaireHelpButtonStyle))
    val helpButtonStyleResId = attrs.getResourceId(0, 0)

// Retrieve the color defined in questionnaireHelpButtonStyle attribute
    val helpButtonStyleAttrs = context.obtainStyledAttributes(helpButtonStyleResId, intArrayOf(R.attr.tint))
    val helpButtonColor = helpButtonStyleAttrs.getColor(0, Color.BLACK)

// Create a new ImageSpan and apply the color to it
    ContextCompat.getDrawable(context, R.drawable.ic_help_48px).apply {
      this!!.setTint(helpButtonColor)
    }
    val imageSpan = ImageSpan(context,R.drawable.ic_help_48px, DynamicDrawableSpan.ALIGN_CENTER)

    setSpan(
      imageSpan,
      text.length,
      text.length + 1,
      Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    setSpan(clickableSpan,
      text.length,
      text.length + 1,
      Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
  }.let {
    text = it
    movementMethod = LinkMovementMethod.getInstance()
  }

  helpTextView.updateTextAndVisibility(questionnaireItem.localizedHelpSpanned)
}

fun setSpan(clickableSpan: ClickableSpan) {

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
//  helpButton.visibility =
//    if (questionnaireItem.hasHelpButton) {
//      VISIBLE
//    } else {
//      GONE
//    }
  helpButton.setOnClickListener {
    helpCardView.visibility =
      when (helpCardView.visibility) {
        VISIBLE -> GONE
        else -> VISIBLE
      }
  }
  helpTextView.updateTextAndVisibility(questionnaireItem.localizedHelpSpanned)
}
