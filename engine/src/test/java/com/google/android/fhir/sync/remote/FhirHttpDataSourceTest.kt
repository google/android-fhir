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

import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.ContentTypes
import com.google.android.fhir.NetworkConfiguration
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.sync.UrlUploadRequest
import com.google.android.fhir.sync.remote.FhirHttpDataSource
import com.google.android.fhir.sync.remote.RetrofitHttpService
import com.google.android.fhir.testing.assertResourceEquals
import com.google.common.truth.Truth.assertThat
import java.net.HttpURLConnection
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hl7.fhir.r4.model.Binary
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.codesystems.HttpVerb
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class FhirHttpDataSourceTest {

  private val mockWebServer = MockWebServer()
  private val baseUrl = "/baseR4/"
  private val fhirHttpService by lazy {
    RetrofitHttpService.Builder(mockWebServer.url(baseUrl).toString(), NetworkConfiguration())
      .build()
  }
  private val parser = FhirContext.forR4Cached().newJsonParser()

  private val mockResponse =
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

  private lateinit var dataSource: FhirHttpDataSource

  @Before
  fun setup() {
    mockWebServer.start()
    dataSource = FhirHttpDataSource(fhirHttpService)
  }

  @After
  fun teardown() {
    mockWebServer.shutdown()
  }

  @Test
  fun `test upload with UrlUploadRequest POST`() = runTest {
    val patient =
      Patient().apply {
        id = "Patient-001"
        addName(
          HumanName().apply {
            addGiven("John")
            family = "Doe"
          }
        )
      }
    val request =
      UrlUploadRequest(HttpVerb.POST, "Patient", patient, LocalChangeToken(listOf(1)), emptyMap())
    mockWebServer.enqueue(mockResponse)
    dataSource.upload(request)
    val recordedRequest: RecordedRequest = mockWebServer.takeRequest()

    assertThat(recordedRequest.path).isEqualTo("${baseUrl}${request.url}")
    assertThat(recordedRequest.method).isEqualTo("POST")
    assertResourceEquals(patient, parser.parseResource(recordedRequest.body.readUtf8()) as Resource)
  }

  @Test
  fun `test upload with UrlUploadRequest PUT`() = runTest {
    val patient =
      Patient().apply {
        id = "Patient-001"
        addName(
          HumanName().apply {
            addGiven("John")
            family = "Doe"
          }
        )
      }
    val request =
      UrlUploadRequest(
        HttpVerb.PUT,
        "Patient/Patient-001",
        patient,
        LocalChangeToken(listOf(1)),
        emptyMap()
      )
    mockWebServer.enqueue(mockResponse)
    dataSource.upload(request)
    val recordedRequest: RecordedRequest = mockWebServer.takeRequest()

    assertThat(recordedRequest.path).isEqualTo("${baseUrl}${request.url}")
    assertThat(recordedRequest.method).isEqualTo("PUT")
    assertResourceEquals(patient, parser.parseResource(recordedRequest.body.readUtf8()) as Resource)
  }

  @Test
  fun `test upload with UrlUploadRequest PATCH`() = runTest {
    val patchToApply =
      Binary().apply {
        data =
          "[{\"op\":\"replace\",\"path\":\"\\/name\\/0\\/given\\/0\",\"value\":\"Janet\"}]".toByteArray()
      }

    val request =
      UrlUploadRequest(
        HttpVerb.PATCH,
        "Patient/Patient-001",
        patchToApply,
        LocalChangeToken(listOf(1)),
        mapOf("Content-Type" to ContentTypes.APPLICATION_JSON_PATCH)
      )
    mockWebServer.enqueue(mockResponse)
    dataSource.upload(request)
    val recordedRequest: RecordedRequest = mockWebServer.takeRequest()

    assertThat(recordedRequest.path).isEqualTo("${baseUrl}${request.url}")
    assertThat(recordedRequest.method).isEqualTo("PATCH")
    assertThat(recordedRequest.body.readUtf8())
      .isEqualTo("[{\"op\":\"replace\",\"path\":\"/name/0/given/0\",\"value\":\"Janet\"}]")
    assertThat(recordedRequest.getHeader("Content-Type"))
      .contains(ContentTypes.APPLICATION_JSON_PATCH)
  }
}
