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

package com.google.android.fhir.document

import com.google.android.fhir.NetworkConfiguration
import com.google.android.fhir.document.decode.ReadSHLinkUtils
import com.google.android.fhir.document.decode.SHLinkDecoderImpl
import com.google.android.fhir.document.scan.SHLinkScanData
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SHLinkDecoderImplTest {
  private lateinit var shLinkDecoderImpl: SHLinkDecoderImpl

  @Mock private lateinit var readSHLinkUtils: ReadSHLinkUtils
  @Mock private lateinit var shLinkScanData: SHLinkScanData

  private val mockWebServer = MockWebServer()
  private val baseUrl = "/shl/"

  private val apiService by lazy {
    RetrofitSHLService.Builder(mockWebServer.url(baseUrl).toString(), NetworkConfiguration())
      .build()
  }

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    // val shLinkScanData = SHLinkScanData("shlink:/fullLink", "extractedJson", "url", "key", "flag")
    shLinkDecoderImpl = SHLinkDecoderImpl(shLinkScanData, readSHLinkUtils, apiService)

    val manifestResponse = MockResponse().setResponseCode(200).setBody("{'files': []}")
    mockWebServer.enqueue(manifestResponse)
  }

  @After
  fun tearDown() {
    mockWebServer.shutdown()
  }

  @Test
  fun testDecodeSHLinkToDocumentWithEmptyJSON() = runTest {
    shLinkDecoderImpl.decodeSHLinkToDocument("{}")
    `when`(readSHLinkUtils.extractUrl(anyString())).thenReturn("url")
    `when`(readSHLinkUtils.decodeUrl(anyString())).thenReturn(null)
  }
}
