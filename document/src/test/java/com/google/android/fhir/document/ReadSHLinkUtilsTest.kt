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
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ReadSHLinkUtilsTest {
  private val readSHLinkUtils = ReadSHLinkUtils()

  private val responseBody =
    "eyJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiZGlyIn0..OgGwTWbECJk9tQc4.PUxr0STCtKQ6DmdPqPtJtTowTBxdprFykeZ2WUOUw234_TtdGWLJ0hzfuWjZXDyBpa55TXwvSwobpcbut9Cdl2nATA0_j1nW0-A32uAwH0qEE1ELV5G0IQVT5AqKJRTCMGpy0mWH.qATmrk-UdwCOaT1TY6GEJg"
  private val key = "VmFndWVseS1FbmdhZ2luZy1QYXJhZG94LTA1NTktMDg"

  @Test
  fun `test extractUrl with a valid SHL`() {
    val scannedData = "shlink:/example-url"
    val result = readSHLinkUtils.extractUrl(scannedData)
    assertEquals("example-url", result)
  }

  @Test
  fun `test extractUrl with an invalid SHL`() {
    val scannedData = "invalidSHL"
    assertThrows(IllegalArgumentException::class.java) { readSHLinkUtils.extractUrl(scannedData) }
  }

  @Test
  fun `test extractUrl with an empty SHL`() {
    val scannedData = ""
    assertThrows(IllegalArgumentException::class.java) { readSHLinkUtils.extractUrl(scannedData) }
  }

  @Test
  fun `test that decodeUrl successfully decodes a Base64 encoded input string`() {
    val extractedUrl = "aGVsbG8="
    val result = readSHLinkUtils.decodeUrl(extractedUrl)
    assertTrue(result.contentEquals("hello".toByteArray()))
  }

  @Test
  fun `test that decodeUrl throws an error when decoding an invalid Base64 encoded input string`() {
    val extractedUrl = "aGsbG8="
    assertThrows(IllegalArgumentException::class.java) { readSHLinkUtils.decodeUrl(extractedUrl) }
  }

  @Test
  fun `test that decodeUrl throws an error when decoding an empty input string`() {
    val extractedUrl = ""
    assertThrows(IllegalArgumentException::class.java) { readSHLinkUtils.decodeUrl(extractedUrl) }
  }

  @Test
  fun `test that decodeShc can successfully decrypt an SHL with it's given key`() {
    val result = readSHLinkUtils.decodeShc(responseBody, key)
    assertEquals(
      "{\"iss\":\"DinoChiesa.github.io\",\"sub\":\"idris\",\"aud\":\"kina\",\"iat\":1691158997,\"exp\":1691159597,\"aaa\":true}",
      result.trim(),
    )
  }

  @Test
  fun `test that decodeShc unsuccessfully decrypts an empty string`() {
    val responseBody = ""
    assertThrows(Exception::class.java) { readSHLinkUtils.decodeShc(responseBody, key) }
  }

  @Test
  fun `test that decodeShc unsuccessfully decrypts an SHL with an empty key`() {
    val key = ""
    assertThrows(Exception::class.java) { readSHLinkUtils.decodeShc(responseBody, key) }
  }

  @Test
  fun `test that decodeShc unsuccessfully decrypts an SHL with an invalid key`() {
    val invalidKey = "VmFndWVseS1FbmdhZ2luZy1QYXJhZG94LTA1NTktMDb"
    assertThrows(Exception::class.java) { readSHLinkUtils.decodeShc(responseBody, invalidKey) }
  }

  @Test
  fun `test extractVerifiableCredential successfully extracts the data from a JSON string`() {
    val jsonStringWithCredential = "{\"verifiableCredential\": [\"credentialData\"]}"
    val resultWithCredential = readSHLinkUtils.extractVerifiableCredential(jsonStringWithCredential)
    assertEquals("credentialData", resultWithCredential)
  }

  @Test
  fun `test extractVerifiableCredential successfully extracts the data from an empty JSON string`() {
    val jsonStringWithoutCredential = "{}"
    val resultWithoutCredential =
      readSHLinkUtils.extractVerifiableCredential(jsonStringWithoutCredential)
    assertEquals("", resultWithoutCredential)
  }

  @Test
  fun `test that jwts can be decoded and decompressed into JSON data`() {
    val jwt =
      "eyJ6aXAiOiJERUYiLCJhbGciOiJFUzI1NiIsImtpZCI6IjNLZmRnLVh3UC03Z1h5eXd0VWZVQUR3QnVtRE9QS01ReC1pRUxMMTFXOXMifQ.pZJJT8MwEIX_ChquaZZSthyBAxwQSCwX1IPrTBsjL9HYKRSU_85MaAVCiAtSDnH85vN7z3kHEyPU0KbUxbooYoc6j05RalHZ1OZaURMLfFWusxgLVvdIkIFfLKGujman5bQ8mM7y6dFhBmsN9TukTYdQP30xf-L2PxcTWTDq_zrjXO_Nm0omeJhnoAkb9Mkoe9cvnlEnsbVsDT0iRdHUMMvLvGKofD3rfWNRNIQx9KTxfowA241sGwl0sJZpQsiAD6AN52Ryb-0DWRbs5uuSBbvFL-Bbtsrz0qNy-AlRztiNHErhRfgrs0YvPd5YfiOYD5xsYTj6hUoCmZbV8aSsJuUMhiH71Ub1t42r771lEJNKfRxzym0nlNbXSmvj8Tw0I0GHxvjV6DhuYkK3_Xn4Xlp7nAdaFVJpEU1T6PUrA_Q4CeUJDPMhg24bfXSzREIv1r43x6KgdU_jlmS9N-5HXsYgLQM57kWsKJ0CCbIxsbNKarxGMglp7zLEziRluaP5-AzDBw.xOwN6qSTeHU-FkqTIojbvryr8Ztue_HBbiiGdIcfio7m2-STuC-CdNIEt9WbxU_CpveZwdwdYlaQ3cX-yi-SQg"
    val expectedData =
      "{\"iss\":\"https://spec.smarthealth.cards/examples/issuer\",\"nbf\":1649020324.265,\"vc\":{\"type\":[\"https://smarthealth.cards#health-card\",\"https://smarthealth.cards#health-card\",\"https://smarthealth.cards#immunization\"],\"credentialSubject\":{\"fhirVersion\":\"4.0.1\",\"fhirBundle\":{\"resourceType\":\"Bundle\",\"type\":\"collection\",\"entry\":[{\"fullUrl\":\"resource:0\",\"resource\":{\"resourceType\":\"Patient\",\"name\":[{\"family\":\"Brown\",\"given\":[\"Oliver\"]}],\"birthDate\":\"2017-01-04\"}},{\"fullUrl\":\"resource:1\",\"resource\":{\"resourceType\":\"Immunization\",\"status\":\"completed\",\"vaccineCode\":{\"coding\":[{\"system\":\"http://hl7.org/fhir/sid/cvx\",\"code\":\"08\"}]},\"patient\":{\"reference\":\"resource:0\"},\"occurrenceDateTime\":\"2017-01-04\",\"performer\":[{\"actor\":{\"display\":\"Meriter Hospital\"}}]}}]}}}}\n"
    val decoded = readSHLinkUtils.decodeAndDecompressPayload(jwt)
    assertEquals(decoded.trim(), expectedData.trim())
  }

  @Test
  fun `test that empty jwts throw an error when attempting to decode and decompress them`() {
    val jwt = ""
    assertThrows(Error::class.java) { readSHLinkUtils.decodeAndDecompressPayload(jwt) }
  }

  @Test
  fun `test that invalid jwts throw an error when attempting to decode and decompress them`() {
    val jwt =
      "XAiOiJERUYiLCJhbGciOiJFUzI1NiIsImtpZCI6IjNLZmRnLVh3UC03Z1h5eXd0VWZVQUR3QnVtRE9QS01ReC1pRUxMMTFXOXMifQ.pHJJT8MwEIX_ChquaZZSthyBAxwQSCwX1IPrTBsjL9HYKRSU_85MaAVCiAtSDnH85vN7z3kHEyPU0KbUxbooYoc6j05RalHZ1OZaURMLfFWusxgLVvdIkIFfLKGujman5bQ8mM7y6dFhBmsN9TukTYdQP30xf-L2PxcTWTDq_zrjXO_Nm0omeJhnoAkb9Mkoe9cvnlEnsbVsDT0iRdHUMMvLvGKofD3rfWNRNIQx9KTxfowA241sGwl0sJZpQsiAD6AN52Ryb-0DWRbs5uuSBbvFL-Bbtsrz0qNy-AlRztiNHErhRfgrs0YvPd5YfiOYD5xsYTj6hUoCmZbV8aSsJuUMhiH71Ub1t42r771lEJNKfRxzym0nlNbXSmvj8Tw0I0GHxvjV6DhuYkK3_Xn4Xlp7nAdaFVJpEU1T6PUrA_Q4CeUJDPMhg24bfXSzREIv1r43x6KgdU_jlmS9N-5HXsYgLQM57kWsKJ0CCbIxsbNKarxGMglp7zLEziRluaP5-AzDBw.xOwN6qSTeHU-FkqTIojbvryr8Ztue_HBbiiGdIcfio7m2-STuC-CdNIEt9WbxU_CpveZwdwdYlaQ3cX-yi-SQg"
    assertThrows(Error::class.java) { readSHLinkUtils.decodeAndDecompressPayload(jwt) }
  }
}
