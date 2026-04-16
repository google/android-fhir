/*
 * Copyright 2025-2026 Google LLC
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

package com.google.android.fhir

import kotlin.time.Instant

/** Data class for squashed local changes for resource. */
data class LocalChange(
  /** The resource type name (e.g. "Patient"). */
  val resourceType: String,
  /** The resource id. */
  val resourceId: String,
  /** The id of the version of the resource that this local change is based on. */
  val versionId: String? = null,
  /** The time instant the app user performed a CUD operation on the resource. */
  val timestamp: Instant,
  /** Type of local change like insert, delete, etc. */
  val type: Type,
  /** JSON string with local changes. */
  val payload: String,
  /**
   * This token value must be explicitly applied when list of local changes are squashed and
   * [LocalChange] class instance is created.
   */
  var token: LocalChangeToken,
) {
  enum class Type(val value: Int) {
    INSERT(1), // create a new resource. payload is the entire resource json.
    UPDATE(2), // patch. payload is the json patch.
    DELETE(3), // delete. payload is empty string.
    ;

    companion object {
      fun from(input: Int): Type = entries.first { it.value == input }
    }
  }
}

data class LocalChangeToken(val ids: List<Long>)
