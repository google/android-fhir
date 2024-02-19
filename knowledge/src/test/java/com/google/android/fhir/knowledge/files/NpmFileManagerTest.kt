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

package com.google.android.fhir.knowledge.files

import com.google.android.fhir.knowledge.FhirNpmPackage
import com.google.common.truth.Truth.assertThat
import java.io.File
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NpmFileManagerTest {
  private val testDataFolder = File(javaClass.getResource("/cache_manager")!!.file)
  private val npmFileManager: NpmFileManager = NpmFileManager(testDataFolder)

  @Test
  fun getPackageFolder() {
    val packageFolder = npmFileManager.getPackageDir(PACKAGE_ID, VERSION)

    assertThat(packageFolder.absolutePath)
      .isEqualTo("${testDataFolder.absolutePath}/$PACKAGE_ID#$VERSION")
  }

  @Test
  fun getPackage() = runTest {
    val npmPackage = npmFileManager.getLocalFhirNpmPackageMetadata(PACKAGE_ID, VERSION)

    assertThat(npmPackage.packageId).isEqualTo(PACKAGE_ID)
    assertThat(npmPackage.version).isEqualTo(VERSION)
    assertThat(npmPackage.dependencies).isEqualTo(DEPENDENCIES)
    assertThat(npmPackage.rootDirectory)
      .isEqualTo(File(testDataFolder, "$PACKAGE_ID#$VERSION/package"))
  }

  @Test
  fun containsPackage_notFound() = runTest {
    assertThat(npmFileManager.containsPackage(PACKAGE_ID, MISSING_VERSION)).isFalse()
  }

  companion object {
    const val PACKAGE_ID = "test-package"
    const val VERSION = "13.3.7"
    const val MISSING_VERSION = "13.3.8"
    val DEPENDENCIES =
      listOf(
        FhirNpmPackage("hl7.fhir.r4.core", "4.0.1"),
      )
  }
}
