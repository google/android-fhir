/*
 * Copyright 2022-2024 Google LLC
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
import com.google.android.fhir.db.Database
import com.google.android.fhir.db.impl.DatabaseConfiguration
import com.google.android.fhir.db.impl.DatabaseImpl
import com.google.android.fhir.impl.FhirEngineImpl
import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.sync.FhirDataStore
import com.google.android.fhir.sync.remote.FhirHttpDataSource
import com.google.android.fhir.sync.remote.RetrofitHttpService

internal data class FhirServices(
  val fhirEngine: FhirEngine,
  val database: Database,
  val remoteDataSource: DataSource? = null,
  val fhirDataStore: FhirDataStore,
) {
  class Builder(private val context: Context) {
    private var syncConfiguration: SyncConfiguration? = null
    private var databaseConfiguration = DatabaseConfiguration()

    internal fun setSyncConfiguration(syncConfiguration: SyncConfiguration) = apply {
      this.syncConfiguration = syncConfiguration
    }

    internal fun setDatabaseConfiguration(databaseConfiguration: DatabaseConfiguration) = apply {
      this.databaseConfiguration = databaseConfiguration
    }

    fun build(): FhirServices {
      val db = DatabaseImpl(context = context, databaseConfiguration = databaseConfiguration)
      val engine = FhirEngineImpl(database = db, context = context)
      val remoteDataSource =
        syncConfiguration?.serverConfiguration?.let {
          FhirHttpDataSource(
            fhirHttpService =
              RetrofitHttpService.builder(it.baseUrl, it.networkConfiguration)
                .setAuthenticator(it.authenticator)
                .setHttpLogger(it.httpLogger)
                .build(),
          )
        }
      return FhirServices(
        fhirEngine = engine,
        database = db,
        remoteDataSource = remoteDataSource,
        fhirDataStore = FhirDataStore(context),
      )
    }
  }

  companion object {
    fun builder(context: Context) = Builder(context)
  }
}
