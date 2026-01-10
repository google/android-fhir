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
  return buildString {
    datePattern.lowercase().forEach {
      when (it) {
        'd' -> {
          if (!hasDay) {
            append("dd")
            hasDay = true
          }
        }
        'm' -> {
          if (!hasMonth) {
            append("MM")
            hasMonth = true
          }
        }
        'y' -> {
          if (!hasYear) {
            append("yyyy")
            hasYear = true
          }
        }
        datePatternSeparator -> {
          append(datePatternSeparator)
        }
        else -> {}
      }
    }
  }
}

@OptIn(ExperimentalTime::class)
internal fun Long.toLocalDate(): LocalDate =
  Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC).date
