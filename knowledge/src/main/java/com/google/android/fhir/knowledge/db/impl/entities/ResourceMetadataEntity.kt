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

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.File
import org.hl7.fhir.r4.model.ResourceType

/**
 * A DB entity containing the description and metadata for
 * [FHIR Resource](https://build.fhir.org/canonicalresource.html).
 */
@Entity(
  indices =
    [
      Index(value = ["resourceMetadataId"]),
      Index(value = ["url", "version", "resourceFile"], unique = true)
    ],
)
internal data class ResourceMetadataEntity(
  @PrimaryKey(autoGenerate = true) val resourceMetadataId: Long,
  val resourceType: ResourceType,
  val url: String?,
  val name: String?,
  val version: String?,
  /** Location of the JSON file with a full Resource. */
  val resourceFile: File,
)
