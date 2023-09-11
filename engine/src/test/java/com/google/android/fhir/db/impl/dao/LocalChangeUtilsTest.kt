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

package com.google.android.fhir.db.impl.dao

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.LocalChange
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.logicalId
import com.google.android.fhir.testing.assertJsonArrayEqualsIgnoringOrder
import com.google.android.fhir.testing.readFromFile
import com.google.android.fhir.testing.readJsonArrayFromFile
import com.google.android.fhir.versionId
import com.google.common.truth.Truth.assertThat
import java.time.Instant
import java.util.Date
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Meta
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.json.JSONArray
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LocalChangeUtilsTest : TestCase() {

  val jsonParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
  @Test
  fun `toLocalChange() should return LocalChange`() = runBlocking {
    val localChangeEntity =
      LocalChangeEntity(
        id = 1,
        resourceType = ResourceType.Patient.name,
        resourceId = "Patient-001",
        type = LocalChangeEntity.Type.INSERT,
        payload =
          jsonParser.encodeResourceToString(
            Patient().apply {
              id = "Patient-001"
              addName(
                HumanName().apply {
                  addGiven("John")
                  family = "Doe"
                }
              )
            }
          ),
        timestamp = Instant.now()
      )

    val localChange = localChangeEntity.toLocalChange()
    assertThat(localChangeEntity.id).isEqualTo(localChange.token.ids.first())
    assertThat(localChangeEntity.resourceType).isEqualTo(localChange.resourceType)
    assertThat(localChangeEntity.resourceId).isEqualTo(localChange.resourceId)
    assertThat(localChangeEntity.timestamp).isEqualTo(localChange.timestamp)
    assertThat(LocalChange.Type.from(localChangeEntity.type.value)).isEqualTo(localChange.type)
    assertThat(localChangeEntity.payload).isEqualTo(localChange.payload)
    assertThat(localChangeEntity.versionId).isEqualTo(localChange.versionId)
  }

  @Test
  fun insertAndUpdateLocalChanges_shouldReturnInsertChange() = runBlocking {
    val patient: Patient = readFromFile(Patient::class.java, "/date_test_patient.json")
    val insertionLocalChange = generateResourceAsLocalChange(patient)
    val updatedPatient = readFromFile(Patient::class.java, "/update_test_patient_1.json")
    val updateLocalChange = generateResourceUpdateAsLocalChange(patient, updatedPatient, 1L)
    val patientString = jsonParser.encodeResourceToString(updatedPatient)
    val squashedChange = LocalChangeUtils.squash(listOf(insertionLocalChange, updateLocalChange))
    with(squashedChange) {
      assertThat(type).isEqualTo(LocalChange.Type.INSERT)
      assertThat(resourceId).isEqualTo(patient.logicalId)
      assertThat(resourceType).isEqualTo(patient.resourceType.name)
      assertThat(payload).isEqualTo(patientString)
    }
  }

  @Test
  fun updateTwice_shouldReturnMergedPatch() = runBlocking {
    val remoteMeta =
      Meta().apply {
        versionId = "patient-version-1"
        lastUpdated = Date()
      }
    val remotePatient: Patient = readFromFile(Patient::class.java, "/date_test_patient.json")
    remotePatient.meta = remoteMeta
    val updatedPatient1 = readFromFile(Patient::class.java, "/update_test_patient_1.json")
    updatedPatient1.meta = remoteMeta
    val updatelocalChange1 = generateResourceUpdateAsLocalChange(remotePatient, updatedPatient1, 1L)
    val updatedPatient2 = readFromFile(Patient::class.java, "/update_test_patient_2.json")
    updatedPatient2.meta = remoteMeta
    val updatelocalChange2 =
      generateResourceUpdateAsLocalChange(updatedPatient1, updatedPatient2, 2L)
    val updatePatch = readJsonArrayFromFile("/update_patch_2.json")
    val squashedChange = LocalChangeUtils.squash(listOf(updatelocalChange1, updatelocalChange2))
    with(squashedChange) {
      assertThat(type).isEqualTo(LocalChange.Type.UPDATE)
      assertThat(resourceId).isEqualTo(remotePatient.logicalId)
      assertThat(resourceType).isEqualTo(remotePatient.resourceType.name)
      assertThat(versionId).isEqualTo(remoteMeta.versionId)
      assertJsonArrayEqualsIgnoringOrder(JSONArray(payload), updatePatch)
    }
  }

  @Test
  fun deleteAfterUpdates_shouldReturnDeleteLocalChange() = runBlocking {
    val remoteMeta =
      Meta().apply {
        versionId = "patient-version-1"
        lastUpdated = Date()
      }
    val remotePatient: Patient = readFromFile(Patient::class.java, "/date_test_patient.json")
    remotePatient.meta = remoteMeta
    val updatedPatient1 = remotePatient.copy()
    updatedPatient1.name = listOf(HumanName().addGiven("John").setFamily("Doe"))
    val updatelocalChange1 = generateResourceUpdateAsLocalChange(remotePatient, updatedPatient1, 1L)
    val updatedPatient2 = updatedPatient1.copy()
    updatedPatient2.name = listOf(HumanName().addGiven("Jimmy").setFamily("Doe"))
    val updatelocalChange2 =
      generateResourceUpdateAsLocalChange(updatedPatient1, updatedPatient2, 2L)
    val deleteLocalChange = generateDeleteResourceAsLocalChange(updatedPatient2, 3L)
    val squashedChange =
      LocalChangeUtils.squash(listOf(updatelocalChange1, updatelocalChange2, deleteLocalChange))
    with(squashedChange) {
      assertThat(type).isEqualTo(LocalChange.Type.DELETE)
      assertThat(resourceId).isEqualTo(remotePatient.logicalId)
      assertThat(resourceType).isEqualTo(remotePatient.resourceType.name)
      assertThat(versionId).isEqualTo(remoteMeta.versionId)
      assertThat(payload).isEmpty()
    }
  }

  private fun generateResourceUpdateAsLocalChange(
    oldEntity: Resource,
    updatedResource: Resource,
    currentChangeId: Long
  ): LocalChange {
    val jsonDiff = LocalChangeUtils.diff(jsonParser, oldEntity, updatedResource)
    return LocalChange(
      resourceId = oldEntity.logicalId,
      resourceType = oldEntity.resourceType.name,
      type = LocalChange.Type.UPDATE,
      payload = jsonDiff.toString(),
      versionId = oldEntity.versionId,
      token = LocalChangeToken(listOf(currentChangeId + 1)),
      timestamp = Instant.now()
    )
  }

  private fun generateResourceAsLocalChange(entity: Resource): LocalChange {
    return LocalChange(
      resourceId = entity.logicalId,
      resourceType = entity.resourceType.name,
      type = LocalChange.Type.INSERT,
      payload = jsonParser.encodeResourceToString(entity),
      versionId = entity.versionId,
      token = LocalChangeToken(listOf(1L)),
      timestamp = Instant.now()
    )
  }

  private fun generateDeleteResourceAsLocalChange(
    entity: Resource,
    currentChangeId: Long
  ): LocalChange {
    return LocalChange(
      resourceId = entity.logicalId,
      resourceType = entity.resourceType.name,
      type = LocalChange.Type.DELETE,
      payload = "",
      versionId = entity.versionId,
      token = LocalChangeToken(listOf(currentChangeId + 1)),
      timestamp = Instant.now()
    )
  }
}
