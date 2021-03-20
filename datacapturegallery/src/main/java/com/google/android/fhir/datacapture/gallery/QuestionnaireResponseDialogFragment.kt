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

package com.google.android.fhir.datacapture.gallery

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.fhir.datacapture.gallery.databinding.QuestionnaireResponseDialogContentsBinding

class QuestionnaireResponseDialogFragment() : DialogFragment() {
  private var _binding: QuestionnaireResponseDialogContentsBinding? = null
  private val binding
    get() = _binding!!
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val contents = requireArguments().getString(BUNDLE_KEY_CONTENTS)
    return activity?.let {
      _binding = QuestionnaireResponseDialogContentsBinding.inflate(layoutInflater)
      binding.contents.text = contents

      AlertDialog.Builder(it).setView(binding.root).create()
    }
      ?: throw IllegalStateException("Activity cannot be null")
  }

  companion object {
    const val TAG = "questionnaire-response-dialog-fragment"
    const val BUNDLE_KEY_CONTENTS = "contents"
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
