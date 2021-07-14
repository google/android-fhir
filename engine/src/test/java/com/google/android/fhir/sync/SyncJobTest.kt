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
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.impl.utils.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.db.Database
import com.google.android.fhir.impl.FhirEngineImpl
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
@LooperMode(LooperMode.Mode.PAUSED)
class SyncJobTest {
  private lateinit var workManager: WorkManager
  private lateinit var fhirEngine: FhirEngine

  @Mock private lateinit var database: Database

  @Mock private lateinit var dataSource: DataSource

  private lateinit var syncJob: SyncJob

  private val testDispatcher = TestCoroutineDispatcher()

  private lateinit var context: Context

  @get:Rule var instantExecutorRule = InstantTaskExecutorRule()

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)

    fhirEngine = FhirEngineImpl(database, ApplicationProvider.getApplicationContext())

    val resourceSyncParam = mapOf(ResourceType.Patient to mapOf("address-city" to "NAIROBI"))

    syncJob = Sync.basicSyncJob(fhirEngine, dataSource, resourceSyncParam)

    context = ApplicationProvider.getApplicationContext()

    val config =
      Configuration.Builder()
        .setMinimumLoggingLevel(Log.DEBUG)
        .setExecutor(SynchronousExecutor())
        .build()

    // Initialize WorkManager for instrumentation tests.
    WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
    workManager = WorkManager.getInstance(context)
  }

  @Test
  fun `should poll accurately with given delay`() = runBlockingTest {
    whenever(database.getAllLocalChanges()).thenReturn(listOf())
    whenever(dataSource.loadData(any())).thenReturn(Bundle())

    val worker = PeriodicWorkRequestBuilder<TestSyncWorker>(15, TimeUnit.MINUTES).build()

    val workInfoFlow = syncJob.workInfoFlowFor(SyncWorkType.DOWNLOAD_UPLOAD.workerName, context)
    val stateFlow = syncJob.stateFlowFor(SyncWorkType.DOWNLOAD_UPLOAD.workerName, context)

    val workInfoList = mutableListOf<WorkInfo>()
    val stateList = mutableListOf<State>()

    val job1 = launch { workInfoFlow.toList(workInfoList) }

    val job2 = launch { stateFlow.toList(stateList) }

    workManager
      .enqueueUniquePeriodicWork(
        SyncWorkType.DOWNLOAD_UPLOAD.workerName,
        ExistingPeriodicWorkPolicy.KEEP,
        worker
      )
      .result
      .get()

    val testDriver = WorkManagerTestInitHelper.getTestDriver(context)!!
    testDriver.setPeriodDelayMet(worker.id)

    Thread.sleep(10000) // how to avoid it ???

    // WorkInfos emitted by WorkManager are
    // Enqueued, Running [1 on start],
    // Running [4 from emitted progress],
    // Enqueued for successful runs
    assertTrue(workInfoList.size >= 4)

    assertTrue(workInfoList[0].state == WorkInfo.State.ENQUEUED)
    assertTrue(workInfoList[1].state == WorkInfo.State.RUNNING)
    // 2nd last item is Running with progress State.Success
    assertTrue(workInfoList[workInfoList.size-2].state == WorkInfo.State.RUNNING)
    // last item is enqueued
    assertTrue(workInfoList[workInfoList.size-1].state == WorkInfo.State.ENQUEUED)

    // States are Nothing, Started, InProgress .... , Success
    assertTrue(stateList[stateList.size-1] is State.Success)

    job1.cancel()
    job2.cancel()
  }

  @Test
  fun `should run synchronizer and emit states accurately in sequence`() = runBlockingTest {
    whenever(database.getAllLocalChanges()).thenReturn(listOf())
    whenever(dataSource.loadData(any())).thenReturn(Bundle())

    val res = mutableListOf<State>()

    val flow = MutableSharedFlow<State>()
    val job = launch { flow.collect { res.add(it) } }

    syncJob.run(flow)

    // State transition for successful job as below
    // Nothing, Started, InProgress, Success
    assertTrue(res[0] is State.Nothing)
    assertTrue(res[1] is State.Started)
    assertTrue(res[2] is State.InProgress)
    assertTrue(res[3] is State.Success)
    assertEquals(
      LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
      (res[3] as State.Success).lastSyncTimestamp.truncatedTo(ChronoUnit.SECONDS)
    )

    assertEquals(4, res.size)

    job.cancel()
  }

  @Test
  fun `should run synchronizer and emit  with error accurately in sequence`() = runBlockingTest {
    whenever(database.getAllLocalChanges()).thenReturn(listOf())
    whenever(dataSource.loadData(any())).thenThrow(IllegalStateException::class.java)

    val res = mutableListOf<State>()

    val flow = MutableSharedFlow<State>()
    val job = launch { flow.collect { res.add(it) } }

    syncJob.run(flow)

    // State transition for failed job as below
    // Nothing, Started, InProgress, Error
    assertTrue(res[0] is State.Nothing)
    assertTrue(res[1] is State.Started)
    assertTrue(res[2] is State.InProgress)
    assertTrue(res[3] is State.Error)
    assertEquals(
      LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
      (res[3] as State.Error).lastSyncTimestamp.truncatedTo(ChronoUnit.SECONDS)
    )

    assertTrue((res[3] as State.Error).exceptions[0].exception is java.lang.IllegalStateException)
    assertEquals(4, res.size)

    job.cancel()
  }
}
