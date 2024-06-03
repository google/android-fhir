/*
 * Copyright 2024 Google LLC
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

package com.google.android.fhir.sync.upload

import androidx.test.core.app.ApplicationProvider
import com.google.android.fhir.FhirServices
import kotlin.test.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ResourceConsolidatorFactoryTest {
  private val services =
    FhirServices.builder(ApplicationProvider.getApplicationContext()).inMemory().build()

  @Test
  fun `return HttpPostResourceConsolidator instance `() {
    val httpPostResourceConsolidator =
      ResourceConsolidatorFactory.byHttpVerb(
        UploadStrategy.SingleResourcePost.requestGeneratorMode,
        services.database,
      )
    assertTrue(httpPostResourceConsolidator is HttpPostResourceConsolidator)
  }

  @Test
  fun `return DefaultResourceConsolidator instance `() {
    val httpPostResourceConsolidator =
      ResourceConsolidatorFactory.byHttpVerb(
        UploadStrategy.AllChangesSquashedBundlePut.requestGeneratorMode,
        services.database,
      )
    assertTrue(httpPostResourceConsolidator is DefaultResourceConsolidator)
  }
}
