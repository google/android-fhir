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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
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
  fun updateSyncJobStatus() = runBlocking {
    val key = "key"
    val collectedValues = mutableListOf<SyncJobStatus>()
    val syncJobStatusList =
      listOf(
        SyncJobStatus.Started(),
        SyncJobStatus.InProgress(SyncOperation.DOWNLOAD, 256, 20),
        SyncJobStatus.Finished()
      )
    val editJob = launch {
      syncJobStatusList.forEach { fhirDataStore.updateSyncJobStatus(key, it) }
    }
    val collectJob = launch {
      fhirDataStore.getSyncJobStatusPreferencesFlow(key).take(3).collect { syncJobStatus ->
        collectedValues.add(syncJobStatus)
      }
    }
    collectJob.join()
    editJob.join()
    assertTrue(syncJobStatusList.first() is SyncJobStatus.Started)
    assertEquals(syncJobStatusList[1], collectedValues[1])
    assertTrue(syncJobStatusList[2] is SyncJobStatus.Finished)
  }
}
