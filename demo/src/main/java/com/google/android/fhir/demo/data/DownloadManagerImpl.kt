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

import com.google.android.fhir.sync.DownloadManager
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.ListResource
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Reference
import org.hl7.fhir.r4.model.Resource

class DownloadManagerImpl : DownloadManager {
  override fun getInitialUrl(): String {
    return "Patient?address-city=NAIROBI"
  }

  override fun createDownloadUrl(preProcessUrl: String, lastUpdate: String?): String {
    var downloadUrl = preProcessUrl

    // Affix lastUpdate to a $everything query using _since as per:
    // https://hl7.org/fhir/operation-patient-everything.html
    if (lastUpdate != null && downloadUrl.contains("\$everything")) {
      downloadUrl = "$downloadUrl?_since=$lastUpdate"
    }

    // Affix lastUpdate to non-$everything queries as per:
    // https://hl7.org/fhir/operation-patient-everything.html
    if (lastUpdate != null && !downloadUrl.contains("\$everything")) {
      downloadUrl = "$downloadUrl&_lastUpdated=gt$lastUpdate"
    }

    // Do not modify any URL set by a server that specifies the token of the page to return.
    if (downloadUrl.contains("&page_token")) {
      downloadUrl = preProcessUrl
    }

    return downloadUrl
  }

  override fun extractResourcesFromResponse(resourceResponse: Resource): Collection<Resource> {
    // As per FHIR documentation :
    // If the search fails (cannot be executed, not that there are no matches), the
    // return value SHALL be a status code 4xx or 5xx with an OperationOutcome.
    // See https://www.hl7.org/fhir/http.html#search for more details.
    if (resourceResponse is OperationOutcome) {
      throw FHIRException(resourceResponse.issueFirstRep.diagnostics)
    }
    var bundleCollection: Collection<Resource> = mutableListOf()

    if (resourceResponse is Bundle && resourceResponse.type == Bundle.BundleType.SEARCHSET) {
      bundleCollection = resourceResponse.entry.map { it.resource }
    }
    return bundleCollection
  }

  override fun extractNextUrlsFromResource(resourceResponse: Resource): Collection<String> {
    val queueWork = mutableListOf<String>()

    // If the resource returned is a List, extract Patient references and fetch all resources
    // related to the patient using the $everything operation.
    if (resourceResponse is ListResource) {
      for (entry in resourceResponse.entry) {
        val reference = Reference(entry.item.reference)
        if (reference.referenceElement.resourceType.equals("Patient")) {
          val patientUrl = "${entry.item.reference}/\$everything"
          queueWork.add(patientUrl)
        }
      }
    }

    // If the resource returned is a Bundle, check to see if there is a "next" relation referenced
    // in the Bundle.link component, if so, append the URL referenced to list of URLs to download.
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
