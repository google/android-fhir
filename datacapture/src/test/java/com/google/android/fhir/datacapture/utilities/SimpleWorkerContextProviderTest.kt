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

package com.google.android.fhir.datacapture.utilities

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.android.fhir.datacapture.mapping.ShadowNpmPackageProvider
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.context.SimpleWorkerContext
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.util.ReflectionHelpers

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], shadows = [ShadowNpmPackageProvider::class])
class SimpleWorkerContextProviderTest {

  @Before
  fun setUp() {
    ReflectionHelpers.setField(SimpleWorkerContextProvider, "simpleWorkerContext", null)
  }

  @Test
  fun `loadSimpleWorkerContext() should cache SimpleWorkerContext`() {
    val expectedSimpleWorkerContext: SimpleWorkerContext

    runBlocking {
      expectedSimpleWorkerContext =
        SimpleWorkerContextProvider.loadSimpleWorkerContext(
          ApplicationProvider.getApplicationContext()
        )
    }

    val actualSimpleWorkerContext: SimpleWorkerContext

    runBlocking {
      actualSimpleWorkerContext =
        SimpleWorkerContextProvider.loadSimpleWorkerContext(
          ApplicationProvider.getApplicationContext()
        )
    }

    assertThat(expectedSimpleWorkerContext).isEqualTo(actualSimpleWorkerContext)
  }
}
