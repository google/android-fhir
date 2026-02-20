/*
 * Copyright 2026 Google LLC
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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import co.touchlab.kermit.Logger
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.compressImage
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openCameraPicker
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.mimeType
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UniformTypeIdentifiers.UTType

internal class IosMediaHandler(
  override val maxSupportedFileSizeBytes: BigDecimal,
  override val supportedMimeTypes: Array<String>,
) : MediaHandler {

  override suspend fun capturePhoto(): MediaCaptureResult {
    val currentAuthorizedStatus = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
    return when (currentAuthorizedStatus) {
      AVAuthorizationStatusAuthorized -> {
        val pickedFile = FileKit.openCameraPicker()
        pickedFile?.let {
          captureResult(
            FileKit.compressImage(it),
            titleName = it.name,
            mimeType = it.mimeType()?.toString() ?: "application/octet-stream",
          )
        }
          ?: throw CancellationException()
      }
      AVAuthorizationStatusNotDetermined -> {
        suspendCancellableCoroutine { continuation ->
          AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { isGranted ->
            continuation.resumeWith(Result.success(Unit))
          }
        }
        throw CancellationException() // cancel camera picker request to reset and allow user to
        // pick again
      }
      AVAuthorizationStatusDenied ->
        MediaCaptureResult.Error("Error: Camera permission not granted")
      else -> {
        Logger.e("unknown camera permission status $currentAuthorizedStatus")
        MediaCaptureResult.Error("Error: Camera permission not granted")
      }
    }
  }

  override suspend fun selectFile(inputMimeTypes: Array<String>): MediaCaptureResult {
    val imageOnly = inputMimeTypes.all { it.startsWith("image/") }
    val fileKitType =
      if (imageOnly) {
        FileKitType.Image
      } else {
        val extensions =
          inputMimeTypes.mapNotNull { getExtensionFromMimeType(it) } +
            inputMimeTypes.flatMap { getExtensionsForWildcardMimeType(it) }
        FileKitType.File(
          extensions.toSet().takeIf { it.isNotEmpty() },
        )
      }
    val pickedFile = FileKit.openFilePicker(type = fileKitType)

    return pickedFile?.let {
      captureResult(
        if (fileKitType == FileKitType.Image) FileKit.compressImage(it) else it.readBytes(),
        mimeType = it.mimeType()?.toString() ?: "application/octet-stream",
        titleName = it.name,
      )
    }
      ?: throw CancellationException()
  }

  override fun isCameraSupported(): Boolean =
    UIImagePickerController.isSourceTypeAvailable(
      UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera,
    )

  private fun getExtensionsForWildcardMimeType(mimeType: String): List<String> {
    return when (mimeType.lowercase()) {
      "image/*" -> listOf("jpg", "jpeg", "png", "gif", "bmp")
      "text/*" -> listOf("txt", "md")
      "video/*" -> listOf("mp4", "avi", "mov", "wmv")
      "audio/*" -> listOf("mp3", "wav", "flac", "aac", "m4a")
      else -> emptyList()
    }
  }

  private fun getExtensionFromMimeType(mimeType: String): String? {
    val type = UTType.typeWithMIMEType(mimeType)
    return type?.preferredFilenameExtension
  }
}

@Composable
internal actual fun rememberMediaHandler(
  maxSupportedFileSizeBytes: BigDecimal,
  supportedMimeTypes: Array<String>,
): MediaHandler {
  return remember(maxSupportedFileSizeBytes, supportedMimeTypes) {
    IosMediaHandler(maxSupportedFileSizeBytes, supportedMimeTypes)
  }
}
