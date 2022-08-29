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

package com.google.android.fhir.sync.progress

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.hl7.fhir.r4.model.codesystems.HttpVerb

/**
 * Adds an interceptor to notify progress for requests if request is tagged with [ProgressCallback].
 */
internal class ProgressInterceptor : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()

    return request.tag(ProgressCallback::class.java)?.let { callback ->
      chain.proceed(wrapRequest(request, callback))
    }
      ?: chain.proceed(request)
  }

  private fun wrapRequest(request: Request, progressCallback: ProgressCallback): Request {
    // do request wrapping for non GET requests only
    return if (HttpVerb.fromCode(request.method) == HttpVerb.GET) request
    else
      request
        .newBuilder()
        .let {
          val wrappedBody = ProgressRequestBody(request.body!!, progressCallback)
          when (HttpVerb.fromCode(request.method)) {
            HttpVerb.POST -> it.post(wrappedBody)
            HttpVerb.PUT -> it.put(wrappedBody)
            HttpVerb.DELETE -> it.delete(wrappedBody)
            HttpVerb.PATCH -> it.patch(wrappedBody)
            else ->
              throw IllegalArgumentException(
                "${request.method} not allowed for progress update request wrapper"
              )
          }
        }
        .build()
  }
}
