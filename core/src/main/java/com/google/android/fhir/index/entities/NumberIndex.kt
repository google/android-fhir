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

import java.math.BigDecimal

/**
 * An index record for a number value in a resource.
 *
 * See https://hl7.org/FHIR/search.html#number.
 */
internal data class NumberIndex(
  /** The name of the number index, e.g. "probability". */
  val name: String,
  /** The path of the number index, e.g. "RiskAssessment.​prediction.​probability". */
  val path: String,
  /** The value of the number index, e.g. "0.1". */
  val value: BigDecimal
)
