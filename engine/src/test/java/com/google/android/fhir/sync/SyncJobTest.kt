/*
 * Copyright 2021 Google LLC
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
import androidx.work.testing.TestDriver
import androidx.work.testing.WorkManagerTestInitHelper
import com.google.android.fhir.DatastoreUtil
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.db.Database
import com.google.android.fhir.impl.FhirEngineImpl
import com.google.android.fhir.resource.TestingUtils
import com.google.common.truth.Truth.assertThat
import java.time.OffsetDateTime
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.hl7.fhir.r4.model.Bundle
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
@LooperMode(LooperMode.Mode.PAUSED)
class SyncJobTest {
  private val context: Context = ApplicationProvider.getApplicationContext()
  private val datastoreUtil = DatastoreUtil(context)

  private lateinit var workManager: WorkManager
  private lateinit var fhirEngine: FhirEngine

  private val database = mock<Database>()
  private val dataSource = mock<DataSource>()

  @get:Rule var instantExecutorRule = InstantTaskExecutorRule()

  private lateinit var syncJob: SyncJob
  private lateinit var mock: MockedStatic<FhirEngineProvider>

  @Before
  fun setup() {
    fhirEngine = FhirEngineImpl(database, context)
    syncJob = Sync.basicSyncJob(context)

    val config =
      Configuration.Builder()
        .setMinimumLoggingLevel(Log.DEBUG)
        .setExecutor(SynchronousExecutor())
        .build()

    // Initialize WorkManager for instrumentation tests.
    WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
    //testDriver = WorkManagerTestInitHelper.getTestDriver(context)!!
    workManager = WorkManager.getInstance(context)
    mock = Mockito.mockStatic(FhirEngineProvider::class.java)
    whenever(FhirEngineProvider.getDataSource(anyOrNull())).thenReturn(dataSource)
  }

  @After
  fun tearDown() {
    mock.close()
  }

  @Test
  fun `Should poll accurately with given delay`() = runBlockingTest {
    val worker = PeriodicWorkRequestBuilder<TestSyncWorker>(15, TimeUnit.MINUTES).build()

    // Get flows return by work manager wrapper
    val workInfoFlow = syncJob.workInfoFlow()
    val stateFlow = syncJob.stateFlow()

    val workInfoList = mutableListOf<WorkInfo>()
    val stateList = mutableListOf<State>()

    // Convert flows to list to assert later
    val job1 = launch { workInfoFlow.toList(workInfoList) }
    val job2 = launch { stateFlow.toList(stateList) }

    workManager
      .enqueueUniquePeriodicWork(
        SyncWorkType.DOWNLOAD_UPLOAD.workerName,
        ExistingPeriodicWorkPolicy.REPLACE,
        worker
      )
      .result
      .get()

    Thread.sleep(5000)

    assertThat(workInfoList.map { it.state })
      .containsAtLeast(
        WorkInfo.State.ENQUEUED, // Waiting for turn
        WorkInfo.State.RUNNING, // Worker launched
        WorkInfo.State.RUNNING, // Progresses emitted Started, InProgress..State.Success
        WorkInfo.State.ENQUEUED // Waiting again for next turn
      )
      .inOrder()

    // States are Started, InProgress .... , Finished (Success)
    assertThat(stateList.map { it::class.java }).contains(State.Finished::class.java)

    val success = (stateList[stateList.size - 1] as State.Finished).result
    assertThat(success.timestamp).isEqualTo(datastoreUtil.readLastSyncTimestamp())

    job1.cancel()
    job2.cancel()
  }

  @Test
  fun `Should run synchronizer and emit states accurately in sequence`() = runBlockingTest {
    whenever(database.getAllLocalChanges()).thenReturn(listOf())
    whenever(dataSource.download(any()))
      .thenReturn(Bundle().apply { type = Bundle.BundleType.SEARCHSET })

    val res = mutableListOf<State>()

    val flow = MutableSharedFlow<State>()
    val job = launch { flow.collect { res.add(it) } }

    syncJob.run(fhirEngine, TestingUtils.TestDownloadManagerImpl(), flow)

    // State transition for successful job as below
    // Started, InProgress, Finished (Success)
    assertThat(res.map { it::class.java })
      .containsExactly(
        State.Started::class.java,
        State.InProgress::class.java,
        State.Finished::class.java
      )
      .inOrder()

    val success = (res[2] as State.Finished).result

    assertThat(success.timestamp).isEqualTo(datastoreUtil.readLastSyncTimestamp())

    job.cancel()
  }

  @Test
  fun `Should run synchronizer and emit  with error accurately in sequence`() = runBlockingTest {
    whenever(database.getAllLocalChanges()).thenReturn(listOf())
    whenever(dataSource.download(any())).thenThrow(IllegalStateException::class.java)

    val res = mutableListOf<State>()

    val flow = MutableSharedFlow<State>()

    val job = launch { flow.collect { res.add(it) } }

    syncJob.run(fhirEngine, TestingUtils.TestDownloadManagerImpl(), flow)
    // State transition for failed job as below
    // Started, InProgress, Glitch, Failed (Error)
    assertThat(res.map { it::class.java })
      .containsExactly(
        State.Started::class.java,
        State.InProgress::class.java,
        State.Glitch::class.java,
        State.Failed::class.java
      )
      .inOrder()

    val error = (res[3] as State.Failed).result

    assertThat(error.timestamp).isEqualTo(datastoreUtil.readLastSyncTimestamp())

    assertThat(error.exceptions[0].exception)
      .isInstanceOf(java.lang.IllegalStateException::class.java)

    job.cancel()
  }

  @Test
  fun `Sync time should update on every sync call`() = runBlockingTest {
    val worker1 = PeriodicWorkRequestBuilder<TestSyncWorker>(15, TimeUnit.MINUTES).build()

    // Get flows return by work manager wrapper
    val stateFlow1 = syncJob.stateFlow()

    val stateList1 = mutableListOf<State>()

    // Convert flows to list to assert later
    val job1 = launch { stateFlow1.toList(stateList1) }

    val currentTimeStamp: OffsetDateTime = OffsetDateTime.now()
    workManager
      .enqueueUniquePeriodicWork(
        SyncWorkType.DOWNLOAD_UPLOAD.workerName,
        ExistingPeriodicWorkPolicy.REPLACE,
        worker1
      )
      .result
      .get()
    Thread.sleep(5000)
    println("After enqueue")
    val firstSyncResult = (stateList1[stateList1.size - 1] as State.Finished).result
    println("After stateList1 size :"+stateList1)
    assertThat(firstSyncResult.timestamp).isGreaterThan(currentTimeStamp)
    assertThat(datastoreUtil.readLastSyncTimestamp()!!).isGreaterThan(currentTimeStamp)
    job1.cancel()

    // Run sync for second time
    val worker2 = PeriodicWorkRequestBuilder<TestSyncWorker>(15, TimeUnit.MINUTES).build()
    val stateFlow2 = syncJob.stateFlow()
    val stateList2 = mutableListOf<State>()
    val job2 = launch { stateFlow2.toList(stateList2) }
    workManager
      .enqueueUniquePeriodicWork(
        SyncWorkType.DOWNLOAD_UPLOAD.workerName,
        ExistingPeriodicWorkPolicy.REPLACE,
        worker2
      )
      .result
      .get()
    Thread.sleep(5000)
    val secondSyncResult = (stateList2[stateList2.size - 1] as State.Finished).result
    assertThat(secondSyncResult.timestamp).isGreaterThan(firstSyncResult.timestamp)
    assertThat(datastoreUtil.readLastSyncTimestamp()!!).isGreaterThan(firstSyncResult.timestamp)
    job2.cancel()
  }

  @Test
  fun `while loop in download keeps running after first exception`() = runBlockingTest {
    whenever(dataSource.download(any()))
      .thenReturn(Bundle())
      .thenThrow(RuntimeException("test"))
      .thenThrow(RuntimeException("anotherOne"))

    whenever(database.getAllLocalChanges()).thenReturn(listOf())

    val res = mutableListOf<State>()

    val flow = MutableSharedFlow<State>()

    val job = launch { flow.collect { res.add(it) } }

    syncJob.run(
      fhirEngine,
      TestingUtils.TestDownloadManagerImplWithQueue(
        listOf("Patient/bob", "Encounter/doc", "Observation/obs")
      ),
      flow
    )

    assertThat(res.map { it::class.java })
      .containsExactly(
        State.Started::class.java,
        State.InProgress::class.java,
        State.Glitch::class.java,
        State.Failed::class.java
      )
      .inOrder()

    val error = (res[3] as State.Failed).result

    assertThat(error.exceptions.size).isEqualTo(2)

    assertThat(error.exceptions[0].exception).isInstanceOf(java.lang.RuntimeException::class.java)
    assertThat(error.exceptions[0].exception.message).isEqualTo("test")
    assertThat(error.exceptions[1].exception.message).isEqualTo("anotherOne")

    job.cancel()
  }

  @Test
  fun `number of resources loaded equals number of resources in TestDownloaderImpl`() =
      runBlockingTest {
    whenever(database.getAllLocalChanges()).thenReturn(listOf())
    whenever(dataSource.download(any())).thenReturn(Bundle())

    val res = mutableListOf<State>()

    val flow = MutableSharedFlow<State>()

    val job = launch { flow.collect { res.add(it) } }

    syncJob.run(
      fhirEngine,
      TestingUtils.TestDownloadManagerImplWithQueue(
        listOf("Patient/bob", "Encounter/doc", "Observation/obs")
      ),
      flow
    )

    assertThat(res.map { it::class.java })
      .containsExactly(
        State.Started::class.java,
        State.InProgress::class.java,
        State.Finished::class.java
      )
      .inOrder()
    job.cancel()

    verify(dataSource, times(3)).download(any())
  }

  @Test
  fun `should fail when there data source is null`() = runBlockingTest {
    whenever(FhirEngineProvider.getDataSource(anyOrNull())).thenReturn(null)
    whenever(database.getAllLocalChanges()).thenReturn(listOf())
    whenever(dataSource.download(any()))
      .thenReturn(Bundle().apply { type = Bundle.BundleType.SEARCHSET })

    val res = mutableListOf<State>()
    val flow = MutableSharedFlow<State>()
    val job = launch { flow.collect { res.add(it) } }

    val result = syncJob.run(fhirEngine, TestingUtils.TestDownloadManagerImplWithQueue(), flow)

    assertThat(res).isEmpty()
    assertThat(result).isInstanceOf(Result.Error::class.java)
    assertThat((result as Result.Error).exceptions.first().exception)
      .isInstanceOf(IllegalStateException::class.java)

    job.cancel()
  }
}
