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

internal class JsMediaHandler(
  override val maxSupportedFileSizeBytes: BigDecimal,
  override val supportedMimeTypes: Array<String>,
) : MediaHandler {
  override suspend fun capturePhoto(): MediaCaptureResult {
    TODO("Not yet implemented")
  }

  override suspend fun selectFile(inputMimeTypes: Array<String>): MediaCaptureResult {
    TODO("Not yet implemented")
  }

  override fun isCameraAvailable(): Boolean =
    try {
      js("navigator.mediaDevices && navigator.mediaDevices.getUserMedia") != null
    } catch (_: Throwable) {
      false
    }
}

@Composable
internal actual fun getMediaHandler(
  maxSupportedFileSizeBytes: BigDecimal,
  supportedMimeTypes: Array<String>,
): MediaHandler {
  return remember(maxSupportedFileSizeBytes, supportedMimeTypes) {
    JsMediaHandler(maxSupportedFileSizeBytes, supportedMimeTypes)
  }
}
