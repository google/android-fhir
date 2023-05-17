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

package com.google.android.fhir.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.NetworkConfiguration
import com.google.android.fhir.sync.remote.FhirConverterFactory
import com.google.android.fhir.sync.remote.GzipUploadInterceptor
import java.math.BigDecimal
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

class I_GzipUploadInterceptorBenchmark {

  @get:Rule val benchmarkRule = BenchmarkRule()

  private val fhirJsonParser: IParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

  private lateinit var mockWebServer: MockWebServer

  private lateinit var httpServiceWithGzip: TestHttpService

  private lateinit var httpServiceWithoutGzip: TestHttpService

  @Before
  fun setup() {
    mockWebServer = MockWebServer()
    mockWebServer.start(8080)
    val url = "http://${mockWebServer.hostName}:${mockWebServer.port}"

    httpServiceWithGzip =
      TestHttpService.builder(url, NetworkConfiguration(uploadWithGzip = true)).build()

    httpServiceWithoutGzip =
      TestHttpService.builder(url, NetworkConfiguration(uploadWithGzip = false)).build()
  }

  @After
  fun teardown() {
    mockWebServer.shutdown()
  }

  @Test fun upload_10patientsWithGzip() = uploader(10, httpServiceWithGzip)

  @Test fun upload_100patientsWithGzip() = uploader(100, httpServiceWithGzip)

  @Test fun upload_1000patientsWithGzip() = uploader(1000, httpServiceWithGzip)

  @Test fun upload_10patientsWithoutGzip() = uploader(10, httpServiceWithoutGzip)

  @Test fun upload_100patientsWithoutGzip() = uploader(100, httpServiceWithoutGzip)

  @Test fun upload_1000patientsWithoutGzip() = uploader(1000, httpServiceWithoutGzip)

  private fun uploader(numberObservations: Int, httpService: TestHttpService) = runBlocking {
    mockWebServer.dispatcher =
      object : okhttp3.mockwebserver.Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
          val response = MockResponse().addHeader("Content-Type", "application/json; charset=utf-8")
          return response
            .setBody(fhirJsonParser.encodeResourceToString(Bundle()))
            .setResponseCode(200)
        }
      }

    benchmarkRule.measureRepeated {
      runBlocking {
        val requestBundle =
          Bundle().apply {
            type = Bundle.BundleType.SEARCHSET
            id = UUID.randomUUID().toString()
            entry =
              (1..numberObservations).map {
                Bundle.BundleEntryComponent().setResource(createMockObservation())
              }
          }
        httpService.post(requestBundle)
      }
    }
  }

  private fun createMockObservation(): Observation =
    Observation().apply {
      id = UUID.randomUUID().toString()
      status = Observation.ObservationStatus.FINAL
      subject = Reference("Patient/123")
      performer = listOf(Reference("Practitioner/${UUID.randomUUID()}"))
      value =
        Quantity().apply {
          value = BigDecimal.valueOf(6.2)
          unit = "kPa"
          code = "kPa"
          system = "http://unitsofmeasure.org"
        }
      code =
        CodeableConcept().apply {
          coding = listOf(Coding("http://unitsofmeasure.org", "kPa", "kPa"))
        }
      referenceRange =
        listOf(
          Observation.ObservationReferenceRangeComponent().apply {
            low =
              Quantity().apply {
                value = BigDecimal.valueOf(4.8)
                unit = "kPa"
                code = "kPa"
                system = "http://unitsofmeasure.org"
              }
            high =
              Quantity().apply {
                value = BigDecimal.valueOf(6.0)
                unit = "kPa"
                code = "kPa"
                system = "http://unitsofmeasure.org"
              }
          }
        )
      interpretation =
        listOf(
          CodeableConcept(
            Coding(
              "http://terminology.hl7.org/CodeSystem/v3-ObservationInterpretation",
              "H",
              "high"
            )
          )
        )
    }

  interface TestHttpService {

    @GET suspend fun get(@Url path: String): Resource

    @POST(".") suspend fun post(@Body bundle: Bundle): Resource

    class Builder(
      private val baseUrl: String,
      private val networkConfiguration: NetworkConfiguration
    ) {

      fun build(): TestHttpService {
        val client =
          OkHttpClient.Builder()
            .connectTimeout(networkConfiguration.connectionTimeOut, TimeUnit.SECONDS)
            .readTimeout(networkConfiguration.readTimeOut, TimeUnit.SECONDS)
            .writeTimeout(networkConfiguration.writeTimeOut, TimeUnit.SECONDS)
            .apply {
              if (networkConfiguration.uploadWithGzip) {
                addInterceptor(GzipUploadInterceptor)
              }
            }
            .build()
        return Retrofit.Builder()
          .baseUrl(baseUrl)
          .client(client)
          .addConverterFactory(FhirConverterFactory.create())
          .build()
          .create(TestHttpService::class.java)
      }
    }

    companion object {
      fun builder(baseUrl: String, networkConfiguration: NetworkConfiguration) =
        Builder(baseUrl, networkConfiguration)
    }
  }
}
