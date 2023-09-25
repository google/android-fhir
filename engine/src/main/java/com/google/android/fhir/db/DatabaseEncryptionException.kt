/*
 * Copyright 2021-2023 Google LLC
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

import com.google.android.fhir.db.DatabaseEncryptionException.DatabaseEncryptionErrorCode.TIMEOUT
import com.google.android.fhir.db.DatabaseEncryptionException.DatabaseEncryptionErrorCode.UNKNOWN
import com.google.android.fhir.db.DatabaseEncryptionException.DatabaseEncryptionErrorCode.UNSUPPORTED
import com.google.android.fhir.db.KeyStoreExceptionErrorCode.ERROR_SECURE_HW_BUSY
import com.google.android.fhir.db.KeyStoreExceptionErrorCode.ERROR_SECURE_HW_COMMUNICATION_FAILED
import com.google.android.fhir.db.KeyStoreExceptionErrorCode.ERROR_UNSUPPORTED_ALGORITHM
import com.google.android.fhir.db.KeyStoreExceptionErrorCode.ERROR_UNSUPPORTED_BLOCK_MODE
import com.google.android.fhir.db.KeyStoreExceptionErrorCode.ERROR_UNSUPPORTED_DIGEST
import com.google.android.fhir.db.KeyStoreExceptionErrorCode.ERROR_UNSUPPORTED_EC_FIELD
import com.google.android.fhir.db.KeyStoreExceptionErrorCode.ERROR_UNSUPPORTED_KEY_ENCRYPTION_ALGORITHM
import com.google.android.fhir.db.KeyStoreExceptionErrorCode.ERROR_UNSUPPORTED_KEY_FORMAT
import com.google.android.fhir.db.KeyStoreExceptionErrorCode.ERROR_UNSUPPORTED_KEY_SIZE
import com.google.android.fhir.db.KeyStoreExceptionErrorCode.ERROR_UNSUPPORTED_KEY_VERIFICATION_ALGORITHM
import com.google.android.fhir.db.KeyStoreExceptionErrorCode.ERROR_UNSUPPORTED_MAC_LENGTH
import com.google.android.fhir.db.KeyStoreExceptionErrorCode.ERROR_UNSUPPORTED_MIN_MAC_LENGTH
import com.google.android.fhir.db.KeyStoreExceptionErrorCode.ERROR_UNSUPPORTED_PADDING_MODE
import com.google.android.fhir.db.KeyStoreExceptionErrorCode.ERROR_UNSUPPORTED_PURPOSE
import com.google.android.fhir.db.KeyStoreExceptionErrorCode.ERROR_UNSUPPORTED_TAG
import java.security.KeyStoreException

/**
 * An database encryption exception wrapper which maps comprehensive keystore errors to a limited
 * set of actionable errors.
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

val KeyStoreException.databaseEncryptionException: DatabaseEncryptionException
  get() {
    message?.let {
      "-[0-9]+".toRegex().find(it)?.let { matchResult ->
        matchResult.value.toIntOrNull()?.let { errorCode ->
          val encryptionException =
            when (errorCode) {
              // Unsupported: these errors can't be fixed by retries.
              ERROR_UNSUPPORTED_PURPOSE,
              ERROR_UNSUPPORTED_ALGORITHM,
              ERROR_UNSUPPORTED_KEY_SIZE,
              ERROR_UNSUPPORTED_BLOCK_MODE,
              ERROR_UNSUPPORTED_MAC_LENGTH,
              ERROR_UNSUPPORTED_PADDING_MODE,
              ERROR_UNSUPPORTED_DIGEST,
              ERROR_UNSUPPORTED_KEY_FORMAT,
              ERROR_UNSUPPORTED_KEY_ENCRYPTION_ALGORITHM,
              ERROR_UNSUPPORTED_KEY_VERIFICATION_ALGORITHM,
              ERROR_UNSUPPORTED_TAG,
              ERROR_UNSUPPORTED_EC_FIELD,
              ERROR_UNSUPPORTED_MIN_MAC_LENGTH, -> DatabaseEncryptionException(this, UNSUPPORTED)
              // Timeout: these errors could be recoverable
              ERROR_SECURE_HW_BUSY,
              ERROR_SECURE_HW_COMMUNICATION_FAILED, -> DatabaseEncryptionException(this, TIMEOUT)
              else -> DatabaseEncryptionException(this, UNKNOWN)
            }
          return encryptionException
        }
      }
    }
    return DatabaseEncryptionException(this, UNKNOWN)
  }

/**
 * A list of keystore error. This is a duplicate of
 * https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/security/keymaster/KeymasterDefs.java
 */
@Suppress("Unused")
object KeyStoreExceptionErrorCode {
  const val ERROR_OK = 0
  const val ERROR_ROOT_OF_TRUST_ALREADY_SET = -1
  const val ERROR_UNSUPPORTED_PURPOSE = -2
  const val ERROR_INCOMPATIBLE_PURPOSE = -3
  const val ERROR_UNSUPPORTED_ALGORITHM = -4
  const val ERROR_INCOMPATIBLE_ALGORITHM = -5
  const val ERROR_UNSUPPORTED_KEY_SIZE = -6
  const val ERROR_UNSUPPORTED_BLOCK_MODE = -7
  const val ERROR_INCOMPATIBLE_BLOCK_MODE = -8
  const val ERROR_UNSUPPORTED_MAC_LENGTH = -9
  const val ERROR_UNSUPPORTED_PADDING_MODE = -10
  const val ERROR_INCOMPATIBLE_PADDING_MODE = -11
  const val ERROR_UNSUPPORTED_DIGEST = -12
  const val ERROR_INCOMPATIBLE_DIGEST = -13
  const val ERROR_INVALID_EXPIRATION_TIME = -14
  const val ERROR_INVALID_USER_ID = -15
  const val ERROR_INVALID_AUTHORIZATION_TIMEOUT = -16
  const val ERROR_UNSUPPORTED_KEY_FORMAT = -17
  const val ERROR_INCOMPATIBLE_KEY_FORMAT = -18
  const val ERROR_UNSUPPORTED_KEY_ENCRYPTION_ALGORITHM = -19
  const val ERROR_UNSUPPORTED_KEY_VERIFICATION_ALGORITHM = -20
  const val ERROR_INVALID_INPUT_LENGTH = -21
  const val ERROR_KEY_EXPORT_OPTIONS_INVALID = -22
  const val ERROR_DELEGATION_NOT_ALLOWED = -23
  const val ERROR_KEY_NOT_YET_VALID = -24
  const val ERROR_KEY_EXPIRED = -25
  const val ERROR_KEY_USER_NOT_AUTHENTICATED = -26
  const val ERROR_OUTPUT_PARAMETER_NULL = -27
  const val ERROR_INVALID_OPERATION_HANDLE = -28
  const val ERROR_INSUFFICIENT_BUFFER_SPACE = -29
  const val ERROR_VERIFICATION_FAILED = -30
  const val ERROR_TOO_MANY_OPERATIONS = -31
  const val ERROR_UNEXPECTED_NULL_POINTER = -32
  const val ERROR_INVALID_KEY_BLOB = -33
  const val ERROR_IMPORTED_KEY_NOT_ENCRYPTED = -34
  const val ERROR_IMPORTED_KEY_DECRYPTION_FAILED = -35
  const val ERROR_IMPORTED_KEY_NOT_SIGNED = -36
  const val ERROR_IMPORTED_KEY_VERIFICATION_FAILED = -37
  const val ERROR_INVALID_ARGUMENT = -38
  const val ERROR_UNSUPPORTED_TAG = -39
  const val ERROR_INVALID_TAG = -40
  const val ERROR_MEMORY_ALLOCATION_FAILED = -41
  const val ERROR_INVALID_RESCOPING = -42
  const val ERROR_IMPORT_PARAMETER_MISMATCH = -44
  const val ERROR_SECURE_HW_ACCESS_DENIED = -45
  const val ERROR_OPERATION_CANCELLED = -46
  const val ERROR_CONCURRENT_ACCESS_CONFLICT = -47
  const val ERROR_SECURE_HW_BUSY = -48
  const val ERROR_SECURE_HW_COMMUNICATION_FAILED = -49
  const val ERROR_UNSUPPORTED_EC_FIELD = -50
  const val ERROR_MISSING_NONCE = -51
  const val ERROR_INVALID_NONCE = -52
  const val ERROR_MISSING_MAC_LENGTH = -53
  const val ERROR_KEY_RATE_LIMIT_EXCEEDED = -54
  const val ERROR_CALLER_NONCE_PROHIBITED = -55
  const val ERROR_KEY_MAX_OPS_EXCEEDED = -56
  const val ERROR_INVALID_MAC_LENGTH = -57
  const val ERROR_MISSING_MIN_MAC_LENGTH = -58
  const val ERROR_UNSUPPORTED_MIN_MAC_LENGTH = -59
  const val ERROR_CANNOT_ATTEST_IDS = -66
  const val ERROR_UNIMPLEMENTED = -100
  const val ERROR_VERSION_MISMATCH = -101
  const val ERROR_UNKNOWN_ERROR = -1000
}
