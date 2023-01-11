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

package com.google.android.fhir.implementationguide.db.impl.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.google.android.fhir.implementationguide.db.impl.entities.IgResourceCrossRef
import com.google.android.fhir.implementationguide.db.impl.entities.ImplementationGuideEntity
import com.google.android.fhir.implementationguide.db.impl.entities.ImplementationGuideWithResources
import com.google.android.fhir.implementationguide.db.impl.entities.ResourceMetadataEntity
import org.hl7.fhir.r4.model.ResourceType

@Dao
abstract class ImplementationGuideDao {

  @Transaction
  internal open suspend fun insertResource(
    implementationGuideId: Long,
    resource: ResourceMetadataEntity,
  ) {
    val resourceMetadataId = insert(resource)
    insert(IgResourceCrossRef(implementationGuideId, resourceMetadataId))
  }

  @Transaction
  internal open suspend fun deleteImplementationGuide(name: String, version: String) {
    val igEntity = getImplementationGuide(name, version)
    deleteImplementationGuide(igEntity)
    deleteOrphanedResources()
  }

  @Query(
    "DELETE from ResourceMetadataEntity WHERE resourceMetadataId NOT IN (SELECT DISTINCT resourceMetadataId from IgResourceCrossRef)"
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
  ): ImplementationGuideEntity

  @Query("SELECT * from ResourceMetadataEntity")
  internal abstract suspend fun getResources(): List<ResourceMetadataEntity>

  @Query("SELECT * from ResourceMetadataEntity WHERE resourceType = :resourceType")
  internal abstract suspend fun getResources(
    resourceType: ResourceType,
  ): List<ResourceMetadataEntity>

  @Query("SELECT * from ResourceMetadataEntity WHERE  resourceType = :resourceType AND url = :url")
  internal abstract suspend fun getResourcesWithUrl(
    resourceType: ResourceType,
    url: String,
  ): List<ResourceMetadataEntity>

  @Query(
    "SELECT * from ResourceMetadataEntity WHERE  resourceType = :resourceType AND name = :name"
  )
  internal abstract suspend fun getResourcesWithName(
    resourceType: ResourceType,
    name: String,
  ): List<ResourceMetadataEntity>

  @Query(
    "SELECT * from ResourceMetadataEntity WHERE resourceType = :resourceType AND name = :name AND version = :version"
  )
  internal abstract suspend fun getResourcesWithNameAndVersion(
    resourceType: ResourceType,
    name: String,
    version: String,
  ): List<ResourceMetadataEntity>

  @Transaction
  @Query("SELECT * FROM ImplementationGuideEntity WHERE implementationGuideId = :igId")
  internal abstract suspend fun getImplementationGuidesWithResources(
    igId: Long
  ): ImplementationGuideWithResources?

  @Delete
  internal abstract suspend fun deleteImplementationGuide(igEntity: ImplementationGuideEntity)

  // TODO: implement better conflict resolution (compare the substantive part of the resource and
  // fail only if they are different)
  @Insert(onConflict = OnConflictStrategy.ABORT)
  internal abstract suspend fun insert(resource: ResourceMetadataEntity): Long

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  internal abstract suspend fun insert(igResourceXRef: IgResourceCrossRef): Long

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  internal abstract suspend fun insert(ig: ImplementationGuideEntity): Long
}
