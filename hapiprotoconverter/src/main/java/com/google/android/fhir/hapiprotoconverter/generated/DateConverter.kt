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

package com.google.android.fhir.hapiprotoconverter.generated

import ca.uhn.fhir.model.api.TemporalPrecisionEnum
import com.google.fhir.r4.core.Date
import java.time.Instant
import java.util.TimeZone
import org.hl7.fhir.r4.model.DateType

public object DateConverter {
  public fun DateType.toProto(): Date {
    val protoValue =
      Date.newBuilder()
        .setTimezone(timeZone.id)
        .setValueUs(value.time)
        .setPrecision(precision.toProtoPrecision())
        .build()
    return protoValue
  }

  public fun Date.toHapi(): DateType {
    val hapiValue = DateType()
    hapiValue.timeZone = TimeZone.getTimeZone(timezone)
    hapiValue.value = java.util.Date.from(Instant.ofEpochMilli(valueUs))
    hapiValue.precision = precision.toHapiPrecision()
    return hapiValue
  }

  private fun TemporalPrecisionEnum.toProtoPrecision(): Date.Precision =
    when (this) {
      TemporalPrecisionEnum.YEAR -> Date.Precision.YEAR
      TemporalPrecisionEnum.MONTH -> Date.Precision.MONTH
      TemporalPrecisionEnum.DAY -> Date.Precision.DAY
      else -> Date.Precision.PRECISION_UNSPECIFIED
    }

  private fun Date.Precision.toHapiPrecision(): TemporalPrecisionEnum =
    when (this) {
      Date.Precision.YEAR -> TemporalPrecisionEnum.YEAR
      Date.Precision.MONTH -> TemporalPrecisionEnum.MONTH
      Date.Precision.DAY -> TemporalPrecisionEnum.DAY
      else -> TemporalPrecisionEnum.MILLI
    }
}
