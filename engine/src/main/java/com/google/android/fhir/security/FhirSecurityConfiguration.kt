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

import android.app.PendingIntent
import android.app.admin.DevicePolicyManager.PASSWORD_COMPLEXITY_HIGH
import android.app.admin.DevicePolicyManager.PASSWORD_COMPLEXITY_LOW
import android.app.admin.DevicePolicyManager.PASSWORD_COMPLEXITY_MEDIUM
import java.util.EnumSet

/** Configuration for Android FHIR SDK security mechanism. */
data class FhirSecurityConfiguration(
  val lockScreenRequirement: LockScreenRequirement,
  val warningCallback: PendingIntent? = null
)

/** Sealed class hierarchy representing a security policy. */
sealed interface SecurityRequirement

/**
 * The required lock screen policy for apps integrated with Android FHIR SDK.
 *
 * @param complexity: the lock screen complexity of the device. See
 * [SUPPORTED_LOCK_SCREEN_COMPLEXITIES] for the list of supported values.
 * @param policyViolationActions: a list of actions that the FHIR SDK will trigger when there is a
 * lock screen policy violation.
 */
data class LockScreenRequirement(
  val complexity: Int,
  val policyViolationActions: EnumSet<RequirementViolationAction>
) : SecurityRequirement {
  init {
    require(complexity in SUPPORTED_LOCK_SCREEN_COMPLEXITIES) {
      "Unsupported lock screen complexity: $complexity"
    }
  }

  companion object {
    val SUPPORTED_LOCK_SCREEN_COMPLEXITIES =
      setOf(
        PASSWORD_COMPLEXITY_HIGH,
        PASSWORD_COMPLEXITY_LOW,
        PASSWORD_COMPLEXITY_MEDIUM,
      )
  }
}

/** Supported security measure when a security policy is violated. */
enum class RequirementViolationAction {
  /** When a security policy is violated, a callback provided in [FhirSecurityConfiguration] */
  WARN,
}
