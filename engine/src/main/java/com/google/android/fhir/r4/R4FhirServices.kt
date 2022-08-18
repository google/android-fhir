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

package com.google.android.fhir.r4

import android.content.Context
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.db.impl.DatabaseEncryptionKeyProvider.isDatabaseEncryptionSupported
import com.google.android.fhir.r4.db.impl.DatabaseConfig
import com.google.android.fhir.r4.db.impl.DatabaseImpl
import com.google.android.fhir.r4.db.impl.R4Database
import com.google.android.fhir.r4.impl.R4FhirEngineImpl
import com.google.android.fhir.r4.sync.R4DataSource
import com.google.android.fhir.r4.sync.remote.RemoteFhirService
import timber.log.Timber

internal data class R4FhirServices(
  val fhirEngine: R4FhirEngine,
  val parser: IParser,
  val database: R4Database,
  val remoteDataSource: R4DataSource? = null
) {
  class Builder(private val context: Context) {
    private var inMemory: Boolean = false
    private var enableEncryption: Boolean = false
    private var databaseErrorStrategy = DatabaseErrorStrategy.UNSPECIFIED
    private var serverConfiguration: ServerConfiguration? = null

    internal fun inMemory() = apply { inMemory = true }

    internal fun enableEncryptionIfSupported() = apply {
      if (!isDatabaseEncryptionSupported()) {
        Timber.w("Database encryption isn't supported in this device.")
        return this
      }
      enableEncryption = true
    }

    internal fun setDatabaseErrorStrategy(databaseErrorStrategy: DatabaseErrorStrategy) {
      this.databaseErrorStrategy = databaseErrorStrategy
    }

    internal fun setServerConfiguration(serverConfiguration: ServerConfiguration) {
      this.serverConfiguration = serverConfiguration
    }

    fun build(): R4FhirServices {
      val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
      val db =
        DatabaseImpl(
          context = context,
          iParser = parser,
          DatabaseConfig(inMemory, enableEncryption, databaseErrorStrategy)
        )
      val engine = R4FhirEngineImpl(database = db, context = context)
      val remoteDataSource =
        serverConfiguration?.let {
          RemoteFhirService.builder(it.baseUrl, it.networkConfiguration)
            .apply { setAuthenticator(it.authenticator) }
            .build()
        }
      return R4FhirServices(
        fhirEngine = engine,
        parser = parser,
        database = db,
        remoteDataSource = remoteDataSource
      )
    }
  }

  companion object {
    fun builder(context: Context) = Builder(context)
  }
}
