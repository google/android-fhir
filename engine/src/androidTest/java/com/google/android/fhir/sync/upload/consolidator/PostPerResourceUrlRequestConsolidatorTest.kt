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

package com.google.android.fhir.sync.upload.consolidator

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.FhirServices
import com.google.android.fhir.LocalChange
import com.google.android.fhir.LocalChangeToken
import com.google.android.fhir.db.Database
import com.google.android.fhir.logicalId
import com.google.android.fhir.search.Search
import com.google.android.fhir.search.getQuery
import com.google.android.fhir.testing.assertResourceEquals
import com.google.common.truth.Truth.assertThat
import java.time.Instant
import java.util.Date
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.SearchParameter
import org.hl7.fhir.r4.model.StringType
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class PostPerResourceUrlRequestConsolidatorTest {
  private val context: Context = ApplicationProvider.getApplicationContext()
  private lateinit var services: FhirServices
  private lateinit var database: Database
  private lateinit var postPerResourceUrlRequestConsolidator: PostPerResourceUrlRequestConsolidator
  private val iParser = FhirContext.forR4Cached().newJsonParser()

  @Before
  fun setUp(): Unit = runBlocking {
    buildFhirService()
    postPerResourceUrlRequestConsolidator = PostPerResourceUrlRequestConsolidator(database)
  }

  private fun buildFhirService(customSearchParameter: List<SearchParameter>? = null) {
    services =
      FhirServices.builder(context)
        .inMemory()
        .apply { setSearchParameters(customSearchParameter) }
        .build()
    database = services.database
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun insertPatientAndReferringResource_shouldUpdateReferencesAndUpdateResourceId() = runBlocking {
    // create a patient
    val locallyCreatedPatientResourceId = "local-patient-1"
    val locallyCreatedPatient =
      Patient().apply {
        id = locallyCreatedPatientResourceId
        name = listOf(HumanName().setFamily("Family").setGiven(listOf(StringType("First Name"))))
      }
    database.insert(locallyCreatedPatient)

    // create an observation for the patient
    val locallyCreatedObservationResourceId = "local-observation-1"
    val locallyCreatedPatientObservation =
      Observation().apply {
        subject = Reference("Patient/$locallyCreatedPatientResourceId")
        id = locallyCreatedObservationResourceId
      }
    database.insert(locallyCreatedPatientObservation)

    val patientResourceEntity =
      database.selectEntity(locallyCreatedPatient.resourceType, locallyCreatedPatientResourceId)

    val patientLocalChanges =
      database.getLocalChanges(locallyCreatedPatient.resourceType, locallyCreatedPatientResourceId)

    // pretend that the resource has been created on the server with an updated ID
    val remotelyCreatedPatientResourceId = "remote-patient-1"
    val remotelyCreatedPatient =
      locallyCreatedPatient.apply { id = remotelyCreatedPatientResourceId }

    // perform consolidation
    postPerResourceUrlRequestConsolidator.consolidate(
      LocalChangeToken(patientLocalChanges.flatMap { it.token.ids }),
      remotelyCreatedPatient,
    )

    // check if resource is fetch-able by its new server assigned ID
    val updatedPatientResourceEntity =
      database.selectEntity(remotelyCreatedPatient.resourceType, remotelyCreatedPatient.id)
    assertThat(updatedPatientResourceEntity.resourceUuid)
      .isEqualTo(patientResourceEntity.resourceUuid)

    // verify that all the local changes are deleted for this newly server created resource
    val patientLocalChangesAfterConsolidation =
      database.getLocalChanges(
        updatedPatientResourceEntity.resourceUuid,
      )
    assertThat(patientLocalChangesAfterConsolidation).isEmpty()

    // verify that Observation is updated with new patient ID reference
    val updatedObservationResource =
      database.select(
        locallyCreatedPatientObservation.resourceType,
        locallyCreatedObservationResourceId,
      ) as Observation
    assertThat(updatedObservationResource.subject.reference)
      .isEqualTo("Patient/$remotelyCreatedPatientResourceId")

    // verify that Observation's LocalChanges are updated with new patient ID reference
    val updatedObservationLocalChanges =
      database.getLocalChanges(
        locallyCreatedPatientObservation.resourceType,
        locallyCreatedObservationResourceId,
      )
    assertThat(updatedObservationLocalChanges.size).isEqualTo(1)
    val observationLocalChange = updatedObservationLocalChanges[0]
    assertThat(observationLocalChange.type).isEqualTo(LocalChange.Type.INSERT)
    val observationLocalChangePayload =
      iParser.parseResource(observationLocalChange.payload) as Observation
    assertThat(observationLocalChangePayload.subject.reference)
      .isEqualTo("Patient/$remotelyCreatedPatientResourceId")

    // verify that Observation is searchable i.e. ReferenceIndex is updated with new patient ID
    // reference
    val searchedObservations =
      database.search<Observation>(
        Search(ResourceType.Observation)
          .apply {
            filter(
              Observation.SUBJECT,
              { value = "Patient/$remotelyCreatedPatientResourceId" },
            )
          }
          .getQuery(),
      )
    assertThat(searchedObservations.size).isEqualTo(1)
    assertThat(searchedObservations[0].logicalId).isEqualTo(locallyCreatedObservationResourceId)
  }

  @Test
  fun insertPatientAndInsertUpdateReferringResource_shouldUpdateReferencesAndUpdateResourceId() =
    runBlocking {
      // create a new patient
      val locallyCreatedPatientResourceId = "local-patient-1"
      val locallyCreatedPatient =
        Patient().apply {
          id = locallyCreatedPatientResourceId
          name = listOf(HumanName().setFamily("Family").setGiven(listOf(StringType("First Name"))))
        }
      database.insert(locallyCreatedPatient)

      // create an observation for the new patient
      val locallyCreatedObservationResourceId = "local-observation-1"
      val locallyCreatedPatientObservation =
        Observation().apply {
          subject = Reference("Patient/$locallyCreatedPatientResourceId")
          id = locallyCreatedObservationResourceId
        }
      database.insert(locallyCreatedPatientObservation)
      // update the observation resource (so that there are multiple local changes with references
      // to the same patient)
      database.update(
        locallyCreatedPatientObservation.apply {
          performer = listOf(Reference("Patient/$locallyCreatedPatientResourceId"))
        },
      )

      val patientResourceEntity =
        database.selectEntity(locallyCreatedPatient.resourceType, locallyCreatedPatientResourceId)

      val patientLocalChanges =
        database.getLocalChanges(
          locallyCreatedPatient.resourceType,
          locallyCreatedPatientResourceId,
        )
      val observationLocalChanges =
        database.getLocalChanges(
          locallyCreatedPatientObservation.resourceType,
          locallyCreatedObservationResourceId,
        )
      assertThat(observationLocalChanges.size).isEqualTo(2)

      // pretend that the resource has been created on the server with an updated ID
      val remotelyCreatedPatientResourceId = "remote-patient-1"
      val remotelyCreatedPatient =
        locallyCreatedPatient.apply { id = remotelyCreatedPatientResourceId }

      postPerResourceUrlRequestConsolidator.consolidate(
        LocalChangeToken(patientLocalChanges.flatMap { it.token.ids }),
        remotelyCreatedPatient,
      )

      // check if resource is fetch-able by its new ID
      val updatedPatientResourceEntity =
        database.selectEntity(remotelyCreatedPatient.resourceType, remotelyCreatedPatient.id)
      assertThat(updatedPatientResourceEntity.resourceUuid)
        .isEqualTo(patientResourceEntity.resourceUuid)

      // verify that all the local changes are deleted for this newly created resource
      val patientLocalChangesAfterConsolidation =
        database.getLocalChanges(
          updatedPatientResourceEntity.resourceUuid,
        )
      assertThat(patientLocalChangesAfterConsolidation).isEmpty()

      // verify that Observation is updated
      val updatedObservationResource =
        database.select(
          locallyCreatedPatientObservation.resourceType,
          locallyCreatedObservationResourceId,
        ) as Observation
      assertThat(updatedObservationResource.subject.reference)
        .isEqualTo("Patient/$remotelyCreatedPatientResourceId")

      // verify that Observation's LocalChanges are updated
      val updatedObservationLocalChanges =
        database.getLocalChanges(
          locallyCreatedPatientObservation.resourceType,
          locallyCreatedObservationResourceId,
        )
      assertThat(updatedObservationLocalChanges.size).isEqualTo(2)
      val observationLocalChange1 = updatedObservationLocalChanges[0]
      assertThat(observationLocalChange1.type).isEqualTo(LocalChange.Type.INSERT)
      val observationLocalChange1Payload =
        iParser.parseResource(observationLocalChange1.payload) as Observation
      assertThat(observationLocalChange1Payload.subject.reference)
        .isEqualTo("Patient/$remotelyCreatedPatientResourceId")

      val observationLocalChange2 = updatedObservationLocalChanges[1]
      assertThat(observationLocalChange2.type).isEqualTo(LocalChange.Type.UPDATE)
      // payload =
      // [{"op":"add","path":"\/performer","value":[{"reference":"Patient\/remote-patient-1"}]}]
      val observationLocalChange2Payload = JSONArray(observationLocalChange2.payload)
      val patch = observationLocalChange2Payload.get(0) as JSONObject
      val patchValueReference = patch.getJSONArray("value").get(0) as JSONObject
      assertThat(patchValueReference.getString("reference"))
        .isEqualTo("Patient/$remotelyCreatedPatientResourceId")

      // verify that Observation is searchable i.e. ReferenceIndex is updated
      val searchedObservations =
        database.search<Observation>(
          Search(ResourceType.Observation)
            .apply {
              filter(
                Observation.SUBJECT,
                { value = "Patient/$remotelyCreatedPatientResourceId" },
              )
            }
            .getQuery(),
        )
      assertThat(searchedObservations.size).isEqualTo(1)
      assertThat(searchedObservations[0].logicalId).isEqualTo(locallyCreatedObservationResourceId)
    }

  @Test
  fun updatePatientAndInsertUpdateReferringResource_shouldUpdateReferencesAndUpdateResourceId() =
    runBlocking {
      // imitate syncing of a patient from the server
      val serverCreatedPatientResourceId = "remote-patient-1"
      val serverSyncedPatient =
        Patient().apply {
          id = serverCreatedPatientResourceId
          name = listOf(HumanName().setFamily("Family").setGiven(listOf(StringType("First Name"))))
        }
      database.insertRemote(serverSyncedPatient)

      // update the patient resource
      val updatedPatient =
        serverSyncedPatient.apply {
          name =
            listOf(
              HumanName().setFamily("Updated Family").setGiven(listOf(StringType("First Name"))),
            )
        }
      database.update(updatedPatient)

      // create an observation for the new patient
      val locallyCreatedObservationResourceId = "local-observation-1"
      val locallyCreatedPatientObservation =
        Observation().apply {
          subject = Reference("Patient/$serverCreatedPatientResourceId")
          id = locallyCreatedObservationResourceId
        }
      database.insert(locallyCreatedPatientObservation)
      // update the observation (so that there are multiple local changes with references to the
      // same patient)
      database.update(
        locallyCreatedPatientObservation.apply {
          performer = listOf(Reference("Patient/$serverCreatedPatientResourceId"))
        },
      )

      val patientResourceEntity =
        database.selectEntity(serverSyncedPatient.resourceType, serverSyncedPatient.id)

      val patientLocalChanges =
        database.getLocalChanges(
          serverSyncedPatient.resourceType,
          serverCreatedPatientResourceId,
        )
      assertThat(patientLocalChanges.size).isEqualTo(1)
      val observationLocalChanges =
        database.getLocalChanges(
          locallyCreatedPatientObservation.resourceType,
          locallyCreatedObservationResourceId,
        )
      assertThat(observationLocalChanges.size).isEqualTo(2)

      // pretend that the resource has been updated on the server
      val serverUpdatedTime = Date.from(Instant.now())
      val serverImitatedResponse = updatedPatient.apply { meta.lastUpdated = serverUpdatedTime }
      postPerResourceUrlRequestConsolidator.consolidate(
        LocalChangeToken(patientLocalChanges.flatMap { it.token.ids }),
        serverImitatedResponse,
      )

      // check if resource is fetch-able by its new ID
      val updatedPatientResourceEntity =
        database.selectEntity(updatedPatient.resourceType, serverImitatedResponse.id)
      assertThat(updatedPatientResourceEntity.resourceUuid)
        .isEqualTo(patientResourceEntity.resourceUuid)

      // verify that all the local changes are deleted for this newly created resource
      val patientLocalChangesAfterConsolidation =
        database.getLocalChanges(
          updatedPatientResourceEntity.resourceUuid,
        )
      assertThat(patientLocalChangesAfterConsolidation).isEmpty()

      // verify that Observation is still the same as the last version
      val observationResource =
        database.select(
          locallyCreatedPatientObservation.resourceType,
          locallyCreatedObservationResourceId,
        ) as Observation
      assertResourceEquals(locallyCreatedPatientObservation, observationResource)

      // verify that Observation's LocalChanges are retained
      val observationLocalChangesPostConsolidation =
        database.getLocalChanges(
          locallyCreatedPatientObservation.resourceType,
          locallyCreatedObservationResourceId,
        )
      assertThat(observationLocalChanges.size)
        .isEqualTo(observationLocalChangesPostConsolidation.size)
      observationLocalChanges.forEachIndexed { changeIndex, originalObservationChange ->
        assertThat(originalObservationChange.token.ids)
          .containsExactlyElementsIn(
            observationLocalChangesPostConsolidation.get(changeIndex).token.ids,
          )
      }

      // verify that Observation is searchable i.e. ReferenceIndex is updated
      val searchedObservations =
        database.search<Observation>(
          Search(ResourceType.Observation)
            .apply {
              filter(
                Observation.SUBJECT,
                { value = "Patient/$serverCreatedPatientResourceId" },
              )
            }
            .getQuery(),
        )
      assertThat(searchedObservations.size).isEqualTo(1)
      assertThat(searchedObservations[0].logicalId).isEqualTo(locallyCreatedObservationResourceId)
    }
}
