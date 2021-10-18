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

package com.google.android.fhir.db.impl

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties.KEY_ALGORITHM_HMAC_SHA256
import android.security.keystore.KeyProperties.PURPOSE_SIGN
import android.util.ArrayMap
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import com.google.android.fhir.db.DatabaseEncryptionException
import com.google.android.fhir.db.DatabaseEncryptionException.DatabaseEncryptionErrorCode.UNSUPPORTED
import com.google.android.fhir.db.databaseEncryptionException
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey

/** A singleton object for generating or getting previously generated storage keys. */
internal object DatabaseEncryptionKeyProvider {
  private val keyMap = ArrayMap<String, ByteArray>()
  /**
   * Returns a previous generated storage passphrase with name [keyName].
   *
   * If there is no key associated with [keyName], generates a storage passphrase with length
   * [keyLength] and stores the passphrase in an encrypted storage.
   */
  @Synchronized
  @RequiresApi(Build.VERSION_CODES.M)
  fun getOrCreatePassphrase(keyName: String): ByteArray {
    if (!isDatabaseEncryptionSupported()) {
      throw UnsupportedOperationException("Database encryption is not supported on this device.")
    }

    keyMap[keyName]?.let {
      return it
    }

    val keyStore =
      try {
        KeyStore.getInstance(ANDROID_KEYSTORE_NAME)
      } catch (exception: KeyStoreException) {
        throw exception.databaseEncryptionException
      }

    val hmac =
      try {
        Mac.getInstance(KEY_ALGORITHM_HMAC_SHA256)
      } catch (exception: NoSuchAlgorithmException) {
        throw DatabaseEncryptionException(exception, UNSUPPORTED)
      }

    try {
      keyStore.load(/* param = */ null)
      val signingKey: SecretKey =
        keyStore.getKey(keyName, /* password= */ null) as SecretKey?
          ?: run {
            val keyGenerator =
              KeyGenerator.getInstance(KEY_ALGORITHM_HMAC_SHA256, ANDROID_KEYSTORE_NAME)
            keyGenerator.init(KeyGenParameterSpec.Builder(keyName, PURPOSE_SIGN).build())
            keyGenerator.generateKey()
          }
      hmac.init(signingKey)
      val key = hmac.doFinal("".toByteArray(StandardCharsets.UTF_8))
      keyMap[keyName] = key
      return key
    } catch (exception: KeyStoreException) {
      throw exception.databaseEncryptionException
    }
  }

  fun isDatabaseEncryptionSupported() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

  @VisibleForTesting
  fun clearKeyCache() {
    keyMap.clear()
  }

  @VisibleForTesting const val ANDROID_KEYSTORE_NAME = "AndroidKeyStore"
}
