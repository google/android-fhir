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

import com.google.common.truth.Truth.assertThat
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.util.ReflectionHelpers

@RunWith(RobolectricTestRunner::class)
class MoreHttpLoggerKtTest {

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
  fun `toOkHttpLoggingInterceptor headersToRedact should be empty when headersToIgnore is not provided`() {
    assertThat(
        httpLogger(HttpLogger.Level.BODY, emptyList()).toOkHttpLoggingInterceptor().redactHeaders
      )
      .isEmpty()
  }

  @Test
  fun `toOkHttpLoggingInterceptor headersToIgnore should populate headersToRedact`() {
    assertThat(
        httpLogger(HttpLogger.Level.BODY, listOf("Authorization", "content-type"))
          .toOkHttpLoggingInterceptor()
          .redactHeaders
      )
      .containsExactly("Authorization", "content-type")
  }

  private fun httpLogger(level: HttpLogger.Level, headersToIgnore: List<String>? = null) =
    HttpLogger(HttpLogger.Configuration(level, headersToIgnore)) {}

  private val HttpLoggingInterceptor.redactHeaders: Collection<String>
    get() = ReflectionHelpers.getField(this, "headersToRedact")
}
