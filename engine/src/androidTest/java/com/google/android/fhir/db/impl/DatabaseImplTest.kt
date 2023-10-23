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

package com.google.android.fhir.db.impl

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.MediumTest
import ca.uhn.fhir.rest.gclient.StringClientParam
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import com.google.android.fhir.DateProvider
import com.google.android.fhir.FhirServices
import com.google.android.fhir.LocalChange
import com.google.android.fhir.LocalChangeToken
import com.google.android.fhir.SearchResult
import com.google.android.fhir.db.Database
import com.google.android.fhir.db.ResourceNotFoundException
import com.google.android.fhir.logicalId
import com.google.android.fhir.search.LOCAL_LAST_UPDATED_PARAM
import com.google.android.fhir.search.Operation
import com.google.android.fhir.search.Order
import com.google.android.fhir.search.Search
import com.google.android.fhir.search.StringFilterModifier
import com.google.android.fhir.search.execute
import com.google.android.fhir.search.getQuery
import com.google.android.fhir.search.has
import com.google.android.fhir.search.include
import com.google.android.fhir.search.revInclude
import com.google.android.fhir.sync.upload.LocalChangesFetchMode
import com.google.android.fhir.sync.upload.UploadSyncResult
import com.google.android.fhir.testing.assertJsonArrayEqualsIgnoringOrder
import com.google.android.fhir.testing.assertResourceEquals
import com.google.android.fhir.testing.readFromFile
import com.google.android.fhir.testing.readJsonArrayFromFile
import com.google.android.fhir.versionId
import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import java.time.Instant
import java.util.Date
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.CarePlan
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Extension
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.Immunization
import org.hl7.fhir.r4.model.Meta
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Organization
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Practitioner
import org.hl7.fhir.r4.model.Quantity
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.RiskAssessment
import org.hl7.fhir.r4.model.SearchParameter
import org.hl7.fhir.r4.model.StringType
import org.json.JSONArray
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
 *   allow for better coverage on them.
 * * Robolectric's SQLite implementation does not match Android, e.g.:
 *   https://github.com/robolectric/robolectric/blob/master/shadows/framework/src/main/java/org/robolectric/shadows/ShadowSQLiteConnection.java#L97
 */
@MediumTest
@RunWith(Parameterized::class)
class DatabaseImplTest {
  /** Whether to run the test with encryption on or off. */
  @JvmField @Parameterized.Parameter(0) var encrypted: Boolean = false

  private val context: Context = ApplicationProvider.getApplicationContext()
  private lateinit var services: FhirServices
  private lateinit var database: Database

  @Before
  fun setUp(): Unit = runBlocking {
    buildFhirService()
    database.insert(TEST_PATIENT_1)
  }

  private fun buildFhirService(customSearchParameter: List<SearchParameter>? = null) {
    services =
      FhirServices.builder(context)
        .inMemory()
        .apply {
          if (encrypted) enableEncryptionIfSupported()
          setSearchParameters(customSearchParameter)
        }
        .build()
    database = services.database
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun insert_shouldInsertResource() = runBlocking {
    database.insert(TEST_PATIENT_2)
    assertResourceEquals(TEST_PATIENT_2, database.select(ResourceType.Patient, TEST_PATIENT_2_ID))
  }

  @Test
  fun insertAll_shouldInsertResources() = runBlocking {
    val patients = ArrayList<Patient>()
    patients.add(TEST_PATIENT_1)
    patients.add(TEST_PATIENT_2)
    database.insert(*patients.toTypedArray())
    assertResourceEquals(TEST_PATIENT_1, database.select(ResourceType.Patient, TEST_PATIENT_1_ID))
    assertResourceEquals(TEST_PATIENT_2, database.select(ResourceType.Patient, TEST_PATIENT_2_ID))
  }

  @Test
  fun update_existentResource_shouldUpdateResource() = runBlocking {
    val patient = Patient()
    patient.id = TEST_PATIENT_1_ID
    patient.gender = Enumerations.AdministrativeGender.FEMALE
    database.update(patient)
    assertResourceEquals(patient, database.select(ResourceType.Patient, TEST_PATIENT_1_ID))
  }

  @Test
  fun update_existentResourceWithNoChange_shouldNotUpdateResource() = runBlocking {
    val patient: Patient = readFromFile(Patient::class.java, "/date_test_patient.json")
    database.insert(patient)
    patient.gender = Enumerations.AdministrativeGender.FEMALE
    database.update(patient)
    patient.name[0].family = "TestPatient"
    database.update(patient)
    val resourceLocalChanges =
      database.getAllLocalChanges().filter { it.resourceId == patient.logicalId }
    assertThat(resourceLocalChanges.size).isEqualTo(3)
    with(resourceLocalChanges) {
      assertThat(all { it.resourceId == patient.logicalId }).isTrue()
      assertThat(all { it.resourceType == patient.resourceType.name }).isTrue()
      assertThat(get(0).type).isEqualTo(LocalChange.Type.INSERT)
    }

    // update patient with no local change
    database.update(patient)
    val resourceLocalChangesWithNoFurtherUpdate =
      database.getAllLocalChanges().filter { it.resourceId.equals(patient.logicalId) }
    assertThat(resourceLocalChangesWithNoFurtherUpdate.size).isEqualTo(3)
    with(resourceLocalChangesWithNoFurtherUpdate) {
      assertThat(all { it.resourceId == patient.logicalId }).isTrue()
      assertThat(all { it.resourceType == patient.resourceType.name }).isTrue()
      assertThat(get(0).type).isEqualTo(LocalChange.Type.INSERT)
    }
  }

  @Test
  fun getLocalChanges_withSingleLocaleChange_shouldReturnSingleLocalChanges() = runBlocking {
    val patient: Patient = readFromFile(Patient::class.java, "/date_test_patient.json")
    database.insert(patient)
    val patientString = services.parser.encodeResourceToString(patient)
    val resourceLocalChanges = database.getLocalChanges(patient.resourceType, patient.logicalId)
    assertThat(resourceLocalChanges.size).isEqualTo(1)
    with(resourceLocalChanges[0]) {
      assertThat(resourceId).isEqualTo(patient.logicalId)
      assertThat(resourceType).isEqualTo(patient.resourceType.name)
      assertThat(type).isEqualTo(LocalChange.Type.INSERT)
      assertThat(payload).isEqualTo(patientString)
    }
  }

  @Test
  fun getLocalChanges_withMultipleLocaleChanges_shouldReturnAllLocalChanges() = runBlocking {
    val patient: Patient = readFromFile(Patient::class.java, "/date_test_patient.json")
    database.insert(patient)

    patient.gender = Enumerations.AdministrativeGender.FEMALE
    database.update(patient)
    patient.name[0].family = "TestPatient"
    database.update(patient)

    val resourceLocalChanges = database.getLocalChanges(patient.resourceType, patient.logicalId)
    with(resourceLocalChanges) {
      assertThat(size).isEqualTo(3)
      assertThat(all { change -> change.resourceId == patient.logicalId }).isTrue()
      assertThat(all { change -> change.resourceType == patient.resourceType.name }).isTrue()
      assertThat(get(0).type).isEqualTo(LocalChange.Type.INSERT)
      assertThat(get(1).type).isEqualTo(LocalChange.Type.UPDATE)
      assertThat(get(2).type).isEqualTo(LocalChange.Type.UPDATE)
    }
  }

  @Test
  fun getLocalChanges_withWrongResourceId_shouldReturnNull() = runBlocking {
    val patient: Patient = readFromFile(Patient::class.java, "/date_test_patient.json")
    database.insert(patient)
    assertThat(database.getLocalChanges(patient.resourceType, "nonexistent_patient")).isEmpty()
  }

  @Test
  fun getLocalChanges_withWrongResourceType_shouldReturnNull() = runBlocking {
    val patient: Patient = readFromFile(Patient::class.java, "/date_test_patient.json")
    database.insert(patient)
    assertThat(database.getLocalChanges(ResourceType.Encounter, patient.logicalId)).isEmpty()
  }

  @Test
  fun getAllChangesForEarliestChangedResource_withMultipleChanges_shouldReturnFirstChange() =
    runBlocking {
      val patient: Patient = readFromFile(Patient::class.java, "/date_test_patient.json")
      database.insert(patient)
      database.insert(TEST_PATIENT_2)
      database.update(
        TEST_PATIENT_1.copy().apply { gender = Enumerations.AdministrativeGender.FEMALE },
      )
      assertThat(
          database.getAllChangesForEarliestChangedResource().all {
            it.resourceId.equals(TEST_PATIENT_1.logicalId)
          },
        )
        .isTrue()
    }

  @Test
  fun clearDatabase_shouldClearAllTablesData() = runBlocking {
    val patient: Patient = readFromFile(Patient::class.java, "/date_test_patient.json")
    database.insert(patient)
    val patientString = services.parser.encodeResourceToString(patient)
    val resourceLocalChanges = database.getLocalChanges(patient.resourceType, patient.logicalId)
    assertThat(resourceLocalChanges.size).isEqualTo(1)
    with(resourceLocalChanges[0]) {
      assertThat(resourceId).isEqualTo(patient.logicalId)
      assertThat(resourceType).isEqualTo(patient.resourceType.name)
      assertThat(LocalChange.Type.from(type.value)).isEqualTo(LocalChange.Type.INSERT)
      assertThat(payload).isEqualTo(patientString)
    }
    assertResourceEquals(patient, database.select(ResourceType.Patient, patient.logicalId))
    database.clearDatabase()

    assertThat(database.getLocalChanges(patient.resourceType, patient.logicalId)).isEmpty()

    val resourceNotFoundException =
      assertThrows(ResourceNotFoundException::class.java) {
        runBlocking { database.select(ResourceType.Patient, patient.logicalId) }
      }
    assertThat(resourceNotFoundException.message)
      .isEqualTo("Resource not found with type Patient and id ${patient.logicalId}!")
  }

  @Test
  fun purge_withLocalChangeAndForcePurgeTrue_shouldPurgeResource() = runBlocking {
    database.purge(ResourceType.Patient, TEST_PATIENT_1_ID, true)
    // after purge the resource is not available in database
    val resourceNotFoundException =
      assertThrows(ResourceNotFoundException::class.java) {
        runBlocking { database.select(ResourceType.Patient, TEST_PATIENT_1_ID) }
      }
    assertThat(resourceNotFoundException.message)
      .isEqualTo(
        "Resource not found with type ${TEST_PATIENT_1.resourceType.name} and id $TEST_PATIENT_1_ID!",
      )
    assertThat(database.getLocalChanges(ResourceType.Patient, TEST_PATIENT_1_ID)).isEmpty()
  }

  @Test
  fun purge_withLocalChangeAndForcePurgeFalse_shouldThrowIllegalStateException() = runBlocking {
    val resourceIllegalStateException =
      assertThrows(IllegalStateException::class.java) {
        runBlocking { database.purge(ResourceType.Patient, TEST_PATIENT_1_ID) }
      }
    assertThat(resourceIllegalStateException.message)
      .isEqualTo(
        "Resource with type ${TEST_PATIENT_1.resourceType.name} and id $TEST_PATIENT_1_ID has local changes, either sync with server or FORCE_PURGE required",
      )
  }

  @Test
  fun purge_withNoLocalChangeAndForcePurgeFalse_shouldPurgeResource() = runBlocking {
    database.insertRemote(TEST_PATIENT_2)

    assertThat(database.getLocalChanges(ResourceType.Patient, TEST_PATIENT_2_ID)).isEmpty()
    assertResourceEquals(TEST_PATIENT_2, database.select(ResourceType.Patient, TEST_PATIENT_2_ID))

    database.purge(TEST_PATIENT_2.resourceType, TEST_PATIENT_2_ID)

    val resourceNotFoundException =
      assertThrows(ResourceNotFoundException::class.java) {
        runBlocking { database.select(ResourceType.Patient, TEST_PATIENT_2_ID) }
      }
    assertThat(resourceNotFoundException.message)
      .isEqualTo("Resource not found with type ${ResourceType.Patient} and id $TEST_PATIENT_2_ID!")
  }

  @Test
  fun purge_withNoLocalChangeAndForcePurgeTrue_shouldPurgeResource() = runBlocking {
    database.insertRemote(TEST_PATIENT_2)
    assertThat(database.getLocalChanges(ResourceType.Patient, TEST_PATIENT_2_ID)).isEmpty()

    assertResourceEquals(TEST_PATIENT_2, database.select(ResourceType.Patient, TEST_PATIENT_2_ID))

    database.purge(TEST_PATIENT_2.resourceType, TEST_PATIENT_2_ID, true)

    val resourceNotFoundException =
      assertThrows(ResourceNotFoundException::class.java) {
        runBlocking { database.select(ResourceType.Patient, TEST_PATIENT_2_ID) }
      }
    assertThat(resourceNotFoundException.message)
      .isEqualTo("Resource not found with type ${ResourceType.Patient} and id $TEST_PATIENT_2_ID!")
  }

  @Test
  fun purge_resourceNotAvailable_shouldThrowResourceNotFoundException() = runBlocking {
    val resourceNotFoundException =
      assertThrows(ResourceNotFoundException::class.java) {
        runBlocking { database.purge(ResourceType.Patient, TEST_PATIENT_2_ID) }
      }
    assertThat(resourceNotFoundException.message)
      .isEqualTo(
        "Resource not found with type ${TEST_PATIENT_1.resourceType.name} and id $TEST_PATIENT_2_ID!",
      )
  }

  @Test
  fun update_nonExistingResource_shouldNotInsertResource() {
    val resourceNotFoundException =
      assertThrows(ResourceNotFoundException::class.java) {
        runBlocking { database.update(TEST_PATIENT_2) }
      }
    assertThat(resourceNotFoundException.message)
      .isEqualTo(
        "Resource not found with type ${TEST_PATIENT_2.resourceType.name} and id $TEST_PATIENT_2_ID!",
      )
  }

  @Test
  fun select_nonexistentResource_shouldThrowResourceNotFoundException() {
    val resourceNotFoundException =
      assertThrows(ResourceNotFoundException::class.java) {
        runBlocking { database.select(ResourceType.Patient, "nonexistent_patient") }
      }
    assertThat(resourceNotFoundException.message)
      .isEqualTo("Resource not found with type Patient and id nonexistent_patient!")
  }

  @Test
  fun select_shouldReturnResource() = runBlocking {
    assertResourceEquals(TEST_PATIENT_1, database.select(ResourceType.Patient, TEST_PATIENT_1_ID))
  }

  @Test
  fun insert_shouldAddInsertLocalChange() = runBlocking {
    val testPatient2String = services.parser.encodeResourceToString(TEST_PATIENT_2)
    database.insert(TEST_PATIENT_2)
    val resourceLocalChanges =
      database.getAllLocalChanges().filter { it.resourceId.equals(TEST_PATIENT_2_ID) }
    assertThat(resourceLocalChanges.size).isEqualTo(1)
    with(resourceLocalChanges[0]) {
      assertThat(type).isEqualTo(LocalChange.Type.INSERT)
      assertThat(resourceId).isEqualTo(TEST_PATIENT_2_ID)
      assertThat(resourceType).isEqualTo(TEST_PATIENT_2.resourceType.name)
      assertThat(payload).isEqualTo(testPatient2String)
    }
  }

  @Test
  fun update_remoteResourceWithLocalChange_shouldSaveVersionIdAndLastUpdated() = runBlocking {
    val patient =
      Patient().apply {
        id = "remote-patient-1"
        addName(
          HumanName().apply {
            family = "FamilyName"
            addGiven("FirstName")
          },
        )
        meta =
          Meta().apply {
            versionId = "remote-patient-1-version-001"
            lastUpdated = Date()
          }
      }

    database.insertRemote(patient)

    val updatedPatient =
      Patient().apply {
        id = "remote-patient-1"
        addName(
          HumanName().apply {
            family = "UpdatedFamilyName"
            addGiven("UpdatedFirstName")
          },
        )
      }
    database.update(updatedPatient)

    val selectedEntity = database.selectEntity(ResourceType.Patient, "remote-patient-1")
    assertThat(selectedEntity.resourceId).isEqualTo("remote-patient-1")
    assertThat(selectedEntity.versionId).isEqualTo(patient.meta.versionId)
    assertThat(selectedEntity.lastUpdatedRemote).isEqualTo(patient.meta.lastUpdated.toInstant())

    val resourceLocalChange =
      database.getAllLocalChanges().first { it.resourceId == "remote-patient-1" }
    assertThat(resourceLocalChange.resourceId).isEqualTo("remote-patient-1")
    assertThat(resourceLocalChange.versionId).isEqualTo(patient.meta.versionId)
  }

  @Test
  fun delete_shouldAddDeleteLocalChange() = runBlocking {
    database.delete(ResourceType.Patient, TEST_PATIENT_1_ID)
    val resourceLocalChanges =
      database.getAllLocalChanges().filter { it.resourceId == TEST_PATIENT_1_ID }
    assertThat(resourceLocalChanges.size).isEqualTo(2)
    with(resourceLocalChanges[1]) {
      assertThat(type).isEqualTo(LocalChange.Type.DELETE)
      assertThat(resourceId).isEqualTo(TEST_PATIENT_1_ID)
      assertThat(resourceType).isEqualTo(TEST_PATIENT_1.resourceType.name)
      assertThat(payload).isEmpty()
    }
  }

  @Test
  fun delete_nonExistent_shouldNotInsertLocalChange() = runBlocking {
    database.delete(ResourceType.Patient, "nonexistent_patient")
    assertThat(
        database
          .getAllLocalChanges()
          .map { it }
          .none { it.type == LocalChange.Type.DELETE && it.resourceId == "nonexistent_patient" },
      )
      .isTrue()
  }

  @Test
  fun deleteUpdates_shouldDeleteLocalChanges() = runBlocking {
    var patient: Patient = readFromFile(Patient::class.java, "/date_test_patient.json")
    database.insert(patient)
    patient = readFromFile(Patient::class.java, "/update_test_patient_1.json")
    database.update(patient)
    services.parser.encodeResourceToString(patient)
    val localChangeTokenIds =
      database
        .getAllLocalChanges()
        .filter { it.resourceId == patient.logicalId }
        .flatMap { it.token.ids }
    database.deleteUpdates(LocalChangeToken(localChangeTokenIds))
    assertThat(database.getAllLocalChanges().none { it.resourceId.equals(patient.logicalId) })
      .isTrue()
  }

  @Test
  fun insert_remoteResource_shouldNotInsertLocalChange() = runBlocking {
    val patient: Patient = readFromFile(Patient::class.java, "/date_test_patient.json")
    database.insertRemote(patient)
    assertThat(database.getAllLocalChanges().map { it }.none { it.resourceId == patient.logicalId })
      .isTrue()
  }

  @Test
  fun insert_existingRemoteResource_shouldNotChangeResourceEntityUuidOrId() = runBlocking {
    val patient: Patient = readFromFile(Patient::class.java, "/date_test_patient.json")
    database.insertRemote(patient)
    val patientEntityAfterFirstRemoteSync =
      database.selectEntity(ResourceType.Patient, patient.logicalId)
    database.insertRemote(patient)
    val patientEntityAfterSecondRemoteSync =
      database.selectEntity(ResourceType.Patient, patient.logicalId)
    assertThat(patientEntityAfterSecondRemoteSync.resourceUuid)
      .isEqualTo(patientEntityAfterFirstRemoteSync.resourceUuid)
    assertThat(patientEntityAfterSecondRemoteSync.id)
      .isEqualTo(patientEntityAfterFirstRemoteSync.id)
  }

  @Test
  fun insert_remoteResource_shouldSaveVersionIdAndLastUpdated() = runBlocking {
    val patient =
      Patient().apply {
        id = "remote-patient-1"
        meta =
          Meta().apply {
            versionId = "remote-patient-1-version-1"
            lastUpdated = Date()
          }
      }
    database.insertRemote(patient)
    val selectedEntity = database.selectEntity(ResourceType.Patient, "remote-patient-1")
    assertThat(selectedEntity.versionId).isEqualTo("remote-patient-1-version-1")
    assertThat(selectedEntity.lastUpdatedRemote).isEqualTo(patient.meta.lastUpdated.toInstant())
  }

  @Test
  fun insert_remoteResourceWithNoMeta_shouldSaveNullRemoteVersionAndLastUpdated() = runBlocking {
    val patient = Patient().apply { id = "remote-patient-2" }
    database.insertRemote(patient)
    val selectedEntity = database.selectEntity(ResourceType.Patient, "remote-patient-2")
    assertThat(selectedEntity.versionId).isNull()
    assertThat(selectedEntity.lastUpdatedRemote).isNull()
  }

  @Test
  fun insert_localResourceWithNoMeta_shouldSaveNullRemoteVersionAndLastUpdated() = runBlocking {
    val patient = Patient().apply { id = "local-patient-2" }
    database.insert(patient)
    val selectedEntity = database.selectEntity(ResourceType.Patient, "local-patient-2")
    assertThat(selectedEntity.versionId).isNull()
    assertThat(selectedEntity.lastUpdatedRemote).isNull()
  }

  @Test
  fun insert_localResourceWithNoMetaAndSync_shouldSaveRemoteVersionAndLastUpdated() = runBlocking {
    val patient = Patient().apply { id = "remote-patient-3" }
    val remoteMeta =
      Meta().apply {
        versionId = "remote-patient-3-version-001"
        lastUpdated = Date()
      }
    database.insert(patient)
    // Delete the patient created in setup as we only want to upload the patient in this test
    database.deleteUpdates(listOf(TEST_PATIENT_1))
    services.fhirEngine
      .syncUpload(LocalChangesFetchMode.AllChanges) {
        it
          .first { it.resourceId == "remote-patient-3" }
          .let {
            UploadSyncResult.Success(
              it.token,
              listOf(
                Patient().apply {
                  id = it.resourceId
                  meta = remoteMeta
                },
              ),
            )
          }
      }
      .collect()
    val selectedEntity = database.selectEntity(ResourceType.Patient, "remote-patient-3")
    assertThat(selectedEntity.versionId).isEqualTo(remoteMeta.versionId)
    assertThat(selectedEntity.lastUpdatedRemote).isEqualTo(remoteMeta.lastUpdated.toInstant())
  }

  @Test
  fun insertAll_remoteResources_shouldNotInsertAnyLocalChange() = runBlocking {
    val patient: Patient = readFromFile(Patient::class.java, "/date_test_patient.json")
    database.insertRemote(patient, TEST_PATIENT_2)
    assertThat(
        database
          .getAllLocalChanges()
          .map { it }
          .none { it.resourceId in listOf(patient.logicalId, TEST_PATIENT_2_ID) },
      )
      .isTrue()
  }

  @Test
  fun insert_should_remove_old_indexes() = runBlocking {
    val patient =
      Patient().apply {
        id = "local-1"
        addName(
          HumanName().apply {
            addGiven("Jane")
            family = "Doe"
          },
        )
      }

    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient).apply { filter(Patient.GIVEN, { value = "Jane" }) }.getQuery(),
      )
    assertThat(result.size).isEqualTo(1)

    val updatedPatient =
      Patient().apply {
        id = "local-1"
        addName(
          HumanName().apply {
            addGiven("John")
            family = "Doe"
          },
        )
      }

    database.insert(updatedPatient)
    val updatedResult =
      database.search<Patient>(
        Search(ResourceType.Patient).apply { filter(Patient.GIVEN, { value = "Jane" }) }.getQuery(),
      )
    assertThat(updatedResult.size).isEqualTo(0)
  }

  @Test
  fun insertRemote_should_remove_old_indexes() = runBlocking {
    val patient =
      Patient().apply {
        id = "local-1"
        addName(
          HumanName().apply {
            addGiven("Jane")
            family = "Doe"
          },
        )
      }

    database.insertRemote(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient).apply { filter(Patient.GIVEN, { value = "Jane" }) }.getQuery(),
      )
    assertThat(result.size).isEqualTo(1)

    val updatedPatient =
      Patient().apply {
        id = "local-1"
        addName(
          HumanName().apply {
            addGiven("John")
            family = "Doe"
          },
        )
      }

    database.insertRemote(updatedPatient)
    val updatedResult =
      database.search<Patient>(
        Search(ResourceType.Patient).apply { filter(Patient.GIVEN, { value = "Jane" }) }.getQuery(),
      )
    assertThat(updatedResult.size).isEqualTo(0)
  }

  @Test
  fun update_remoteResource_readSquashedChanges_shouldReturnPatch() = runBlocking {
    val patient: Patient = readFromFile(Patient::class.java, "/date_test_patient.json")
    database.insertRemote(patient)
    val updatedPatient = readFromFile(Patient::class.java, "/update_test_patient_1.json")
    val updatePatch = readJsonArrayFromFile("/update_patch_1.json")
    database.update(updatedPatient)
    val resourceLocalChanges =
      database.getAllLocalChanges().filter { it.resourceId == patient.logicalId }
    assertThat(resourceLocalChanges.size).isEqualTo(1)
    with(resourceLocalChanges[0]) {
      assertThat(type).isEqualTo(LocalChange.Type.UPDATE)
      assertThat(resourceId).isEqualTo(patient.logicalId)
      assertThat(resourceType).isEqualTo(patient.resourceType.name)
      assertJsonArrayEqualsIgnoringOrder(JSONArray(payload), updatePatch)
    }
  }

  @Test
  fun update_should_remove_old_indexes() = runBlocking {
    val patient =
      Patient().apply {
        id = "local-1"
        addName(
          HumanName().apply {
            addGiven("Jane")
            family = "Doe"
          },
        )
      }

    database.insertRemote(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient).apply { filter(Patient.GIVEN, { value = "Jane" }) }.getQuery(),
      )
    assertThat(result.size).isEqualTo(1)

    val updatedPatient =
      Patient().apply {
        id = "local-1"
        addName(
          HumanName().apply {
            addGiven("John")
            family = "Doe"
          },
        )
      }

    database.update(updatedPatient)
    val updatedResult =
      database.search<Patient>(
        Search(ResourceType.Patient).apply { filter(Patient.GIVEN, { value = "Jane" }) }.getQuery(),
      )
    assertThat(updatedResult.size).isEqualTo(0)
  }

  @Test
  fun delete_remoteResource_shouldReturnDeleteLocalChange() = runBlocking {
    database.insertRemote(TEST_PATIENT_2)
    database.delete(ResourceType.Patient, TEST_PATIENT_2_ID)
    val resourceLocalChanges =
      database.getAllLocalChanges().map { it }.filter { it.resourceId.equals(TEST_PATIENT_2_ID) }
    assertThat(resourceLocalChanges.size).isEqualTo(1)
    with(resourceLocalChanges[0]) {
      assertThat(type).isEqualTo(LocalChange.Type.DELETE)
      assertThat(resourceId).isEqualTo(TEST_PATIENT_2_ID)
      assertThat(resourceType).isEqualTo(TEST_PATIENT_2.resourceType.name)
      assertThat(versionId).isEqualTo(TEST_PATIENT_2.versionId)
      assertThat(payload).isEmpty()
    }
  }

  @Test
  fun getLocalChangesCount_noLocalChange_returnsZero() = runBlocking {
    database.deleteUpdates(listOf(TEST_PATIENT_1))
    assertThat(database.getLocalChangesCount()).isEqualTo(0)
  }

  @Test
  fun getLocalChangesCount_oneLocalChange_returnsOne() = runBlocking {
    assertThat(database.getLocalChangesCount()).isEqualTo(1)
  }

  @Test
  fun getLocalChangesCount_twoLocalChange_returnsTwo() = runBlocking {
    database.insert(TEST_PATIENT_2)
    assertThat(database.getLocalChangesCount()).isEqualTo(2)
  }

  @Test
  fun search_sortDescending_twoVeryCloseFloatingPointNumbers_orderedCorrectly() = runBlocking {
    val smallerId = "risk_assessment_1"
    val largerId = "risk_assessment_2"
    database.insert(
      riskAssessment(id = smallerId, probability = BigDecimal("0.3")),
      riskAssessment(id = largerId, probability = BigDecimal("0.30000000001")),
    )

    val results =
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply { sort(RiskAssessment.PROBABILITY, Order.DESCENDING) }
          .getQuery(),
      )

    val ids = results.map { it.id }
    assertThat(ids)
      .containsExactly("RiskAssessment/$largerId", "RiskAssessment/$smallerId")
      .inOrder()
  }

  private fun riskAssessment(id: String, probability: BigDecimal) =
    RiskAssessment().apply {
      setId(id)
      prediction =
        listOf(
          RiskAssessment.RiskAssessmentPredictionComponent().apply {
            setProbability(DecimalType(probability))
          },
        )
    }

  @Test
  fun search_string_default() = runBlocking {
    val patient =
      Patient().apply {
        id = "test_1"
        addName(HumanName().addGiven("Evelyn"))
      }
    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient).apply { filter(Patient.GIVEN, { value = "eve" }) }.getQuery(),
      )

    assertThat(result.single().id).isEqualTo("Patient/${patient.id}")
  }

  @Test
  fun search_string_default_no_match() = runBlocking {
    val patient =
      Patient().apply {
        id = "test_1"
        addName(HumanName().addGiven("Severine"))
      }
    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient).apply { filter(Patient.GIVEN, { value = "eve" }) }.getQuery(),
      )

    assertThat(result).isEmpty()
  }

  @Test
  fun search_string_exact() = runBlocking {
    val patient =
      Patient().apply {
        id = "test_1"
        addName(HumanName().addGiven("Eve"))
      }
    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              Patient.GIVEN,
              {
                value = "Eve"
                modifier = StringFilterModifier.MATCHES_EXACTLY
              },
            )
          }
          .getQuery(),
      )

    assertThat(result.single().id).isEqualTo("Patient/${patient.id}")
  }

  @Test
  fun search_string_exact_no_match() = runBlocking {
    val patient =
      Patient().apply {
        id = "test_1"
        addName(HumanName().addGiven("EVE"))
      }
    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              Patient.GIVEN,
              {
                value = "Eve"
                modifier = StringFilterModifier.MATCHES_EXACTLY
              },
            )
          }
          .getQuery(),
      )

    assertThat(result).isEmpty()
  }

  @Test
  fun search_string_contains() = runBlocking {
    val patient =
      Patient().apply {
        id = "test_1"
        addName(HumanName().addGiven("Severine"))
      }

    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              Patient.GIVEN,
              {
                value = "Eve"
                modifier = StringFilterModifier.CONTAINS
              },
            )
          }
          .getQuery(),
      )

    assertThat(result.single().id).isEqualTo("Patient/${patient.id}")
  }

  @Test
  fun search_string_contains_no_match() = runBlocking {
    val patient =
      Patient().apply {
        id = "test_1"
        addName(HumanName().addGiven("John"))
      }
    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              Patient.GIVEN,
              {
                value = "eve"
                modifier = StringFilterModifier.CONTAINS
              },
            )
          }
          .getQuery(),
      )

    assertThat(result).isEmpty()
  }

  @Test
  fun search_number_equal() = runBlocking {
    val riskAssessment =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.5)),
        )
      }

    database.insert(riskAssessment)
    val result =
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(
              RiskAssessment.PROBABILITY,
              {
                prefix = ParamPrefixEnum.EQUAL
                value = BigDecimal("100")
              },
            )
          }
          .getQuery(),
      )

    assertThat(result.single().id).isEqualTo("RiskAssessment/${riskAssessment.id}")
  }

  @Test
  fun search_number_equal_no_match() = runBlocking {
    val riskAssessment =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(100.5)),
        )
      }

    database.insert(riskAssessment)
    val result =
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(
              RiskAssessment.PROBABILITY,
              {
                prefix = ParamPrefixEnum.EQUAL
                value = BigDecimal("100")
              },
            )
          }
          .getQuery(),
      )

    assertThat(result).isEmpty()
  }

  @Test
  fun search_number_notEqual() = runBlocking {
    val riskAssessment =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.0)),
        )
      }

    database.insert(riskAssessment)
    val result =
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(
              RiskAssessment.PROBABILITY,
              {
                prefix = ParamPrefixEnum.NOT_EQUAL
                value = BigDecimal("100")
              },
            )
          }
          .getQuery(),
      )
    assertThat(result.single().id).isEqualTo("RiskAssessment/${riskAssessment.id}")
  }

  @Test
  fun search_number_notEqual_no_match() = runBlocking {
    val riskAssessment =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.5)),
        )
      }

    database.insert(riskAssessment)
    val result =
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(
              RiskAssessment.PROBABILITY,
              {
                prefix = ParamPrefixEnum.NOT_EQUAL
                value = BigDecimal("100")
              },
            )
          }
          .getQuery(),
      )

    assertThat(result).isEmpty()
  }

  @Test
  fun search_number_greater() = runBlocking {
    val riskAssessment =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(100)),
        )
      }

    database.insert(riskAssessment)
    val result =
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(
              RiskAssessment.PROBABILITY,
              {
                prefix = ParamPrefixEnum.GREATERTHAN
                value = BigDecimal("99.5")
              },
            )
          }
          .getQuery(),
      )

    assertThat(result.single().id).isEqualTo("RiskAssessment/${riskAssessment.id}")
  }

  @Test
  fun search_number_greater_no_match() = runBlocking {
    val riskAssessment =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.5)),
        )
      }

    database.insert(riskAssessment)
    val result =
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(
              RiskAssessment.PROBABILITY,
              {
                prefix = ParamPrefixEnum.GREATERTHAN
                value = BigDecimal("99.5")
              },
            )
          }
          .getQuery(),
      )

    assertThat(result).isEmpty()
  }

  @Test
  fun search_number_greaterThanEqual() = runBlocking {
    val riskAssessment =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.5)),
        )
      }

    database.insert(riskAssessment)
    val result =
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(
              RiskAssessment.PROBABILITY,
              {
                prefix = ParamPrefixEnum.GREATERTHAN_OR_EQUALS
                value = BigDecimal("99.5")
              },
            )
          }
          .getQuery(),
      )

    assertThat(result.single().id).isEqualTo("RiskAssessment/${riskAssessment.id}")
  }

  @Test
  fun search_number_greaterThanEqual_no_match() = runBlocking {
    val riskAssessment =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.0)),
        )
      }

    database.insert(riskAssessment)
    val result =
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(
              RiskAssessment.PROBABILITY,
              {
                prefix = ParamPrefixEnum.GREATERTHAN_OR_EQUALS
                value = BigDecimal("99.5")
              },
            )
          }
          .getQuery(),
      )

    assertThat(result).isEmpty()
  }

  @Test
  fun search_number_less() = runBlocking {
    val riskAssessment =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.0)),
        )
      }

    database.insert(riskAssessment)
    val result =
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(
              RiskAssessment.PROBABILITY,
              {
                prefix = ParamPrefixEnum.LESSTHAN
                value = BigDecimal("99.5")
              },
            )
          }
          .getQuery(),
      )

    assertThat(result.single().id).isEqualTo("RiskAssessment/${riskAssessment.id}")
  }

  @Test
  fun search_number_less_no_match() = runBlocking {
    val riskAssessment =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.5)),
        )
      }

    database.insert(riskAssessment)
    val result =
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(
              RiskAssessment.PROBABILITY,
              {
                prefix = ParamPrefixEnum.LESSTHAN
                value = BigDecimal("99.5")
              },
            )
          }
          .getQuery(),
      )

    assertThat(result).isEmpty()
  }

  @Test
  fun search_number_lessThanEquals() = runBlocking {
    val riskAssessment =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.5)),
        )
      }

    database.insert(riskAssessment)
    val result =
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(
              RiskAssessment.PROBABILITY,
              {
                prefix = ParamPrefixEnum.LESSTHAN_OR_EQUALS
                value = BigDecimal("99.5")
              },
            )
          }
          .getQuery(),
      )
    assertThat(result.single().id).isEqualTo("RiskAssessment/${riskAssessment.id}")
  }

  @Test
  fun search_number_lessThanEquals_no_match() = runBlocking {
    val riskAssessment =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(100)),
        )
      }

    database.insert(riskAssessment)
    val result =
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(
              RiskAssessment.PROBABILITY,
              {
                prefix = ParamPrefixEnum.LESSTHAN_OR_EQUALS
                value = BigDecimal("99.5")
              },
            )
          }
          .getQuery(),
      )

    assertThat(result).isEmpty()
  }

  @Test
  fun search_decimal_endsBefore() = runBlocking {
    val riskAssessment =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.0)),
        )
      }

    database.insert(riskAssessment)
    val result =
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(
              RiskAssessment.PROBABILITY,
              {
                prefix = ParamPrefixEnum.ENDS_BEFORE
                value = BigDecimal("99.5")
              },
            )
          }
          .getQuery(),
      )

    assertThat(result.single().id).isEqualTo("RiskAssessment/${riskAssessment.id}")
  }

  @Test
  fun search_decimal_endsBefore_no_match() = runBlocking {
    val riskAssessment =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.5)),
        )
      }

    database.insert(riskAssessment)
    val result =
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(
              RiskAssessment.PROBABILITY,
              {
                prefix = ParamPrefixEnum.ENDS_BEFORE
                value = BigDecimal("99.5")
              },
            )
          }
          .getQuery(),
      )

    assertThat(result).isEmpty()
  }

  @Test
  fun search_decimal_startAfter() = runBlocking {
    val riskAssessment =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(100)),
        )
      }

    database.insert(riskAssessment)
    val result =
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(
              RiskAssessment.PROBABILITY,
              {
                prefix = ParamPrefixEnum.STARTS_AFTER
                value = BigDecimal("99.5")
              },
            )
          }
          .getQuery(),
      )

    assertThat(result.single().id).isEqualTo("RiskAssessment/${riskAssessment.id}")
  }

  @Test
  fun search_decimal_startAfter_no_match() = runBlocking {
    val riskAssessment =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(99.5)),
        )
      }

    database.insert(riskAssessment)
    val result =
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(
              RiskAssessment.PROBABILITY,
              {
                prefix = ParamPrefixEnum.STARTS_AFTER
                value = BigDecimal("99.5")
              },
            )
          }
          .getQuery(),
      )

    assertThat(result).isEmpty()
  }

  @Test
  fun search_number_approximate() = runBlocking {
    val riskAssessment =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(93)),
        )
      }

    database.insert(riskAssessment)
    val result =
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(
              RiskAssessment.PROBABILITY,
              {
                prefix = ParamPrefixEnum.APPROXIMATE
                value = BigDecimal("100")
              },
            )
          }
          .getQuery(),
      )

    assertThat(result.single().id).isEqualTo("RiskAssessment/${riskAssessment.id}")
  }

  @Test
  fun search_number_approximate_no_match() = runBlocking {
    val riskAssessment =
      RiskAssessment().apply {
        id = "1"
        addPrediction(
          RiskAssessment.RiskAssessmentPredictionComponent().setProbability(DecimalType(120)),
        )
      }

    database.insert(riskAssessment)
    val result =
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply {
            filter(
              RiskAssessment.PROBABILITY,
              {
                prefix = ParamPrefixEnum.APPROXIMATE
                value = BigDecimal("100")
              },
            )
          }
          .getQuery(),
      )

    assertThat(result).isEmpty()
  }

  @Test
  fun search_dateTime_approximate() = runBlocking {
    DateProvider(Instant.ofEpochMilli(mockEpochTimeStamp))
    val patient =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2013-03-16T10:00:00-05:30")
      }
    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              Patient.DEATH_DATE,
              {
                value = of(DateTimeType("2013-03-14"))
                prefix = ParamPrefixEnum.APPROXIMATE
              },
            )
          }
          .getQuery(),
      )
    assertThat(result.single().id).isEqualTo("Patient/1")
  }

  @Test
  fun search_dateTime_approximate_no_match() = runBlocking {
    DateProvider(Instant.ofEpochMilli(mockEpochTimeStamp))
    val patient =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2013-03-16T10:00:00-05:30")
      }
    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              Patient.DEATH_DATE,
              {
                value = of(DateTimeType("2020-03-14"))
                prefix = ParamPrefixEnum.APPROXIMATE
              },
            )
          }
          .getQuery(),
      )
    assertThat(result).isEmpty()
  }

  @Test
  fun search_date_approximate() = runBlocking {
    DateProvider(Instant.ofEpochMilli(mockEpochTimeStamp))
    val patient =
      Patient().apply {
        id = "1"
        birthDateElement = DateType("2013-03-16")
      }
    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              Patient.BIRTHDATE,
              {
                value = of(DateType("2013-03-14"))
                prefix = ParamPrefixEnum.APPROXIMATE
              },
            )
          }
          .getQuery(),
      )
    assertThat(result.single().id).isEqualTo("Patient/1")
  }

  @Test
  fun search_date_approximate_no_match() = runBlocking {
    DateProvider(Instant.ofEpochMilli(mockEpochTimeStamp))
    val patient =
      Patient().apply {
        id = "1"
        birthDateElement = DateType("2013-03-16")
      }
    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              Patient.BIRTHDATE,
              {
                value = of(DateType("2020-03-14"))
                prefix = ParamPrefixEnum.APPROXIMATE
              },
            )
          }
          .getQuery(),
      )
    assertThat(result).isEmpty()
  }

  @Test
  fun search_date_starts_after() = runBlocking {
    val patient =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2013-03-23T10:00:00-05:30")
      }
    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              Patient.DEATH_DATE,
              {
                value = of(DateTimeType("2013-03-14"))
                prefix = ParamPrefixEnum.STARTS_AFTER
              },
            )
          }
          .getQuery(),
      )
    assertThat(result.single().id).isEqualTo("Patient/1")
  }

  @Test
  fun search_date_starts_after_noMatch() = runBlocking {
    val patient =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2013-03-13T10:00:00-05:30")
      }
    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              Patient.DEATH_DATE,
              {
                value = of(DateTimeType("2013-03-14"))
                prefix = ParamPrefixEnum.STARTS_AFTER
              },
            )
          }
          .getQuery(),
      )
    assertThat(result).isEmpty()
  }

  @Test
  fun search_date_ends_before() = runBlocking {
    val patient =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2013-03-13T01:00:00")
      }
    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              Patient.DEATH_DATE,
              {
                value = of(DateTimeType("2013-03-14"))
                prefix = ParamPrefixEnum.ENDS_BEFORE
              },
            )
          }
          .getQuery(),
      )
    assertThat(result.single().id).isEqualTo("Patient/1")
  }

  @Test
  fun search_date_ends_before_noMatch() = runBlocking {
    val patient =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2014-03-13T01:00:00")
      }
    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              Patient.DEATH_DATE,
              {
                value = of(DateTimeType("2013-03-14"))
                prefix = ParamPrefixEnum.ENDS_BEFORE
              },
            )
          }
          .getQuery(),
      )
    assertThat(result).isEmpty()
  }

  @Test
  fun search_date_not_equals() = runBlocking {
    val patient =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2013-03-13T10:00:00-05:30")
      }
    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              Patient.DEATH_DATE,
              {
                value = of(DateTimeType("2013-03-14"))
                prefix = ParamPrefixEnum.NOT_EQUAL
              },
            )
          }
          .getQuery(),
      )
    assertThat(result.single().id).isEqualTo("Patient/1")
  }

  @Test
  fun search_date_not_equals_noMatch() = runBlocking {
    val patient =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2013-03-14T10:00:00-05:30")
      }
    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              Patient.DEATH_DATE,
              {
                value = of(DateTimeType("2013-03-14"))
                prefix = ParamPrefixEnum.NOT_EQUAL
              },
            )
          }
          .getQuery(),
      )
    assertThat(result).isEmpty()
  }

  @Test
  fun search_date_equals() = runBlocking {
    val patient =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2013-03-14T10:00:00-05:30")
      }
    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              Patient.DEATH_DATE,
              {
                value = of(DateTimeType("2013-03-14"))
                prefix = ParamPrefixEnum.EQUAL
              },
            )
          }
          .getQuery(),
      )
    assertThat(result.single().id).isEqualTo("Patient/1")
  }

  @Test
  fun search_date_equals_noMatch() = runBlocking {
    val patient =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2013-03-13T10:00:00-05:30")
      }
    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              Patient.DEATH_DATE,
              {
                value = of(DateTimeType("2013-03-14"))
                prefix = ParamPrefixEnum.EQUAL
              },
            )
          }
          .getQuery(),
      )
    assertThat(result).isEmpty()
  }

  @Test
  fun search_date_greater() = runBlocking {
    val patient =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2013-03-15")
      }
    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              Patient.DEATH_DATE,
              {
                value = of(DateTimeType("2013-03-14"))
                prefix = ParamPrefixEnum.GREATERTHAN
              },
            )
          }
          .getQuery(),
      )
    assertThat(result.single().id).isEqualTo("Patient/1")
  }

  @Test
  fun search_date_greater_noMatch() = runBlocking {
    val patient =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2013-03-14T10:00:00-05:30")
      }
    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              Patient.DEATH_DATE,
              {
                value = of(DateTimeType("2013-03-14"))
                prefix = ParamPrefixEnum.GREATERTHAN
              },
            )
          }
          .getQuery(),
      )
    assertThat(result).isEmpty()
  }

  @Test
  fun search_date_greater_or_equal() = runBlocking {
    val patient =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2013-03-14T10:00:00-05:30")
      }
    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              Patient.DEATH_DATE,
              {
                value = of(DateTimeType("2013-03-14"))
                prefix = ParamPrefixEnum.GREATERTHAN_OR_EQUALS
              },
            )
          }
          .getQuery(),
      )
    assertThat(result.single().id).isEqualTo("Patient/1")
  }

  @Test
  fun search_date_greater_or_equal_noMatch() = runBlocking {
    val patient =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2013-03-13T10:00:00-05:30")
      }
    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              Patient.DEATH_DATE,
              {
                value = of(DateTimeType("2013-03-14"))
                prefix = ParamPrefixEnum.GREATERTHAN_OR_EQUALS
              },
            )
          }
          .getQuery(),
      )
    assertThat(result).isEmpty()
  }

  @Test
  fun search_date_less() = runBlocking {
    val patient =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2013-03-13")
      }
    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              Patient.DEATH_DATE,
              {
                value = of(DateTimeType("2013-03-14"))
                prefix = ParamPrefixEnum.LESSTHAN
              },
            )
          }
          .getQuery(),
      )
    assertThat(result.single().id).isEqualTo("Patient/1")
  }

  @Test
  fun search_date_less_noMatch() = runBlocking {
    val patient =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2013-03-14T10:00:00-05:30")
      }
    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              Patient.DEATH_DATE,
              {
                value = of(DateTimeType("2013-03-14"))
                prefix = ParamPrefixEnum.LESSTHAN
              },
            )
          }
          .getQuery(),
      )
    assertThat(result).isEmpty()
  }

  @Test
  fun search_date_less_or_equal() = runBlocking {
    val patient =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2013-03-14T10:00:00-05:30")
      }
    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              Patient.DEATH_DATE,
              {
                value = of(DateTimeType("2013-03-14"))
                prefix = ParamPrefixEnum.LESSTHAN_OR_EQUALS
              },
            )
          }
          .getQuery(),
      )
    assertThat(result.single().id).isEqualTo("Patient/1")
  }

  @Test
  fun search_date_less_or_equal_noMatch() = runBlocking {
    val patient =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2013-03-14T23:00:00-05:30")
      }
    database.insert(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              Patient.DEATH_DATE,
              {
                value = of(DateTimeType("2013-03-14T00:00:00-00:00"))
                prefix = ParamPrefixEnum.LESSTHAN_OR_EQUALS
              },
            )
          }
          .getQuery(),
      )
    assertThat(result).isEmpty()
  }

  @Test
  fun search_quantity_equal() = runBlocking {
    val observation =
      Observation().apply {
        id = "1"
        value =
          Quantity().apply {
            value = BigDecimal("5.403")
            system = "http://unitsofmeasure.org"
            code = "g"
          }
      }
    database.insert(observation)
    val result =
      database.search<Observation>(
        Search(ResourceType.Observation)
          .apply {
            filter(
              Observation.VALUE_QUANTITY,
              {
                prefix = ParamPrefixEnum.EQUAL
                value = BigDecimal("5.403")
                system = "http://unitsofmeasure.org"
                unit = "g"
              },
            )
          }
          .getQuery(),
      )
    assertThat(result.single().id).isEqualTo("Observation/1")
  }

  @Test
  fun search_quantity_not_equal() = runBlocking {
    val observation =
      Observation().apply {
        id = "1"
        value =
          Quantity().apply {
            value = BigDecimal("5.403")
            system = "http://unitsofmeasure.org"
            code = "g"
          }
      }
    database.insert(observation)
    val result =
      database.search<Observation>(
        Search(ResourceType.Observation)
          .apply {
            filter(
              Observation.VALUE_QUANTITY,
              {
                prefix = ParamPrefixEnum.NOT_EQUAL
                value = BigDecimal("5.403")
                system = "http://unitsofmeasure.org"
                unit = "g"
              },
            )
          }
          .getQuery(),
      )
    assertThat(result).isEmpty()
  }

  @Test
  fun search_quantity_less() = runBlocking {
    val observation =
      Observation().apply {
        id = "1"
        value =
          Quantity().apply {
            value = BigDecimal("5.3")
            system = "http://unitsofmeasure.org"
            code = "g"
          }
      }
    database.insert(observation)
    val result =
      database.search<Observation>(
        Search(ResourceType.Observation)
          .apply {
            filter(
              Observation.VALUE_QUANTITY,
              {
                prefix = ParamPrefixEnum.LESSTHAN
                value = BigDecimal("5.403")
                system = "http://unitsofmeasure.org"
                unit = "g"
              },
            )
          }
          .getQuery(),
      )
    assertThat(result.single().id).isEqualTo("Observation/1")
  }

  @Test
  fun search_quantity_less_no_match() = runBlocking {
    val observation =
      Observation().apply {
        id = "1"
        value =
          Quantity().apply {
            value = BigDecimal("5.4035")
            system = "http://unitsofmeasure.org"
            code = "g"
          }
      }
    database.insert(observation)
    val result =
      database.search<Observation>(
        Search(ResourceType.Observation)
          .apply {
            filter(
              Observation.VALUE_QUANTITY,
              {
                prefix = ParamPrefixEnum.LESSTHAN
                value = BigDecimal("5.403")
                system = "http://unitsofmeasure.org"
                unit = "g"
              },
            )
          }
          .getQuery(),
      )
    assertThat(result).isEmpty()
  }

  @Test
  fun search_quantity_greater() = runBlocking {
    val observation =
      Observation().apply {
        id = "1"
        value =
          Quantity().apply {
            value = BigDecimal("5.5")
            system = "http://unitsofmeasure.org"
            code = "g"
          }
      }
    database.insert(observation)
    val result =
      database.search<Observation>(
        Search(ResourceType.Observation)
          .apply {
            filter(
              Observation.VALUE_QUANTITY,
              {
                prefix = ParamPrefixEnum.GREATERTHAN
                value = BigDecimal("5.403")
                system = "http://unitsofmeasure.org"
                unit = "g"
              },
            )
          }
          .getQuery(),
      )
    assertThat(result.single().id).isEqualTo("Observation/1")
  }

  @Test
  fun search_quantity_greater_no_match() = runBlocking {
    val observation =
      Observation().apply {
        id = "1"
        value =
          Quantity().apply {
            value = BigDecimal("5.3")
            system = "http://unitsofmeasure.org"
            code = "g"
          }
      }
    database.insert(observation)
    val result =
      database.search<Observation>(
        Search(ResourceType.Observation)
          .apply {
            filter(
              Observation.VALUE_QUANTITY,
              {
                prefix = ParamPrefixEnum.GREATERTHAN
                value = BigDecimal("5.403")
                system = "http://unitsofmeasure.org"
                unit = "g"
              },
            )
          }
          .getQuery(),
      )
    assertThat(result).isEmpty()
  }

  @Test
  fun search_quantity_less_or_equal() = runBlocking {
    val observation =
      Observation().apply {
        id = "1"
        value =
          Quantity().apply {
            value = BigDecimal("5.3")
            system = "http://unitsofmeasure.org"
            code = "g"
          }
      }
    database.insert(observation)
    val result =
      database.search<Observation>(
        Search(ResourceType.Observation)
          .apply {
            filter(
              Observation.VALUE_QUANTITY,
              {
                prefix = ParamPrefixEnum.LESSTHAN_OR_EQUALS
                value = BigDecimal("5.403")
                system = "http://unitsofmeasure.org"
                unit = "g"
              },
            )
          }
          .getQuery(),
      )
    assertThat(result.single().id).isEqualTo("Observation/1")
  }

  @Test
  fun search_quantity_less_or_equal_no_match() = runBlocking {
    val observation =
      Observation().apply {
        id = "1"
        value =
          Quantity().apply {
            value = BigDecimal("5.5")
            system = "http://unitsofmeasure.org"
            code = "g"
          }
      }
    database.insert(observation)
    val result =
      database.search<Observation>(
        Search(ResourceType.Observation)
          .apply {
            filter(
              Observation.VALUE_QUANTITY,
              {
                prefix = ParamPrefixEnum.LESSTHAN_OR_EQUALS
                value = BigDecimal("5.403")
                system = "http://unitsofmeasure.org"
                unit = "g"
              },
            )
          }
          .getQuery(),
      )
    assertThat(result).isEmpty()
  }

  @Test
  fun search_quantity_greater_or_equal() = runBlocking {
    val observation =
      Observation().apply {
        id = "1"
        value =
          Quantity().apply {
            value = BigDecimal("5.5")
            system = "http://unitsofmeasure.org"
            code = "g"
          }
      }
    database.insert(observation)
    val result =
      database.search<Observation>(
        Search(ResourceType.Observation)
          .apply {
            filter(
              Observation.VALUE_QUANTITY,
              {
                prefix = ParamPrefixEnum.GREATERTHAN_OR_EQUALS
                value = BigDecimal("5.403")
                system = "http://unitsofmeasure.org"
                unit = "g"
              },
            )
          }
          .getQuery(),
      )
    assertThat(result.single().id).isEqualTo("Observation/1")
  }

  @Test
  fun search_quantity_greater_or_equal_no_match() = runBlocking {
    val observation =
      Observation().apply {
        id = "1"
        value =
          Quantity().apply {
            value = BigDecimal("5.3")
            system = "http://unitsofmeasure.org"
            code = "g"
          }
      }
    database.insert(observation)
    val result =
      database.search<Observation>(
        Search(ResourceType.Observation)
          .apply {
            filter(
              Observation.VALUE_QUANTITY,
              {
                prefix = ParamPrefixEnum.GREATERTHAN_OR_EQUALS
                value = BigDecimal("5.403")
                system = "http://unitsofmeasure.org"
                unit = "g"
              },
            )
          }
          .getQuery(),
      )
    assertThat(result).isEmpty()
  }

  @Test
  fun search_quantity_starts_after() = runBlocking {
    val observation =
      Observation().apply {
        id = "1"
        value =
          Quantity().apply {
            value = BigDecimal("5.5")
            system = "http://unitsofmeasure.org"
            code = "g"
          }
      }
    database.insert(observation)
    val result =
      database.search<Observation>(
        Search(ResourceType.Observation)
          .apply {
            filter(
              Observation.VALUE_QUANTITY,
              {
                prefix = ParamPrefixEnum.STARTS_AFTER
                value = BigDecimal("5.403")
                system = "http://unitsofmeasure.org"
                unit = "g"
              },
            )
          }
          .getQuery(),
      )
    assertThat(result.single().id).isEqualTo("Observation/1")
  }

  @Test
  fun search_quantity_starts_after_no_match() = runBlocking {
    val observation =
      Observation().apply {
        id = "1"
        value =
          Quantity().apply {
            value = BigDecimal("5.3")
            system = "http://unitsofmeasure.org"
            code = "g"
          }
      }
    database.insert(observation)
    val result =
      database.search<Observation>(
        Search(ResourceType.Observation)
          .apply {
            filter(
              Observation.VALUE_QUANTITY,
              {
                prefix = ParamPrefixEnum.STARTS_AFTER
                value = BigDecimal("5.403")
                system = "http://unitsofmeasure.org"
                unit = "g"
              },
            )
          }
          .getQuery(),
      )
    assertThat(result).isEmpty()
  }

  @Test
  fun search_quantity_ends_before() = runBlocking {
    val observation =
      Observation().apply {
        id = "1"
        value =
          Quantity().apply {
            value = BigDecimal("5.3")
            system = "http://unitsofmeasure.org"
            code = "g"
          }
      }
    database.insert(observation)
    val result =
      database.search<Observation>(
        Search(ResourceType.Observation)
          .apply {
            filter(
              Observation.VALUE_QUANTITY,
              {
                prefix = ParamPrefixEnum.ENDS_BEFORE
                value = BigDecimal("5.403")
                system = "http://unitsofmeasure.org"
                unit = "g"
              },
            )
          }
          .getQuery(),
      )
    assertThat(result.single().id).isEqualTo("Observation/1")
  }

  @Test
  fun search_quantity_ends_before_no_match() = runBlocking {
    val observation =
      Observation().apply {
        id = "1"
        value =
          Quantity().apply {
            value = BigDecimal("5.5")
            system = "http://unitsofmeasure.org"
            code = "g"
          }
      }
    database.insert(observation)
    val result =
      database.search<Observation>(
        Search(ResourceType.Observation)
          .apply {
            filter(
              Observation.VALUE_QUANTITY,
              {
                prefix = ParamPrefixEnum.ENDS_BEFORE
                value = BigDecimal("5.403")
                system = "http://unitsofmeasure.org"
                unit = "g"
              },
            )
          }
          .getQuery(),
      )
    assertThat(result).isEmpty()
  }

  @Test
  fun search_quantity_canonical() = runBlocking {
    val observation =
      Observation().apply {
        id = "1"
        value =
          Quantity().apply {
            value = BigDecimal("5.403")
            system = "http://unitsofmeasure.org"
            code = "g"
          }
      }
    database.insert(observation)
    val result =
      database.search<Observation>(
        Search(ResourceType.Observation)
          .apply {
            filter(
              Observation.VALUE_QUANTITY,
              {
                prefix = ParamPrefixEnum.EQUAL
                value = BigDecimal("5403")
                system = "http://unitsofmeasure.org"
                unit = "mg"
              },
            )
          }
          .getQuery(),
      )
    assertThat(result.single().id).isEqualTo("Observation/1")
  }

  @Test
  fun search_nameGivenDuplicate_deduplicatePatient() = runBlocking {
    val patient: Patient = readFromFile(Patient::class.java, "/patient_name_given_duplicate.json")
    database.insertRemote(patient)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            sort(Patient.GIVEN, Order.ASCENDING)
            count = 100
            from = 0
          }
          .getQuery(),
      )
    assertThat(result.filter { it.id == patient.id }).hasSize(1)
  }

  @Test
  fun search_patient_has_taken_influenza_vaccine_in_India() = runBlocking {
    val patient =
      Patient().apply {
        gender = Enumerations.AdministrativeGender.MALE
        id = "100"
        addAddress(Address().apply { country = "IN" })
      }
    val immunization =
      Immunization().apply {
        this.patient = Reference("Patient/${patient.logicalId}")
        vaccineCode =
          CodeableConcept(
            Coding(
              "http://hl7.org/fhir/sid/cvx",
              "140",
              "Influenza, seasonal, injectable, preservative free",
            ),
          )
        status = Immunization.ImmunizationStatus.COMPLETED
      }
    database.insert(patient, TEST_PATIENT_1, immunization)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            has<Immunization>(Immunization.PATIENT) {
              filter(
                Immunization.VACCINE_CODE,
                {
                  value =
                    of(
                      Coding(
                        "http://hl7.org/fhir/sid/cvx",
                        "140",
                        "Influenza, seasonal, injectable, preservative free",
                      ),
                    )
                },
              )

              // Follow Immunization.ImmunizationStatus
              filter(
                Immunization.STATUS,
                {
                  value = of(Coding("http://hl7.org/fhir/event-status", "completed", "Body Weight"))
                },
              )
            }

            filter(
              Patient.ADDRESS_COUNTRY,
              {
                modifier = StringFilterModifier.MATCHES_EXACTLY
                value = "IN"
              },
            )
          }
          .getQuery(),
      )
    assertThat(result.map { it.logicalId }).containsExactly("100").inOrder()
  }

  @Test
  fun search_patient_return_single_patient_who_has_diabetic_careplan() = runBlocking {
    val patient =
      Patient().apply {
        gender = Enumerations.AdministrativeGender.MALE
        id = "100"
      }
    // This careplan has 2 patient references. One as subject and other as a performer.
    // The search should only find the subject Patient.
    val carePlan =
      CarePlan().apply {
        subject = Reference("Patient/${patient.logicalId}")
        activityFirstRep.detail.performer.add(Reference("Patient/${TEST_PATIENT_1.logicalId}"))
        category =
          listOf(
            CodeableConcept(
              Coding("http://snomed.info/sct", "698360004", "Diabetes self management plan"),
            ),
          )
      }
    database.insert(patient, TEST_PATIENT_1, carePlan)
    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            has<CarePlan>(CarePlan.SUBJECT) {
              filter(
                CarePlan.CATEGORY,
                {
                  value =
                    of(
                      Coding(
                        "http://snomed.info/sct",
                        "698360004",
                        "Diabetes self management plan",
                      ),
                    )
                },
              )
            }
          }
          .getQuery(),
      )
    assertThat(result.map { it.logicalId }).containsExactly("100").inOrder()
  }

  @Test
  fun search_sortDescending_Date(): Unit = runBlocking {
    database.insert(
      Patient().apply {
        id = "older-patient"
        birthDateElement = DateType("2020-12-12")
      },
    )

    database.insert(
      Patient().apply {
        id = "younger-patient"
        birthDateElement = DateType("2020-12-13")
      },
    )

    assertThat(
        database
          .search<Patient>(
            Search(ResourceType.Patient)
              .apply { sort(Patient.BIRTHDATE, Order.DESCENDING) }
              .getQuery(),
          )
          .map { it.id },
      )
      .containsExactly("Patient/younger-patient", "Patient/older-patient", "Patient/test_patient_1")
  }

  @Test
  fun search_sortAscending_Date(): Unit = runBlocking {
    database.insert(
      Patient().apply {
        id = "older-patient"
        birthDateElement = DateType("2020-12-12")
      },
    )

    database.insert(
      Patient().apply {
        id = "younger-patient"
        birthDateElement = DateType("2020-12-13")
      },
    )

    assertThat(
        database
          .search<Patient>(
            Search(ResourceType.Patient)
              .apply { sort(Patient.BIRTHDATE, Order.ASCENDING) }
              .getQuery(),
          )
          .map { it.id },
      )
      .containsExactly("Patient/test_patient_1", "Patient/older-patient", "Patient/younger-patient")
  }

  @Test
  fun search_filter_param_values_disjunction_covid_immunization_records() = runBlocking {
    val resources =
      listOf(
        Immunization().apply {
          id = "immunization-1"
          vaccineCode =
            CodeableConcept(
              Coding(
                "http://id.who.int/icd11/mms",
                "XM1NL1",
                "COVID-19 vaccine, inactivated virus",
              ),
            )
          status = Immunization.ImmunizationStatus.COMPLETED
        },
        Immunization().apply {
          id = "immunization-2"
          vaccineCode =
            CodeableConcept(
              Coding(
                "http://id.who.int/icd11/mms",
                "XM5DF6",
                "COVID-19 vaccine, live attenuated virus",
              ),
            )
          status = Immunization.ImmunizationStatus.COMPLETED
        },
        Immunization().apply {
          id = "immunization-3"
          vaccineCode =
            CodeableConcept(
              Coding("http://id.who.int/icd11/mms", "XM6AT1", "COVID-19 vaccine, DNA based"),
            )
          status = Immunization.ImmunizationStatus.COMPLETED
        },
        Immunization().apply {
          id = "immunization-4"
          vaccineCode =
            CodeableConcept(
              Coding(
                "http://hl7.org/fhir/sid/cvx",
                "140",
                "Influenza, seasonal, injectable, preservative free",
              ),
            )
          status = Immunization.ImmunizationStatus.COMPLETED
        },
      )

    database.insert(*resources.toTypedArray())

    val result =
      database.search<Immunization>(
        Search(ResourceType.Immunization)
          .apply {
            filter(
              Immunization.VACCINE_CODE,
              {
                value =
                  of(
                    Coding(
                      "http://id.who.int/icd11/mms",
                      "XM1NL1",
                      "COVID-19 vaccine, inactivated virus",
                    ),
                  )
              },
              {
                value =
                  of(
                    Coding(
                      "http://id.who.int/icd11/mms",
                      "XM5DF6",
                      "COVID-19 vaccine, inactivated virus",
                    ),
                  )
              },
              operation = Operation.OR,
            )
          }
          .getQuery(),
      )

    assertThat(result.map { it.vaccineCode.codingFirstRep.code })
      .containsExactly("XM1NL1", "XM5DF6")
      .inOrder()
  }

  @Test
  fun test_search_multiple_param_disjunction_covid_immunization_records() = runBlocking {
    val resources =
      listOf(
        Immunization().apply {
          id = "immunization-1"
          vaccineCode =
            CodeableConcept(
              Coding(
                "http://id.who.int/icd11/mms",
                "XM1NL1",
                "COVID-19 vaccine, inactivated virus",
              ),
            )
          status = Immunization.ImmunizationStatus.COMPLETED
        },
        Immunization().apply {
          id = "immunization-2"
          vaccineCode =
            CodeableConcept(
              Coding(
                "http://id.who.int/icd11/mms",
                "XM5DF6",
                "COVID-19 vaccine, live attenuated virus",
              ),
            )
          status = Immunization.ImmunizationStatus.COMPLETED
        },
        Immunization().apply {
          id = "immunization-3"
          vaccineCode =
            CodeableConcept(
              Coding("http://id.who.int/icd11/mms", "XM6AT1", "COVID-19 vaccine, DNA based"),
            )
          status = Immunization.ImmunizationStatus.COMPLETED
        },
        Immunization().apply {
          id = "immunization-4"
          vaccineCode =
            CodeableConcept(
              Coding(
                "http://hl7.org/fhir/sid/cvx",
                "140",
                "Influenza, seasonal, injectable, preservative free",
              ),
            )
          status = Immunization.ImmunizationStatus.COMPLETED
        },
      )

    database.insert(*resources.toTypedArray())

    val result =
      database.search<Immunization>(
        Search(ResourceType.Immunization)
          .apply {
            filter(
              Immunization.VACCINE_CODE,
              { value = of(Coding("http://id.who.int/icd11/mms", "XM1NL1", "")) },
            )

            filter(
              Immunization.VACCINE_CODE,
              { value = of(Coding("http://id.who.int/icd11/mms", "XM5DF6", "")) },
            )
            operation = Operation.OR
          }
          .getQuery(),
      )

    assertThat(result.map { it.vaccineCode.codingFirstRep.code })
      .containsExactly("XM1NL1", "XM5DF6")
      .inOrder()
  }

  @Test
  fun test_search_multiple_param_conjunction_with_multiple_values_disjunction() = runBlocking {
    val resources =
      listOf(
        Patient().apply {
          id = "patient-01"
          addName(
            HumanName().apply {
              addGiven("John")
              family = "Doe"
            },
          )
        },
        Patient().apply {
          id = "patient-02"
          addName(
            HumanName().apply {
              addGiven("Jane")
              family = "Doe"
            },
          )
        },
        Patient().apply {
          id = "patient-03"
          addName(
            HumanName().apply {
              addGiven("John")
              family = "Roe"
            },
          )
        },
        Patient().apply {
          id = "patient-04"
          addName(
            HumanName().apply {
              addGiven("Jane")
              family = "Roe"
            },
          )
        },
        Patient().apply {
          id = "patient-05"
          addName(
            HumanName().apply {
              addGiven("Rocky")
              family = "Balboa"
            },
          )
        },
      )
    database.insert(*resources.toTypedArray())

    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              Patient.GIVEN,
              {
                value = "John"
                modifier = StringFilterModifier.MATCHES_EXACTLY
              },
              {
                value = "Jane"
                modifier = StringFilterModifier.MATCHES_EXACTLY
              },
              operation = Operation.OR,
            )

            filter(
              Patient.FAMILY,
              {
                value = "Doe"
                modifier = StringFilterModifier.MATCHES_EXACTLY
              },
              {
                value = "Roe"
                modifier = StringFilterModifier.MATCHES_EXACTLY
              },
              operation = Operation.OR,
            )

            operation = Operation.AND
          }
          .getQuery(),
      )

    assertThat(result.map { it.nameFirstRep.nameAsSingleString })
      .containsExactly("John Doe", "Jane Doe", "John Roe", "Jane Roe")
      .inOrder()
  }

  @Test
  fun search_patient_with_extension_as_search_param() = runBlocking {
    val maidenNameSearchParameter =
      SearchParameter().apply {
        url = "http://example.com/SearchParameter/patient-mothersMaidenName"
        addBase("Patient")
        name = "mothers-maiden-name"
        code = "mothers-maiden-name"
        type = Enumerations.SearchParamType.STRING
        expression =
          "Patient.extension('http://hl7.org/fhir/StructureDefinition/patient-mothersMaidenName').value.as(String)"
        description = "search on mother's maiden name"
      }
    val patient =
      Patient().apply {
        addIdentifier(
          Identifier().apply {
            system = "https://custom-identifier-namespace"
            value = "OfficialIdentifier_DarcySmith_0001"
          },
        )

        addName(
          HumanName().apply {
            use = HumanName.NameUse.OFFICIAL
            family = "Smith"
            addGiven("Darcy")
            gender = Enumerations.AdministrativeGender.FEMALE
            birthDateElement = DateType("1970-01-01")
          },
        )

        addExtension(
          Extension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/patient-mothersMaidenName"
            setValue(StringType("Marca"))
          },
        )
      }
    // Get rid of the default service and create one with search params
    tearDown()
    buildFhirService(listOf(maidenNameSearchParameter))
    database.insert(patient)

    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              StringClientParam("mothers-maiden-name"),
              {
                value = "Marca"
                modifier = StringFilterModifier.MATCHES_EXACTLY
              },
            )
          }
          .getQuery(),
      )

    assertThat(result.map { it.nameFirstRep.nameAsSingleString }).contains("Darcy Smith")
  }

  @Test
  fun search_patient_with_custom_value_as_search_param() = runBlocking {
    val patient =
      Patient().apply {
        addIdentifier(
          Identifier().apply {
            system = "https://custom-identifier-namespace"
            value = "OfficialIdentifier_DarcySmith_0001"
          },
        )

        addName(
          HumanName().apply {
            use = HumanName.NameUse.OFFICIAL
            family = "Smith"
            addGiven("Darcy")
            gender = Enumerations.AdministrativeGender.FEMALE
            birthDateElement = DateType("1970-01-01")
          },
        )

        addExtension(
          Extension().apply {
            url = "http://hl7.org/fhir/StructureDefinition/patient-mothersMaidenName"
            setValue(StringType("Marca"))
          },
        )
      }
    val identifierPartialSearchParameter =
      SearchParameter().apply {
        url = "http://example.com/SearchParameter/patient-identifierPartial"
        addBase("Patient")
        name = "identifierPartial"
        code = "identifierPartial"
        type = Enumerations.SearchParamType.STRING
        expression = "Patient.identifier.value"
        description = "Search the identifier"
      }
    // Get rid of the default service and create one with search params
    tearDown()
    buildFhirService(listOf(identifierPartialSearchParameter))
    database.insert(patient)

    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(
              StringClientParam("identifierPartial"),
              {
                value = "OfficialIdentifier_"
                modifier = StringFilterModifier.STARTS_WITH
              },
            )
          }
          .getQuery(),
      )

    assertThat(result.map { it.nameFirstRep.nameAsSingleString }).contains("Darcy Smith")
  }

  @Test
  fun search_patient_with_local_lastUpdated() = runBlocking {
    database.insert(
      Patient().apply { id = "patient-test-001" },
      Patient().apply { id = "patient-test-002" },
      Patient().apply { id = "patient-test-003" },
    )

    database.update(
      Patient().apply {
        id = "patient-test-002"
        gender = Enumerations.AdministrativeGender.FEMALE
      },
    )

    val result =
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply { sort(LOCAL_LAST_UPDATED_PARAM, Order.DESCENDING) }
          .getQuery(),
      )

    assertThat(result.map { it.logicalId })
      .containsAtLeast("patient-test-002", "patient-test-003", "patient-test-001")
      .inOrder()
  }

  @Test
  fun search_patient_and_include_practitioners(): Unit = runBlocking {
    val patient01 =
      Patient().apply {
        id = "pa-01"
        addName(
          HumanName().apply {
            addGiven("James")
            family = "Gorden"
          },
        )
        addGeneralPractitioner(Reference("Practitioner/gp-01"))
        addGeneralPractitioner(Reference("Practitioner/gp-02"))
      }

    val patient02 =
      Patient().apply {
        id = "pa-02"
        addName(
          HumanName().apply {
            addGiven("James")
            family = "Bond"
          },
        )
        addGeneralPractitioner(Reference("Practitioner/gp-02"))
        addGeneralPractitioner(Reference("Practitioner/gp-03"))
      }
    val patients = listOf(patient01, patient02)

    val gp01 =
      Practitioner().apply {
        id = "gp-01"
        addName(
          HumanName().apply {
            family = "Practitioner-01"
            addGiven("General-01")
          },
        )
        active = true
      }
    val gp02 =
      Practitioner().apply {
        id = "gp-02"
        addName(
          HumanName().apply {
            family = "Practitioner-02"
            addGiven("General-02")
          },
        )
        active = false
      }
    val gp03 =
      Practitioner().apply {
        id = "gp-03"
        addName(
          HumanName().apply {
            family = "Practitioner-03"
            addGiven("General-03")
          },
        )
        active = true
      }

    val practitioners = listOf(gp01, gp02, gp03)

    database.insertRemote(*(patients + practitioners).toTypedArray())

    val result =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.GIVEN,
            {
              value = "James"
              modifier = StringFilterModifier.MATCHES_EXACTLY
            },
          )

          include<Practitioner>(Patient.GENERAL_PRACTITIONER) {
            filter(Practitioner.ACTIVE, { value = of(true) })
          }
        }
        .execute<Patient>(database)

    assertThat(result)
      .isEqualTo(
        listOf(
          SearchResult(
            patient01,
            included = mapOf(Patient.GENERAL_PRACTITIONER.paramName to listOf(gp01)),
            revIncluded = null,
          ),
          SearchResult(
            patient02,
            included = mapOf(Patient.GENERAL_PRACTITIONER.paramName to listOf(gp03)),
            revIncluded = null,
          ),
        ),
      )
  }

  @Test
  fun search_patient_and_revInclude_conditions(): Unit = runBlocking {
    val patient01 =
      Patient().apply {
        id = "pa-01"
        addName(
          HumanName().apply {
            addGiven("James")
            family = "Gorden"
          },
        )
        addGeneralPractitioner(Reference("Practitioner/gp-01"))
      }

    val patient02 =
      Patient().apply {
        id = "pa-02"
        addName(
          HumanName().apply {
            addGiven("James")
            family = "Bond"
          },
        )
        addGeneralPractitioner(Reference("Practitioner/gp-02"))
      }
    val patients = listOf(patient01, patient02)
    val diabetesCodeableConcept =
      CodeableConcept(Coding("http://snomed.info/sct", "44054006", "Diabetes"))
    val hyperTensionCodeableConcept =
      CodeableConcept(Coding("http://snomed.info/sct", "827069000", "Hypertension stage 1"))
    val migraineCodeableConcept =
      CodeableConcept(Coding("http://snomed.info/sct", "37796009", "Migraine"))

    val con1 =
      Condition().apply {
        id = "con-01"
        code = diabetesCodeableConcept
        subject = Reference("Patient/pa-01")
      }
    val con2 =
      Condition().apply {
        id = "con-02"
        code = hyperTensionCodeableConcept
        subject = Reference("Patient/pa-01")
      }
    val con3 =
      Condition().apply {
        id = "con-03"
        code = migraineCodeableConcept
        subject = Reference("Patient/pa-02")
      }
    val conditions = listOf(con1, con2, con3)

    database.insertRemote(*(patients + conditions).toTypedArray())

    val result =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.GIVEN,
            {
              value = "James"
              modifier = StringFilterModifier.MATCHES_EXACTLY
            },
          )
          revInclude<Condition>(Condition.SUBJECT) {
            filter(Condition.CODE, { value = of(diabetesCodeableConcept) })
            filter(Condition.CODE, { value = of(migraineCodeableConcept) })
            operation = Operation.OR
          }
        }
        .execute<Patient>(database)

    assertThat(result)
      .isEqualTo(
        listOf(
          SearchResult(
            patient01,
            included = null,
            revIncluded =
              mapOf((ResourceType.Condition to Condition.SUBJECT.paramName) to listOf(con1)),
          ),
          SearchResult(
            patient02,
            included = null,
            revIncluded =
              mapOf((ResourceType.Condition to Condition.SUBJECT.paramName) to listOf(con3)),
          ),
        ),
      )
  }

  @Test
  fun search_patient_with_reference_resources(): Unit = runBlocking {
    val diabetesCodeableConcept =
      CodeableConcept(Coding("http://snomed.info/sct", "44054006", "Diabetes"))
    val hyperTensionCodeableConcept =
      CodeableConcept(Coding("http://snomed.info/sct", "827069000", "Hypertension stage 1"))
    val migraineCodeableConcept =
      CodeableConcept(Coding("http://snomed.info/sct", "37796009", "Migraine"))

    val patients =
      listOf(
        Patient().apply {
          id = "pa-01"
          addName(
            HumanName().apply {
              addGiven("James")
              family = "Gorden"
            },
          )
          addGeneralPractitioner(Reference("Practitioner/gp-01"))
          addGeneralPractitioner(Reference("Practitioner/gp-02"))
          addGeneralPractitioner(Reference("Practitioner/gp-03"))
          managingOrganization = Reference("Organization/org-01")
        },
        Patient().apply {
          id = "pa-02"
          addName(
            HumanName().apply {
              addGiven("James")
              family = "Bond"
            },
          )
          addGeneralPractitioner(Reference("Practitioner/gp-01"))
          addGeneralPractitioner(Reference("Practitioner/gp-02"))
          addGeneralPractitioner(Reference("Practitioner/gp-03"))
          managingOrganization = Reference("Organization/org-02")
        },
        Patient().apply {
          id = "pa-03"
          addName(
            HumanName().apply {
              addGiven("James")
              family = "Doe"
            },
          )
          addGeneralPractitioner(Reference("Practitioner/gp-01"))
          addGeneralPractitioner(Reference("Practitioner/gp-02"))
          addGeneralPractitioner(Reference("Practitioner/gp-03"))
          managingOrganization = Reference("Organization/org-03")
        },
      )

    val practitioners =
      listOf(
        Practitioner().apply {
          id = "gp-01"
          addName(
            HumanName().apply {
              family = "Practitioner-01"
              addGiven("General-01")
            },
          )
          active = true
        },
        Practitioner().apply {
          id = "gp-02"
          addName(
            HumanName().apply {
              family = "Practitioner-02"
              addGiven("General-02")
            },
          )
          active = true
        },
        Practitioner().apply {
          id = "gp-03"
          addName(
            HumanName().apply {
              family = "Practitioner-03"
              addGiven("General-03")
            },
          )
          active = false
        },
      )

    val organizations =
      listOf(
        Organization().apply {
          id = "org-01"
          name = "Organization-01"
          active = true
        },
        Organization().apply {
          id = "org-02"
          name = "Organization-02"
          active = true
        },
        Organization().apply {
          id = "org-03"
          name = "Organization-03"
          active = false
        },
      )

    val conditions =
      listOf(
        Condition().apply {
          id = "con-01-pa-01"
          code = diabetesCodeableConcept
          subject = Reference("Patient/pa-01")
        },
        Condition().apply {
          id = "con-02-pa-01"
          code = hyperTensionCodeableConcept
          subject = Reference("Patient/pa-01")
        },
        Condition().apply {
          id = "con-03-pa-01"
          code = migraineCodeableConcept
          subject = Reference("Patient/pa-01")
        },
        Condition().apply {
          id = "con-01-pa-02"
          code = diabetesCodeableConcept
          subject = Reference("Patient/pa-02")
        },
        Condition().apply {
          id = "con-02-pa-02"
          code = hyperTensionCodeableConcept
          subject = Reference("Patient/pa-02")
        },
        Condition().apply {
          id = "con-03-pa-02"
          code = migraineCodeableConcept
          subject = Reference("Patient/pa-02")
        },
        Condition().apply {
          id = "con-01-pa-03"
          code = diabetesCodeableConcept
          subject = Reference("Patient/pa-03")
        },
        Condition().apply {
          id = "con-02-pa-03"
          code = hyperTensionCodeableConcept
          subject = Reference("Patient/pa-03")
        },
        Condition().apply {
          id = "con-03-pa-03"
          code = migraineCodeableConcept
          subject = Reference("Patient/pa-03")
        },
      )

    val encounters =
      listOf(
        Encounter().apply {
          id = "en-01-pa-01"
          subject = Reference("Patient/pa-01")
          period =
            Period().apply {
              start = DateType(2023, 2, 1).value
              end = DateType(2023, 11, 1).value
            }
        },
        Encounter().apply {
          id = "en-02-pa-01"
          subject = Reference("Patient/pa-01")
          period =
            Period().apply {
              start = DateType(2023, 2, 1).value
              end = DateType(2023, 11, 1).value
            }
        },
        Encounter().apply {
          id = "en-03-pa-01"
          subject = Reference("Patient/pa-01")
          period =
            Period().apply {
              start = DateType(2022, 2, 1).value
              end = DateType(2022, 11, 1).value
            }
        },
        Encounter().apply {
          id = "en-01-pa-02"
          subject = Reference("Patient/pa-02")
          period =
            Period().apply {
              start = DateType(2023, 2, 1).value
              end = DateType(2023, 11, 1).value
            }
        },
        Encounter().apply {
          id = "en-02-pa-02"
          subject = Reference("Patient/pa-02")
          period =
            Period().apply {
              start = DateType(2023, 2, 1).value
              end = DateType(2023, 11, 1).value
            }
        },
        Encounter().apply {
          id = "en-03-pa-02"
          subject = Reference("Patient/pa-02")
          period =
            Period().apply {
              start = DateType(2022, 2, 1).value
              end = DateType(2022, 11, 1).value
            }
        },
        Encounter().apply {
          id = "en-01-pa-03"
          subject = Reference("Patient/pa-03")
          period =
            Period().apply {
              start = DateType(2023, 2, 1).value
              end = DateType(2023, 11, 1).value
            }
        },
        Encounter().apply {
          id = "en-02-pa-03"
          subject = Reference("Patient/pa-03")
          period =
            Period().apply {
              start = DateType(2023, 2, 1).value
              end = DateType(2023, 11, 1).value
            }
        },
        Encounter().apply {
          id = "en-03-pa-03"
          subject = Reference("Patient/pa-03")
          period =
            Period().apply {
              start = DateType(2022, 2, 1).value
              end = DateType(2022, 11, 1).value
            }
        },
      )
    // 3 Patients.
    // Each has 3 conditions, only 2 should match
    // Each has 3 encounters, only 2 should match

    val resources: Map<String, Resource> =
      (patients + practitioners + organizations + conditions + encounters).associateBy {
        it.logicalId
      }
    // Each has 3 GP, only 2 should match
    database.insertRemote(*resources.values.toTypedArray())

    val result =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.GIVEN,
            {
              value = "James"
              modifier = StringFilterModifier.MATCHES_EXACTLY
            },
          )

          include<Practitioner>(Patient.GENERAL_PRACTITIONER) {
            filter(Practitioner.ACTIVE, { value = of(true) })
            filter(
              Practitioner.FAMILY,
              {
                value = "Practitioner"
                modifier = StringFilterModifier.STARTS_WITH
              },
            )
            operation = Operation.AND
          }
          include<Organization>(Patient.ORGANIZATION) {
            filter(
              Organization.NAME,
              {
                value = "Organization"
                modifier = StringFilterModifier.STARTS_WITH
              },
            )
            filter(Practitioner.ACTIVE, { value = of(true) })
            operation = Operation.AND
          }

          revInclude<Condition>(Condition.SUBJECT) {
            filter(Condition.CODE, { value = of(diabetesCodeableConcept) })
            filter(Condition.CODE, { value = of(migraineCodeableConcept) })
            operation = Operation.OR
          }
          revInclude<Encounter>(Encounter.SUBJECT) {
            filter(
              Encounter.DATE,
              {
                value = of(DateTimeType("2023-01-01"))
                prefix = ParamPrefixEnum.GREATERTHAN_OR_EQUALS
              },
            )
          }
        }
        .execute<Patient>(database)

    assertThat(result)
      .isEqualTo(
        listOf(
          SearchResult(
            resources["pa-01"]!!,
            mapOf(
              "general-practitioner" to listOf(resources["gp-01"]!!, resources["gp-02"]!!),
              "organization" to listOf(resources["org-01"]!!),
            ),
            mapOf(
              Pair(ResourceType.Condition, "subject") to
                listOf(resources["con-01-pa-01"]!!, resources["con-03-pa-01"]!!),
              Pair(ResourceType.Encounter, "subject") to
                listOf(resources["en-01-pa-01"]!!, resources["en-02-pa-01"]!!),
            ),
          ),
          SearchResult(
            resources["pa-02"]!!,
            mapOf(
              "general-practitioner" to listOf(resources["gp-01"]!!, resources["gp-02"]!!),
              "organization" to listOf(resources["org-02"]!!),
            ),
            mapOf(
              Pair(ResourceType.Condition, "subject") to
                listOf(resources["con-01-pa-02"]!!, resources["con-03-pa-02"]!!),
              Pair(ResourceType.Encounter, "subject") to
                listOf(resources["en-01-pa-02"]!!, resources["en-02-pa-02"]!!),
            ),
          ),
          SearchResult(
            resources["pa-03"]!!,
            mapOf(
              "general-practitioner" to listOf(resources["gp-01"]!!, resources["gp-02"]!!),
            ),
            mapOf(
              Pair(ResourceType.Condition, "subject") to
                listOf(resources["con-01-pa-03"]!!, resources["con-03-pa-03"]!!),
              Pair(ResourceType.Encounter, "subject") to
                listOf(resources["en-01-pa-03"]!!, resources["en-02-pa-03"]!!),
            ),
          ),
        ),
      )
  }

  private companion object {
    const val mockEpochTimeStamp = 1628516301000
    const val TEST_PATIENT_1_ID = "test_patient_1"
    val TEST_PATIENT_1 = Patient()

    init {
      TEST_PATIENT_1.meta.id = "v1-of-patient1"
      TEST_PATIENT_1.id = TEST_PATIENT_1_ID
      TEST_PATIENT_1.gender = Enumerations.AdministrativeGender.MALE
    }

    const val TEST_PATIENT_2_ID = "test_patient_2"
    val TEST_PATIENT_2 = Patient()

    init {
      TEST_PATIENT_2.meta.id = "v1-of-patient2"
      TEST_PATIENT_2.id = TEST_PATIENT_2_ID
      TEST_PATIENT_2.gender = Enumerations.AdministrativeGender.MALE
    }

    @JvmStatic @Parameters(name = "encrypted={0}") fun data(): Array<Boolean> = arrayOf(true, false)
  }
}
