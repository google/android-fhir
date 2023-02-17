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

import android.app.Application
import android.app.PendingIntent
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.work.testing.TestListenableWorkerBuilder
import com.google.android.fhir.FhirEngineConfiguration
import com.google.android.fhir.FhirEngineProvider
import com.google.android.fhir.security.LockScreenComplexity.HIGH
import com.google.common.truth.Truth.assertThat
import java.util.EnumSet
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

/** Unit tests for [SecurityCheckWorker]. */
@RunWith(RobolectricTestRunner::class)
class SecurityCheckWorkerTest {

  private val context: Context = ApplicationProvider.getApplicationContext()

  @After
  fun tearDown() {
    FhirEngineProvider.forceCleanup()
  }

  @Test
  fun securityPolicyMet_shouldNotNotifyPendingIntent() = runBlocking {
    val pendingIntent =
      PendingIntent.getBroadcast(
        ApplicationProvider.getApplicationContext(),
        /* requestCode= */ 0,
        Intent("TEST_ACTION"),
        /* flags= */ 0
      )
    val config =
      FhirEngineConfiguration(
        securityConfiguration =
          FhirSecurityConfiguration(
            LockScreenRequirement(HIGH, EnumSet.noneOf(RequirementViolationAction::class.java)),
            warningCallback = pendingIntent
          )
      )
    FhirEngineProvider.init(config)
    shadowOf(context.getSystemService(DevicePolicyManager::class.java))
      .setPasswordComplexity(DevicePolicyManager.PASSWORD_COMPLEXITY_HIGH)

    TestListenableWorkerBuilder<SecurityCheckWorker>(
        context = context,
      )
      .build()
      .doWork()

    assertThat(shadowOf(context as Application).broadcastIntents).isEmpty()
  }

  @Test
  fun securityPolicyMet_shouldHaveZeroViolation() = runBlocking {
    val pendingIntent =
      PendingIntent.getBroadcast(
        ApplicationProvider.getApplicationContext(),
        /* requestCode= */ 0,
        Intent("TEST_ACTION"),
        /* flags= */ 0
      )
    val config =
      FhirEngineConfiguration(
        securityConfiguration =
          FhirSecurityConfiguration(
            LockScreenRequirement(HIGH, EnumSet.noneOf(RequirementViolationAction::class.java)),
            warningCallback = pendingIntent
          )
      )
    FhirEngineProvider.init(config)
    shadowOf(context.getSystemService(DevicePolicyManager::class.java))
      .setPasswordComplexity(DevicePolicyManager.PASSWORD_COMPLEXITY_HIGH)

    TestListenableWorkerBuilder<SecurityCheckWorker>(
        context = context,
      )
      .build()
      .doWork()

    assertThat(FhirEngineProvider.getSecurityRequirementsManager(context).getLatestViolations())
      .isEmpty()
  }

  @Test
  fun securityPolicyViolated_shouldNotifyPendingIntent() = runBlocking {
    val pendingIntent =
      PendingIntent.getBroadcast(
        ApplicationProvider.getApplicationContext(),
        /* requestCode= */ 0,
        Intent("TEST_ACTION"),
        /* flags= */ 0
      )
    val config =
      FhirEngineConfiguration(
        securityConfiguration =
          FhirSecurityConfiguration(
            LockScreenRequirement(HIGH, EnumSet.noneOf(RequirementViolationAction::class.java)),
            warningCallback = pendingIntent
          )
      )
    FhirEngineProvider.init(config)
    shadowOf(context.getSystemService(DevicePolicyManager::class.java))
      .setPasswordComplexity(DevicePolicyManager.PASSWORD_COMPLEXITY_LOW)

    TestListenableWorkerBuilder<SecurityCheckWorker>(
        context = context,
      )
      .build()
      .doWork()

    assertThat(shadowOf(context as Application).broadcastIntents.single().action)
      .isEqualTo("TEST_ACTION")
  }

  @Test
  fun securityPolicyViolated_shouldHaveOneViolation() = runBlocking {
    val pendingIntent =
      PendingIntent.getBroadcast(
        ApplicationProvider.getApplicationContext(),
        /* requestCode= */ 0,
        Intent("TEST_ACTION"),
        /* flags= */ 0
      )
    val config =
      FhirEngineConfiguration(
        securityConfiguration =
          FhirSecurityConfiguration(
            LockScreenRequirement(HIGH, EnumSet.noneOf(RequirementViolationAction::class.java)),
            warningCallback = pendingIntent
          )
      )
    FhirEngineProvider.init(config)
    shadowOf(context.getSystemService(DevicePolicyManager::class.java))
      .setPasswordComplexity(DevicePolicyManager.PASSWORD_COMPLEXITY_LOW)

    TestListenableWorkerBuilder<SecurityCheckWorker>(
        context = context,
      )
      .build()
      .doWork()

    assertThat(
        FhirEngineProvider.getSecurityRequirementsManager(context).getLatestViolations().single()
      )
      .isEqualTo(
        LockScreenRequirementViolation(
          requiredComplexity = DevicePolicyManager.PASSWORD_COMPLEXITY_HIGH,
          currentComplexity = DevicePolicyManager.PASSWORD_COMPLEXITY_LOW
        )
      )
  }
}
