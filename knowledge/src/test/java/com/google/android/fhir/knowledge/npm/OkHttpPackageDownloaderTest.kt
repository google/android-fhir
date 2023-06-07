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

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.android.fhir.knowledge.ImplementationGuide
import com.google.common.truth.Truth.assertThat
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Buffer
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class OkHttpPackageDownloaderTest {

  @get:Rule val mockWebServer = MockWebServer()

  private val rootCacheFolder = ApplicationProvider.getApplicationContext<Context>().dataDir
  private val cacheManager = CacheManager(rootCacheFolder)
  private var downloader = OkHttpPackageDownloader(cacheManager)

  @Test
  fun downloadPackage_returnsNpmPackage() = runTest {
    val packageServerUrl = mockWebServer.url("/packages/$PACKAGE_ID#$VERSION").toString()
    val implementationGuide = ImplementationGuide(PACKAGE_ID, VERSION)
    val testFileBytes = javaClass.getResourceAsStream("/okhttp_downloader/package.tgz")!!
    val testFileBuffer = Buffer().readFrom(testFileBytes)
    val responseBody = MockResponse().setResponseCode(200).setBody(testFileBuffer)
    mockWebServer.enqueue(responseBody)

    val npmPackage = downloader.downloadPackage(implementationGuide, packageServerUrl)

    assertThat(npmPackage.packageId).isEqualTo(PACKAGE_ID)
    assertThat(npmPackage.version).isEqualTo(VERSION)
    assertThat(npmPackage.dependencies).isEqualTo(DEPENDENCIES)
  }

  @Test(expected = IOException::class)
  fun testDownloadPackage_serverError_throwsException() = runTest {
    val packageServerUrl = mockWebServer.url("/packages/$PACKAGE_ID#$VERSION").toString()
    val implementationGuide = ImplementationGuide(PACKAGE_ID, VERSION)

    val responseBody = MockResponse().setResponseCode(500)

    mockWebServer.enqueue(responseBody)

    downloader.downloadPackage(implementationGuide, packageServerUrl)
  }

  companion object {
    const val PACKAGE_ID = "test-package"
    const val VERSION = "13.3.7"
    val DEPENDENCIES =
      listOf(
        ImplementationGuide("hl7.fhir.r4.core", "4.0.1"),
        ImplementationGuide("hl7.terminology.r4", "5.0.0"),
        ImplementationGuide("hl7.fhir.fr.core", "1.1.0")
      )
  }
}
