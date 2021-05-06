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
import java.util.Date
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.IntegerType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MoreTypesTest {

  @Test
  fun shouldReturnValidResult() {
    val integerValue = IntegerType()
    integerValue.value = 20
    val integerValueToBeCompared = IntegerType()
    integerValueToBeCompared.value = 19
    assertThat(integerValue > integerValueToBeCompared).isTrue()
  }

  @Test
  fun shouldReturnValidResultForDecimal() {
    val decimalValue = DecimalType()
    decimalValue.setValue(20.2)
    val decimalValueToBeCompared = DecimalType()
    decimalValueToBeCompared.setValue(19.21)
    assertThat(decimalValue > decimalValueToBeCompared).isTrue()
  }

  @Test
  fun shouldReturnValidResultForDateTimeType() {
    val dateValue = DateType()
    dateValue.value = Calendar.getInstance().time
    val dateValueToBeCompared = DateType()
    dateValueToBeCompared.value = Date(94, 5, 6)
    assertThat(dateValue > dateValueToBeCompared).isTrue()
  }

  @Test
  fun shouldReturnInvalidValidResult() {
    val integerValue = IntegerType()
    integerValue.value = 20
    val integerValueToBeCompared = IntegerType()
    integerValueToBeCompared.value = 19
    assertThat(integerValue < integerValueToBeCompared).isFalse()
  }

  @Test
  fun shouldReturnInvalidResultForDecimal() {
    val decimalValue = DecimalType()
    decimalValue.setValue(19.21)
    val decimalValueToBeCompared = DecimalType()
    decimalValueToBeCompared.setValue(20.2)
    assertThat(decimalValue > decimalValueToBeCompared).isFalse()
  }

  @Test
  fun shouldReturnInvalidResultForDateTimeType() {
    val dateValue = DateType()
    dateValue.value = Date(94, 5, 6)
    val dateValueToBeCompared = DateType()
    dateValueToBeCompared.value = Calendar.getInstance().time
    assertThat(dateValue > dateValueToBeCompared).isFalse()
  }
}
