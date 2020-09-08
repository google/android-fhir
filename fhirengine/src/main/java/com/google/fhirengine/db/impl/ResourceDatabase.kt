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

package com.google.fhirengine.db.impl

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.google.fhirengine.db.impl.dao.ResourceDao
import com.google.fhirengine.db.impl.dao.SyncedResourceDao
import com.google.fhirengine.db.impl.entities.DateIndexEntity
import com.google.fhirengine.db.impl.entities.LocalChange
import com.google.fhirengine.db.impl.entities.NumberIndexEntity
import com.google.fhirengine.db.impl.entities.QuantityIndexEntity
import com.google.fhirengine.db.impl.entities.ReferenceIndexEntity
import com.google.fhirengine.db.impl.entities.ResourceEntity
import com.google.fhirengine.db.impl.entities.StringIndexEntity
import com.google.fhirengine.db.impl.entities.SyncedResourceEntity
import com.google.fhirengine.db.impl.entities.TokenIndexEntity
import com.google.fhirengine.db.impl.entities.UriIndexEntity

@Database(
        entities = [
            ResourceEntity::class,
            StringIndexEntity::class,
            ReferenceIndexEntity::class,
            TokenIndexEntity::class,
            QuantityIndexEntity::class,
            UriIndexEntity::class,
            DateIndexEntity::class,
            NumberIndexEntity::class,
            SyncedResourceEntity::class,
            LocalChange::class
        ],
        version = 1,
        exportSchema = false
)
@TypeConverters(
    DbTypeConverters::class
)
internal abstract class ResourceDatabase : RoomDatabase() {
    abstract fun resourceDao(): ResourceDao
    abstract fun syncedResourceDao(): SyncedResourceDao
}
