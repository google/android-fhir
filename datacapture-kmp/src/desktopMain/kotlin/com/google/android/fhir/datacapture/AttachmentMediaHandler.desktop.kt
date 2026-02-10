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
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.name
import kotlin.coroutines.cancellation.CancellationException

internal class JvmMediaHandler(
  override val maxSupportedFileSizeBytes: BigDecimal,
  override val supportedMimeTypes: Array<String>,
) : MediaHandler {
  override fun isCameraSupported(): Boolean = false

  override suspend fun capturePhoto(): MediaCaptureResult {
    // Camera capture is not available on desktop through standard APIs
    error("Error: Camera not available")
  }

  override suspend fun selectFile(inputMimeTypes: Array<String>): MediaCaptureResult {
    val pickedFile =
      FileKit.openFilePicker(
        type =
          FileKitType.File(
            inputMimeTypes
              .flatMap { getExtensionsForMimeType(it) }
              .toSet()
              .takeIf { it.isNotEmpty() },
          ),
      )
    return pickedFile?.let {
      val fileName = it.name
      captureResult(
        it.file.readBytes(),
        getMimeTypeFromExtension(fileName.substringAfterLast(".")),
        titleName = fileName,
      )
    }
      ?: throw CancellationException()
  }

  private fun getExtensionsForMimeType(mimeType: String): List<String> {
    return when (mimeType.lowercase()) {
      "image/*" -> listOf("jpg", "jpeg", "png", "gif", "bmp")
      "image/jpeg" -> listOf("jpg", "jpeg")
      "image/png" -> listOf("png")
      "image/gif" -> listOf("gif")
      "application/pdf" -> listOf("pdf")
      "text/*" -> listOf("txt", "md")
      "text/plain" -> listOf("txt")
      "application/msword" -> listOf("doc", "docx")
      "application/vnd.ms-excel" -> listOf("xls", "xlsx")
      "application/vnd.ms-powerpoint" -> listOf("ppt", "pptx")
      "video/*" -> listOf("mp4", "avi", "mov", "wmv")
      "video/mp4" -> listOf("mp4")
      "audio/*" -> listOf("mp3", "wav", "flac", "aac")
      "audio/mpeg" -> listOf("mp3")
      "audio/wav" -> listOf("wav")
      else -> emptyList()
    }
  }

  private fun getMimeTypeFromExtension(extension: String): String {
    return when (extension.lowercase()) {
      "jpg",
      "jpeg", -> "image/jpeg"
      "png" -> "image/png"
      "gif" -> "image/gif"
      "bmp" -> "image/bmp"
      "pdf" -> "application/pdf"
      "txt" -> "text/plain"
      "md" -> "text/markdown"
      "doc" -> "application/msword"
      "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
      "xls" -> "application/vnd.ms-excel"
      "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
      "ppt" -> "application/vnd.ms-powerpoint"
      "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
      "mp4" -> "video/mp4"
      "avi" -> "video/x-msvideo"
      "mov" -> "video/quicktime"
      "wmv" -> "video/x-ms-wmv"
      "mp3" -> "audio/mpeg"
      "wav" -> "audio/wav"
      "flac" -> "audio/flac"
      "aac" -> "audio/aac"
      else -> "application/octet-stream"
    }
  }
}

@Composable
internal actual fun rememberMediaHandler(
  maxSupportedFileSizeBytes: BigDecimal,
  supportedMimeTypes: Array<String>,
): MediaHandler {
  return remember(maxSupportedFileSizeBytes, supportedMimeTypes) {
    JvmMediaHandler(maxSupportedFileSizeBytes, supportedMimeTypes)
  }
}
