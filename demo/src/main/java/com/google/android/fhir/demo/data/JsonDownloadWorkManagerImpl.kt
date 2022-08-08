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

import com.google.android.fhir.json.sync.JsonDownloadWorkManager
import java.util.LinkedList
import org.hl7.fhir.exceptions.FHIRException
import org.json.JSONObject

class JsonDownloadWorkManagerImpl : JsonDownloadWorkManager {
  private val urls = LinkedList(listOf("Patient?address-city=NAIROBI"))

  override suspend fun getNextRequestUrl(): String? {
    return urls.poll() ?: return null
  }

  // App developer implements this logic
  override suspend fun processResponse(response: JSONObject): Collection<JSONObject> {

    val resourceType = response.get("resourceType")

    if (resourceType.equals("OperationOutcome")) {
      throw FHIRException(response.getJSONArray("issue").getJSONObject(0)?.getString("diagnostics"))
    }

    // If the resource returned is a List containing Patients, extract Patient references and fetch
    // all resources related to the patient using the $everything operation.
    if (resourceType.equals("ListResource")) {
      for (i in 0 until response.getJSONArray("entry").length()) {
        val entry = response.getJSONArray("entry").getJSONObject(i)
        urls.add(entry.getJSONObject("item").getString("reference"))
      }
    }

    // If the resource returned is a Bundle, check to see if there is a "next" relation referenced
    // in the Bundle.link component, if so, append the URL referenced to list of URLs to download.
    if (resourceType.equals("Bundle")) {
      var nextUrl: String? = null
      for (i in 0 until response.getJSONArray("link").length()) {
        val link = response.getJSONArray("link").getJSONObject(i)
        if (link.getString("relation").equals("next")) {
          nextUrl = link.getString("url")
        }
      }

      nextUrl?.let { urls.add(nextUrl) }
    }

    // Finally, extract the downloaded resources from the bundle.
    val bundleCollection = mutableListOf<JSONObject>()
    if (resourceType.equals("Bundle") && response.getString("type").equals("searchset")) {
      for (i in 0 until response.getJSONArray("entry").length()) {
        val entry = response.getJSONArray("entry").getJSONObject(i)
        bundleCollection.add(entry.getJSONObject("resource"))
      }
    }
    return bundleCollection
  }
}
