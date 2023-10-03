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

package com.google.android.fhir.sync.upload.consolidator

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import ca.uhn.fhir.parser.IParser
import com.google.android.fhir.LocalChange
import com.google.android.fhir.LocalChangeToken
import com.google.android.fhir.db.Database
import com.google.android.fhir.db.impl.dao.ReferringResource
import com.google.android.fhir.logicalId
import com.google.android.fhir.sync.upload.request.UrlUploadRequest
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.json.JSONArray
import org.json.JSONObject

/**
 * Implementation of [ResourceConsolidator] for POST mode of creation of resources. The
 * [LocalChange]s are squashed at a resource level and each resource request is uploaded
 * individually using [UrlUploadRequest].
 *
 * Since we know that all the changes will be squashed at a [Resource] level even for the subsequent
 * changes, it is safe to assume that we can update references for only those resources whose
 * references exist in the [ReferenceIndex]. i.e. any stale reference which exists in the
 * [LocalChange] but does not exist in the [ReferenceIndex] will eventually be removed as a result
 * of squashing [LocalChange] at a resource level.
 */
internal class PostPerResourceUrlRequestConsolidator(
  private val database: Database,
) : ResourceConsolidator {

  private val defaultConsolidator: DefaultResourceConsolidator =
    DefaultResourceConsolidator(database)

  private val fhirContext: FhirContext = FhirContext.forCached(FhirVersionEnum.R4)

  private val iParser: IParser = fhirContext.newJsonParser()

  override suspend fun consolidate(localChangeToken: LocalChangeToken, response: Resource) {
    val localChanges = database.getAllLocalChanges(localChangeToken)
    decideConsolidationByChangesType(localChanges, localChangeToken, response)
  }

  private suspend fun decideConsolidationByChangesType(
    localChanges: List<LocalChange>,
    localChangeToken: LocalChangeToken,
    response: Resource,
  ) {
    if (localChanges.first().resourceId != response.logicalId) {
      consolidateForResourceCreation(localChanges, response)
    }
    defaultConsolidator.consolidate(localChangeToken, response)
  }

  private suspend fun consolidateForResourceCreation(
    localChanges: List<LocalChange>,
    updatedResource: Resource,
  ) {
    val createdResourceType = ResourceType.fromCode(localChanges.first().resourceType)
    val createdResourceLocalId = localChanges.first().resourceId
    val createdResourceUpdatedId = updatedResource.logicalId
    val resourceEntity = database.selectEntity(createdResourceType, createdResourceLocalId)

    // update the resource
    database.updateResourceWithUuid(updatedResource, resourceEntity.resourceUuid)

    // update all the local changes for this resource with the new resource Id
    database.updateResourceIdForResourceChanges(
      resourceEntity.resourceUuid,
      createdResourceUpdatedId,
    )

    // consolidate all resource which refer to the newly created resource
    val referringResourceWithReferringPaths =
      database.getAllResourcesReferringToResourceWithPath(
        createdResourceType,
        createdResourceLocalId,
      )
    val updatedReference = "${createdResourceType.name}/$createdResourceUpdatedId"
    referringResourceWithReferringPaths.forEach { referringResource ->
      updateReferencesInResource(
        referringResource,
        referringResource.referenceValue,
        updatedReference,
      )
    }
  }

  private suspend fun updateReferencesInResource(
    referringResource: ReferringResource,
    currentReference: String,
    updatedReference: String,
  ) {
    val resourceWithUpdatedReferences =
      addUpdatedReferenceToResource(
        referringResource.resource,
        referringResource.referenceValue,
        updatedReference,
      )
    database.updateResourceWithUuid(resourceWithUpdatedReferences, referringResource.resourceUuid)

    val referringResourceChanges = database.getLocalChanges(referringResource.resourceUuid)
    val updatedResourceChanges =
      referringResourceChanges.map {
        replaceReferencesInLocalChange(it, currentReference, updatedReference)
      }
    database.replaceResourceChanges(
      referringResource.resourceType,
      referringResource.resourceUuid,
      updatedResourceChanges,
    )
  }

  private fun addUpdatedReferenceToResource(
    resource: Resource,
    outdatedReference: String,
    updatedReference: String,
  ): Resource {
    val resourceJsonObject = JSONObject(iParser.encodeResourceToString(resource))
    val updatedResource = replaceJsonValue(resourceJsonObject, outdatedReference, updatedReference)
    return iParser.parseResource(updatedResource.toString()) as Resource
  }

  private fun replaceReferencesInLocalChange(
    localChange: LocalChange,
    oldReference: String,
    updatedReference: String,
  ): LocalChange {
    return when (localChange.type) {
      LocalChange.Type.INSERT -> {
        val insertResourcePayload = iParser.parseResource(localChange.payload) as Resource
        val updatedResourcePayload =
          addUpdatedReferenceToResource(insertResourcePayload, oldReference, updatedReference)
        return localChange.copy(payload = iParser.encodeResourceToString(updatedResourcePayload))
      }
      LocalChange.Type.UPDATE -> {
        val patchArray = JSONArray(localChange.payload)
        val updatedPatchArray = JSONArray()
        for (i in 0 until patchArray.length()) {
          val updatedPatch =
            replaceJsonValue(patchArray.getJSONObject(i), oldReference, updatedReference)
          updatedPatchArray.put(updatedPatch)
        }
        return localChange.copy(payload = updatedPatchArray.toString())
      }
      LocalChange.Type.DELETE -> localChange
    }
  }

  private fun replaceJsonValue(
    jsonObject: JSONObject,
    currentValue: String,
    newValue: String,
  ): JSONObject {
    val iterator: Iterator<*> = jsonObject.keys()
    var key: String?
    while (iterator.hasNext()) {
      key = iterator.next() as String
      // if object is just string we change value in key
      if (jsonObject.optJSONArray(key) == null && jsonObject.optJSONObject(key) == null) {
        if (jsonObject.optString(key) == currentValue) {
          jsonObject.put(key, newValue)
          return jsonObject
        }
      }

      // if it's jsonobject
      if (jsonObject.optJSONObject(key) != null) {
        replaceJsonValue(jsonObject.getJSONObject(key), currentValue, newValue)
      }

      // if it's jsonarray
      if (jsonObject.optJSONArray(key) != null) {
        val jArray = jsonObject.getJSONArray(key)
        for (i in 0 until jArray.length()) {
          replaceJsonValue(jArray.getJSONObject(i), currentValue, newValue)
        }
      }
    }
    return jsonObject
  }
}
