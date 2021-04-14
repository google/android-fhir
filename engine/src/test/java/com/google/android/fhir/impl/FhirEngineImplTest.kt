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
import ca.uhn.fhir.rest.param.StringParam
import com.google.android.fhir.FhirServices.Companion.builder
import com.google.android.fhir.ResourceNotFoundException
import com.google.android.fhir.db.ResourceNotFoundInDbException
import com.google.android.fhir.resource.TestingUtils
import com.google.android.fhir.search.Search
import com.google.android.fhir.sync.FhirDataSource
import com.google.common.truth.Truth
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Unit tests for [FhirEngineImpl]. */
@RunWith(RobolectricTestRunner::class)
class FhirEngineImplTest {
  private val dataSource =
    object : FhirDataSource {
      override suspend fun loadData(path: String): Bundle {
        return Bundle()
      }

      override suspend fun insert(
        resourceType: String,
        resourceId: String,
        payload: String
      ): Resource {
        return Patient()
      }

      override suspend fun update(
        resourceType: String,
        resourceId: String,
        payload: String
      ): OperationOutcome {
        return OperationOutcome()
      }

      override suspend fun delete(resourceType: String, resourceId: String): OperationOutcome {
        return OperationOutcome()
      }
    }
  private val services =
    builder(dataSource, ApplicationProvider.getApplicationContext()).inMemory().build()
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
      assertThrows(ResourceNotFoundInDbException::class.java) {
        runBlocking { fhirEngine.update(TEST_PATIENT_2) }
      }
    Truth.assertThat(exception.message)
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
    Truth.assertThat(resourceNotFoundException.message)
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
  fun search_String_Exact_shouldReturnExactResource() {
    val patient1 =
      Patient().apply {
        id = "test_1"
        addName(HumanName().addGiven("Eve"))
      }
    val patient2 =
      Patient().apply {
        id = "test_2"
        addName(HumanName().addGiven("EVE"))
      }
    val patient3 =
      Patient().apply {
        id = "test_3"
        addName(HumanName().addGiven("eve"))
      }
    val res = runBlocking {
      fhirEngine.save(patient1, patient2, patient3)
      fhirEngine.search<Patient>(
        Search(ResourceType.Patient).apply {
          filter(Patient.GIVEN) {
            value = "Eve"
            modifier = StringParam().setExact(true)
          }
        }
      )
    }
    Truth.assertThat(res).hasSize(1)
    Truth.assertThat(res[0].id).isEqualTo("Patient/${patient1.id}")
    Truth.assertThat(res[0].nameFirstRep.given.any { it.toString() == "Eve" }).isTrue()
  }

  @Test
  fun search_String_Contains_shouldReturnContainsResource() {
    val patient1 =
      Patient().apply {
        id = "test_1"
        addName(HumanName().addGiven("Severine"))
      }
    val patient2 =
      Patient().apply {
        id = "test_2"
        addName(HumanName().addGiven("Evelyn"))
      }
    val patient3 =
      Patient().apply {
        id = "test_3"
        addName(HumanName().addGiven("Eve"))
      }
    val res = runBlocking {
      fhirEngine.save(patient1, patient2, patient3)
      fhirEngine.search<Patient>(
        Search(ResourceType.Patient).apply {
          filter(Patient.GIVEN) {
            value = "eve"
            modifier = StringParam().setContains(true)
          }
        }
      )
    }
    Truth.assertThat(res).hasSize(3)
    Truth.assertThat(
        res.all { patient ->
          patient.nameFirstRep.given.any { it.toString().toLowerCase().contains("eve") }
        }
      )
      .isTrue()
  }

  @Test
  fun search_String_Default_shouldReturnDefaultResource() {
    val patient1 =
      Patient().apply {
        id = "test_1"
        addName(HumanName().addGiven("Doe").addGiven("Eve"))
      }
    val patient2 =
      Patient().apply {
        id = "test_2"
        addName(HumanName().addGiven("Evelyn"))
      }
    val patient3 =
      Patient().apply {
        id = "test_3"
        addName(HumanName().setFamily("Severine"))
      }
    val res = runBlocking {
      fhirEngine.save(patient1, patient2, patient3)
      fhirEngine.search<Patient>(
        Search(ResourceType.Patient).apply { filter(Patient.GIVEN) { value = "eve" } }
      )
    }
    Truth.assertThat(res).hasSize(2)
    Truth.assertThat(
        res.all { patient -> patient.nameFirstRep.given.any { it.toString().startsWith("Eve") } }
      )
      .isTrue()
  }

  companion object {
    private const val TEST_PATIENT_1_ID = "test_patient_1"
    private var TEST_PATIENT_1 = Patient()
    init {
      TEST_PATIENT_1.id = TEST_PATIENT_1_ID
      TEST_PATIENT_1.gender = Enumerations.AdministrativeGender.MALE
    }

    private const val TEST_PATIENT_2_ID = "test_patient_2"
    private var TEST_PATIENT_2 = Patient()
    init {
      TEST_PATIENT_2.id = TEST_PATIENT_2_ID
      TEST_PATIENT_2.gender = Enumerations.AdministrativeGender.MALE
    }
  }
}
