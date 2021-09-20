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
import android.content.SharedPreferences
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

/** A key storage which is backed by [EncryptedPreferencesKeyStorage]. */
internal class EncryptedPreferencesKeyStorage(val context: Context) : KeyStorage {
  private val encryptedSharedPreferences: SharedPreferences

  init {
    check(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      "Unsupported Android SDK version: " + Build.VERSION.SDK_INT
    }

    encryptedSharedPreferences =
      EncryptedSharedPreferences.create(
        STORAGE_KEY_PREFERENCES_NAME,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
      )
  }

  @Synchronized
  override fun updateKey(keyName: String, key: ByteArray) =
    with(encryptedSharedPreferences.edit()) {
      putString(keyName, key.toHexString())
      apply()
    }

  @Synchronized
  override fun getKey(alias: String) =
    encryptedSharedPreferences.getString(alias, null)?.decodeHex()

  companion object {
    const val STORAGE_KEY_PREFERENCES_NAME = "store_key_preferences"
  }
}
