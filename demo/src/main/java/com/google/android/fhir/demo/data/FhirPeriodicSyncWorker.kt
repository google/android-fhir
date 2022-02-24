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

import android.content.Context
import androidx.work.WorkerParameters
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.google.android.fhir.demo.FhirApplication
import com.google.android.fhir.demo.api.HapiFhirService
import com.google.android.fhir.sync.FhirSyncWorker
import com.google.android.fhir.sync.SyncDataParams
import com.google.android.fhir.sync.concatParams
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.ListResource
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

class FhirPeriodicSyncWorker(appContext: Context, workerParams: WorkerParameters) :
  FhirSyncWorker(appContext, workerParams) {

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

  override fun getCreateDownloadUrl(): (String, String?) -> String = { preprocess, lastUpdate ->
    var downloadUrl = preprocess

    if (lastUpdate != null && downloadUrl.contains("\$everything")) {
      downloadUrl = "$downloadUrl?_since=$lastUpdate"
    }

    if (lastUpdate != null && !downloadUrl.contains("\$everything")) {
      downloadUrl = "$downloadUrl&_lastUpdated=gt$lastUpdate"
    }

    downloadUrl
  }

  override fun getExtractResourcesFromResponse(): (Resource) -> Collection<Resource> = { it ->
    var bundleCollection: Collection<Resource> = mutableListOf()

    if (it is Bundle && it.type == Bundle.BundleType.SEARCHSET) {
      bundleCollection = it.entry.map { it.resource }
    }
    bundleCollection
  }

  override fun getExtractNextUrlsFromResource(): (Resource) -> Collection<String> = {
    val queueWork = mutableListOf<String>()

    if (it is ListResource) {
      for (entry in it.entry) {
        val patientUrl = "${entry.item.reference}/\$everything"
        queueWork.add(patientUrl)
      }
    }

    if (it is Bundle) {
      val nextUrl = it.link.firstOrNull { component -> component.relation == "next" }?.url
      if (nextUrl != null) {
        queueWork.add(nextUrl)
      }
    }
    queueWork
  }

  override fun getDataSource() =
    HapiFhirResourceDataSource(
      HapiFhirService.create(FhirContext.forCached(FhirVersionEnum.R4).newJsonParser())
    )

  override fun getFhirEngine() = FhirApplication.fhirEngine(applicationContext)
}
