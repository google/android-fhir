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

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

internal class ProgressInterceptor : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()

    val progressCallback = request.tag(ProgressCallback::class.java)
    if (progressCallback != null) {
      return chain.proceed(wrapRequest(request, progressCallback))
    }

    return chain.proceed(request)
  }

  private fun wrapRequest(request: Request, progressCallback: ProgressCallback): Request {
    return request
      .newBuilder()
      // TODO ??????????? Assume that any request tagged with a ProgressCallback is a POST
      // request and has a non-null body
      .post(ProgressRequestBody(request.body!!, progressCallback))
      .build()
  }
}
