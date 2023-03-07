/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir.datacapture.views.attachment

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.fhir.datacapture.views.factories.AttachmentViewHolderFactory

/** Used for launching open document activity */
class OpenDocumentLauncherFragment : DialogFragment() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val mimeTypes = arguments?.getStringArray(AttachmentViewHolderFactory.EXTRA_MIME_TYPE_KEY)

    registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        setFragmentResult(OPEN_DOCUMENT_RESULT_KEY, bundleOf(OPEN_DOCUMENT_RESULT_KEY to uri))
        dismiss()
      }
      .launch(mimeTypes)
  }

  companion object {
    const val OPEN_DOCUMENT_RESULT_KEY = "open_document_result"
  }
}
