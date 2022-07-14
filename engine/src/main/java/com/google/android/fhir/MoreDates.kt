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

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

@Suppress("DEPRECATION") // java.util.Date API used by HAPI
internal val Date.epochDay
  get() = LocalDate.of(year + 1900, month + 1, date).toEpochDay()

/** Validates if given string matches ISO_DATE format i.e. yyyy-MM-dd or yyyy-MM-dd+00:00 */
internal val String.isValidDateOnly
  get() = kotlin.runCatching { LocalDate.parse(this, DateTimeFormatter.ISO_DATE) }.isSuccess
