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
import com.google.android.fhir.db.impl.DatabaseConfiguration
import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.sync.FhirDataStore
import com.google.android.fhir.sync.HttpAuthenticator
import com.google.android.fhir.sync.remote.HttpLogger
import com.google.android.fhir.sync.upload.UploadStrategy
import java.io.File

/** The provider for [FhirEngine] instance. */
object FhirEngineProvider {
  private var fhirEngineConfiguration: FhirEngineConfiguration? = null
  private var fhirServices: FhirServices? = null

  /**
   * Initializes the [FhirEngine] singleton with a custom Configuration.
   *
   * This method throws [IllegalStateException] if it is called multiple times. It throws
   * [NullPointerException] if [FhirEngineConfiguration.context] is null.
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
  private fun getOrCreateFhirService(context: Context): FhirServices {
    if (fhirServices == null) {
      fhirEngineConfiguration = fhirEngineConfiguration ?: FhirEngineConfiguration()
      val configuration = checkNotNull(fhirEngineConfiguration)
      fhirServices =
        FhirServices.builder(context.applicationContext)
          .apply {
            setDatabaseConfiguration(configuration.databaseConfiguration)
            if (configuration.syncConfiguration != null) {
              setSyncConfiguration(configuration.syncConfiguration)
            }
          }
          .build()
    }
    return checkNotNull(fhirServices)
  }

  @Synchronized
  fun cleanup() {
    check(fhirEngineConfiguration?.databaseConfiguration?.inMemory == true) {
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
  val syncConfiguration: SyncConfiguration? = null,
  val databaseConfiguration: DatabaseConfiguration = DatabaseConfiguration(),
)

data class SyncConfiguration(
  val uploadStrategy: UploadStrategy = UploadStrategy.AllChangesSquashedBundlePut,
  val serverConfiguration: ServerConfiguration = ServerConfiguration(),
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
  RECREATE_AT_OPEN,
}

/** A configuration to provide necessary params for network connection. */
data class ServerConfiguration(
  /** Url of the remote FHIR server. */
  val baseUrl: String = "",
  /** A configuration to provide the network connection parameters. */
  val networkConfiguration: NetworkConfiguration = NetworkConfiguration(),
  /** An [HttpAuthenticator] for providing HTTP authorization header. */
  val authenticator: HttpAuthenticator? = null,
  /** Logs the communication between the engine and the remote server. */
  val httpLogger: HttpLogger = HttpLogger.NONE,
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
  /** Cache setting to enable Cache-Control Header */
  val httpCache: CacheConfiguration? = null,
)

/** Cache configuration wrapper */
data class CacheConfiguration(
  /** Cache directory eg: File(application.cacheDir, "http_cache") */
  val cacheDir: File,
  /** Cache size in bits eg: 50L * 1024L * 1024L // 50 MiB */
  val maxSize: Long,
)
