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

import com.google.common.truth.Truth.assertThat
import kotlin.test.assertFailsWith
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.util.ReflectionHelpers

@RunWith(RobolectricTestRunner::class)
class DataCaptureTest {

  @Before
  fun setUp() {
    ReflectionHelpers.setStaticField(DataCapture.javaClass, "_configuration", null)
  }

  @Test(expected = Test.None::class)
  fun `should initialize one time without exception`() {
    DataCapture.initialize(Configuration())
  }

  @Test(expected = Test.None::class)
  fun `should throw exception second time initialize is called`() {
    DataCapture.initialize(Configuration())
    val message =
      assertFailsWith<IllegalStateException> { DataCapture.initialize(Configuration()) }
        .localizedMessage

    assertThat(message).isEqualTo("DataCapture is already initialized")
  }
}
