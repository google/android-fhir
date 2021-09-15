/*
 * Copyright 2020 Google LLC
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

package com.google.android.fhir.security

import android.content.Context
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.security.SecureRandom

/** A singleton object for generating or getting previously generated storage keys. */
object StorageKeyProvider {
  /**
   * Returns a previous generated storage passphrase with name [passphraseName].
   *
   * If there is no key associated with [passphraseName], generates a storage passphrase with length
   * [keyLength] and stores the passphrase in an encrypted storage.
   */
  @Synchronized
  fun getOrCreatePassphrase(
    context: Context,
    passphraseName: String,
    keyLength: Int = STORAGE_KEY_LENGTH
  ): ByteArray {
    val mainKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    val encryptedSharedPreferences =
      EncryptedSharedPreferences.create(
        STORAGE_KEY_PREFERENCES_NAME,
        mainKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
      )
    encryptedSharedPreferences.getString(passphraseName, null)?.decodeHex()?.let {
      return it
    }
    val passphrase = generatePassphrase(keyLength)
    with(encryptedSharedPreferences.edit()) {
      putString(passphraseName, passphrase.toHexString())
      apply()
    }
    return passphrase
  }

  @Synchronized
  @SuppressWarnings("newApi") // API check in the code
  private fun generatePassphrase(keyLength: Int): ByteArray {
    val passphrase = ByteArray(keyLength)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      SecureRandom.getInstanceStrong().nextBytes(passphrase)
    } else {
      SecureRandom().nextBytes(passphrase)
    }
    return passphrase
  }

  private const val STORAGE_KEY_PREFERENCES_NAME = "store_key_preferences"
  private const val STORAGE_KEY_LENGTH = 16
}

fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

fun String.decodeHex(): ByteArray {
  check(length % 2 == 0) { "Must have an even length" }
  return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}
