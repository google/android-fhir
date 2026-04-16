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

package com.google.android.fhir.sync.remote

import co.touchlab.kermit.Logger as KermitLogger
import com.google.android.fhir.NetworkConfiguration
import com.google.android.fhir.sync.HttpAuthenticator
import com.google.fhir.model.r4.FhirR4Json
import com.google.fhir.model.r4.Resource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

/** Ktor implementation of the [FhirHttpService]. */
internal class KtorHttpService(
  private val client: HttpClient,
  private val fhirJson: FhirR4Json = FhirR4Json(),
) : FhirHttpService {

  /**
   * Sanitizes JSON to work around bugs in the kotlin-fhir library (fhir-model beta):
   * 1. Truncates DateTime values in date-only fields (FhirDate.fromString() crash)
   * 2. Strips "text" (Narrative) fields that cause NPE when status/div is missing
   */
  private fun sanitizeJson(json: String): String {
    val sanitized = json.replace(dateFieldWithTimeRegex) { match ->
      "\"${match.groupValues[1]}\" : \"${match.groupValues[2]}\""
    }
    return try {
      val element = lenientJson.parseToJsonElement(sanitized)
      lenientJson.encodeToString(JsonElement.serializer(), stripNarrativeText(element))
    } catch (_: Exception) {
      sanitized
    }
  }

  private fun stripNarrativeText(element: JsonElement): JsonElement =
    when (element) {
      is JsonObject -> JsonObject(
        element.jsonObject
          .filterKeys { it != "text" || !looksLikeNarrative(element[it]) }
          .mapValues { (_, v) -> stripNarrativeText(v) }
      )
      is kotlinx.serialization.json.JsonArray -> kotlinx.serialization.json.JsonArray(
        element.jsonArray.map { stripNarrativeText(it) }
      )
      else -> element
    }

  private fun looksLikeNarrative(element: JsonElement?): Boolean =
    element is JsonObject && element.containsKey("div")

  override suspend fun get(path: String, headers: Map<String, String>): Resource {
    val json: String =
      client.get(path) { headers { headers.forEach { (k, v) -> append(k, v) } } }.body()
    return fhirJson.decodeFromString(sanitizeJson(json)) as Resource
  }

  override suspend fun post(
    path: String,
    resource: Resource,
    headers: Map<String, String>,
  ): Resource {
    val json: String =
      client
        .post(path) {
          contentType(ContentType.Application.Json)
          headers { headers.forEach { (k, v) -> append(k, v) } }
          setBody(fhirJson.encodeToString(resource))
        }
        .body()
    return fhirJson.decodeFromString(sanitizeJson(json)) as Resource
  }

  override suspend fun put(
    path: String,
    resource: Resource,
    headers: Map<String, String>,
  ): Resource {
    val json: String =
      client
        .put(path) {
          contentType(ContentType.Application.Json)
          headers { headers.forEach { (k, v) -> append(k, v) } }
          setBody(fhirJson.encodeToString(resource))
        }
        .body()
    return fhirJson.decodeFromString(sanitizeJson(json)) as Resource
  }

  override suspend fun patch(
    path: String,
    patchDocument: JsonArray,
    headers: Map<String, String>,
  ): Resource {
    val json: String =
      client
        .patch(path) {
          contentType(ContentType.parse("application/json-patch+json"))
          headers { headers.forEach { (k, v) -> append(k, v) } }
          setBody(patchDocument.toString())
        }
        .body()
    return fhirJson.decodeFromString(sanitizeJson(json)) as Resource
  }

  override suspend fun delete(path: String, headers: Map<String, String>): Resource {
    val json: String =
      client.delete(path) { headers { headers.forEach { (k, v) -> append(k, v) } } }.body()
    return fhirJson.decodeFromString(sanitizeJson(json)) as Resource
  }

  companion object {
    private val lenientJson = Json { ignoreUnknownKeys = true; isLenient = true }

    /** Matches FHIR date-only fields that incorrectly contain DateTime values. */
    private val dateFieldWithTimeRegex =
      Regex(""""(birthDate|deceasedDate)"\s*:\s*"(\d{4}-\d{2}-\d{2})T[^"]*"""")

    fun builder(baseUrl: String, networkConfiguration: NetworkConfiguration) =
      Builder(baseUrl, networkConfiguration)
  }

  class Builder(
    private val baseUrl: String,
    private val networkConfiguration: NetworkConfiguration,
  ) {
    private var authenticator: HttpAuthenticator? = null
    private var httpLogger: HttpLogger? = null

    fun setAuthenticator(authenticator: HttpAuthenticator?) = apply {
      this.authenticator = authenticator
    }

    fun setHttpLogger(httpLogger: HttpLogger) = apply { this.httpLogger = httpLogger }

    fun build(): KtorHttpService {
      val client = HttpClient {
        install(HttpTimeout) {
          connectTimeoutMillis = networkConfiguration.connectionTimeOut * 1000
          requestTimeoutMillis = networkConfiguration.readTimeOut * 1000
          socketTimeoutMillis = networkConfiguration.writeTimeOut * 1000
        }

        if (networkConfiguration.uploadWithGzip) {
          install(ContentEncoding) { gzip() }
        }

        if (networkConfiguration.httpCache != null) {
          install(HttpCache)
        }

        install(DefaultRequest) {
          url(baseUrl)
          authenticator?.let {
            headers {
              val authMethod = it.getAuthenticationMethod()
              append(HttpHeaders.Authorization, authMethod.getAuthorizationHeader())
            }
          }
        }

        httpLogger?.let { loggerConfig ->
          install(Logging) {
            level =
              when (loggerConfig.level) {
                HttpLogger.Level.NONE -> LogLevel.NONE
                HttpLogger.Level.BASIC -> LogLevel.INFO
                HttpLogger.Level.HEADERS -> LogLevel.HEADERS
                HttpLogger.Level.BODY -> LogLevel.ALL
              }
            logger =
              object : io.ktor.client.plugins.logging.Logger {
                override fun log(message: String) {
                  KermitLogger.v { message }
                }
              }
            sanitizeHeader { header -> loggerConfig.headersToIgnore.contains(header) }
          }
        }
      }
      return KtorHttpService(client)
    }
  }
}
