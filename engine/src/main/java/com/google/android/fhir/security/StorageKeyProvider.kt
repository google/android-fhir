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

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties.KEY_ALGORITHM_HMAC_SHA256
import android.security.keystore.KeyProperties.PURPOSE_SIGN
import androidx.annotation.VisibleForTesting
import java.lang.UnsupportedOperationException
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.Mac
import javax.crypto.SecretKey

/** A singleton object for generating or getting previously generated storage keys. */
object StorageKeyProvider {
  /**
   * Returns a previous generated storage passphrase with name [keyName].
   *
   * If there is no key associated with [keyName], generates a storage passphrase with length
   * [keyLength] and stores the passphrase in an encrypted storage.
   */
  @Synchronized
  fun getOrCreatePassphrase(keyName: String): ByteArray? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      // Unsupported. Android security library only supports API 23 or above.
      throw UnsupportedOperationException("Database encryption is supported on API 23 onwards.")
    }

    val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE_NAME)
    keyStore.load(/* param = */ null)
    val signingKey =
      if (keyStore.containsAlias(keyName)) {
        keyStore.getKey(keyName, /* password= */ null) as SecretKey
      } else {
        val keyGenerator =
          KeyGenerator.getInstance(KEY_ALGORITHM_HMAC_SHA256, ANDROID_KEYSTORE_NAME)
        keyGenerator.init(KeyGenParameterSpec.Builder(keyName, PURPOSE_SIGN).build())
        keyGenerator.generateKey()
      }

    return Mac.getInstance(KEY_ALGORITHM_HMAC_SHA256).let {
      it.init(signingKey)
      it.doFinal("".toByteArray(StandardCharsets.UTF_8))
    }
  }

  @VisibleForTesting const val ANDROID_KEYSTORE_NAME = "AndroidKeyStore"
}
