/*
 * Copyright 2020 Google LLC
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

package com.google.android.fhir.reference

interface NavigationDrawer {
  /**
   * Sets the [LOCK_MODE_UNLOCKED] or [LOCK_MODE_UNLOCKED] value to drawer layout.
   *
   * @param enabled if true sets LOCK_MODE_UNLOCKED else LOCK_MODE_UNLOCKED to drawer layout
   */
  fun setDrawerEnabled(enabled: Boolean)

  /** Opens navigation drawer from start. */
  fun openNavigationDrawer()
}
