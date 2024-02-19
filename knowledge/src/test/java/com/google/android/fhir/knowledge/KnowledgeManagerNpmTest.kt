/*
 * Copyright 2023 Google LLC
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

package com.google.android.fhir.knowledge

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.android.fhir.knowledge.db.KnowledgeDatabase
import com.google.android.fhir.knowledge.files.NpmFileManager
import com.google.android.fhir.knowledge.npm.NpmPackageDownloader
import com.google.common.truth.Truth.assertThat
import java.io.File
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class KnowledgeManagerNpmTest {

  private val downloadedDependencies = mutableSetOf<FhirNpmPackage>()
  private val fakeNpmPackageDownloader: NpmPackageDownloader =
    NpmPackageDownloader { fhirNpmPackage: FhirNpmPackage, _ ->
      downloadedDependencies.add(fhirNpmPackage)
      NPM_CACHE_MAP.getValue(fhirNpmPackage)
    }

  private val context: Context = ApplicationProvider.getApplicationContext()
  private val mockNpmFileManager = mock<NpmFileManager>()
  private val knowledgeDb =
    Room.inMemoryDatabaseBuilder(context, KnowledgeDatabase::class.java).build()

  private val knowledgeManager =
    KnowledgeManager(
      knowledgeDb,
      npmFileManager = mockNpmFileManager,
      npmPackageDownloader = fakeNpmPackageDownloader,
    )

  @Test
  fun install_withDependencies() = runTest {
    whenever(mockNpmFileManager.containsPackage(any(), any())).thenReturn(false)
    whenever(mockNpmFileManager.getLocalFhirNpmPackageMetadata(DEP1.name, DEP1.version))
      .thenReturn(NPM1)
    whenever(mockNpmFileManager.getLocalFhirNpmPackageMetadata(DEP2.name, DEP2.version))
      .thenReturn(NPM2)
    whenever(mockNpmFileManager.getLocalFhirNpmPackageMetadata(DEP3.name, DEP3.version))
      .thenReturn(NPM3)

    knowledgeManager.install(DEP1)

    assertThat(downloadedDependencies).containsExactly(DEP1, DEP2, DEP3)
  }

  @Test
  fun install_alreadyCached() = runTest {
    whenever(mockNpmFileManager.containsPackage(any(), any())).thenReturn(true)
    whenever(mockNpmFileManager.getLocalFhirNpmPackageMetadata(DEP1.name, DEP1.version))
      .thenReturn(NPM1)
    whenever(mockNpmFileManager.getLocalFhirNpmPackageMetadata(DEP2.name, DEP2.version))
      .thenReturn(NPM2)
    whenever(mockNpmFileManager.getLocalFhirNpmPackageMetadata(DEP3.name, DEP3.version))
      .thenReturn(NPM3)

    knowledgeManager.install(DEP1)

    assertThat(downloadedDependencies).isEmpty()
  }

  @Test
  fun install_someCached() = runTest {
    whenever(mockNpmFileManager.containsPackage(DEP1.name, DEP1.version)).thenReturn(false)
    whenever(mockNpmFileManager.containsPackage(DEP2.name, DEP2.version)).thenReturn(true)
    whenever(mockNpmFileManager.containsPackage(DEP3.name, DEP3.version)).thenReturn(true)
    whenever(mockNpmFileManager.getLocalFhirNpmPackageMetadata(DEP1.name, DEP1.version))
      .thenReturn(NPM1)
    whenever(mockNpmFileManager.getLocalFhirNpmPackageMetadata(DEP2.name, DEP2.version))
      .thenReturn(NPM2)
    whenever(mockNpmFileManager.getLocalFhirNpmPackageMetadata(DEP3.name, DEP3.version))
      .thenReturn(NPM3)

    knowledgeManager.install(DEP1, DEP2)

    assertThat(downloadedDependencies).containsExactly(DEP1)
  }

  private companion object {
    val DEP1 = FhirNpmPackage("package1", "version")
    val DEP2 = FhirNpmPackage("package2", "version")
    val DEP3 = FhirNpmPackage("package3", "version")
    val NPM1 =
      LocalFhirNpmPackageMetadata(DEP1.name, DEP1.version, null, listOf(DEP2), File("/fakePath"))
    val NPM2 =
      LocalFhirNpmPackageMetadata(DEP2.name, DEP2.version, null, listOf(DEP3), File("/fakePath"))
    val NPM3 =
      LocalFhirNpmPackageMetadata(DEP3.name, DEP1.version, null, emptyList(), File("/fakePath"))
    val NPM_CACHE_MAP = mapOf(DEP1 to NPM1, DEP2 to NPM2, DEP3 to NPM3)
  }
}
