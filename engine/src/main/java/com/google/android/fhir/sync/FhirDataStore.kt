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
import android.util.Log
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

private const val FHIR_PREFERENCES_NAME = "fhir_preferences"

class FhirDataStore(context: Context) {
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
  private val syncJobStatusFlowMap = mutableMapOf<String, Flow<SyncJobStatusPreferences>>()

  /**
   * Utilizes a flow from the DataStore to collect and transform data. It catches potential
   * IOExceptions and emits an empty preference instance in such cases. The collected data is then
   * mapped into a [SyncJobStatusPreferences].
   *
   * @param key The key associated with the data to collect.
   * @return A flow that emits the [SyncJobStatusPreferences].
   */
  fun getSyncJobStatusPreferencesFlow(key: String): Flow<SyncJobStatusPreferences> =
    syncJobStatusFlowMap.getOrPut(key) {
      dataStore.data
        .catch { exception ->
          if (exception is IOException) {
            emit(emptyPreferences())
          } else {
            throw exception
          }
        }
        .mapNotNull { preferences -> preferences[stringPreferencesKey(key)] }
        .mapNotNull { statusData -> gson.fromJson(statusData, Data::class.java) }
        .mapNotNull { data ->
          val stateType = data.getString("StateType")
          val stateData = data.getString("State")
          stateType?.let { type ->
            stateData?.let { gson.fromJson(stateData, Class.forName(type)) as? SyncJobStatus }
          }
        }
        .map { syncStatus -> SyncJobStatusPreferences(syncStatus) }
    }

  /**
   * Edits the DataStore to store synchronization job status. It creates a data object containing
   * the state type and serialized state of the synchronization job status. The edited preferences
   * are updated with the serialized data.
   *
   * @param syncJobStatus The synchronization job status to be stored.
   * @param key The key associated with the data to edit.
   */
  suspend fun updateSyncJobStatus(key: String, syncJobStatus: SyncJobStatus) {
    dataStore.edit { preferences ->
      val data =
        workDataOf(
          "StateType" to syncJobStatus::class.java.name,
          "State" to gson.toJson(syncJobStatus)
        )
      Log.d("Demo1", "edit operation : " + gson.toJson(data))
      preferences[stringPreferencesKey(key)] = gson.toJson(data)
    }
  }
}

data class SyncJobStatusPreferences(val status: SyncJobStatus? = null)
