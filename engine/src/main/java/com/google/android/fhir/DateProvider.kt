/*
 * Copyright 2021-2023 Google LLC
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

import java.time.Clock
import java.time.Instant
import java.time.ZoneId

/** The DateProvider Instance [FhirEngine] uses for date/time related operations. */
object DateProvider {
  lateinit var clock: Clock
  private var fixed = false
  // TODO possibly provide more customization options
  /**
   * * Returns the cached [clock] instance. If an Instant is passed , subsequent calls to the
   *   function will return a clock fixed to [instant]. To reset to a normal (unfixed) clock use
   *   [resetClock].
   */
  operator fun invoke(instant: Instant? = null): Clock =
    synchronized(this) {
      if (!::clock.isInitialized) {
        clock =
          if (instant == null) {
            Clock.systemDefaultZone()
          } else {
            fixed = true
            Clock.fixed(instant, ZoneId.systemDefault())
          }
      }
      // to change instant to another fixed time
      if (instant != null) {
        if (!fixed || clock.instant() != instant) {
          clock = Clock.fixed(instant, ZoneId.systemDefault())
        }
      }
      return clock
    }

  fun resetClock() = synchronized(this) { clock = Clock.systemDefaultZone() }
}
