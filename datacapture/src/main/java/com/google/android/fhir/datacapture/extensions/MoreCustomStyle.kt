/*
 * Copyright 2024 Google LLC
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
import android.content.res.TypedArray
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.fhir.datacapture.R

enum class CustomStyleViewAttributes(val attrId: Int) {
  TEXT_COLOR(R.styleable.QuestionnaireCustomStyle_android_textColor),
  BACKGROUND(R.styleable.QuestionnaireCustomStyle_android_background),
  TEXT_SIZE(R.styleable.QuestionnaireCustomStyle_android_textSize),
}

/**
 * Retrieves the resource ID of a style given its name.
 *
 * This function uses the `getIdentifier` method to look up the style resource ID based on the style
 * name provided. If the style name is not found, it returns 0.
 *
 * @param context The context used to access resources.
 * @param styleName The name of the style whose resource ID is to be retrieved.
 * @return The resource ID of the style, or 0 if the style name is not found.
 */
internal fun getStyleResIdByName(context: Context, styleName: String): Int {
  return context.resources.getIdentifier(styleName, "style", context.packageName)
}

internal fun getTypedArrayForQuestionnaireCustomStyle(
  context: Context,
  styleResId: Int,
): TypedArray {
  return context.obtainStyledAttributes(styleResId, R.styleable.QuestionnaireCustomStyle)
}

/**
 * Applies custom styles to a view based on its type.
 *
 * This function first applies common style attributes that can be applied to any view, and then
 * applies attributes specific to `TextView` if the view is an instance of `TextView`.
 *
 * @param context The context to access resources and theme.
 * @param view The view to which the style should be applied.
 * @param styleResId The resource ID of the style to be applied.
 */
internal fun applyCustomStyleBasedOnViewType(context: Context, view: View, styleResId: Int) {
  val typedArray = getTypedArrayForQuestionnaireCustomStyle(context, styleResId)
  // Apply common style attributes
  applyGenericViewCustomStyle(context, view, typedArray)
  // Apply TextView-specific attributes if the view is a TextView
  if (view is TextView) {
    applyTextViewSpecificCustomStyle(view, typedArray)
  }

  typedArray.recycle()
}

private fun applyGenericViewCustomStyle(context: Context, view: View, typedArray: TypedArray) {
  for (i in 0 until typedArray.indexCount) {
    when (typedArray.getIndex(i)) {
      CustomStyleViewAttributes.BACKGROUND.attrId -> {
        val backgroundColor =
          typedArray.getColor(i, ContextCompat.getColor(context, android.R.color.white))
        view.setBackgroundColor(backgroundColor)
      }
    }
  }
}

private fun applyTextViewSpecificCustomStyle(
  textView: TextView,
  typedArray: TypedArray,
) {
  for (i in 0 until typedArray.indexCount) {
    when (typedArray.getIndex(i)) {
      CustomStyleViewAttributes.TEXT_SIZE.attrId -> {
        val textSize = typedArray.getDimension(i, textView.textSize)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
      }
      CustomStyleViewAttributes.TEXT_COLOR.attrId -> {
        val textColor = typedArray.getColor(i, textView.currentTextColor)
        textView.setTextColor(textColor)
      }
      else -> {
        // Ignore other attributes for TextView
      }
    }
  }
}
