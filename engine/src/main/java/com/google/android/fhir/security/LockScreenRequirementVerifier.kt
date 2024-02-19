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
import android.content.Context
import android.os.Build
import timber.log.Timber

/** A verifier for lock screen requirement. */
internal class LockScreenRequirementVerifier(context: Context) :
  SecurityRequirementVerifier<LockScreenRequirement> {
  private val devicePolicyManager = context.getSystemService(DevicePolicyManager::class.java)

  override suspend fun verify(requirement: LockScreenRequirement): SecurityRequirementVerdict {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
      Timber.w("Lock screen requirement is not supported in SDK ${Build.VERSION.SDK_INT}.")
      return SecurityRequirementUnsupported
    }

    return if (devicePolicyManager.passwordComplexity >= requirement.complexity.complexity) {
      Timber.i("The current lock screen satisfies the security requirement.")
      SecurityRequirementMet
    } else {
      Timber.w(
        "The current lock screen doesn't the security requirement. " +
          "Current: ${devicePolicyManager.passwordComplexity}, required: ${requirement.complexity.complexity}."
      )
      LockScreenRequirementViolation(
        requiredComplexity = requirement.complexity.complexity,
        currentComplexity = devicePolicyManager.passwordComplexity
      )
    }
  }
}
