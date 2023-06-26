/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir.sync.remote

import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.NetworkConfiguration
import com.google.android.fhir.logicalId
import com.google.common.truth.Truth.assertThat
import java.net.HttpURLConnection
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Patient
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RetrofitHttpServiceTest {

  private val mockWebServer = MockWebServer()

  private val retrofitHttpService by lazy {
    RetrofitHttpService.Builder(mockWebServer.url("/").toString(), NetworkConfiguration()).build()
  }

  private val parser = FhirContext.forR4Cached().newJsonParser()

  @Before
  fun setUp() {
    mockWebServer.start()
  }

  @After
  fun shutdown() {
    mockWebServer.shutdown()
  }

  @Test // https://github.com/google/android-fhir/issues/1892
  fun `should assemble download request correctly`() = runTest {
    // checks that a download request can be made successfully with parameters without exception
    val mockResponse =
      MockResponse().apply {
        setResponseCode(HttpURLConnection.HTTP_OK)
        setBody(
          parser.encodeResourceToString(
            Patient().apply {
              id = "patient-001"
              addName(
                HumanName().apply {
                  addGiven("John")
                  family = "Doe"
                }
              )
            }
          )
        )
      }
    mockWebServer.enqueue(mockResponse)

    val result =
      retrofitHttpService.get("Patient/patient-001", mapOf("If-Match" to "randomResourceVersionID"))
    val serverRequest = mockWebServer.takeRequest()
    assertThat(serverRequest.headers).contains("If-Match" to "randomResourceVersionID")
    // No exception should occur
    assertThat(result).isInstanceOf(Patient::class.java)
  }

  @Test // https://github.com/google/android-fhir/issues/1892
  fun `should assemble upload request correctly`() = runTest {
    // checks that a upload request can be made successfully with parameters without exception
    val mockResponse =
      MockResponse().apply {
        setResponseCode(HttpURLConnection.HTTP_OK)
        setBody(
          parser.encodeResourceToString(
            Bundle().apply {
              id = "transaction-response-1"
              type = Bundle.BundleType.TRANSACTIONRESPONSE
            }
          )
        )
      }
    mockWebServer.enqueue(mockResponse)
    val request =
      Bundle().apply {
        id = "transaction-1"
        type = Bundle.BundleType.TRANSACTION
      }

    val result = retrofitHttpService.post(request, mapOf("If-Match" to "randomResourceVersionID"))
    val serverRequest = mockWebServer.takeRequest()
    assertThat(serverRequest.headers["If-Match"]).isEqualTo("randomResourceVersionID")
    // No exception has occurred
    assertThat(result).isInstanceOf(Bundle::class.java)
  }

  @Test
  fun `should use fhir converter to serialize and deserialize request and response for fhir resources`() =
    runTest {
      val mockResponse =
        MockResponse().apply {
          setResponseCode(HttpURLConnection.HTTP_OK)
          setBody(
            parser.encodeResourceToString(
              Bundle().apply {
                id = "transaction-response-1"
                type = Bundle.BundleType.TRANSACTIONRESPONSE
              }
            )
          )
        }
      mockWebServer.enqueue(mockResponse)
      val request =
        Bundle().apply {
          id = "transaction-1"
          type = Bundle.BundleType.TRANSACTION
        }

      val result = retrofitHttpService.post(request, emptyMap())

      assertThat(result).isInstanceOf(Bundle::class.java)
      assertThat((result as Bundle).type).isEqualTo(Bundle.BundleType.TRANSACTIONRESPONSE)
      assertThat((result).logicalId).isEqualTo("transaction-response-1")
    }
}
