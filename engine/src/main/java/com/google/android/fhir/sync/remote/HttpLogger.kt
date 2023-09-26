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

import androidx.annotation.WorkerThread

/** Logger for the network communication between the engine and the remote server */
class HttpLogger(val configuration: Configuration, @WorkerThread val log: (String) -> Unit) {

  data class Configuration(
    val level: Level,
    /** Http headers to be ignored for the logging purpose. */
    val headersToIgnore: List<String>? = null,
  )

  /** Different levels to specify the content to be logged. */
  enum class Level {
    /** Nothing will be logged. */
    NONE,

    /** Request and response lines will be logged. */
    BASIC,

    /** Lines along with the headers will be logged for the request and response. */
    HEADERS,

    /** Lines, headers and body (if present) will be logged for the request and response. */
    BODY,
  }

  companion object {
    /** The logger will not log any data. */
    val NONE = HttpLogger(Configuration(Level.NONE)) {}
  }
}
