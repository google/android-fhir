/*
 * Copyright 2022-2026 Google LLC
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
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.util.FhirTerser
import com.google.android.fhir.db.Database
import com.google.android.fhir.db.impl.DatabaseConfig
import com.google.android.fhir.db.impl.DatabaseEncryptionKeyProvider.isDatabaseEncryptionSupported
import com.google.android.fhir.db.impl.DatabaseImpl
import com.google.android.fhir.impl.FhirEngineImpl
import com.google.android.fhir.index.ResourceIndexer
import com.google.android.fhir.index.SearchParamDefinitionsProvider
import com.google.android.fhir.index.SearchParamDefinitionsProviderImpl
import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.sync.FhirDataStore
import com.google.android.fhir.sync.remote.FhirHttpDataSource
import com.google.android.fhir.sync.remote.RetrofitHttpService
import org.hl7.fhir.r4.model.SearchParameter
import timber.log.Timber

internal data class FhirServices(
  val fhirEngine: FhirEngine,
  val database: Database,
  val remoteDataSource: DataSource? = null,
  val fhirDataStore: FhirDataStore,
  val searchParamProvider: SearchParamDefinitionsProvider,
) {
  class Builder(private val context: Context) {
    private var inMemory: Boolean = false
    private var enableEncryption: Boolean = false
    private var databaseErrorStrategy = DatabaseErrorStrategy.UNSPECIFIED
    private var serverConfiguration: ServerConfiguration? = null
    private var searchParameters: List<SearchParameter>? = null

    internal fun inMemory() = apply { inMemory = true }

    internal fun enableEncryptionIfSupported() = apply {
      if (!isDatabaseEncryptionSupported()) {
        Timber.w("Database encryption isn't supported in this device.")
        return this
      }
      enableEncryption = true
    }

    internal fun setDatabaseErrorStrategy(databaseErrorStrategy: DatabaseErrorStrategy) = apply {
      this.databaseErrorStrategy = databaseErrorStrategy
    }

    internal fun setServerConfiguration(serverConfiguration: ServerConfiguration) = apply {
      this.serverConfiguration = serverConfiguration
    }

    internal fun setSearchParameters(searchParameters: List<SearchParameter>?) = apply {
      this.searchParameters = searchParameters
    }

    fun build(): FhirServices {
      val terser = FhirTerser(FhirContext.forR4Cached())
      val searchParamMap =
        searchParameters?.asMapOfResourceTypeToSearchParamDefinitions() ?: emptyMap()
      val provider = SearchParamDefinitionsProviderImpl(searchParamMap)
      val db =
        DatabaseImpl(
          context = context,
          fhirTerser = terser,
          DatabaseConfig(inMemory, enableEncryption, databaseErrorStrategy),
          resourceIndexer = ResourceIndexer(provider),
        )
      val engine = FhirEngineImpl(database = db, context = context)
      val remoteDataSource =
        serverConfiguration?.let {
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
        searchParamProvider = provider,
      )
    }
  }

  companion object {
    fun builder(context: Context) = Builder(context)
  }
}
