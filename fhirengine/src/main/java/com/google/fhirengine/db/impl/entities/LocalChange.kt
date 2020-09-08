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

package com.google.fhirengine.db.impl.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.fhirengine.sync.model.Update
import org.hl7.fhir.r4.model.ResourceType

/**
 * When a local change to a resource happens, the lastUpdated timestamp in
 * [ResourceEntity] is updated and the diff itself is inserted in this table.
 * The value of the diff depends upon the nature of change and can be the empty
 * string, the full resource in JSON form or a RFC 6902 JSON patch. For a fully
 * synced resource this table should be empty.
 */
@Entity(
        foreignKeys = [
          ForeignKey(
                  entity = ResourceEntity::class,
                  parentColumns = ["resourceId", "resourceType"],
                  childColumns = ["resourceId", "resourceType"],
                  onDelete = ForeignKey.CASCADE,
                  onUpdate = ForeignKey.NO_ACTION,
                  deferred = true
          )
        ]
)
internal data class LocalChange(
  @PrimaryKey(autoGenerate = true)
  val id: Long,
  val resourceType: ResourceType,
  val resourceId: String,
  val timestamp: String,
  val type: Update.Type,
  val diff: String
)
