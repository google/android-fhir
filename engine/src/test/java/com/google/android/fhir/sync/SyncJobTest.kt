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
import androidx.test.core.app.ActivityScenario.launch
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SyncJobTest {
  private val testDispatcher = TestCoroutineDispatcher()
  private val poller = SyncJobImpl(testDispatcher)

  @Test
  fun `should poll accurately with given delay`() = runBlockingTest {
    // the duration to run the flow
    val duration = 1000L
    val delay = 100L

    // polling with given delay
    val flow = poller.poll(delay, null)

    launch {
      // consume elements generated in given duration and given delay
      val dataList = flow.take(10).toList()
      assertEquals(dataList.size, 10)
    }

    // wait until job is completed
    testDispatcher.advanceTimeBy(duration)

    poller.close()
  }
}
