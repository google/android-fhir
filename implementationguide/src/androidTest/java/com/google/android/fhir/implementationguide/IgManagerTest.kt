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

package com.google.android.fhir.implementationguide.npm

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.fhir.implementationguide.IgManager
import com.google.android.fhir.implementationguide.ImplementationGuide
import org.junit.Test
import org.junit.runner.RunWith
import kotlinx.coroutines.test.runTest
import com.google.common.truth.Truth.assertThat

@RunWith(AndroidJUnit4::class)
class IgManagerTest {
  private val context: Context = ApplicationProvider.getApplicationContext()
  private val igManager = IgManager.create(context)

  @Test
  fun smoketest() = runTest {
    igManager.install(ImplementationGuide("hl7.fhir.r4b.examples", "4.3.0", "http://hl7.org/fhir"))
    assertThat(
      igManager.loadResources(
        resourceType = "Library",
        url = "http://ohie.org/Library/hiv-indicators"
      )
    )
      .isNotEmpty()
  }
}
