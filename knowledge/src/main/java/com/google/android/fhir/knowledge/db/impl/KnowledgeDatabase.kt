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

package com.google.android.fhir.knowledge.db.impl

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.google.android.fhir.knowledge.db.impl.dao.KnowledgeDao
import com.google.android.fhir.knowledge.db.impl.entities.ImplementationGuideEntity
import com.google.android.fhir.knowledge.db.impl.entities.ImplementationGuideResourceMetadataEntity
import com.google.android.fhir.knowledge.db.impl.entities.ResourceMetadataEntity

/**
 * Stores knowledge artifacts metadata for implementation guides and their containing FHIR
 * Resources.
 *
 * Same FhirResource (identified as the resource with the same `url`) can be part of the different
 * IGs. To avoid duplications, [ImplementationGuideEntity] are connected with
 * [ResourceMetadataEntity] through [ImplementationGuideResourceMetadataEntity] in a
 * [many-to-many](https://developer.android.com/training/data-storage/room/relationships#many-to-many)
 * relationship.
 */
@Database(
  entities =
    [
      ImplementationGuideEntity::class,
      ResourceMetadataEntity::class,
      ImplementationGuideResourceMetadataEntity::class
    ],
  version = 1,
  exportSchema = false
)
@TypeConverters(DbTypeConverters::class)
internal abstract class KnowledgeDatabase : RoomDatabase() {
  abstract fun knowledgeDao(): KnowledgeDao
}
