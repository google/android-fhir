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

package com.google.android.fhir.sync.upload

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.ContentTypes
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.db.impl.dao.LocalChangeUtils
import com.google.android.fhir.db.impl.dao.toLocalChange
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.sync.DeleteUploadRequest
import com.google.android.fhir.sync.PatchUploadRequest
import com.google.android.fhir.sync.PutUploadRequest
import com.google.common.truth.Truth
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SimpleUploadRequestGeneratorTest {

  companion object {
    val jsonParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
  }

  @Test
  fun `generateUploadRequests() should return empty list if there are no local changes`() =
    runBlocking {
      val generator =
        SimpleUploadRequestGenerator.getPutForCreateAndPatchForUpdateUploadRequestGenerator()
      val result = generator.generateUploadRequests(listOf())
      Truth.assertThat(result).isEmpty()
    }

  @Test
  fun `generateUploadRequests() with default generator for insert should return a put upload request`() =
    runBlocking {
      val patientResource =
        Patient().apply {
          id = "Patient-001"
          addName(
            HumanName().apply {
              addGiven("John")
              family = "Doe"
            }
          )
        }
      val insertionChange =
        LocalChangeEntity(
            id = 1,
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-001",
            type = LocalChangeEntity.Type.INSERT,
            payload = jsonParser.encodeResourceToString(patientResource)
          )
          .toLocalChange()
          .apply { token = LocalChangeToken(listOf(1)) }
      val generator =
        SimpleUploadRequestGenerator.getPutForCreateAndPatchForUpdateUploadRequestGenerator()

      val result = generator.generateUploadRequests(listOf(insertionChange))

      Truth.assertThat(result).hasSize(1)
      val putUploadRequest = result.get(0) as PutUploadRequest
      Truth.assertThat(putUploadRequest.resource).isInstanceOf(Patient::class.java)
      Truth.assertThat(putUploadRequest.resourceId).isEqualTo("Patient-001")
      Truth.assertThat(putUploadRequest.resourceType).isEqualTo(ResourceType.Patient)
      Truth.assertThat(putUploadRequest.headers).isEqualTo(emptyMap<String, String>())
      Truth.assertThat(putUploadRequest.localChangeToken.ids).contains(1L)
    }

  @Test
  fun `generateUploadRequests() with default generator for update should return a patch upload request`() =
    runBlocking {
      val updateChange =
        LocalChangeEntity(
            id = 2,
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-002",
            type = LocalChangeEntity.Type.UPDATE,
            payload =
              LocalChangeUtils.diff(
                  jsonParser,
                  Patient().apply {
                    id = "Patient-002"
                    addName(
                      HumanName().apply {
                        addGiven("Jane")
                        family = "Doe"
                      }
                    )
                  },
                  Patient().apply {
                    id = "Patient-002"
                    addName(
                      HumanName().apply {
                        addGiven("Janet")
                        family = "Doe"
                      }
                    )
                  }
                )
                .toString(),
            versionId = "v-p002-01"
          )
          .toLocalChange()
          .apply { LocalChangeToken(listOf(2)) }
      val generator =
        SimpleUploadRequestGenerator.getPutForCreateAndPatchForUpdateUploadRequestGenerator()

      val result = generator.generateUploadRequests(listOf(updateChange))

      Truth.assertThat(result).hasSize(1)
      val patchUploadRequest = result[0] as PatchUploadRequest
      Truth.assertThat(patchUploadRequest.patchBody)
        .isEqualTo(
          "[{\"op\":\"replace\",\"path\":\"\\/name\\/0\\/given\\/0\",\"value\":\"Janet\"}]"
        )
      Truth.assertThat(patchUploadRequest.resourceId).isEqualTo("Patient-002")
      Truth.assertThat(patchUploadRequest.resourceType).isEqualTo(ResourceType.Patient)
      Truth.assertThat(patchUploadRequest.headers)
        .containsEntry("Content-Type", ContentTypes.APPLICATION_JSON_PATCH)
      Truth.assertThat(patchUploadRequest.headers).containsEntry("If-Match", "W/\"v-p002-01\"")
      Truth.assertThat(patchUploadRequest.localChangeToken.ids).contains(2L)
    }

  @Test
  fun `generateUploadRequests() with default generator for delete should return a delete upload request`() =
    runBlocking {
      val deletionChange =
        LocalChangeEntity(
            id = 3,
            resourceType = ResourceType.Patient.name,
            resourceId = "Patient-003",
            type = LocalChangeEntity.Type.DELETE,
            payload =
              jsonParser.encodeResourceToString(
                Patient().apply {
                  id = "Patient-003"
                  addName(
                    HumanName().apply {
                      addGiven("John")
                      family = "Roe"
                    }
                  )
                }
              ),
            versionId = "v-p003-01"
          )
          .toLocalChange()
          .apply { LocalChangeToken(listOf(3)) }
      val generator =
        SimpleUploadRequestGenerator.getPutForCreateAndPatchForUpdateUploadRequestGenerator()

      val result = generator.generateUploadRequests(listOf(deletionChange))

      Truth.assertThat(result).hasSize(1)
      val deleteUploadRequest = result.get(0) as DeleteUploadRequest
      Truth.assertThat(deleteUploadRequest.resourceId).isEqualTo("Patient-003")
      Truth.assertThat(deleteUploadRequest.resourceType).isEqualTo(ResourceType.Patient)
      Truth.assertThat(deleteUploadRequest.headers).containsEntry("If-Match", "W/\"v-p003-01\"")
      Truth.assertThat(deleteUploadRequest.localChangeToken.ids).contains(3L)
    }
}
