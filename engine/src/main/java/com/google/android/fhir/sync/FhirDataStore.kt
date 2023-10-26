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

package com.google.android.fhir.sync

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.Data
import androidx.work.workDataOf
import com.google.android.fhir.OffsetDateTimeTypeAdapter
import com.google.gson.GsonBuilder
import java.io.IOException
import java.time.OffsetDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import timber.log.Timber

@PublishedApi
internal class FhirDataStore(context: Context) {
  private val Context.dataStore by
    preferencesDataStore(
      name = FHIR_PREFERENCES_NAME,
    )
  private val dataStore = context.dataStore
  private val gson =
    GsonBuilder()
      .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeTypeAdapter().nullSafe())
      .setExclusionStrategies(FhirSyncWorker.StateExclusionStrategy())
      .create()
  private val syncJobStatusFlowMap = mutableMapOf<String, Flow<SyncJobStatus?>>()
  private val allowedSyncJobStatusPackages =
    listOf(
      "com.google.android.fhir.sync.SyncJobStatus\$Finished",
      "com.google.android.fhir.sync.SyncJobStatus\$Failed",
    )
  private val lastSyncTimestampKey by lazy { stringPreferencesKey(LAST_SYNC_TIMESTAMP) }

  /**
   * Observes the sync job terminal state for a given key and provides it as a Flow.
   *
   * @param key The key associated with the sync job.
   * @return A Flow of [SyncJobStatus] representing the terminal state of the sync job, or null if
   *   the state is not allowed.
   */
  @PublishedApi
  internal fun observeSyncJobTerminalState(key: String): Flow<SyncJobStatus?> =
    syncJobStatusFlowMap.getOrPut(key) {
      dataStore.data
        .catch { exception ->
          if (exception is IOException) {
            emit(emptyPreferences())
          } else {
            Timber.e(exception)
            throw exception
          }
        }
        .map { preferences -> preferences[stringPreferencesKey(key)] }
        .map { statusData -> gson.fromJson(statusData, Data::class.java) }
        .map { data ->
          data?.let {
            val stateType = data.getString(STATE_TYPE)
            val stateData = data.getString(STATE)
            if (stateType?.isAllowedClass() == true) {
              stateData?.let { gson.fromJson(it, Class.forName(stateType)) as? SyncJobStatus }
            } else {
              null
            }
          }
        }
    }

  /**
   * Edits the DataStore to store synchronization job status. It creates a data object containing
   * the state type and serialized state of the synchronization job status. The edited preferences
   * are updated with the serialized data.
   *
   * @param syncJobStatus The synchronization job status to be stored.
   * @param key The key associated with the data to edit.
   */
  internal suspend fun updateSyncJobTerminalState(
    key: String,
    syncJobStatus: SyncJobStatus? = null,
  ) {
    updateJobStatus(key, syncJobStatus)
  }

  /**
   * Updates the last sync job status for the specified key.
   *
   * @param key The key associated with the sync job status.
   * @param syncJobStatus The sync job status to be updated.
   * @throws IllegalArgumentException if the provided syncJobStatus is an intermediate state.
   */
  internal suspend fun updateLastSyncJobStatus(key: String, syncJobStatus: SyncJobStatus) {
    when (syncJobStatus) {
      is SyncJobStatus.Finished,
      is SyncJobStatus.Failed, -> {
        updateJobStatus(key + LAST_JOB_TERMINAL_STATE, syncJobStatus)
      }
      else -> {
        throw IllegalArgumentException(
          "Intermediate state $syncJobStatus is not supported.",
        )
      }
    }
  }

  /**
   * Retrieves the last sync job status for the specified key.
   *
   * @param key The key associated with the sync job status.
   * @return The last sync job status.
   */
  internal suspend fun getLastSyncJobStatus(key: String) =
    observeSyncJobTerminalState(key + LAST_JOB_TERMINAL_STATE).first()

  internal fun readLastSyncTimestamp(): OffsetDateTime? {
    val millis = runBlocking { dataStore.data.first()[lastSyncTimestampKey] } ?: return null
    return OffsetDateTime.parse(millis)
  }

  internal fun writeLastSyncTimestamp(datetime: OffsetDateTime) {
    runBlocking { dataStore.edit { pref -> pref[lastSyncTimestampKey] = datetime.toString() } }
  }

  private suspend fun updateJobStatus(key: String, syncJobStatus: SyncJobStatus?) {
    dataStore.edit { preferences ->
      val data =
        syncJobStatus?.let {
          workDataOf(
            STATE_TYPE to it::class.java.name,
            STATE to gson.toJson(it),
          )
        }
      preferences[stringPreferencesKey(key)] = gson.toJson(data)
    }
  }

  private fun String.isAllowedClass(): Boolean {
    return allowedSyncJobStatusPackages.any { this.startsWith(it) }
  }

  companion object {
    private const val STATE_TYPE = "StateType"
    private const val STATE = "State"
    private const val LAST_JOB_TERMINAL_STATE = "lastJobTerminalState"

    private const val FHIR_PREFERENCES_NAME = "FHIR_ENGINE_PREF_DATASTORE"
    private const val LAST_SYNC_TIMESTAMP = "LAST_SYNC_TIMESTAMP"
  }
}
