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
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.testing.TestDataSourceImpl
import com.google.android.fhir.testing.TestDownloadManagerImpl
import com.google.android.fhir.testing.TestFailingDatasource
import com.google.android.fhir.testing.TestFhirEngineImpl
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class FhirSyncWorkerTest {
  private lateinit var context: Context
  class PassingPeriodicSyncWorker(appContext: Context, workerParams: WorkerParameters) :
    FhirSyncWorker(appContext, workerParams) {

    override fun getFhirEngine(): FhirEngine = TestFhirEngineImpl
    override fun getDataSource(): DataSource = TestDataSourceImpl
    override fun getDownloadWorkManager(): DownloadWorkManager = TestDownloadManagerImpl()
    override fun getConflictResolver() = AcceptRemoteConflictResolver
  }

  class FailingPeriodicSyncWorker(appContext: Context, workerParams: WorkerParameters) :
    FhirSyncWorker(appContext, workerParams) {

    override fun getFhirEngine(): FhirEngine = TestFhirEngineImpl
    override fun getDataSource(): DataSource = TestFailingDatasource
    override fun getDownloadWorkManager(): DownloadWorkManager = TestDownloadManagerImpl()
    override fun getConflictResolver() = AcceptRemoteConflictResolver
  }

  class FailingPeriodicSyncWorkerWithoutDataSource(
    appContext: Context,
    workerParams: WorkerParameters
  ) : FhirSyncWorker(appContext, workerParams) {

    override fun getFhirEngine(): FhirEngine = TestFhirEngineImpl
    override fun getDownloadWorkManager() = TestDownloadManagerImpl()
    override fun getDataSource(): DataSource? = null
    override fun getConflictResolver() = AcceptRemoteConflictResolver
  }

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
  }

  @Test
  fun fhirSyncWorker_successfulTask_resultSuccess() {
    // In a ListenableWorker, runAttemptCount starts from 0. But in a TestListenableWorkerBuilder,
    // its set to start from 1. So overriding the default value when required in the test case.
    val worker =
      TestListenableWorkerBuilder<PassingPeriodicSyncWorker>(
          context,
          inputData = Data.Builder().putInt(MAX_RETRIES_ALLOWED, 1).build(),
          runAttemptCount = 0
        )
        .build()
    val result = runBlocking { worker.doWork() }
    assertThat(result).isInstanceOf(ListenableWorker.Result.success()::class.java)
  }

  @Test
  fun fhirSyncWorker_failedTaskWithZeroRetires_resultShouldBeFail() {
    val worker =
      TestListenableWorkerBuilder<FailingPeriodicSyncWorker>(
          context,
          inputData = Data.Builder().putInt(MAX_RETRIES_ALLOWED, 0).build(),
          runAttemptCount = 0
        )
        .build()
    val result = runBlocking { worker.doWork() }
    assertThat(result).isInstanceOf(ListenableWorker.Result.failure()::class.java)
  }

  @Test
  fun fhirSyncWorker_failedTaskWithCurrentRunAttemptSameAsTheReties_resultShouldBeFail() {
    val worker =
      TestListenableWorkerBuilder<FailingPeriodicSyncWorker>(
          context,
          inputData = Data.Builder().putInt(MAX_RETRIES_ALLOWED, 2).build(),
          runAttemptCount = 2
        )
        .build()
    val result = runBlocking { worker.doWork() }
    assertThat(result).isInstanceOf(ListenableWorker.Result.failure()::class.java)
  }

  @Test
  fun fhirSyncWorker_failedTaskWithCurrentRunAttemptSmallerThanTheReties_resultShouldBeRetry() {
    val worker =
      TestListenableWorkerBuilder<FailingPeriodicSyncWorker>(
          context,
          inputData = Data.Builder().putInt(MAX_RETRIES_ALLOWED, 2).build(),
          runAttemptCount = 1
        )
        .build()
    val result = runBlocking { worker.doWork() }
    assertThat(result).isEqualTo(ListenableWorker.Result.retry())
  }

  @Test
  fun fhirSyncWorker_nullDataSource_resultShouldBeFail() {
    val worker =
      TestListenableWorkerBuilder<FailingPeriodicSyncWorkerWithoutDataSource>(
          context,
          inputData = Data.Builder().putInt(MAX_RETRIES_ALLOWED, 2).build(),
          runAttemptCount = 2
        )
        .build()
    val result = runBlocking { worker.doWork() }
    assertThat(result).isInstanceOf(ListenableWorker.Result.failure()::class.java)
    assertThat((result as ListenableWorker.Result.Failure).outputData).isNotNull()
    assertThat(result.outputData.keyValueMap)
      .containsEntry("error", "java.lang.IllegalStateException")
  }
}
