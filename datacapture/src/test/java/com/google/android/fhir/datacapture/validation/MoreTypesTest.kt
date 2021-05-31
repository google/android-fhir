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

import android.os.Build
import com.google.common.truth.Truth.assertThat
import java.util.Calendar
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.IntegerType
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MoreTypesTest {

  @Test
  fun compareTo_int_shouldReturnPositiveValue() {
    val integerValue = IntegerType()
    integerValue.value = 20
    val integerValueToBeCompared = IntegerType()
    integerValueToBeCompared.value = 19
    assertThat(integerValue.compareTo(integerValueToBeCompared)).isEqualTo(1)
  }

  @Test
  fun compareTo_int_shouldReturnZero() {
    val integerValue = IntegerType()
    integerValue.value = 20
    val integerValueToBeCompared = IntegerType()
    integerValueToBeCompared.value = 20
    assertThat(integerValue.compareTo(integerValueToBeCompared)).isEqualTo(0)
  }

  @Test
  fun compareTo_int_shouldReturnNegativeValue() {
    val integerValue = IntegerType()
    integerValue.value = 19
    val integerValueToBeCompared = IntegerType()
    integerValueToBeCompared.value = 20
    assertThat(integerValue.compareTo(integerValueToBeCompared)).isEqualTo(-1)
  }

  @Test
  fun compareTo_decimal_shouldReturnPositiveValue() {
    val decimalValue = DecimalType()
    decimalValue.setValue(20.2)
    val decimalValueToBeCompared = DecimalType()
    decimalValueToBeCompared.setValue(19.21)
    assertThat(decimalValue.compareTo(decimalValueToBeCompared)).isEqualTo(1)
  }

  @Test
  fun compareTo_decimal_shouldReturnZero() {
    val decimalValue = DecimalType()
    decimalValue.setValue(20.2)
    val decimalValueToBeCompared = DecimalType()
    decimalValueToBeCompared.setValue(20.2)
    assertThat(decimalValue.compareTo(decimalValueToBeCompared)).isEqualTo(0)
  }

  @Test
  fun compareTo_decimal_shouldReturnNegativeValue() {
    val decimalValue = DecimalType()
    decimalValue.setValue(19.21)
    val decimalValueToBeCompared = DecimalType()
    decimalValueToBeCompared.setValue(20.2)
    assertThat(decimalValue.compareTo(decimalValueToBeCompared)).isEqualTo(-1)
  }

  @Test
  fun compareTo_dateTime_shouldReturnPositiveValue() {
    val dateValue = DateType()
    dateValue.value = Calendar.getInstance().time
    val dateValueToBeCompared = DateType()
    val calendarInstance = Calendar.getInstance()
    calendarInstance.set(1994, 5, 6)
    dateValueToBeCompared.value = calendarInstance.time
    assertThat(dateValue.compareTo(dateValueToBeCompared)).isEqualTo(1)
  }

  @Test
  fun compareTo_dateTime_shouldReturnZero() {
    val dateValue = DateType()
    dateValue.value = Calendar.getInstance().time
    val dateValueToBeCompared = DateType()
    dateValueToBeCompared.value = Calendar.getInstance().time
    assertThat(dateValue.compareTo(dateValueToBeCompared)).isEqualTo(0)
  }

  @Test
  fun compareTo_dateTime_shouldReturnNegativeValue() {
    val dateValue = DateType()
    val calendarInstance = Calendar.getInstance()
    calendarInstance.set(1994, 5, 6)
    dateValue.value = calendarInstance.time
    val dateValueToBeCompared = DateType()
    dateValueToBeCompared.value = Calendar.getInstance().time
    assertThat(dateValue.compareTo(dateValueToBeCompared)).isEqualTo(-1)
  }

  @Test
  fun compareTo_shouldThrowExceptionInCaseOfTypeMismatch() {
    val decimalValue = DecimalType()
    decimalValue.setValue(19.21)
    val integerValue = IntegerType()
    integerValue.value = 19
    val exception =
      assertThrows(IllegalArgumentException::class.java) { decimalValue > integerValue }
    assertThat(exception.message)
      .isEqualTo("Cannot compare different data types: decimal and integer")
  }
}
