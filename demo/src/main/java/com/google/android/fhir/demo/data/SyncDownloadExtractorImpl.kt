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

package com.google.android.fhir.demo.data

import com.google.android.fhir.sync.SyncDataParams
import com.google.android.fhir.sync.SyncDownloadExtractor
import com.google.android.fhir.sync.concatParams
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.ListResource
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

class SyncDownloadExtractorImpl : SyncDownloadExtractor {
  override fun getInitialUrl(): String {
    val resourceSyncParams = ResourceType.Patient to mapOf("address-city" to "NAIROBI")
    val resourceType = resourceSyncParams.first
    val params = resourceSyncParams.second
    val newParams = params.toMutableMap()

    if (!params.containsKey(SyncDataParams.SORT_KEY)) {
      newParams[SyncDataParams.SORT_KEY] = SyncDataParams.LAST_UPDATED_KEY
    }
    return "${resourceType.name}?${newParams.concatParams()}"
  }

  override fun createDownloadUrl(preProcessUrl: String, lastUpdate: String?): String {
    var downloadUrl = preProcessUrl

    if (lastUpdate != null && downloadUrl.contains("\$everything")) {
      downloadUrl = "$downloadUrl?_since=$lastUpdate"
    }

    if (lastUpdate != null && !downloadUrl.contains("\$everything")) {
      downloadUrl = "$downloadUrl&_lastUpdated=gt$lastUpdate"
    }

    if (downloadUrl.contains("&page_token")) {
      downloadUrl = preProcessUrl
    }

    return downloadUrl
  }

  override fun extractResourcesFromResponse(resourceResponse: Resource): Collection<Resource> {
    var bundleCollection: Collection<Resource> = mutableListOf()

    if (resourceResponse is Bundle && resourceResponse.type == Bundle.BundleType.SEARCHSET) {
      bundleCollection = resourceResponse.entry.map { it.resource }
    }
    return bundleCollection
  }

  override fun extractNextUrlsFromResource(resourceResponse: Resource): Collection<String> {
    val queueWork = mutableListOf<String>()

    if (resourceResponse is ListResource) {
      for (entry in resourceResponse.entry) {
        val patientUrl = "${entry.item.reference}/\$everything"
        queueWork.add(patientUrl)
      }
    }

    if (resourceResponse is Bundle) {
      val nextUrl =
        resourceResponse.link.firstOrNull { component -> component.relation == "next" }?.url
      if (nextUrl != null) {
        queueWork.add(nextUrl)
      }
    }
    return queueWork
  }
}
