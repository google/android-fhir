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

import android.content.Context
import android.icu.text.DateFormat
import android.text.format.DateFormat as AndroidDateFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale
import com.google.android.fhir.datacapture.extensions.length
import java.text.ParseException
import java.time.ZoneId
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.util.Date
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalTime
import kotlinx.datetime.toKotlinLocalDate

class AndroidLocalDateFormatter(private val context: Context) : LocalDateTimeFormatter {

  override fun parseStringToLocalDate(
    str: String,
    pattern: String,
  ): LocalDate {
    val localDate =
      java.time.LocalDate.parse(
        str,
        DateTimeFormatter.ofPattern(pattern, Locale.current.platformLocale),
      )

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
      val date =
        Date.from(localDate.toJavaLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant())
      DateFormat.getDateInstance(DateFormat.SHORT, Locale.current.platformLocale).format(date)
    } else {
      DateTimeFormatter.ofPattern(pattern, Locale.current.platformLocale)
        .format(localDate.toJavaLocalDate())
    }

  override val localDateShortFormatPattern: String
    get() =
      DateTimeFormatterBuilder.getLocalizedDateTimePattern(
        FormatStyle.SHORT,
        null,
        IsoChronology.INSTANCE,
        Locale.current.platformLocale,
      )

  // ICU on Android does not observe the user's 24h/12h time format setting (obtained from
  // DateFormat.is24HourFormat()). In order to observe the setting, we are using DateFormat as
  // suggested in the docs. See
  // https://developer.android.com/guide/topics/resources/internationalization#24h-setting for
  // details.
  override fun localizedTimeString(time: LocalTime): String {
    val date =
      Date.from(
        java.time.LocalDateTime.of(java.time.LocalDate.now(), time.toJavaLocalTime())
          .atZone(ZoneId.systemDefault())
          .toInstant(),
      )

    // Using applicationContext to allow for changing of locale in tests
    return AndroidDateFormat.getTimeFormat(context.applicationContext).format(date)
  }
}

@Composable
actual fun getLocalDateTimeFormatter(): LocalDateTimeFormatter {
  val context = LocalContext.current
  return remember(context) { AndroidLocalDateFormatter(context) }
}
