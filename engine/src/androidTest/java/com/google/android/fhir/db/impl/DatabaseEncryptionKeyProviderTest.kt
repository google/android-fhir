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

package com.google.android.fhir.db.impl

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import java.security.KeyStore
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Integration test for [DatabaseEncryptionKeyProviderTest]. */
@SmallTest
@RunWith(AndroidJUnit4::class)
class DatabaseEncryptionKeyProviderTest {
  @Before fun setup() = deleteTestKeys()

  @After fun tearDown() = deleteTestKeys()

  @Test
  fun getOrCreatePassphrase_aliasNotExists_shouldGenerateKey() {
    assertThat(DatabaseEncryptionKeyProvider.getOrCreatePassphrase(ALIAS_NAME)).isNotNull()
  }

  @Test
  fun getOrCreatePassphrase_aliasExists_shouldReturnSameKey() {
    val key = DatabaseEncryptionKeyProvider.getOrCreatePassphrase(ALIAS_NAME)

    assertThat(DatabaseEncryptionKeyProvider.getOrCreatePassphrase(ALIAS_NAME)).isEqualTo(key)
  }

  @Test
  fun getOrCreatePassphrase_aliasExists_keyCacheCleared_shouldReturnSameKey() {
    val key = DatabaseEncryptionKeyProvider.getOrCreatePassphrase(ALIAS_NAME)
    DatabaseEncryptionKeyProvider.clearKeyCache()

    assertThat(DatabaseEncryptionKeyProvider.getOrCreatePassphrase(ALIAS_NAME)).isEqualTo(key)
  }

  @Test
  fun getOrCreatePassphrase_otherAliasExists_shouldReplaceKey() {
    val key = DatabaseEncryptionKeyProvider.getOrCreatePassphrase(ALIAS_NAME)

    assertThat(DatabaseEncryptionKeyProvider.getOrCreatePassphrase(OTHER_ALIAS_NAME))
      .isNotEqualTo(key)
  }

  private fun deleteTestKeys() {
    val keyStore = KeyStore.getInstance(DatabaseEncryptionKeyProvider.ANDROID_KEYSTORE_NAME)
    keyStore.load(null)
    keyStore.deleteEntry(ALIAS_NAME)
    keyStore.deleteEntry(OTHER_ALIAS_NAME)
    DatabaseEncryptionKeyProvider.clearKeyCache()
  }

  private companion object {
    const val ALIAS_NAME = "test_key"
    const val OTHER_ALIAS_NAME = "other_test_key"
  }
}
