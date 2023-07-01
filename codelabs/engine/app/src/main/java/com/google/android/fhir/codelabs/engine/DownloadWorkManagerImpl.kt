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

package com.google.android.fhir.codelabs.engine

import com.google.android.fhir.sync.DownloadWorkManager
import com.google.android.fhir.sync.Request
import java.util.LinkedList
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

class DownloadWorkManagerImpl : DownloadWorkManager {
  private val urls = LinkedList(listOf("Patient"))

  override suspend fun getNextRequest(): Request? {
    val url = urls.poll() ?: return null
    return Request.of(url)
  }

  override suspend fun getSummaryRequestUrls() = mapOf<ResourceType, String>()

  override suspend fun processResponse(response: Resource): Collection<Resource> {
    var bundleCollection: Collection<Resource> = mutableListOf()
    if (response is Bundle && response.type == Bundle.BundleType.SEARCHSET) {
      bundleCollection = response.entry.map { it.resource }
    }
    return bundleCollection
  }
}
