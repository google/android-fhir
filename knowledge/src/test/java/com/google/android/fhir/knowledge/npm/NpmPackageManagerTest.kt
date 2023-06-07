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

package com.google.android.fhir.knowledge.npm

import com.google.android.fhir.knowledge.ImplementationGuide
import com.google.common.truth.Truth.assertThat
import java.io.File
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class NpmPackageManagerTest {

  private val fakePackageDownloader: PackageDownloader =
    PackageDownloader { implementationGuide: ImplementationGuide, _ ->
      NPM_CACHE_MAP.getValue(implementationGuide)
    }
  private val mockCacheManager = mock<CacheManager>()

  private val npmPackageManager = NpmPackageManager(mockCacheManager, fakePackageDownloader)

  @Test
  fun install_withDependencies() = runTest {
    whenever(mockCacheManager.containsPackage(any(), any())).thenReturn(false)

    assertThat(npmPackageManager.install(IG1)).containsExactly(NPM1, NPM2, NPM3)
  }

  @Test
  fun install_alreadyCached() = runTest {
    whenever(mockCacheManager.containsPackage(any(), any())).thenReturn(true)
    whenever(mockCacheManager.getPackage(IG1.packageId, IG1.version)).thenReturn(NPM1)

    assertThat(npmPackageManager.install(IG1)).containsExactly(NPM1)
  }

  @Test
  fun install_someCached() = runTest {
    whenever(mockCacheManager.containsPackage(IG1.packageId, IG1.version)).thenReturn(false)
    whenever(mockCacheManager.containsPackage(IG2.packageId, IG2.version)).thenReturn(true)
    whenever(mockCacheManager.getPackage(IG2.packageId, IG2.version)).thenReturn(NPM2)

    assertThat(npmPackageManager.install(IG1)).containsExactly(NPM1, NPM2)
  }

  private companion object {
    val IG1 = ImplementationGuide("package1", "version")
    val IG2 = ImplementationGuide("package2", "version")
    val IG3 = ImplementationGuide("package3", "version")
    val NPM1 = NpmPackage(IG1.packageId, IG1.version, null, listOf(IG2), File("fakePath"))
    val NPM2 = NpmPackage(IG2.packageId, IG2.version, null, listOf(IG3), File("fakePath"))
    val NPM3 = NpmPackage(IG3.packageId, IG1.version, null, emptyList(), File("fakePath"))
    val NPM_CACHE_MAP = mapOf(IG1 to NPM1, IG2 to NPM2, IG3 to NPM3)
  }
}
