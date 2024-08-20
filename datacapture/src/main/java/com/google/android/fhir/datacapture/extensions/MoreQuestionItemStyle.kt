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
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.fhir.datacapture.R

/**
 * Applies either a custom style or a default style to the given view based on the provided custom
 * style name and default style resource ID.
 *
 * If the custom style resource name is valid, it applies the custom style to the view. If the
 * custom style resource name is not valid or not found, it falls back to applying the default style
 * defined by the given style resource ID.
 *
 * @param context the context used to access resources.
 * @param view the view to which the style should be applied.
 * @param customStyleName the name of the custom style to apply.
 * @param defaultStyleResId the default style resource ID to use if no custom style is found.
 */
internal fun applyCustomOrDefaultStyle(
  context: Context,
  view: View,
  customStyleName: String?,
  defaultStyleResId: Int,
) {
  val customStyleResId = customStyleName?.let { getStyleResIdByName(context, it) } ?: 0
  when {
    customStyleResId != 0 -> {
      view.tag = customStyleResId
      QuestionItemCustomStyle().applyStyle(context, view, customStyleResId)
    }
    defaultStyleResId != 0 -> {
      applyDefaultStyleIfNotApplied(context, view, defaultStyleResId)
    }
  }
}

/**
 * Applies the default style to the given view if the default style has not already been applied.
 *
 * This function checks the `view`'s tag to determine if a style has been previously applied. If the
 * tag is an integer, it will apply the default style specified by `defaultStyleResId`. After
 * applying the style, it resets the view's tag to `null` to indicate that the default style has
 * been applied.
 *
 * @param context The context used to access resources and themes.
 * @param view The view to which the default style will be applied.
 * @param defaultStyleResId The resource ID of the default style to apply.
 */
private fun applyDefaultStyleIfNotApplied(
  context: Context,
  view: View,
  defaultStyleResId: Int,
) {
  (view.tag as? Int)?.let {
    QuestionItemDefaultStyle().applyStyle(context, view, defaultStyleResId)
    view.tag = null
  }
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
private fun getStyleResIdByName(context: Context, styleName: String): Int {
  return context.resources.getIdentifier(styleName, "style", context.packageName)
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
internal fun getStyleResIdFromAttribute(context: Context, attr: Int): Int {
  val typedArray = context.theme.obtainStyledAttributes(intArrayOf(attr))
  val styleResId = typedArray.getResourceId(0, 0)
  typedArray.recycle()
  return styleResId
}

internal abstract class QuestionItemStyle {

  /**
   * Applies a style to a view.
   *
   * @param context The context used to apply the style.
   * @param view The view to which the style will be applied.
   * @param styleResId The resource ID of the style to apply.
   */
  abstract fun applyStyle(context: Context, view: View, styleResId: Int)

  /**
   * Applies the style from a TypedArray to a view.
   *
   * @param context The context used to apply the style.
   * @param view The view to which the style will be applied.
   * @param typedArray The TypedArray containing the style attributes.
   */
  internal fun applyStyle(context: Context, view: View, typedArray: TypedArray) {
    applyGenericViewStyle(context, view, typedArray)
    if (view is TextView) {
      applyTextViewSpecificStyle(view, typedArray)
    }
    typedArray.recycle()
  }

  /**
   * Abstract function to apply generic view styles from a TypedArray.
   *
   * @param context The context used to apply the style.
   * @param view The view to which the style will be applied.
   * @param typedArray The TypedArray containing the style attributes.
   */
  abstract fun applyGenericViewStyle(context: Context, view: View, typedArray: TypedArray)

  /**
   * Abstract function to apply TextView-specific styles from a TypedArray.
   *
   * @param textView The TextView to which the style will be applied.
   * @param typedArray The TypedArray containing the style attributes.
   */
  abstract fun applyTextViewSpecificStyle(textView: TextView, typedArray: TypedArray)

  /**
   * Applies the background color from a TypedArray to a view.
   *
   * @param context The context used to apply the background color.
   * @param view The view to which the background color will be applied.
   * @param typedArray The TypedArray containing the background color attribute.
   * @param index The index of the background color attribute in the TypedArray.
   */
  protected fun applyBackgroundColor(
    context: Context,
    view: View,
    typedArray: TypedArray,
    index: Int,
  ) {
    val backgroundColor =
      typedArray.getColor(index, ContextCompat.getColor(context, android.R.color.transparent))
    view.setBackgroundColor(backgroundColor)
  }

  /**
   * Applies the text appearance from a TypedArray to a TextView.
   *
   * @param textView The TextView to which the text appearance will be applied.
   * @param typedArray The TypedArray containing the text appearance attribute.
   * @param index The index of the text appearance attribute in the TypedArray.
   */
  protected fun applyTextAppearance(textView: TextView, typedArray: TypedArray, index: Int) {
    val textAppearance = typedArray.getResourceId(index, -1)
    if (textAppearance != -1) {
      textView.setTextAppearance(textAppearance)
    }
  }
}

internal class QuestionItemCustomStyle : QuestionItemStyle() {
  private enum class CustomStyleViewAttributes(val attrId: Int) {
    TEXT_APPEARANCE(R.styleable.QuestionnaireCustomStyle_questionnaire_textAppearance),
    BACKGROUND(R.styleable.QuestionnaireCustomStyle_questionnaire_background),
  }

  override fun applyStyle(context: Context, view: View, styleResId: Int) {
    val typedArray =
      context.obtainStyledAttributes(styleResId, R.styleable.QuestionnaireCustomStyle)
    applyStyle(context, view, typedArray)
  }

  override fun applyGenericViewStyle(context: Context, view: View, typedArray: TypedArray) {
    for (i in 0 until typedArray.indexCount) {
      when (typedArray.getIndex(i)) {
        CustomStyleViewAttributes.BACKGROUND.attrId -> {
          applyBackgroundColor(context, view, typedArray, i)
        }
      }
    }
  }

  override fun applyTextViewSpecificStyle(
    textView: TextView,
    typedArray: TypedArray,
  ) {
    for (i in 0 until typedArray.indexCount) {
      when (typedArray.getIndex(i)) {
        CustomStyleViewAttributes.TEXT_APPEARANCE.attrId -> {
          applyTextAppearance(textView, typedArray, i)
        }
      }
    }
  }
}

internal class QuestionItemDefaultStyle : QuestionItemStyle() {
  private enum class DefaultStyleViewAttributes(val attrId: Int) {
    TEXT_APPEARANCE(android.R.attr.textAppearance),
    BACKGROUND(android.R.attr.background),
    // Add other attributes you want to apply
  }

  override fun applyStyle(context: Context, view: View, styleResId: Int) {
    val attrs = DefaultStyleViewAttributes.values().map { it.attrId }.toIntArray()
    val typedArray: TypedArray = context.obtainStyledAttributes(styleResId, attrs)
    applyStyle(context, view, typedArray)
  }

  override fun applyGenericViewStyle(context: Context, view: View, typedArray: TypedArray) {
    for (i in 0 until typedArray.indexCount) {
      when (DefaultStyleViewAttributes.values()[i]) {
        DefaultStyleViewAttributes.BACKGROUND -> {
          applyBackgroundColor(context, view, typedArray, i)
        }
        else -> {
          // Ignore view specific attributes.
        }
      }
    }
  }

  override fun applyTextViewSpecificStyle(
    textView: TextView,
    typedArray: TypedArray,
  ) {
    for (i in 0 until typedArray.indexCount) {
      when (DefaultStyleViewAttributes.values()[i]) {
        DefaultStyleViewAttributes.TEXT_APPEARANCE -> {
          applyTextAppearance(textView, typedArray, i)
        }
        else -> {
          // applyGenericViewDefaultStyle for other attributes.
        }
      }
    }
  }
}
