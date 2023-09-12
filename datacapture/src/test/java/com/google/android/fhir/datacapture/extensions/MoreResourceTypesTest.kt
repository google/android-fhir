/*
 * Copyright 2022-2023 Google LLC
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

import android.os.Build
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MoreResourceTypesTest {

  @Test
  fun isValidCode_shouldReturnTrue_becauseOfValidCode() {
    val code = "Patient"
    val isValid = MoreResourceTypes.isValidCode(code)
    assertThat(isValid).isTrue()
  }

  @Test
  fun isValidCode_shouldReturnFalse_becauseOfInvalidCode() {
    val code = "WaitWhat"
    val isValid = MoreResourceTypes.isValidCode(code)
    assertThat(isValid).isFalse()
  }

  @Test
  fun fromCodeOrNull_shouldReturnResourceType_becauseOfValidCode() {
    val code = "Patient"
    val resourceType = MoreResourceTypes.fromCodeOrNull(code)
    assertThat(resourceType).isEqualTo(ResourceType.Patient)
  }

  @Test
  fun fromCodeOrNull_shouldReturnNull_becauseOfInvalidCode() {
    val code = "WaitWhat"
    val resourceType = MoreResourceTypes.fromCodeOrNull(code)
    assertThat(resourceType).isNull()
  }
}
