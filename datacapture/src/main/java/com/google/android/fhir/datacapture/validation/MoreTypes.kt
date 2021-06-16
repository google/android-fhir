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

package com.google.android.fhir.datacapture.validation

import java.util.Calendar
import java.util.Date
import org.hl7.fhir.r4.model.Type

operator fun Type.compareTo(value: Type?): Int {
  if (value != null) {
    if (!this.fhirType().equals(value.fhirType())) {
      throw IllegalArgumentException(
        "Cannot compare different data types: ${this.fhirType()} and ${value.fhirType()}"
      )
    }
    when {
      this.fhirType().equals("integer") -> {
        return this.primitiveValue().toInt().compareTo(value.primitiveValue().toInt())
      }
      this.fhirType().equals("decimal") -> {
        return this.primitiveValue().toBigDecimal().compareTo(value.primitiveValue().toBigDecimal())
      }
      this.fhirType().equals("date") -> {
        return clearTimeFromDateValue(this.dateTimeValue().value)
          .compareTo(clearTimeFromDateValue(value.dateTimeValue().value))
      }
      this.fhirType().equals("dateTime") -> {
        return this.dateTimeValue().value.compareTo(value.dateTimeValue().value)
      }
      else -> {
        throw NotImplementedError()
      }
    }
  }
  return 0
}

private fun clearTimeFromDateValue(dateValue: Date): Date {
  val calendarValue = Calendar.getInstance()
  calendarValue.time = dateValue
  calendarValue.set(Calendar.HOUR_OF_DAY, 0)
  calendarValue.set(Calendar.MINUTE, 0)
  calendarValue.set(Calendar.SECOND, 0)
  calendarValue.set(Calendar.MILLISECOND, 0)
  return calendarValue.time
}
