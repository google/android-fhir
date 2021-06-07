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

package com.google.android.fhir.db.impl.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import ca.uhn.fhir.parser.IParser
import ca.uhn.fhir.rest.annotation.Transaction
import com.google.android.fhir.db.impl.entities.DateIndexEntity
import com.google.android.fhir.db.impl.entities.NumberIndexEntity
import com.google.android.fhir.db.impl.entities.PositionIndexEntity
import com.google.android.fhir.db.impl.entities.QuantityIndexEntity
import com.google.android.fhir.db.impl.entities.ReferenceIndexEntity
import com.google.android.fhir.db.impl.entities.ResourceEntity
import com.google.android.fhir.db.impl.entities.StringIndexEntity
import com.google.android.fhir.db.impl.entities.TokenIndexEntity
import com.google.android.fhir.db.impl.entities.UriIndexEntity
import com.google.android.fhir.index.ResourceIndexer
import com.google.android.fhir.index.ResourceIndices
import com.google.android.fhir.logicalId
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

@Dao
internal abstract class ResourceDao {
  // this is ugly but there is no way to inject these right now in Room as it is the one creating
  // the dao
  lateinit var iParser: IParser

  @Transaction
  open fun update(resource: Resource) {
    updateResource(
      resource.logicalId,
      resource.resourceType,
      iParser.encodeResourceToString(resource)
    )
    val entity =
      ResourceEntity(
        id = 0,
        resourceType = resource.resourceType,
        resourceId = resource.logicalId,
        serializedResource = iParser.encodeResourceToString(resource)
      )
    val index = ResourceIndexer.index(resource)
    updateIndicesForResource(index, entity)
  }

  @Transaction
  open fun insert(resource: Resource) {
    insertResource(resource)
  }

  @Transaction
  open fun insertAll(resources: List<Resource>) {
    resources.forEach { resource -> insertResource(resource) }
  }

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertResource(resource: ResourceEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertStringIndex(stringIndexEntity: StringIndexEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertReferenceIndex(referenceIndexEntity: ReferenceIndexEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertCodeIndex(tokenIndexEntity: TokenIndexEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertQuantityIndex(quantityIndexEntity: QuantityIndexEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertUriIndex(uriIndexEntity: UriIndexEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertDateIndex(dateIndexEntity: DateIndexEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertNumberIndex(numberIndexEntity: NumberIndexEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  abstract fun insertPositionIndex(positionIndexEntity: PositionIndexEntity)

  @Query(
    """
        UPDATE ResourceEntity
        SET serializedResource = :serializedResource
        WHERE resourceId = :resourceId
        AND resourceType = :resourceType
        """
  )
  abstract fun updateResource(
    resourceId: String,
    resourceType: ResourceType,
    serializedResource: String
  )

  @Query(
    """
        DELETE FROM ResourceEntity
        WHERE resourceId = :resourceId AND resourceType = :resourceType"""
  )
  abstract fun deleteResource(resourceId: String, resourceType: ResourceType): Int

  @Query(
    """
        SELECT serializedResource
        FROM ResourceEntity
        WHERE resourceId = :resourceId AND resourceType = :resourceType"""
  )
  abstract fun getResource(resourceId: String, resourceType: ResourceType): String?

  @Query(
    """
        SELECT ResourceEntity.serializedResource
        FROM ResourceEntity 
        JOIN ReferenceIndexEntity
        ON ResourceEntity.resourceType = ReferenceIndexEntity.resourceType
            AND ResourceEntity.resourceId = ReferenceIndexEntity.resourceId
        WHERE ReferenceIndexEntity.resourceType = :resourceType
            AND ReferenceIndexEntity.index_path = :indexPath
            AND ReferenceIndexEntity.index_value = :indexValue"""
  )
  abstract fun getResourceByReferenceIndex(
    resourceType: String,
    indexPath: String,
    indexValue: String
  ): List<String>

  @Query(
    """
        SELECT ResourceEntity.serializedResource
        FROM ResourceEntity
        JOIN StringIndexEntity
        ON ResourceEntity.resourceType = StringIndexEntity.resourceType
            AND ResourceEntity.resourceId = StringIndexEntity.resourceId
        WHERE StringIndexEntity.resourceType = :resourceType
            AND StringIndexEntity.index_path = :indexPath
            AND StringIndexEntity.index_value = :indexValue"""
  )
  abstract fun getResourceByStringIndex(
    resourceType: String,
    indexPath: String,
    indexValue: String
  ): List<String>

  @Query(
    """
        SELECT ResourceEntity.serializedResource
        FROM ResourceEntity
        JOIN TokenIndexEntity
        ON ResourceEntity.resourceType = TokenIndexEntity.resourceType
            AND ResourceEntity.resourceId = TokenIndexEntity.resourceId
        WHERE TokenIndexEntity.resourceType = :resourceType
            AND TokenIndexEntity.index_path = :indexPath
            AND TokenIndexEntity.index_system = :indexSystem
            AND TokenIndexEntity.index_value = :indexValue"""
  )
  abstract fun getResourceByCodeIndex(
    resourceType: String,
    indexPath: String,
    indexSystem: String,
    indexValue: String
  ): List<String>

  @RawQuery abstract fun getResources(query: SupportSQLiteQuery): List<String>

  @RawQuery abstract fun countResources(query: SupportSQLiteQuery): Long

  private fun insertResource(resource: Resource) {
    val entity =
      ResourceEntity(
        id = 0,
        resourceType = resource.resourceType,
        resourceId = resource.logicalId,
        serializedResource = iParser.encodeResourceToString(resource)
      )
    insertResource(entity)
    val index = ResourceIndexer.index(resource)
    updateIndicesForResource(index, entity)
  }

  private fun updateIndicesForResource(index: ResourceIndices, resource: ResourceEntity) {
    // TODO Move StringIndices to persistable types
    //  https://github.com/jingtang10/fhir-engine/issues/31
    //  we can either use room-autovalue integration or go w/ embedded data classes.
    //  we may also want to merge them:
    //  https://github.com/jingtang10/fhir-engine/issues/33
    index.stringIndices.forEach {
      insertStringIndex(
        StringIndexEntity(
          id = 0,
          resourceType = resource.resourceType,
          index = it,
          resourceId = resource.resourceId
        )
      )
    }
    index.referenceIndices.forEach {
      insertReferenceIndex(
        ReferenceIndexEntity(
          id = 0,
          resourceType = resource.resourceType,
          index = it,
          resourceId = resource.resourceId
        )
      )
    }
    index.tokenIndices.forEach {
      insertCodeIndex(
        TokenIndexEntity(
          id = 0,
          resourceType = resource.resourceType,
          index = it,
          resourceId = resource.resourceId
        )
      )
    }
    index.quantityIndices.forEach {
      insertQuantityIndex(
        QuantityIndexEntity(
          id = 0,
          resourceType = resource.resourceType,
          index = it,
          resourceId = resource.resourceId
        )
      )
    }
    index.uriIndices.forEach {
      insertUriIndex(
        UriIndexEntity(
          id = 0,
          resourceType = resource.resourceType,
          index = it,
          resourceId = resource.resourceId
        )
      )
    }
    index.dateIndices.forEach {
      insertDateIndex(
        DateIndexEntity(
          id = 0,
          resourceType = resource.resourceType,
          index = it,
          resourceId = resource.resourceId
        )
      )
    }
    index.numberIndices.forEach {
      insertNumberIndex(
        NumberIndexEntity(
          id = 0,
          resourceType = resource.resourceType,
          index = it,
          resourceId = resource.resourceId
        )
      )
    }
    index.positionIndices.forEach {
      insertPositionIndex(
        PositionIndexEntity(
          id = 0,
          resourceType = resource.resourceType,
          index = it,
          resourceId = resource.resourceId
        )
      )
    }
  }
}
