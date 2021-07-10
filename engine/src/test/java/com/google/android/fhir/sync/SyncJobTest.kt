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

import android.os.Build
import com.google.android.fhir.FhirEngine
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SyncJobTest {
  @Mock
  private lateinit var fhirEngine: FhirEngine

  @Mock
  private lateinit var dataSource: DataSource

  private lateinit var syncJob: SyncJobImpl


  private val testDispatcher = TestCoroutineDispatcher()

  @Before
  fun setup(){
    MockitoAnnotations.openMocks(this)

    var resourceSyncParam = mapOf(ResourceType.Patient to mapOf("address-city" to "NAIROBI"))

    syncJob = SyncJobImpl(testDispatcher, fhirEngine, dataSource, resourceSyncParam)
  }

  @Test
  fun test() = runBlockingTest {
    val res = mutableListOf<Result>()

    syncJob.subscribe().collect {
      res.add(it)
    }
    
    launch {
      syncJob.run()

      assertEquals(Result.Started, res[0])
      assertEquals(5, res.size)
    }

    testDispatcher.advanceTimeBy(30000)
    syncJob.close()
  }

  @Test
  fun `should poll accurately with given delay`() = runBlockingTest {
    // the duration to run the flow
    val duration = 1000L
    val delay = 100L

    // polling with given delay
    val flow = syncJob.poll(delay)

    launch {
      // consume elements generated in given duration and given delay
      val dataList = flow.take(10).toList()
      assertEquals(dataList.size, 10)
    }

    // wait until job is completed
    testDispatcher.advanceTimeBy(duration)

    syncJob.close()
  }

  @Test
  fun `should poll accurately with given delay and initial delay`() = runBlockingTest {
    // the duration required to run the test
    val duration = 1040L
    val delay = 100L
    val initialDelay = 40L

    val flow = syncJob.poll(delay, initialDelay)

    launch {
      // consume elements generated in given duration and given delay
      val dataList = flow.take(10).toList()
      assertEquals(dataList.size, 10)
    }

    // wait until job is completed
    testDispatcher.advanceTimeBy(duration)

    syncJob.close()
  }
}
