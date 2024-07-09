/*
 * Copyright 2023-2024 Google LLC
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
import com.google.android.fhir.db.Database
import com.google.android.fhir.index.SearchParamDefinitionsProvider
import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.sync.FhirDataStore
import com.google.android.fhir.sync.HttpAuthenticator
import com.google.android.fhir.sync.remote.HttpLogger
import java.io.File
import org.hl7.fhir.r4.model.SearchParameter

/**
 * Provides access to the [FhirEngine] instance.
 *
 * Use [init] to initialize the engine with a custom configuration. Otherwise, the default
 * configuration will be used. Call [getInstance] to retrieve the engine instance.
 */
object FhirEngineProvider {
  private var fhirEngineConfiguration: FhirEngineConfiguration? = null
  private var fhirServices: FhirServices? = null

  /**
   * Initializes the [FhirEngine] singleton with a custom [FhirEngineConfiguration]. This must be
   * done only once; we recommend doing this in the `onCreate()` function of your Application class.
   *
   * @throws IllegalStateException if called multiple times.
   * @throws NullPointerException if `FhirEngineConfiguration.context` is null.
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

  @PublishedApi
  @Synchronized
  internal fun getFhirDataStore(context: Context): FhirDataStore {
    return getOrCreateFhirService(context).fhirDataStore
  }

  @Synchronized
  internal fun getFhirDatabase(context: Context): Database {
    return getOrCreateFhirService(context).database
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

  /**
   * Returns the [SearchParamDefinitionsProvider] instance created in [FhirServices] class once the
   * [FhirServices] class has been created, ie, which is when [FhirEngine] instance has been asked
   * for by the application.
   *
   * If this method is called without creating [FhirEngine] instance, this will return null.
   */
  internal fun getSearchParamProvider() = fhirServices?.searchParamProvider

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
 * Configuration for the FHIR Engine, including database setup, error recovery, server connection,
 * and custom search parameters.
 *
 * **Database Encryption:**
 *
 * Database encryption is only available on API 23 (Android 6.0) or above. If
 * `enableEncryptionIfSupported` is true, the FHIR SDK will only enable database encryption on
 * supported devices.
 *
 * **WARNING:** If database encryption is enabled, devices that are on API 22 (Android 5.1) or lower
 * will have an unencrypted database. If those devices are later updated to API 23 or newer, they
 * will encounter an `IllegalStateException`. This is because the database was created without
 * encryption on the older API level, and enabling encryption in the new release will not
 * retroactively encrypt the existing database, causing the app to try to decrypt an unencrypted
 * database.
 *
 * @property enableEncryptionIfSupported Enables database encryption if supported by the device.
 *   Defaults to false.
 * @property databaseErrorStrategy The strategy to handle database errors. Defaults to
 *   [DatabaseErrorStrategy.UNSPECIFIED].
 * @property serverConfiguration Optional configuration for connecting to a remote FHIR server.
 * @property testMode Whether to run the engine in test mode (using an in-memory database). Defaults
 *   to false.
 * @property customSearchParameters Additional search parameters to be used for querying the FHIR
 *   engine with the Search API. These are in addition to the default
 *   [search parameters](https://www.hl7.org/fhir/searchparameter-registry.html) defined in the FHIR
 *   specification. Custom search parameters must be unique and not change existing or default
 *   search parameters.<p> **Note:** The engine does not automatically reindex resources after new
 *   custom search parameters are added. You must manually reindex resources by updating them. Any
 *   new CRUD operations on a resource after adding new search parameters will automatically trigger
 *   reindexing.
 */
data class FhirEngineConfiguration(
  val enableEncryptionIfSupported: Boolean = false,
  val databaseErrorStrategy: DatabaseErrorStrategy = UNSPECIFIED,
  val serverConfiguration: ServerConfiguration? = null,
  val testMode: Boolean = false,
  val customSearchParameters: List<SearchParameter>? = null,
)

/** How database errors should be handled. */
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
  RECREATE_AT_OPEN,
}

/**
 * Configuration for connecting to a remote FHIR server.
 *
 * @property baseUrl The base URL of the remote FHIR server.
 * @property networkConfiguration Configuration for network connection parameters. Defaults to
 *   [NetworkConfiguration].
 * @property authenticator An optional [HttpAuthenticator] for providing HTTP authorization headers.
 * @property httpLogger Logs the communication between the engine and the remote server. Defaults to
 *   [HttpLogger.NONE].
 */
data class ServerConfiguration(
  val baseUrl: String,
  val networkConfiguration: NetworkConfiguration = NetworkConfiguration(),
  val authenticator: HttpAuthenticator? = null,
  val httpLogger: HttpLogger = HttpLogger.NONE,
)

/**
 * Configuration for network connection parameters used when communicating with a remote FHIR
 * server.
 *
 * @property connectionTimeOut Connection timeout in seconds. Defaults to 10 seconds.
 * @property readTimeOut Read timeout in seconds for network connections. Defaults to 10 seconds.
 * @property writeTimeOut Write timeout in seconds for network connections. Defaults to 10 seconds.
 * @property uploadWithGzip Enables compression of requests when uploading to a server that supports
 *   gzip. Defaults to false.
 * @property httpCache Optional [CacheConfiguration] to enable Cache-Control headers for network
 *   requests.
 */
data class NetworkConfiguration(
  val connectionTimeOut: Long = 10,
  val readTimeOut: Long = 10,
  val writeTimeOut: Long = 10,
  val uploadWithGzip: Boolean = false,
  val httpCache: CacheConfiguration? = null,
)

/**
 * Configuration for HTTP caching of network requests.
 *
 * @property cacheDir The directory used for caching, e.g., `File(application.cacheDir,
 *   "http_cache")`.
 * @property maxSize The maximum size of the cache in bits, e.g., `50L * 1024L * 1024L` for 50 MiB.
 */
data class CacheConfiguration(
  val cacheDir: File,
  val maxSize: Long,
)
