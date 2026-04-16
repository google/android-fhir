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

package com.google.android.fhir.db

/**
 * A database encryption exception wrapper which maps comprehensive keystore errors to a limited set
 * of actionable errors.
 */
class DatabaseEncryptionException(cause: Exception, val errorCode: DatabaseEncryptionErrorCode) :
  Exception(cause) {

  enum class DatabaseEncryptionErrorCode {
    /** Unclassified error. The error could potentially be mitigated by recreating the database. */
    UNKNOWN,

    /** Required encryption algorithm is not available. */
    UNSUPPORTED,

    /** Timeout when accessing encrypted database. */
    TIMEOUT,
  }
}
