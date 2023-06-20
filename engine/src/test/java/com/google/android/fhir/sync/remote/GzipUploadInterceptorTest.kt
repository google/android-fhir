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

import com.google.android.fhir.MediaTypes
import com.google.common.truth.Truth.assertThat
import java.util.zip.GZIPInputStream
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GzipUploadInterceptorTest {

  @JvmField @Rule val server = MockWebServer()
  private val client = OkHttpClient.Builder().addInterceptor(GzipUploadInterceptor).build()

  @Test
  fun `uncompressed request body received by server should match request body sent`() {
    server.enqueue(MockResponse())

    val request =
      Request.Builder()
        .url(server.url("/"))
        .method("POST", "abc".toRequestBody(MediaTypes.MEDIA_TYPE_FHIR_JSON))
        .build()

    client.newCall(request).execute()

    val recordedRequest = server.takeRequest()
    val uncompressedBodyReceived =
      String(GZIPInputStream(recordedRequest.body.inputStream()).use { it.readBytes() })
    assertThat(uncompressedBodyReceived).isEqualTo("abc")
    assertThat(recordedRequest.getHeader(CONTENT_ENCODING_HEADER_NAME)).isEqualTo("gzip")
  }

  @Test
  fun `content length header gets set`() {
    server.enqueue(MockResponse())
    val request =
      Request.Builder()
        .url(server.url("/"))
        .method("POST", "abc".toRequestBody(MediaTypes.MEDIA_TYPE_FHIR_JSON))
        .build()

    client.newCall(request).execute()

    val recordedRequest = server.takeRequest()
    assertThat(recordedRequest.getHeader("Content-Length")?.toLong()).isGreaterThan(-1)
  }

  @Test
  fun `gzip gets appended to content header if another value already exists`() {
    server.enqueue(MockResponse())
    val request =
      Request.Builder()
        .url(server.url("/"))
        .addHeader(CONTENT_ENCODING_HEADER_NAME, "deflate")
        .method("POST", "abc".toRequestBody(MediaTypes.MEDIA_TYPE_FHIR_JSON))
        .build()

    client.newCall(request).execute()

    val recordedRequest = server.takeRequest()
    assertThat(recordedRequest.getHeader(CONTENT_ENCODING_HEADER_NAME)).isEqualTo("deflate, gzip")
  }
  @Test
  fun `no compression happens if the request body is empty`() {
    server.enqueue(MockResponse())
    val request =
      Request.Builder()
        .url(server.url("/"))
        .addHeader(CONTENT_ENCODING_HEADER_NAME, "deflate")
        .method("GET", null)
        .build()

    client.newCall(request).execute()

    val recordedRequest = server.takeRequest()
    assertThat(recordedRequest.getHeader(CONTENT_ENCODING_HEADER_NAME)).doesNotContain("gzip")
  }
}
