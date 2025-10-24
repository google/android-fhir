/*
 * Copyright 2025 Google LLC
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

package com.google.android.fhir.datacapture.views.compose

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class DateVisualTransformation(
  val dateInputFormat: DateInputFormat,
) : VisualTransformation {

  private val firstDelimiterOffset: Int =
    dateInputFormat.patternWithDelimiters.indexOf(dateInputFormat.delimiter)
  private val secondDelimiterOffset: Int =
    dateInputFormat.patternWithDelimiters.lastIndexOf(dateInputFormat.delimiter)
  private val dateFormatLength: Int = dateInputFormat.patternWithoutDelimiters.length

  private val dateOffsetTranslator =
    object : OffsetMapping {

      override fun originalToTransformed(offset: Int): Int {
        return when {
          firstDelimiterOffset == -1 -> offset
          offset < firstDelimiterOffset -> offset
          offset < secondDelimiterOffset -> offset + 1
          offset <= dateFormatLength -> offset + 2
          else -> dateFormatLength + 2 // 10
        }
      }

      override fun transformedToOriginal(offset: Int): Int {
        return when {
          firstDelimiterOffset == -1 -> offset
          offset <= firstDelimiterOffset - 1 -> offset
          offset <= secondDelimiterOffset - 1 -> offset - 1
          offset <= dateFormatLength + 1 -> offset - 2
          else -> dateFormatLength // 8
        }
      }
    }

  override fun filter(text: AnnotatedString): TransformedText {
    val trimmedText =
      if (text.text.length > dateFormatLength) {
        text.text.substring(0 until dateFormatLength)
      } else {
        text.text
      }
    var transformedText = ""
    trimmedText.forEachIndexed { index, char ->
      transformedText += char
      if (index + 1 == firstDelimiterOffset || index + 2 == secondDelimiterOffset) {
        transformedText += dateInputFormat.delimiter
      }
    }
    return TransformedText(AnnotatedString(transformedText), dateOffsetTranslator)
  }
}

data class DateInputFormat(val patternWithDelimiters: String, val delimiter: Char) {
  val patternWithoutDelimiters: String = patternWithDelimiters.replace(delimiter.toString(), "")
}
