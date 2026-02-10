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

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.init
import io.github.vinceglb.filekit.dialogs.openCameraPicker
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.mimeType
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import java.io.File
import kotlin.coroutines.cancellation.CancellationException
import kotlin.use
import kotlinx.coroutines.suspendCancellableCoroutine

internal class AndroidMediaHandler(
  private val activityResultRegistry: ActivityResultRegistry,
  private val context: Context,
  override val maxSupportedFileSizeBytes: BigDecimal,
  override val supportedMimeTypes: Array<String>,
) : MediaHandler {

  init {
    FileKit.init(activityResultRegistry)
  }

  override suspend fun capturePhoto(): MediaCaptureResult {
    val isCameraPermissionGranted = requestCameraPermission()
    if (!isCameraPermissionGranted) {
      return MediaCaptureResult.Error("Error: Camera permission not granted")
    }
    val pickedFile = FileKit.openCameraPicker()
    return pickedFile?.let {
      captureResult(
        it.readBytes(),
        titleName = it.name,
        mimeType = it.mimeType()?.toString() ?: "application/octet-stream",
      )
    }
      ?: throw CancellationException()
  }

  override suspend fun selectFile(
    inputMimeTypes: Array<String>,
  ): MediaCaptureResult {
    val imageOnly = inputMimeTypes.all { it.startsWith("image/") }

    val fileKitType =
      if (imageOnly) {
        FileKitType.Image
      } else {
        FileKitType.File(
          inputMimeTypes.toSet().takeIf { it.isNotEmpty() },
        )
      }
    val pickedFile = FileKit.openFilePicker(type = fileKitType)

    return pickedFile?.let {
      captureResult(
        it.readBytes(),
        mimeType = it.mimeType()?.toString() ?: "application/octet-stream",
        titleName = it.name,
      )
    }
      ?: throw CancellationException()
  }

  override fun isCameraSupported(): Boolean =
    context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

  private suspend fun requestCameraPermission(): Boolean =
    suspendCancellableCoroutine { continuation ->
      val requestPermissionLauncher =
        activityResultRegistry.register(
          CAMERA_REQUEST_PERMISSION_RESULT_CONTRACT_KEY,
          ActivityResultContracts.RequestPermission(),
        ) {
          continuation.resumeWith(Result.success(it))
        }

      continuation.invokeOnCancellation { requestPermissionLauncher.unregister() }

      if (
        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
          PackageManager.PERMISSION_GRANTED
      ) {
        continuation.resumeWith(Result.success(true))
      } else {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
      }
    }

  private suspend fun launchTakePicture(): Pair<String, Uri> =
    suspendCancellableCoroutine { continuation ->
      val file = File.createTempFile("IMG_", ".jpeg", context.cacheDir)
      val fileUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

      val takePickerLauncher =
        activityResultRegistry.register(
          TAKE_PICTURE_RESULT_CONTRACT_KEY,
          ActivityResultContracts.TakePicture(),
        ) { success ->
          if (!success) {
            continuation.cancel()
          } else {
            continuation.resumeWith(Result.success(file.name to fileUri))
          }
        }

      continuation.invokeOnCancellation {
        takePickerLauncher.unregister()
        if (file.exists()) {
          file.delete()
        }
      }

      takePickerLauncher.launch(fileUri)
    }

  private suspend fun pickPhoto(): Uri = suspendCancellableCoroutine { continuation ->
    val photoVideoLauncher =
      activityResultRegistry.register(
        SELECT_PHOTO_RESULT_CONTRACT_KEY,
        ActivityResultContracts.PickVisualMedia(),
      ) { uri ->
        if (uri == null) {
          continuation.cancel()
        } else {
          continuation.resumeWith(Result.success(uri))
        }
      }
    continuation.invokeOnCancellation { photoVideoLauncher.unregister() }
    photoVideoLauncher.launch(
      PickVisualMediaRequest(
        mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly,
      ),
    )
  }

  private suspend fun pickFile(fileTypes: Array<String>): Uri =
    suspendCancellableCoroutine { continuation ->
      val openFilePickerLauncher =
        activityResultRegistry.register(
          SELECT_FILE_RESULT_CONTRACT_KEY,
          ActivityResultContracts.OpenDocument(),
        ) { uri ->
          if (uri == null) {
            continuation.cancel()
          } else {
            continuation.resumeWith(Result.success(uri))
          }
        }

      continuation.invokeOnCancellation { openFilePickerLauncher.unregister() }
      openFilePickerLauncher.launch(fileTypes)
    }

  private fun createAttachment(fileUri: Uri, fileTitleName: String): MediaCaptureResult {
    val attachmentByteArray = context.readBytesFromUri(fileUri)

    val attachmentMimeTypeWithSubType = context.getMimeTypeFromUri(fileUri)
    val attachmentMimeType = attachmentMimeTypeWithSubType.substringBefore("/")
    if (!isMimeTypeSupported(attachmentMimeType)) {
      return MediaCaptureResult.Error("Error: Wrong media format")
    }

    return captureResult(
      byteArray = attachmentByteArray,
      mimeType = attachmentMimeTypeWithSubType,
      titleName = fileTitleName,
    )
  }

  companion object {
    private const val CAMERA_REQUEST_PERMISSION_RESULT_CONTRACT_KEY =
      "com.google.android.fhir.datacapture.AndroidMediaHandler.CameraPermission"
    private const val SELECT_FILE_RESULT_CONTRACT_KEY =
      "com.google.android.fhir.datacapture.AndroidMediaHandler.SelectFile"
    private const val SELECT_PHOTO_RESULT_CONTRACT_KEY =
      "com.google.android.fhir.datacapture.AndroidMediaHandler.SelectPhoto"
    private const val TAKE_PICTURE_RESULT_CONTRACT_KEY =
      "com.google.android.fhir.datacapture.AndroidMediaHandler.TakePicture"
  }
}

private fun Context.getFileName(uri: Uri): String {
  var fileName = ""
  val columns = arrayOf(OpenableColumns.DISPLAY_NAME)
  contentResolver.query(uri, columns, null, null, null)?.use { cursor ->
    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    if (cursor.moveToFirst() && nameIndex >= 0) {
      fileName = cursor.getString(nameIndex) ?: ""
    }
  }
  return fileName
}

internal fun Context.readBytesFromUri(uri: Uri): ByteArray {
  return contentResolver.openInputStream(uri)?.use { it.buffered().readBytes() } ?: ByteArray(0)
}

internal fun Context.getMimeTypeFromUri(uri: Uri): String {
  return contentResolver.getType(uri) ?: "*/*"
}

@Composable
internal actual fun rememberMediaHandler(
  maxSupportedFileSizeBytes: BigDecimal,
  supportedMimeTypes: Array<String>,
): MediaHandler {
  val context = LocalContext.current
  val activityResultRegistry = LocalActivityResultRegistryOwner.current!!.activityResultRegistry
  val lifecycleOwner = LocalLifecycleOwner.current

  return remember(
    context,
    activityResultRegistry,
    lifecycleOwner,
    maxSupportedFileSizeBytes,
    supportedMimeTypes,
  ) {
    AndroidMediaHandler(
      activityResultRegistry,
      context,
      maxSupportedFileSizeBytes,
      supportedMimeTypes,
    )
  }
}
