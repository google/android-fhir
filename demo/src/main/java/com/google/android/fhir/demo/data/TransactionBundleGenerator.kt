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

package com.google.android.fhir.demo.data

import com.google.android.fhir.LocalChange
import com.google.android.fhir.LocalChange.Type
import com.google.android.fhir.ResourceForDatabaseToSave
import com.google.android.fhir.ResourceType
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.resourceType
import com.google.android.fhir.sync.ResourceBundleAndAssociatedLocalChangeTokens
import com.google.android.fhir.sync.UploadWorkManager
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.instance.model.api.IAnyResource
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Resource

/**
 * Generates pairs of Transaction [Bundle] and [LocalChangeToken]s associated with the resources
 * present in the transaction bundle.
 */
open class TransactionBundleGenerator(
  val getBundleEntryComponentGeneratorForLocalChangeType:
    (type: Type) -> HttpVerbBasedBundleEntryComponentGenerator
) : UploadWorkManager {

  override fun generate(
    localChanges: List<List<LocalChange>>
  ): List<ResourceBundleAndAssociatedLocalChangeTokens> {
    return localChanges.filter { it.isNotEmpty() }.map { generateBundle(it) }
  }

  private fun generateBundle(
    localChanges: List<LocalChange>
  ): ResourceBundleAndAssociatedLocalChangeTokens {
    return Bundle().apply {
      type = Bundle.BundleType.TRANSACTION
      localChanges.forEach {
        this.addEntry(getBundleEntryComponentGeneratorForLocalChangeType(it.type).getEntry(it))
      }
    } to localChanges.map { it.token }
  }

  override fun getUploadResult(response: IAnyResource, localChangeTokens: List<LocalChangeToken>) =
    when {
      response is Bundle && response.type == Bundle.BundleType.TRANSACTIONRESPONSE -> {
        val listOfResourcesToSave = mutableListOf<ResourceForDatabaseToSave>()
        response.entry.forEach {
          when {
            it.hasResource() ->
              updateVersionIdAndLastUpdated(it.resource)?.let { resourceToSave ->
                listOfResourcesToSave.add(resourceToSave)
              }
            it.hasResponse() ->
              updateVersionIdAndLastUpdated(it.response)?.let { resourceToSave ->
                listOfResourcesToSave.add(resourceToSave)
              }
          }
        }
        LocalChangeToken(localChangeTokens.flatMap { it.ids }) to listOfResourcesToSave
      }
      response is OperationOutcome && response.issue.isNotEmpty() -> {
        throw FHIRException(response.issueFirstRep.diagnostics)
      }
      else -> {
        throw FHIRException("Unknown response for ${response.resourceType}")
      }
    }

  private fun updateVersionIdAndLastUpdated(resource: Resource): ResourceForDatabaseToSave? {
    if (resource.hasMeta() && resource.meta.hasVersionId() && resource.meta.hasLastUpdated()) {
      return ResourceForDatabaseToSave(
        resource.id,
        ResourceType.fromCode(resource.fhirType()),
        resource.meta.versionId,
        resource.meta.lastUpdated.toInstant()
      )
    }
    return null
  }

  private fun updateVersionIdAndLastUpdated(
    response: Bundle.BundleEntryResponseComponent
  ): ResourceForDatabaseToSave? {
    if (response.hasEtag() && response.hasLastModified() && response.hasLocation()) {
      return response.resourceIdAndType?.let { (id, type) ->
        ResourceForDatabaseToSave(id, type, response.etag, response.lastModified.toInstant())
      }
    }
    return null
  }

  /**
   * May return a Pair of versionId and resource type extracted from the
   * [Bundle.BundleEntryResponseComponent.location].
   *
   * [Bundle.BundleEntryResponseComponent.location] may be:
   *
   * 1. absolute path: `<server-path>/<resource-type>/<resource-id>/_history/<version>`
   *
   * 2. relative path: `<resource-type>/<resource-id>/_history/<version>`
   */
  private val Bundle.BundleEntryResponseComponent.resourceIdAndType: Pair<String, ResourceType>?
    get() =
      location
        ?.split("/")
        ?.takeIf { it.size > 3 }
        ?.let { it[it.size - 3] to ResourceType.fromCode(it[it.size - 4]) }

  companion object Factory {

    fun getDefault() = PutForCreateAndPatchForUpdateBasedTransactionGenerator

    /**
     * Returns a [TransactionBundleGenerator] based on the provided [Bundle.HTTPVerb]s for creating
     * and updating resources. The function may throw an [IllegalArgumentException] if the provided
     * [Bundle.HTTPVerb]s are not supported.
     */
    fun getGenerator(
      httpVerbToUseForCreate: Bundle.HTTPVerb,
      httpVerbToUseForUpdate: Bundle.HTTPVerb
    ): TransactionBundleGenerator {

      return if (httpVerbToUseForCreate == Bundle.HTTPVerb.PUT &&
          httpVerbToUseForUpdate == Bundle.HTTPVerb.PATCH
      ) {
        PutForCreateAndPatchForUpdateBasedTransactionGenerator
      } else {
        throw IllegalArgumentException(
          "Engine currently supports creation using [PUT] and updates using [PATCH]"
        )
      }
    }
  }
}

object PutForCreateAndPatchForUpdateBasedTransactionGenerator :
  TransactionBundleGenerator({ type ->
    when (type) {
      Type.INSERT -> HttpPutForCreateEntryComponentGenerator
      Type.UPDATE -> HttpPatchForUpdateEntryComponentGenerator
      Type.DELETE -> HttpDeleteEntryComponentGenerator
    }
  })
