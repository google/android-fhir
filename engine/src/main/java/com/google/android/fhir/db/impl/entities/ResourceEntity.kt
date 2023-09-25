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

package com.google.android.fhir.db.impl.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.UUID
import org.hl7.fhir.r4.model.ResourceType

@Entity(
  indices =
    [
      Index(value = ["resourceUuid"], unique = true),
      Index(value = ["resourceType", "resourceId"], unique = true),
    ],
)
internal data class ResourceEntity(
  @PrimaryKey(autoGenerate = true) val id: Long,
  val resourceUuid: UUID,
  val resourceType: ResourceType,
  val resourceId: String,
  val serializedResource: String,
  val versionId: String?,
  val lastUpdatedRemote: Instant?,
  val lastUpdatedLocal: Instant?,
)
