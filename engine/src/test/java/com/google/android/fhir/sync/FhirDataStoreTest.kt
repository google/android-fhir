/*
 * Copyright 2023 Google LLC
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

import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class FhirDataStoreTest {
  private val fhirDataStore = FhirDataStore(ApplicationProvider.getApplicationContext())

  @Test
  fun observeSyncJobTerminalState() = runBlocking {
    val key = "key"
    val collectedValues = mutableListOf<SyncJobStatus>()
    val editJob = launch { fhirDataStore.updateSyncJobTerminalState(key, SyncJobStatus.Finished) }
    val collectJob = launch {
      collectedValues.add(fhirDataStore.observeSyncJobTerminalState(key).filterNotNull().first())
    }
    collectJob.join()
    editJob.join()
    assertTrue(collectedValues[0] is SyncJobStatus.Finished)
  }

  @Test
  fun getLastSyncJobStatus() = runBlocking {
    val key = "key"
    val collectedValues = mutableListOf<SyncJobStatus?>()
    val editJob = launch { fhirDataStore.updateLastSyncJobStatus(key, SyncJobStatus.Finished) }
    collectedValues.add(fhirDataStore.getLastSyncJobStatus(key))
    editJob.join()
    assertTrue(fhirDataStore.getLastSyncJobStatus(key) is SyncJobStatus.Finished)
  }
}
