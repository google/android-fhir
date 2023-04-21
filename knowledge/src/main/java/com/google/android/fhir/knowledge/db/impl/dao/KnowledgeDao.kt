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

package com.google.android.fhir.knowledge.db.impl.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.google.android.fhir.knowledge.db.impl.entities.ImplementationGuideEntity
import com.google.android.fhir.knowledge.db.impl.entities.ImplementationGuideResourceMetadataEntity
import com.google.android.fhir.knowledge.db.impl.entities.ImplementationGuideWithResources
import com.google.android.fhir.knowledge.db.impl.entities.ResourceMetadataEntity
import org.hl7.fhir.r4.model.ResourceType

@Dao
abstract class KnowledgeDao {

  @Transaction
  internal open suspend fun insertResource(
    implementationGuideId: Long?,
    resource: ResourceMetadataEntity,
  ) {

    val resourceMetadata =
      if (resource.url != null) {
        getResourceWithUrl(resource.url)
      } else {
        getResourcesWithNameAndVersion(resource.resourceType, resource.name, resource.version)
      }
    // TODO(ktarasenko) compare the substantive part of the old and new resource and thrown an
    // exception if they
    // are different.
    val resourceMetadataId = resourceMetadata?.resourceMetadataId ?: insert(resource)
    insert(ImplementationGuideResourceMetadataEntity(0, implementationGuideId, resourceMetadataId))
  }

  @Transaction
  internal open suspend fun deleteImplementationGuide(name: String, version: String) {
    val igEntity = getImplementationGuide(name, version)
    if (igEntity != null) {
      deleteImplementationGuide(igEntity)
      deleteOrphanedResources()
    }
  }

  @Query(
    "DELETE from ResourceMetadataEntity WHERE resourceMetadataId NOT IN (SELECT DISTINCT resourceMetadataId from ImplementationGuideResourceMetadataEntity)"
  )
  internal abstract suspend fun deleteOrphanedResources()

  @Query("SELECT * from ImplementationGuideEntity")
  internal abstract suspend fun getImplementationGuides(): List<ImplementationGuideEntity>

  @Query(
    "SELECT * from ImplementationGuideEntity WHERE packageId = :packageId AND version = :version"
  )
  internal abstract suspend fun getImplementationGuide(
    packageId: String,
    version: String?,
  ): ImplementationGuideEntity?

  @Query("SELECT * from ResourceMetadataEntity")
  internal abstract suspend fun getResources(): List<ResourceMetadataEntity>

  @Query("SELECT * from ResourceMetadataEntity WHERE resourceType = :resourceType")
  internal abstract suspend fun getResources(
    resourceType: ResourceType,
  ): List<ResourceMetadataEntity>

  @Query("SELECT * from ResourceMetadataEntity WHERE url = :url")
  internal abstract suspend fun getResourceWithUrl(
    url: String,
  ): ResourceMetadataEntity?
  // Remove after https://github.com/google/android-fhir/issues/1920
  @Query("SELECT * from ResourceMetadataEntity WHERE url LIKE :urlPart")
  internal abstract suspend fun getResourceWithUrlLike(
    urlPart: String,
  ): ResourceMetadataEntity?

  @Query(
    "SELECT * from ResourceMetadataEntity WHERE  resourceType = :resourceType AND name = :name"
  )
  internal abstract suspend fun getResourcesWithName(
    resourceType: ResourceType,
    name: String?,
  ): List<ResourceMetadataEntity>

  @Query(
    "SELECT * from ResourceMetadataEntity WHERE resourceType = :resourceType AND name = :name AND version = :version"
  )
  internal abstract suspend fun getResourcesWithNameAndVersion(
    resourceType: ResourceType,
    name: String?,
    version: String?,
  ): ResourceMetadataEntity?

  @Transaction
  @Query("SELECT * FROM ImplementationGuideEntity WHERE implementationGuideId = :igId")
  internal abstract suspend fun getImplementationGuidesWithResources(
    igId: Long,
  ): ImplementationGuideWithResources?

  @Delete
  internal abstract suspend fun deleteImplementationGuide(igEntity: ImplementationGuideEntity)

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  internal abstract suspend fun insert(resource: ResourceMetadataEntity): Long

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  internal abstract suspend fun insert(
    igResourceXRef: ImplementationGuideResourceMetadataEntity
  ): Long

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  internal abstract suspend fun insert(ig: ImplementationGuideEntity): Long
}
