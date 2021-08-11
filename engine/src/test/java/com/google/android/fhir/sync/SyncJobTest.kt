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
import com.google.android.fhir.DatastoreUtil
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.db.Database
import com.google.android.fhir.impl.FhirEngineImpl
import com.google.common.truth.Truth.assertThat
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
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
  private lateinit var resourceSyncParam: Map<ResourceType, Map<String, String>>

  private val database = mock<Database>()
  private val dataSource = mock<DataSource>()

  @get:Rule var instantExecutorRule = InstantTaskExecutorRule()

  private lateinit var syncJob: SyncJob

  @Before
  fun setup() {
    fhirEngine = FhirEngineImpl(database, context)

    resourceSyncParam = mapOf(ResourceType.Patient to mapOf("address-city" to "NAIROBI"))

    syncJob = Sync.basicSyncJob(context)

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

    // get flows return by work manager wrapper
    val workInfoFlow = syncJob.workInfoFlow()
    val stateFlow = syncJob.stateFlow()

    val workInfoList = mutableListOf<WorkInfo>()
    val stateList = mutableListOf<State>()

    // convert flows to list to assert later
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
        WorkInfo.State.ENQUEUED, // waiting for turn
        WorkInfo.State.RUNNING, // worker launched
        WorkInfo.State.RUNNING, // progresses emitted Started, InProgress..State.Success
        WorkInfo.State.ENQUEUED // waiting again for next turn
      )
      .inOrder()

    // States are  Started, InProgress .... , Finished (Success)
    assertThat(stateList.map { it::class.java }).contains(State.Finished::class.java)

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

    syncJob.run(fhirEngine, dataSource, resourceSyncParam, flow)

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
  fun `should run synchronizer and emit  with error accurately in sequence`() = runBlockingTest {
    whenever(database.getAllLocalChanges()).thenReturn(listOf())
    whenever(dataSource.loadData(any())).thenThrow(IllegalStateException::class.java)

    val res = mutableListOf<State>()

    val flow = MutableSharedFlow<State>()

    val job = launch { flow.collect { res.add(it) } }

    syncJob.run(fhirEngine, dataSource, resourceSyncParam, flow)

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
}
