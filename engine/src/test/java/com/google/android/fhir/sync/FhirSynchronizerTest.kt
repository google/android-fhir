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

package com.google.android.fhir.sync

import androidx.test.core.app.ApplicationProvider
import com.google.android.fhir.LocalChangeToken
import com.google.android.fhir.sync.download.DownloadState
import com.google.android.fhir.sync.download.Downloader
import com.google.android.fhir.sync.upload.UploadState
import com.google.android.fhir.sync.upload.Uploader
import com.google.android.fhir.testing.TestFhirEngineImpl
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FhirSynchronizerTest {

  @Mock private lateinit var uploader: Uploader

  @Mock private lateinit var downloader: Downloader

  @Mock private lateinit var conflictResolver: ConflictResolver

  private lateinit var fhirSynchronizer: FhirSynchronizer

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    fhirSynchronizer =
      FhirSynchronizer(
        ApplicationProvider.getApplicationContext(),
        TestFhirEngineImpl,
        uploader,
        downloader,
        conflictResolver,
      )
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `synchronize should return Success on successful download and upload`() =
    runTest(UnconfinedTestDispatcher()) {
      `when`(downloader.download()).thenReturn(flowOf(DownloadState.Success(listOf(), 10, 10)))
      `when`(uploader.upload(any()))
        .thenReturn(
          flowOf(
            UploadState.Success(
              LocalChangeToken(listOf()),
              Patient(),
              1,
              1,
            ),
          ),
        )

      val emittedValues = mutableListOf<SyncJobStatus>()
      backgroundScope.launch { fhirSynchronizer.syncState.collect { emittedValues.add(it) } }

      val result = fhirSynchronizer.synchronize()

      assertThat(emittedValues)
        .containsExactly(
          SyncJobStatus.Started,
          SyncJobStatus.InProgress(SyncOperation.DOWNLOAD, total = 10, completed = 10),
          SyncJobStatus.InProgress(SyncOperation.UPLOAD, total = 1, completed = 1),
          SyncJobStatus.Finished,
        )

      assertThat(SyncJobStatus.Finished::class.java).isEqualTo(result::class.java)
    }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `synchronize should return Failed on failed download`() =
    runTest(UnconfinedTestDispatcher()) {
      val error = ResourceSyncException(ResourceType.Patient, Exception("Download error"))
      `when`(downloader.download()).thenReturn(flowOf(DownloadState.Failure(error)))
      `when`(uploader.upload(any()))
        .thenReturn(
          flowOf(
            UploadState.Success(
              LocalChangeToken(listOf()),
              Patient(),
              1,
              1,
            ),
          ),
        )

      val emittedValues = mutableListOf<SyncJobStatus>()
      backgroundScope.launch { fhirSynchronizer.syncState.collect { emittedValues.add(it) } }

      val result = fhirSynchronizer.synchronize()

      assertThat(emittedValues)
        .containsExactly(
          SyncJobStatus.Started,
          SyncJobStatus.InProgress(SyncOperation.UPLOAD, total = 1, completed = 1),
          SyncJobStatus.Failed(exceptions = listOf(error)),
        )
      assertThat(result).isInstanceOf(SyncJobStatus.Failed::class.java)
      assertThat(listOf(error)).isEqualTo((result as SyncJobStatus.Failed).exceptions)
    }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `synchronize should return Failed on failed upload`() =
    runTest(UnconfinedTestDispatcher()) {
      `when`(downloader.download()).thenReturn(flowOf(DownloadState.Success(listOf(), 10, 10)))
      val error = ResourceSyncException(ResourceType.Patient, Exception("Upload error"))
      `when`(uploader.upload(any()))
        .thenReturn(
          flowOf(UploadState.Failure(error)),
        )

      val emittedValues = mutableListOf<SyncJobStatus>()
      backgroundScope.launch { fhirSynchronizer.syncState.collect { emittedValues.add(it) } }

      val result = fhirSynchronizer.synchronize()

      assertThat(emittedValues)
        .containsExactly(
          SyncJobStatus.Started,
          SyncJobStatus.InProgress(SyncOperation.DOWNLOAD, total = 10, completed = 10),
          SyncJobStatus.Failed(exceptions = listOf(error)),
        )
      assertThat(result).isInstanceOf(SyncJobStatus.Failed::class.java)
      assertThat(listOf(error)).isEqualTo((result as SyncJobStatus.Failed).exceptions)
    }
}
