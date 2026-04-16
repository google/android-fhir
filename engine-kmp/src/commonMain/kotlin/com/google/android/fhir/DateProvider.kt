/*
 * Copyright 2025-2026 Google LLC
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

package com.google.android.fhir

import kotlin.time.Clock
import kotlin.time.Instant

/** The DateProvider instance [FhirEngine] uses for date/time related operations. */
internal object DateProvider {
  private var fixedInstant: Instant? = null

  /**
   * Returns the current [Instant]. If a fixed instant has been set via [setFixed], returns that
   * instead.
   */
  fun now(): Instant = fixedInstant ?: Clock.System.now()

  /** Fixes the clock to always return the given [instant]. */
  fun setFixed(instant: Instant) {
    fixedInstant = instant
  }

  /** Resets the clock to use the system clock. */
  fun resetClock() {
    fixedInstant = null
  }
}
