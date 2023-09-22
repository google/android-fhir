/*
 * Copyright 2023 Google LLC
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

package com.google.android.fhir.sync.upload.patch

import com.google.android.fhir.LocalChange
import java.time.Instant
import org.hl7.fhir.r4.model.Resource

/** Data class for squashed local changes for resource */
data class Patch(
  /** The [ResourceType] */
  val resourceType: String,
  /** The resource id [Resource.id] */
  val resourceId: String,
  /** This is the id of the version of the resource that this local change is based of */
  val versionId: String? = null,
  /** The time instant the app user performed a CUD operation on the resource. */
  val timestamp: Instant,
  /** Type of local change like insert, delete, etc */
  val type: Type,
  /** json string with local changes */
  val payload: String,
) {
  enum class Type(val value: Int) {
    INSERT(1), // create a new resource. payload is the entire resource json.
    UPDATE(2), // patch. payload is the json patch.
    DELETE(3), // delete. payload is empty string.
    ;

    companion object {
      fun from(input: Int): Type = values().first { it.value == input }
    }
  }
}

internal fun LocalChange.Type.toPatchType(): Patch.Type {
  return when (this) {
    LocalChange.Type.INSERT -> Patch.Type.INSERT
    LocalChange.Type.UPDATE -> Patch.Type.UPDATE
    LocalChange.Type.DELETE -> Patch.Type.DELETE
  }
}
