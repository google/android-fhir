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
import com.google.android.fhir.document.interfaces.RetrofitSHLService
import com.google.android.fhir.document.utils.EncryptionUtils
import com.google.android.fhir.document.utils.GenerateShlUtils
import com.google.android.fhir.document.utils.QRGeneratorUtils
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class GenerateShlUtilsTest {

  @Mock private lateinit var qrGeneratorUtils: QRGeneratorUtils

  @Mock private lateinit var encryptionUtility: EncryptionUtils
  private lateinit var generateShlUtils: GenerateShlUtils

  private val mockWebServer = MockWebServer()
  private val baseUrl = "/shl/"

  private val apiService by lazy {
    RetrofitSHLService.Builder(mockWebServer.url(baseUrl).toString(), NetworkConfiguration())
      .build()
  }

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    generateShlUtils = GenerateShlUtils(qrGeneratorUtils, apiService, encryptionUtility)
  }

  @Test
  fun pFlagIsIncludedWhenPasscodeIsPresent() {
    val flags = generateShlUtils.getKeyFlags("passcode")
    assert(flags.contains("P"))
  }

  @Test
  fun pFlagIsNotIncludedWhenPasscodeIsNotPresent() {
    val flags = generateShlUtils.getKeyFlags("")
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
      JSONObject()
        .put("url", manifestUrl)
        .put("key", key)
        .put("flag", flags)
        .put("label", label)
        .put("exp", generateShlUtils.convertDateStringToEpochSeconds(expirationDate))

    val payload =
      generateShlUtils.constructSHLinkPayload(manifestUrl, label, flags, key, expirationDate)
    val actualJson = JSONObject(payload)
    assertEquals(expectedJson.toString(), actualJson.toString())
  }

  @Test
  fun testDateToEpochSeconds() {
    val dateString = "2023-09-30"
    val expectedEpochSeconds = 1696032000L
    val epochSeconds = generateShlUtils.convertDateStringToEpochSeconds(dateString)
    assertEquals(expectedEpochSeconds, epochSeconds)
  }
}
