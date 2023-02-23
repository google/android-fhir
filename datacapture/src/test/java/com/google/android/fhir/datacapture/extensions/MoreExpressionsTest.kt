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

import android.os.Build
import com.google.android.fhir.datacapture.isFhirPath
import com.google.android.fhir.datacapture.isXFhirQuery
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Expression
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MoreExpressionsTest {

  @Test
  fun `isXFhirQuery should return true`() {
    val expression = Expression().apply { this.language = "application/x-fhir-query" }

    assertThat(expression.isXFhirQuery).isTrue()
  }

  @Test
  fun `isXFhirQuery should return false`() {
    val expression = Expression().apply { this.language = "text/cql" }

    assertThat(expression.isXFhirQuery).isFalse()
  }

  @Test
  fun `isFhirPath should return true`() {
    val expression = Expression().apply { this.language = "text/fhirpath" }

    assertThat(expression.isFhirPath).isTrue()
  }

  @Test
  fun `isFhirPath should return false`() {
    val expression = Expression().apply { this.language = "application/x-fhir-query" }

    assertThat(expression.isFhirPath).isFalse()
  }
}
