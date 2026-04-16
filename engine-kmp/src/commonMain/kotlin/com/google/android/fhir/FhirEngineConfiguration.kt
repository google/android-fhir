/*
 * Copyright 2025-2026 Google LLC
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

import com.google.android.fhir.index.SearchParamDefinition
import com.google.android.fhir.sync.HttpAuthenticator
import com.google.android.fhir.sync.remote.HttpLogger

/**
 * Configuration for the FHIR Engine, including database setup, error recovery, server connection,
 * and custom search parameters.
 *
 * @property enableEncryptionIfSupported Enables database encryption if supported by the platform.
 *   Defaults to false.
 * @property databaseErrorStrategy The strategy to handle database errors. Defaults to
 *   [DatabaseErrorStrategy.UNSPECIFIED].
 * @property serverConfiguration Optional configuration for connecting to a remote FHIR server.
 * @property testMode Whether to run the engine in test mode (using an in-memory database). Defaults
 *   to false.
 * @property customSearchParameters Additional search parameters to be used for querying the FHIR
 *   engine with the Search API. These are in addition to the default search parameters defined in
 *   the FHIR specification. Custom search parameters must be unique and not change existing or
 *   default search parameters.
 */
data class FhirEngineConfiguration(
  val enableEncryptionIfSupported: Boolean = false,
  val databaseErrorStrategy: DatabaseErrorStrategy = DatabaseErrorStrategy.UNSPECIFIED,
  val serverConfiguration: ServerConfiguration? = null,
  val testMode: Boolean = false,
  val customSearchParameters: List<SearchParamDefinition>? = null,
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
 * @property cacheDir The directory path used for caching (platform-agnostic string path).
 * @property maxSize The maximum size of the cache in bits, e.g., `50L * 1024L * 1024L` for 50 MiB.
 */
data class CacheConfiguration(
  val cacheDir: String,
  val maxSize: Long,
)
