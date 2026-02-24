/*
 * Copyright 2024-2026 Google LLC
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

package com.google.android.fhir.demo.extensions

import android.content.Context
import android.text.format.DateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

fun OffsetDateTime.formatSyncTimestamp(context: Context): String {
  val formatter = getDateTimeFormatter(context)
  return toLocalDateTime().format(formatter)
}

private fun getDateTimeFormatter(context: Context): DateTimeFormatter {
  return DateTimeFormatter.ofPattern(
    if (DateFormat.is24HourFormat(context)) {
      "yyyy-MM-dd HH:mm:ss"
    } else {
      "yyyy-MM-dd hh:mm:ss a"
    },
  )
}
