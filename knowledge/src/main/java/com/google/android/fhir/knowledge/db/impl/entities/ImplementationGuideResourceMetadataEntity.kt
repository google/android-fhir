/*
 * Copyright 2022 Google LLC
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

package com.google.android.fhir.knowledge.db.impl.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

/** Connects [ImplementationGuideEntity] to [ResourceMetadataEntity]. */
@Entity(
  indices =
    [
      Index(value = ["implementationGuideId"]),
      Index(value = ["resourceMetadataId"]),
      Index(value = ["implementationGuideId", "resourceMetadataId"], unique = true)
    ],
  foreignKeys =
    [
      ForeignKey(
        entity = ImplementationGuideEntity::class,
        parentColumns = ["implementationGuideId"],
        childColumns = ["implementationGuideId"],
        onDelete = ForeignKey.CASCADE
      ),
      ForeignKey(
        entity = ResourceMetadataEntity::class,
        parentColumns = ["resourceMetadataId"],
        childColumns = ["resourceMetadataId"],
        onDelete = ForeignKey.CASCADE
      )
    ]
)
internal data class ImplementationGuideResourceMetadataEntity(
  @PrimaryKey(autoGenerate = true) val id: Long,
  val implementationGuideId: Long?,
  val resourceMetadataId: Long,
)

internal data class ImplementationGuideWithResources(
  @Embedded val implementationGuideEntity: ImplementationGuideEntity,
  @Relation(
    parentColumn = "implementationGuideId",
    entityColumn = "resourceMetadataId",
    associateBy = Junction(ImplementationGuideResourceMetadataEntity::class)
  )
  val resources: List<ResourceMetadataEntity>,
)
