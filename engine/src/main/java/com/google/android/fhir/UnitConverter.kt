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

package com.google.android.fhir

import java.math.BigDecimal
import java.math.MathContext
import kotlin.jvm.Throws
import org.fhir.ucum.Decimal
import org.fhir.ucum.Pair
import org.fhir.ucum.UcumEssenceService
import org.fhir.ucum.UcumException

internal object UnitConverter {
  private val ucumService by lazy {
    UcumEssenceService(this::class.java.getResourceAsStream("/ucum-essence.xml"))
  }

  /**
   * Returns the canonical form of a Ucum Value.
   *
   * For example a value of 1000 mm will return 1 m.
   */
  @Throws(ConverterException::class)
  internal fun getCanonicalUnits(value: UcumValue): UcumValue {

    try {
      val pair =
        ucumService.getCanonicalForm(Pair(Decimal(value.value.toPlainString()), value.units))
      return UcumValue(
        pair.code,
        pair.value.asDecimal().toBigDecimal(MathContext(value.value.precision()))
      )
    } catch (exception: UcumException) {
      exception.printStackTrace()
      throw ConverterException(exception)
    }
  }
}

internal class ConverterException(cause: Throwable) : Exception(cause)

internal data class UcumValue(val units: String, val value: BigDecimal)
