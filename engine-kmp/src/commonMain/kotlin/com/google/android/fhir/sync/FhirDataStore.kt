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

package com.google.android.fhir.sync

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import co.touchlab.kermit.Logger
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json

@PublishedApi
internal class FhirDataStore(private val dataStore: DataStore<Preferences>) {

  private val json = Json { ignoreUnknownKeys = true }
  private val mutexCacheMutex = Mutex()
  private val mutexCache = mutableMapOf<String, Mutex>()

  /**
   * Observes the sync job terminal state for a given key and provides it as a Flow.
   *
   * @param key The key associated with the sync job.
   * @return A Flow of [LastSyncJobStatus] representing the terminal state of the sync job, or null
   *   if the state is not allowed.
   */
  internal fun observeLastSyncJobStatus(key: String): Flow<LastSyncJobStatus?> =
    dataStore.data
      .catch { e ->
        Logger.e(e) { "Error reading FhirDataStore" }
        emit(emptyPreferences())
      }
      .map { prefs ->
        prefs[stringPreferencesKey(key)]
          ?.let { json.decodeFromString<SyncJobStatus>(it) }
          ?.let {
            when (it) {
              is SyncJobStatus.Succeeded -> LastSyncJobStatus.Succeeded(it.timestamp)
              is SyncJobStatus.Failed -> LastSyncJobStatus.Failed(it.timestamp)
              else -> null
            }
          }
      }

  /**
   * Edits the DataStore to store synchronization job status. It creates a data object containing
   * the state type and serialized state of the synchronization job status. The edited preferences
   * are updated with the serialized data.
   *
   * @param key The key associated with the data to edit.
   * @param syncJobStatus The synchronization job status to be stored.
   */
  internal suspend fun writeTerminalSyncJobStatus(key: String, syncJobStatus: SyncJobStatus) {
    when (syncJobStatus) {
      is SyncJobStatus.Succeeded,
      is SyncJobStatus.Failed, -> writeStatus(key, syncJobStatus)
      else -> error("Cannot persist non-terminal sync status")
    }
  }

  internal suspend fun readLastSyncTimestamp(): Instant? =
    dataStore.data.first()[stringPreferencesKey(LAST_SYNC_TIMESTAMP_KEY)]?.let { Instant.parse(it) }

  internal suspend fun writeLastSyncTimestamp(timestamp: Instant) =
    dataStore.edit { it[stringPreferencesKey(LAST_SYNC_TIMESTAMP_KEY)] = timestamp.toString() }

  /** Stores the given unique-work-name in DataStore. */
  @PublishedApi
  internal suspend fun storeUniqueWorkName(key: String, value: String) =
    mutexFor(key).withLock { dataStore.edit { it[stringPreferencesKey("$key-name")] = value } }

  @PublishedApi
  internal suspend fun removeUniqueWorkName(key: String) =
    mutexFor(key).withLock {
      dataStore.edit {
        val value = it.remove(stringPreferencesKey("$key-name"))
        Logger.d("Removed value: $value")
      }
    }

  /** Fetches the stored unique-work-name from DataStore. */
  @PublishedApi
  internal suspend fun fetchUniqueWorkName(key: String): String? =
    mutexFor(key).withLock { dataStore.data.first()[stringPreferencesKey("$key-name")] }

  private suspend fun writeStatus(key: String, syncJobStatus: SyncJobStatus) {
    dataStore.edit { prefs ->
      prefs[stringPreferencesKey(key)] = json.encodeToString(syncJobStatus)
    }
  }

  private fun readLastStatus(prefs: Preferences, key: String): LastSyncJobStatus? {
    // This method is now replaced by logic in observeLastSyncJobStatus map.
    // Keeping it for internal use if needed, but updated to use json.
    return prefs[stringPreferencesKey(key)]
      ?.let { json.decodeFromString<SyncJobStatus>(it) }
      ?.let {
        when (it) {
          is SyncJobStatus.Succeeded -> LastSyncJobStatus.Succeeded(it.timestamp)
          is SyncJobStatus.Failed -> LastSyncJobStatus.Failed(it.timestamp)
          else -> null
        }
      }
  }

  private suspend fun mutexFor(key: String): Mutex =
    mutexCacheMutex.withLock { mutexCache.getOrPut(key) { Mutex() } }

  companion object {
    private const val LAST_SYNC_TIMESTAMP_KEY = "LAST_SYNC_TIMESTAMP"
  }
}
