/*
 * Copyright 2025-2026 Google LLC
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
import com.google.android.fhir.db.impl.entities.DateIndexEntity
import com.google.android.fhir.db.impl.entities.DateTimeIndexEntity
import com.google.android.fhir.db.impl.entities.NumberIndexEntity
import com.google.android.fhir.db.impl.entities.PositionIndexEntity
import com.google.android.fhir.db.impl.entities.QuantityIndexEntity
import com.google.android.fhir.db.impl.entities.ReferenceIndexEntity
import com.google.android.fhir.db.impl.entities.ResourceEntity
import com.google.android.fhir.db.impl.entities.StringIndexEntity
import com.google.android.fhir.db.impl.entities.TokenIndexEntity
import com.google.android.fhir.db.impl.entities.UriIndexEntity

@Dao
internal interface ResourceDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertResource(resource: ResourceEntity)

  @Query(
    """
    SELECT serializedResource
    FROM ResourceEntity
    WHERE resourceId = :resourceId AND resourceType = :resourceType
    """,
  )
  suspend fun getResource(resourceId: String, resourceType: String): String?

  @Query(
    """
    SELECT *
    FROM ResourceEntity
    WHERE resourceId = :resourceId AND resourceType = :resourceType
    """,
  )
  suspend fun getResourceEntity(resourceId: String, resourceType: String): ResourceEntity?

  @Query(
    """
    DELETE FROM ResourceEntity
    WHERE resourceId = :resourceId AND resourceType = :resourceType
    """,
  )
  suspend fun deleteResource(resourceId: String, resourceType: String): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertStringIndex(entity: StringIndexEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertReferenceIndex(entity: ReferenceIndexEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTokenIndex(entity: TokenIndexEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertQuantityIndex(entity: QuantityIndexEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUriIndex(entity: UriIndexEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDateIndex(entity: DateIndexEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDateTimeIndex(entity: DateTimeIndexEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNumberIndex(entity: NumberIndexEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPositionIndex(entity: PositionIndexEntity)

  @Query("DELETE FROM ResourceEntity")
  suspend fun deleteAllResources()

  @Query(
    """
    UPDATE ResourceEntity
    SET versionId = :versionId, lastUpdatedRemote = :lastUpdated
    WHERE resourceId = :resourceId AND resourceType = :resourceType
    """,
  )
  suspend fun updateVersionIdAndLastUpdated(
    resourceId: String,
    resourceType: String,
    versionId: String?,
    lastUpdated: Long?,
  )

  @Query(
    """
    UPDATE ResourceEntity
    SET resourceId = :newResourceId, versionId = :versionId, lastUpdatedRemote = :lastUpdated
    WHERE resourceId = :oldResourceId AND resourceType = :resourceType
    """,
  )
  suspend fun updateResourceIdAndMeta(
    oldResourceId: String,
    newResourceId: String,
    resourceType: String,
    versionId: String?,
    lastUpdated: Long?,
  )
}
