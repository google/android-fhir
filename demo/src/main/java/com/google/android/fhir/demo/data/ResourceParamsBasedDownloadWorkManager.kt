package com.google.android.fhir.demo.data

import com.google.android.fhir.ResourceType
import com.google.android.fhir.SyncDownloadContext
import com.google.android.fhir.sync.DownloadWorkManager
import com.google.android.fhir.sync.ParamMap
import com.google.android.fhir.sync.SyncDataParams
import com.google.android.fhir.sync.concatParams
import java.util.LinkedList
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.instance.model.api.IAnyResource
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.OperationOutcome

typealias ResourceSearchParams = Map<ResourceType, ParamMap>

/**
 * [DownloadWorkManager] implementation based on the provided [ResourceSearchParams] to generate
 * [Resource] search queries and parse [Bundle.BundleType.SEARCHSET] type [Bundle]. This
 * implementation takes a DFS approach and downloads all available resources for a particular
 * [ResourceType] before moving on to the next [ResourceType].
 */
class ResourceParamsBasedDownloadWorkManager(syncParams: ResourceSearchParams) :
  DownloadWorkManager {
  private val resourcesToDownloadWithSearchParams = LinkedList(syncParams.entries)
  private val urlOfTheNextPagesToDownloadForAResource = LinkedList<String>()

  override suspend fun getNextRequestUrl(context: SyncDownloadContext): String? {
    if (urlOfTheNextPagesToDownloadForAResource.isNotEmpty())
      return urlOfTheNextPagesToDownloadForAResource.poll()

    return resourcesToDownloadWithSearchParams.poll()?.let { (resourceType, params) ->
      val newParams = params.toMutableMap()
      if (!params.containsKey(SyncDataParams.SORT_KEY)) {
        newParams[SyncDataParams.SORT_KEY] = SyncDataParams.LAST_UPDATED_KEY
      }
      if (!params.containsKey(SyncDataParams.LAST_UPDATED_KEY)) {
        val lastUpdate = context.getLatestTimestampFor(resourceType)
        if (!lastUpdate.isNullOrEmpty()) {
          newParams[SyncDataParams.LAST_UPDATED_KEY] = "gt$lastUpdate"
        }
      }

      "${resourceType.name}?${newParams.concatParams()}"
    }
  }

  override suspend fun processResponse(response: IAnyResource): Collection<IAnyResource> {
    if (response is OperationOutcome) {
      throw FHIRException(response.issueFirstRep.diagnostics)
    }

    return if (response is Bundle && response.type == Bundle.BundleType.SEARCHSET) {
      response.link
        .firstOrNull { component -> component.relation == "next" }
        ?.url?.let { next -> urlOfTheNextPagesToDownloadForAResource.add(next) }

      response.entry.map { it.resource }
    } else {
      emptyList()
    }
  }
}