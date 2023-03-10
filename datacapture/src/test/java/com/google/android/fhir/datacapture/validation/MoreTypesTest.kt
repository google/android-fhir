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

package com.google.android.fhir.datacapture.validation

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import java.time.LocalDate
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Expression
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.IntegerType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MoreTypesTest {
  lateinit var context: Context

  @Before
  fun setup() {
    context = ApplicationProvider.getApplicationContext()
  }

  @Test
  fun `should return calculated value for cqf expression`() {
    val today = LocalDate.now().toString()
    val type =
      DateType().apply {
        extension =
          listOf(
            Extension(
              CQF_CALCULATED_EXPRESSION_URL,
              Expression().apply {
                language = "text/fhirpath"
                expression = "today()"
              }
            )
          )
      }
    assertThat((type.valueOrCalculateValue() as? DateType)?.valueAsString).isEqualTo(today)
  }

  @Test
  fun `should return entered value when no cqf expression is defined`() {
    val type = IntegerType().apply { value = 500 }
    assertThat((type.valueOrCalculateValue() as? IntegerType)?.value).isEqualTo(500)
  }
}
