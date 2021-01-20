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

package com.google.fhirengine.db.impl

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.google.fhirengine.FhirServices
import com.google.fhirengine.db.ResourceNotFoundInDbException
import com.google.fhirengine.resource.TestingUtils
import com.google.fhirengine.sync.FhirDataSource
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for [DatabaseImpl]. There are written as integration tests as officially
 * recommend because:
 * * Different versions of android are shipped with different versions of SQLite. Integration tests
 * allow for better coverage on them.
 * * Robolectric's SQLite implementation does not match Android, e.g.: https://github.com/robolectric/robolectric/blob/master/shadows/framework/src/main/java/org/robolectric/shadows/ShadowSQLiteConnection.java#L97
 */
@RunWith(AndroidJUnit4::class)
class DatabaseImplTest {
    private val dataSource = object : FhirDataSource {
        override suspend fun loadData(path: String): Bundle {
            return Bundle()
        }
    }
    private val services =
        FhirServices.builder(dataSource, ApplicationProvider.getApplicationContext())
            .inMemory()
            .build()
    private val testingUtils = TestingUtils(services.parser)
    private val database = services.database

    @Before
    fun setUp() {
        database.insert(TEST_PATIENT_1)
    }

    @Test
    fun insert_shouldInsertResource() {
        database.insert(TEST_PATIENT_2)
        testingUtils.assertResourceEquals(
            TEST_PATIENT_2,
            database.select(Patient::class.java, TEST_PATIENT_2_ID)
        )
    }

    @Test
    fun insertAll_shouldInsertResources() {
        val patients = ArrayList<Patient>()
        patients.add(TEST_PATIENT_1)
        patients.add(TEST_PATIENT_2)
        database.insertAll(patients)
        testingUtils.assertResourceEquals(
            TEST_PATIENT_1,
            database.select(Patient::class.java, TEST_PATIENT_1_ID)
        )
        testingUtils.assertResourceEquals(
            TEST_PATIENT_2,
            database.select(Patient::class.java, TEST_PATIENT_2_ID)
        )
    }

    @Test
    fun update_existentResource_shouldUpdateResource() {
        val patient = Patient()
        patient.setId(TEST_PATIENT_1_ID)
        patient.setGender(Enumerations.AdministrativeGender.FEMALE)
        database.update(patient)
        testingUtils.assertResourceEquals(
            patient,
            database.select(Patient::class.java, TEST_PATIENT_1_ID)
        )
    }

    @Test
    fun update_nonExistingResource_shouldNotInsertResource() {
        database.update(TEST_PATIENT_2)
        val resourceNotFoundInDbException = assertThrows(
            ResourceNotFoundInDbException::class.java,
            { database.select(Patient::class.java, TEST_PATIENT_2_ID) }
        )
        Truth.assertThat(resourceNotFoundInDbException.message).isEqualTo(
            "Resource not found with type ${TEST_PATIENT_2.resourceType.name} and id ${TEST_PATIENT_2_ID}!"
        )
    }

    @Test
    fun select_invalidResourceType_shouldThrowIllegalArgumentException() {
        val illegalArgumentException =
            assertThrows(
                IllegalArgumentException::class.java,
                { database.select(Resource::class.java, "resource_id") }
            )
        assertEquals(
            "Cannot resolve resource type for " + Resource::class.java.name,
            illegalArgumentException.message
        )
    }

    @Test
    fun select_nonexistentResource_shouldThrowResourceNotFondException() {
        val resourceNotFoundInDbException =
            assertThrows(
                ResourceNotFoundInDbException::class.java,
                { database.select(Patient::class.java, "nonexistent_patient") }
            )
        assertEquals(
            "Resource not found with type " +
                ResourceType.Patient.name +
                " and id nonexistent_patient!",
            resourceNotFoundInDbException.message
        )
    }

    @Test
    fun select_shouldReturnResource() {
        testingUtils.assertResourceEquals(
            TEST_PATIENT_1,
            database.select(Patient::class.java, TEST_PATIENT_1_ID)
        )
    }

    private companion object {
        const val TEST_PATIENT_1_ID = "test_patient_1"
        val TEST_PATIENT_1 = Patient()

        init {
            TEST_PATIENT_1.setId(TEST_PATIENT_1_ID)
            TEST_PATIENT_1.setGender(Enumerations.AdministrativeGender.MALE)
        }

        const val TEST_PATIENT_2_ID = "test_patient_2"
        val TEST_PATIENT_2 = Patient()

        init {
            TEST_PATIENT_2.setId(TEST_PATIENT_2_ID)
            TEST_PATIENT_2.setGender(Enumerations.AdministrativeGender.MALE)
        }
    }
}
