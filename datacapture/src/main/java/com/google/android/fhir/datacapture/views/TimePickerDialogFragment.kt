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

package com.google.android.fhir.datacapture.views

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.time.LocalTime

internal class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {
  @SuppressLint("NewApi") // Suppress warnings for java.time APIs
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    // Use the current time as the default time in the picker
    val now = LocalTime.now()
    return TimePickerDialog(requireContext(), this, now.hour, now.minute, false)
  }

  override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
    setFragmentResult(
      RESULT_REQUEST_KEY,
      bundleOf(RESULT_BUNDLE_KEY_HOUR to hourOfDay, RESULT_BUNDLE_KEY_MINUTE to minute)
    )
    dismiss()
  }

  companion object {
    const val TAG = "time-picker-fragment"
    const val RESULT_REQUEST_KEY = "time-picker-request-key"
    const val RESULT_BUNDLE_KEY_HOUR = "time-picker-bundle-key-hour"
    const val RESULT_BUNDLE_KEY_MINUTE = "time-picker-bundle-key-minute"
  }
}
