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

package com.google.android.fhir.impl

import androidx.test.core.app.ApplicationProvider
import com.google.android.fhir.FhirServices.Companion.builder
import com.google.android.fhir.db.LocalChange
import com.google.android.fhir.db.LocalChangeType
import com.google.android.fhir.db.ResourceNotFoundException
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.get
import com.google.android.fhir.logicalId
import com.google.android.fhir.resource.TestingUtils
import com.google.android.fhir.sync.AcceptLocalConflictResolver
import com.google.android.fhir.sync.AcceptRemoteConflictResolver
import com.google.common.truth.Truth.assertThat
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Address
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.Meta
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Unit tests for [FhirEngineImpl]. */
@RunWith(RobolectricTestRunner::class)
class FhirEngineImplTest {
  private val services = builder(ApplicationProvider.getApplicationContext()).inMemory().build()
  private val fhirEngine = services.fhirEngine
  private val testingUtils = TestingUtils(services.parser)

  @Before fun setUp(): Unit = runBlocking { fhirEngine.create(TEST_PATIENT_1) }

  @Test
  fun create_shouldCreateResource() = runBlocking {
    val ids = fhirEngine.create(TEST_PATIENT_2)
    assertThat(ids).containsExactly("test_patient_2")
    testingUtils.assertResourceEquals(TEST_PATIENT_2, fhirEngine.get<Patient>(TEST_PATIENT_2_ID))
  }

  @Test
  fun createAll_shouldCreateResource() = runBlocking {
    val ids = fhirEngine.create(TEST_PATIENT_1, TEST_PATIENT_2)
    assertThat(ids).containsExactly("test_patient_1", "test_patient_2")
    testingUtils.assertResourceEquals(TEST_PATIENT_1, fhirEngine.get<Patient>(TEST_PATIENT_1_ID))
    testingUtils.assertResourceEquals(TEST_PATIENT_2, fhirEngine.get<Patient>(TEST_PATIENT_2_ID))
  }

  @Test
  fun create_resourceWithoutId_shouldCreateResourceWithAssignedId() = runBlocking {
    val patient =
      Patient().apply {
        addName(
          HumanName().apply {
            family = "FamilyName"
            addGiven("GivenName")
          }
        )
      }
    val ids = fhirEngine.create(patient.copy())
    assertThat(ids).hasSize(1)
    testingUtils.assertResourceEquals(
      patient.setId(ids.first()),
      fhirEngine.get<Patient>(ids.first())
    )
  }

  @Test
  fun update_nonexistentResource_shouldNotInsertResource() {
    val exception =
      assertThrows(ResourceNotFoundException::class.java) {
        runBlocking { fhirEngine.update(TEST_PATIENT_2) }
      }
    assertThat(exception.message)
      .isEqualTo(
        "Resource not found with type ${TEST_PATIENT_2.resourceType.name} and id $TEST_PATIENT_2_ID!"
      )
  }

  @Test
  fun update_shouldUpdateResource() = runBlocking {
    val patient1 =
      Patient().apply {
        id = "test-update-patient-001"
        gender = Enumerations.AdministrativeGender.MALE
      }
    val patient2 =
      Patient().apply {
        id = "test-update-patient-002"
        gender = Enumerations.AdministrativeGender.FEMALE
      }
    fhirEngine.create(patient1, patient2)
    val updatedPatient1 = patient1.copy().setGender(Enumerations.AdministrativeGender.FEMALE)
    val updatedPatient2 = patient2.copy().setGender(Enumerations.AdministrativeGender.MALE)

    fhirEngine.update(updatedPatient1, updatedPatient2)

    testingUtils.assertResourceEquals(
      updatedPatient1,
      fhirEngine.get<Patient>("test-update-patient-001")
    )
    testingUtils.assertResourceEquals(
      updatedPatient2,
      fhirEngine.get<Patient>("test-update-patient-002")
    )
  }

  @Test
  fun update_existingAndNonExistingResource_shouldNotUpdateAnyResource() = runBlocking {
    val patient1 =
      Patient().apply {
        id = "test-update-patient-001"
        gender = Enumerations.AdministrativeGender.MALE
      }

    val patient2 =
      Patient().apply {
        id = "test-update-patient-002"
        gender = Enumerations.AdministrativeGender.FEMALE
      }
    fhirEngine.create(patient1)
    val updatedPatient1 = patient1.copy().setGender(Enumerations.AdministrativeGender.FEMALE)
    val updatedPatient2 = patient2.copy().setGender(Enumerations.AdministrativeGender.MALE)

    assertThrows(ResourceNotFoundException::class.java) {
      runBlocking { fhirEngine.update(updatedPatient1, updatedPatient2) }
    }

    testingUtils.assertResourceNotEquals(
      updatedPatient1,
      fhirEngine.get<Patient>("test-update-patient-001")
    )
    testingUtils.assertResourceEquals(patient1, fhirEngine.get<Patient>("test-update-patient-001"))
  }

  @Test
  fun load_nonexistentResource_shouldThrowResourceNotFoundException() {
    val resourceNotFoundException =
      assertThrows(ResourceNotFoundException::class.java) {
        runBlocking { fhirEngine.get<Patient>("nonexistent_patient") }
      }
    assertThat(resourceNotFoundException.message)
      .isEqualTo(
        "Resource not found with type ${ResourceType.Patient.name} and id nonexistent_patient!"
      )
  }

  @Test
  fun load_shouldReturnResource() = runBlocking {
    testingUtils.assertResourceEquals(TEST_PATIENT_1, fhirEngine.get<Patient>(TEST_PATIENT_1_ID))
  }

  @Test
  fun syncUpload_uploadLocalChange() = runBlocking {
    val localChanges = mutableListOf<LocalChange>()
    fhirEngine.syncUpload {
      flow {
        localChanges.addAll(it)
        emit(LocalChangeToken(it.flatMap { it.token.ids }) to TEST_PATIENT_1)
      }
    }

    assertThat(localChanges).hasSize(1)
    // val localChange = localChanges[0].localChange
    with(localChanges[0]) {
      assertThat(this.resourceType).isEqualTo(ResourceType.Patient.toString())
      assertThat(this.resourceId).isEqualTo(TEST_PATIENT_1.id)
      assertThat(this.type).isEqualTo(LocalChangeType.INSERT)
      assertThat(this.payload).isEqualTo(services.parser.encodeResourceToString(TEST_PATIENT_1))
    }
  }

  @Test
  fun syncDownload_downloadResources() = runBlocking {
    fhirEngine.syncDownload(AcceptLocalConflictResolver) { flowOf((listOf((TEST_PATIENT_2)))) }

    testingUtils.assertResourceEquals(TEST_PATIENT_2, fhirEngine.get<Patient>(TEST_PATIENT_2_ID))
  }

  @Test
  fun `getLocalChange() should return single local change`() = runBlocking {
    val patient: Patient = testingUtils.readFromFile(Patient::class.java, "/date_test_patient.json")
    fhirEngine.create(patient)
    val patientString = services.parser.encodeResourceToString(patient)
    val squashedLocalChange = fhirEngine.getLocalChange(patient.resourceType, patient.logicalId)
    with(squashedLocalChange) {
      assertThat(this!!.resourceId).isEqualTo(patient.logicalId)
      assertThat(resourceType).isEqualTo(patient.resourceType.name)
      assertThat(type).isEqualTo(LocalChangeType.INSERT)
      assertThat(payload).isEqualTo(patientString)
    }
  }

  @Test
  fun `getLocalChange() should return squashed local change`() = runBlocking {
    val patient: Patient = testingUtils.readFromFile(Patient::class.java, "/date_test_patient.json")
    fhirEngine.create(patient)

    patient.gender = Enumerations.AdministrativeGender.FEMALE
    fhirEngine.update(patient)
    patient.name[0].family = "TestPatient"
    fhirEngine.update(patient)

    val patientString = services.parser.encodeResourceToString(patient)
    val squashedLocalChange = fhirEngine.getLocalChange(patient.resourceType, patient.logicalId)
    with(squashedLocalChange) {
      assertThat(this!!.resourceId).isEqualTo(patient.logicalId)
      assertThat(resourceType).isEqualTo(patient.resourceType.name)
      assertThat(type).isEqualTo(LocalChangeType.INSERT)
      assertThat(payload).isEqualTo(patientString)
    }
  }

  @Test
  fun `getLocalChange() with wrong resource id should return null`() = runBlocking {
    val patient: Patient = testingUtils.readFromFile(Patient::class.java, "/date_test_patient.json")
    fhirEngine.create(patient)
    assertThat(fhirEngine.getLocalChange(patient.resourceType, "nonexistent_patient")).isNull()
  }

  @Test
  fun `getLocalChange() with wrong resource type should return null`() = runBlocking {
    val patient: Patient = testingUtils.readFromFile(Patient::class.java, "/date_test_patient.json")
    fhirEngine.create(patient)

    assertThat(fhirEngine.getLocalChange(ResourceType.Encounter, patient.logicalId)).isNull()
  }

  @Test
  fun `clearDatabase() should clear all tables data`() = runBlocking {
    val patient: Patient = testingUtils.readFromFile(Patient::class.java, "/date_test_patient.json")
    fhirEngine.create(patient)
    val patientString = services.parser.encodeResourceToString(patient)
    val squashedLocalChange = fhirEngine.getLocalChange(patient.resourceType, patient.logicalId)
    with(squashedLocalChange) {
      assertThat(this!!.resourceId).isEqualTo(patient.logicalId)
      assertThat(resourceType).isEqualTo(patient.resourceType.name)
      assertThat(type).isEqualTo(LocalChangeType.INSERT)
      assertThat(payload).isEqualTo(patientString)
    }
    testingUtils.assertResourceEquals(
      patient,
      fhirEngine.get(ResourceType.Patient, patient.logicalId)
    )
    // clear databse
    runBlocking(Dispatchers.IO) { fhirEngine.clearDatabase() }
    // assert that previously present resource not available after clearing database
    assertThat(fhirEngine.getLocalChange(patient.resourceType, patient.logicalId)).isNull()
    val resourceNotFoundException =
      assertThrows(ResourceNotFoundException::class.java) {
        runBlocking { fhirEngine.get(ResourceType.Patient, patient.logicalId) }
      }
    assertThat(resourceNotFoundException.message)
      .isEqualTo("Resource not found with type Patient and id ${patient.logicalId}!")
  }

  @Test
  fun `purge() with local change and force purge true should purge resource`() = runBlocking {
    fhirEngine.purge(ResourceType.Patient, TEST_PATIENT_1_ID, true)
    // after purge the resource is not available in database
    val resourceNotFoundException =
      assertThrows(ResourceNotFoundException::class.java) {
        runBlocking { fhirEngine.get(ResourceType.Patient, TEST_PATIENT_1_ID) }
      }
    assertThat(resourceNotFoundException.message)
      .isEqualTo(
        "Resource not found with type ${TEST_PATIENT_1.resourceType.name} and id $TEST_PATIENT_1_ID!"
      )
    assertThat(fhirEngine.getLocalChange(ResourceType.Patient, TEST_PATIENT_1_ID)).isNull()
  }

  @Test
  fun `purge() with local change and force purge false should throw IllegalStateException`() =
      runBlocking {
    val resourceIllegalStateException =
      assertThrows(IllegalStateException::class.java) {
        runBlocking { fhirEngine.purge(ResourceType.Patient, TEST_PATIENT_1_ID) }
      }
    assertThat(resourceIllegalStateException.message)
      .isEqualTo(
        "Resource with type ${TEST_PATIENT_1.resourceType.name} and id $TEST_PATIENT_1_ID has local changes, either sync with server or FORCE_PURGE required"
      )
  }

  @Test
  fun `purge() resource not available should throw ResourceNotFoundException`() = runBlocking {
    val resourceNotFoundException =
      assertThrows(ResourceNotFoundException::class.java) {
        runBlocking { fhirEngine.purge(ResourceType.Patient, "nonexistent_patient") }
      }
    assertThat(resourceNotFoundException.message)
      .isEqualTo(
        "Resource not found with type ${TEST_PATIENT_1.resourceType.name} and id nonexistent_patient!"
      )
  }
  fun syncDownload_conflictResolution_acceptRemote_shouldHaveNoLocalChangeAnymore() = runBlocking {
    val originalPatient =
      Patient().apply {
        id = "original-001"
        meta =
          Meta().apply {
            versionId = "1"
            lastUpdated = Date()
          }
        addName(
          HumanName().apply {
            family = "Stark"
            addGiven("Tony")
          }
        )
      }
    fhirEngine.syncDownload(AcceptRemoteConflictResolver) { flowOf((listOf((originalPatient)))) }

    val localChange =
      originalPatient.copy().apply { addAddress(Address().apply { city = "Malibu" }) }
    fhirEngine.update(localChange)

    val remoteChange =
      originalPatient.copy().apply {
        meta =
          Meta().apply {
            versionId = "2"
            lastUpdated = Date()
          }
        addAddress(Address().apply { country = "USA" })
      }

    fhirEngine.syncDownload(AcceptRemoteConflictResolver) { flowOf((listOf(remoteChange))) }

    assertThat(
        services.database.getAllLocalChanges().filter { it.resourceId == "Patient/original-001" }
      )
      .isEmpty()
    testingUtils.assertResourceEquals(fhirEngine.get<Patient>("original-001"), remoteChange)
  }

  @Test
  fun syncDownload_conflictResolution_acceptLocal_shouldHaveLocalChangeCreatedAgainstRemoteVersion() =
      runBlocking {
    val originalPatient =
      Patient().apply {
        id = "original-002"
        meta =
          Meta().apply {
            versionId = "1"
            lastUpdated = Date()
          }
        addName(
          HumanName().apply {
            family = "Stark"
            addGiven("Tony")
          }
        )
      }
    fhirEngine.syncDownload(AcceptLocalConflictResolver) { flowOf((listOf((originalPatient)))) }
    var localChange =
      originalPatient.copy().apply { addAddress(Address().apply { city = "Malibu" }) }
    fhirEngine.update(localChange)

    localChange =
      localChange.copy().apply {
        addAddress(
          Address().apply {
            city = "Malibu"
            state = "California"
          }
        )
      }
    fhirEngine.update(localChange)

    val remoteChange =
      originalPatient.copy().apply {
        meta =
          Meta().apply {
            versionId = "2"
            lastUpdated = Date()
          }
        addAddress(Address().apply { country = "USA" })
      }

    fhirEngine.syncDownload(AcceptLocalConflictResolver) { flowOf((listOf(remoteChange))) }

    val localChangeDiff =
      """[{"op":"remove","path":"\/address\/0\/country"},{"op":"add","path":"\/address\/0\/city","value":"Malibu"},{"op":"add","path":"\/address\/-","value":{"city":"Malibu","state":"California"}}]"""
    assertThat(
        services.database.getAllLocalChanges().first { it.resourceId == "original-002" }.payload
      )
      .isEqualTo(localChangeDiff)
    testingUtils.assertResourceEquals(fhirEngine.get<Patient>("original-002"), localChange)
  }

  companion object {
    private const val TEST_PATIENT_1_ID = "test_patient_1"
    private var TEST_PATIENT_1 =
      Patient().apply {
        id = TEST_PATIENT_1_ID
        gender = Enumerations.AdministrativeGender.MALE
      }

    private const val TEST_PATIENT_2_ID = "test_patient_2"
    private var TEST_PATIENT_2 =
      Patient().apply {
        id = TEST_PATIENT_2_ID
        gender = Enumerations.AdministrativeGender.MALE
        meta = Meta().apply { lastUpdated = Date() }
      }
  }
}
