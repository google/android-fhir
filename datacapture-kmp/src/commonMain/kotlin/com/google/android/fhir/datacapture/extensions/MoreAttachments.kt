/*
 * Copyright 2025 Google LLC
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

package com.google.android.fhir.datacapture.extensions

import androidx.compose.ui.text.capitalize
import com.google.android.fhir.datacapture.UrlResolver
import com.google.fhir.model.r4.Attachment
import com.google.fhir.model.r4.Base64Binary
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.text.capitalize

internal suspend fun Attachment.imageData(): ByteArray? {
  check(mimeType == MimeType.IMAGE.value) {
    "${mimeType?.capitalize()} attachment is not supported in Item Media extension yet"
  }

  return data?.data ?: url?.value?.let { imageUrlResolver().resolveBitmapUrl(it) }
}

/** Returns the main MIME type of a MIME type string (e.g. image/png returns image). */
internal val Attachment.mimeType: String?
  get() = contentType?.value?.substringBefore("/")

@OptIn(ExperimentalEncodingApi::class)
internal val Base64Binary.data: ByteArray?
  get() = value?.let { Base64.decode(it) }

@Suppress("KotlinNoActualForExpect") expect fun imageUrlResolver(): UrlResolver
