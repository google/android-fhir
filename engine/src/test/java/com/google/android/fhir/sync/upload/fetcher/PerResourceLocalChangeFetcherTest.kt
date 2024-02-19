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

package com.google.android.fhir.sync.upload.fetcher

import androidx.test.core.app.ApplicationProvider
import com.google.android.fhir.FhirServices
import com.google.android.fhir.LocalChange
import com.google.android.fhir.logicalId
import com.google.android.fhir.sync.upload.PerResourceLocalChangeFetcher
import com.google.common.truth.Truth.assertThat
import java.util.Date
import kotlinx.coroutines.test.runTest
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Meta
import org.hl7.fhir.r4.model.Patient
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PerResourceLocalChangeFetcherTest {

  private val services =
    FhirServices.builder(ApplicationProvider.getApplicationContext()).inMemory().build()
  private val database = services.database

  @Test
  fun `fetcher is created correctly`() = runTest {
    database.insert(TEST_PATIENT_1, TEST_PATIENT_2)
    database.update(
      TEST_PATIENT_1.copy().apply { gender = Enumerations.AdministrativeGender.FEMALE },
    )
    val fetcher = PerResourceLocalChangeFetcher(database).apply { initTotalCount() }

    assertThat(fetcher.getProgress().initialTotal).isEqualTo(3)
  }

  @Test
  fun `hasNext returns correct value`() = runTest {
    database.insert(TEST_PATIENT_1, TEST_PATIENT_2)
    database.update(
      TEST_PATIENT_1.copy().apply { gender = Enumerations.AdministrativeGender.FEMALE },
    )
    val fetcher = PerResourceLocalChangeFetcher(database).apply { initTotalCount() }

    assertThat(fetcher.hasNext()).isTrue()
    database.deleteUpdates(listOf(TEST_PATIENT_1))
    assertThat(fetcher.hasNext()).isTrue()
    database.deleteUpdates(listOf(TEST_PATIENT_2))
    assertThat(fetcher.hasNext()).isFalse()
  }

  @Test
  fun `next returns correct set of changes in the right order`() = runTest {
    database.insert(TEST_PATIENT_1, TEST_PATIENT_2)
    database.update(
      TEST_PATIENT_1.copy().apply { gender = Enumerations.AdministrativeGender.FEMALE },
    )
    val fetcher = PerResourceLocalChangeFetcher(database).apply { initTotalCount() }

    val firstSetOfChanges = fetcher.next()
    database.deleteUpdates(listOf(TEST_PATIENT_1))
    val secondSetOfChanges = fetcher.next()
    database.deleteUpdates(listOf(TEST_PATIENT_2))

    assertThat(firstSetOfChanges.size).isEqualTo(2)
    with(firstSetOfChanges[0]) {
      assertThat(type).isEqualTo(LocalChange.Type.INSERT)
      assertThat(resourceId).isEqualTo(TEST_PATIENT_1.logicalId)
    }

    with(firstSetOfChanges[1]) {
      assertThat(type).isEqualTo(LocalChange.Type.UPDATE)
      assertThat(resourceId).isEqualTo(TEST_PATIENT_1.logicalId)
    }

    assertThat(secondSetOfChanges.size).isEqualTo(1)
    with(secondSetOfChanges[0]) {
      assertThat(type).isEqualTo(LocalChange.Type.INSERT)
      assertThat(resourceId).isEqualTo(TEST_PATIENT_2.logicalId)
    }
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
