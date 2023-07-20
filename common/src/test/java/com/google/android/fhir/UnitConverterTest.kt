/*
 * Copyright 2023 Google LLC
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

import android.os.Build
import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class UnitConverterTest {

  @Test
  fun `should convert 1kg to 1000g`() {
    val canonicalValue = UnitConverter.getCanonicalForm(UcumValue("kg", BigDecimal.valueOf(1.0)))

    assertThat(canonicalValue.code).isEqualTo("g")
    assertThat(canonicalValue.value.toInt()).isEqualTo(1000)
  }

  @Test
  fun `should throw exception while converting Cel`() {
    val exception =
      assertThrows(ConverterException::class.java) {
        UnitConverter.getCanonicalForm(UcumValue("Cel", BigDecimal.valueOf(37.0)))
      }

    assertThat(exception)
      .hasMessageThat()
      .isEqualTo("Missing numerical value in the canonical UCUM value")
  }

  @Test
  fun `should return original code and value if fails to convert Cel to K`() {
    val canonicalValue =
      UnitConverter.getCanonicalFormOrOriginal(UcumValue("Cel", BigDecimal.valueOf(37.0)))

    assertThat(canonicalValue.code).isEqualTo("Cel")
    assertThat(canonicalValue.value.toDouble()).isEqualTo(37.0)
  }
}
