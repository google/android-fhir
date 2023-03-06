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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(RobolectricTestRunner::class)
class RemoteFhirServiceTest {
  private val mockWebServer = MockWebServer()
  private val apiService by lazy {
    Retrofit.Builder()
      .baseUrl(mockWebServer.url("/"))
      .addConverterFactory(FhirConverterFactory.create())
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(RemoteFhirService::class.java)
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
  @Test
  fun download() = runTest {
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

    val result = apiService.download("Patient/patient-001")

    assertThat(result).isInstanceOf(Patient::class.java)
    assertThat((result as Patient).logicalId).isEqualTo("patient-001")
  }

  @Test
  fun upload() = runTest {
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
    val result = apiService.upload(request)

    assertThat(result).isInstanceOf(Bundle::class.java)
    assertThat((result as Bundle).type).isEqualTo(Bundle.BundleType.TRANSACTIONRESPONSE)
    assertThat((result).logicalId).isEqualTo("transaction-response-1")
  }
}
