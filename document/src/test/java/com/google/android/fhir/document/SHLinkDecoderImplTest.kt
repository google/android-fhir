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

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.NetworkConfiguration
import com.google.android.fhir.document.decode.ReadSHLinkUtils
import com.google.android.fhir.document.decode.SHLinkDecoderImpl
import com.google.android.fhir.document.scan.SHLinkScanData
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hl7.fhir.r4.model.Bundle
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertNotNull
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
  private val baseUrl = ""

  private val apiService by lazy {
    RetrofitSHLService.Builder(mockWebServer.url(baseUrl).toString(), NetworkConfiguration())
      .build()
  }

  private val manifestFileResponse = JSONObject().apply {
    val filesArray = JSONArray().apply {
      val fileObject = JSONObject().apply {
        put("embedded","embeddedData1")
      }
      put(fileObject)
    }
    put("files", filesArray)
  }

  private val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    shLinkDecoderImpl = SHLinkDecoderImpl(shLinkScanData, readSHLinkUtils, apiService)

    val mockResponse = MockResponse().setResponseCode(200).setBody(manifestFileResponse.toString())
    mockWebServer.enqueue(mockResponse)
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

  @Test
  fun testDecodeSHLinkToDocumentWithEmbeddedAndNoVC() = runBlocking {
    val jsonData = "test json data"
    val testBundleString = "{\"resourceType\" : \"Bundle\"}"
    val testBundle = parser.parseResource(testBundleString) as Bundle
    `when`(readSHLinkUtils.extractUrl("fullLink")).thenReturn("extractedJson")
    `when`(readSHLinkUtils.decodeUrl("extractedJson")).thenReturn("{}".toByteArray())

    `when`(shLinkScanData.fullLink).thenReturn("fullLink")
    `when`(shLinkScanData.manifestUrl).thenReturn("url")
    `when`(shLinkScanData.flag).thenReturn("flag")
    `when`(shLinkScanData.key).thenReturn("key")

    `when`(readSHLinkUtils.decodeShc("embeddedData1", "")).thenReturn(testBundleString)
    `when`(readSHLinkUtils.extractVerifiableCredential(testBundleString)).thenReturn("")

    val result = shLinkDecoderImpl.decodeSHLinkToDocument(jsonData)
    assertNotNull(result)
  }
}
