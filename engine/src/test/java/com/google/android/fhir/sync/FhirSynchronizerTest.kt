/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir.sync

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.android.fhir.FhirServices
import com.google.android.fhir.resource.TestingUtils
import com.google.android.fhir.sync.upload.BundleUploader
import com.google.android.fhir.sync.upload.LocalChangesPaginator
import com.google.android.fhir.sync.upload.TransactionBundleGenerator
import com.google.common.truth.Truth.assertThat
import java.util.Date
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Meta
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class FhirSynchronizerTest {
  private val services =
    FhirServices.builder(ApplicationProvider.getApplicationContext()).inMemory().build()
  private val fhirEngine = services.fhirEngine
  private val bundleUploader =
    BundleUploader(
      dataSource = TestingUtils.TestDataSourceImpl,
      bundleGenerator = TransactionBundleGenerator.getDefault(),
      localChangesPaginator = LocalChangesPaginator.DEFAULT
    )

  private lateinit var context: Context

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
  }

  @Test
  fun `synchronize() should catch invalid resource downloaded`() = runTest {
    val flow = MutableSharedFlow<SyncJobStatus>()

    val testEncounterId = "encounter-1"
    val invalidEncounter =
      Encounter().apply {
        id = testEncounterId
        class_ = Coding()
        meta = Meta().apply { lastUpdated = Date() }
      }
    val fhirSynchronizer =
      FhirSynchronizer(
        context = context,
        fhirEngine = fhirEngine,
        uploader = bundleUploader,
        downloader =
          object : Downloader {
            override suspend fun download(): Flow<DownloadState> = flow {
              emit(DownloadState.Success(listOf(invalidEncounter), 1, 1))
            }
          },
        conflictResolver = AcceptLocalConflictResolver
      )

    val result = fhirSynchronizer.apply { subscribe(flow) }.synchronize()
    val exception = (result as SyncJobStatus.Failed).exceptions.firstOrNull()
    assertThat(exception!!.exception.localizedMessage)
      .isEqualTo("java.lang.NullPointerException: coding.code must not be null")
    assertThat(exception.resourceType).isEqualTo(ResourceType.Bundle)
  }

  @Test
  fun `synchronize() should sync successfully`() = runTest {
    val flow = MutableSharedFlow<SyncJobStatus>()

    val codeString = "1427AAAAA"
    val systemString = "http://openmrs.org/concepts"
    val encounter =
      Encounter().apply {
        id = "non-null-ID"
        class_ =
          Coding().apply {
            system = systemString
            code = codeString
            display = "Display"
          }
        meta = Meta().apply { lastUpdated = Date() }
      }
    val fhirSynchronizer =
      FhirSynchronizer(
        context = context,
        fhirEngine = fhirEngine,
        uploader = bundleUploader,
        downloader =
          object : Downloader {
            override suspend fun download(): Flow<DownloadState> = flow {
              emit(DownloadState.Success(listOf(encounter), 1, 1))
            }
          },
        conflictResolver = AcceptLocalConflictResolver
      )

    val result = fhirSynchronizer.apply { subscribe(flow) }.synchronize()
    assertThat(result).isInstanceOf(SyncJobStatus.Finished::class.java)
  }
}
