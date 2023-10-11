/*
 * Copyright 2023 Google LLC
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
      PositionIndexEntity::class,
    ],
  version = 7,
  exportSchema = true,
)
@TypeConverters(DbTypeConverters::class)
internal abstract class ResourceDatabase : RoomDatabase() {
  abstract fun resourceDao(): ResourceDao

  abstract fun localChangeDao(): LocalChangeDao
}

val MIGRATION_1_2 =
  object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
      database.execSQL("DROP table if exists SyncedResourceEntity")
    }
  }

val MIGRATION_2_3 =
  object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
      database.execSQL(
        "CREATE INDEX IF NOT EXISTS `index_DateTimeIndexEntity_index_from` ON `DateTimeIndexEntity` (`index_from`)",
      )
    }
  }

val MIGRATION_3_4 =
  object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
      database.execSQL(
        "CREATE INDEX IF NOT EXISTS `index_DateTimeIndexEntity_resourceType_index_name_resourceUuid_index_from_index_to` ON `DateTimeIndexEntity` (`resourceType`, `index_name`, `resourceUuid`, `index_from`, `index_to`)",
      )
      database.execSQL(
        "CREATE INDEX IF NOT EXISTS `index_DateIndexEntity_resourceType_index_name_resourceUuid_index_from_index_to` ON `DateIndexEntity` (`resourceType`, `index_name`, `resourceUuid`, `index_from`, `index_to`)",
      )
      database.execSQL(
        "CREATE INDEX IF NOT EXISTS `index_TokenIndexEntity_resourceType_index_name_index_system_index_value_resourceUuid` ON `TokenIndexEntity` (`resourceType`, `index_name`, `index_system`, `index_value`, `resourceUuid`)",
      )
      database.execSQL("DROP INDEX IF EXISTS `index_DateTimeIndexEntity_index_from`")
      database.execSQL(
        "DROP INDEX IF EXISTS `index_DateTimeIndexEntity_resourceType_index_name_index_from_index_to`",
      )
      database.execSQL(
        "DROP INDEX IF EXISTS `index_DateIndexEntity_resourceType_index_name_index_from_index_to`",
      )
      database.execSQL(
        "DROP INDEX IF EXISTS `index_TokenIndexEntity_resourceType_index_name_index_system_index_value`",
      )
    }
  }

val MIGRATION_4_5 =
  object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
      database.execSQL(
        "ALTER TABLE `ResourceEntity` ADD COLUMN `lastUpdatedLocal` INTEGER DEFAULT NULL",
      )
    }
  }

/** Changes column type of [LocalChangeEntity.timestamp] from [String] to [java.time.Instant]. */
val MIGRATION_5_6 =
  object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
      database.execSQL(
        "CREATE TABLE IF NOT EXISTS `_new_LocalChangeEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `resourceType` TEXT NOT NULL, `resourceId` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `type` INTEGER NOT NULL, `payload` TEXT NOT NULL, `versionId` TEXT)",
      )
      database.execSQL(
        "INSERT INTO `_new_LocalChangeEntity` (`id`,`resourceType`,`resourceId`,`timestamp`,`type`,`payload`,`versionId`) SELECT `id`,`resourceType`,`resourceId`, COALESCE(strftime('%s', `timestamp`) ||  substr(strftime('%f', `timestamp`), 4) , /* default is EPOCH*/ 0 ),`type`,`payload`,`versionId` FROM `LocalChangeEntity`",
      )
      database.execSQL("DROP TABLE `LocalChangeEntity`")
      database.execSQL("ALTER TABLE `_new_LocalChangeEntity` RENAME TO `LocalChangeEntity`")
      database.execSQL(
        "CREATE INDEX IF NOT EXISTS `index_LocalChangeEntity_resourceType_resourceId` ON `LocalChangeEntity` (`resourceType`, `resourceId`)",
      )
    }
  }

/** Add column resourceUuid in [LocalChangeEntity] */
val MIGRATION_6_7 =
  object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
      database.execSQL(
        "CREATE TABLE IF NOT EXISTS `_new_LocalChangeEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `resourceType` TEXT NOT NULL, `resourceId` TEXT NOT NULL, `resourceUuid` BLOB NOT NULL, `timestamp` INTEGER NOT NULL, `type` INTEGER NOT NULL, `payload` TEXT NOT NULL, `versionId` TEXT)",
      )
      database.execSQL(
        "INSERT INTO `_new_LocalChangeEntity` (`id`,`resourceType`,`resourceId`,`resourceUuid`,`timestamp`,`type`,`payload`,`versionId`) " +
          "SELECT localChange.id, localChange.resourceType, localChange.resourceId, resource.resourceUuid, localChange.timestamp, localChange.type, localChange.payload, localChange.versionId FROM `LocalChangeEntity` localChange LEFT JOIN ResourceEntity resource ON localChange.resourceId= resource.resourceId",
      )
      database.execSQL("DROP TABLE `LocalChangeEntity`")
      database.execSQL("ALTER TABLE `_new_LocalChangeEntity` RENAME TO `LocalChangeEntity`")
      database.execSQL(
        "CREATE INDEX IF NOT EXISTS `index_LocalChangeEntity_resourceType_resourceId` ON `LocalChangeEntity` (`resourceType`, `resourceId`)",
      )
      database.execSQL(
        "CREATE INDEX IF NOT EXISTS `index_LocalChangeEntity_resourceUuid` ON `LocalChangeEntity` (`resourceUuid`)",
      )
    }
  }
