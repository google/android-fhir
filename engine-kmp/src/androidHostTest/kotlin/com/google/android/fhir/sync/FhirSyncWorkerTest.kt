/*
 * Copyright 2025-2026 Google LLC
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
import com.google.android.fhir.sync.upload.HttpCreateMethod
import com.google.android.fhir.sync.upload.HttpUpdateMethod
import com.google.android.fhir.sync.upload.UploadStrategy
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Adapted from engine/src/test/java/com/google/android/fhir/sync/FhirSyncWorkerTest.kt
 *
 * Tests the FhirSyncWorker integration with WorkManager to verify that sync operations
 * correctly return success, failure, or retry results based on the configuration.
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
internal class FhirSyncWorkerTest {
  private lateinit var context: Context

  class PassingPeriodicSyncWorker(appContext: Context, workerParams: WorkerParameters) :
    FhirSyncWorker(appContext, workerParams) {

    override fun getFhirEngine(): FhirEngine = TestFhirEngineImpl

    override fun getDataSource(): DataSource = TestDataSourceImpl

    override fun getDownloadWorkManager(): DownloadWorkManager = TestDownloadManagerImpl()

    override fun getConflictResolver() = AcceptRemoteConflictResolver

    override fun getUploadStrategy(): UploadStrategy =
      UploadStrategy.forBundleRequest(
        methodForCreate = HttpCreateMethod.PUT,
        methodForUpdate = HttpUpdateMethod.PATCH,
        squash = true,
        bundleSize = 500,
      )

    override fun getFhirDataStore(): FhirDataStore =
      FhirDataStore(createDataStore(applicationContext))
  }

  class FailingPeriodicSyncWorker(appContext: Context, workerParams: WorkerParameters) :
    FhirSyncWorker(appContext, workerParams) {

    override fun getFhirEngine(): FhirEngine = TestFhirEngineImpl

    override fun getDataSource(): DataSource = TestFailingDatasource

    override fun getDownloadWorkManager(): DownloadWorkManager = TestDownloadManagerImpl()

    override fun getConflictResolver() = AcceptRemoteConflictResolver

    override fun getUploadStrategy(): UploadStrategy =
      UploadStrategy.forBundleRequest(
        methodForCreate = HttpCreateMethod.PUT,
        methodForUpdate = HttpUpdateMethod.PATCH,
        squash = true,
        bundleSize = 500,
      )

    override fun getFhirDataStore(): FhirDataStore =
      FhirDataStore(createDataStore(applicationContext))
  }

  class FailingPeriodicSyncWorkerWithoutDataSource(
    appContext: Context,
    workerParams: WorkerParameters,
  ) : FhirSyncWorker(appContext, workerParams) {

    override fun getFhirEngine(): FhirEngine = TestFhirEngineImpl

    override fun getDownloadWorkManager() = TestDownloadManagerImpl()

    override fun getDataSource(): DataSource? = null

    override fun getConflictResolver() = AcceptRemoteConflictResolver

    override fun getUploadStrategy(): UploadStrategy =
      UploadStrategy.forBundleRequest(
        methodForCreate = HttpCreateMethod.PUT,
        methodForUpdate = HttpUpdateMethod.PATCH,
        squash = true,
        bundleSize = 500,
      )

    override fun getFhirDataStore(): FhirDataStore =
      FhirDataStore(createDataStore(applicationContext))
  }

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
  }

  @Test
  fun fhirSyncWorker_successfulTask_resultSuccess() {
    val worker =
      TestListenableWorkerBuilder<PassingPeriodicSyncWorker>(
          context,
          inputData = Data.Builder().putInt(MAX_RETRIES_ALLOWED, 1).build(),
          runAttemptCount = 0,
        )
        .build()
    val result = runBlocking { worker.doWork() }
    assertIs<ListenableWorker.Result>(result)
    assertEquals(ListenableWorker.Result.success()::class, result::class)
  }

  @Test
  fun fhirSyncWorker_failedTaskWithZeroRetries_resultShouldBeFail() {
    val worker =
      TestListenableWorkerBuilder<FailingPeriodicSyncWorker>(
          context,
          inputData = Data.Builder().putInt(MAX_RETRIES_ALLOWED, 0).build(),
          runAttemptCount = 0,
        )
        .build()
    val result = runBlocking { worker.doWork() }
    assertIs<ListenableWorker.Result>(result)
    assertEquals(ListenableWorker.Result.failure()::class, result::class)
  }

  @Test
  fun fhirSyncWorker_failedTaskWithCurrentRunAttemptSameAsTheRetries_resultShouldBeFail() {
    val worker =
      TestListenableWorkerBuilder<FailingPeriodicSyncWorker>(
          context,
          inputData = Data.Builder().putInt(MAX_RETRIES_ALLOWED, 2).build(),
          runAttemptCount = 2,
        )
        .build()
    val result = runBlocking { worker.doWork() }
    assertIs<ListenableWorker.Result>(result)
    assertEquals(ListenableWorker.Result.failure()::class, result::class)
  }

  @Test
  fun fhirSyncWorker_failedTaskWithCurrentRunAttemptSmallerThanTheRetries_resultShouldBeRetry() {
    val worker =
      TestListenableWorkerBuilder<FailingPeriodicSyncWorker>(
          context,
          inputData = Data.Builder().putInt(MAX_RETRIES_ALLOWED, 1).build(),
          runAttemptCount = 0,
        )
        .build()
    val result = runBlocking { worker.doWork() }
    assertEquals(ListenableWorker.Result.retry(), result)
  }

  @Test
  fun fhirSyncWorker_nullDataSource_resultShouldBeFail() {
    val worker =
      TestListenableWorkerBuilder<FailingPeriodicSyncWorkerWithoutDataSource>(
          context,
          inputData = Data.Builder().putInt(MAX_RETRIES_ALLOWED, 1).build(),
          runAttemptCount = 2,
        )
        .build()
    val result = runBlocking { worker.doWork() }
    assertEquals(ListenableWorker.Result.failure()::class, result::class)
    assertIs<ListenableWorker.Result.Failure>(result)
    val outputData = (result as ListenableWorker.Result.Failure).outputData
    assertNotNull(outputData)
    assertTrue(outputData.keyValueMap.containsKey("error"))
    assertEquals("java.lang.IllegalStateException", outputData.getString("error"))
  }
}
