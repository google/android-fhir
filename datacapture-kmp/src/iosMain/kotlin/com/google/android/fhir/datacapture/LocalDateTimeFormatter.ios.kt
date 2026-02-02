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
import kotlin.time.ExperimentalTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toNSDate
import platform.Foundation.NSCalendar
import platform.Foundation.NSDate
import platform.Foundation.NSDateComponents
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterNoStyle
import platform.Foundation.NSDateFormatterShortStyle

@OptIn(ExperimentalTime::class)
object IosLocalDateTimeFormatter : LocalDateTimeFormatter {

  override fun parseStringToLocalDate(
    str: String,
    pattern: String,
  ): LocalDate {
    val nsDateFormatter = NSDateFormatter()
    nsDateFormatter.dateFormat = pattern
    val localDate =
      nsDateFormatter.dateFromString(str) ?: throw IllegalArgumentException("Invalid date format")
    return localDate.toKotlinInstant().toLocalDateTime(TimeZone.currentSystemDefault()).date
  }

  override fun format(localDate: LocalDate, pattern: String?): String {
    val nsDateFormatter = NSDateFormatter()
    if (!pattern.isNullOrBlank()) {
      nsDateFormatter.dateFormat = pattern
    } else {
      nsDateFormatter.dateStyle = NSDateFormatterShortStyle
    }
    return nsDateFormatter.stringFromDate(
      localDate.atStartOfDayIn(TimeZone.currentSystemDefault()).toNSDate(),
    )
  }

  override val localDateShortFormatPattern: String
    get() {
      val nsDateFormatter = NSDateFormatter()
      nsDateFormatter.dateStyle = NSDateFormatterShortStyle
      nsDateFormatter.timeStyle = NSDateFormatterNoStyle
      return nsDateFormatter.dateFormat
    }

  override fun localizedTimeString(time: LocalTime): String {
    val nsTime = time.toNSDate() ?: return ""
    val nsDateFormatter = NSDateFormatter()
    nsDateFormatter.dateStyle = NSDateFormatterNoStyle
    nsDateFormatter.timeStyle = NSDateFormatterShortStyle
    return nsDateFormatter.stringFromDate(nsTime)
  }

  private fun LocalTime.toNSDate(): NSDate? {
    val calendar = NSCalendar.currentCalendar
    val components =
      NSDateComponents().apply {
        hour = this@toNSDate.hour.toLong()
        minute = this@toNSDate.minute.toLong()
        second = this@toNSDate.second.toLong()
      }

    return calendar.dateFromComponents(components)
  }
}

@Composable
actual fun getLocalDateTimeFormatter(): LocalDateTimeFormatter {
  return remember { IosLocalDateTimeFormatter }
}
