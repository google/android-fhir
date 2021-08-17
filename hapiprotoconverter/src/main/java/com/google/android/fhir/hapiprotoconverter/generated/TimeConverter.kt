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

import com.google.fhir.r4.core.Time
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.Int
import kotlin.String
import org.hl7.fhir.r4.model.TimeType

object TimeConverter {
  /** returns the proto Time equivalent of the hapi TimeType */
  fun TimeType.toProto(): Time {
    val protoValue = Time.newBuilder()
    if (value != null) protoValue.valueUs = LocalTime.parse(value).toNanoOfDay() / 1000
    if (value != null) protoValue.precisionValue = getTimePrecision(value)
    return protoValue.build()
  }

  /** returns the hapi TimeType equivalent of the proto Time */
  fun Time.toHapi(): TimeType {
    val hapiValue = TimeType()
    hapiValue.value = LocalTime.ofNanoOfDay(valueUs * 1000).format(DateTimeFormatter.ISO_LOCAL_TIME)
    return hapiValue
  }

  /** generates Time.Precision for the hapi TimeType */
  private fun getTimePrecision(timeString: String): Int =
    when (timeString.length) {
      8 -> Time.Precision.SECOND_VALUE
      12 -> Time.Precision.MILLISECOND_VALUE
      else -> -1
    }
}
