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
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import com.google.android.fhir.datacapture.R

internal fun applyStyle(context: Context, view: View, styleResId: Int) {
  val typedArray = context.obtainStyledAttributes(styleResId, R.styleable.QuestionnaireCustomStyle)
  for (i in 0 until typedArray.indexCount) {
    when (typedArray.getIndex(i)) {
      R.styleable.QuestionnaireCustomStyle_android_textSize -> {
        if (view is TextView) {
          val textSize =
            typedArray.getDimension(R.styleable.QuestionnaireCustomStyle_android_textSize, 16f)
          view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize)
        }
      }
      R.styleable.QuestionnaireCustomStyle_android_textColor -> {
        if (view is TextView) {
          val textColor =
            typedArray.getColor(
              R.styleable.QuestionnaireCustomStyle_android_textColor,
              view.currentTextColor,
            )
          view.setTextColor(textColor)
        }
      }
      R.styleable.QuestionnaireCustomStyle_android_background -> {
        val backgroundColor =
          typedArray.getColor(R.styleable.QuestionnaireCustomStyle_android_background, 0xFFFFFF)
        view.setBackgroundColor(backgroundColor)
      }
    }
  }
  typedArray.recycle()
}
