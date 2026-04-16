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

package com.google.android.fhir.db.impl.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.android.fhir.LocalChange
import com.google.android.fhir.LocalChangeToken
import kotlin.time.Instant

@Entity(
  indices =
    [
      Index(value = ["resourceType", "resourceId"]),
      Index(value = ["resourceUuid"]),
    ],
)
internal data class LocalChangeEntity(
  @PrimaryKey(autoGenerate = true) val id: Long,
  val resourceType: String,
  val resourceId: String,
  val resourceUuid: String,
  val timestamp: Long,
  val type: Int,
  val payload: String,
  val versionId: String? = null,
) {
  fun toLocalChange() =
    LocalChange(
      resourceType = resourceType,
      resourceId = resourceId,
      versionId = versionId,
      timestamp = Instant.fromEpochMilliseconds(timestamp),
      type = LocalChange.Type.from(type),
      payload = payload,
      token = LocalChangeToken(listOf(id)),
    )
}
