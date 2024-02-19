/*
 * Copyright 2022-2023 Google LLC
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

import android.icu.text.DateFormat
import com.google.android.fhir.datacapture.views.factories.length
import com.google.android.fhir.datacapture.views.factories.localDate
import java.lang.Character.isLetter
import java.text.ParseException
import java.time.LocalDate
import java.time.ZoneId
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.util.Date
import java.util.Locale

/**
 * Returns the first character that is not a letter in the given date pattern string (e.g. "/" for
 * "dd/mm/yyyy") otherwise null.
 */
internal fun getDateSeparator(localeDatePattern: String): Char? =
  localeDatePattern.filterNot { isLetter(it) }.firstOrNull()

/**
 * Converts date pattern to acceptable date pattern where 2 digits are expected for day(dd) and
 * month(MM) and 4 digits are expected for year(yyyy), e.g., dd/mm/yyyy is returned for d/M/yy"
 */
internal fun canonicalizeDatePattern(datePattern: String): String {
  val datePatternSeparator = getDateSeparator(datePattern)
  var hasDay = false
  var hasMonth = false
  var hasYear = false
  val newDatePattern = StringBuilder()
  datePattern.lowercase().forEach {
    when (it) {
      'd' -> {
        if (!hasDay) {
          newDatePattern.append("dd")
          hasDay = true
        }
      }
      'm' -> {
        if (!hasMonth) {
          newDatePattern.append("MM")
          hasMonth = true
        }
      }
      'y' -> {
        if (!hasYear) {
          newDatePattern.append("yyyy")
          hasYear = true
        }
      }
      datePatternSeparator -> {
        newDatePattern.append(datePatternSeparator)
      }
      else -> {}
    }
  }
  return newDatePattern.toString()
}

/**
 * Parses a date string using the given date pattern, or the default date pattern for the device
 * locale. If the parsing fails, an exception is thrown. These exceptions are caught in the calling
 * widgets.
 */
internal fun parseDate(text: String, datePattern: String): LocalDate {
  val localDate =
    if (datePattern.isNotEmpty()) {
      LocalDate.parse(text, DateTimeFormatter.ofPattern(datePattern))
    } else {
      DateFormat.getDateInstance(DateFormat.SHORT).apply { isLenient = false }.parse(text).localDate
    }

  // Throw ParseException if year is less than 4 digits.
  if (localDate.year.length() < 4) {
    throw ParseException("Year has less than 4 digits.", text.indexOf('y'))
  }
  // date/localDate with year more than 4 digit throws data format exception if deep copy
  // operation get performed on QuestionnaireResponse,
  // QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent in org.hl7.fhir.r4.model
  // e.g ca.uhn.fhir.parser.DataFormatException: Invalid date/time format: "19843-12-21":
  // Expected character '-' at index 4 but found 3
  if (localDate.year.length() > 4) {
    throw ParseException("Year has more than 4 digits.", text.indexOf('y'))
  }
  return localDate
}

/**
 * Returns the local date string using the provided date pattern, or the default date pattern for
 * the system locale if no date pattern is provided.
 */
internal fun LocalDate.format(pattern: String? = null): String {
  return if (pattern.isNullOrEmpty()) {
    val date = Date.from(atStartOfDay(ZoneId.systemDefault())?.toInstant())
    return DateFormat.getDateInstance(DateFormat.SHORT).format(date)
  } else {
    DateTimeFormatter.ofPattern(pattern).format(this)
  }
}

/**
 * Medium and long format styles use alphabetical month names which are difficult for the user to
 * input. Use short format style which is always numerical.
 */
internal fun getLocalizedDatePattern(): String {
  return DateTimeFormatterBuilder.getLocalizedDateTimePattern(
    FormatStyle.SHORT,
    null,
    IsoChronology.INSTANCE,
    Locale.getDefault(),
  )
}
