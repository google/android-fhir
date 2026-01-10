/*
 * Copyright 2026 Google LLC
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

package com.google.android.fhir.datacapture

import co.touchlab.kermit.Logger
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime

interface LocalDateTimeFormatter {
  /**
   * Parses a date string using the given date pattern, or the default date pattern for the device
   * locale. If the parsing fails, an exception is thrown.
   */
  fun parseStringToLocalDate(str: String, pattern: String): LocalDate

  /**
   * Returns the local date string using the provided date pattern, or the default date pattern for
   * the system locale if no date pattern is provided.
   */
  fun format(localDate: LocalDate, pattern: String? = null): String

  /**
   * Medium and long format styles use alphabetical month names which are difficult for the user to
   * input. Use short format style which is always numerical.
   */
  val localDateShortFormatPattern: String

  fun localizedTimeString(time: LocalTime): String
}

internal fun LocalDateTimeFormatter.parseLocalDateOrNull(
  dateToDisplay: String,
  pattern: String,
): LocalDate? {
  return try {
    parseStringToLocalDate(dateToDisplay, pattern)
  } catch (_: Exception) {
    null
  }
}

@OptIn(ExperimentalTime::class, FormatStringsInDatetimeFormats::class)
internal fun LocalDateTimeFormatter.isValidDateEntryFormat(entryFormat: String?): Boolean {
  return entryFormat?.let {
    try {
      val dateFormat = LocalDate.Format { byUnicodePattern(entryFormat) }
      val text =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.format(dateFormat)
      parseStringToLocalDate(
        text,
        entryFormat,
      )
      true
    } catch (e: Exception) {
      Logger.w(messageString = e.message ?: "Error parsing date", throwable = e)
      false
    }
  }
    ?: false
}
