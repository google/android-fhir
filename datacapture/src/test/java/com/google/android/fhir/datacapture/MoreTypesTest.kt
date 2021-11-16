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

package com.google.android.fhir.datacapture

import android.os.Build
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Attachment
import org.hl7.fhir.r4.model.BooleanType
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Reference
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MoreTypesTest {

  @Test
  fun equals_differentTypes_shouldReturnFalse() {
    assertThat(equals(BooleanType(true), DecimalType(1.1))).isFalse()
  }

  @Test
  fun equals_sameObject_shouldReturnTrue() {
    val value = BooleanType(true)
    assertThat(equals(value, value)).isTrue()
  }

  @Test
  fun equals_samePrimitiveValue_shouldReturnTrue() {
    assertThat(equals(DecimalType(1.1), DecimalType(1.1))).isTrue()
  }

  @Test
  fun equals_differentPrimitiveValues_shouldReturnFalse() {
    assertThat(equals(DecimalType(1.1), DecimalType(1.2))).isFalse()
  }

  @Test
  fun equals_coding_differentSystems_shouldReturnFalse() {
    assertThat(
        equals(Coding("system", "code", "display"), Coding("otherSystem", "code", "display"))
      )
      .isFalse()
  }

  @Test
  fun equals_coding_differentCodes_shouldReturnFalse() {
    assertThat(
        equals(Coding("system", "code", "display"), Coding("system", "otherCode", "display"))
      )
      .isFalse()
  }

  @Test
  fun equals_coding_differentDisplays_shouldReturnTrue() {
    assertThat(
        equals(Coding("system", "code", "display"), Coding("system", "code", "otherDisplay"))
      )
      .isTrue()
  }

  @Test
  fun equals_attachment_shouldThrowException() {
    val exception =
      assertThrows(NotImplementedError::class.java) { equals(Attachment(), Attachment()) }

    assertThat(exception.message)
      .isEqualTo("Comparison for type ${Attachment::class.java} not supported.")
  }

  @Test
  fun equals_quantity_shouldThrowException() {
    val exception = assertThrows(NotImplementedError::class.java) { equals(Quantity(), Quantity()) }

    assertThat(exception.message)
      .isEqualTo("Comparison for type ${Quantity::class.java} not supported.")
  }

  @Test
  fun equals_reference_shouldThrowException() {
    val exception =
      assertThrows(NotImplementedError::class.java) { equals(Reference(), Reference()) }

    assertThat(exception.message)
      .isEqualTo("Comparison for type ${Reference::class.java} not supported.")
  }
}
