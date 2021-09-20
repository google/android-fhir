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

/** Encodes a byte array to a string in hexadecimal format. */
fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

/** Decodes a hexadecimal encoded string into a byte array. */
fun String.decodeHex(): ByteArray {
  check(length % 2 == 0) { "Must have an even length" }
  return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}
