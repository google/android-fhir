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
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
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

  private val manifestFileResponseWithEmbedded =
    JSONObject().apply {
      val filesArray =
        JSONArray().apply {
          val fileObject = JSONObject().apply { put("embedded", "embeddedData") }
          put(fileObject)
        }
      put("files", filesArray)
    }

  private val manifestFileResponseWithLocation =
    JSONObject().apply {
      val filesArray =
        JSONArray().apply {
          val fileObject = JSONObject().apply { put("location", "locationData") }
          put(fileObject)
        }
      put("files", filesArray)
    }

  private val getLocationResponse = "locationData"

  private val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    shLinkDecoderImpl = SHLinkDecoderImpl(shLinkScanData, readSHLinkUtils, apiService)
  }

  @After
  fun tearDown() {
    mockWebServer.shutdown()
  }

  @Test
  fun testDecodeSHLinkToDocumentWithEmbeddedAndNoVC() = runBlocking {
    val mockResponse =
      MockResponse().setResponseCode(200).setBody(manifestFileResponseWithEmbedded.toString())
    mockWebServer.enqueue(mockResponse)

    val jsonData = "test json data"
    val testBundleString = "{\"resourceType\" : \"Bundle\"}"

    `when`(readSHLinkUtils.extractUrl("fullLink")).thenReturn("extractedJson")
    `when`(readSHLinkUtils.decodeUrl("extractedJson")).thenReturn("{}".toByteArray())
    `when`(shLinkScanData.fullLink).thenReturn("fullLink")
    `when`(readSHLinkUtils.decodeShc("embeddedData", "")).thenReturn(testBundleString)
    `when`(readSHLinkUtils.extractVerifiableCredential(testBundleString)).thenReturn("")

    val result = shLinkDecoderImpl.decodeSHLinkToDocument(jsonData)
    assertNotNull(result)
    assertNotNull(result!!.document)

    val recordedRequestGetManifest: RecordedRequest = mockWebServer.takeRequest()
    assertEquals("/fullLink", recordedRequestGetManifest.path)
  }

  @Test
  fun testDecodeSHLinkToDocumentWithLocationAndNoVC() = runBlocking {
    val mockResponse =
      MockResponse().setResponseCode(200).setBody(manifestFileResponseWithLocation.toString())
    mockWebServer.enqueue(mockResponse)

    val mockGetLocationResponse = MockResponse().setResponseCode(200).setBody(getLocationResponse)
    mockWebServer.enqueue(mockGetLocationResponse)

    val jsonData = "test json data"
    val testBundleString = "{\"resourceType\" : \"Bundle\"}"

    `when`(readSHLinkUtils.extractUrl("fullLink")).thenReturn("extractedJson")
    `when`(readSHLinkUtils.decodeUrl("extractedJson")).thenReturn("{}".toByteArray())
    `when`(shLinkScanData.fullLink).thenReturn("fullLink")
    `when`(readSHLinkUtils.decodeShc("locationData", "")).thenReturn(testBundleString)
    `when`(readSHLinkUtils.extractVerifiableCredential(testBundleString)).thenReturn("")

    val result = shLinkDecoderImpl.decodeSHLinkToDocument(jsonData)
    assertNotNull(result)
    assertNotNull(result!!.document)

    val recordedRequestGetManifest: RecordedRequest = mockWebServer.takeRequest()
    assertEquals("/fullLink", recordedRequestGetManifest.path)

    val recordedRequestGetLocation: RecordedRequest = mockWebServer.takeRequest()
    assertEquals("/locationData", recordedRequestGetLocation.path)
  }

  @Test
  fun testDecodeSHLinkToDocumentWithNoDataAtLocation() = runBlocking {
    val mockResponse =
      MockResponse().setResponseCode(200).setBody(manifestFileResponseWithLocation.toString())
    mockWebServer.enqueue(mockResponse)

    val mockGetLocationResponse = MockResponse().setResponseCode(200).setBody(getLocationResponse)
    mockWebServer.enqueue(mockGetLocationResponse)

    val jsonData = "test json data"
    val testBundleString = "{\"resourceType\" : \"Bundle\"}"

    `when`(readSHLinkUtils.extractUrl("fullLink")).thenReturn("extractedJson")
    `when`(readSHLinkUtils.decodeUrl("extractedJson")).thenReturn("{}".toByteArray())
    `when`(shLinkScanData.fullLink).thenReturn("fullLink")
    `when`(readSHLinkUtils.decodeShc("locationData", "")).thenReturn("")
    `when`(readSHLinkUtils.extractVerifiableCredential(testBundleString)).thenReturn("")

    val result = runCatching { shLinkDecoderImpl.decodeSHLinkToDocument(jsonData) }

    val recordedRequestGetManifest: RecordedRequest = mockWebServer.takeRequest()
    assertEquals("/fullLink", recordedRequestGetManifest.path)

    val recordedRequestGetLocation: RecordedRequest = mockWebServer.takeRequest()
    assertEquals("/locationData", recordedRequestGetLocation.path)

    assertTrue(result.isFailure)
    assertEquals(Error::class, result.exceptionOrNull()!!::class)
  }

  @Test
  fun testDecodeSHLinkToDocumentWithEmptyFullLink() = runBlocking {
    val jsonData = "test json data"
    `when`(shLinkScanData.fullLink).thenReturn("")

    val result = runCatching { shLinkDecoderImpl.decodeSHLinkToDocument(jsonData) }
    assertTrue(result.isFailure)
    assertEquals(IllegalArgumentException::class, result.exceptionOrNull()!!::class)
    assertEquals(
      "Provided SHLinkScanData object's fullLink is empty",
      result.exceptionOrNull()!!.message,
    )
  }

  @Test
  fun testDecodeSHLinkToDocumentWithoutAFullLink() = runBlocking {
    val jsonData = "test json data"
    `when`(shLinkScanData.fullLink).thenReturn(null)

    val result = runCatching { shLinkDecoderImpl.decodeSHLinkToDocument(jsonData) }
    assertTrue(result.isFailure)
    assertEquals(NullPointerException::class, result.exceptionOrNull()!!::class)
    assertEquals(
      "Provided SHLinkScanData object's fullLink has not been initialised",
      result.exceptionOrNull()!!.message,
    )
  }

  @Test
  fun testDecodeSHLinkToDocumentWithUnsuccessfulManifestPost() = runBlocking {
    val mockResponse =
      MockResponse().setResponseCode(500).setBody(manifestFileResponseWithLocation.toString())
    mockWebServer.enqueue(mockResponse)

    val jsonData = "test json data"

    `when`(readSHLinkUtils.extractUrl("fullLink")).thenReturn("extractedJson")
    `when`(readSHLinkUtils.decodeUrl("extractedJson")).thenReturn("{}".toByteArray())
    `when`(shLinkScanData.fullLink).thenReturn("fullLink")

    val result = runCatching { shLinkDecoderImpl.decodeSHLinkToDocument(jsonData) }

    assertTrue(result.isFailure)
    assertEquals(Error::class, result.exceptionOrNull()!!::class)
    assert(result.exceptionOrNull()!!.message!!.contains("Error posting to the manifest:"))
  }

  @Test
  fun testDecodeEmbeddedArrayWithVerifiableCredential() = runBlocking {
    val mockResponse =
      MockResponse().setResponseCode(200).setBody(manifestFileResponseWithEmbedded.toString())
    mockWebServer.enqueue(mockResponse)

    val jsonData = "test json data"
    val testBundleString = "{\"resourceType\" : \"Bundle\"}"

    `when`(readSHLinkUtils.extractUrl("fullLink")).thenReturn("extractedJson")
    `when`(readSHLinkUtils.decodeUrl("extractedJson")).thenReturn("{}".toByteArray())
    `when`(shLinkScanData.fullLink).thenReturn("fullLink")
    `when`(readSHLinkUtils.decodeShc("embeddedData", "")).thenReturn(testBundleString)
    `when`(readSHLinkUtils.extractVerifiableCredential(testBundleString))
      .thenReturn("verifiableCredentialData")
    `when`(readSHLinkUtils.decodeAndDecompressPayload("verifiableCredentialData"))
      .thenReturn(
        "{\"vc\": {\"credentialSubject\":{\"fhirBundle\":{\"resourceType\":\"Bundle\"}}}}",
      )

    val result = shLinkDecoderImpl.decodeSHLinkToDocument(jsonData)
    assertNotNull(result)
    assertNotNull(result!!.document)

    val recordedRequestGetManifest: RecordedRequest = mockWebServer.takeRequest()
    assertEquals("/fullLink", recordedRequestGetManifest.path)
  }
}
