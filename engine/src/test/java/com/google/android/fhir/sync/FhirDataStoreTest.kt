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

import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.cancel
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
    val editJob = launch {
      fhirDataStore.writeTerminalSyncJobStatus(key, SyncJobStatus.Succeeded())
      fhirDataStore.observeTerminalSyncJobStatus(key).collect {
        assertTrue(it is SyncJobStatus.Succeeded)
        this.cancel()
      }
    }
    editJob.join()
  }
}
