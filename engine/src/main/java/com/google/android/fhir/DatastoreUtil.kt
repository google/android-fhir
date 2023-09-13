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

package com.google.android.fhir

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.time.OffsetDateTime
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by
  preferencesDataStore(name = "FHIR_ENGINE_PREF_DATASTORE")

internal class DatastoreUtil(private val context: Context) {
  private val lastSyncTimestampKey by lazy { stringPreferencesKey("LAST_SYNC_TIMESTAMP") }

  fun readLastSyncTimestamp(): OffsetDateTime? {
    val millis = runBlocking { context.dataStore.data.first()[lastSyncTimestampKey] } ?: return null

    return OffsetDateTime.parse(millis)
  }

  fun writeLastSyncTimestamp(datetime: OffsetDateTime) {
    runBlocking {
      context.dataStore.edit { pref -> pref[lastSyncTimestampKey] = datetime.toString() }
    }
  }
}
