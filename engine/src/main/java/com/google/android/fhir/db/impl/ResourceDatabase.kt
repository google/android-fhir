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

package com.google.android.fhir.db.impl

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.google.android.fhir.db.impl.dao.LocalChangeDao
import com.google.android.fhir.db.impl.dao.ResourceDao
import com.google.android.fhir.db.impl.dao.SyncedResourceDao
import com.google.android.fhir.db.impl.entities.DateIndexEntity
import com.google.android.fhir.db.impl.entities.DateTimeIndexEntity
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.db.impl.entities.NumberIndexEntity
import com.google.android.fhir.db.impl.entities.PositionIndexEntity
import com.google.android.fhir.db.impl.entities.QuantityIndexEntity
import com.google.android.fhir.db.impl.entities.ReferenceIndexEntity
import com.google.android.fhir.db.impl.entities.ResourceEntity
import com.google.android.fhir.db.impl.entities.StringIndexEntity
import com.google.android.fhir.db.impl.entities.SyncedResourceEntity
import com.google.android.fhir.db.impl.entities.TokenIndexEntity
import com.google.android.fhir.db.impl.entities.UriIndexEntity

@Database(
  entities =
    [
      ResourceEntity::class,
      StringIndexEntity::class,
      ReferenceIndexEntity::class,
      TokenIndexEntity::class,
      QuantityIndexEntity::class,
      UriIndexEntity::class,
      DateIndexEntity::class,
      DateTimeIndexEntity::class,
      NumberIndexEntity::class,
      SyncedResourceEntity::class,
      LocalChangeEntity::class,
      PositionIndexEntity::class],
  version = 1,
  exportSchema = false
)
@TypeConverters(DbTypeConverters::class)
internal abstract class ResourceDatabase : RoomDatabase() {
  abstract fun resourceDao(): ResourceDao
  abstract fun syncedResourceDao(): SyncedResourceDao
  abstract fun localChangeDao(): LocalChangeDao
}
