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

package com.google.android.fhir.db.impl

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.android.fhir.db.impl.dao.LocalChangeDao
import com.google.android.fhir.db.impl.dao.ResourceDao
import com.google.android.fhir.db.impl.entities.DateIndexEntity
import com.google.android.fhir.db.impl.entities.DateTimeIndexEntity
import com.google.android.fhir.db.impl.entities.LocalChangeEntity
import com.google.android.fhir.db.impl.entities.NumberIndexEntity
import com.google.android.fhir.db.impl.entities.PositionIndexEntity
import com.google.android.fhir.db.impl.entities.QuantityIndexEntity
import com.google.android.fhir.db.impl.entities.ReferenceIndexEntity
import com.google.android.fhir.db.impl.entities.ResourceEntity
import com.google.android.fhir.db.impl.entities.StringIndexEntity
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
      LocalChangeEntity::class,
      PositionIndexEntity::class
    ],
  version = 3,
  exportSchema = true
)
@TypeConverters(DbTypeConverters::class)
internal abstract class ResourceDatabase : RoomDatabase() {
  abstract fun resourceDao(): ResourceDao
  abstract fun localChangeDao(): LocalChangeDao
}

val MIGRATION_1_2 =
  object : Migration(/* startVersion = */ 1, /* endVersion = */ 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
      database.execSQL("DROP table if exists SyncedResourceEntity")
    }
  }

val MIGRATION_2_3 =
  object : Migration(/* startVersion = */ 2, /* endVersion = */ 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
      database.execSQL(
        "CREATE INDEX IF NOT EXISTS `index_DateTimeIndexEntity_index_from` ON `DateTimeIndexEntity` (`index_from`)"
      )
    }
  }
