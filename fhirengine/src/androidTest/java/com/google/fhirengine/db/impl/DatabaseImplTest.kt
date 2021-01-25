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
import com.google.fhirengine.db.impl.entities.LocalChange
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
        val resourceNotFoundInDbException = assertThrows(ResourceNotFoundInDbException::class.java) {
            database.update(TEST_PATIENT_2)
        }
        Truth.assertThat(resourceNotFoundInDbException.message)
            /* ktlint-disable max-line-length */
            .isEqualTo("Resource not found with type ${TEST_PATIENT_2.resourceType.name} and id $TEST_PATIENT_2_ID!"
            /* ktlint-enable max-line-length */
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
    fun select_nonexistentResource_shouldThrowResourceNotFoundException() {
        val resourceNotFoundException =
            assertThrows(
                ResourceNotFoundInDbException::class.java,
                { database.select(Patient::class.java, "nonexistent_patient") }
            )
        assertEquals(
            "Resource not found with type ${ResourceType.Patient.name} and id nonexistent_patient!",
            resourceNotFoundException.message
        )
    }

    @Test
    fun select_shouldReturnResource() {
        testingUtils.assertResourceEquals(
            TEST_PATIENT_1,
            database.select(Patient::class.java, TEST_PATIENT_1_ID)
        )
    }

    @Test
    fun update_insertAndUpdate_shouldAddUpdateLocalChange() {
        var patient: Patient = testingUtils.readFromFile(Patient::class.java, "/date_test_patient.json")
        database.insert(patient)
        patient = testingUtils.readFromFile(Patient::class.java, "/update_test_patient_1.json")
        database.update(patient)
        val patientString = services.parser.encodeResourceToString(patient)
        val localChange = database.getAllLocalChanges().first { it.resourceId.equals(patient.id) }
        Truth.assertThat(localChange.type).isEqualTo(LocalChange.Type.INSERT)
        Truth.assertThat(localChange.resourceId).isEqualTo(patient.id)
        Truth.assertThat(localChange.resourceType).isEqualTo(patient.resourceType.name)
        Truth.assertThat(localChange.diff).isEqualTo(patientString)
    }

    @Test
    fun insert_shouldAddInsertLocalChange() {
        val testPatient2String = services.parser.encodeResourceToString(TEST_PATIENT_2)
        database.insert(TEST_PATIENT_2)
        val localChange = database.getAllLocalChanges().first { it.resourceId.equals(TEST_PATIENT_2_ID) }
        Truth.assertThat(localChange.type).isEqualTo(LocalChange.Type.INSERT)
        Truth.assertThat(localChange.resourceId).isEqualTo(TEST_PATIENT_2_ID)
        Truth.assertThat(localChange.resourceType).isEqualTo(TEST_PATIENT_2.resourceType.name)
        Truth.assertThat(localChange.diff).isEqualTo(testPatient2String)
    }

    @Test
    fun delete_shouldAddDeleteLocalChange() {
        database.delete(Patient::class.java, TEST_PATIENT_1_ID)
        val localChange = database.getAllLocalChanges().first { it.resourceId.equals(TEST_PATIENT_1_ID) }
        Truth.assertThat(localChange.type).isEqualTo(LocalChange.Type.DELETE)
        Truth.assertThat(localChange.resourceId).isEqualTo(TEST_PATIENT_1_ID)
        Truth.assertThat(localChange.resourceType).isEqualTo(TEST_PATIENT_1.resourceType.name)
        Truth.assertThat(localChange.diff).isEmpty()
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
