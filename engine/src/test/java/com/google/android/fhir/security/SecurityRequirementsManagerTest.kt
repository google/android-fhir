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
import android.app.admin.DevicePolicyManager.PASSWORD_COMPLEXITY_HIGH
import android.app.admin.DevicePolicyManager.PASSWORD_COMPLEXITY_LOW
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.truth.content.IntentSubject.assertThat
import com.google.android.fhir.security.LockScreenComplexity.HIGH
import com.google.android.fhir.security.LockScreenComplexity.LOW
import com.google.android.fhir.security.SecurityRequirementViolation.EXTRA_LOCK_SCREEN_REQUIREMENT_VIOLATION
import com.google.common.truth.Truth.assertThat
import java.util.EnumSet
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

/** Unit tests for [LockScreenRequirementVerifierTest]. */
@RunWith(RobolectricTestRunner::class)
class SecurityRequirementsManagerTest {

  private val context: Context = ApplicationProvider.getApplicationContext()
  private val devicePolicyManager = context.getSystemService(DevicePolicyManager::class.java)

  @Test
  fun checkSecurityRequirements_lockScreenRequirementMet_shouldNotInvokeWarningCallback() =
    runBlocking {
      shadowOf(devicePolicyManager).setPasswordComplexity(PASSWORD_COMPLEXITY_HIGH)

      SecurityRequirementsManager(
          context,
          FhirSecurityConfiguration(
            LockScreenRequirement(LOW, EnumSet.noneOf(RequirementViolationAction::class.java)),
            warningCallback = createWarningCallback(context)
          ),
        )
        .checkSecurityRequirements()

      assertThat(shadowOf(context as Application).broadcastIntents).isEmpty()
    }

  @Test
  fun getLatestViolation_lockScreenRequirementMet_shouldReturnEmptyList() = runBlocking {
    shadowOf(devicePolicyManager).setPasswordComplexity(PASSWORD_COMPLEXITY_HIGH)
    val securityRequirementsManager =
      SecurityRequirementsManager(
        context,
        FhirSecurityConfiguration(
          LockScreenRequirement(LOW, EnumSet.noneOf(RequirementViolationAction::class.java)),
          warningCallback = createWarningCallback(context)
        ),
      )
    securityRequirementsManager.checkSecurityRequirements()

    assertThat(securityRequirementsManager.getLatestViolations()).isEmpty()
  }

  @Test
  fun checkSecurityRequirements_lockScreenRequirementViolated_shouldInvokeWarningCallback() =
    runBlocking {
      shadowOf(devicePolicyManager).setPasswordComplexity(PASSWORD_COMPLEXITY_LOW)

      SecurityRequirementsManager(
          context,
          FhirSecurityConfiguration(
            LockScreenRequirement(HIGH, EnumSet.noneOf(RequirementViolationAction::class.java)),
            warningCallback = createWarningCallback(context)
          )
        )
        .checkSecurityRequirements()

      val callbackIntent = shadowOf(context as Application).broadcastIntents.single()
      assertThat(callbackIntent).hasAction("TEST_ACTION")
      assertThat(callbackIntent)
        .extras()
        .parcelable<LockScreenRequirementViolation>(EXTRA_LOCK_SCREEN_REQUIREMENT_VIOLATION)
        .isEqualTo(
          LockScreenRequirementViolation(
            requiredComplexity = PASSWORD_COMPLEXITY_HIGH,
            currentComplexity = PASSWORD_COMPLEXITY_LOW
          )
        )
    }

  @Test
  fun getLatestViolation_lockScreenRequirementNotMet_shouldReturnLockScreenRequirementViolation() =
    runBlocking {
      shadowOf(devicePolicyManager).setPasswordComplexity(PASSWORD_COMPLEXITY_LOW)
      val securityRequirementsManager =
        SecurityRequirementsManager(
          context,
          FhirSecurityConfiguration(
            LockScreenRequirement(HIGH, EnumSet.noneOf(RequirementViolationAction::class.java)),
            warningCallback = createWarningCallback(context)
          ),
        )
      securityRequirementsManager.checkSecurityRequirements()

      assertThat(securityRequirementsManager.getLatestViolations().single())
        .isEqualTo(
          LockScreenRequirementViolation(
            requiredComplexity = PASSWORD_COMPLEXITY_HIGH,
            currentComplexity = PASSWORD_COMPLEXITY_LOW
          )
        )
    }

  private companion object {
    fun createWarningCallback(context: Context) =
      PendingIntent.getBroadcast(
        context,
        /* requestCode= */ 0,
        Intent("TEST_ACTION"),
        /* flags= */ 0
      )
  }
}
