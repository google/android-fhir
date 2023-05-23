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

import android.content.Context
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineConfiguration
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.ServerConfiguration
import com.google.android.fhir.sync.AcceptRemoteConflictResolver
import com.google.android.fhir.sync.DownloadWorkManager
import com.google.android.fhir.sync.FhirSyncWorker
import com.google.android.fhir.sync.Request
import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import java.util.LinkedList
import java.util.UUID
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.ContactPoint
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Encounter.EncounterParticipantComponent
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.ListResource
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.StringType
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class H_FhirSyncWorkerBenchmark {

  @get:Rule val benchmarkRule = BenchmarkRule()

  private val fhirJsonParser: IParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

  class BenchmarkTestOneTimeSyncWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
  ) : FhirSyncWorker(appContext, workerParams) {

    override fun getFhirEngine(): FhirEngine {
      return FhirEngineProvider.getInstance(appContext)
    }
    override fun getDownloadWorkManager(): DownloadWorkManager = BenchmarkTestDownloadManagerImpl()
    override fun getConflictResolver() = AcceptRemoteConflictResolver
  }

  open class BenchmarkTestDownloadManagerImpl(queries: List<String> = listOf("List/sync-list")) :
    DownloadWorkManager {
    private val urls = LinkedList(queries)

    override suspend fun getNextRequest() = urls.poll()?.let { Request.of(it) }
    override suspend fun getSummaryRequestUrls(): Map<ResourceType, String> {
      return emptyMap()
    }

    override suspend fun processResponse(response: Resource): Collection<Resource> {
      if (response is ListResource) {
        for (entry in response.entry) {
          val reference = Reference(entry.item.reference)
          if (reference.referenceElement.resourceType.equals("Patient")) {
            urls.add(entry.item.reference)
            urls.add("Observation?subject=${entry.item.referenceElement.idPart}")
            urls.add("Encounter?subject=${entry.item.referenceElement.idPart}")
          }
        }
        return emptyList()
      }

      if (response is Patient) {
        return listOf(response)
      }

      if (response is Bundle && response.type == Bundle.BundleType.SEARCHSET) {
        return response.entry.map { it.resource }
      }
      return emptyList()
    }
  }

  @Test fun oneTimeSync_1patient() = oneTimeSync(1, 5, 5)

  @Test fun oneTimeSync_10patients() = oneTimeSync(10, 5, 5)

  @Test fun oneTimeSync_50patients() = oneTimeSync(50, 5, 5)

  private fun oneTimeSync(numberPatients: Int, numberObservations: Int, numberEncounters: Int) =
    runBlocking {
      val context: Context = ApplicationProvider.getApplicationContext()
      val worker = TestListenableWorkerBuilder<BenchmarkTestOneTimeSyncWorker>(context).build()
      setupMockServerDispatcher(numberPatients, numberObservations, numberEncounters)
      benchmarkRule.measureRepeated {
        runBlocking {
          val result = worker.doWork()
          assertThat(result).isInstanceOf(ListenableWorker.Result.Success::class.java)
        }
      }
    }

  private fun setupMockServerDispatcher(
    numberPatients: Int,
    numberObservations: Int,
    numberEncounters: Int
  ) {
    mockWebServer.dispatcher =
      object : Dispatcher() {
        @Throws(InterruptedException::class)
        override fun dispatch(request: RecordedRequest): MockResponse {
          if (request.path == "/fhir/List/sync-list") {
            val list =
              ListResource().apply {
                entry =
                  (1..numberPatients).map { _ ->
                    ListResource.ListEntryComponent().apply {
                      item =
                        (Reference().apply {
                          setReferenceElement(StringType("Patient/${UUID.randomUUID()}"))
                        })
                    }
                  }
              }
            return createResourceResponse(list)
          }

          if (request.path!!.startsWith("/fhir/Patient")) {
            val patientId = request.path!!.split("//").last()
            return createResourceResponse(createMockPatient(patientId))
          }

          if (request.path!!.startsWith("/fhir/Observation")) {
            val patientId = request.requestUrl!!.queryParameter("subject")!!
            val bundle =
              Bundle().apply {
                type = Bundle.BundleType.SEARCHSET
                id = UUID.randomUUID().toString()
                entry =
                  (1..numberObservations).map {
                    Bundle.BundleEntryComponent().setResource(createMockObservation(patientId))
                  }
              }
            return createResourceResponse(bundle)
          }

          if (request.path!!.startsWith("/fhir/Encounter")) {
            val patientId = request.requestUrl!!.queryParameter("subject")!!
            val bundle =
              Bundle().apply {
                type = Bundle.BundleType.SEARCHSET
                entry =
                  (1..numberEncounters).map {
                    Bundle.BundleEntryComponent().setResource(createMockEncounter(patientId))
                  }
              }
            return createResourceResponse(bundle)
          }
          return MockResponse().setResponseCode(404)
        }
      }
  }

  private fun createResourceResponse(resource: IBaseResource): MockResponse {
    val response = MockResponse().addHeader("Content-Type", "application/json; charset=utf-8")
    return response.setBody(fhirJsonParser.encodeResourceToString(resource)).setResponseCode(200)
  }

  private fun createMockPatient(patientId: String): Patient {
    return Patient().apply {
      id = patientId
      gender =
        if (patientId.last().isDigit()) Enumerations.AdministrativeGender.FEMALE
        else Enumerations.AdministrativeGender.MALE
      name =
        listOf(
          HumanName().apply {
            given = listOf(StringType("Test patient Name $patientId"))
            family = "Patient Family"
          }
        )
      address =
        listOf(
          Address().apply {
            text = "534 Erewhon St PeasantVille, Rainbow, Vic  3999"
            city = "PleasantVille"
            district = "Rainbow"
            state = "Vic"
            postalCode = "postalCode"
            line = listOf(StringType("534 Erewhon St"))
          }
        )
      contact =
        listOf(
          Patient.ContactComponent().apply {
            relationship =
              listOf(
                CodeableConcept().apply {
                  coding = listOf(Coding("http://terminology.hl7.org/CodeSystem/v2-0131", "N", ""))
                  name =
                    HumanName().apply {
                      given = listOf(StringType("Test patient rep Name $patientId"))
                      family = "Patient Family"
                    }
                  gender =
                    if (patientId.last().isDigit()) Enumerations.AdministrativeGender.MALE
                    else Enumerations.AdministrativeGender.FEMALE
                }
              )
          }
        )
      telecom =
        listOf(
          ContactPoint().apply {
            system = ContactPoint.ContactPointSystem.PHONE
            value = "(03) 5555 6473"
            use = ContactPoint.ContactPointUse.HOME
          },
          ContactPoint().apply {
            system = ContactPoint.ContactPointSystem.PHONE
            value = "(03) 3410 5613"
            use = ContactPoint.ContactPointUse.WORK
          }
        )
    }
  }

  private fun createMockObservation(patientId: String): Observation {
    return Observation().apply {
      id = UUID.randomUUID().toString()
      status = Observation.ObservationStatus.FINAL
      subject = Reference("Patient/$patientId")
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
  }

  private fun createMockEncounter(patientId: String): Encounter {
    return Encounter().apply {
      id = UUID.randomUUID().toString()
      status = Encounter.EncounterStatus.FINISHED
      class_ =
        Coding("http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP", "inpatient encounter")
      priority =
        CodeableConcept().apply {
          coding = listOf(Coding("http://snomed.info/sct", "394849002", "High priority"))
        }
      type =
        listOf(
          CodeableConcept().apply {
            coding =
              listOf(Coding("http://snomed.info/sct", "183807002", "Inpatient stay for nine days"))
          }
        )
      subject = Reference("Patient/$patientId")
      episodeOfCare = listOf(Reference("Episode/${UUID.randomUUID()}"))
      serviceProvider = Reference("Organization/123")
      participant =
        listOf(
          EncounterParticipantComponent().apply {
            individual = Reference("Practitioner/${UUID.randomUUID()}")
            type =
              listOf(
                CodeableConcept().apply {
                  coding =
                    listOf(
                      Coding(
                        "http://terminology.hl7.org/CodeSystem/v3-ParticipationType",
                        "PART",
                        "Participant"
                      )
                    )
                }
              )
          }
        )
      diagnosis =
        listOf(
          Encounter.DiagnosisComponent().apply {
            condition = Reference("Condition/stroke")
            use =
              CodeableConcept().apply {
                coding =
                  listOf(
                    Coding(
                      "http://terminology.hl7.org/CodeSystem/diagnosis-role",
                      "AD",
                      "Admission diagnosis"
                    )
                  )
              }
          },
          Encounter.DiagnosisComponent().apply {
            condition = Reference("Condition/f201")
            use =
              CodeableConcept().apply {
                coding =
                  listOf(
                    Coding(
                      "http://terminology.hl7.org/CodeSystem/diagnosis-role",
                      "DD",
                      "Discharge diagnosis"
                    )
                  )
              }
          }
        )
    }
  }

  companion object {

    private const val mockServerPort = 8080
    private val mockWebServer: MockWebServer = MockWebServer()

    @JvmStatic
    @BeforeClass
    fun oneTimeSetup() {
      FhirEngineProvider.init(
        FhirEngineConfiguration(
          serverConfiguration = ServerConfiguration("http://127.0.0.1:$mockServerPort/fhir/"),
          testMode = true
        )
      )
      mockWebServer.start(mockServerPort)
    }

    @JvmStatic
    @AfterClass
    fun oneTimeTearDown() {
      FhirEngineProvider.cleanup()
      mockWebServer.shutdown()
    }
  }
}
