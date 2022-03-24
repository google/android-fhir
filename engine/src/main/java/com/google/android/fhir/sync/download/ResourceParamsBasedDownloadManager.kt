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

package com.google.android.fhir.sync.download

import android.net.Uri
import com.google.android.fhir.sync.DownloadManager
import com.google.android.fhir.sync.ParamMap
import com.google.android.fhir.sync.ResourceSyncParams
import com.google.android.fhir.sync.SyncDataParams
import com.google.android.fhir.sync.concatParams
import java.util.LinkedList
import java.util.Queue
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/**
 * [DownloadManager] implementation based on the provided [ResourceSyncParams] to generate
 * [Resource] search queries and parse [Bundle.BundleType.SEARCHSET] type [Bundle].
 */
class ResourceParamsBasedDownloadManager(syncParams: ResourceSyncParams) : DownloadManager {
  private val queue: Queue<Map.Entry<ResourceType, ParamMap>> = LinkedList(syncParams.entries)

  override fun getInitialUrl(): String {
    return getNextDownloadUrl()
      ?: throw IllegalStateException("No resource sync params provided to create the url")
  }

  override fun createDownloadUrl(preProcessUrl: String, lastUpdate: String?): String {
    // Do not modify any URL if its an absolute one
    if (Uri.parse(preProcessUrl).isAbsolute) {
      return preProcessUrl
    }
    return if (lastUpdate.isNullOrEmpty()) {
      preProcessUrl
    } else {
      "$preProcessUrl&_lastUpdated=gt$lastUpdate"
    }
  }

  override fun extractResourcesFromResponse(resourceResponse: Resource): Collection<Resource> {
    if (resourceResponse is OperationOutcome) {
      throw FHIRException(resourceResponse.issueFirstRep.diagnostics)
    }
    return if (resourceResponse is Bundle && resourceResponse.type == Bundle.BundleType.SEARCHSET) {
      resourceResponse.entry.map { it.resource }
    } else {
      emptyList()
    }
  }

  override fun extractNextUrlsFromResource(resourceResponse: Resource): Collection<String> {
    // Once we have downloaded all the resources for a particular ResourceType, lets move on to the
    // next ResourceType supplied by the user.
    if (resourceResponse is Bundle && resourceResponse.type == Bundle.BundleType.SEARCHSET) {
      val next =
        resourceResponse.link.firstOrNull { component -> component.relation == "next" }?.url
      if (!next.isNullOrEmpty()) return listOf(next)
    }
    return getNextDownloadUrl()?.let { listOf(it) } ?: emptyList()
  }

  private fun getNextDownloadUrl(): String? {
    return queue.peek()?.let { (resourceType, params) ->
      queue.poll()
      val newParams = params.toMutableMap()
      if (!params.containsKey(SyncDataParams.SORT_KEY)) {
        newParams[SyncDataParams.SORT_KEY] = SyncDataParams.LAST_UPDATED_KEY
      }
      "${resourceType.name}?${newParams.concatParams()}"
    }
  }
}
