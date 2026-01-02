/*
 * Copyright 2022-2026 Google LLC
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

import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime

/**
 * Returns the first character that is not a letter in the given date pattern string (e.g. "/" for
 * "dd/mm/yyyy") otherwise null.
 */
internal fun getDateSeparator(localeDatePattern: String): Char? =
  localeDatePattern.filterNot { it.isLetter() }.firstOrNull()

/**
 * Converts date pattern to acceptable date pattern where 2 digits are expected for day(dd) and
 * month(MM) and 4 digits are expected for year(yyyy), e.g., dd/mm/yyyy is returned for d/M/yy
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
 *
 * TODO: Add locale-aware parsing when pattern is empty (currently uses ISO format yyyy-MM-dd)
 */
@OptIn(FormatStringsInDatetimeFormats::class)
internal fun parseDate(text: String, datePattern: String): LocalDate {
  val format =
    if (datePattern.isNotEmpty()) {
      LocalDate.Format { byUnicodePattern(datePattern) }
    } else {
      LocalDate.Formats.ISO // Default to ISO format: yyyy-MM-dd
    }

  val localDate = LocalDate.parse(text, format)

  // Validate year has exactly 4 digits
  if (localDate.year.toString().length < 4) {
    throw IllegalArgumentException("Year has less than 4 digits.")
  }
  // date/localDate with year more than 4 digit throws data format exception if deep copy
  // operation get performed on QuestionnaireResponse,
  // QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent in org.hl7.fhir.r4.model
  // e.g ca.uhn.fhir.parser.DataFormatException: Invalid date/time format: "19843-12-21":
  // Expected character '-' at index 4 but found 3
  if (localDate.year.toString().length > 4) {
    throw IllegalArgumentException("Year has more than 4 digits.")
  }
  return localDate
}

/**
 * Returns the local date string using the provided date pattern, or the default date pattern for
 * the system locale if no date pattern is provided.
 *
 * TODO: Add locale-aware formatting when pattern is null (currently uses ISO format yyyy-MM-dd)
 */
@OptIn(FormatStringsInDatetimeFormats::class)
internal fun LocalDate.format(pattern: String? = null): String {
  val format =
    if (!pattern.isNullOrEmpty()) {
      LocalDate.Format { byUnicodePattern(pattern) }
    } else {
      LocalDate.Formats.ISO // Default to ISO format: yyyy-MM-dd
    }
  return format.format(this)
}

/**
 * Medium and long format styles use alphabetical month names which are difficult for the user to
 * input. Use short format style which is always numerical.
 *
 * TODO: Add platform-specific localized date pattern detection
 *     - Android: Use DateTimeFormatterBuilder.getLocalizedDateTimePattern()
 *     - iOS: Use NSDateFormatter.dateFormat Currently returns a fixed pattern.
 */
internal fun getLocalizedDatePattern(): String {
  return "yyyy-MM-dd" // TODO: Make this locale-aware
}

internal fun parseLocalDateOrNull(dateToDisplay: String, pattern: String): LocalDate? {
  return try {
    parseDate(dateToDisplay, pattern)
  } catch (_: IllegalArgumentException) {
    null
  }
}

@OptIn(ExperimentalTime::class)
internal fun Long.toLocalDate(): LocalDate =
  Instant.fromEpochMilliseconds(this).toLocalDateTime(ZONE_ID_UTC).date

internal val ZONE_ID_UTC = TimeZone.of("UTC")
