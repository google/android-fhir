/*
 * Copyright 2021 Google LLC
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

/** The builder for [FhirEngine] instance */
class FhirEngineProvider {
  companion object {
    private var fhirServicesInstance: FhirServices? = null
    private var fhirEngineConfiguration: FhirEngineConfiguration = FhirEngineConfiguration()

    /** Initializes the [FhirEngine] singleton with a custom Configuration. */
    @Synchronized
    fun init(fhirEngineConfiguration: FhirEngineConfiguration) {
      this.fhirEngineConfiguration = fhirEngineConfiguration
    }

    /** Resets the [FhirEngine] singleton. Convenience method for tests. */
    @Synchronized
    @JvmStatic
    fun reset() {
      fhirServicesInstance?.database?.close()
      fhirServicesInstance = null
    }

    @Synchronized
    @JvmStatic
    internal fun getDataSource(context: Context): DataSource? {
      return getOrCreateFhirService(context).remoteDataSource
    }

    @Synchronized
    @JvmStatic
    private fun getOrCreateFhirService(context: Context): FhirServices {
      var fhirServices = fhirServicesInstance
      if (fhirServices == null) {
        fhirServices =
          FhirServices.builder(context.applicationContext)
            .apply {
              if (fhirEngineConfiguration.enableEncryptionIfSupported) enableEncryptionIfSupported()
              setDatabaseErrorStrategy(fhirEngineConfiguration.databaseErrorStrategy)
              fhirEngineConfiguration.serverConfiguration?.let { setServerConfiguration(it) }
            }
            .build()
        fhirServicesInstance = fhirServices
      }
      return fhirServices
    }

    /**
     * Returns the cached [FhirEngine] instance. Creates a new instance from the supplied [Context]
     * if it doesn't exist.
     *
     * If this method is called without calling [init], the default [FhirEngineConfiguration] is
     * used.
     */
    @Synchronized
    @JvmStatic
    fun getInstance(context: Context): FhirEngine {
      return getOrCreateFhirService(context).fhirEngine
    }
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
  val serverConfiguration: ServerConfiguration? = null
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
data class ServerConfiguration(val baseUrl: String, val authenticator: Authenticator? = null)
