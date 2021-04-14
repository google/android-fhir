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

package com.google.android.fhir.db.impl.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * When a local change to a resource happens, the lastUpdated timestamp in [ResourceEntity] is
 * updated and the diff itself is inserted in this table. The value of the diff depends upon the
 * type of change and can be:
 * * DELETE: The empty string, "".
 * * INSERT: The full resource in JSON form, e.g. {
 * ```
 *      "resourceType": "Patient",
 *      "id": "animal",
 *      "name": [
 *              {
 *               "use": "usual",
 *               "given": [
 *               "Kenzi"
 *              ]
 *              }
 *        ],
 *      ...
 * ```
 * }
 * * UPDATE: A RFC 6902 JSON patch. e.g. a patch that changes the given name of a patient: [
 * ```
 *      {
 *      "op": "replace",
 *      "path": "/name/0/given/0",
 *      "value": "Binny"
 *      }
 * ```
 * ] For resource that is fully synced with server this table should not have any rows.
 */
@Entity(indices = [Index(value = ["resourceType", "resourceId"])])
data class LocalChangeEntity(
  @PrimaryKey(autoGenerate = true) val id: Long,
  val resourceType: String,
  val resourceId: String,
  val timestamp: String = "",
  val type: Type,
  val payload: String
) {
  enum class Type(val value: Int) {
    INSERT(1), // create a new resource. payload is the entire resource json.
    UPDATE(2), // patch. payload is the json patch.
    DELETE(3); // delete. payload is empty string.

    companion object {
      fun from(input: Int): Type = values().first { it.value == input }
    }
  }
}
