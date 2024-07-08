/*
 * Copyright 2023-2024 Google LLC
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

import com.google.android.fhir.sync.download.DownloadState
import com.google.android.fhir.sync.download.Downloader
import com.google.android.fhir.sync.upload.UploadRequestResult
import com.google.android.fhir.sync.upload.UploadStrategy
import com.google.android.fhir.sync.upload.Uploader
import com.google.android.fhir.testing.TestFhirEngineImpl
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class FhirSynchronizerTest {

  @Mock private lateinit var uploader: Uploader

  @Mock private lateinit var downloader: Downloader

  @Mock private lateinit var conflictResolver: ConflictResolver

  @Mock private lateinit var fhirDataStore: FhirDataStore

  private lateinit var fhirSynchronizer: FhirSynchronizer

  @Before
  fun setUp() {
    MockitoAnnotations.openMocks(this)
    fhirSynchronizer =
      FhirSynchronizer(
        TestFhirEngineImpl,
        UploadConfiguration(uploader, UploadStrategy.AllChangesSquashedBundlePut),
        DownloadConfiguration(downloader, conflictResolver),
        fhirDataStore,
      )
  }

  @Test
  fun `synchronize should return Success on successful download and upload`() =
    runTest(UnconfinedTestDispatcher()) {
      `when`(downloader.download()).thenReturn(flowOf(DownloadState.Success(listOf(), 10, 10)))
      `when`(uploader.upload(any()))
        .thenReturn(
          flowOf(UploadRequestResult.Success(listOf())),
        )

      val emittedValues = mutableListOf<SyncJobStatus>()
      backgroundScope.launch { fhirSynchronizer.syncState.collect { emittedValues.add(it) } }

      val result = fhirSynchronizer.synchronize()

      assertThat(emittedValues[0]).isInstanceOf(SyncJobStatus.Started::class.java)
      assertThat(emittedValues[1])
        .isEqualTo(SyncJobStatus.InProgress(SyncOperation.DOWNLOAD, total = 10, completed = 10))
      assertThat(emittedValues[2])
        .isEqualTo(SyncJobStatus.InProgress(SyncOperation.UPLOAD, total = 1, completed = 0))
      assertThat(emittedValues[3])
        .isEqualTo(SyncJobStatus.InProgress(SyncOperation.UPLOAD, total = 1, completed = 1))
      assertThat(emittedValues[4]).isInstanceOf(SyncJobStatus.Succeeded::class.java)

      assertThat(SyncJobStatus.Succeeded::class.java).isEqualTo(result::class.java)
    }

  @Test
  fun `synchronize should return Failed on failed download`() =
    runTest(UnconfinedTestDispatcher()) {
      val error = ResourceSyncException(ResourceType.Patient, Exception("Download error"))
      `when`(downloader.download()).thenReturn(flowOf(DownloadState.Failure(error)))
      `when`(uploader.upload(any()))
        .thenReturn(
          flowOf(UploadRequestResult.Success(listOf())),
        )

      val emittedValues = mutableListOf<SyncJobStatus>()
      backgroundScope.launch { fhirSynchronizer.syncState.collect { emittedValues.add(it) } }

      val result = fhirSynchronizer.synchronize()

      assertThat(emittedValues[0]).isInstanceOf(SyncJobStatus.Started::class.java)
      assertThat(emittedValues[1])
        .isEqualTo(SyncJobStatus.InProgress(SyncOperation.UPLOAD, total = 1, completed = 0))
      assertThat(emittedValues[2])
        .isEqualTo(SyncJobStatus.InProgress(SyncOperation.UPLOAD, total = 1, completed = 1))
      assertThat(emittedValues[3]).isEqualTo(SyncJobStatus.Failed(exceptions = listOf(error)))
      assertThat(result).isInstanceOf(SyncJobStatus.Failed::class.java)
      assertThat(listOf(error)).isEqualTo((result as SyncJobStatus.Failed).exceptions)
    }

  @Test
  fun `synchronize should return Failed on failed upload`() =
    runTest(UnconfinedTestDispatcher()) {
      `when`(downloader.download()).thenReturn(flowOf(DownloadState.Success(listOf(), 10, 10)))
      val error = ResourceSyncException(ResourceType.Patient, Exception("Upload error"))
      `when`(uploader.upload(any()))
        .thenReturn(flowOf(UploadRequestResult.Failure(listOf(), error)))

      val emittedValues = mutableListOf<SyncJobStatus>()
      backgroundScope.launch { fhirSynchronizer.syncState.collect { emittedValues.add(it) } }

      val result = fhirSynchronizer.synchronize()

      assertThat(emittedValues[0]).isInstanceOf(SyncJobStatus.Started::class.java)
      assertThat(emittedValues[1])
        .isEqualTo(SyncJobStatus.InProgress(SyncOperation.DOWNLOAD, total = 10, completed = 10))
      assertThat(emittedValues[2])
        .isEqualTo(SyncJobStatus.InProgress(SyncOperation.UPLOAD, total = 1, completed = 0))
      assertThat(emittedValues[3]).isEqualTo(SyncJobStatus.Failed(exceptions = listOf(error)))
      assertThat(result).isInstanceOf(SyncJobStatus.Failed::class.java)
      assertThat(listOf(error)).isEqualTo((result as SyncJobStatus.Failed).exceptions)
    }

  /**
   * If you encounter flakiness in this test, consider increasing the delay time in the downloader
   * object.
   */
  @Test
  fun `synchronize multiple invocations should execute in order`() =
    runTest(UnconfinedTestDispatcher()) {
      `when`(downloader.download()).thenReturn(flowOf(DownloadState.Success(listOf(), 0, 0)))
      `when`(uploader.upload(any()))
        .thenReturn(
          flowOf(
            UploadRequestResult.Success(
              listOf(),
            ),
          ),
        )
      val fhirSynchronizerWithDelayInDownload =
        FhirSynchronizer(
          TestFhirEngineImpl,
          UploadConfiguration(uploader, UploadStrategy.AllChangesSquashedBundlePut),
          DownloadConfiguration(
            object : Downloader {
              override suspend fun download(): Flow<DownloadState> {
                delay(10)
                return flowOf(DownloadState.Success(listOf(), 10, 10))
              }
            },
            conflictResolver,
          ),
          fhirDataStore,
        )
      val emittedValues = mutableListOf<SyncJobStatus>()
      val jobs =
        listOf(fhirSynchronizerWithDelayInDownload, fhirSynchronizer).map {
          backgroundScope.launch { it.syncState.collect { emittedValues.add(it) } }
          /**
           * Invoke synchronize() in separate coroutines. First invoke the synchronizer with delay
           * in download. Then the [fhirSynchronizer]
           */
          backgroundScope.launch { it.synchronize() }
        }

      jobs.forEach { it.join() }

      assertThat(emittedValues).hasSize(10)
      assertThat(emittedValues[0]).isInstanceOf(SyncJobStatus.Started::class.java)
      assertThat(emittedValues[1])
        .isEqualTo(SyncJobStatus.InProgress(SyncOperation.DOWNLOAD, total = 10, completed = 10))
      assertThat(emittedValues[2])
        .isEqualTo(SyncJobStatus.InProgress(SyncOperation.UPLOAD, total = 1, completed = 0))
      assertThat(emittedValues[3])
        .isEqualTo(SyncJobStatus.InProgress(SyncOperation.UPLOAD, total = 1, completed = 1))
      assertThat(emittedValues[4]).isInstanceOf(SyncJobStatus.Succeeded::class.java)
      assertThat(emittedValues[5]).isInstanceOf(SyncJobStatus.Started::class.java)
      assertThat(emittedValues[6])
        .isEqualTo(SyncJobStatus.InProgress(SyncOperation.DOWNLOAD, total = 0, completed = 0))
      assertThat(emittedValues[7])
        .isEqualTo(SyncJobStatus.InProgress(SyncOperation.UPLOAD, total = 1, completed = 0))
      assertThat(emittedValues[8])
        .isEqualTo(SyncJobStatus.InProgress(SyncOperation.UPLOAD, total = 1, completed = 1))
      assertThat(emittedValues[9]).isInstanceOf(SyncJobStatus.Succeeded::class.java)
    }
}
