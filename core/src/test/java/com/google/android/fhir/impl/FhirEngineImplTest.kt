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
import com.google.android.fhir.FhirServices.Companion.builder
import com.google.android.fhir.ResourceNotFoundException
import com.google.android.fhir.db.ResourceNotFoundInDbException
import com.google.android.fhir.resource.TestingUtils
import com.google.android.fhir.sync.FhirDataSource
import com.google.common.truth.Truth
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Enumerations
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
  private val dataSource =
    object : FhirDataSource {
      override suspend fun loadData(path: String): Bundle {
        return Bundle()
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
