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

package com.google.android.fhir

import android.content.Context
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.cql.FhirEngineDataProvider
import com.google.android.fhir.cql.FhirEngineLibraryLoader
import com.google.android.fhir.cql.FhirEngineTerminologyProvider
import com.google.android.fhir.db.Database
import com.google.android.fhir.db.impl.DatabaseImpl
import com.google.android.fhir.impl.FhirEngineImpl
import com.google.android.fhir.search.impl.SearchImpl
import com.google.android.fhir.sync.FhirDataSource
import com.google.android.fhir.sync.PeriodicSyncConfiguration

internal data class FhirServices(
  val fhirEngine: FhirEngine,
  val parser: IParser,
  val database: Database
) {
  class Builder(private val dataSource: FhirDataSource, private val context: Context) {
    private var databaseName: String? = "fhirEngine"
    private var periodicSyncConfiguration: PeriodicSyncConfiguration? = null

    fun inMemory() = apply { databaseName = null }

    fun databaseName(name: String) = apply { databaseName = name }

    fun periodicSyncConfiguration(config: PeriodicSyncConfiguration) = apply {
      periodicSyncConfiguration = config
    }

    fun build(): FhirServices {
      val parser = FhirContext.forR4().newJsonParser()
      val db = DatabaseImpl(context = context, iParser = parser, databaseName = databaseName)

      val dataProvider = FhirEngineDataProvider.Factory.create(db)
      val engine =
        FhirEngineImpl(
          database = db,
          search = SearchImpl(db),
          libraryLoader = FhirEngineLibraryLoader(db),
          dataProviderMap = mapOf("http://hl7.org/fhir" to dataProvider),
          terminologyProvider = FhirEngineTerminologyProvider(),
          periodicSyncConfiguration = periodicSyncConfiguration,
          dataSource = dataSource,
          context = context
        )
      return FhirServices(fhirEngine = engine, parser = parser, database = db)
    }
  }

  companion object {
    @JvmStatic
    fun builder(dataSource: FhirDataSource, context: Context) = Builder(dataSource, context)
  }
}
