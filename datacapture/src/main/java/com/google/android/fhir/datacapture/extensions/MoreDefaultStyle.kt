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

import android.content.Context
import android.content.res.TypedArray
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat

enum class DefaultStyleViewAttributes(val attrId: Int) {
  TEXT_SIZE(android.R.attr.textSize),
  TEXT_COLOR(android.R.attr.textColor),
  BACKGROUND(android.R.attr.background),
  // Add other attributes you want to apply
}

/**
 * Retrieves the style resource ID associated with a specific attribute from the current theme.
 *
 * This function obtains the style resource ID that is linked to a given attribute in the current
 * theme. It uses the `obtainStyledAttributes` method to fetch the attributes and extract the
 * resource ID.
 *
 * @param context The context to access the current theme and resources.
 * @param attr The attribute whose associated style resource ID is to be retrieved.
 * @return The resource ID of the style associated with the specified attribute, or 0 if not found.
 */
internal fun getStyleResIdFromTheme(context: Context, attr: Int): Int {
  val typedArray = context.theme.obtainStyledAttributes(intArrayOf(attr))
  val styleResId = typedArray.getResourceId(0, 0)
  typedArray.recycle()
  return styleResId
}

/**
 * Helper function to apply a style programmatically to a View.
 *
 * @param context The context used to access resources.
 * @param view The View to which the style should be applied.
 * @param styleResId The resource ID of the style to apply.
 */
fun applyDefaultStyleToView(context: Context, view: View, styleResId: Int) {
  (view.tag as? Int)?.let {
    val attrs = DefaultStyleViewAttributes.values().map { it.attrId }.toIntArray()
    val typedArray: TypedArray = context.obtainStyledAttributes(styleResId, attrs)
    applyGenericViewDefaultStyle(context, view, typedArray)
    if (view is TextView) {
      applyTextViewDefaultStyle(context, view, typedArray)
    }
    typedArray.recycle()
  }
}

private fun applyGenericViewDefaultStyle(context: Context, view: View, typedArray: TypedArray) {
  for (i in 0 until typedArray.indexCount) {
    when (DefaultStyleViewAttributes.values()[i]) {
      DefaultStyleViewAttributes.BACKGROUND -> {
        val backgroundColor =
          typedArray.getColor(i, ContextCompat.getColor(context, android.R.color.transparent))
        view.setBackgroundColor(backgroundColor)
      }
      else -> {
        // Ignore other attributes for generic View
      }
    }
  }
}

private fun applyTextViewDefaultStyle(
  context: Context,
  textView: TextView,
  typedArray: TypedArray,
) {
  for (i in 0 until typedArray.indexCount) {
    when (DefaultStyleViewAttributes.values()[i]) {
      DefaultStyleViewAttributes.TEXT_SIZE -> {
        val textSize = typedArray.getDimension(i, textView.textSize)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
      }
      DefaultStyleViewAttributes.TEXT_COLOR -> {
        val textColor =
          typedArray.getColor(i, ContextCompat.getColor(context, android.R.color.black))
        textView.setTextColor(textColor)
      }
      else -> {
        // Ignore other attributes for TextView
      }
    }
  }
}
