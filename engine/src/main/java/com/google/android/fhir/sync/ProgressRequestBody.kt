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

package com.google.android.fhir.sync

import android.os.Handler
import android.os.Looper
import com.google.android.fhir.percentof
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import okio.Sink
import okio.buffer

internal class ProgressRequestBody(
  private val delegate: RequestBody,
  private val callback: ProgressCallback,
) : RequestBody() {
  override fun contentType(): MediaType? = delegate.contentType()
  override fun contentLength(): Long = delegate.contentLength()

  override fun writeTo(sink: BufferedSink) {
    val countingSink = CountingSink(sink).buffer()
    delegate.writeTo(countingSink)
    countingSink.flush()
  }

  private inner class CountingSink(delegate: Sink) : ForwardingSink(delegate) {
    private val handler = Handler(Looper.getMainLooper())
    private val totalBytes = contentLength()
    private var uploadedBytes = 0L

    override fun write(source: Buffer, byteCount: Long) {
      super.write(source, byteCount)
      uploadedBytes += byteCount

      handler.post { runBlocking { callback.onProgress(percentof(uploadedBytes, totalBytes)) } }
    }
  }
}
