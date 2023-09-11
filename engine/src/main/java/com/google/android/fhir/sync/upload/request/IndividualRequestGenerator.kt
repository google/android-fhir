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

package com.google.android.fhir.sync.upload.request

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.ContentTypes
import com.google.android.fhir.LocalChange
import com.google.android.fhir.LocalChange.Type
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.sync.UrlUploadRequest
import org.hl7.fhir.r4.model.Binary
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.codesystems.HttpVerb

/** Generates list of [UrlUploadRequest]s with for each [LocalChange] given. */
class IndividualRequestGenerator(
  private val getIndividualRequestForLocalChangeType:
    (type: Type, localChange: LocalChange) -> UrlUploadRequest
) : UploadRequestGenerator {

  override fun generateUploadRequests(localChanges: List<LocalChange>): List<UrlUploadRequest> =
    localChanges.map { getIndividualRequestForLocalChangeType(it.type, it) }

  companion object Factory {

    private val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

    private val createMapping =
      mapOf(
        HttpVerb.POST to this::postForCreateResource,
        HttpVerb.PUT to this::putForCreateResource,
      )

    private val updateMapping =
      mapOf(
        HttpVerb.PATCH to this::patchForUpdateResource,
      )

    fun getDefault() = getGenerator(HttpVerb.PUT, HttpVerb.PATCH)

    /**
     * Returns a [IndividualRequestGenerator] based on the provided [HttpVerb]s for creating and
     * updating resources. The function may throw an [IllegalArgumentException] if the provided
     * [HttpVerb]s are not supported.
     */
    fun getGenerator(
      httpVerbToUseForCreate: HttpVerb,
      httpVerbToUseForUpdate: HttpVerb
    ): IndividualRequestGenerator {

      val createFunction =
        createMapping[httpVerbToUseForCreate]
          ?: throw IllegalArgumentException(
            "Creation using $httpVerbToUseForCreate is not supported."
          )

      val updateFunction =
        updateMapping[httpVerbToUseForUpdate]
          ?: throw IllegalArgumentException(
            "Update using $httpVerbToUseForUpdate is not supported."
          )

      return IndividualRequestGenerator { type, localChange ->
        when (type) {
          Type.INSERT -> createFunction(localChange)
          Type.UPDATE -> updateFunction(localChange)
          Type.DELETE -> deleteFunction(localChange)
          Type.NO_OP -> error("Cannot create a request from a NO_OP type")
        }
      }
    }

    private fun deleteFunction(localChange: LocalChange) =
      UrlUploadRequest(
        httpVerb = HttpVerb.DELETE,
        url = "${localChange.resourceType}/${localChange.resourceId}",
        resource = parser.parseResource(localChange.payload) as Resource,
        localChangeToken = LocalChangeToken(localChange.token.ids),
      )

    private fun postForCreateResource(localChange: LocalChange) =
      UrlUploadRequest(
        httpVerb = HttpVerb.POST,
        url = localChange.resourceType,
        resource = parser.parseResource(localChange.payload) as Resource,
        localChangeToken = LocalChangeToken(localChange.token.ids),
      )

    private fun putForCreateResource(localChange: LocalChange) =
      UrlUploadRequest(
        httpVerb = HttpVerb.PUT,
        url = "${localChange.resourceType}/${localChange.resourceId}",
        resource = parser.parseResource(localChange.payload) as Resource,
        localChangeToken = LocalChangeToken(localChange.token.ids),
      )

    private fun patchForUpdateResource(localChange: LocalChange) =
      UrlUploadRequest(
        httpVerb = HttpVerb.PATCH,
        url = "${localChange.resourceType}/${localChange.resourceId}",
        resource = Binary().apply { data = localChange.payload.toByteArray() },
        localChangeToken = LocalChangeToken(localChange.token.ids),
        headers = mapOf("Content-Type" to ContentTypes.APPLICATION_JSON_PATCH)
      )
  }
}
