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

import com.ionspin.kotlin.bignum.decimal.BigDecimal

/**
 * Canonicalizes unit values to UCUM base units.
 *
 * For details of UCUM, see http://unitsofmeasure.org/
 *
 * For using UCUM with FHIR, see https://www.hl7.org/fhir/ucum.html
 *
 * TODO: Provide a full UCUM conversion implementation for KMP. Currently returns the original value
 *   unchanged.
 */
internal object UnitConverter {

  /**
   * Returns the canonical form of a UCUM Value. Currently returns the original value as UCUM
   * conversion is not yet available for KMP.
   */
  fun getCanonicalFormOrOriginal(value: UcumValue): UcumValue = value
}

internal class ConverterException(message: String, cause: Throwable? = null) :
  Exception(message, cause)

internal data class UcumValue(val code: String, val value: BigDecimal)
