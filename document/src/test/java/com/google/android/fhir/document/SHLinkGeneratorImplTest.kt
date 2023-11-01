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

import com.google.android.fhir.NetworkConfiguration
import com.google.android.fhir.document.generate.EncryptionUtils
import com.google.android.fhir.document.generate.SHLinkGenerationData
import com.google.android.fhir.document.generate.SHLinkGeneratorImpl
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hl7.fhir.r4.model.Bundle
import org.json.JSONObject
import org.junit.Assert.assertEquals
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

  private val shLinkGenerationDataWithExp =
    SHLinkGenerationData("Label", "2023-11-01", mockIPSDocument)
  private val shLinkGenerationDataWithoutExp = SHLinkGenerationData("Label", "", mockIPSDocument)

  val initialPostResponse = JSONObject().apply {
    put("id", "123")
    put("managementToken", "token123")
  }

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    shLinkGeneratorImpl = SHLinkGeneratorImpl(apiService, encryptionUtility)
    `when`(mockIPSDocument.document).thenReturn(Bundle())
    `when`(encryptionUtility.encrypt(Mockito.anyString(), Mockito.anyString())).thenReturn("")
  }

  @Test
  fun pFlagIsIncludedWhenPasscodeIsPresent() {
    val flags = shLinkGeneratorImpl.getKeyFlags("passcode")
    assert(flags.contains("P"))
  }

  @Test
  fun pFlagIsNotIncludedWhenPasscodeIsNotPresent() {
    val flags = shLinkGeneratorImpl.getKeyFlags("")
    assert(!flags.contains("P"))
  }

  @Test
  fun canCorrectlyConstructSHLinkPayload() {
    val manifestUrl = "https://example.com/manifest"
    val label = "My SHL"
    val flags = "P"
    val key = "key"
    val expirationDate = "2023-12-31"

    /* Construct the expected JSON object */
    val expectedJson =
      JSONObject().put("url", manifestUrl).put("key", key).put("flag", flags).put("label", label)
        .put("exp", shLinkGeneratorImpl.convertDateStringToEpochSeconds(expirationDate))

    val payload =
      shLinkGeneratorImpl.constructSHLinkPayload(manifestUrl, label, flags, key, expirationDate)
    val actualJson = JSONObject(payload)
    assertEquals(expectedJson.toString(), actualJson.toString())
  }

  @Test
  fun testDateToEpochSeconds() {
    val dateString = "2023-09-30"
    val expectedEpochSeconds = 1696032000L
    val epochSeconds = shLinkGeneratorImpl.convertDateStringToEpochSeconds(dateString)
    assertEquals(expectedEpochSeconds, epochSeconds)
  }

  @Test
  fun testGenerateSHLink() = runTest {
    TODO("Test to be implemented")
  }

  /* Write the same test where exp isn't present */
  @Test
  fun testGenerateAndPostPayloadWithoutExp() = runTest {
    `when`(encryptionUtility.generateRandomKey()).thenReturn("mockedKey")

    val initialPostResponse = JSONObject()
    initialPostResponse.put("id", "123")
    initialPostResponse.put("managementToken", "token123")

    val mockResponse = MockResponse().setResponseCode(200)
    mockResponse.setBody("{'test key': 'test value'}")
    mockWebServer.enqueue(mockResponse)

    val mockSHLinkGenerationData =
      SHLinkGenerationData("Mocked Label", "2023-12-31", mockIPSDocument)

    val result = shLinkGeneratorImpl.generateAndPostPayload(
      initialPostResponse, mockSHLinkGenerationData, "passcode", mockWebServer.url("/").toString()
    )

    assertTrue(result.contains("https://demo.vaxx.link/viewer#shlink:/"))
    /* Section after link is base64 encoded - can I decode this and check correct label is in there? */
    /* Also check that exp isn't empty */
  }

  @Test
  fun testPostPayloadWithEmptyResponseBody() = runTest {
    val response = MockResponse().setResponseCode(200).setBody("")
    mockWebServer.enqueue(response)

    val result = shLinkGeneratorImpl.postPayload("fileData", "manifestToken", "key", "managementToken")

    val recordedRequest = mockWebServer.takeRequest()
    recordedRequest.path?.let { assertTrue(it.contains(baseUrl)) }
    assertEquals(recordedRequest.method, "POST")

    // Check that the result is an empty JSON
    assertEquals(JSONObject().toString(), result.toString())
  }

  @Test
  fun testPostPayloadWithNonEmptyResponseBody() = runTest {
    val responseBodyString = "{'test key': 'test value'}"
    val response = MockResponse().setResponseCode(200).setBody(responseBodyString)
    mockWebServer.enqueue(response)

    val result = shLinkGeneratorImpl.postPayload("fileData", "manifestToken", "key", "managementToken")

    val recordedRequest = mockWebServer.takeRequest()
    recordedRequest.path?.let { assertTrue(it.contains(baseUrl)) }
    assertEquals(recordedRequest.method, "POST")

    // Check that the result is a non-empty JSON
    val expectedJson = JSONObject(responseBodyString)
    assertEquals(expectedJson.toString(), result.toString())
  }
}
