/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir.datacapture.utilities

import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import com.google.android.fhir.datacapture.views.length
import com.google.android.fhir.datacapture.views.localDate
import java.lang.StringBuilder
import java.text.ParseException
import java.time.LocalDate
import java.time.ZoneId
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.util.Date
import java.util.Locale

internal val LocalDate.localizedString: String
  get() {
    val date = Date.from(atStartOfDay(ZoneId.systemDefault())?.toInstant())
    return DateFormat.getDateInstance(DateFormat.SHORT).format(date)
  }

/**
 * Medium and long format styles use alphabetical month names which are difficult for the user to
 * input. Use short format style which is always numerical.
 */
internal val localeDatePattern =
  DateTimeFormatterBuilder.getLocalizedDateTimePattern(
    FormatStyle.SHORT,
    null,
    IsoChronology.INSTANCE,
    Locale.getDefault()
  )

/** Special character used in date format */
internal val dateFormatSpecialChar =
  localeDatePattern.lowercase().replace("m", "").replace("d", "").replace("y", "").first()

/**
 * Convert date pattern to acceptable date pattern where 2 digits are expected for day and month, 4
 * digits are expected for year.
 */
internal fun generateAcceptableDateFormat(
  datePattern: String,
  dateFormatSpecialChar: Char
): String {
  var newDateFormat = StringBuilder()
  datePattern
    .lowercase()
    .forEach {
      if (it == 'd') {
        if (!newDateFormat.contains("dd")) {
          newDateFormat.append("dd")
        }
      } else if (it == 'm') {
        if (!newDateFormat.contains("MM")) {
          newDateFormat.append("MM")
        }
      } else if (it == 'y') {
        if (!newDateFormat.contains("yyyy")) {
          newDateFormat.append("yyyy")
        }
      } else if (it == dateFormatSpecialChar) {
        newDateFormat.append(dateFormatSpecialChar)
      }
    }
    .toString()
  return newDateFormat.toString()
}

/** Parse date string into given given format. */
internal fun parseDate(text: CharSequence?, acceptableDateFormat: String?): LocalDate {
  val localDate =
    if (!acceptableDateFormat.isNullOrEmpty()) {
        SimpleDateFormat(acceptableDateFormat).apply { isLenient = false }.parse(text.toString())
      } else {
        DateFormat.getDateInstance(DateFormat.SHORT)
          .apply { isLenient = false }
          .parse(text.toString())
      }
      .localDate
  // date/localDate with year more than 4 digit throws data format exception if deep copy
  // operation get performed on QuestionnaireResponse,
  // QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent in org.hl7.fhir.r4.model
  // e.g ca.uhn.fhir.parser.DataFormatException: Invalid date/time format: "19843-12-21":
  // Expected character '-' at index 4 but found 3
  if (localDate.year.length() > 4) {
    throw ParseException("Year has more than 4 digits.", 4)
  }
  return localDate
}
