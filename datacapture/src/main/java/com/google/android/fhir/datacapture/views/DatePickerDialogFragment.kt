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
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.time.LocalDate

internal class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
  @SuppressLint("NewApi") // Suppress warnings for java.time APIs
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    // Use the current date as the default date in the picker
    val today = LocalDate.now()

    // Create a new instance of DatePickerDialog and return it
    return DatePickerDialog(
      requireContext(),
      this,
      today.year,
      // month values are 1-12 in java.time but DatePickerDialog expects 0-11
      today.monthValue - 1,
      today.dayOfMonth
    )
  }

  override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
    setFragmentResult(
      RESULT_REQUEST_KEY,
      bundleOf(
        RESULT_BUNDLE_KEY_YEAR to year,
        RESULT_BUNDLE_KEY_MONTH to month,
        RESULT_BUNDLE_KEY_DAY_OF_MONTH to dayOfMonth
      )
    )
    dismiss()
  }

  companion object {
    const val TAG = "date-picker-fragment"
    const val RESULT_REQUEST_KEY = "date-picker-request-key"
    const val RESULT_BUNDLE_KEY_YEAR = "date-picker-bundle-key-year"
    const val RESULT_BUNDLE_KEY_MONTH = "date-picker-bundle-key-month"
    const val RESULT_BUNDLE_KEY_DAY_OF_MONTH = "date-picker-bundle-day-of-month"
  }
}
