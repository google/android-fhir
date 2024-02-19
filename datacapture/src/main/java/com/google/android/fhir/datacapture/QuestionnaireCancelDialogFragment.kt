/*
 * Copyright 2022-2023 Google LLC
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

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder

internal class QuestionnaireCancelDialogFragment : DialogFragment() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return MaterialAlertDialogBuilder(requireContext())
      .setMessage(R.string.questionnaire_cancel_text)
      .setPositiveButton(android.R.string.ok) { dialog, _ ->
        setFragmentResult(REQUEST_KEY, bundleOf(RESULT_KEY to RESULT_YES))
        dialog?.dismiss()
      }
      .setNegativeButton(android.R.string.cancel) { dialog, _ ->
        setFragmentResult(REQUEST_KEY, bundleOf(RESULT_KEY to RESULT_NO))
        dialog?.dismiss()
      }
      .create()
  }

  companion object {
    const val TAG = "QuestionnaireCancelDialogFragment"
    const val REQUEST_KEY = "request-key"
    const val RESULT_KEY = "result-key"
    const val RESULT_NO = "no"
    const val RESULT_YES = "yes"
  }
}
