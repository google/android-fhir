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

import com.google.android.fhir.datacapture.extensions.length
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atDate
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

@OptIn(FormatStringsInDatetimeFormats::class)
object WasmJsLocalDateTimeFormatter : LocalDateTimeFormatter {
  override fun parseStringToLocalDate(
    str: String,
    pattern: String,
  ): LocalDate {
    val dateFormat = LocalDate.Format { byUnicodePattern(pattern) }
    val localDate = LocalDate.parse(str, dateFormat)

    // Validate year has exactly 4 digits
    if (localDate.year.length() < 4) {
      throw IllegalArgumentException("Year has less than 4 digits.")
    }
    // date/localDate with year more than 4 digits
    if (localDate.year.length() > 4) {
      throw IllegalArgumentException("Year has more than 4 digits.")
    }
    return localDate
  }

  override fun format(localDate: LocalDate, pattern: String?): String {
    val format =
      if (!pattern.isNullOrEmpty()) {
        LocalDate.Format { byUnicodePattern(pattern) }
      } else {
        LocalDate.Formats.ISO
      }

    return format.format(localDate)
  }

  override val localDateShortFormatPattern: String
    get() = "dd/MM/yyyy"

  @OptIn(ExperimentalTime::class)
  override fun localizedTimeString(time: LocalTime): String {
    val dateTime =
      time
        .atDate(Clock.System.todayIn(TimeZone.currentSystemDefault()))
        .toInstant(TimeZone.currentSystemDefault())
        .toLocalDateTime(
          TimeZone.currentSystemDefault(),
        )
    return dateTime.time.format(LocalTime.Formats.ISO)
  }
}
