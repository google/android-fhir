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

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Unit tests for [EncryptionHelper]. */
@RunWith(RobolectricTestRunner::class)
class EncryptionHelperTest {
  @Test
  fun decodeHex_validHexString_shouldDecode() {
    assertThat("01234C".decodeHex()).isEqualTo(byteArrayOf(0x01, 0x23, 0x4C))
  }

  @Test
  fun decodeHex_invalidHexString_shouldThrow() {
    assertThrows(IllegalStateException::class.java) { "0123H".decodeHex() }
  }

  @Test
  fun toHexString_byteArray_shouldEncode() {
    assertThat(byteArrayOf(0x01, 0x23, 0x4C).toHexString()).isEqualTo("01234c")
  }
}
