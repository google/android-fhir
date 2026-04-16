/*
 * Copyright 2025-2026 Google LLC
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

import kotlin.time.Instant
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/** Formats an [Instant] as an ISO 8601 date-time string with timezone offset. */
internal fun Instant.toTimeZoneString(): String {
  val localDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
  return localDateTime.toString()
}

/** Returns true if given string matches ISO date format i.e. "yyyy-MM-dd", false otherwise. */
internal fun isValidDateOnly(date: String): Boolean = Regex("^\\d{4}-\\d{2}-\\d{2}$").matches(date)

/** Implementation of a parallelized map. */
suspend fun <A, B> Iterable<A>.pmap(
  dispatcher: CoroutineDispatcher,
  f: suspend (A) -> B,
): List<B> = coroutineScope { map { async(dispatcher) { f(it) } }.awaitAll() }

/** Url for the UCUM system of measures. */
internal const val ucumUrl = "http://unitsofmeasure.org"

internal fun percentOf(value: Number, total: Number) =
  if (total == 0) 0.0 else value.toDouble() / total.toDouble()
