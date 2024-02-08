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
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.anyString
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SHLinkDecoderImplTest {

  private lateinit var shLinkDecoderImpl: SHLinkDecoderImpl

  @Mock private lateinit var readSHLinkUtils: ReadSHLinkUtils

  private val mockWebServer = MockWebServer()
  private val baseUrl = "/shl/"

  private val shlRetrofitService by lazy {
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
  private val exampleSHL =
    "shlink:/eyJsYWJlbCI6IkN1c3RvbSBEYXRhc2V0IDExIiwidXJsIjoiaHR0cHM6Ly9hcGkudmF4eC5saW5rL2FwaS9zaGwveFJ4M2Q0QzNROE0wbnhaejZmc1ZYdGYyLW5QTDlwUXdBb2RRZVVYNzFqYyIsImZsYWciOiIiLCJrZXkiOiI0RXNvSFF3WXdTLU8wVW43WkNBQXlSMnQ2TVJ3WjJpSndLMFlZTnBNcEZFIn0"
  private val exampleJsonData = "{\"passcode\": \"\", \"recipient\": \"Example SHL Client\"}"
  private val filesWithEmbedded =
    "{\"files\": [{\"contentType\": \"application/smart-health-card\", \"embedded\": \"embeddedData\"}]}"
  private val filesWithLocation =
    "{\"files\": [{\"contentType\": \"application/smart-health-card\", \"location\": \"https://api.vaxx.link/api/shl/xRx3d4C3Q8M0nxZz6fsVXtf2-nPL9pQwAodQeUX71jc/file/EGxABJF-Co4oplPtLN87HpSlydj9K_BhCip1sGUvevY?ticket=...\"}]}"
  private val testBundleString = "{\"resourceType\" : \"Bundle\"}"

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    shLinkDecoderImpl = SHLinkDecoderImpl(readSHLinkUtils, shlRetrofitService)
  }

  @After
  fun tearDown() {
    mockWebServer.shutdown()
  }

  @Test
  fun `test decodeSHLinkToDocument with embedded data and no verifiable content`() = runBlocking {
    val mockResponse = MockResponse().setResponseCode(200).setBody(filesWithEmbedded)
    mockWebServer.enqueue(mockResponse)

    `when`(readSHLinkUtils.extractUrl("fullLink")).thenReturn("extractedJson")
    `when`(readSHLinkUtils.decodeUrl("extractedJson")).thenReturn("{}".toByteArray())
    `when`(readSHLinkUtils.decodeShc(anyString(), anyString())).thenReturn(testBundleString)
    `when`(readSHLinkUtils.extractVerifiableCredential(testBundleString)).thenReturn("")

    val result =
      shLinkDecoderImpl.decodeSHLinkToDocument(
        exampleSHL,
        exampleJsonData,
      )
    assertThat(result).isNotNull()
    assertThat(result!!.document).isNotNull()
  }

  @Test
  fun `test decodeSHLinkToDocument with externally stored data and no verifiable content`() =
    runBlocking {
      val mockResponse = MockResponse().setResponseCode(200).setBody(filesWithLocation)
      mockWebServer.enqueue(mockResponse)

      val mockGetLocationResponse = MockResponse().setResponseCode(200).setBody(getLocationResponse)
      mockWebServer.enqueue(mockGetLocationResponse)

      `when`(readSHLinkUtils.extractUrl("fullLink")).thenReturn("extractedJson")
      `when`(readSHLinkUtils.decodeUrl("extractedJson")).thenReturn("{}".toByteArray())
      `when`(readSHLinkUtils.decodeShc(anyString(), anyString())).thenReturn(testBundleString)
      `when`(readSHLinkUtils.extractVerifiableCredential(testBundleString)).thenReturn("")

      val result = shLinkDecoderImpl.decodeSHLinkToDocument(exampleSHL, exampleJsonData)
      assertThat(result).isNotNull()
      assertThat(result!!.document).isNotNull()

      val recordedRequestGetManifest: RecordedRequest = mockWebServer.takeRequest()
      assertThat(recordedRequestGetManifest.path)
        .isEqualTo("/shl/xRx3d4C3Q8M0nxZz6fsVXtf2-nPL9pQwAodQeUX71jc")

      val recordedRequestGetLocation: RecordedRequest = mockWebServer.takeRequest()
      assertThat(
          recordedRequestGetLocation.path!!,
        )
        .contains(
          "/shl/file/EGxABJF-Co4oplPtLN87HpSlydj9K_BhCip1sGUvevY?ticket=",
        )
    }

  @Test
  fun `test decodeSHLinkToDocument with no data stored at the external location provided`() =
    runBlocking {
      val mockResponse =
        MockResponse()
          .setResponseCode(200)
          .setBody(
            filesWithLocation,
          )
      mockWebServer.enqueue(mockResponse)

      val mockGetLocationResponse = MockResponse().setResponseCode(200).setBody("")
      mockWebServer.enqueue(mockGetLocationResponse)

      `when`(readSHLinkUtils.extractUrl("fullLink")).thenReturn("extractedJson")
      `when`(readSHLinkUtils.decodeUrl("extractedJson")).thenReturn("{}".toByteArray())
      `when`(readSHLinkUtils.decodeShc(anyString(), anyString())).thenReturn(testBundleString)
      `when`(readSHLinkUtils.extractVerifiableCredential(testBundleString)).thenReturn("")

      val result = runCatching {
        shLinkDecoderImpl.decodeSHLinkToDocument(exampleSHL, exampleJsonData)
      }
      assertThat(result.isFailure).isTrue()
      assertThat(result.exceptionOrNull()!!::class).isEqualTo(Error::class)
    }

  @Test
  fun `test decodeSHLinkToDocument with an invalid SHL passed in`() = runBlocking {
    val result = runCatching {
      shLinkDecoderImpl.decodeSHLinkToDocument("invalidLink", exampleJsonData)
    }
    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()!!::class).isEqualTo(IllegalArgumentException::class)
    assertThat(result.exceptionOrNull()!!.message).isEqualTo("Not a valid SHLink")
  }

  @Test
  fun `test decodeSHLinkToDocument with an empty SHL passed in`() = runBlocking {
    val result = runCatching { shLinkDecoderImpl.decodeSHLinkToDocument("", exampleJsonData) }
    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()!!::class).isEqualTo(IllegalArgumentException::class)
    assertThat(result.exceptionOrNull()!!.message).isEqualTo("Not a valid SHLink")
  }

  @Test
  fun `test decodeSHLinkToDocument with an unsuccessful POST to the manifest URL`() = runBlocking {
    val mockResponse =
      MockResponse().setResponseCode(500).setBody(manifestFileResponseWithLocation.toString())
    mockWebServer.enqueue(mockResponse)

    val jsonData = "test json data"

    `when`(readSHLinkUtils.extractUrl("fullLink")).thenReturn("extractedJson")
    `when`(readSHLinkUtils.decodeUrl("extractedJson")).thenReturn("{}".toByteArray())

    val result = runCatching { shLinkDecoderImpl.decodeSHLinkToDocument(exampleSHL, jsonData) }

    assertThat(result.isFailure).isTrue()
    assertThat(result.exceptionOrNull()!!::class).isEqualTo(Error::class)
    assertThat(result.exceptionOrNull()!!.message).contains("Error posting to the manifest:")
  }

  @Test
  fun `test decodeSHLinkToDocument with embedded data and verifiable content`() = runBlocking {
    val mockResponse =
      MockResponse().setResponseCode(200).setBody(manifestFileResponseWithEmbedded.toString())
    mockWebServer.enqueue(mockResponse)

    `when`(readSHLinkUtils.extractUrl("fullLink")).thenReturn("extractedJson")
    `when`(readSHLinkUtils.decodeUrl("extractedJson")).thenReturn("{}".toByteArray())
    `when`(readSHLinkUtils.decodeShc(anyString(), anyString())).thenReturn(testBundleString)
    `when`(readSHLinkUtils.extractVerifiableCredential(testBundleString))
      .thenReturn("verifiableCredentialData")
    `when`(readSHLinkUtils.decodeAndDecompressPayload("verifiableCredentialData"))
      .thenReturn(
        "{\"vc\": {\"credentialSubject\":{\"fhirBundle\":{\"resourceType\":\"Bundle\"}}}}",
      )

    val result = shLinkDecoderImpl.decodeSHLinkToDocument(exampleSHL, exampleJsonData)
    assertThat(result).isNotNull()
    assertThat(result!!.document).isNotNull()

    val recordedRequestGetManifest: RecordedRequest = mockWebServer.takeRequest()
    assertThat(recordedRequestGetManifest.path)
      .isEqualTo(
        "/shl/xRx3d4C3Q8M0nxZz6fsVXtf2-nPL9pQwAodQeUX71jc",
      )
  }
}
