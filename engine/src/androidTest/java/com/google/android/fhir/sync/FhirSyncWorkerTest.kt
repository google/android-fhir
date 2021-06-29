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

package com.google.android.fhir.sync

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.google.android.fhir.FhirEngine
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockkConstructor
import io.mockk.unmockkConstructor
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.P)
class FhirSyncWorkerTest {
  private lateinit var context: Context
  class MockedPeriodicSyncWorker(appContext: Context, workerParams: WorkerParameters) :
    FhirSyncWorker(appContext, workerParams) {
    init {
      MockKAnnotations.init(this)
    }
    @MockK lateinit var engine: FhirEngine
    @MockK lateinit var source: DataSource
    override fun getFhirEngine(): FhirEngine = engine
    override fun getDataSource(): DataSource = source
    override fun getSyncData(): ResourceSyncParams = mapOf()
  }

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    mockkConstructor(FhirSynchronizer::class)
  }

  @After
  fun tearDown() {
    unmockkConstructor(FhirSynchronizer::class)
  }

  @Test
  fun fhirSyncWorker_successfulTask_resultSuccess() {
    // In a ListenableWorker, runAttemptCount starts from 0. But in a TestListenableWorkerBuilder,
    // its set to start from 1. So overriding the default value when required in the test case.
    val worker =
      TestListenableWorkerBuilder<MockedPeriodicSyncWorker>(
          context,
          inputData = Data.Builder().putInt(MAX_RETRIES_ALLOWED, 1).build(),
          runAttemptCount = 0
        )
        .build()
    coEvery { anyConstructed<FhirSynchronizer>().synchronize() } returns Result.Success
    val result = runBlocking { worker.doWork() }
    assertThat(result).isEqualTo(ListenableWorker.Result.success())
  }

  @Test
  fun fhirSyncWorker_failedTaskWithZeroRetires_resultShouldBeFail() {
    val worker =
      TestListenableWorkerBuilder<MockedPeriodicSyncWorker>(
          context,
          inputData = Data.Builder().putInt(MAX_RETRIES_ALLOWED, 0).build(),
          runAttemptCount = 0
        )
        .build()
    coEvery { anyConstructed<FhirSynchronizer>().synchronize() } returns Result.Error(listOf())
    val result = runBlocking { worker.doWork() }
    assertThat(result).isEqualTo(ListenableWorker.Result.failure())
  }

  @Test
  fun fhirSyncWorker_failedTaskWithCurrentRunAttemptSameAsTheReties_resultShouldBeFail() {
    val worker =
      TestListenableWorkerBuilder<MockedPeriodicSyncWorker>(
          context,
          inputData = Data.Builder().putInt(MAX_RETRIES_ALLOWED, 2).build(),
          runAttemptCount = 2
        )
        .build()
    coEvery { anyConstructed<FhirSynchronizer>().synchronize() } returns Result.Error(listOf())
    val result = runBlocking { worker.doWork() }
    assertThat(result).isEqualTo(ListenableWorker.Result.failure())
  }

  @Test
  fun fhirSyncWorker_failedTaskWithCurrentRunAttemptSmallerThanTheReties_resultShouldBeRetry() {
    val worker =
      TestListenableWorkerBuilder<MockedPeriodicSyncWorker>(
          context,
          inputData = Data.Builder().putInt(MAX_RETRIES_ALLOWED, 2).build(),
          runAttemptCount = 1
        )
        .build()
    coEvery { anyConstructed<FhirSynchronizer>().synchronize() } returns Result.Error(listOf())
    val result = runBlocking { worker.doWork() }
    assertThat(result).isEqualTo(ListenableWorker.Result.retry())
  }
}
