/*
 * Copyright 2025 Google LLC
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

package com.google.android.fhir.datacapture

import android.icu.number.NumberFormatter
import android.icu.text.DecimalFormat
import android.os.Build
import java.util.Locale

actual object NumberFormatter {
  actual fun formatInteger(value: Int): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      NumberFormatter.withLocale(Locale.getDefault()).format(value).toString()
    } else {
      DecimalFormat.getInstance(Locale.getDefault()).format(value)
    }
  }
}
