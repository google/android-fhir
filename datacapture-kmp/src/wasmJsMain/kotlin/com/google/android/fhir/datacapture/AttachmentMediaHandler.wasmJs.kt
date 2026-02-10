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
import io.github.vinceglb.filekit.mimeType
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlin.coroutines.cancellation.CancellationException

internal class WasmMediaHandler(
  override val maxSupportedFileSizeBytes: BigDecimal,
  override val supportedMimeTypes: Array<String>,
) : MediaHandler {
  override suspend fun capturePhoto(): MediaCaptureResult {
    error("Error: Camera not supported")
  }

  override suspend fun selectFile(inputMimeTypes: Array<String>): MediaCaptureResult {
    val pickedFile =
      FileKit.openFilePicker(
        type =
          FileKitType.File(
            inputMimeTypes.toSet().takeIf { it.isNotEmpty() },
          ),
      )

    return pickedFile?.let {
      captureResult(
        it.readBytes(),
        mimeType = it.mimeType()?.toString() ?: "application/octet-stream",
        titleName = it.name,
      )
    }
      ?: throw CancellationException()
  }

  override fun isCameraSupported(): Boolean = false
}

@Composable
internal actual fun rememberMediaHandler(
  maxSupportedFileSizeBytes: BigDecimal,
  supportedMimeTypes: Array<String>,
): MediaHandler {
  return remember(maxSupportedFileSizeBytes, supportedMimeTypes) {
    WasmMediaHandler(maxSupportedFileSizeBytes, supportedMimeTypes)
  }
}
