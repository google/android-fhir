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

/** A centralized hub for checking all supported security requirements. */
class SecurityRequirementsManager
constructor(context: Context, private val securityConfiguration: FhirSecurityConfiguration?) {

  private val lockScreenRequirementVerifier = LockScreenRequirementVerifier(context)

  private val violations = mutableSetOf<SecurityRequirementVerdict>()

  internal suspend fun checkSecurityRequirements() {
    if (securityConfiguration == null) return

    val lockScreenRequirementVerdict =
      lockScreenRequirementVerifier.verify(securityConfiguration.lockScreenRequirement)
    if (lockScreenRequirementVerdict is LockScreenRequirementViolation) {
      violations.add(lockScreenRequirementVerdict)
    }

    if (violations.isNotEmpty()) securityConfiguration.warningCallback?.let { it.send() }
  }

  /** Gets the latest list of security requirement violations. */
  fun getLatestViolations(): List<SecurityRequirementVerdict> = violations.toList()
}
