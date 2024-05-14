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
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent
import org.hl7.fhir.r4.model.DomainResource
import org.hl7.fhir.r4.model.InstantType
import org.hl7.fhir.r4.model.ResourceType
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(AndroidJUnit4::class)
class ResourceConsolidatorTest {
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
    resourceConsolidator = DefaultResourceConsolidator(database)
  }

  @After
  fun closeDatabase() {
    database.close()
  }

  @Test
  fun consolidate_shouldUpdateMeta() = runBlocking {
    val patientJsonString =
      """
        {
          "resourceType": "Patient",
          "id": "patient1",
          "meta": {
            "versionId": "1",
            "lastUpdated": "2024-04-26T07:48:11.651+00:00"
          }
        }
            """
        .trimIndent()
    val patient =
      FhirContext.forR4Cached().newJsonParser().parseResource(patientJsonString) as DomainResource
    database.insert(patient)
    val localChanges = database.getLocalChanges(patient.resourceType, patient.logicalId)

    val postSyncPatientJsonString =
      """
      {
          "resourceType": "Patient",
          "id": "patient1",
          "meta": {
            "versionId": "2",
            "lastUpdated": "2024-04-27T07:48:11.651+00:00"
          }
        }
        """
        .trimIndent()
    val postSyncPatient =
      FhirContext.forR4Cached().newJsonParser().parseResource(postSyncPatientJsonString)
        as DomainResource
    val uploadRequestResult =
      UploadRequestResult.Success(
        listOf(ResourceUploadResponseMapping(localChanges, postSyncPatient)),
      )
    resourceConsolidator.consolidate(uploadRequestResult)

    assertThat(database.select(ResourceType.Patient, "patient1").meta.versionId).isEqualTo("2")
    assertThat(database.select(ResourceType.Patient, "patient1").meta.lastUpdatedElement.toString())
      .isEqualTo(InstantType("2024-04-27T07:48:11.651+00:00").toString())
  }

  @Test
  fun consolidate_allChangesBundleSquashedPut_shouldUpdateMetaData() = runBlocking {
    val preSyncPatientJsonString =
      """
        {
          "resourceType": "Patient",
          "id": "patient1",
          "meta": {
            "versionId": "1",
            "lastUpdated": "2024-04-26T07:48:11.651+00:00"
          }
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
                "location": "Patient/patient1/_history/2",
                "etag": "2",
                "lastModified": "2024-04-27T07:48:11.651+00:00",
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

    val uploadRequestResult =
      UploadRequestResult.Success(
        listOf(BundleComponentUploadResponseMapping(localChanges, patientResponseEntry)),
      )

    resourceConsolidator.consolidate(uploadRequestResult)

    assertThat(database.select(ResourceType.Patient, "patient1").meta.versionId).isEqualTo("2")
    assertThat(
        database.select(ResourceType.Patient, "patient1").meta.lastUpdatedElement.valueAsString,
      )
      .isEqualTo(InstantType("2024-04-27T07:48:11.651Z").valueAsString)
  }
}
