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

import org.hl7.fhir.r4.model.Type

operator fun Type.compareTo(value: Type?): Int {
  if (value != null) {
    when {
      this.fhirType().equals("integer") && value.fhirType().equals("integer") -> {
        if (value.primitiveValue().toInt() > this.primitiveValue().toInt()) return 1
        if (value.primitiveValue().toInt() < this.primitiveValue().toInt()) return -1
        if (value.primitiveValue().toInt() == this.primitiveValue().toInt()) return 0
      }
      this.fhirType().equals("decimal") && value.fhirType().equals("decimal") -> {
        if (value.primitiveValue().toBigDecimal() > this.primitiveValue().toBigDecimal()) return 1
        if (value.primitiveValue().toBigDecimal() < this.primitiveValue().toBigDecimal()) return -1
        if (value.primitiveValue().toBigDecimal() == this.primitiveValue().toBigDecimal()) return 0
      }
      this.fhirType().equals("date") && value.fhirType().equals("date") -> {
        if (value.dateTimeValue().value > this.dateTimeValue().value) return 1
        if (value.dateTimeValue().value < this.dateTimeValue().value) return -1
        if (value.dateTimeValue().value == this.dateTimeValue().value) return 0
      }
      this.fhirType().equals("dateTime") && value.fhirType().equals("dateTime") -> {
        if (value.dateTimeValue().value > this.dateTimeValue().value) return 1
        if (value.dateTimeValue().value < this.dateTimeValue().value) return -1
        if (value.dateTimeValue().value == this.dateTimeValue().value) return 0
      }
    }
  }
  return 0
}
