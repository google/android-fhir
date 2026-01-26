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
import java.nio.file.Files
import java.nio.file.Paths
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class JvmMediaHandler(
  override val maxSupportedFileSizeBytes: BigDecimal,
  override val supportedMimeTypes: Array<String>,
) : MediaHandler {
  override suspend fun capturePhoto(): MediaCaptureResult {
    // Camera capture is not available on desktop through standard APIs
    error("Error: Camera not available")
  }

  override suspend fun selectFile(inputMimeTypes: Array<String>): MediaCaptureResult {
    return try {
      val (fileName, byteArray) = pickFile(inputMimeTypes)
      captureResult(
        byteArray,
        getMimeTypeFromExtension(fileName.substringAfterLast(".")),
        titleName = fileName,
      )
    } catch (e: Exception) {
      if (e is CancellationException) {
        throw e // rethrow
      }

      MediaCaptureResult.Error("Error: selecting file failed ${e.message}")
    }
  }

  override fun isCameraAvailable(): Boolean = false

  private suspend fun pickFile(mimeTypes: Array<String>): Pair<String, ByteArray> {
    val fileChooser = JFileChooser()

    mimeTypes.forEach {
      val extensions = getExtensionsForMimeType(it)
      if (extensions.isNotEmpty()) {
        val filter = FileNameExtensionFilter("$it files", *extensions.toTypedArray())
        fileChooser.addChoosableFileFilter(filter)
      }
    }

    val result = fileChooser.showOpenDialog(null)
    if (result == JFileChooser.APPROVE_OPTION) {
      val selectedFile = fileChooser.selectedFile
      val fileBytes =
        withContext(Dispatchers.IO) { Files.readAllBytes(Paths.get(selectedFile.absolutePath)) }
      return selectedFile.name to fileBytes
    } else {
      throw CancellationException()
    }
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
internal actual fun getMediaHandler(
  maxSupportedFileSizeBytes: BigDecimal,
  supportedMimeTypes: Array<String>,
): MediaHandler {
  return remember(maxSupportedFileSizeBytes, supportedMimeTypes) {
    JvmMediaHandler(maxSupportedFileSizeBytes, supportedMimeTypes)
  }
}
