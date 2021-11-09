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

package com.google.android.fhir.reference.util

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

/** Hides soft keyboard. */
inline fun Activity.hideSoftKeyboard() {
  currentFocus?.let { view ->
    // Clear focus to avoid showing soft keyboard again if activity is launched again
    view.clearFocus()
    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
      view.windowToken,
      0
    )
  }
}
