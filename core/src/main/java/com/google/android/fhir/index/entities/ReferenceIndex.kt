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
 * An index record for a reference value in a resource.
 *
 * See https://hl7.org/FHIR/search.html#reference.
 */
internal data class ReferenceIndex(
  /** The name of the reference index, e.g. "subject". */
  val name: String,
  /** The path of the reference index, e.g. "Observation.subject". */
  val path: String,
  /** The value of the reference index, e.g. "Patient/123". */
  val value: String
)
