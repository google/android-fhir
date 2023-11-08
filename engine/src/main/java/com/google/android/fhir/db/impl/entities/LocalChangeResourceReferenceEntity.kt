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
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  indices =
    [
      Index(value = ["resourceReferenceValue"]),
      // To avoid full table scans whenever parent table is modified.
      Index(value = ["localChangeId"]),
    ],
  foreignKeys =
    [
      ForeignKey(
        entity = LocalChangeEntity::class,
        parentColumns = ["id"],
        childColumns = ["localChangeId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.NO_ACTION,
        deferred = true,
      ),
    ],
)
internal data class LocalChangeResourceReferenceEntity(
  @PrimaryKey(autoGenerate = true) val id: Long,
  val localChangeId: Long,
  val resourceReferenceValue: String,
  val resourceReferencePath: String?,
)
