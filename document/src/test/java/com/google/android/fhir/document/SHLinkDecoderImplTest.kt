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
import org.junit.Assert.assertNotNull
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

  private val apiService by lazy {
    RetrofitSHLService.Builder(mockWebServer.url(baseUrl).toString(), NetworkConfiguration())
      .build()
  }

  private val manifestFileResponseWithEmbedded = JSONObject().apply {
    val filesArray = JSONArray().apply {
      val fileObject = JSONObject().apply { put("embedded", "embeddedData") }
      put(fileObject)
    }
    put("files", filesArray)
  }

  private val manifestFileResponseWithLocation = JSONObject().apply {
    val filesArray = JSONArray().apply {
      val fileObject = JSONObject().apply { put("location", "locationData") }
      put(fileObject)
    }
    put("files", filesArray)
  }

  private val getLocationResponse = "locationData"
  private val exampleSHL =
    "shlink:/eyJsYWJlbCI6IkN1c3RvbSBEYXRhc2V0IDExIiwidXJsIjoiaHR0cHM6Ly9hcGkudmF4eC5saW5rL2FwaS9zaGwveFJ4M2Q0QzNROE0wbnhaejZmc1ZYdGYyLW5QTDlwUXdBb2RRZVVYNzFqYyIsImZsYWciOiIiLCJrZXkiOiI0RXNvSFF3WXdTLU8wVW43WkNBQXlSMnQ2TVJ3WjJpSndLMFlZTnBNcEZFIn0"
  private val exampleJsonData = "{\"passcode\": \"\", \"recipient\": \"Example SHL Client\"}"

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    shLinkDecoderImpl = SHLinkDecoderImpl(readSHLinkUtils, apiService)
  }

  @After
  fun tearDown() {
    mockWebServer.shutdown()
  }

  @Test
  fun `test decodeSHLinkToDocument with embedded data and no verifiable content`() = runBlocking {
    val mockResponse = MockResponse().setResponseCode(200).setBody(
        "{\n" + "    \"files\": [\n" + "        {\n" + "            \"contentType\": \"application/smart-health-card\",\n" + "            \"embedded\": \"eyJhbGciOiJkaXIiLCJlbmMiOiJBMjU2R0NNIiwiY29udGVudFR5cGUiOiJhcHBsaWNhdGlvbi9zbWFydC1oZWFsdGgtY2FyZCJ9..1r1opfOWe6C8z9hw.ogAcofRDaoXMC2r1EUUWgzD5BhcgNOB63Sxb_9TF5NsT2hWmXIf9IjaEAe4ThCus1CzL-u3JGLF0_D85B8ZLKTSRgKL4AEdh9baxFSKu0LySdcS6Qigi_mvVGvztyUjFUY40WKfsSY2F2_6vD82itPWoh-j-2X5L2dnMgYWwGIrXMSx8p7N5VDOqrvrzswaaQ6hSS506a4Mbn0FdDWkDMm1P50QWyImEN_LkGH3mA926b4Cusmd2ImW8cvBKpP_vuCrSm_NHeDi0yl8SgZBq-absSSOLxdyoq_xnRISd6CXMXChIzxeOGeLvAtWQSWYEDCG75nCuLxplZN675MG0uFHPhh2jJIo6pXGUdldHrzzVlPp0UUEViHMkL8wGQNYOpl2-IdOhEddAGXNL-YCPh2I_n7uWWZ2CtOBrGS-BydYhQzl15BP2jBghgXaZqbl6dz-58WNAShvNCtJXTQu0EUQj4pIwOehd_kD0spDYol7KTSjAsg2Hwa2Vsn58KN1MqjoU0Srzhc4i9nXQNLmwzJJvr2Ub_dzrekvD3Lr9QtSvZf7bcZFenJeq8t_G-fHKj-Wpc-VGWM_di06jp0AnvB6aLkQFV1Lm5p0Deh-D1Zk15oQOyn02ZMH3zz162cL2rTBqBPMw6P7wY53HfAmi6w-1IDGhyEFCKgTjhHAdoN5olJ4tte1RRGbMQqjkyWY9M-DMLduZq8ztMu90qhe_4lGm7jADQWHtVmg0ZPAbVa6VBL-vTKhcKAmUCbshz8iZCC-83K_-3aRnYVZSEu7MgpcwedFwEvdMN__vhKjpFMowaDkBTeUjU_RGmVFSpLtFUgU1Io0p3qJkdZnRPAKHxMIrDxHGMyEdiJnzBlGkdoIfiiPOZNxhWjaWH-yQtklFabntNnacDkbiQGmBHsTe_je9svO4BpDqhped-DL3igFfLOjktW-QfK8kP5Ha7unb3ZhO8-ip2BJrAr1dvicQ6bTiAE4L.1Zwx0ZU-8ja9BIrgZKPHUw\",\n" + "            \"location\": \"https://api.vaxx.link/api/shl/xRx3d4C3Q8M0nxZz6fsVXtf2-nPL9pQwAodQeUX71jc/file/EGxABJF-Co4oplPtLN87HpSlydj9K_BhCip1sGUvevY?ticket=qo1dHKcLGGQ7Vu0uReyLlX-Q2vITloIZyrddMaoL93g\"\n" + "        }\n" + "    ]\n" + "}",
      )
    mockWebServer.enqueue(mockResponse)

    val testBundleString = "{\"resourceType\" : \"Bundle\"}"

    `when`(readSHLinkUtils.extractUrl("fullLink")).thenReturn("extractedJson")
    `when`(readSHLinkUtils.decodeUrl("extractedJson")).thenReturn("{}".toByteArray())
    `when`(readSHLinkUtils.decodeShc(anyString(), anyString())).thenReturn(testBundleString)
    `when`(readSHLinkUtils.extractVerifiableCredential(testBundleString)).thenReturn("")

    val result = shLinkDecoderImpl.decodeSHLinkToDocument(
      exampleSHL,
      exampleJsonData,
    )
    assertNotNull(result)
    assertNotNull(result!!.document)
  }

  @Test
  fun `test decodeSHLinkToDocument with externally stored data and no verifiable content`() =
    runBlocking {
      val mockResponse = MockResponse().setResponseCode(200).setBody(
          "{\n" + "    \"files\": [\n" + "        {\n" + "            \"contentType\": \"application/smart-health-card\",\n" + "            \"location\": \"https://api.vaxx.link/api/shl/xRx3d4C3Q8M0nxZz6fsVXtf2-nPL9pQwAodQeUX71jc/file/EGxABJF-Co4oplPtLN87HpSlydj9K_BhCip1sGUvevY?ticket=qo1dHKcLGGQ7Vu0uReyLlX-Q2vITloIZyrddMaoL93g\"\n" + "        }\n" + "    ]\n" + "}",
        )
      mockWebServer.enqueue(mockResponse)

      val mockGetLocationResponse = MockResponse().setResponseCode(200).setBody(getLocationResponse)
      mockWebServer.enqueue(mockGetLocationResponse)

      val testBundleString = "{\"resourceType\" : \"Bundle\"}"

      `when`(readSHLinkUtils.extractUrl("fullLink")).thenReturn("extractedJson")
      `when`(readSHLinkUtils.decodeUrl("extractedJson")).thenReturn("{}".toByteArray())
      `when`(readSHLinkUtils.decodeShc(anyString(), anyString())).thenReturn(testBundleString)
      `when`(readSHLinkUtils.extractVerifiableCredential(testBundleString)).thenReturn("")

      val result = shLinkDecoderImpl.decodeSHLinkToDocument(exampleSHL, exampleJsonData)
      assertNotNull(result)
      assertNotNull(result!!.document)

      val recordedRequestGetManifest: RecordedRequest = mockWebServer.takeRequest()
      assertThat(recordedRequestGetManifest.path).isEqualTo("/shl/xRx3d4C3Q8M0nxZz6fsVXtf2-nPL9pQwAodQeUX71jc")

      val recordedRequestGetLocation: RecordedRequest = mockWebServer.takeRequest()
      assertThat(
        recordedRequestGetLocation.path!!
      ).contains(
        "/shl/file/EGxABJF-Co4oplPtLN87HpSlydj9K_BhCip1sGUvevY?ticket=",
      )
    }

  @Test
  fun `test decodeSHLinkToDocument with no data stored at the external location provided`() =
    runBlocking {
      val mockResponse = MockResponse().setResponseCode(200).setBody(
          "{\n" + "    \"files\": [\n" + "        {\n" + "            \"contentType\": \"application/smart-health-card\",\n" + "            \"location\": \"https://api.vaxx.link/api/shl/xRx3d4C3Q8M0nxZz6fsVXtf2-nPL9pQwAodQeUX71jc/file/EGxABJF-Co4oplPtLN87HpSlydj9K_BhCip1sGUvevY?ticket=qo1dHKcLGGQ7Vu0uReyLlX-Q2vITloIZyrddMaoL93g\"\n" + "        }\n" + "    ]\n" + "}",
        )
      mockWebServer.enqueue(mockResponse)

      val mockGetLocationResponse = MockResponse().setResponseCode(200).setBody("")
      mockWebServer.enqueue(mockGetLocationResponse)

      val testBundleString = "{\"resourceType\" : \"Bundle\"}"

      `when`(readSHLinkUtils.extractUrl("fullLink")).thenReturn("extractedJson")
      `when`(readSHLinkUtils.decodeUrl("extractedJson")).thenReturn("{}".toByteArray())
      `when`(readSHLinkUtils.decodeShc(anyString(), anyString())).thenReturn(testBundleString)
      `when`(readSHLinkUtils.extractVerifiableCredential(testBundleString)).thenReturn("")

      val result = runCatching {
        shLinkDecoderImpl.decodeSHLinkToDocument(exampleSHL, exampleJsonData)
      }
      assertThat(result.isFailure).isTrue()
      assertThat(result.exceptionOrNull()!!::class).isEqualTo(Error::class.java)
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

    val testBundleString = "{\"resourceType\" : \"Bundle\"}"

    `when`(readSHLinkUtils.extractUrl("fullLink")).thenReturn("extractedJson")
    `when`(readSHLinkUtils.decodeUrl("extractedJson")).thenReturn("{}".toByteArray())
    `when`(readSHLinkUtils.decodeShc(anyString(), anyString())).thenReturn(testBundleString)
    `when`(readSHLinkUtils.extractVerifiableCredential(testBundleString)).thenReturn("verifiableCredentialData")
    `when`(readSHLinkUtils.decodeAndDecompressPayload("verifiableCredentialData")).thenReturn(
        "{\"vc\": {\"credentialSubject\":{\"fhirBundle\":{\"resourceType\":\"Bundle\"}}}}",
      )

    val result = shLinkDecoderImpl.decodeSHLinkToDocument(exampleSHL, exampleJsonData)
    assertThat(result).isNotNull()
    assertThat(result!!.document).isNotNull()

    val recordedRequestGetManifest: RecordedRequest = mockWebServer.takeRequest()
    assertThat(recordedRequestGetManifest.path).isEqualTo(
      "/shl/xRx3d4C3Q8M0nxZz6fsVXtf2-nPL9pQwAodQeUX71jc"
    )
  }
}
