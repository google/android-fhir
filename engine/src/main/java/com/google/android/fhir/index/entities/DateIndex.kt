/*
 * Copyright 2020 Google LLC
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

package com.google.android.fhir.index.entities

/**
 * An index record for a `Date` value in a FHIR resource.
 *
 * Note one fundamental difference between `Date` and `DateTime` data types in FHIR in that `Date`
 * does not contain timezone info where `DateTime` does.
 *
 * See https://hl7.org/FHIR/search.html#date.
 */
internal data class DateIndex(
  /** The name of the date index, e.g. "birthdate". */
  val name: String,
  /** The path of the date index, e.g. "Patient.birthdate". */
  val path: String,
  /** The epoch day of the first date. */
  val from: Long,
  /** The epoch day of the last date. */
  val to: Long
)
