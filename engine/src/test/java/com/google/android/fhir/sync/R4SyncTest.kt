/*
 * Copyright 2022 Google LLC
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
import androidx.work.BackoffPolicy
import androidx.work.WorkerParameters
import com.google.android.fhir.r4.R4FhirEngine
import com.google.android.fhir.r4.sync.R4DataSource
import com.google.android.fhir.r4.sync.R4DownloadWorkManager
import com.google.android.fhir.r4.sync.R4FhirSyncWorker
import com.google.android.fhir.r4.sync.R4Sync
import com.google.android.fhir.resource.TestingUtils
import com.google.common.truth.Truth.assertThat
import java.util.concurrent.TimeUnit
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class R4SyncTest {
  class PassingPeriodicSyncWorkerR4(appContext: Context, workerParams: WorkerParameters) :
    R4FhirSyncWorker(appContext, workerParams) {

    override fun getFhirEngine(): R4FhirEngine = TestingUtils.TestFhirEngineImpl
    override fun getDataSource(): R4DataSource = TestingUtils.TestR4DataSourceImpl
    override fun getDownloadWorkManager(): R4DownloadWorkManager =
      TestingUtils.TestDownloadManagerImpl()
    override fun getConflictResolver() = AcceptRemoteConflictResolver
  }

  @Test
  fun createOneTimeWorkRequestWithRetryConfiguration_shouldHave3MaxTries() {
    val workRequest =
      R4Sync.createOneTimeWorkRequest(
        RetryConfiguration(BackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.SECONDS), 3),
        PassingPeriodicSyncWorkerR4::class.java
      )
    assertThat(workRequest.workSpec.backoffPolicy).isEqualTo(BackoffPolicy.LINEAR)
    assertThat(workRequest.workSpec.backoffDelayDuration).isEqualTo(TimeUnit.SECONDS.toMillis(30))
    assertThat(workRequest.workSpec.input.getInt(MAX_RETRIES_ALLOWED, 0)).isEqualTo(3)
  }

  @Test
  fun createOneTimeWorkRequest_withoutRetryConfiguration_shouldHaveZeroMaxTries() {
    val workRequest = R4Sync.createOneTimeWorkRequest(null, PassingPeriodicSyncWorkerR4::class.java)
    assertThat(workRequest.workSpec.input.getInt(MAX_RETRIES_ALLOWED, 0)).isEqualTo(0)
    //    Not checking [workRequest.workSpec.backoffPolicy] and
    // [workRequest.workSpec.backoffDelayDuration] as they have default values.
  }

  @Test
  fun createPeriodicWorkRequest_withRetryConfiguration_shouldHave3MaxTries() {
    val workRequest =
      R4Sync.createPeriodicWorkRequest(
        PeriodicSyncConfiguration(
          repeat = RepeatInterval(20, TimeUnit.MINUTES),
          retryConfiguration =
            RetryConfiguration(BackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.SECONDS), 3)
        ),
        PassingPeriodicSyncWorkerR4::class.java
      )
    assertThat(workRequest.workSpec.intervalDuration).isEqualTo(TimeUnit.MINUTES.toMillis(20))
    assertThat(workRequest.workSpec.backoffPolicy).isEqualTo(BackoffPolicy.LINEAR)
    assertThat(workRequest.workSpec.backoffDelayDuration).isEqualTo(TimeUnit.SECONDS.toMillis(30))
    assertThat(workRequest.workSpec.input.getInt(MAX_RETRIES_ALLOWED, 0)).isEqualTo(3)
  }

  @Test
  fun createPeriodicWorkRequest_withoutRetryConfiguration_shouldHaveZeroMaxRetries() {
    val workRequest =
      R4Sync.createPeriodicWorkRequest(
        PeriodicSyncConfiguration(
          repeat = RepeatInterval(20, TimeUnit.MINUTES),
          retryConfiguration = null
        ),
        PassingPeriodicSyncWorkerR4::class.java
      )
    assertThat(workRequest.workSpec.intervalDuration).isEqualTo(TimeUnit.MINUTES.toMillis(20))
    assertThat(workRequest.workSpec.input.getInt(MAX_RETRIES_ALLOWED, 0)).isEqualTo(0)
  }
}
