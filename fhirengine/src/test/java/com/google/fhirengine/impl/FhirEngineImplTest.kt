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

package com.google.fhirengine.impl

import androidx.test.core.app.ApplicationProvider
import com.google.fhirengine.FhirServices.Companion.builder
import com.google.fhirengine.ResourceNotFoundException
import com.google.fhirengine.resource.TestingUtils
import com.google.fhirengine.sync.FhirDataSource
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Assert
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Unit tests for [FhirEngineImpl].  */
@RunWith(RobolectricTestRunner::class)
class FhirEngineImplTest {
    private val dataSource = object : FhirDataSource {
        override suspend fun loadData(path: String): Bundle {
            return Bundle()
        }
    }
    private val services = builder(dataSource,
        ApplicationProvider.getApplicationContext())
        .inMemory()
        .build()
    private val fhirEngine = services.fhirEngine
    private val testingUtils = TestingUtils(services.parser)

    @Before
    fun setUp() {
        fhirEngine.save(TEST_PATIENT_1)
    }

    @Test
    @Throws(Exception::class)
    fun save_shouldSaveResource() {
        fhirEngine.save(TEST_PATIENT_2)
        testingUtils.assertResourceEquals(
            TEST_PATIENT_2,
            fhirEngine.load(Patient::class.java, TEST_PATIENT_2_ID)
        )
    }

    @Test
    @Throws(Exception::class)
    fun saveAll_shouldSaveResource() {
        val patients = listOf(TEST_PATIENT_1, TEST_PATIENT_2)
        fhirEngine.saveAll(patients)
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
    @Throws(Exception::class)
    fun update_nonexistentResource_shouldInsertResource() {
        fhirEngine.update(TEST_PATIENT_2)
        testingUtils.assertResourceEquals(
            TEST_PATIENT_2,
            fhirEngine.load(Patient::class.java, TEST_PATIENT_2_ID)
        )
    }

    @Test
    @Throws(Exception::class)
    fun update_shouldUpdateResource() {
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
    @Throws(Exception::class)
    fun load_nonexistentResource_shouldThrowResourceNotFondException() {
        val resourceNotFoundInDbException =
            assertThrows(ResourceNotFoundException::class.java) {
                fhirEngine.load(Patient::class.java, "nonexistent_patient")
            }
        Assert.assertEquals(
            "Resource not found with type " +
                ResourceType.Patient.name +
                " and id nonexistent_patient!",
            resourceNotFoundInDbException.message
        )
    }

    @Test
    @Throws(Exception::class)
    fun load_shouldReturnResource() {
        testingUtils.assertResourceEquals(
            TEST_PATIENT_1,
            fhirEngine.load(Patient::class.java, TEST_PATIENT_1_ID)
        )
    }

    companion object {
        private const val TEST_PATIENT_1_ID = "test_patient_1"
        private var TEST_PATIENT_1 = Patient()
        init {
            TEST_PATIENT_1.setId(TEST_PATIENT_1_ID)
            TEST_PATIENT_1.setGender(Enumerations.AdministrativeGender.MALE)
        }

        private const val TEST_PATIENT_2_ID = "test_patient_2"
        private var TEST_PATIENT_2 = Patient()
        init {
            TEST_PATIENT_2.setId(TEST_PATIENT_2_ID)
            TEST_PATIENT_2.setGender(Enumerations.AdministrativeGender.MALE)
        }
    }
}
