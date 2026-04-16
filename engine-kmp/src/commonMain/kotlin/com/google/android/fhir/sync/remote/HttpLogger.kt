/*
 * Copyright 2025-2026 Google LLC
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

/**
 * Configuration for logging HTTP communication between the engine and the remote FHIR server.
 *
 * @property level The level of detail to log.
 * @property headersToIgnore A set of header names to exclude from logged output.
 */
data class HttpLogger(
  val level: Level = Level.NONE,
  val headersToIgnore: Set<String> = emptySet(),
) {
  enum class Level {
    /** No logs. */
    NONE,

    /** Logs request and response lines. */
    BASIC,

    /** Logs request and response lines and their respective headers. */
    HEADERS,

    /** Logs request and response lines, headers, and bodies. */
    BODY,
  }

  companion object {
    val NONE = HttpLogger()
  }
}
