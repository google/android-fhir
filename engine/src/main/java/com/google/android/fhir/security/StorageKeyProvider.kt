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
import java.security.SecureRandom

/** A singleton object for generating or getting previously generated storage keys. */
object StorageKeyProvider {
  private lateinit var keyStorage: KeyStorage
  /**
   * Returns a previous generated storage passphrase with name [keyName].
   *
   * If there is no key associated with [keyName], generates a storage passphrase with length
   * [keyLength] and stores the passphrase in an encrypted storage.
   */
  @Synchronized
  fun getOrCreatePassphrase(context: Context, keyName: String): ByteArray? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      // Unsupported. Android security library only supports API 23 or above.
      return null
    }

    getKeyStore(context).let {
      it.getKey(keyName)?.let { key ->
        return key
      }
      val passphrase = generatePassphrase()
      it.updateKey(keyName, passphrase)
      return passphrase
    }
  }

  /**
   * Generates a randomized key, in [ByteArray], with a length ranging from [MIN_STORAGE_KEY_LENGTH]
   * to [MIN_STORAGE_KEY_LENGTH]
   * * 2 - 1.
   */
  @Synchronized
  @SuppressWarnings("newApi") // API check in the code
  private fun generatePassphrase(): ByteArray {
    val secureRandom =
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        SecureRandom.getInstanceStrong()
      } else {
        SecureRandom()
      }
    val passphrase =
      ByteArray(secureRandom.nextInt(MIN_STORAGE_KEY_LENGTH) + MIN_STORAGE_KEY_LENGTH)
    secureRandom.nextBytes(passphrase)
    return passphrase
  }

  @Synchronized
  private fun getKeyStore(context: Context): KeyStorage {
    if (!::keyStorage.isInitialized) {
      keyStorage = EncryptedPreferencesKeyStorage(context)
    }
    return keyStorage
  }

  private const val MIN_STORAGE_KEY_LENGTH = 16
}
