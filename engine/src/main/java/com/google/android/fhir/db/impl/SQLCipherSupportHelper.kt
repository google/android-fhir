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

import android.util.Log
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.google.android.fhir.DatabaseErrorStrategy
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteDatabaseHook
import net.sqlcipher.database.SQLiteException
import net.sqlcipher.database.SQLiteOpenHelper

/** A [SupportSQLiteOpenHelper] which initializes a [SQLiteDatabase] with a passphrase. */
class SQLCipherSupportHelper(
  private val configuration: SupportSQLiteOpenHelper.Configuration,
  hook: SQLiteDatabaseHook? = null,
  private val databaseErrorStrategy: DatabaseErrorStrategy,
  private val passphraseFetcher: () -> ByteArray,
) : SupportSQLiteOpenHelper {

  init {
    SQLiteDatabase.loadLibs(configuration.context)
  }

  private val standardHelper =
    object :
      SQLiteOpenHelper(
        configuration.context,
        configuration.name,
        /* factory= */ null,
        configuration.callback.version,
        hook
      ) {
      override fun onCreate(db: SQLiteDatabase) {
        configuration.callback.onCreate(db)
      }

      override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        configuration.callback.onUpgrade(db, oldVersion, newVersion)
      }

      override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        configuration.callback.onDowngrade(db, oldVersion, newVersion)
      }

      override fun onOpen(db: SQLiteDatabase) {
        configuration.callback.onOpen(db)
      }

      override fun onConfigure(db: SQLiteDatabase) {
        configuration.callback.onConfigure(db)
      }
    }

  override fun getDatabaseName() = standardHelper.databaseName

  override fun setWriteAheadLoggingEnabled(enabled: Boolean) {
    standardHelper.setWriteAheadLoggingEnabled(enabled)
  }

  override fun getWritableDatabase(): SupportSQLiteDatabase? {
    val result =
      try {
        standardHelper.getWritableDatabase(passphraseFetcher())
      } catch (ex: SQLiteException) {
        if (databaseErrorStrategy == DatabaseErrorStrategy.RECREATE_AT_OPEN) {
          Log.w(LOG_TAG, "Fail to open database. Recreating database.")
          configuration.context.getDatabasePath(databaseName).delete()
          standardHelper.getWritableDatabase(passphraseFetcher())
        } else {
          throw ex
        }
      }
    return result
  }

  override fun getReadableDatabase() = writableDatabase

  override fun close() {
    standardHelper.close()
  }

  private companion object {
    const val LOG_TAG = "SQLCipherSupportHelper"
  }
}
