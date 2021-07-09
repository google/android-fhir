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
import com.google.common.truth.Truth
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.util.ReflectionHelpers

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], shadows = [ShadowNpmPackageProvider::class])
class NpmPackageProviderTest {

  @Before
  fun setUp() {
    ReflectionHelpers.setField(NpmPackageProvider, "npmPackage", null)
    ReflectionHelpers.setField(NpmPackageProvider, "contextR4", null)
  }

  @Test
  fun `loadSimpleWorkerContext() should initialize contextR4`() {
    Assert.assertThrows(UninitializedPropertyAccessException::class.java) {
      NpmPackageProvider::contextR4.get()
    }

    val npmPackage = NpmPackageProvider.loadNpmPackage(ApplicationProvider.getApplicationContext())

    NpmPackageProvider.loadSimpleWorkerContext(npmPackage)

    Truth.assertThat(NpmPackageProvider.contextR4).isNotNull()
  }

  @Test
  fun `loadNpmPackage() should initialize npmPackage`() {
    Assert.assertThrows(UninitializedPropertyAccessException::class.java) {
      NpmPackageProvider::npmPackage.get()
    }

    NpmPackageProvider.loadNpmPackage(ApplicationProvider.getApplicationContext())

    Truth.assertThat(NpmPackageProvider.npmPackage).isNotNull()
  }

  @Test
  fun `loadNpmPackage() should cache npmPackage`() {
    Assert.assertThrows(UninitializedPropertyAccessException::class.java) {
      NpmPackageProvider::npmPackage.get()
    }

    val generatedNpmPackage =
      NpmPackageProvider.loadNpmPackage(ApplicationProvider.getApplicationContext())

    Truth.assertThat(generatedNpmPackage).isEqualTo(NpmPackageProvider.npmPackage)
  }

  @Test
  fun `loadSimpleWorkerContext() should cache SimpleWorkerContext`() {
    Assert.assertThrows(UninitializedPropertyAccessException::class.java) {
      NpmPackageProvider::contextR4.get()
    }

    val generatedSimpleWorkerContext =
      NpmPackageProvider.loadSimpleWorkerContext(
        NpmPackageProvider.loadNpmPackage(ApplicationProvider.getApplicationContext())
      )

    Truth.assertThat(generatedSimpleWorkerContext).isEqualTo(NpmPackageProvider.contextR4)
  }
}
