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

/** A storage for managing application level keys used in encryption. */
interface KeyStorage {

  /**
   * Stores a given [key] with name [keyName] in a storage.
   *
   * If the storage previously contained a mapping for the [keyName], the old key is replaced.
   */
  fun updateKey(keyName: String, key: ByteArray)

  /**
   * Returns a key, in [ByteArray], to which the specified [keyName] is mapped, or null if this
   * storage contain no mapping for the [keyName].
   */
  fun getKey(keyName: String): ByteArray?
}
