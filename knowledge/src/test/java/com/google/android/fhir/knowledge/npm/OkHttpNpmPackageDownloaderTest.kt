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

package com.google.android.fhir.knowledge.npm

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.android.fhir.knowledge.FhirNpmPackage
import com.google.android.fhir.knowledge.files.NpmFileManager
import com.google.common.truth.Truth.assertThat
import java.io.IOException
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Buffer
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OkHttpNpmPackageDownloaderTest {

  @get:Rule val mockWebServer = MockWebServer()

  private val rootCacheFolder = ApplicationProvider.getApplicationContext<Context>().dataDir
  private val npmFileManager = NpmFileManager(rootCacheFolder)

  @Test
  fun downloadPackage_returnsNpmPackage() = runTest {
    val downloader =
      OkHttpNpmPackageDownloader(mockWebServer.url("/packages/$PACKAGE_ID#$VERSION").toString())

    val fhirNpmPackage = FhirNpmPackage(PACKAGE_ID, VERSION)
    val testFileBytes = javaClass.getResourceAsStream("/okhttp_downloader/package.tgz")!!
    val testFileBuffer = Buffer().readFrom(testFileBytes)
    val responseBody = MockResponse().setResponseCode(200).setBody(testFileBuffer)
    mockWebServer.enqueue(responseBody)

    downloader.downloadPackage(
      fhirNpmPackage,
      npmFileManager.getPackageDir(fhirNpmPackage.name, fhirNpmPackage.version),
    )

    val npmPackage =
      npmFileManager.getLocalFhirNpmPackageMetadata(fhirNpmPackage.name, fhirNpmPackage.version)
    assertThat(npmPackage.packageId).isEqualTo(PACKAGE_ID)
    assertThat(npmPackage.version).isEqualTo(VERSION)
    assertThat(npmPackage.dependencies).isEqualTo(DEPENDENCIES)
  }

  @Test(expected = IOException::class)
  fun testDownloadPackage_serverError_throwsException() = runTest {
    val downloader =
      OkHttpNpmPackageDownloader(mockWebServer.url("/packages/$PACKAGE_ID#$VERSION").toString())
    val fhirNpmPackage = FhirNpmPackage(PACKAGE_ID, VERSION)

    val responseBody = MockResponse().setResponseCode(500)

    mockWebServer.enqueue(responseBody)

    downloader.downloadPackage(
      fhirNpmPackage,
      npmFileManager.getPackageDir(fhirNpmPackage.name, fhirNpmPackage.version),
    )
  }

  companion object {
    const val PACKAGE_ID = "test-package"
    const val VERSION = "13.3.7"
    val DEPENDENCIES =
      listOf(
        FhirNpmPackage("hl7.fhir.r4.core", "4.0.1"),
        FhirNpmPackage("hl7.terminology.r4", "5.0.0"),
        FhirNpmPackage("hl7.fhir.fr.core", "1.1.0"),
      )
  }
}
