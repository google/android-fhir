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

import ca.uhn.fhir.model.api.TemporalPrecisionEnum

/**
 * An index record for a date value in a resource.
 *
 * See https://hl7.org/FHIR/search.html#date.
 */
internal data class DateIndex(
    /** The name of the date index, e.g. "birthdate". */
    val name: String,
    /** The path of the date index, e.g. "Patient.birthdate". */
    val path: String,
    /** The high timestamp. */
    val tsHigh: Long,
    /** The low timestamp. */
    val tsLow: Long,
    /**
     * The smallest value we can unambiguously resolve the date to.
     * This is an indication to clients that any part of the timestamp smaller than the
     * [temporalPrecision] should be ignored.
     */
    val temporalPrecision: TemporalPrecisionEnum
)
