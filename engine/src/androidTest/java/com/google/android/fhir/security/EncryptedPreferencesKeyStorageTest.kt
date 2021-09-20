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
import android.content.Context.MODE_PRIVATE
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.filters.SmallTest
import com.google.android.fhir.security.EncryptedPreferencesKeyStorage.Companion.STORAGE_KEY_PREFERENCES_NAME
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Integration test for [EncryptedPreferencesKeyStorageTest]. */
@SmallTest
@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.M)
class EncryptedPreferencesKeyStorageTest {
  private val context: Context = ApplicationProvider.getApplicationContext()
  private val provider: EncryptedPreferencesKeyStorage = EncryptedPreferencesKeyStorage(context)

  @Before
  fun setup() {
    resetSharedPreferences()
  }

  @After
  fun tearDown() {
    resetSharedPreferences()
  }

  @Test
  fun getKey_noMapping_shouldReturnNull() {
    assertThat(provider.getKey(KEY_NAME)).isNull()
  }

  @Test
  fun updateKey_noMapping_shouldStoreKey() {
    provider.updateKey(KEY_NAME, TEST_KEY_1)

    assertThat(provider.getKey(KEY_NAME)).isEqualTo(TEST_KEY_1)
  }

  @Test
  fun updateKey_hasMapping_shouldReplaceKey() {
    provider.updateKey(KEY_NAME, TEST_KEY_1)
    provider.updateKey(KEY_NAME, TEST_KEY_2)

    assertThat(provider.getKey(KEY_NAME)).isEqualTo(TEST_KEY_2)
  }

  @Test
  fun updateKey_hasDifferentMapping_shouldStoreKeySepearately() {
    provider.updateKey(KEY_NAME, TEST_KEY_1)
    provider.updateKey(OTHER_KEY_NAME, TEST_KEY_2)

    assertThat(provider.getKey(KEY_NAME)).isEqualTo(TEST_KEY_1)
    assertThat(provider.getKey(OTHER_KEY_NAME)).isEqualTo(TEST_KEY_2)
  }

  private fun resetSharedPreferences() =
    context.getSharedPreferences(STORAGE_KEY_PREFERENCES_NAME, MODE_PRIVATE).apply {
      edit().clear().commit()
    }

  private companion object {
    const val KEY_NAME = "test_key"
    const val OTHER_KEY_NAME = "other_test_key"
    val TEST_KEY_1 = byteArrayOf(0x23, 0x45, 0x1E, 0x4F)
    val TEST_KEY_2 = byteArrayOf(0x4F, 0x1E, 0x23, 0x45)
  }
}
