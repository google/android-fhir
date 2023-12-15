/*
 * Copyright 2023 Google LLC
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

package com.google.android.fhir.sync.upload

import com.google.android.fhir.LocalChangeToken
import com.google.android.fhir.db.Database
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.DomainResource
import org.hl7.fhir.r4.model.ResourceType

/**
 * Represents a mechanism to consolidate resources after they are uploaded.
 *
 * INTERNAL ONLY. This interface should NEVER been exposed as an external API because it works
 * together with other components in the upload package to fulfill a specific upload strategy. After
 * a resource is uploaded to a remote FHIR server and a response is returned, we need to consolidate
 * any changes in the database, Examples of this would be, updating the lastUpdated timestamp field,
 * or deleting the local change from the database, or updating the resource IDs and payloads to
 * correspond with the server’s feedback.
 */
internal fun interface ResourceConsolidator {

  /** Consolidates the local change token with the provided response from the FHIR server. */
  suspend fun consolidate(uploadSyncResult: UploadSyncResult)
}

/** Default implementation of [ResourceConsolidator] that uses the database to aid consolidation. */
internal class DefaultResourceConsolidator(private val database: Database) : ResourceConsolidator {

  override suspend fun consolidate(uploadSyncResult: UploadSyncResult) =
    when (uploadSyncResult) {
      is UploadSyncResult.Success -> {
        database.deleteUpdates(
          LocalChangeToken(
            uploadSyncResult.uploadResponses.flatMap {
              it.localChanges.flatMap { localChange -> localChange.token.ids }
            },
          ),
        )
        uploadSyncResult.uploadResponses.forEach {
          when (it) {
            is BundleComponentUploadResponseMapping -> updateVersionIdAndLastUpdated(it.output)
            is ResourceUploadResponseMapping -> updateVersionIdAndLastUpdated(it.output)
          }
        }
      }
      is UploadSyncResult.Failure -> {
        /* For now, do nothing (we do not delete the local changes from the database as they were
        not uploaded successfully. In the future, add consolidation required if upload fails.
         */
      }
    }

  private suspend fun updateVersionIdAndLastUpdated(response: Bundle.BundleEntryResponseComponent) {
    if (response.hasEtag() && response.hasLastModified() && response.hasLocation()) {
      response.resourceIdAndType?.let { (id, type) ->
        database.updateVersionIdAndLastUpdated(
          id,
          type,
          getVersionFromETag(response.etag),
          response.lastModified.toInstant(),
        )
      }
    }
  }

  private suspend fun updateVersionIdAndLastUpdated(resource: DomainResource) {
    if (resource.hasMeta() && resource.meta.hasVersionId() && resource.meta.hasLastUpdated()) {
      database.updateVersionIdAndLastUpdated(
        resource.id,
        resource.resourceType,
        resource.meta.versionId,
        resource.meta.lastUpdated.toInstant(),
      )
    }
  }

  /**
   * FHIR uses weak ETag that look something like W/"MTY4NDMyODE2OTg3NDUyNTAwMA", so we need to
   * extract version from it. See https://hl7.org/fhir/http.html#Http-Headers.
   */
  private fun getVersionFromETag(eTag: String) =
    // The server should always return a weak etag that starts with W, but if it server returns a
    // strong tag, we store it as-is. The http-headers for conditional upload like if-match will
    // always add value as a weak tag.
    if (eTag.startsWith("W/")) {
      eTag.split("\"")[1]
    } else {
      eTag
    }

  /**
   * May return a Pair of versionId and resource type extracted from the
   * [Bundle.BundleEntryResponseComponent.location].
   *
   * [Bundle.BundleEntryResponseComponent.location] may be:
   * 1. absolute path: `<server-path>/<resource-type>/<resource-id>/_history/<version>`
   * 2. relative path: `<resource-type>/<resource-id>/_history/<version>`
   */
  private val Bundle.BundleEntryResponseComponent.resourceIdAndType: Pair<String, ResourceType>?
    get() =
      location
        ?.split("/")
        ?.takeIf { it.size > 3 }
        ?.let { it[it.size - 3] to ResourceType.fromCode(it[it.size - 4]) }
}
