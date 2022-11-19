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

package com.google.android.fhir.implementationguide.db.impl.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.File
import org.hl7.fhir.r4.model.ResourceType

@Entity(
  indices =
    [
      Index(value = ["implementationGuideId"], unique = false),
      Index(value = ["resourceType", "resourceId", "implementationGuideId"], unique = true)
    ],
  foreignKeys =
    [
      ForeignKey(
        entity = ImplementationGuideEntity::class,
        parentColumns = ["id"],
        childColumns = ["implementationGuideId"]
      )
    ]
)
data class ResourceEntity(
  @PrimaryKey(autoGenerate = true) val id: Long,
  val resourceType: ResourceType,
  val resourceId: String,
  val url: String?,
  val name: String?,
  val version: String?,
  val fileUri: File,
  val implementationGuideId: Long,
)
