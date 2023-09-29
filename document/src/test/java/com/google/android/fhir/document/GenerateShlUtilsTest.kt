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

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.document.utils.GenerateShlUtils
import com.google.android.fhir.document.utils.QRGeneratorUtils
import com.google.android.fhir.testing.readFromFile
import com.nimbusds.jose.shaded.gson.Gson
import kotlinx.coroutines.test.runTest
import org.hl7.fhir.r4.model.Bundle
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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

  private lateinit var generateShlUtils: GenerateShlUtils

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    generateShlUtils = GenerateShlUtils(qrGeneratorUtils)
  }

  private val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
  private val minimalBundleString =
    parser.encodeResourceToString(readFromFile(Bundle::class.java, "/bundleMinimal.json"))

  @Test
  fun postingToServerReturnsManifestIdAndToken() = runTest {
    val postResponse = generateShlUtils.getManifestUrlAndToken("")
    println("Response: $postResponse")

    assertNotNull(postResponse)
    assertTrue(postResponse.has("id"))
    assertTrue(postResponse.has("managementToken"))
    assertTrue(postResponse.has("active"))
  }

  @Test
  fun randomKeysCanBeGenerated() {
    val key = generateShlUtils.generateRandomKey()
    assert(key.length == 44)
  }

  @Test
  fun canConvertFilesIntoJweTokens() {
    val encryptionKey = generateShlUtils.generateRandomKey()
    val contentJson = Gson().toJson(minimalBundleString)
    val contentEncrypted = generateShlUtils.encrypt(contentJson, encryptionKey)
    println(contentEncrypted)
    assertEquals(contentEncrypted.split('.').size, 5)
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
    val key = generateShlUtils.generateRandomKey()
    val expirationDate = "2023-12-31"

    /* Construct the expected JSON object */
    val expectedJson =
      JSONObject().put("url", manifestUrl).put("key", key).put("flag", flags).put("label", label)
        .put("exp", generateShlUtils.dateStringToEpochSeconds(expirationDate))

    val payload =
      generateShlUtils.constructSHLinkPayload(manifestUrl, label, flags, key, expirationDate)
    val actualJson = JSONObject(payload)
    assertEquals(expectedJson.toString(), actualJson.toString())
  }

  @Test
  fun testDateToEpochSeconds() {
    val dateString = "2023-09-30"
    val expectedEpochSeconds = 1696032000L
    val epochSeconds = generateShlUtils.dateStringToEpochSeconds(dateString)
    assertEquals(expectedEpochSeconds, epochSeconds)
  }

  @Test
  fun canPostPayloadWithValidInputs() = runTest {
    val manifestUrl = "https://api.vaxx.link/api/shl/eT4EyhrDJ3gMJao9ovpwSX2SIKfkCBgjzOC-ft6BCk8"
    val key = generateShlUtils.generateRandomKey()
    val managementToken = "M6hY9PkPiYQ6SiaFAtmve9a7tkzP_xExwlWdRXLh3BQ"
    val fileData = ""
    val postResponse = generateShlUtils.postPayload(fileData, manifestUrl, key, managementToken)

    assertNotNull(postResponse)
    assertTrue(postResponse.has("passcodeFailuresRemaining"))
    assertTrue(postResponse.has("managementToken"))
    assertTrue(postResponse.has("active"))
  }

  @Test
  fun failsToPostPayloadWithInvalidManifestUrl() = runTest {
    val manifestUrl = "invalid_url"
    val key = generateShlUtils.generateRandomKey()
    val managementToken = ""
    val fileData = ""
    val result = kotlin.runCatching {
      generateShlUtils.postPayload(fileData, manifestUrl, key, managementToken)
    }
    assertTrue(result.isFailure)
  }

}
