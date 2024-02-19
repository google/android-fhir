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

package com.google.android.fhir.demo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.fhir.sync.DownloadWorkManager
import kotlinx.coroutines.flow.first
import org.hl7.fhir.r4.model.ResourceType

private val Context.dataStorage: DataStore<Preferences> by
  preferencesDataStore(name = "demo_app_storage")

/**
 * Stores the lastUpdated timestamp per resource to be used by [DownloadWorkManager]'s
 * implementation for optimal sync. See
 * [_lastUpdated](https://build.fhir.org/search.html#_lastUpdated).
 */
class DemoDataStore(private val context: Context) {

  suspend fun saveLastUpdatedTimestamp(resourceType: ResourceType, timestamp: String) {
    context.dataStorage.edit { pref -> pref[stringPreferencesKey(resourceType.name)] = timestamp }
  }

  suspend fun getLasUpdateTimestamp(resourceType: ResourceType): String? {
    return context.dataStorage.data.first()[stringPreferencesKey(resourceType.name)]
  }
}
