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

package com.google.android.fhir.sync.remote

import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import okio.BufferedSink
import okio.GzipSink
import okio.buffer

const val CONTENT_ENCODING_HEADER_NAME = "Content-Encoding"

/** Compresses upload requests with gzip. */
object GzipUploadInterceptor : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val uncompressedRequest = chain.request()
    if (uncompressedRequest.body == null) {
      return chain.proceed(uncompressedRequest)
    }

    val encodingHeader =
      if (uncompressedRequest.header(CONTENT_ENCODING_HEADER_NAME) != null) {
        "${uncompressedRequest.header(CONTENT_ENCODING_HEADER_NAME)}, gzip"
      } else {
        "gzip"
      }

    val compressedRequest =
      uncompressedRequest
        .newBuilder()
        .header(CONTENT_ENCODING_HEADER_NAME, encodingHeader)
        .method(uncompressedRequest.method, addContentLength(gzip(uncompressedRequest.body!!)))
        .build()

    return chain.proceed(compressedRequest)
  }

  private fun gzip(body: RequestBody): RequestBody =
    object : RequestBody() {
      override fun contentType(): MediaType? = body.contentType()

      override fun writeTo(sink: BufferedSink) {
        val gzipBufferedSink: BufferedSink = GzipSink(sink).buffer()
        body.writeTo(gzipBufferedSink)
        gzipBufferedSink.close()
      }
    }

  private fun addContentLength(requestBody: RequestBody): RequestBody {
    val buffer = Buffer()
    requestBody.writeTo(buffer)
    return object : RequestBody() {
      override fun contentType(): MediaType? = requestBody.contentType()

      override fun contentLength(): Long = buffer.size

      override fun writeTo(sink: BufferedSink) {
        sink.write(buffer.snapshot())
      }
    }
  }
}
