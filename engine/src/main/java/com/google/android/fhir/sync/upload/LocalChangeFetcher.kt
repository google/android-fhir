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

package com.google.android.fhir.sync.upload

import com.google.android.fhir.LocalChange
import com.google.android.fhir.db.Database
import com.google.android.fhir.sync.ResourceSyncException
import kotlin.properties.Delegates

/**
 * Fetches local changes.
 *
 * This interface provides methods to check for the existence of further changes, retrieve the next
 * batch of changes, and get the progress of fetched changes.
 *
 * It is marked as internal to keep [Database] unexposed to clients
 */
internal interface LocalChangeFetcher {

  /** Represents the initial total number of local changes to upload. */
  val total: Int

  /** Checks if there are more local changes to be fetched. */
  suspend fun hasNext(): Boolean

  /** Retrieves the next batch of local changes. */
  suspend fun next(): List<LocalChange>

  /**
   * Returns [SyncUploadProgress], which contains the remaining changes left to upload and the
   * initial total to upload.
   */
  suspend fun getProgress(): SyncUploadProgress
}

data class SyncUploadProgress(
  val remaining: Int,
  val initialTotal: Int,
  val uploadError: ResourceSyncException? = null,
)

internal class AllChangesLocalChangeFetcher(
  private val database: Database,
) : LocalChangeFetcher {

  override var total by Delegates.notNull<Int>()

  suspend fun initTotalCount() {
    total = database.getLocalChangesCount()
  }

  override suspend fun hasNext(): Boolean = database.getLocalChangesCount().isNotZero()

  override suspend fun next(): List<LocalChange> = database.getAllLocalChanges()

  override suspend fun getProgress(): SyncUploadProgress =
    SyncUploadProgress(database.getLocalChangesCount(), total)
}

internal class PerResourceLocalChangeFetcher(
  private val database: Database,
) : LocalChangeFetcher {

  override var total by Delegates.notNull<Int>()

  suspend fun initTotalCount() {
    total = database.getLocalChangesCount()
  }

  override suspend fun hasNext(): Boolean = database.getLocalChangesCount().isNotZero()

  override suspend fun next(): List<LocalChange> =
    database.getAllChangesForEarliestChangedResource()

  override suspend fun getProgress(): SyncUploadProgress =
    SyncUploadProgress(database.getLocalChangesCount(), total)
}

/** Represents the mode in which local changes should be fetched. */
sealed class LocalChangesFetchMode {
  object AllChanges : LocalChangesFetchMode()

  object PerResource : LocalChangesFetchMode()

  object EarliestChange : LocalChangesFetchMode()
}

internal object LocalChangeFetcherFactory {
  suspend fun byMode(
    mode: LocalChangesFetchMode,
    database: Database,
  ): LocalChangeFetcher =
    when (mode) {
      is LocalChangesFetchMode.AllChanges ->
        AllChangesLocalChangeFetcher(database).apply { initTotalCount() }
      is LocalChangesFetchMode.PerResource ->
        PerResourceLocalChangeFetcher(database).apply { initTotalCount() }
      else -> throw NotImplementedError("$mode is not implemented yet.")
    }
}

private fun Int.isNotZero() = this != 0
