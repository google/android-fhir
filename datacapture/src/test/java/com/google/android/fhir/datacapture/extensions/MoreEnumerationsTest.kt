/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir.datacapture.extensions

import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Enumeration
import org.hl7.fhir.r4.model.Enumerations
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MoreEnumerationsTest {

  @Test
  fun enumeration_should_return_coding() {
    val maleEnumerationCoding =
      Enumeration(Enumerations.AdministrativeGenderEnumFactory(), "male").toCoding()
    val coding =
      Coding().apply {
        display = "Male"
        code = "male"
        system = "http://hl7.org/fhir/administrative-gender"
      }
    assertThat(maleEnumerationCoding.equalsShallow(coding)).isTrue()
  }

  @Test
  fun invokeFunction_shouldWorkWithNoArgs() {
    val string = "android-fhir"
    val length = string.invokeFunction("length")
    assertThat(length).isEqualTo(12)
  }

  @Test
  fun invokeFunction_shouldWorkWithSingleArgs() {
    val string = "android-fhir"
    val subString = string.invokeFunction("substring", listOf(Int::class.java), 8)
    assertThat(subString).isEqualTo("fhir")
  }

  @Test
  fun invokeFunction_shouldWorkWithMultipleArgs() {
    val string = "android-fhir"
    val startsWith =
      string.invokeFunction("startsWith", listOf(String::class.java, Int::class.java), "droid", 2)
    assertThat(startsWith).isEqualTo(true)
  }
}
