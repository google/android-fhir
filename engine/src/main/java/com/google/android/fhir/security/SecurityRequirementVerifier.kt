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

/** An interface for a security requirement verifier. */
internal interface SecurityRequirementVerifier<R : SecurityRequirement> {
  /** Checks if the given security requirement is fulfilled. */
  suspend fun verify(requirement: R): SecurityRequirementVerdict
}
/**
 * A class hierarchy for security requirement verdict.
 *
 * This sealed interface isn't exhaustive. Don't use it with `when`.
 */
sealed interface SecurityRequirementVerdict

/** Represents a verdict, which the security requirement can't be verified. */
object SecurityRequirementUnsupported : SecurityRequirementVerdict

/** Represents a verdict, which the security requirement has been met. */
object SecurityRequirementMet : SecurityRequirementVerdict

/** Represents a violation of lock screen requirement. */
class LockScreenRequirementViolation(val requiredComplexity: Int, val currentComplexity: Int) :
  SecurityRequirementVerdict
