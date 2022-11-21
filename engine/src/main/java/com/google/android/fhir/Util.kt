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

package com.google.android.fhir

import android.annotation.SuppressLint
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

/** Utility function to format a [Date] object using the system's default locale. */
@SuppressLint("NewApi")
internal fun Date.toTimeZoneString(): String {
  val simpleDateFormat =
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
      .withZone(ZoneId.systemDefault())
  return simpleDateFormat.format(this.toInstant())
}

/**
 * Returns true if given string matches ISO_DATE format i.e. "yyyy-MM-dd" or "yyyy-MM-dd+00:00",
 * false otherwise.
 */
internal fun isValidDateOnly(date: String) =
  runCatching { LocalDate.parse(date, DateTimeFormatter.ISO_DATE) }.isSuccess


class OffsetDateTimeTypeAdapter : TypeAdapter<OffsetDateTime>() {
  override fun write(out: JsonWriter, value: OffsetDateTime) {
    out.value(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(value))
  }

  override fun read(input: JsonReader): OffsetDateTime = OffsetDateTime.parse(input.nextString())
}

/** Url for the UCUM system of measures. */
const val ucumUrl = "http://unitsofmeasure.org"
