/*
 * Copyright 2022-2023 Google LLC
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

import com.google.common.truth.Truth.assertThat
import java.util.concurrent.TimeUnit
import okhttp3.Call
import okhttp3.Connection
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MoreHttpLoggersTest {

  @Test
  fun `toOkHttpLoggingInterceptor HttpLoggingInterceptor Level should match provided HttpLogger Level`() {
    assertThat(httpLogger(HttpLogger.Level.NONE).toOkHttpLoggingInterceptor().level)
      .isEqualTo(HttpLoggingInterceptor.Level.NONE)
    assertThat(httpLogger(HttpLogger.Level.BASIC).toOkHttpLoggingInterceptor().level)
      .isEqualTo(HttpLoggingInterceptor.Level.BASIC)
    assertThat(httpLogger(HttpLogger.Level.HEADERS).toOkHttpLoggingInterceptor().level)
      .isEqualTo(HttpLoggingInterceptor.Level.HEADERS)
    assertThat(httpLogger(HttpLogger.Level.BODY).toOkHttpLoggingInterceptor().level)
      .isEqualTo(HttpLoggingInterceptor.Level.BODY)
  }

  @Test
  fun `toOkHttpLoggingInterceptor all headers should be logged when headersToIgnore is not provided`() {
    val logMessages =
      intercept(
        level = HttpLogger.Level.BODY,
        headersToIgnore = emptyList(),
        requestHeaders =
          listOf(
            "Restricted" to "request-restricted-value",
            "Unrestricted" to "request-unrestricted-value",
          ),
        responseHeaders =
          listOf(
            "Restricted" to "response-restricted-value",
            "Unrestricted" to "response-unrestricted-value",
          ),
      )
    assertThat(logMessages)
      .containsAtLeast(
        "Restricted: request-restricted-value",
        "Restricted: response-restricted-value",
        "Unrestricted: request-unrestricted-value",
        "Unrestricted: response-unrestricted-value",
      )
  }

  @Test
  fun `toOkHttpLoggingInterceptor provided headersToIgnore should not be logged`() {
    val logMessages =
      intercept(
        level = HttpLogger.Level.BODY,
        headersToIgnore = listOf("Restricted"),
        requestHeaders =
          listOf(
            "Restricted" to "request-restricted-value",
            "Unrestricted" to "request-unrestricted-value",
          ),
        responseHeaders =
          listOf(
            "Restricted" to "response-restricted-value",
            "Unrestricted" to "response-unrestricted-value",
          ),
      )

    assertThat(logMessages)
      .containsAtLeast(
        "Unrestricted: request-unrestricted-value",
        "Unrestricted: response-unrestricted-value",
      )
    assertThat(logMessages)
      .containsNoneOf(
        "Restricted: request-restricted-value",
        "Restricted: response-restricted-value",
      )
  }

  private fun httpLogger(level: HttpLogger.Level, headersToIgnore: List<String>? = null) =
    HttpLogger(HttpLogger.Configuration(level, headersToIgnore)) {}

  private fun intercept(
    level: HttpLogger.Level,
    headersToIgnore: List<String>? = null,
    requestHeaders: List<Pair<String, String>>,
    responseHeaders: List<Pair<String, String>>,
  ): List<String> {
    val logMessages = mutableListOf<String>()
    HttpLogger(HttpLogger.Configuration(level, headersToIgnore)) { logMessages.add(it) }
      .toOkHttpLoggingInterceptor()
      .intercept(TestInterceptorChain(requestHeaders, responseHeaders))
    return logMessages
  }

  class TestInterceptorChain(
    private val requestHeaders: List<Pair<String, String>>,
    private val responseHeaders: List<Pair<String, String>>,
  ) : Interceptor.Chain {

    override fun connection(): Connection? = null

    override fun proceed(request: Request) =
      Response.Builder()
        .code(200)
        .request(request())
        .protocol(Protocol.HTTP_2)
        .message("OK")
        .body("Sample-Response".toResponseBody())
        .apply { responseHeaders.forEach { header(it.first, it.second) } }
        .build()

    override fun request() =
      Request.Builder()
        .url("http://server.test.url")
        .get()
        .apply { requestHeaders.forEach { header(it.first, it.second) } }
        .build()

    override fun connectTimeoutMillis() = 1000

    override fun readTimeoutMillis() = 1000

    override fun withConnectTimeout(timeout: Int, unit: TimeUnit) = apply {}

    override fun withReadTimeout(timeout: Int, unit: TimeUnit) = apply {}

    override fun withWriteTimeout(timeout: Int, unit: TimeUnit) = apply {}

    override fun writeTimeoutMillis() = 1000

    override fun call(): Call {
      TODO("Not yet implemented")
    }
  }
}
