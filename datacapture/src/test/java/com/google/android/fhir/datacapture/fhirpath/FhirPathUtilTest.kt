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

package com.google.android.fhir.datacapture.fhirpath

import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Patient
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FhirPathUtilTest {

  @Test
  fun `evaluateToDisplay should return concatenated string for expressions evaluation on given resource`() {
    val expressions = listOf("name.given", "name.family")
    val resource =
      Patient().apply {
        addName(
          HumanName().apply {
            this.family = "Doe"
            this.addGiven("John")
          }
        )
      }

    assertThat(evaluateToDisplay(expressions, resource)).isEqualTo("John Doe")
  }
}
