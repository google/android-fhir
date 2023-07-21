/*
 * Copyright 2023 Google LLC
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

import androidx.annotation.WorkerThread
import okhttp3.Credentials

/**
 * Provides an authorization method for the HTTP requests FHIR Engine sends to the FHIR server.
 *
 * FHIR Engine does not handle user authentication. The application should handle user
 * authentication and provide the appropriate authentication method so the HTTP requests FHIR Engine
 * sends to the FHIR server contain the correct user information for the request to be
 * authenticated.
 *
 * The implementation can provide different `HttpAuthenticationMethod`s at runtime. This is
 * important if the authentication token expires or the user needs to re-authenticate.
 */
fun interface HttpAuthenticator {
  fun getAuthenticationMethod(): HttpAuthenticationMethod
}

/**
 * The HTTP authentication method to be used for generating HTTP authorization header.
 *
 * See https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication.
 */
sealed interface HttpAuthenticationMethod {
  /** @return The authorization header for the engine to make requests on user's behalf. */
  @WorkerThread fun getAuthorizationHeader(): String

  /** See https://datatracker.ietf.org/doc/html/rfc7617. */
  data class Basic(val username: String, val password: String) : HttpAuthenticationMethod {
    override fun getAuthorizationHeader() = Credentials.basic(username, password)
  }

  /** See https://datatracker.ietf.org/doc/html/rfc6750. */
  data class Bearer(val token: String) : HttpAuthenticationMethod {
    override fun getAuthorizationHeader() = "Bearer $token"
  }
}
