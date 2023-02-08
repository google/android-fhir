/*
 * Copyright 2021 Google LLC
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

import androidx.annotation.WorkerThread
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.google.android.fhir.DatabaseErrorStrategy
import com.google.android.fhir.db.DatabaseEncryptionException
import com.google.android.fhir.db.DatabaseEncryptionException.DatabaseEncryptionErrorCode.TIMEOUT
import com.google.android.fhir.db.DatabaseEncryptionException.DatabaseEncryptionErrorCode.UNKNOWN
import com.google.android.fhir.db.impl.DatabaseImpl.Companion.UNENCRYPTED_DATABASE_NAME
import java.time.Duration
import java.util.UUID
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteDatabaseHook
import net.sqlcipher.database.SQLiteException
import net.sqlcipher.database.SQLiteOpenHelper
import timber.log.Timber

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

        /*//Load the spellfix3 extension
        db.query(
          """
					SELECT load_extension('libspellfix3.so')
				""".trimIndent()
        )*/

        //Use it to create a test table
        db.execSQL(
          """
			create table "Test" (
        "rowId" integer primary key autoincrement not null,
				"id" text unique not null,
				"text1" text not null,
        "searchText" text not null
			)
			""".trimIndent()
        )

        //Use it to create a virtual table
        db.execSQL(
          """
					create virtual table "Demo" using spellfix1
				""".trimIndent()
        )

        /*//Add words into the database
        db.execSQL(
          """
			insert into "Demo"("rowid","word") select "rowId","searchText" from "Test"
		""".trimIndent()
        )*/
      }

      override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        configuration.callback.onUpgrade(db, oldVersion, newVersion)
      }

      override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        configuration.callback.onDowngrade(db, oldVersion, newVersion)
      }

      override fun onOpen(db: SQLiteDatabase) {
        configuration.callback.onOpen(db)
        Timber.e(itemsMatching("some").toString())
        //db.execSQL("SELECT spellfix1(?, ?, ?, ?, ?);", arrayOf("vocab", "main", "words", "word", 2))
      }

      override fun onConfigure(db: SQLiteDatabase) {
        configuration.callback.onConfigure(db)
      }
    }


  override fun setWriteAheadLoggingEnabled(enabled: Boolean) {
    standardHelper.setWriteAheadLoggingEnabled(enabled)
  }

  private fun getWritableDb(): SupportSQLiteDatabase {
    check(!configuration.context.getDatabasePath(UNENCRYPTED_DATABASE_NAME).exists()) {
      "Unexpected unencrypted database, $UNENCRYPTED_DATABASE_NAME, already exists. " +
        "Check if you have accidentally disabled database encryption across releases."
    }
    val key = runBlocking { getPassphraseWithRetry() }
    return try {
      standardHelper.getWritableDatabase(key)
    } catch (ex: SQLiteException) {
      if (databaseErrorStrategy == DatabaseErrorStrategy.RECREATE_AT_OPEN) {
        Timber.w("Fail to open database. Recreating database.")
        configuration.context.getDatabasePath(databaseName).delete()
        standardHelper.getWritableDatabase(key)
      } else {
        throw ex
      }
    }
  }

  private suspend fun getPassphraseWithRetry(): ByteArray {
    var lastException: DatabaseEncryptionException? = null
    for (retryAttempt in 1..MAX_RETRY_ATTEMPTS) {
      try {
        return passphraseFetcher()
      } catch (exception: DatabaseEncryptionException) {
        lastException = exception
        if (exception.errorCode == TIMEOUT) {
          Timber.i("Fail to get the encryption key on attempt: $retryAttempt")
          delay(retryDelay.toMillis() * retryAttempt)
        } else {
          throw exception
        }
      }
    }
    Timber.w("Can't access the database encryption key after $MAX_RETRY_ATTEMPTS attempts.")
    throw lastException ?: DatabaseEncryptionException(Exception(), UNKNOWN)
  }

  override val databaseName: String = standardHelper.databaseName
  override val readableDatabase: SupportSQLiteDatabase = getWritableDb()
  override val writableDatabase: SupportSQLiteDatabase = getWritableDb()

  override fun close() {
    standardHelper.close()
  }

  private companion object {
    const val MAX_RETRY_ATTEMPTS = 3

    /** The time delay before retrying a database operation. */
    val retryDelay: Duration = Duration.ofSeconds(1)
  }

  data class ReadItem(
    val id: UUID,
    val text: String,
    val editDistance: Int
  )

  @WorkerThread
  fun items(search: String = ""): List<ReadItem> {
    return readableDatabase
      .query(
        """
          select *, editdist3('$search', "searchText") as "editDist" from "Test" where ("searchText" like '%$search%')
        """.trimIndent()
      ).run {
        use {
          generateSequence {
            if (moveToNext()) {
              ReadItem(
                id = UUID.fromString(getString(getColumnIndex("id"))),
                text = getString(getColumnIndex("text1")),
                editDistance = getInt(getColumnIndex("editDist"))
              )
            } else null
          }.toList()
        }
      }

  }

  @WorkerThread
  fun itemsMatching(pattern: String): List<ReadItem> {
    return readableDatabase
      .query(
        """
			select "Test"."text1", "Demo"."score" as score
        from "Demo" inner join "Test" on "Demo"."rowid" = "Test"."rowId"
        where "Demo"."word" match '$pattern*' and score < 500 and top=3
		""".trimIndent()
      )
      .run {
        use {
          generateSequence {
            if (moveToNext()) {
              ReadItem(
                //									id = UUID.fromString(getString(getColumnIndex("id"))),
                id = UUID.randomUUID(),
                text = getString(getColumnIndex("text1")),
                editDistance = getInt(getColumnIndex("score"))
              )
            } else null
          }.toList()
        }
      }
  }
}
