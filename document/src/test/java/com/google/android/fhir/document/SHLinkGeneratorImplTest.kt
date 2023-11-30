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

package com.google.android.fhir.document

import android.util.Base64
import com.google.android.fhir.NetworkConfiguration
import com.google.android.fhir.document.generate.EncryptionUtils
import com.google.android.fhir.document.generate.SHLinkGenerationData
import com.google.android.fhir.document.generate.SHLinkGeneratorImpl
import java.time.Instant
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hl7.fhir.r4.model.Bundle
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class SHLinkGeneratorImplTest {

  @Mock private lateinit var encryptionUtility: EncryptionUtils
  private lateinit var shLinkGeneratorImpl: SHLinkGeneratorImpl

  private val mockWebServer = MockWebServer()
  private val baseUrl = "/shl/"

  private val apiService by lazy {
    RetrofitSHLService.Builder(mockWebServer.url(baseUrl).toString(), NetworkConfiguration())
      .build()
  }

  private val mockIPSDocument = mock(IPSDocument::class.java)
  private val initialPostResponse =
    JSONObject().apply {
      put("id", "123")
      put("managementToken", "token123")
    }

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    shLinkGeneratorImpl = SHLinkGeneratorImpl(apiService, encryptionUtility)
    `when`(mockIPSDocument.document).thenReturn(Bundle())
    `when`(
        encryptionUtility.encrypt(
          Mockito.anyString(),
          Mockito.anyString(),
        ),
      )
      .thenReturn("something")
    `when`(encryptionUtility.generateRandomKey()).thenReturn("key")

    // Mock response for getManifestUrlAndToken
    val mockResponse = MockResponse().setResponseCode(200).setBody(initialPostResponse.toString())
    mockWebServer.enqueue(mockResponse)

    // Mock response for postPayload
    val postPayloadResponse =
      MockResponse().setResponseCode(200).setBody("{'test key': 'test value'}")
    mockWebServer.enqueue(postPayloadResponse)
  }

  private suspend fun assertCommonFunctionality(
    shLinkGenerationData: SHLinkGenerationData,
    passcode: String,
    optionalViewer: String,
    expectedExp: String? = null,
    expectedFlag: String? = null,
    expectedLabel: String? = null,
  ) {
    val result =
      shLinkGeneratorImpl.generateSHLink(
        shLinkGenerationData,
        passcode,
        "",
        optionalViewer,
      )

    val recordedRequestGetManifest: RecordedRequest = mockWebServer.takeRequest()
    assertEquals("/shl/", recordedRequestGetManifest.path)
    val recordedRequestPostPayload: RecordedRequest = mockWebServer.takeRequest()
    assertEquals("/shl/123", recordedRequestPostPayload.path)

    val parts = result.split("#shlink:/")
    assertTrue(parts.size == 2)

    val decodedJSON =
      with(parts[1]) {
        val decodedString = String(Base64.decode(this, Base64.URL_SAFE), Charsets.UTF_8)
        JSONObject(decodedString)
      }

    expectedExp?.let {
      assertTrue(decodedJSON.has("exp"))
      assertEquals(decodedJSON.get("exp"), it)
    }
      ?: assertFalse(decodedJSON.has("exp"))

    expectedFlag?.let {
      assertTrue(decodedJSON.has("flag"))
      assertEquals(decodedJSON.get("flag").toString(), expectedFlag)
    }
      ?: {
        assertTrue(decodedJSON.has("flag"))
        assertFalse(decodedJSON.get("flag").toString().contains("P"))
      }

    expectedLabel?.let {
      assertTrue(decodedJSON.has("label"))
      assertEquals(decodedJSON.get("label"), expectedLabel)
    }
      ?: assertFalse(decodedJSON.has("label"))

    if (optionalViewer.isNotEmpty()) {
      assertTrue(result.contains("$optionalViewer#shlink:/"))
    }
  }

  @Test
  fun testGenerateSHLinkWithExp() = runTest {
    assertCommonFunctionality(
      SHLinkGenerationData("", Instant.parse("2023-11-01T00:00:00.00Z"), mockIPSDocument),
      "",
      "",
      "1698796800",
      null,
      null,
    )
  }

  @Test
  fun testGenerateSHLinkWithoutExp() = runTest {
    assertCommonFunctionality(
      SHLinkGenerationData("", null, mockIPSDocument),
      "",
      "",
      null,
      null,
      null,
    )
  }

  @Test
  fun testGenerateSHLinkWithPasscode() = runTest {
    assertCommonFunctionality(
      SHLinkGenerationData("", null, mockIPSDocument),
      "passcode",
      "",
      null,
      "P",
      null,
    )
  }

  @Test
  fun testGenerateSHLinkWithoutPasscode() = runTest {
    assertCommonFunctionality(
      SHLinkGenerationData("", null, mockIPSDocument),
      "",
      "",
      null,
      "",
      null,
    )
  }

  @Test
  fun testGenerateSHLinkWithLabel() = runTest {
    assertCommonFunctionality(
      SHLinkGenerationData("label", null, mockIPSDocument),
      "",
      "",
      null,
      null,
      "label",
    )
  }

  @Test
  fun testGenerateSHLinkWithoutLabel() = runTest {
    assertCommonFunctionality(
      SHLinkGenerationData("", null, mockIPSDocument),
      "",
      "",
      null,
      null,
      null,
    )
  }

  @Test
  fun testGenerateSHLinkWithOptionalViewer() = runTest {
    assertCommonFunctionality(
      SHLinkGenerationData("", null, mockIPSDocument),
      "",
      "viewerUrl",
      null,
      null,
      null,
    )
  }

  @Test
  fun testGenerateSHLinkWithoutOptionalViewer() = runTest {
    assertCommonFunctionality(
      SHLinkGenerationData("", null, mockIPSDocument),
      "",
      "",
      null,
      null,
      null,
    )
  }

  @Test
  fun testGenerateSHLinkWithAllFeatures() = runTest {
    assertCommonFunctionality(
      SHLinkGenerationData("label", Instant.parse("2023-11-01T00:00:00.00Z"), mockIPSDocument),
      "passcode",
      "viewerUrl",
      "1698796800",
      "P",
      "label",
    )
  }

  @Test
  fun testGenerateSHLinkWithNoFeatures() = runTest {
    assertCommonFunctionality(
      SHLinkGenerationData("", null, mockIPSDocument),
      "",
      "",
      null,
      null,
      null,
    )
  }
}
