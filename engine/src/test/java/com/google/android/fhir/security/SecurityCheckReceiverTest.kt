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

package com.google.android.fhir.security

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.work.Configuration
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

/** Unit tests for [SecurityCheckReceiver]. */
@RunWith(RobolectricTestRunner::class)
class SecurityCheckReceiverTest {

  private val context: Context = ApplicationProvider.getApplicationContext()

  @Before
  fun setUp() {
    WorkManagerTestInitHelper.initializeTestWorkManager(
      context,
      Configuration.Builder().setExecutor(SynchronousExecutor()).build()
    )
  }

  @Test
  fun bootComplete_shouldEnqueueWorker() {
    context.sendBroadcast(Intent(Intent.ACTION_BOOT_COMPLETED).setPackage(context.packageName))

    // Wait for broadcast queue to finishWorkManagerTestInitHelper.initializeTestWorkManager(
    Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable()

    val workInfo =
      WorkManager.getInstance(context)
        .getWorkInfosForUniqueWork(SecurityCheckReceiver.SECURITY_CHECK_WORKER_NAME)
        .get()
        .single()
    assertThat(workInfo.state).isEqualTo(WorkInfo.State.ENQUEUED)
  }

  @Test
  fun appUpdate_shouldEnqueueWorker() {
    context.sendBroadcast(Intent(Intent.ACTION_MY_PACKAGE_REPLACED).setPackage(context.packageName))

    // Wait for broadcast queue to finish
    Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable()

    val workInfo =
      WorkManager.getInstance(context)
        .getWorkInfosForUniqueWork(SecurityCheckReceiver.SECURITY_CHECK_WORKER_NAME)
        .get()
        .single()
    assertThat(workInfo.state).isEqualTo(WorkInfo.State.ENQUEUED)
  }
}
