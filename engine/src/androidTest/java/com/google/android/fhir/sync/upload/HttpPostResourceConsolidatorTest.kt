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

package com.google.android.fhir.sync.upload

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.FhirServices
import com.google.android.fhir.db.Database
import com.google.android.fhir.db.ResourceNotFoundException
import com.google.android.fhir.logicalId
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent
import org.hl7.fhir.r4.model.DomainResource
import org.hl7.fhir.r4.model.InstantType
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.ResourceType
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(AndroidJUnit4::class)
class HttpPostResourceConsolidatorTest {
  @JvmField @Parameterized.Parameter(0) var encrypted: Boolean = false

  private val context: Context = ApplicationProvider.getApplicationContext()
  private lateinit var database: Database
  private lateinit var resourceConsolidator: ResourceConsolidator

  @Before
  fun setupDatabase() = runBlocking {
    database =
      FhirServices.builder(context)
        .inMemory()
        .apply {
          if (encrypted) enableEncryptionIfSupported()
          setSearchParameters(null)
        }
        .build()
        .database
    resourceConsolidator = HttpPostResourceConsolidator(database)
  }

  @After
  fun closeDatabase() {
    database.close()
  }

  @Test
  fun postSync_resourceConsolidation_updateResourceId() = runBlocking {
    val patientJsonString =
      """
        {
          "resourceType": "Patient",
          "id": "patient1"
        }
            """
        .trimIndent()
    val patient =
      FhirContext.forCached(FhirVersionEnum.R4).newJsonParser().parseResource(patientJsonString)
        as DomainResource
    database.insert(patient)
    val localChanges = database.getLocalChanges(patient.resourceType, patient.logicalId)

    val postSyncPatientJsonString =
      """
      {
        "resourceType": "Patient",
        "id": "patient2",
        "meta": {
          "versionId": "1"
        }
      }
        """
        .trimIndent()
    val postSyncPatient =
      FhirContext.forCached(FhirVersionEnum.R4)
        .newJsonParser()
        .parseResource(postSyncPatientJsonString) as DomainResource
    postSyncPatient.meta.lastUpdatedElement = InstantType.now()
    val uploadRequestResult =
      UploadRequestResult.Success(
        listOf(ResourceUploadResponseMapping(localChanges, postSyncPatient)),
      )
    resourceConsolidator.consolidate(uploadRequestResult)

    assertThat(database.select(ResourceType.Patient, "patient2").logicalId)
      .isEqualTo(postSyncPatient.logicalId)

    val exception =
      assertThrows(ResourceNotFoundException::class.java) {
        runBlocking { database.select(ResourceType.Patient, "patient1") }
      }

    assertThat(exception.message).isEqualTo("Resource not found with type Patient and id patient1!")
  }

  @Test
  fun postSync_resourceConsolidation_updateReferencesForResource() = runBlocking {
    val patientJsonString =
      """
        {
          "resourceType": "Patient",
          "id": "patient1"
        }
            """
        .trimIndent()
    val patient =
      FhirContext.forCached(FhirVersionEnum.R4).newJsonParser().parseResource(patientJsonString)
        as DomainResource
    val observationJsonString =
      """
        {
          "resourceType": "Observation",
          "id": "observation1",
          "subject": {
            "reference": "Patient/patient1"
          }
        }
            """
        .trimIndent()
    val observation =
      FhirContext.forCached(FhirVersionEnum.R4).newJsonParser().parseResource(observationJsonString)
        as DomainResource
    database.insert(patient, observation)
    val postSyncPatientJsonString =
      """
      {
        "resourceType": "Patient",
        "id": "patient2",
        "meta": {
          "versionId": "1"
        }
      }
        """
        .trimIndent()
    val postSyncPatient =
      FhirContext.forCached(FhirVersionEnum.R4)
        .newJsonParser()
        .parseResource(postSyncPatientJsonString) as DomainResource
    postSyncPatient.meta.lastUpdatedElement = InstantType.now()
    val localChanges = database.getLocalChanges(patient.resourceType, patient.logicalId)
    val uploadRequestResult =
      UploadRequestResult.Success(
        listOf(ResourceUploadResponseMapping(localChanges, postSyncPatient)),
      )

    resourceConsolidator.consolidate(uploadRequestResult)

    assertThat(
        (database.select(ResourceType.Observation, "observation1") as Observation)
          .subject
          .reference,
      )
      .isEqualTo("Patient/patient2")
  }

  @Test
  fun postSync_resourceConsolidation_updateReferencesForLocalChanges() = runBlocking {
    val patientJsonString =
      """
        {
          "resourceType": "Patient",
          "id": "patient1"
        }
            """
        .trimIndent()
    val patient =
      FhirContext.forCached(FhirVersionEnum.R4).newJsonParser().parseResource(patientJsonString)
        as DomainResource
    val observationJsonString =
      """
        {
          "resourceType": "Observation",
          "id": "observation1",
          "subject": {
            "reference": "Patient/patient1"
          }
        }
            """
        .trimIndent()
    val observation =
      FhirContext.forCached(FhirVersionEnum.R4).newJsonParser().parseResource(observationJsonString)
        as DomainResource
    database.insert(patient, observation)
    val postSyncPatientJsonString =
      """
      {
        "resourceType": "Patient",
        "id": "patient2",
        "meta": {
          "versionId": "1"
        }
      }
        """
        .trimIndent()
    val postSyncPatient =
      FhirContext.forCached(FhirVersionEnum.R4)
        .newJsonParser()
        .parseResource(postSyncPatientJsonString) as DomainResource
    postSyncPatient.meta.lastUpdatedElement = InstantType.now()
    val localChanges = database.getLocalChanges(patient.resourceType, patient.logicalId)
    val uploadRequestResult =
      UploadRequestResult.Success(
        listOf(ResourceUploadResponseMapping(localChanges, postSyncPatient)),
      )

    resourceConsolidator.consolidate(uploadRequestResult)

    val localChange = database.getLocalChanges(ResourceType.Observation, "observation1").last()
    assertThat(
        (FhirContext.forCached(FhirVersionEnum.R4)
            .newJsonParser()
            .parseResource(localChange.payload) as Observation)
          .subject
          .reference,
      )
      .isEqualTo("Patient/patient2")
  }

  // AllChangesBundleSquashed

  @Test
  fun postSync_allChangesBundleSquashedPost_resourceConsolidation_updateResourceId() = runBlocking {
    val preSyncPatientJsonString =
      """
        {
          "resourceType": "Patient",
          "id": "patient1"
        }
            """
        .trimIndent()
    val preSyncPatient =
      FhirContext.forCached(FhirVersionEnum.R4)
        .newJsonParser()
        .parseResource(preSyncPatientJsonString) as DomainResource
    database.insert(preSyncPatient)
    val localChanges =
      database.getLocalChanges(preSyncPatient.resourceType, preSyncPatient.logicalId)
    val bundleEntryComponentJsonString =
      """
        {
          "resourceType": "Bundle",
          "id": "bundle1",
          "type": "transaction-response",
          "entry": [
            {
              "response": {
                "status": "201 Created",
                "location": "Patient/patient2/_history/1",
                "etag": "1",
                "lastModified": "2024-04-08T11:15:42.648+00:00",
                "outcome": {
                  "resourceType": "OperationOutcome"
                }
              }
            },
            {
              "response": {
                "status": "201 Created",
                "location": "Encounter/8055/_history/1",
                "etag": "1",
                "lastModified": "2024-04-08T11:15:42.648+00:00",
                "outcome": {
                  "resourceType": "OperationOutcome"
                }
              }
            }
          ]
        }
            """
        .trimIndent()

    val postSyncResponseBundle =
      FhirContext.forCached(FhirVersionEnum.R4)
        .newJsonParser()
        .parseResource(bundleEntryComponentJsonString) as Bundle

    val patientResponseEntry =
      (postSyncResponseBundle.entry.firstOrNull() as BundleEntryComponent).response
    val secondResponseEntry = postSyncResponseBundle.entry[1]

    val uploadRequestResult =
      UploadRequestResult.Success(
        listOf(BundleComponentUploadResponseMapping(localChanges, patientResponseEntry)),
      )

    resourceConsolidator.consolidate(uploadRequestResult)

    assertThat(database.select(ResourceType.Patient, "patient2").logicalId)
      .isEqualTo(patientResponseEntry.resourceIdAndType?.first)

    val exception =
      assertThrows(ResourceNotFoundException::class.java) {
        runBlocking { database.select(ResourceType.Patient, "patient1") }
      }

    assertThat(exception.message).isEqualTo("Resource not found with type Patient and id patient1!")
  }

  @Test
  fun postSync_allChangesBundleSquashedPost_resourceConsolidation_updateReferencesForResource() =
    runBlocking {
      val preSyncPatientJsonString =
        """
        {
          "resourceType": "Patient",
          "id": "patient1"
        }
            """
          .trimIndent()
      val preSyncPatient =
        FhirContext.forCached(FhirVersionEnum.R4)
          .newJsonParser()
          .parseResource(preSyncPatientJsonString) as DomainResource
      val preSyncObservationJsonString =
        """
        {
          "resourceType": "Observation",
          "id": "observation1",
          "subject": {
            "reference": "Patient/patient1"
          }
        }
            """
          .trimIndent()
      val preSyncObservation =
        FhirContext.forCached(FhirVersionEnum.R4)
          .newJsonParser()
          .parseResource(preSyncObservationJsonString) as DomainResource
      database.insert(preSyncPatient, preSyncObservation)
      val patientLocalChanges =
        database.getLocalChanges(preSyncPatient.resourceType, preSyncPatient.logicalId)
      val observationLocalChanges =
        database.getLocalChanges(preSyncObservation.resourceType, preSyncObservation.logicalId)
      val bundleEntryComponentJsonString =
        """
        {
          "resourceType": "Bundle",
          "id": "bundle1",
          "type": "transaction-response",
          "entry": [
            {
              "response": {
                "status": "201 Created",
                "location": "Patient/patient2/_history/1",
                "etag": "1",
                "lastModified": "2024-04-08T11:15:42.648+00:00",
                "outcome": {
                  "resourceType": "OperationOutcome"
                }
              }
            },
            {
              "response": {
                "status": "201 Created",
                "location": "Observation/observation2/_history/1",
                "etag": "1",
                "lastModified": "2024-04-08T11:15:42.648+00:00",
                "outcome": {
                  "resourceType": "OperationOutcome"
                }
              }
            }
          ]
        }
            """
          .trimIndent()

      val postSyncResponseBundle =
        FhirContext.forCached(FhirVersionEnum.R4)
          .newJsonParser()
          .parseResource(bundleEntryComponentJsonString) as Bundle

      val patientResponseEntry =
        (postSyncResponseBundle.entry.firstOrNull() as BundleEntryComponent).response
      val observationResponseEntry =
        (postSyncResponseBundle.entry[1] as BundleEntryComponent).response

      val uploadRequestResult =
        UploadRequestResult.Success(
          listOf(
            BundleComponentUploadResponseMapping(patientLocalChanges, patientResponseEntry),
            BundleComponentUploadResponseMapping(observationLocalChanges, observationResponseEntry),
          ),
        )

      resourceConsolidator.consolidate(uploadRequestResult)

      assertThat(
          (database.select(ResourceType.Observation, "observation2") as Observation)
            .subject
            .reference,
        )
        .isEqualTo("Patient/patient2")
    }

  @Test
  fun postSync_allChangesBundleSquashedPost_resourceConsolidation_localChangesSquashed() =
    runBlocking {
      val preSyncPatientJsonString =
        """
        {
          "resourceType": "Patient",
          "id": "patient1"
        }
            """
          .trimIndent()
      val preSyncPatient =
        FhirContext.forCached(FhirVersionEnum.R4)
          .newJsonParser()
          .parseResource(preSyncPatientJsonString) as DomainResource
      database.insert(preSyncPatient)
      val localChanges =
        database.getLocalChanges(preSyncPatient.resourceType, preSyncPatient.logicalId)
      val bundleEntryComponentJsonString =
        """
        {
          "resourceType": "Bundle",
          "id": "bundle1",
          "type": "transaction-response",
          "entry": [
            {
              "response": {
                "status": "201 Created",
                "location": "Patient/patient2/_history/1",
                "etag": "1"
              }
            }
          ]
        }
            """
          .trimIndent()
      val postSyncResponseBundle =
        FhirContext.forCached(FhirVersionEnum.R4)
          .newJsonParser()
          .parseResource(bundleEntryComponentJsonString) as Bundle
      val patientResponseEntry =
        (postSyncResponseBundle.entry.firstOrNull() as BundleEntryComponent).response
      val uploadRequestResult =
        UploadRequestResult.Success(
          listOf(BundleComponentUploadResponseMapping(localChanges, patientResponseEntry)),
        )

      resourceConsolidator.consolidate(uploadRequestResult)

      assertThat(database.getAllLocalChanges()).isEmpty()
    }
}
