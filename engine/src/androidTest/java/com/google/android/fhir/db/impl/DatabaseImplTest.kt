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

package com.google.android.fhir.db.impl

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import com.google.android.fhir.FhirServices
import com.google.android.fhir.db.ResourceNotFoundInDbException
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.logicalId
import com.google.android.fhir.resource.TestingUtils
import com.google.android.fhir.search.Order
import com.google.android.fhir.search.Search
import com.google.android.fhir.search.StringFilterModifier
import com.google.android.fhir.search.getQuery
import com.google.android.fhir.sync.DataSource
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DecimalType
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.HumanName
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.RiskAssessment
import org.json.JSONArray
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for [DatabaseImpl]. There are written as integration tests as officially
 * recommend because:
 * * Different versions of android are shipped with different versions of SQLite. Integration tests
 * allow for better coverage on them.
 * * Robolectric's SQLite implementation does not match Android, e.g.:
 * https://github.com/robolectric/robolectric/blob/master/shadows/framework/src/main/java/org/robolectric/shadows/ShadowSQLiteConnection.java#L97
 */
@RunWith(AndroidJUnit4::class)
class DatabaseImplTest {
  private val dataSource =
    object : DataSource {

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
    FhirServices.builder(ApplicationProvider.getApplicationContext()).inMemory().build()
  private val testingUtils = TestingUtils(services.parser)
  private val database = services.database

  @Before fun setUp() = runBlocking { database.insert(TEST_PATIENT_1) }

  @Test
  fun insert_shouldInsertResource() = runBlocking {
    database.insert(TEST_PATIENT_2)
    testingUtils.assertResourceEquals(
      TEST_PATIENT_2,
      database.select(Patient::class.java, TEST_PATIENT_2_ID)
    )
  }

  @Test
  fun insertAll_shouldInsertResources() = runBlocking {
    val patients = ArrayList<Patient>()
    patients.add(TEST_PATIENT_1)
    patients.add(TEST_PATIENT_2)
    database.insert(*patients.toTypedArray())
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
  fun update_existentResource_shouldUpdateResource() = runBlocking {
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
    val resourceNotFoundInDbException =
      assertThrows(ResourceNotFoundInDbException::class.java) {
        runBlocking { database.update(TEST_PATIENT_2) }
      }
    /* ktlint-disable max-line-length */
    assertThat(resourceNotFoundInDbException.message)
      .isEqualTo(
        "Resource not found with type ${TEST_PATIENT_2.resourceType.name} and id $TEST_PATIENT_2_ID!"
        /* ktlint-enable max-line-length */
        )
  }

  @Test
  fun select_invalidResourceType_shouldThrowIllegalArgumentException() {
    val illegalArgumentException =
      assertThrows(IllegalArgumentException::class.java) {
        runBlocking { database.select(Resource::class.java, "resource_id") }
      }
    assertThat(illegalArgumentException.message)
      .isEqualTo("Cannot resolve resource type for " + Resource::class.java.name)
  }

  @Test
  fun select_nonexistentResource_shouldThrowResourceNotFoundException() {
    val resourceNotFoundException =
      assertThrows(ResourceNotFoundInDbException::class.java) {
        runBlocking { database.select(Patient::class.java, "nonexistent_patient") }
      }
    assertThat(resourceNotFoundException.message)
      .isEqualTo("Resource not found with type Patient and id nonexistent_patient!")
  }

  @Test
  fun select_shouldReturnResource() = runBlocking {
    testingUtils.assertResourceEquals(
      TEST_PATIENT_1,
      database.select(Patient::class.java, TEST_PATIENT_1_ID)
    )
  }

  @Test
  fun insert_shouldAddInsertLocalChange() = runBlocking {
    val testPatient2String = services.parser.encodeResourceToString(TEST_PATIENT_2)
    database.insert(TEST_PATIENT_2)
    val (_, resourceType, resourceId, _, type, payload) =
      database
        .getAllLocalChanges()
        .single { it.localChange.resourceId.equals(TEST_PATIENT_2_ID) }
        .localChange
    assertThat(type).isEqualTo(LocalChangeEntity.Type.INSERT)
    assertThat(resourceId).isEqualTo(TEST_PATIENT_2_ID)
    assertThat(resourceType).isEqualTo(TEST_PATIENT_2.resourceType.name)
    assertThat(payload).isEqualTo(testPatient2String)
  }

  @Test
  fun update_insertAndUpdate_shouldAddUpdateLocalChange() = runBlocking {
    var patient: Patient = testingUtils.readFromFile(Patient::class.java, "/date_test_patient.json")
    database.insert(patient)
    patient = testingUtils.readFromFile(Patient::class.java, "/update_test_patient_1.json")
    database.update(patient)
    val patientString = services.parser.encodeResourceToString(patient)
    val (_, resourceType, resourceId, _, type, payload) =
      database
        .getAllLocalChanges()
        .single { it.localChange.resourceId.equals(patient.logicalId) }
        .localChange
    assertThat(type).isEqualTo(LocalChangeEntity.Type.INSERT)
    assertThat(resourceId).isEqualTo(patient.logicalId)
    assertThat(resourceType).isEqualTo(patient.resourceType.name)
    assertThat(payload).isEqualTo(patientString)
  }

  @Test
  fun delete_shouldAddDeleteLocalChange() = runBlocking {
    database.delete(Patient::class.java, TEST_PATIENT_1_ID)
    val (_, resourceType, resourceId, _, type, payload) =
      database
        .getAllLocalChanges()
        .single { it.localChange.resourceId.equals(TEST_PATIENT_1_ID) }
        .localChange
    assertThat(type).isEqualTo(LocalChangeEntity.Type.DELETE)
    assertThat(resourceId).isEqualTo(TEST_PATIENT_1_ID)
    assertThat(resourceType).isEqualTo(TEST_PATIENT_1.resourceType.name)
    assertThat(payload).isEmpty()
  }

  @Test
  fun delete_nonExistent_shouldNotInsertLocalChange() = runBlocking {
    database.delete(Patient::class.java, "nonexistent_patient")
    assertThat(
        database.getAllLocalChanges().map { it.localChange }.none {
          it.type.equals(LocalChangeEntity.Type.DELETE) &&
            it.resourceId.equals("nonexistent_patient")
        }
      )
      .isTrue()
  }

  @Test
  fun deleteUpdates_shouldDeleteLocalChanges() = runBlocking {
    var patient: Patient = testingUtils.readFromFile(Patient::class.java, "/date_test_patient.json")
    database.insert(patient)
    patient = testingUtils.readFromFile(Patient::class.java, "/update_test_patient_1.json")
    database.update(patient)
    services.parser.encodeResourceToString(patient)
    val (token, _) =
      database.getAllLocalChanges().single { it.localChange.resourceId.equals(patient.logicalId) }
    database.deleteUpdates(token)
    assertThat(
        database.getAllLocalChanges().none { it.localChange.resourceId.equals(patient.logicalId) }
      )
      .isTrue()
  }

  @Test
  fun insert_remoteResource_shouldNotInsertLocalChange() = runBlocking {
    val patient: Patient = testingUtils.readFromFile(Patient::class.java, "/date_test_patient.json")
    database.insertRemote(patient)
    assertThat(
        database.getAllLocalChanges().map { it.localChange }.none {
          it.resourceId.equals(patient.logicalId)
        }
      )
      .isTrue()
  }

  @Test
  fun insertAll_remoteResources_shouldNotInsertAnyLocalChange() = runBlocking {
    val patient: Patient = testingUtils.readFromFile(Patient::class.java, "/date_test_patient.json")
    database.insertRemote(patient, TEST_PATIENT_2)
    assertThat(
        database.getAllLocalChanges().map { it.localChange }.none {
          it.resourceId in listOf(patient.logicalId, TEST_PATIENT_2_ID)
        }
      )
      .isTrue()
  }

  @Test
  fun update_remoteResource_readSquashedChanges_shouldReturnPatch() = runBlocking {
    val patient: Patient = testingUtils.readFromFile(Patient::class.java, "/date_test_patient.json")
    database.insertRemote(patient)
    val updatedPatient =
      testingUtils.readFromFile(Patient::class.java, "/update_test_patient_1.json")
    val updatePatch = testingUtils.readJsonArrayFromFile("/update_patch_1.json")
    database.update(updatedPatient)
    val (_, resourceType, resourceId, _, type, payload) =
      database
        .getAllLocalChanges()
        .single { it.localChange.resourceId.equals(patient.logicalId) }
        .localChange
    assertThat(type).isEqualTo(LocalChangeEntity.Type.UPDATE)
    assertThat(resourceId).isEqualTo(patient.logicalId)
    assertThat(resourceType).isEqualTo(patient.resourceType.name)
    testingUtils.assertJsonArrayEqualsIgnoringOrder(JSONArray(payload), updatePatch)
  }

  @Test
  fun updateTwice_remoteResource_readSquashedChanges_shouldReturnMergedPatch() = runBlocking {
    var patient: Patient = testingUtils.readFromFile(Patient::class.java, "/date_test_patient.json")
    database.insertRemote(patient)
    patient = testingUtils.readFromFile(Patient::class.java, "/update_test_patient_1.json")
    database.update(patient)
    patient = testingUtils.readFromFile(Patient::class.java, "/update_test_patient_2.json")
    database.update(patient)
    val updatePatch = testingUtils.readJsonArrayFromFile("/update_patch_2.json")
    val (_, resourceType, resourceId, _, type, payload) =
      database
        .getAllLocalChanges()
        .single { it.localChange.resourceId.equals(patient.logicalId) }
        .localChange
    assertThat(type).isEqualTo(LocalChangeEntity.Type.UPDATE)
    assertThat(resourceId).isEqualTo(patient.logicalId)
    assertThat(resourceType).isEqualTo(patient.resourceType.name)
    testingUtils.assertJsonArrayEqualsIgnoringOrder(JSONArray(payload), updatePatch)
  }

  @Test
  fun delete_remoteResource_shouldReturnDeleteLocalChange() = runBlocking {
    database.insertRemote(TEST_PATIENT_2)
    database.delete(Patient::class.java, TEST_PATIENT_2_ID)
    val (_, resourceType, resourceId, _, type, payload) =
      database.getAllLocalChanges().map { it.localChange }.single {
        it.resourceId.equals(TEST_PATIENT_2_ID)
      }
    assertThat(type).isEqualTo(LocalChangeEntity.Type.DELETE)
    assertThat(resourceId).isEqualTo(TEST_PATIENT_2_ID)
    assertThat(resourceType).isEqualTo(TEST_PATIENT_2.resourceType.name)
    assertThat(payload).isEmpty()
  }

  @Test
  fun delete_remoteResource_updateResource_shouldReturnDeleteLocalChange() = runBlocking {
    database.insertRemote(TEST_PATIENT_2)
    TEST_PATIENT_2.setName(listOf(HumanName().addGiven("John").setFamily("Doe")))
    database.update(TEST_PATIENT_2)
    TEST_PATIENT_2.setName(listOf(HumanName().addGiven("Jimmy").setFamily("Doe")))
    database.update(TEST_PATIENT_2)
    database.delete(Patient::class.java, TEST_PATIENT_2_ID)
    val (_, resourceType, resourceId, _, type, payload) =
      database.getAllLocalChanges().map { it.localChange }.single {
        it.resourceId.equals(TEST_PATIENT_2_ID)
      }
    assertThat(type).isEqualTo(LocalChangeEntity.Type.DELETE)
    assertThat(resourceId).isEqualTo(TEST_PATIENT_2_ID)
    assertThat(resourceType).isEqualTo(TEST_PATIENT_2.resourceType.name)
    assertThat(payload).isEmpty()
  }

  @Test
  fun search_sortDescending_twoVeryCloseFloatingPointNumbers_orderedCorrectly() = runBlocking {
    val smallerId = "risk_assessment_1"
    val largerId = "risk_assessment_2"
    database.insert(
      riskAssessment(id = smallerId, probability = BigDecimal("0.3")),
      riskAssessment(id = largerId, probability = BigDecimal("0.30000000001"))
    )

    val results =
      database.search<RiskAssessment>(
        Search(ResourceType.RiskAssessment)
          .apply { sort(RiskAssessment.PROBABILITY, Order.DESCENDING) }
          .getQuery()
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
          }
        )
    }

  @Test
  fun search_string_default() {
    val patient =
      Patient().apply {
        id = "test_1"
        addName(HumanName().addGiven("Evelyn"))
      }
    val res = runBlocking {
      database.insert(patient)
      database.search<Patient>(
        Search(ResourceType.Patient).apply { filter(Patient.GIVEN) { value = "eve" } }.getQuery()
      )
    }

    assertThat(res).hasSize(1)
    assertThat(res[0].id).isEqualTo("Patient/${patient.id}")
  }

  @Test
  fun search_string_default_no_match() {
    val patient =
      Patient().apply {
        id = "test_1"
        addName(HumanName().addGiven("Severine"))
      }
    val res = runBlocking {
      database.insert(patient)
      database.search<Patient>(
        Search(ResourceType.Patient).apply { filter(Patient.GIVEN) { value = "eve" } }.getQuery()
      )
    }

    assertThat(res).hasSize(0)
  }

  @Test
  fun search_string_exact() {
    val patient =
      Patient().apply {
        id = "test_1"
        addName(HumanName().addGiven("Eve"))
      }
    val res = runBlocking {
      database.insert(patient)
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(Patient.GIVEN) {
              value = "Eve"
              modifier = StringFilterModifier.MATCHES_EXACTLY
            }
          }
          .getQuery()
      )
    }

    assertThat(res).hasSize(1)
    assertThat(res[0].id).isEqualTo("Patient/${patient.id}")
  }
  @Test
  fun search_string_exact_no_match() {
    val patient =
      Patient().apply {
        id = "test_1"
        addName(HumanName().addGiven("EVE"))
      }
    val res = runBlocking {
      database.insert(patient)
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(Patient.GIVEN) {
              value = "Eve"
              modifier = StringFilterModifier.MATCHES_EXACTLY
            }
          }
          .getQuery()
      )
    }

    assertThat(res).hasSize(0)
  }

  @Test
  fun search_string_contains() {
    val patient =
      Patient().apply {
        id = "test_1"
        addName(HumanName().addGiven("Severine"))
      }

    val res = runBlocking {
      database.insert(patient)
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(Patient.GIVEN) {
              value = "Eve"
              modifier = StringFilterModifier.CONTAINS
            }
          }
          .getQuery()
      )
    }

    assertThat(res).hasSize(1)
    assertThat(res[0].id).isEqualTo("Patient/${patient.id}")
  }

  @Test
  fun search_string_contains_no_match() {
    val patient =
      Patient().apply {
        id = "test_1"
        addName(HumanName().addGiven("John"))
      }
    val res = runBlocking {
      database.insert(patient)
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(Patient.GIVEN) {
              value = "eve"
              modifier = StringFilterModifier.CONTAINS
            }
          }
          .getQuery()
      )
    }

    assertThat(res).hasSize(0)
  }

  @Test
  fun search_date_starts_after() {
    val patient1 =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2013-03-25")
      }
    val patient2 =
      Patient().apply {
        id = "2"
        deceased = DateTimeType("2013-03-10")
      }
    val patient3 =
      Patient().apply {
        id = "3"
        deceased = DateTimeType("2013-03-03")
      }
    val patient4 =
      Patient().apply {
        id = "4"
        deceased = DateTimeType("2013-03-23T10:00:00")
      }
    val res = runBlocking {
      database.insert(patient1, patient2, patient3, patient4)
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(Patient.DEATH_DATE) {
              prefix = ParamPrefixEnum.STARTS_AFTER
              value = DateTimeType("2013-03-14")
            }
          }
          .getQuery()
      )
    }
    assertThat(res).hasSize(2)
    assertThat(
        res.all { it.deceasedDateTimeType.value.time >= DateTimeType("2013-03-14").value.time }
      )
      .isTrue()
  }
  @Test
  fun search_date_ends_before() {
    val patient1 =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2013-03-25")
      }
    val patient2 =
      Patient().apply {
        id = "2"
        deceased = DateTimeType("2013-03-10")
      }
    val patient3 =
      Patient().apply {
        id = "3"
        deceased = DateTimeType("2013-03-03")
      }
    val patient4 =
      Patient().apply {
        id = "4"
        deceased = DateTimeType("2013-03-23T10:00:00")
      }
    val res = runBlocking {
      database.insert(patient1, patient2, patient3, patient4)
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(Patient.DEATH_DATE) {
              prefix = ParamPrefixEnum.ENDS_BEFORE
              value = DateTimeType("2013-03-14")
            }
          }
          .getQuery()
      )
    }
    assertThat(res).hasSize(2)
    assertThat(
        res.all { it.deceasedDateTimeType.value.time <= DateTimeType("2013-03-14").value.time }
      )
      .isTrue()
  }

  @Test
  fun search_date_not_equals() {
    val patient1 =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2013-03-25")
      }
    val patient2 =
      Patient().apply {
        id = "2"
        deceased = DateTimeType("2013-03-14")
      }
    val patient3 =
      Patient().apply {
        id = "3"
        deceased = DateTimeType("2013-03-15")
      }
    val patient4 =
      Patient().apply {
        id = "4"
        deceased = DateTimeType("2013-03-14T23:59:59.999")
      }
    val res = runBlocking {
      database.insert(patient1, patient2, patient3, patient4)
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(Patient.DEATH_DATE) {
              prefix = ParamPrefixEnum.NOT_EQUAL
              value = DateTimeType("2013-03-14")
            }
          }
          .getQuery()
      )
    }
    assertThat(res).hasSize(2)
    assertThat(
        res.all {
          it.deceasedDateTimeType.value.time >= DateTimeType("2013-03-15").value.time ||
            it.deceasedDateTimeType.value.time < DateTimeType("2013-03-14").value.time
        }
      )
      .isTrue()
  }

  @Test
  fun search_date_equals() {
    val patient1 =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2013-03-25")
      }
    val patient2 =
      Patient().apply {
        id = "2"
        deceased = DateTimeType("2013-03-14")
      }
    val patient3 =
      Patient().apply {
        id = "3"
        deceased = DateTimeType("2013-03-15")
      }
    val patient4 =
      Patient().apply {
        id = "4"
        deceased = DateTimeType("2013-03-14T23:59:59.999")
      }
    val res = runBlocking {
      database.insert(patient1, patient2, patient3, patient4)
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(Patient.DEATH_DATE) {
              prefix = ParamPrefixEnum.EQUAL
              value = DateTimeType("2013-03-14")
            }
          }
          .getQuery()
      )
    }
    assertThat(res).hasSize(2)
    assertThat(
        res.all {
          it.deceasedDateTimeType.value.time < DateTimeType("2013-03-15").value.time &&
            it.deceasedDateTimeType.value.time < DateTimeType("2013-03-15").value.time
        }
      )
      .isTrue()
  }
  @Test
  fun search_date_greater() {
    val patient1 =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2013-03-13")
      }
    val patient2 =
      Patient().apply {
        id = "2"
        deceased = DateTimeType("2013-03-14")
      }
    val patient3 =
      Patient().apply {
        id = "3"
        deceased = DateTimeType("2013-03-15")
      }
    val patient4 =
      Patient().apply {
        id = "4"
        deceased = DateTimeType("2013-03-14T23:59:59.999")
      }
    val res = runBlocking {
      database.insert(patient1, patient2, patient3, patient4)
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(Patient.DEATH_DATE) {
              prefix = ParamPrefixEnum.GREATERTHAN
              value = DateTimeType("2013-03-14")
            }
          }
          .getQuery()
      )
    }
    assertThat(res).hasSize(1)
    assertThat(
        res.all { it.deceasedDateTimeType.value.time >= DateTimeType("2013-03-15").value.time }
      )
      .isTrue()
  }
  @Test
  fun search_date_greater_or_equal() {
    val patient1 =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2013-03-13")
      }
    val patient2 =
      Patient().apply {
        id = "2"
        deceased = DateTimeType("2013-03-14")
      }
    val patient3 =
      Patient().apply {
        id = "3"
        deceased = DateTimeType("2013-03-15")
      }
    val patient4 =
      Patient().apply {
        id = "4"
        deceased = DateTimeType("2013-03-14T23:59:59.999")
      }
    val res = runBlocking {
      database.insert(patient1, patient2, patient3, patient4)
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(Patient.DEATH_DATE) {
              prefix = ParamPrefixEnum.GREATERTHAN_OR_EQUALS
              value = DateTimeType("2013-03-14")
            }
          }
          .getQuery()
      )
    }
    assertThat(res).hasSize(3)
    assertThat(
        res.all { it.deceasedDateTimeType.value.time >= DateTimeType("2013-03-14").value.time }
      )
      .isTrue()
  }
  @Test
  fun search_date_less() {
    val patient1 =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2013-03-13")
      }
    val patient2 =
      Patient().apply {
        id = "2"
        deceased = DateTimeType("2013-03-14")
      }
    val patient3 =
      Patient().apply {
        id = "3"
        deceased = DateTimeType("2013-03-15")
      }
    val patient4 =
      Patient().apply {
        id = "4"
        deceased = DateTimeType("2013-03-14T23:59:59.999")
      }
    val res = runBlocking {
      database.insert(patient1, patient2, patient3, patient4)
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(Patient.DEATH_DATE) {
              prefix = ParamPrefixEnum.LESSTHAN
              value = DateTimeType("2013-03-14")
            }
          }
          .getQuery()
      )
    }
    assertThat(res).hasSize(1)
    assertThat(
        res.all { it.deceasedDateTimeType.value.time < DateTimeType("2013-03-14").value.time }
      )
      .isTrue()
  }

  @Test
  fun search_date_less_or_equal() {
    val patient1 =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2013-03-13")
      }
    val patient2 =
      Patient().apply {
        id = "2"
        deceased = DateTimeType("2013-03-14")
      }
    val patient3 =
      Patient().apply {
        id = "3"
        deceased = DateTimeType("2013-03-15")
      }
    val patient4 =
      Patient().apply {
        id = "4"
        deceased = DateTimeType("2013-03-14T23:59:59.999")
      }
    val res = runBlocking {
      database.insert(patient1, patient2, patient3, patient4)
      database.search<Patient>(
        Search(ResourceType.Patient)
          .apply {
            filter(Patient.DEATH_DATE) {
              prefix = ParamPrefixEnum.LESSTHAN_OR_EQUALS
              value = DateTimeType("2013-03-14")
            }
          }
          .getQuery()
      )
    }
    assertThat(res).hasSize(3)
    assertThat(
        res.all { it.deceasedDateTimeType.value.time <= DateTimeType("2013-03-15").value.time }
      )
      .isTrue()
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
