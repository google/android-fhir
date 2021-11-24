/*
 * Copyright 2020 Google LLC
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
import ca.uhn.fhir.context.FhirContext
import com.google.android.fhir.FhirServices.Companion.builder
import com.google.android.fhir.db.ResourceNotFoundException
import com.google.android.fhir.db.impl.dao.SquashedLocalChange
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.logicalId
import com.google.android.fhir.resource.TestingUtils
import com.google.common.truth.Truth.assertThat
import java.util.Date
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Enumerations
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

  @Before fun setUp() = runBlocking { fhirEngine.save(TEST_PATIENT_1) }

  @Test
  fun save_shouldSaveResource() = runBlocking {
    fhirEngine.save(TEST_PATIENT_2)
    testingUtils.assertResourceEquals(
      TEST_PATIENT_2,
      fhirEngine.load(Patient::class.java, TEST_PATIENT_2_ID)
    )
  }

  @Test
  fun saveAll_shouldSaveResource() = runBlocking {
    fhirEngine.save(TEST_PATIENT_1, TEST_PATIENT_2)
    testingUtils.assertResourceEquals(
      TEST_PATIENT_1,
      fhirEngine.load(Patient::class.java, TEST_PATIENT_1_ID)
    )
    testingUtils.assertResourceEquals(
      TEST_PATIENT_2,
      fhirEngine.load(Patient::class.java, TEST_PATIENT_2_ID)
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
    val patient = Patient()
    patient.id = TEST_PATIENT_1_ID
    patient.gender = Enumerations.AdministrativeGender.FEMALE
    fhirEngine.update(patient)
    testingUtils.assertResourceEquals(
      patient,
      fhirEngine.load(Patient::class.java, TEST_PATIENT_1_ID)
    )
  }

  @Test
  fun load_nonexistentResource_shouldThrowResourceNotFoundException() {
    val resourceNotFoundException =
      assertThrows(ResourceNotFoundException::class.java) {
        runBlocking { fhirEngine.load(Patient::class.java, "nonexistent_patient") }
      }
    assertThat(resourceNotFoundException.message)
      .isEqualTo(
        "Resource not found with type ${ResourceType.Patient.name} and id nonexistent_patient!"
      )
  }

  @Test
  fun load_shouldReturnResource() = runBlocking {
    testingUtils.assertResourceEquals(
      TEST_PATIENT_1,
      fhirEngine.load(Patient::class.java, TEST_PATIENT_1_ID)
    )
  }

  @Test
  fun syncUpload_uploadLocalChange() = runBlocking {
    val localChanges = mutableListOf<SquashedLocalChange>()
    fhirEngine.syncUpload { it ->
      localChanges.addAll(it)
      return@syncUpload it.map { it.token }
    }

    assertThat(localChanges).hasSize(1)
    val localChange = localChanges[0].localChange
    assertThat(localChange.resourceType).isEqualTo(ResourceType.Patient.toString())
    assertThat(localChange.resourceId).isEqualTo(TEST_PATIENT_1.id)
    assertThat(localChange.type).isEqualTo(LocalChangeEntity.Type.INSERT)
    assertThat(localChange.payload)
      .isEqualTo(services.parser.encodeResourceToString(TEST_PATIENT_1))
  }

  @Test
  fun syncDownload_downloadResources() = runBlocking {
    fhirEngine.syncDownload {
      return@syncDownload listOf(TEST_PATIENT_2)
    }

    testingUtils.assertResourceEquals(
      TEST_PATIENT_2,
      fhirEngine.load(Patient::class.java, TEST_PATIENT_2_ID)
    )
  }
  @Test
  fun save_shouldSavePatientResource() = runBlocking {
    val patient: Patient = testingUtils.readFromFile(Patient::class.java, "/New_patient.json")
    fhirEngine.save(patient)
    val json = FhirContext.forR4().newJsonParser().encodeResourceToString(patient)
    println(json)

    testingUtils.assertResourceEquals(
      patient,
      fhirEngine.load(Patient::class.java, patient.logicalId)
    )
    val loadedPatient = fhirEngine.load(Patient::class.java, patient.logicalId)
    assertThat(loadedPatient.name[0].family).isEqualTo("Ross")
  }

  @Test
  fun updateShouldReturnUpdatedResource() = runBlocking {
    val patient: Patient = testingUtils.readFromFile(Patient::class.java, "/New_patient.json")
    fhirEngine.save(patient)
    val newPatientJson = FhirContext.forR4().newJsonParser().encodeResourceToString(patient)
    println(newPatientJson)
    val updatedPatient: Patient =
      testingUtils.readFromFile(Patient::class.java, "/Update_patient.json")
    fhirEngine.update(updatedPatient)
    val updatedPatientJson =
      FhirContext.forR4().newJsonParser().encodeResourceToString(updatedPatient)
    print(updatedPatientJson)

    testingUtils.assertResourceEquals(
      updatedPatient,
      fhirEngine.load(Patient::class.java, updatedPatient.logicalId)
    )
    val loadedPatient = fhirEngine.load(Patient::class.java, updatedPatient.logicalId)
    assertThat(loadedPatient.name[0].family).isEqualTo("Spector")
    assertThat(loadedPatient.nameFirstRep.given.first().valueAsString).isEqualTo("Michel")
  }

  @Test
  fun remove_shouldDeleteResource() = runBlocking {
    val patient: Patient = testingUtils.readFromFile(Patient::class.java, "/New_patient.json")
    fhirEngine.save(patient)
    val newPatientJson = FhirContext.forR4().newJsonParser().encodeResourceToString(patient)
    println(newPatientJson)
    fhirEngine.remove(Patient::class.java, patient.logicalId)

    val resourceNotFoundException =
      assertThrows(ResourceNotFoundException::class.java) {
        runBlocking { fhirEngine.load(Patient::class.java, patient.logicalId) }
      }
    assertThat(resourceNotFoundException.message)
      .isEqualTo(
        "Resource not found with type ${ResourceType.Patient.name} and id ${patient.logicalId}!"
      )
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
