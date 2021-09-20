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

package com.google.android.fhir

import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Unit tests for [FhirEngineProvider]. */
@RunWith(RobolectricTestRunner::class)
class FhirEngineProviderTest {
  private val provider: FhirEngineProvider = FhirEngineProvider

  @Test
  fun build_twiceWithAppContext_shouldReturnSameFhirEngine() {
    val engineOne = provider.getInstance(ApplicationProvider.getApplicationContext())
    val engineTwo = provider.getInstance(ApplicationProvider.getApplicationContext())
    Truth.assertThat(engineOne).isSameInstanceAs(engineTwo)
  }

  @Test
  fun build_withAppAndActivityContext_shouldReturnSameFhirEngine() {
    val engineAppContext = provider.getInstance(ApplicationProvider.getApplicationContext())
    val engineActivityContext =
      provider.getInstance(InstrumentationRegistry.getInstrumentation().context)
    Truth.assertThat(engineAppContext).isSameInstanceAs(engineActivityContext)
  }
}
