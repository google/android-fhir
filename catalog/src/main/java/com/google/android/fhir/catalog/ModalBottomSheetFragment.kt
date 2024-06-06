/*
 * Copyright 2021-2024 Google LLC
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

package com.google.android.fhir.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ModalBottomSheetFragment : BottomSheetDialogFragment() {
  private val args: ModalBottomSheetFragmentArgs by navArgs()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {
    return inflater.inflate(R.layout.fragment_modal_bottom_sheet, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val showHideErrorCheckBox = view.findViewById<CheckBox>(R.id.errorToggleCheckBox)
    showHideErrorCheckBox.isChecked = args.errorState
    showHideErrorCheckBox.setOnCheckedChangeListener { _, isChecked ->
      setFragmentResult(
        REQUEST_ERROR_KEY,
        bundleOf(
          BUNDLE_ERROR_KEY to isChecked,
        ),
      )
    }
    (activity as? MainActivity)?.showOpenQuestionnaireMenu(false)
  }

  companion object {
    const val REQUEST_ERROR_KEY = "errorRequestKey"
    const val BUNDLE_ERROR_KEY = "errorBundleKey"
  }
}
