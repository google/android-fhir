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
import com.google.android.fhir.db.impl.entities.SyncedResourceEntity
import org.hl7.fhir.r4.model.ResourceType

@Dao
interface SyncedResourceDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(entity: SyncedResourceEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(resources: List<SyncedResourceEntity>)

  /**
   * We will always have 1 entry for each [ResourceType] as it's the primary key, so we can limit
   * the result to 1. If there is no entry for that [ResourceType] then `null` will be returned.
   */
  @Query(
    """SELECT lastUpdate FROM SyncedResourceEntity 
        WHERE resourceType = :resourceType LIMIT 1"""
  )
  suspend fun getLastUpdate(resourceType: ResourceType): String?
}
