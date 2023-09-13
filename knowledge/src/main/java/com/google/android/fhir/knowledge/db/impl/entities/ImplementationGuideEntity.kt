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
import com.google.android.fhir.knowledge.ImplementationGuide
import java.io.File

/**
 * A DB Entity containing the metadata of the
 * [Implementation Guide](https://build.fhir.org/implementationguide.html).
 *
 * This entity stores as much metadata as necessary for `NpmPackageManager` to resolve the
 * [dependency](https://build.fhir.org/implementationguide-definitions.html#ImplementationGuide.dependsOn)
 * on another Implementation guide.
 */
@Entity(
  indices =
    [
      Index(value = ["implementationGuideId"]),
      Index(value = ["packageId", "url", "version"], unique = true)
    ]
)
internal data class ImplementationGuideEntity(
  @PrimaryKey(autoGenerate = true) val implementationGuideId: Long,
  val url: String,
  val packageId: String,
  val version: String?,
  /** Directory where the Implementation Guide files are stored */
  val rootDirectory: File,
)

internal fun ImplementationGuide.toEntity(rootFolder: File): ImplementationGuideEntity {
  return ImplementationGuideEntity(0L, uri, packageId, version, rootFolder)
}
