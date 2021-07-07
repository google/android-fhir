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
import com.google.fhir.r4.core.DateTime
import java.time.Instant
import java.util.Date
import java.util.TimeZone
import org.hl7.fhir.r4.model.DateTimeType

/** contains functions that convert between the hapi and proto representations of dateTime */
public object DateTimeConverter {
  /** returns the proto DateTime equivalent of the hapi DateTimeType */
  public fun DateTimeType.toProto(): DateTime {
    val protoValue = DateTime.newBuilder()
    if (timeZone.id != null) protoValue.setTimezone(timeZone.id)
    if (value.time != null) protoValue.setValueUs(value.time)
    if (precision.toProtoPrecision() != null) protoValue.setPrecision(precision.toProtoPrecision())
    return protoValue.build()
  }

  /** returns the hapi DateTimeType equivalent of the proto DateTime */
  public fun DateTime.toHapi(): DateTimeType {
    val hapiValue = DateTimeType()
    hapiValue.timeZone = TimeZone.getTimeZone(timezone)
    hapiValue.value = Date.from(Instant.ofEpochMilli(valueUs))
    hapiValue.precision = precision.toHapiPrecision()
    return hapiValue
  }

  /** converts the hapi temporal precision to DateTime.Precision */
  private fun TemporalPrecisionEnum.toProtoPrecision(): DateTime.Precision =
    when (this) {
      TemporalPrecisionEnum.YEAR -> DateTime.Precision.YEAR
      TemporalPrecisionEnum.MONTH -> DateTime.Precision.MONTH
      TemporalPrecisionEnum.DAY -> DateTime.Precision.DAY
      TemporalPrecisionEnum.SECOND -> DateTime.Precision.SECOND
      TemporalPrecisionEnum.MILLI -> DateTime.Precision.MILLISECOND
      else -> DateTime.Precision.PRECISION_UNSPECIFIED
    }

  /** converts the DateTime.Precision to hapi Temporal Precision */
  private fun DateTime.Precision.toHapiPrecision(): TemporalPrecisionEnum =
    when (this) {
      DateTime.Precision.YEAR -> TemporalPrecisionEnum.YEAR
      DateTime.Precision.MONTH -> TemporalPrecisionEnum.MONTH
      DateTime.Precision.DAY -> TemporalPrecisionEnum.DAY
      DateTime.Precision.SECOND -> TemporalPrecisionEnum.SECOND
      DateTime.Precision.MILLISECOND -> TemporalPrecisionEnum.MILLI
      else -> TemporalPrecisionEnum.MILLI
    }
}
