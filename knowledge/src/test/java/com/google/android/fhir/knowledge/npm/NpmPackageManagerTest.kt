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

import com.google.android.fhir.knowledge.Dependency
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
class NpmPackageManagerTest { // TODO: fix the test

  private val fakePackageDownloader: PackageDownloader =
    PackageDownloader { dependency: Dependency, _ ->
      NPM_CACHE_MAP.getValue(dependency)
    }
  private val mockNpmFileManager = mock<NpmFileManager>()

  private val npmPackageManager =
    null // NpmPackageManager(mockNpmFileManager, fakePackageDownloader)

  @Test
  fun install_withDependencies() = runTest {
    whenever(mockNpmFileManager.containsPackage(any(), any())).thenReturn(false)

    // assertThat(npmPackageManager.getOrDownload(IG1)).containsExactly(NPM1, NPM2, NPM3)
  }

  @Test
  fun install_alreadyCached() = runTest {
    whenever(mockNpmFileManager.containsPackage(any(), any())).thenReturn(true)
    whenever(mockNpmFileManager.getPackage(DEP1.packageId, DEP1.version)).thenReturn(NPM1)

    // assertThat(npmPackageManager.getOrDownload(IG1)).containsExactly(NPM1)
  }

  @Test
  fun install_someCached() = runTest {
    whenever(mockNpmFileManager.containsPackage(DEP1.packageId, DEP1.version)).thenReturn(false)
    whenever(mockNpmFileManager.containsPackage(DEP2.packageId, DEP2.version)).thenReturn(true)
    whenever(mockNpmFileManager.getPackage(DEP2.packageId, DEP2.version)).thenReturn(NPM2)

    // assertThat(npmPackageManager.getOrDownload(IG1)).containsExactly(NPM1, NPM2)
  }

  private companion object {
    val DEP1 = Dependency("package1", "version")
    val DEP2 = Dependency("package2", "version")
    val DEP3 = Dependency("package3", "version")
    val NPM1 = NpmPackage(DEP1.packageId, DEP1.version, null, listOf(DEP2), File("fakePath"))
    val NPM2 = NpmPackage(DEP2.packageId, DEP2.version, null, listOf(DEP3), File("fakePath"))
    val NPM3 = NpmPackage(DEP3.packageId, DEP1.version, null, emptyList(), File("fakePath"))
    val NPM_CACHE_MAP = mapOf(DEP1 to NPM1, DEP2 to NPM2, DEP3 to NPM3)
  }
}
