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

package com.google.android.fhir.sync.upload

import androidx.test.core.app.ApplicationProvider
import com.google.android.fhir.FhirServices
import com.google.common.truth.Truth.assertThat
import java.util.Date
import kotlinx.coroutines.test.runTest
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.Meta
import org.hl7.fhir.r4.model.Patient
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AllChangesLocalChangeFetcherTest {
  private val services =
    FhirServices.builder(ApplicationProvider.getApplicationContext()).inMemory().build()
  private val database = services.database
  private lateinit var fetcher: AllChangesLocalChangeFetcher

  @Before
  fun setup() = runTest {
    database.insert(TEST_PATIENT_1, TEST_PATIENT_2)
    fetcher = AllChangesLocalChangeFetcher(database).apply { initTotalCount() }
  }

  @Test
  fun `next returns all the localChanges`() = runTest {
    val localChanges = fetcher.next()
    assertThat(fetcher.next().size).isEqualTo(2)
    assertThat(localChanges.map { it.resourceId })
      .containsExactly(TEST_PATIENT_1_ID, TEST_PATIENT_2_ID)
  }

  @Test
  fun `hasNext returns true when there are local changes`() = runTest {
    assertThat(fetcher.hasNext()).isTrue()
  }

  @Test
  fun `hasNext returns false when there are no local changes`() = runTest {
    database.deleteUpdates(listOf(TEST_PATIENT_1, TEST_PATIENT_2))
    assertThat(fetcher.hasNext()).isFalse()
  }

  @Test
  fun `getProgress when all local changes are removed`() = runTest {
    database.deleteUpdates(listOf(TEST_PATIENT_1, TEST_PATIENT_2))
    assertThat(fetcher.getProgress()).isEqualTo(SyncUploadProgress(0, 2))
  }

  @Test
  fun `getProgress when half the local changes are removed`() = runTest {
    database.deleteUpdates(listOf(TEST_PATIENT_1))
    assertThat(fetcher.getProgress()).isEqualTo(SyncUploadProgress(1, 2))
  }

  @Test
  fun `getProgress when none of the local changes are removed`() = runTest {
    assertThat(fetcher.getProgress()).isEqualTo(SyncUploadProgress(2, 2))
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
