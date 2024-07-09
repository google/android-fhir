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

    override fun getUploadStrategy(): UploadStrategy = UploadStrategy.AllChangesSquashedBundlePut
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
      Sync.oneTimeSync<TestSyncWorker>(context = context)
        .transformWhile {
          emit(it is CurrentSyncJobStatus.Succeeded)
          it !is CurrentSyncJobStatus.Succeeded
        }
        .shareIn(this, SharingStarted.Eagerly, 5)
    }

    assertThat(workManager.getWorkInfosByTag(TestSyncWorker::class.java.name).get().first().state)
      .isEqualTo(WorkInfo.State.SUCCEEDED)
  }

  @Test
  fun oneTime_worker_syncState() {
    WorkManagerTestInitHelper.initializeTestWorkManager(context)
    val states = mutableListOf<CurrentSyncJobStatus>()
    runBlocking {
      Sync.oneTimeSync<TestSyncWorker>(context = context)
        .transformWhile {
          states.add(it)
          emit(it is CurrentSyncJobStatus.Succeeded)
          it !is CurrentSyncJobStatus.Succeeded
        }
        .shareIn(this, SharingStarted.Eagerly, 5)
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

      launch {
          Sync.oneTimeSync<TestSyncWorker>(context = context).collect {
            states.add(it)
            if (it is CurrentSyncJobStatus.Succeeded) {
              cancel()
            }
          }
        }
        .join()

      launch {
          Sync.oneTimeSync<TestSyncWorker>(context = context).collect {
            nextExecutionStates.add(it)
            if (it is CurrentSyncJobStatus.Succeeded) {
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
      Sync.oneTimeSync<TestSyncWorkerForDownloadFailing>(
          context = context,
          RetryConfiguration(
            BackoffCriteria(
              BackoffPolicy.LINEAR,
              30,
              TimeUnit.SECONDS,
            ),
            0,
          ),
        )
        .transformWhile {
          states.add(it)
          emit(it is CurrentSyncJobStatus.Failed)
          it !is CurrentSyncJobStatus.Failed
        }
        .shareIn(this, SharingStarted.Eagerly, 5)
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
          Sync.oneTimeSync<TestSyncWorkerForDownloadFailing>(
              context = context,
              RetryConfiguration(
                BackoffCriteria(
                  BackoffPolicy.LINEAR,
                  30,
                  TimeUnit.SECONDS,
                ),
                0,
              ),
            )
            .collect {
              states.add(it)
              if (it is CurrentSyncJobStatus.Failed) {
                cancel()
              }
            }
        }
        .join()

      launch {
          Sync.oneTimeSync<TestSyncWorkerForDownloadFailing>(
              context = context,
              RetryConfiguration(
                BackoffCriteria(
                  BackoffPolicy.LINEAR,
                  30,
                  TimeUnit.SECONDS,
                ),
                0,
              ),
            )
            .collect {
              nextExecutionStates.add(it)
              if (it is CurrentSyncJobStatus.Failed) {
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
      Sync.periodicSync<TestSyncWorker>(
          context = context,
          periodicSyncConfiguration =
            PeriodicSyncConfiguration(
              syncConstraints = Constraints.Builder().build(),
              repeat = RepeatInterval(interval = 15, timeUnit = TimeUnit.MINUTES),
            ),
        )
        .transformWhile {
          states.add(it)
          emit(it)
          it.currentSyncJobStatus !is CurrentSyncJobStatus.Succeeded
        }
        .shareIn(this, SharingStarted.Eagerly, 5)
    }

    assertThat(states.first().currentSyncJobStatus)
      .isInstanceOf(CurrentSyncJobStatus.Running::class.java)
    assertThat(states.first().lastSyncJobStatus).isNull()
    assertThat(states.last().currentSyncJobStatus)
      .isInstanceOf(CurrentSyncJobStatus.Succeeded::class.java)
    assertThat(states.last().lastSyncJobStatus)
      .isInstanceOf(LastSyncJobStatus.Succeeded::class.java)
  }

  @Test
  fun periodic_worker_failedPeriodicSyncState() {
    WorkManagerTestInitHelper.initializeTestWorkManager(context)
    val states = mutableListOf<PeriodicSyncJobStatus>()
    // run and wait for periodic worker to finish
    runBlocking {
      Sync.periodicSync<TestSyncWorkerForDownloadFailing>(
          context = context,
          periodicSyncConfiguration =
            PeriodicSyncConfiguration(
              syncConstraints = Constraints.Builder().build(),
              repeat = RepeatInterval(interval = 15, timeUnit = TimeUnit.MINUTES),
            ),
        )
        .transformWhile {
          states.add(it)
          emit(it)
          it.currentSyncJobStatus !is CurrentSyncJobStatus.Failed
        }
        .shareIn(this, SharingStarted.Eagerly, 5)
    }

    assertThat(states.first().currentSyncJobStatus)
      .isInstanceOf(CurrentSyncJobStatus.Running::class.java)
    assertThat(states.first().lastSyncJobStatus).isNull()
    assertThat(states.last().currentSyncJobStatus)
      .isInstanceOf(CurrentSyncJobStatus.Failed::class.java)
    assertThat(states.last().lastSyncJobStatus).isInstanceOf(LastSyncJobStatus.Failed::class.java)
  }

  @Test
  fun periodic_worker_still_queued_to_run_after_oneTime_worker_started() {
    WorkManagerTestInitHelper.initializeTestWorkManager(context)
    val workManager = WorkManager.getInstance(context)
    // run and wait for periodic worker to finish
    runBlocking {
      Sync.periodicSync<TestSyncWorker>(
          context = context,
          periodicSyncConfiguration =
            PeriodicSyncConfiguration(
              syncConstraints = Constraints.Builder().build(),
              repeat = RepeatInterval(interval = 15, timeUnit = TimeUnit.MINUTES),
            ),
        )
        .transformWhile {
          emit(it)
          it.currentSyncJobStatus !is CurrentSyncJobStatus.Succeeded
        }
        .shareIn(this, SharingStarted.Eagerly, 5)
    }

    // Verify the periodic worker completed the run, and is queued to run again
    val periodicWorkerId =
      workManager.getWorkInfosByTag(TestSyncWorker::class.java.name).get().first().id
    assertThat(workManager.getWorkInfoById(periodicWorkerId).get().state)
      .isEqualTo(WorkInfo.State.ENQUEUED)

    // Start and complete a oneTime job, and verify it does not remove the periodic worker
    runBlocking {
      Sync.oneTimeSync<TestSyncWorker>(context = context)
        .transformWhile {
          emit(it)
          it !is CurrentSyncJobStatus.Succeeded
        }
        .shareIn(this, SharingStarted.Eagerly, 5)
    }
    assertThat(workManager.getWorkInfosByTag(TestSyncWorker::class.java.name).get().size)
      .isEqualTo(2)

    // Move forward to the next epoch to trigger the periodic sync
    val testDriver = WorkManagerTestInitHelper.getTestDriver(context)
    testDriver?.setPeriodDelayMet(periodicWorkerId)

    assertThat(workManager.getWorkInfoById(periodicWorkerId).get().state)
      .isEqualTo(WorkInfo.State.RUNNING)
  }
}
