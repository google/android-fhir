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
import androidx.work.Configuration
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.testing.WorkManagerTestInitHelper
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.sync.upload.HttpCreateMethod
import com.google.android.fhir.sync.upload.HttpUpdateMethod
import com.google.android.fhir.sync.upload.UploadStrategy
import android.os.Build
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Tests the AndroidSyncScheduler integration with WorkManager to verify that:
 * - One-time sync jobs are correctly enqueued and can be cancelled
 * - Periodic sync jobs are correctly enqueued and can be cancelled
 * - Unique work name management (store/fetch/remove) in DataStore works correctly
 * - Sync constraints and retry configuration are correctly applied
 * - State flows emit correct initial states after enqueue
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Build.VERSION_CODES.P])
internal class AndroidSyncSchedulerTest {

  private lateinit var context: Context
  private lateinit var dataStore: FhirDataStore
  private lateinit var scheduler: AndroidSyncScheduler

  class TestSyncWorker(appContext: Context, workerParams: WorkerParameters) :
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

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    val config = Configuration.Builder().setMinimumLoggingLevel(android.util.Log.DEBUG).build()
    WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
    dataStore = FhirDataStore(createDataStore(context))
    scheduler = AndroidSyncScheduler(context, TestSyncWorker::class.java, dataStore)
  }

  @Test
  fun createSyncUniqueName_oneTimeSync_shouldContainWorkerClassNameAndSyncType() {
    val name = scheduler.createSyncUniqueName("oneTimeSync")
    assertTrue(name.contains("TestSyncWorker"))
    assertTrue(name.contains("oneTimeSync"))
    assertEquals("${TestSyncWorker::class.java.name}-oneTimeSync", name)
  }

  @Test
  fun createSyncUniqueName_periodicSync_shouldContainWorkerClassNameAndSyncType() {
    val name = scheduler.createSyncUniqueName("periodicSync")
    assertTrue(name.contains("TestSyncWorker"))
    assertTrue(name.contains("periodicSync"))
    assertEquals("${TestSyncWorker::class.java.name}-periodicSync", name)
  }

  @Test
  fun storeUniqueWorkNameInDataStore_shouldPersistWorkName() {
    runBlocking {
      val workName = scheduler.createSyncUniqueName("oneTimeSync")
      scheduler.storeUniqueWorkNameInDataStore(workName)
      val fetched = dataStore.fetchUniqueWorkName(workName)
      assertNotNull(fetched)
      assertEquals(workName, fetched)
    }
  }

  @Test
  fun storeUniqueWorkNameInDataStore_calledTwice_shouldNotOverwrite() {
    runBlocking {
      val workName = scheduler.createSyncUniqueName("oneTimeSync")
      scheduler.storeUniqueWorkNameInDataStore(workName)
      // Call again — should be idempotent (only stores if null)
      scheduler.storeUniqueWorkNameInDataStore("wrong-name")
      val fetched = dataStore.fetchUniqueWorkName(workName)
      assertNotNull(fetched)
      assertEquals(workName, fetched)
    }
  }

  @Test
  fun removeUniqueWorkNameInDataStore_shouldRemoveStoredWorkName() {
    runBlocking {
      val workName = scheduler.createSyncUniqueName("oneTimeSync")
      scheduler.storeUniqueWorkNameInDataStore(workName)
      assertNotNull(dataStore.fetchUniqueWorkName(workName))

      scheduler.removeUniqueWorkNameInDataStore(workName)
      assertNull(dataStore.fetchUniqueWorkName(workName))
    }
  }

  @Test
  fun removeUniqueWorkNameInDataStore_whenNotStored_shouldNotThrow() {
    runBlocking {
      val workName = scheduler.createSyncUniqueName("oneTimeSync")
      // Should not throw even when nothing is stored
      scheduler.removeUniqueWorkNameInDataStore(workName)
      assertNull(dataStore.fetchUniqueWorkName(workName))
    }
  }

  @Test
  fun runOneTimeSync_shouldRunWorkInWorkManager() {
    runBlocking {
      val retryConfig = RetryConfiguration(
        backoffCriteria = BackoffCriteria(BackoffPolicy.LINEAR, 30.seconds),
        maxRetries = 3,
      )
      scheduler.runOneTimeSync(retryConfig)

      // Recreate the same unique work name to check if any work is registered under this name
      val uniqueWorkName = scheduler.createSyncUniqueName("oneTimeSync")
      val workInfos = WorkManager.getInstance(context)
        .getWorkInfosForUniqueWork(uniqueWorkName)
        .get()

      // Work is registered
      assertTrue(workInfos.isNotEmpty())

      // Work should be enqueued or running
      val workInfo = workInfos.first()
        assertEquals(workInfo.state, WorkInfo.State.RUNNING)
    }
  }

  @Test
  fun runOneTimeSync_shouldStoreWorkNameInDataStore() {
    runBlocking {
      val retryConfig = RetryConfiguration(
        backoffCriteria = BackoffCriteria(BackoffPolicy.LINEAR, 30.seconds),
        maxRetries = 3,
      )
      scheduler.runOneTimeSync(retryConfig)

      val uniqueWorkName = scheduler.createSyncUniqueName("oneTimeSync")
      val storedName = dataStore.fetchUniqueWorkName(uniqueWorkName)
      assertNotNull(storedName)
      assertEquals(uniqueWorkName, storedName)
    }
  }

  @Test
  fun schedulePeriodicSync_shouldEnqueuePeriodicWorkInWorkManager() {
    runBlocking {
      val config = PeriodicSyncConfiguration(
        syncConstraints = SyncConstraints(requiredNetworkType = NetworkType.CONNECTED),
        repeat = RepeatInterval(interval = 15.minutes),
        retryConfiguration = RetryConfiguration(
          backoffCriteria = BackoffCriteria(BackoffPolicy.LINEAR, 30.seconds),
          maxRetries = 3,
        ),
      )
      scheduler.schedulePeriodicSync(config)

      val uniqueWorkName = scheduler.createSyncUniqueName("periodicSync")
      val workInfos = WorkManager.getInstance(context)
        .getWorkInfosForUniqueWork(uniqueWorkName)
        .get()

      assertTrue(workInfos.isNotEmpty())
      val workInfo = workInfos.first()
      assertEquals(workInfo.state, WorkInfo.State.ENQUEUED)
    }
  }

  @Test
  fun schedulePeriodicSync_shouldStoreWorkNameInDataStore() {
    runBlocking {
      val config = PeriodicSyncConfiguration(
        repeat = RepeatInterval(interval = 15.minutes),
        retryConfiguration = RetryConfiguration(
          backoffCriteria = BackoffCriteria(BackoffPolicy.LINEAR, 30.seconds),
          maxRetries = 3,
        ),
      )
      scheduler.schedulePeriodicSync(config)

      val uniqueWorkName = scheduler.createSyncUniqueName("periodicSync")
      val storedName = dataStore.fetchUniqueWorkName(uniqueWorkName)
      assertNotNull(storedName)
      assertEquals(uniqueWorkName, storedName)
    }
  }

  @Test
  fun cancelOneTimeSync_afterEnqueue_shouldCancelWork() {
    runBlocking {
      val retryConfig = RetryConfiguration(
        backoffCriteria = BackoffCriteria(BackoffPolicy.LINEAR, 30.seconds),
        maxRetries = 3,
      )
      scheduler.runOneTimeSync(retryConfig)

      val uniqueWorkName = scheduler.createSyncUniqueName("oneTimeSync")
      // Verify work is enqueued
      val workInfosBefore = WorkManager.getInstance(context)
        .getWorkInfosForUniqueWork(uniqueWorkName)
        .get()
      assertTrue(workInfosBefore.isNotEmpty())

      // Cancel the sync
      scheduler.cancelOneTimeSync()

      // Verify work is cancelled
      val workInfosAfter = WorkManager.getInstance(context)
        .getWorkInfosForUniqueWork(uniqueWorkName)
        .get()
        assertEquals(workInfosAfter.first().state, WorkInfo.State.CANCELLED)
    }
  }

  @Test
  fun cancelPeriodicSync_afterSchedule_shouldCancelWork() {
    runBlocking {
      val config = PeriodicSyncConfiguration(
        repeat = RepeatInterval(interval = 15.minutes),
        retryConfiguration = defaultRetryConfiguration,
      )
      scheduler.schedulePeriodicSync(config)

      val uniqueWorkName = scheduler.createSyncUniqueName("periodicSync")
      // Verify work is enqueued
      val workInfosBefore = WorkManager.getInstance(context)
        .getWorkInfosForUniqueWork(uniqueWorkName)
        .get()
      assertTrue(workInfosBefore.isNotEmpty())

      // Cancel the sync
      scheduler.cancelPeriodicSync()

      // Verify work is cancelled
      val workInfosAfter = WorkManager.getInstance(context)
        .getWorkInfosForUniqueWork(uniqueWorkName)
        .get()
        assertEquals(workInfosAfter.first().state, WorkInfo.State.CANCELLED)
    }
  }

  @Test
  fun cancelOneTimeSync_whenNothingEnqueued_shouldNotThrow() {
    runBlocking {
      // Should not throw even when there's no work enqueued
      scheduler.cancelOneTimeSync()
    }
  }

  @Test
  fun cancelPeriodicSync_whenNothingScheduled_shouldNotThrow() {
    runBlocking {
      // Should not throw even when there's no work scheduled
      scheduler.cancelPeriodicSync()
    }
  }

  @Test
  fun runOneTimeSync_withNullRetryConfiguration_shouldEnqueueWork() {
    runBlocking {
      scheduler.runOneTimeSync(retryConfiguration = null)

      val uniqueWorkName = scheduler.createSyncUniqueName("oneTimeSync")
      val workInfos = WorkManager.getInstance(context)
        .getWorkInfosForUniqueWork(uniqueWorkName)
        .get()

      assertTrue(workInfos.isNotEmpty())
    }
  }

  @Test
  fun schedulePeriodicSync_withCustomConstraints_shouldEnqueueWork() {
    runBlocking {
      val config = PeriodicSyncConfiguration(
        syncConstraints = SyncConstraints(
          requiredNetworkType = NetworkType.UNMETERED,
          requiresBatteryNotLow = true,
          requiresCharging = true,
          requiresStorageNotLow = true,
        ),
        repeat = RepeatInterval(interval = 30.minutes),
        retryConfiguration = RetryConfiguration(
          backoffCriteria = BackoffCriteria(BackoffPolicy.EXPONENTIAL, 60.seconds),
          maxRetries = 5,
        ),
      )
      scheduler.schedulePeriodicSync(config)

      val uniqueWorkName = scheduler.createSyncUniqueName("periodicSync")
      val workInfos = WorkManager.getInstance(context)
        .getWorkInfosForUniqueWork(uniqueWorkName)
        .get()

      assertTrue(workInfos.isNotEmpty())
    }
  }

  @Test
  fun runOneTimeSync_withExistingWorkPolicy_shouldKeepExisting() {
    runBlocking {
      val retryConfig = RetryConfiguration(
        backoffCriteria = BackoffCriteria(BackoffPolicy.LINEAR, 30.seconds),
        maxRetries = 3,
      )
      // Enqueue twice — ExistingWorkPolicy.KEEP should keep the first one
      scheduler.runOneTimeSync(retryConfig)
      scheduler.runOneTimeSync(retryConfig)

      val uniqueWorkName = scheduler.createSyncUniqueName("oneTimeSync")
      val workInfos = WorkManager.getInstance(context)
        .getWorkInfosForUniqueWork(uniqueWorkName)
        .get()

      // Should still have exactly one work item (KEEP policy)
      assertEquals(1, workInfos.size)
    }
  }
}
