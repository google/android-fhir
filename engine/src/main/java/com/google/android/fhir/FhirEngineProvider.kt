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
import com.google.android.fhir.DatabaseErrorStrategy.UNSPECIFIED
import com.google.android.fhir.sync.Authenticator
import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.sync.remote.HttpLogger
import org.hl7.fhir.r4.model.SearchParameter

/** The provider for [FhirEngine] instance. */
object FhirEngineProvider {
  private var fhirEngineConfiguration: FhirEngineConfiguration? = null
  private var fhirServices: FhirServices? = null

  /**
   * Initializes the [FhirEngine] singleton with a custom Configuration.
   *
   * This method throws [IllegalStateException] if it is called multiple times
   */
  @Synchronized
  fun init(fhirEngineConfiguration: FhirEngineConfiguration) {
    check(this.fhirEngineConfiguration == null) {
      "FhirEngineProvider: FhirEngineConfiguration has already been initialized."
    }
    this.fhirEngineConfiguration = fhirEngineConfiguration
  }

  /**
   * Returns the cached [FhirEngine] instance. Creates a new instance from the supplied [Context] if
   * it doesn't exist.
   *
   * If this method is called without calling [init], the default [FhirEngineConfiguration] is used.
   */
  @Synchronized
  fun getInstance(context: Context): FhirEngine {
    return getOrCreateFhirService(context).fhirEngine
  }

  @Synchronized
  @JvmStatic // needed for mockito
  internal fun getDataSource(context: Context): DataSource? {
    return getOrCreateFhirService(context).remoteDataSource
  }

  @Synchronized
  private fun getOrCreateFhirService(context: Context): FhirServices {
    if (fhirServices == null) {
      fhirEngineConfiguration = fhirEngineConfiguration ?: FhirEngineConfiguration()
      val configuration = checkNotNull(fhirEngineConfiguration)
      fhirServices =
        FhirServices.builder(context.applicationContext)
          .apply {
            if (configuration.enableEncryptionIfSupported) enableEncryptionIfSupported()
            setDatabaseErrorStrategy(configuration.databaseErrorStrategy)
            configuration.serverConfiguration?.let { setServerConfiguration(it) }
            configuration.customSearchParameters?.let { setSearchParameters(it) }
            if (configuration.testMode) {
              inMemory()
            }
          }
          .build()
    }
    return checkNotNull(fhirServices)
  }

  @Synchronized
  fun cleanup() {
    check(fhirEngineConfiguration?.testMode == true) {
      "FhirEngineProvider: FhirEngineProvider needs to be in the test mode to perform cleanup."
    }
    forceCleanup()
  }

  internal fun forceCleanup() {
    fhirServices?.database?.close()
    fhirServices = null
    fhirEngineConfiguration = null
  }
}

/**
 * A configuration which describes the database setup and error recovery.
 *
 * Database encryption is only available on API 23 or above. If enableEncryptionIfSupported is true,
 * FHIR SDK will only enable database encryption on API 23 or above.
 *
 * WARNING: Your app may try to decrypt an unencrypted database from a device which was previously
 * on API 22 but later upgraded to API 23. When this happens, an [IllegalStateException] is thrown.
 */
data class FhirEngineConfiguration(
  val enableEncryptionIfSupported: Boolean = false,
  val databaseErrorStrategy: DatabaseErrorStrategy = UNSPECIFIED,
  val serverConfiguration: ServerConfiguration? = null,
  val testMode: Boolean = false,
  /**
   * Additional search parameters to be used to query FHIR engine using the search API. These are in
   * addition to the default search parameters defined in
   * [FHIR](https://www.hl7.org/fhir/searchparameter-registry.html). The search parameters should be
   * unique and not change the existing/default search parameters and it may lead to unexpected
   * search behaviour.
   *
   * NOTE: The engine doesn't reindex resources after a new [SearchParameter] is added to the
   * engine. It is the responsibility of the app developer to reindex the resources by updating
   * them. Any new CRUD operations on a resource after a new [SearchParameter] is added will result
   * in the reindexing of the resource.
   */
  val customSearchParameters: List<SearchParameter>? = null
)

enum class DatabaseErrorStrategy {
  /**
   * If unspecified, all database errors will be propagated to the call site. The caller shall
   * handle the database error on a case-by-case basis.
   */
  UNSPECIFIED,

  /**
   * If a database error occurs at open, automatically recreate the database.
   *
   * This strategy is NOT respected when opening a previously unencrypted database with an encrypted
   * configuration or vice versa. An [IllegalStateException] is thrown instead.
   */
  RECREATE_AT_OPEN
}

/** A configuration to provide necessary params for network connection. */
data class ServerConfiguration(
  /** Url of the remote FHIR server. */
  val baseUrl: String,
  /** A configuration to provide the network connection parameters. */
  val networkConfiguration: NetworkConfiguration = NetworkConfiguration(),
  /**
   * An [Authenticator] for supplying any auth token that may be necessary to communicate with the
   * server
   */
  val authenticator: Authenticator? = null,
  /** Logs the communication between the engine and the remote server. */
  val httpLogger: HttpLogger = HttpLogger.NONE
)

/** A configuration to provide the network connection parameters. */
data class NetworkConfiguration(
  /** Connection timeout (in seconds). The default is 10 seconds. */
  val connectionTimeOut: Long = 10,
  /** Read timeout (in seconds) for network connection. The default is 10 seconds. */
  val readTimeOut: Long = 10,
  /** Write timeout (in seconds) for network connection. The default is 10 seconds. */
  val writeTimeOut: Long = 10,
  /** Compresses requests when uploading to a server that supports gzip. */
  val uploadWithGzip: Boolean = false,
)
