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
import com.google.android.fhir.logicalId
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.DomainResource
import org.hl7.fhir.r4.model.InstantType
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.ResourceType
import org.junit.After
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
  }

  @Test
  fun postSync_resourceConsolidation_updateReferences() = runBlocking {
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
    database.select(ResourceType.Observation, "observation1") as Observation
    val localChange = database.getLocalChanges(ResourceType.Observation, "observation1").last()

    assertThat(
        (database.select(ResourceType.Observation, "observation1") as Observation)
          .subject
          .reference,
      )
      .isEqualTo("Patient/patient2")
    assertThat(
        (FhirContext.forCached(FhirVersionEnum.R4)
            .newJsonParser()
            .parseResource(localChange.payload) as Observation)
          .subject
          .reference,
      )
      .isEqualTo("Patient/patient2")
  }
}
