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

package com.google.android.fhir.json.json.db.impl

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.MediumTest
import com.google.android.fhir.json.JsonServices
import com.google.android.fhir.json.db.ResourceNotFoundException
import com.google.android.fhir.json.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.json.resource.TestingUtils
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

/**
 * Integration tests for [DatabaseImpl]. There are written as integration tests as officially
 * recommend because:
 * * Different versions of android are shipped with different versions of SQLite. Integration tests
 * allow for better coverage on them.
 * * Robolectric's SQLite implementation does not match Android, e.g.:
 * https://github.com/robolectric/robolectric/blob/master/shadows/framework/src/main/java/org/robolectric/shadows/ShadowSQLiteConnection.java#L97
 */
@MediumTest
@RunWith(Parameterized::class)
class DatabaseImplTest {
  /** Whether to run the test with encryption on or off. */
  @JvmField @Parameterized.Parameter(0) var encrypted: Boolean = false

  private val context: Context = ApplicationProvider.getApplicationContext()
  private val services =
    JsonServices.builder(context)
      .inMemory()
      .apply { if (encrypted) enableEncryptionIfSupported() }
      .build()
  private val database = services.database

  @Before fun setUp(): Unit = runBlocking { database.insert(TEST_PATIENT_1) }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun insert_shouldInsertResource() = runBlocking {
    database.insert(TEST_PATIENT_2)
    TestingUtils.assertResourceEquals(TEST_PATIENT_2, database.select(TEST_PATIENT_2_ID))
  }

  @Test
  fun insertAll_shouldInsertResources() = runBlocking {
    val patients = ArrayList<JSONObject>()
    patients.add(TEST_PATIENT_1)
    patients.add(TEST_PATIENT_2)
    database.insert(*patients.toTypedArray())
    TestingUtils.assertResourceEquals(TEST_PATIENT_1, database.select(TEST_PATIENT_1_ID))
    TestingUtils.assertResourceEquals(TEST_PATIENT_2, database.select(TEST_PATIENT_2_ID))
  }

  @Test
  fun update_existentResource_shouldUpdateResource() = runBlocking {
    val patient = JSONObject()
    patient.put("id", TEST_PATIENT_1_ID)
    patient.put("gender", "male")
    database.update(patient)
    TestingUtils.assertResourceEquals(patient, database.select(TEST_PATIENT_1_ID))
  }

  @Test
  fun update_existentResourceWithNoChange_shouldNotUpdateResource() = runBlocking {
    val patient: JSONObject = TestingUtils.readJsonFromFile("/date_test_patient.json")
    database.insert(patient)
    patient.put("gender", "female")
    database.update(patient)

    patient.getJSONArray("name").getJSONObject(0).put("family", "TestPatient")
    database.update(patient)
    val squashedLocalChange =
      database.getAllLocalChanges().single { it.localChange.resourceId == patient.get("id") }
    assertThat(squashedLocalChange.token.ids.size).isEqualTo(3)
    with(squashedLocalChange.localChange) {
      assertThat(resourceId).isEqualTo(patient.get("id"))
      assertThat(type).isEqualTo(LocalChangeEntity.Type.INSERT)
      assertThat(JSONObject(payload).toString()).isEqualTo(patient.toString())
    }

    // update patient with no local change
    database.update(patient)
    val squashedLocalChangeWithNoFurtherUpdate =
      database.getAllLocalChanges().single { it.localChange.resourceId == patient.get("id") }
    assertThat(squashedLocalChangeWithNoFurtherUpdate.token.ids.size).isEqualTo(3)
    with(squashedLocalChangeWithNoFurtherUpdate.localChange) {
      assertThat(resourceId).isEqualTo(patient.get("id"))
      assertThat(type).isEqualTo(LocalChangeEntity.Type.INSERT)
      assertThat(JSONObject(payload).toString()).isEqualTo(patient.toString())
    }
  }

  @Test
  fun update_nonExistingResource_shouldNotInsertResource() {
    val resourceNotFoundException =
      assertThrows(ResourceNotFoundException::class.java) {
        runBlocking { database.update(TEST_PATIENT_2) }
      }
    /* ktlint-disable max-line-length */
    assertThat(resourceNotFoundException.message)
      .isEqualTo(
        "Resource not found with id $TEST_PATIENT_2_ID!"
        /* ktlint-enable max-line-length */
        )
  }

  @Test
  fun select_nonexistentResource_shouldThrowResourceNotFoundException() {
    val resourceNotFoundException =
      assertThrows(ResourceNotFoundException::class.java) {
        runBlocking { database.select("nonexistent_patient") }
      }
    assertThat(resourceNotFoundException.message)
      .isEqualTo("Resource not found with id nonexistent_patient!")
  }

  @Test
  fun select_shouldReturnResource() = runBlocking {
    TestingUtils.assertResourceEquals(TEST_PATIENT_1, database.select(TEST_PATIENT_1_ID))
  }
  //
  // @Test
  // fun insert_shouldAddInsertLocalChange() = runBlocking {
  //   val testPatient2String = services.parser.encodeResourceToString(TEST_PATIENT_2)
  //   database.insert(TEST_PATIENT_2)
  //   val (_, resourceType, resourceId, _, type, payload) =
  //     database
  //       .getAllLocalChanges()
  //       .single { it.localChange.resourceId.equals(TEST_PATIENT_2_ID) }
  //       .localChange
  //   assertThat(type).isEqualTo(LocalChangeEntity.Type.INSERT)
  //   assertThat(resourceId).isEqualTo(TEST_PATIENT_2_ID)
  //   assertThat(resourceType).isEqualTo(TEST_PATIENT_2.resourceType.name)
  //   assertThat(payload).isEqualTo(testPatient2String)
  // }
  //
  // @Test
  // fun update_insertAndUpdate_shouldAddUpdateLocalChange() = runBlocking {
  //   var patient: Patient = testingUtils.readFromFile(Patient::class.java,
  // "/date_test_patient.json")
  //   database.insert(patient)
  //   patient = testingUtils.readFromFile(Patient::class.java, "/update_test_patient_1.json")
  //   database.update(patient)
  //   val patientString = services.parser.encodeResourceToString(patient)
  //   val (_, resourceType, resourceId, _, type, payload) =
  //     database
  //       .getAllLocalChanges()
  //       .single { it.localChange.resourceId.equals(patient.logicalId) }
  //       .localChange
  //   assertThat(type).isEqualTo(LocalChangeEntity.Type.INSERT)
  //   assertThat(resourceId).isEqualTo(patient.logicalId)
  //   assertThat(resourceType).isEqualTo(patient.resourceType.name)
  //   assertThat(payload).isEqualTo(patientString)
  // }
  //
  // @Test
  // fun update_remoteResourceWithLocalChange_shouldSaveVersionIdAndLastUpdated() = runBlocking {
  //   val patient =
  //     Patient().apply {
  //       id = "remote-patient-1"
  //       addName(
  //         HumanName().apply {
  //           family = "FamilyName"
  //           addGiven("FirstName")
  //         }
  //       )
  //       meta =
  //         Meta().apply {
  //           versionId = "remote-patient-1-version-001"
  //           lastUpdated = Date()
  //         }
  //     }
  //
  //   database.insertRemote(patient)
  //
  //   val updatedPatient =
  //     Patient().apply {
  //       id = "remote-patient-1"
  //       addName(
  //         HumanName().apply {
  //           family = "UpdatedFamilyName"
  //           addGiven("UpdatedFirstName")
  //         }
  //       )
  //     }
  //   database.update(updatedPatient)
  //
  //   val selectedEntity = database.selectEntity(ResourceType.Patient, "remote-patient-1")
  //   assertThat(selectedEntity.resourceId).isEqualTo("remote-patient-1")
  //   assertThat(selectedEntity.versionId).isEqualTo(patient.meta.versionId)
  //   assertThat(selectedEntity.lastUpdatedRemote).isEqualTo(patient.meta.lastUpdated.toInstant())
  //
  //   val squashedLocalChange =
  //     database.getAllLocalChanges().first { it.localChange.resourceId == "remote-patient-1" }
  //   assertThat(squashedLocalChange.localChange.resourceId).isEqualTo("remote-patient-1")
  //   assertThat(squashedLocalChange.localChange.versionId).isEqualTo(patient.meta.versionId)
  // }
  //
  // @Test
  // fun delete_shouldAddDeleteLocalChange() = runBlocking {
  //   database.delete(ResourceType.Patient, TEST_PATIENT_1_ID)
  //   val (_, resourceType, resourceId, _, type, payload) =
  //     database
  //       .getAllLocalChanges()
  //       .single { it.localChange.resourceId.equals(TEST_PATIENT_1_ID) }
  //       .localChange
  //   assertThat(type).isEqualTo(LocalChangeEntity.Type.DELETE)
  //   assertThat(resourceId).isEqualTo(TEST_PATIENT_1_ID)
  //   assertThat(resourceType).isEqualTo(TEST_PATIENT_1.resourceType.name)
  //   assertThat(payload).isEmpty()
  // }
  //
  // @Test
  // fun delete_nonExistent_shouldNotInsertLocalChange() = runBlocking {
  //   database.delete(ResourceType.Patient, "nonexistent_patient")
  //   assertThat(
  //       database.getAllLocalChanges().map { it.localChange }.none {
  //         it.type.equals(LocalChangeEntity.Type.DELETE) &&
  //           it.resourceId.equals("nonexistent_patient")
  //       }
  //     )
  //     .isTrue()
  // }
  //
  // @Test
  // fun deleteUpdates_shouldDeleteLocalChanges() = runBlocking {
  //   var patient: Patient = testingUtils.readFromFile(Patient::class.java,
  // "/date_test_patient.json")
  //   database.insert(patient)
  //   patient = testingUtils.readFromFile(Patient::class.java, "/update_test_patient_1.json")
  //   database.update(patient)
  //   services.parser.encodeResourceToString(patient)
  //   val (token, _) =
  //     database.getAllLocalChanges().single { it.localChange.resourceId.equals(patient.logicalId)
  // }
  //   database.deleteUpdates(token)
  //   assertThat(
  //       database.getAllLocalChanges().none { it.localChange.resourceId.equals(patient.logicalId)
  // }
  //     )
  //     .isTrue()
  // }
  //
  // @Test
  // fun insert_remoteResource_shouldNotInsertLocalChange() = runBlocking {
  //   val patient: Patient = testingUtils.readFromFile(Patient::class.java,
  // "/date_test_patient.json")
  //   database.insertRemote(patient)
  //   assertThat(
  //       database.getAllLocalChanges().map { it.localChange }.none {
  //         it.resourceId.equals(patient.logicalId)
  //       }
  //     )
  //     .isTrue()
  // }
  //
  // @Test
  // fun insert_remoteResource_shouldSaveVersionIdAndLastUpdated() = runBlocking {
  //   val patient =
  //     Patient().apply {
  //       id = "remote-patient-1"
  //       meta =
  //         Meta().apply {
  //           versionId = "remote-patient-1-version-1"
  //           lastUpdated = Date()
  //         }
  //     }
  //   database.insertRemote(patient)
  //   val selectedEntity = database.selectEntity(ResourceType.Patient, "remote-patient-1")
  //   assertThat(selectedEntity.versionId).isEqualTo("remote-patient-1-version-1")
  //   assertThat(selectedEntity.lastUpdatedRemote).isEqualTo(patient.meta.lastUpdated.toInstant())
  // }
  //
  // @Test
  // fun insert_remoteResourceWithNoMeta_shouldSaveNullRemoteVersionAndLastUpdated() = runBlocking {
  //   val patient = Patient().apply { id = "remote-patient-2" }
  //   database.insertRemote(patient)
  //   val selectedEntity = database.selectEntity(ResourceType.Patient, "remote-patient-2")
  //   assertThat(selectedEntity.versionId).isNull()
  //   assertThat(selectedEntity.lastUpdatedRemote).isNull()
  // }
  //
  // @Test
  // fun insert_localResourceWithNoMeta_shouldSaveNullRemoteVersionAndLastUpdated() = runBlocking {
  //   val patient = Patient().apply { id = "local-patient-2" }
  //   database.insert(patient)
  //   val selectedEntity = database.selectEntity(ResourceType.Patient, "local-patient-2")
  //   assertThat(selectedEntity.versionId).isNull()
  //   assertThat(selectedEntity.lastUpdatedRemote).isNull()
  // }
  //
  // @Test
  // fun insert_localResourceWithNoMetaAndSync_shouldSaveRemoteVersionAndLastUpdated() = runBlocking
  // {
  //   val patient = Patient().apply { id = "remote-patient-3" }
  //   val remoteMeta =
  //     Meta().apply {
  //       versionId = "remote-patient-3-version-001"
  //       lastUpdated = Date()
  //     }
  //   database.insert(patient)
  //   services.fhirEngine.syncUpload { it ->
  //     it.first { it.localChange.resourceId == "remote-patient-3" }.let {
  //       flowOf(
  //         it.token to
  //           Patient().apply {
  //             id = it.localChange.resourceId
  //             meta = remoteMeta
  //           }
  //       )
  //     }
  //   }
  //   val selectedEntity = database.selectEntity(ResourceType.Patient, "remote-patient-3")
  //   assertThat(selectedEntity.versionId).isEqualTo(remoteMeta.versionId)
  //   assertThat(selectedEntity.lastUpdatedRemote).isEqualTo(remoteMeta.lastUpdated.toInstant())
  // }
  //
  // @Test
  // fun insertAll_remoteResources_shouldNotInsertAnyLocalChange() = runBlocking {
  //   val patient: Patient = testingUtils.readFromFile(Patient::class.java,
  // "/date_test_patient.json")
  //   database.insertRemote(patient, TEST_PATIENT_2)
  //   assertThat(
  //       database.getAllLocalChanges().map { it.localChange }.none {
  //         it.resourceId in listOf(patient.logicalId, TEST_PATIENT_2_ID)
  //       }
  //     )
  //     .isTrue()
  // }
  //
  // @Test
  // fun update_remoteResource_readSquashedChanges_shouldReturnPatch() = runBlocking {
  //   val patient: Patient = testingUtils.readFromFile(Patient::class.java,
  // "/date_test_patient.json")
  //   database.insertRemote(patient)
  //   val updatedPatient =
  //     testingUtils.readFromFile(Patient::class.java, "/update_test_patient_1.json")
  //   val updatePatch = testingUtils.readJsonArrayFromFile("/update_patch_1.json")
  //   database.update(updatedPatient)
  //   val (_, resourceType, resourceId, _, type, payload) =
  //     database
  //       .getAllLocalChanges()
  //       .single { it.localChange.resourceId.equals(patient.logicalId) }
  //       .localChange
  //   assertThat(type).isEqualTo(LocalChangeEntity.Type.UPDATE)
  //   assertThat(resourceId).isEqualTo(patient.logicalId)
  //   assertThat(resourceType).isEqualTo(patient.resourceType.name)
  //   testingUtils.assertJsonArrayEqualsIgnoringOrder(JSONArray(payload), updatePatch)
  // }
  //
  // @Test
  // fun updateTwice_remoteResource_readSquashedChanges_shouldReturnMergedPatch() = runBlocking {
  //   val remoteMeta =
  //     Meta().apply {
  //       versionId = "patient-version-1"
  //       lastUpdated = Date()
  //     }
  //   var patient: Patient = testingUtils.readFromFile(Patient::class.java,
  // "/date_test_patient.json")
  //   patient.meta = remoteMeta
  //   database.insertRemote(patient)
  //   patient = testingUtils.readFromFile(Patient::class.java, "/update_test_patient_1.json")
  //   database.update(patient)
  //   patient = testingUtils.readFromFile(Patient::class.java, "/update_test_patient_2.json")
  //   database.update(patient)
  //   val updatePatch = testingUtils.readJsonArrayFromFile("/update_patch_2.json")
  //   val (_, resourceType, resourceId, _, type, payload, versionId) =
  //     database
  //       .getAllLocalChanges()
  //       .single { it.localChange.resourceId.equals(patient.logicalId) }
  //       .localChange
  //   assertThat(type).isEqualTo(LocalChangeEntity.Type.UPDATE)
  //   assertThat(resourceId).isEqualTo(patient.logicalId)
  //   assertThat(resourceType).isEqualTo(patient.resourceType.name)
  //   assertThat(resourceType).isEqualTo(patient.resourceType.name)
  //   assertThat(versionId).isEqualTo(remoteMeta.versionId)
  //   testingUtils.assertJsonArrayEqualsIgnoringOrder(JSONArray(payload), updatePatch)
  // }
  //
  // @Test
  // fun delete_remoteResource_shouldReturnDeleteLocalChange() = runBlocking {
  //   database.insertRemote(TEST_PATIENT_2)
  //   database.delete(ResourceType.Patient, TEST_PATIENT_2_ID)
  //   val (_, resourceType, resourceId, _, type, payload, versionId) =
  //     database.getAllLocalChanges().map { it.localChange }.single {
  //       it.resourceId.equals(TEST_PATIENT_2_ID)
  //     }
  //   assertThat(type).isEqualTo(LocalChangeEntity.Type.DELETE)
  //   assertThat(resourceId).isEqualTo(TEST_PATIENT_2_ID)
  //   assertThat(resourceType).isEqualTo(TEST_PATIENT_2.resourceType.name)
  //   assertThat(versionId).isEqualTo(TEST_PATIENT_2.versionId)
  //   assertThat(payload).isEmpty()
  // }
  //
  // @Test
  // fun delete_remoteResource_updateResource_shouldReturnDeleteLocalChange() = runBlocking {
  //   database.insertRemote(TEST_PATIENT_2)
  //   TEST_PATIENT_2.name = listOf(HumanName().addGiven("John").setFamily("Doe"))
  //   database.update(TEST_PATIENT_2)
  //   TEST_PATIENT_2.name = listOf(HumanName().addGiven("Jimmy").setFamily("Doe"))
  //   database.update(TEST_PATIENT_2)
  //   database.delete(ResourceType.Patient, TEST_PATIENT_2_ID)
  //   val (_, resourceType, resourceId, _, type, payload) =
  //     database.getAllLocalChanges().map { it.localChange }.single {
  //       it.resourceId.equals(TEST_PATIENT_2_ID)
  //     }
  //   assertThat(type).isEqualTo(LocalChangeEntity.Type.DELETE)
  //   assertThat(resourceId).isEqualTo(TEST_PATIENT_2_ID)
  //   assertThat(resourceType).isEqualTo(TEST_PATIENT_2.resourceType.name)
  //   assertThat(payload).isEmpty()
  // }

  private companion object {
    const val mockEpochTimeStamp = 1628516301000
    const val TEST_PATIENT_1_ID = "mom"
    val TEST_PATIENT_1 = TestingUtils.readJsonFromFile("/lastupdated_ts_test_patient.json")

    const val TEST_PATIENT_2_ID = "f001"
    val TEST_PATIENT_2 = TestingUtils.readJsonFromFile("/date_test_patient.json")

    @JvmStatic @Parameters(name = "encrypted={0}") fun data(): Array<Boolean> = arrayOf(true, false)
  }
}
