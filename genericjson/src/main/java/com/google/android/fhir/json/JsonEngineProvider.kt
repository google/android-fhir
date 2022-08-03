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

package com.google.android.fhir.json

import android.content.Context
import com.google.android.fhir.json.DatabaseErrorStrategy.UNSPECIFIED
import com.google.android.fhir.json.sync.Authenticator
import com.google.android.fhir.json.sync.DataSource

/** The provider for [JsonEngine] instance. */
object JsonEngineProvider {
  private var jsonEngineConfiguration: JsonEngineConfiguration? = null
  private var jsonServices: JsonServices? = null

  /**
   * Initializes the [JsonEngine] singleton with a custom Configuration.
   *
   * This method throws [IllegalStateException] if it is called multiple times
   */
  @Synchronized
  fun init(jsonEngineConfiguration: JsonEngineConfiguration) {
    check(JsonEngineProvider.jsonEngineConfiguration == null) {
      "JsonEngineProvider: JsonEngineConfiguration has already been initialized."
    }
    JsonEngineProvider.jsonEngineConfiguration = jsonEngineConfiguration
  }

  /**
   * Returns the cached [JsonEngine] instance. Creates a new instance from the supplied [Context] if
   * it doesn't exist.
   *
   * If this method is called without calling [init], the default [JsonEngineConfiguration] is used.
   */
  @Synchronized
  fun getInstance(context: Context): JsonEngine {
    return getOrCreateFhirService(context).jsonEngine
  }

  @Synchronized
  @JvmStatic // needed for mockito
  internal fun getDataSource(context: Context): DataSource? {
    return getOrCreateFhirService(context).remoteDataSource
  }

  @Synchronized
  private fun getOrCreateFhirService(context: Context): JsonServices {
    if (jsonServices == null) {
      jsonEngineConfiguration = jsonEngineConfiguration ?: JsonEngineConfiguration()
      val configuration = checkNotNull(jsonEngineConfiguration)
      jsonServices =
        JsonServices.builder(context.applicationContext)
          .apply {
            if (configuration.enableEncryptionIfSupported) enableEncryptionIfSupported()
            setDatabaseErrorStrategy(configuration.databaseErrorStrategy)
            configuration.serverConfiguration?.let { setServerConfiguration(it) }
            if (configuration.testMode) {
              inMemory()
            }
          }
          .build()
    }
    return checkNotNull(jsonServices)
  }

  @Synchronized
  fun cleanup() {
    check(jsonEngineConfiguration?.testMode == true) {
      "JsonEngineProvider: JsonEngineProvider needs to be in the test mode to perform cleanup."
    }
    forceCleanup()
  }

  internal fun forceCleanup() {
    jsonServices?.database?.close()
    jsonServices = null
    jsonEngineConfiguration = null
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
data class JsonEngineConfiguration(
  val enableEncryptionIfSupported: Boolean = false,
  val databaseErrorStrategy: DatabaseErrorStrategy = UNSPECIFIED,
  val serverConfiguration: ServerConfiguration? = null,
  val testMode: Boolean = false
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

/**
 * A configuration to provide the remote FHIR server url and an [Authenticator] for supplying any
 * auth token that may be necessary to communicate with the server.
 */
data class ServerConfiguration(
  val baseUrl: String,
  val networkConfiguration: NetworkConfiguration = NetworkConfiguration(),
  val authenticator: Authenticator? = null
)

/** A configuration to provide the network connection parameters. */
data class NetworkConfiguration(
  /** Connection timeout (in seconds). The default is 10 seconds. */
  val connectionTimeOut: Long = 10,
  /** Read timeout (in seconds) for network connection. The default is 10 seconds. */
  val readTimeOut: Long = 10,
  /** Write timeout (in seconds) for network connection. The default is 10 seconds. */
  val writeTimeOut: Long = 10
)
