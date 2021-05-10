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

package com.google.android.fhir.search

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import com.google.android.fhir.FhirServices
import com.google.android.fhir.sync.FhirDataSource
import com.google.common.truth.Truth
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MoreSearchTest {
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
    FhirServices.builder(dataSource, ApplicationProvider.getApplicationContext()).inMemory().build()
  private val database = services.database

  @Test
  fun search_date_approximate() {
    val patient1 =
      Patient().apply {
        id = "1"
        deceased = DateTimeType("2013-03-13")
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
              prefix = ParamPrefixEnum.APPROXIMATE
              value = DateTimeType("2013-03-14")
            }
          }
          .getQuery()
      )
    }
    Truth.assertThat(res).hasSize(3)
    Truth.assertThat(
        res.all {
          it.deceasedDateTimeType.value.time >= DateTimeType("2013-03-04").value.time &&
            it.deceasedDateTimeType.value.time <= DateTimeType("2013-03-24").value.time
        }
      )
      .isTrue()
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
    Truth.assertThat(res).hasSize(2)
    Truth.assertThat(
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
    Truth.assertThat(res).hasSize(2)
    Truth.assertThat(
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
    Truth.assertThat(res).hasSize(2)
    Truth.assertThat(
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
    Truth.assertThat(res).hasSize(2)
    Truth.assertThat(
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
    Truth.assertThat(res).hasSize(1)
    Truth.assertThat(
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
    Truth.assertThat(res).hasSize(3)
    Truth.assertThat(
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
    Truth.assertThat(res).hasSize(1)
    Truth.assertThat(
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
    Truth.assertThat(res).hasSize(3)
    Truth.assertThat(
        res.all { it.deceasedDateTimeType.value.time <= DateTimeType("2013-03-15").value.time }
      )
      .isTrue()
  }
}
