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

import android.app.admin.DevicePolicyManager
import android.app.admin.DevicePolicyManager.PASSWORD_COMPLEXITY_HIGH
import android.app.admin.DevicePolicyManager.PASSWORD_COMPLEXITY_LOW
import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import java.util.EnumSet
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

/** Unit tests for [LockScreenRequirementVerifierTest]. */
@RunWith(RobolectricTestRunner::class)
class LockScreenRequirementVerifierTest {
  private val context: Context = ApplicationProvider.getApplicationContext()
  private val lockScreenRequirementVerifier = LockScreenRequirementVerifier(context)
  private val devicePolicyManager = context.getSystemService(DevicePolicyManager::class.java)

  @Test
  @Config(minSdk = Build.VERSION_CODES.Q)
  fun lockScreenRequirementNotMet_shouldReturnViolationVerdict() = runBlocking {
    shadowOf(devicePolicyManager).setPasswordComplexity(PASSWORD_COMPLEXITY_LOW)

    val verdict =
      lockScreenRequirementVerifier.verify(
        LockScreenRequirement(
          PASSWORD_COMPLEXITY_HIGH,
          EnumSet.noneOf(RequirementViolationAction::class.java)
        )
      )

    assertThat(verdict)
      .isEqualTo(
        LockScreenRequirementViolation(
          requiredComplexity = PASSWORD_COMPLEXITY_HIGH,
          currentComplexity = PASSWORD_COMPLEXITY_LOW
        )
      )
  }

  @Test
  @Config(minSdk = Build.VERSION_CODES.Q)
  fun lockScreenRequirementMet_requiredPasswordComplexityHigh_shouldReturnSecurityRequirementMet() =
    runBlocking {
      shadowOf(devicePolicyManager).setPasswordComplexity(PASSWORD_COMPLEXITY_HIGH)

      val verdict =
        lockScreenRequirementVerifier.verify(
          LockScreenRequirement(
            PASSWORD_COMPLEXITY_HIGH,
            EnumSet.noneOf(RequirementViolationAction::class.java)
          )
        )

      assertThat(verdict).isEqualTo(SecurityRequirementMet)
    }

  @Test
  @Config(maxSdk = Build.VERSION_CODES.P)
  fun lockScreenRequirementNotSupported_shouldReturnSecurityRequirementUnsupported() = runBlocking {
    shadowOf(devicePolicyManager).setPasswordComplexity(PASSWORD_COMPLEXITY_HIGH)

    val verdict =
      lockScreenRequirementVerifier.verify(
        LockScreenRequirement(
          PASSWORD_COMPLEXITY_HIGH,
          EnumSet.noneOf(RequirementViolationAction::class.java)
        )
      )

    assertThat(verdict).isEqualTo(SecurityRequirementUnsupported)
  }
}
