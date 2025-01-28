/*
 * Copyright 2023-2025 Google LLC
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
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.testing.WorkManagerTestInitHelper
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.sync.upload.HttpCreateMethod
import com.google.android.fhir.sync.upload.HttpUpdateMethod
import com.google.android.fhir.sync.upload.UploadStrategy
import com.google.android.fhir.testing.TestDataSourceImpl
import com.google.android.fhir.testing.TestDownloadManagerImpl
import com.google.android.fhir.testing.TestFailingDatasource
import com.google.android.fhir.testing.TestFhirEngineImpl
import com.google.common.truth.Truth.assertThat
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Note : If you are running these tests on a local machine in Android Studio, make sure to clear
 * the storage and cache of the `com.google.android.fhir.test` app on the emulator/device before
 * running each test individually.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class SyncInstrumentedTest {

  private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

  open class TestSyncWorker(appContext: Context, workerParams: WorkerParameters) :
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
  }

  class TestSyncWorkerForDownloadFailing(appContext: Context, workerParams: WorkerParameters) :
    TestSyncWorker(appContext, workerParams) {
    override fun getDataSource(): DataSource = TestFailingDatasource
  }

  @Test
  fun oneTime_worker_runs() {
    WorkManagerTestInitHelper.initializeTestWorkManager(context)
    val workManager = WorkManager.getInstance(context)
    runBlocking {
      val statusFlow = Sync.oneTimeSync<TestSyncWorker>(context = context)
      statusFlow
        .transformWhile { status ->
          emit(status is CurrentSyncJobStatus.Succeeded)
          status !is CurrentSyncJobStatus.Succeeded
        }
        .shareIn(this, SharingStarted.Eagerly, replay = 5)
    }

    assertThat(workManager.getWorkInfosByTag(TestSyncWorker::class.java.name).get().first().state)
      .isEqualTo(WorkInfo.State.SUCCEEDED)
  }

  @Test
  fun oneTime_worker_syncState() {
    WorkManagerTestInitHelper.initializeTestWorkManager(context)
    val states = mutableListOf<CurrentSyncJobStatus>()
    runBlocking {
      val statusFlow = Sync.oneTimeSync<TestSyncWorker>(context = context)
      launch {
        statusFlow
          .transformWhile { status ->
            states.add(status)
            emit(status is CurrentSyncJobStatus.Succeeded)
            status !is CurrentSyncJobStatus.Succeeded
          }
          .collect { isSuccess ->
            if (isSuccess) {
              // Flow has completed successfully
            }
          }
      }
    }
    assertThat(states.first()).isInstanceOf(CurrentSyncJobStatus.Running::class.java)
    assertThat(states.last()).isInstanceOf(CurrentSyncJobStatus.Succeeded::class.java)
  }

  @Test
  fun oneTimeSync_currentSyncJobStatusSucceeded_nextCurrentSyncJobStatusShouldBeRunning() =
    runBlocking {
      WorkManagerTestInitHelper.initializeTestWorkManager(context)
      val states = mutableListOf<CurrentSyncJobStatus>()
      val nextExecutionStates = mutableListOf<CurrentSyncJobStatus>()

      // First one-time sync
      launch {
          val statusFlow = Sync.oneTimeSync<TestSyncWorker>(context = context)
          statusFlow.collect { status ->
            states.add(status)
            if (status is CurrentSyncJobStatus.Succeeded) {
              cancel()
            }
          }
        }
        .join()

      // Second one-time sync
      launch {
          val statusFlow = Sync.oneTimeSync<TestSyncWorker>(context = context)
          statusFlow.collect { status ->
            nextExecutionStates.add(status)
            if (status is CurrentSyncJobStatus.Succeeded) {
              cancel()
            }
          }
        }
        .join()

      assertThat(states.first()).isInstanceOf(CurrentSyncJobStatus.Running::class.java)
      assertThat(states.last()).isInstanceOf(CurrentSyncJobStatus.Succeeded::class.java)
      assertThat(nextExecutionStates.first()).isInstanceOf(CurrentSyncJobStatus.Running::class.java)
    }

  @Test
  fun oneTime_worker_failedSyncState() {
    WorkManagerTestInitHelper.initializeTestWorkManager(context)
    val states = mutableListOf<CurrentSyncJobStatus>()

    runBlocking {
      val statusFlow =
        Sync.oneTimeSync<TestSyncWorkerForDownloadFailing>(
          context = context,
          retryConfiguration =
            RetryConfiguration(
              backoffCriteria =
                BackoffCriteria(
                  BackoffPolicy.LINEAR,
                  30,
                  TimeUnit.SECONDS,
                ),
              maxRetries = 0,
            ),
        )

      launch {
        statusFlow
          .transformWhile { status ->
            states.add(status)
            emit(status is CurrentSyncJobStatus.Failed)
            status !is CurrentSyncJobStatus.Failed
          }
          .collect { isFailed ->
            if (isFailed) {
              // Flow has completed with a failure
            }
          }
      }
    }

    assertThat(states.first()).isInstanceOf(CurrentSyncJobStatus.Running::class.java)
    assertThat(states.last()).isInstanceOf(CurrentSyncJobStatus.Failed::class.java)
  }

  @Test
  fun oneTimeSync_currentSyncJobStatusFailed_nextCurrentSyncJobStatusShouldBeRunning() =
    runBlocking {
      WorkManagerTestInitHelper.initializeTestWorkManager(context)
      val states = mutableListOf<CurrentSyncJobStatus>()
      val nextExecutionStates = mutableListOf<CurrentSyncJobStatus>()

      launch {
          val statusFlow =
            Sync.oneTimeSync<TestSyncWorkerForDownloadFailing>(
              context = context,
              retryConfiguration =
                RetryConfiguration(
                  backoffCriteria =
                    BackoffCriteria(
                      BackoffPolicy.LINEAR,
                      30,
                      TimeUnit.SECONDS,
                    ),
                  maxRetries = 0,
                ),
            )

          statusFlow.collect { status ->
            states.add(status)
            if (status is CurrentSyncJobStatus.Failed) {
              cancel()
            }
          }
        }
        .join()

      launch {
          val nextStatusFlow =
            Sync.oneTimeSync<TestSyncWorkerForDownloadFailing>(
              context = context,
              retryConfiguration =
                RetryConfiguration(
                  backoffCriteria =
                    BackoffCriteria(
                      BackoffPolicy.LINEAR,
                      30,
                      TimeUnit.SECONDS,
                    ),
                  maxRetries = 0,
                ),
            )

          nextStatusFlow.collect { status ->
            nextExecutionStates.add(status)
            if (status is CurrentSyncJobStatus.Failed) {
              cancel()
            }
          }
        }
        .join()

      assertThat(states.first()).isInstanceOf(CurrentSyncJobStatus.Running::class.java)
      assertThat(states.last()).isInstanceOf(CurrentSyncJobStatus.Failed::class.java)
      assertThat(nextExecutionStates.first()).isInstanceOf(CurrentSyncJobStatus.Running::class.java)
    }

  @Test
  fun periodic_worker_periodicSyncState() {
    WorkManagerTestInitHelper.initializeTestWorkManager(context)
    val states = mutableListOf<PeriodicSyncJobStatus>()
    // run and wait for periodic worker to finish
    runBlocking {
      val flow =
        Sync.periodicSync<TestSyncWorker>(
          context = context,
          periodicSyncConfiguration =
            PeriodicSyncConfiguration(
              syncConstraints = Constraints.Builder().build(),
              repeat = RepeatInterval(interval = 15, timeUnit = TimeUnit.MINUTES),
            ),
        )
      flow
        .transformWhile {
          states.add(it)
          emit(it)
          it.currentSyncJobStatus !is CurrentSyncJobStatus.Enqueued
        }
        .shareIn(this, SharingStarted.Eagerly, 5)
    }

    assertThat(states.first().currentSyncJobStatus)
      .isInstanceOf(CurrentSyncJobStatus.Running::class.java)
    assertThat(states.first().lastSyncJobStatus).isNull()
    assertThat(states.last().currentSyncJobStatus)
      .isInstanceOf(CurrentSyncJobStatus.Enqueued::class.java)
    assertThat(states.last().lastSyncJobStatus)
      .isInstanceOf(LastSyncJobStatus.Succeeded::class.java)
  }

  @Test
  fun periodic_worker_failedPeriodicSyncState() {
    WorkManagerTestInitHelper.initializeTestWorkManager(context)
    val states = mutableListOf<PeriodicSyncJobStatus>()

    runBlocking {
      val statusFlow =
        Sync.periodicSync<TestSyncWorkerForDownloadFailing>(
          context = context,
          periodicSyncConfiguration =
            PeriodicSyncConfiguration(
              syncConstraints = Constraints.Builder().build(),
              repeat = RepeatInterval(interval = 15, timeUnit = TimeUnit.MINUTES),
            ),
        )

      launch {
        statusFlow
          .transformWhile { status ->
            states.add(status)
            emit(status)
            status.currentSyncJobStatus !is CurrentSyncJobStatus.Enqueued
          }
          .collect { status ->
            // Flow has emitted a value
          }
      }
    }

    assertThat(states.first().currentSyncJobStatus)
      .isInstanceOf(CurrentSyncJobStatus.Running::class.java)
    assertThat(states.first().lastSyncJobStatus).isNull()
    assertThat(states.last().currentSyncJobStatus)
      .isInstanceOf(CurrentSyncJobStatus.Enqueued::class.java)
    assertThat(states.last().lastSyncJobStatus).isInstanceOf(LastSyncJobStatus.Failed::class.java)
  }

  @Test
  fun periodic_worker_still_queued_to_run_after_oneTime_worker_started() {
    WorkManagerTestInitHelper.initializeTestWorkManager(context)
    val workManager = WorkManager.getInstance(context)

    // Run and wait for periodic worker to finish
    runBlocking {
      val statusFlow =
        Sync.periodicSync<TestSyncWorker>(
          context = context,
          periodicSyncConfiguration =
            PeriodicSyncConfiguration(
              syncConstraints = Constraints.Builder().build(),
              repeat = RepeatInterval(interval = 15, timeUnit = TimeUnit.MINUTES),
            ),
        )

      launch {
        statusFlow
          .transformWhile { status ->
            emit(status)
            status.currentSyncJobStatus !is CurrentSyncJobStatus.Enqueued
          }
          .collect { status -> }
      }
    }

    // Verify the periodic worker completed the run and is queued to run again
    val periodicWorkerInfo =
      workManager
        .getWorkInfoById(
          workManager.getWorkInfosByTag(TestSyncWorker::class.java.name).get().first().id,
        )
        .get()
    assertThat(periodicWorkerInfo.state).isEqualTo(WorkInfo.State.ENQUEUED)

    // Start and complete a one-time job, and verify it does not remove the periodic worker
    runBlocking {
      val oneTimeStatusFlow = Sync.oneTimeSync<TestSyncWorker>(context = context)

      oneTimeStatusFlow
        .transformWhile { status ->
          emit(status)
          status !is CurrentSyncJobStatus.Succeeded
        }
        .collect { status -> }
    }

    assertThat(workManager.getWorkInfosByTag(TestSyncWorker::class.java.name).get().size)
      .isEqualTo(2)

    // Move forward to the next epoch to trigger the periodic sync
    val testDriver = WorkManagerTestInitHelper.getTestDriver(context)
    testDriver?.setPeriodDelayMet(periodicWorkerInfo.id)

    // Confirm the periodic worker is running after advancing the period
    assertThat(workManager.getWorkInfoById(periodicWorkerInfo.id).get().state)
      .isEqualTo(WorkInfo.State.RUNNING)
  }
}
