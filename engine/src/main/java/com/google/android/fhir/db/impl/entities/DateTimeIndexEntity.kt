/*
 * Copyright 2023-2025 Google LLC
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

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.android.fhir.index.entities.DateTimeIndex
import java.util.UUID
import org.hl7.fhir.r4.model.ResourceType

@Entity(
  indices =
    [
      // Covering index for optimizing query performance by minimizing disk I/O and eliminating the
      // need for accessing underlying table data.
      // Column ordered to minimise time to run sortJoinStatement in [MoreSearch], and to resolve:
      // https://github.com/google/android-fhir/issues/2040
      Index(value = ["resourceType", "index_name", "resourceUuid", "index_from", "index_to"]),
      // Keep this index for faster foreign lookup
      Index(value = ["resourceUuid", "index_name", "index_from"]),
    ],
  foreignKeys =
    [
      ForeignKey(
        entity = ResourceEntity::class,
        parentColumns = ["resourceUuid"],
        childColumns = ["resourceUuid"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.NO_ACTION,
        deferred = true,
      ),
    ],
)
internal data class DateTimeIndexEntity(
  @PrimaryKey(autoGenerate = true) val id: Long,
  val resourceUuid: UUID,
  val resourceType: ResourceType,
  @Embedded(prefix = "index_") val index: DateTimeIndex,
)
