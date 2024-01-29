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

import com.google.android.fhir.document.decode.ReadSHLinkUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ReadSHLinkUtilsTest {
  private val readSHLinkUtils = ReadSHLinkUtils()

  @Test
  fun testExtractUrl() {
    val scannedData = "shlink:/example-url"
    val result = readSHLinkUtils.extractUrl(scannedData)
    assertEquals("example-url", result)
  }

  @Test
  fun testDecodeUrl() {
    val extractedUrl = "aGVsbG8="
    val result = readSHLinkUtils.decodeUrl(extractedUrl)
    assertTrue(result.contentEquals("hello".toByteArray()))
  }

  @Test
  fun testDecodeShc() {
    val responseBody =
      "eyJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..OgGwTWbECJk9tQc4.PUxr0STCtKQ6DmdPqPtJtTowTBxdprFykeZ2WUOUw234_TtdGWLJ0hzfuWjZXDyBpa55TXwvSwobpcbut9Cdl2nATA0_j1nW0-A32uAwH0qEE1ELV5G0IQVT5AqKJRTCMGpy0mWH.qATmrk-UdwCOaT1TY6GEJg"
    val key = "VmFndWVseS1FbmdhZ2luZy1QYXJhZG94LTA1NTktMDg"
    val result = readSHLinkUtils.decodeShc(responseBody, key)
    assertEquals(
      "{\"iss\":\"DinoChiesa.github.io\",\"sub\":\"idris\",\"aud\":\"kina\",\"iat\":1691158997,\"exp\":1691159597,\"aaa\":true}",
      result.trim(),
    )
  }

  @Test
  fun testExtractVerifiableCredential() {
    val jsonStringWithCredential = "{\"verifiableCredential\": [\"credentialData\"]}"
    val resultWithCredential = readSHLinkUtils.extractVerifiableCredential(jsonStringWithCredential)
    assertEquals("credentialData", resultWithCredential)
  }

  @Test
  fun testExtractVerifiableCredentialWithEmptyJson() {
    val jsonStringWithoutCredential = "{}"
    val resultWithoutCredential =
      readSHLinkUtils.extractVerifiableCredential(jsonStringWithoutCredential)
    assertEquals("", resultWithoutCredential)
  }
}
