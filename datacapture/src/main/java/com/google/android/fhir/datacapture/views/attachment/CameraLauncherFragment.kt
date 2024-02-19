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

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.fhir.datacapture.views.factories.AttachmentViewHolderFactory
import timber.log.Timber

/** Used for launching camera activity */
class CameraLauncherFragment : DialogFragment() {

  private lateinit var cameraLauncher: ActivityResultLauncher<Uri>

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val uri = arguments?.get(AttachmentViewHolderFactory.EXTRA_SAVED_PHOTO_URI_KEY) as Uri

    cameraLauncher =
      registerForActivityResult(ActivityResultContracts.TakePicture()) { isSaved ->
        setFragmentResult(CAMERA_RESULT_KEY, bundleOf(CAMERA_RESULT_KEY to isSaved))
        dismiss()
      }

    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) ==
        PackageManager.PERMISSION_DENIED
    ) {
      registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
          if (isGranted) {
            Timber.d("Camera permission granted")
            cameraLauncher.launch(uri)
          } else {
            Timber.d("Camera permission not granted")
            dismiss()
          }
        }
        .launch(Manifest.permission.CAMERA)
    } else {
      cameraLauncher.launch(uri)
    }
  }

  companion object {
    const val CAMERA_RESULT_KEY = "camera_result"
  }
}
