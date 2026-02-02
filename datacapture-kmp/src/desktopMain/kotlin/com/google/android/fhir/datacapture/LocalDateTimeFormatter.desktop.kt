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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.android.fhir.datacapture.extensions.length
import java.text.ParseException
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.util.Locale
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalTime
import kotlinx.datetime.toKotlinLocalDate

object JVMLocalDateTimeFormatter : LocalDateTimeFormatter {
  override fun parseStringToLocalDate(
    str: String,
    pattern: String,
  ): LocalDate {
    val localDate = java.time.LocalDate.parse(str, DateTimeFormatter.ofPattern(pattern))

    // Throw ParseException if year is less than 4 digits.
    if (localDate.year.length() < 4) {
      throw ParseException("Year has less than 4 digits.", str.indexOf('y'))
    }
    // date/localDate with year more than 4 digits
    if (localDate.year.length() > 4) {
      throw ParseException("Year has more than 4 digits.", str.indexOf('y'))
    }
    return localDate.toKotlinLocalDate()
  }

  override fun format(localDate: LocalDate, pattern: String?): String =
    if (pattern.isNullOrEmpty()) {
      DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(localDate.toJavaLocalDate())
    } else {
      DateTimeFormatter.ofPattern(pattern).format(localDate.toJavaLocalDate())
    }

  override val localDateShortFormatPattern: String
    get() =
      DateTimeFormatterBuilder.getLocalizedDateTimePattern(
        FormatStyle.SHORT,
        null,
        IsoChronology.INSTANCE,
        Locale.getDefault(),
      )

  override fun localizedTimeString(time: LocalTime): String {
    return DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(time.toJavaLocalTime())
  }
}

@Composable
actual fun getLocalDateTimeFormatter(): LocalDateTimeFormatter {
  return remember { JVMLocalDateTimeFormatter }
}
