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

package com.google.android.fhir

import co.touchlab.kermit.Logger

/** Kermit-based logging for engine-kmp, replacing Timber (Android-only). */
internal object FhirEngineLog {
  private val logger = Logger.withTag("FhirEngine")

  fun w(message: String) {
    logger.w { message }
  }

  fun d(message: String) {
    logger.d { message }
  }

  fun e(message: String, throwable: Throwable? = null) {
    if (throwable != null) {
      logger.e(throwable) { message }
    } else {
      logger.e { message }
    }
  }

  fun i(message: String) {
    logger.i { message }
  }

  fun withTag(tag: String): TaggedLog = TaggedLog(tag)
}

internal class TaggedLog(tag: String) {
  private val logger = Logger.withTag(tag)

  fun w(message: String) {
    logger.w { message }
  }

  fun d(message: String) {
    logger.d { message }
  }

  fun e(message: String, throwable: Throwable? = null) {
    if (throwable != null) {
      logger.e(throwable) { message }
    } else {
      logger.e { message }
    }
  }

  fun i(message: String) {
    logger.i { message }
  }
}
