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

package com.example.sdckmpdemo.sync

import com.google.android.fhir.sync.DownloadWorkManager
import com.google.android.fhir.sync.download.DownloadRequest
import com.google.fhir.model.r4.Bundle
import com.google.fhir.model.r4.Resource
import com.google.fhir.model.r4.terminologies.ResourceType

class DemoDownloadWorkManager : DownloadWorkManager {
  private val urls = ArrayDeque(listOf("Patient?_count=20"))

  override suspend fun getNextRequest(): DownloadRequest? =
    urls.removeFirstOrNull()?.let { DownloadRequest.of(it) }

  override suspend fun getSummaryRequestUrls(): Map<ResourceType, String> =
    mapOf(ResourceType.Patient to "Patient?_summary=count")

  override suspend fun processResponse(response: Resource): Collection<Resource> {
    if (response is Bundle) {
      val nextUrl =
        response.link.firstOrNull { it.relation?.value == "next" }?.url?.value
      if (nextUrl != null) {
        urls.addLast(nextUrl)
      }
      return response.entry.mapNotNull { it.resource }
    }
    return emptyList()
  }
}
