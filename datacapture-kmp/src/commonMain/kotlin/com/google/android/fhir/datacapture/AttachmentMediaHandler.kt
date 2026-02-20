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
import com.google.android.fhir.datacapture.extensions.FhirR4String
import com.google.fhir.model.r4.Attachment
import com.google.fhir.model.r4.Base64Binary
import com.google.fhir.model.r4.Code
import com.google.fhir.model.r4.DateTime
import com.google.fhir.model.r4.FhirDateTime
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import kotlin.io.encoding.Base64
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetAt
import kotlinx.datetime.toLocalDateTime

internal sealed interface MediaCaptureResult {
  data class Success(val attachment: Attachment) : MediaCaptureResult

  data class Error(val error: String) : MediaCaptureResult
}

internal interface MediaHandler {
  val maxSupportedFileSizeBytes: BigDecimal
  val supportedMimeTypes: Array<String>

  /**
   * Captures a photo using the device camera.
   *
   * @return MediaCaptureResult containing the Attachment or error message
   */
  suspend fun capturePhoto(): MediaCaptureResult

  /**
   * Opens file picker to select a file.
   *
   * @param inputMimeTypes Array of acceptable MIME types
   * @param supportedMimeTypes Function to check if a MIME type is supported
   * @return MediaCaptureResult containing the Attachment or error message
   */
  suspend fun selectFile(
    inputMimeTypes: Array<String>,
  ): MediaCaptureResult

  fun isCameraSupported(): Boolean
}

internal fun MediaHandler.isMimeTypeSupported(mimeType: String) =
  supportedMimeTypes.any { it.substringBefore("/") == mimeType }

@OptIn(ExperimentalTime::class)
internal fun MediaHandler.captureResult(
  byteArray: ByteArray,
  mimeType: String,
  titleName: String,
): MediaCaptureResult {
  if (byteArray.size.toBigDecimal() > maxSupportedFileSizeBytes) {
    return MediaCaptureResult.Error(
      "Error: File size is larger than the allowed ${maxSupportedFileSizeBytes.div(1048576L)} MB",
    )
  }

  val byteArrayString = Base64.encode(byteArray)
  val currentTimeZone = TimeZone.currentSystemDefault()

  val attachment =
    Attachment(
      contentType = Code(value = mimeType),
      data = Base64Binary(value = byteArrayString),
      title = FhirR4String(value = titleName),
      creation =
        DateTime(
          value =
            FhirDateTime.DateTime(
              dateTime = Clock.System.now().toLocalDateTime(currentTimeZone),
              utcOffset = currentTimeZone.offsetAt(Clock.System.now()),
            ),
        ),
    )
  return MediaCaptureResult.Success(attachment)
}

@Composable
internal expect fun rememberMediaHandler(
  maxSupportedFileSizeBytes: BigDecimal,
  supportedMimeTypes: Array<String>,
): MediaHandler
