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

import com.google.android.fhir.json.SyncDownloadContext
import com.google.android.fhir.json.sync.JsonDownloadWorkManager
import com.google.android.fhir.json.sync.JsonResource
import java.time.Instant
import java.util.Date
import java.util.LinkedList
import org.json.JSONArray
import org.json.JSONObject

class JsonDownloadWorkManagerImpl : JsonDownloadWorkManager {
  private val urls =
    LinkedList(listOf("mobile_api_get_family_data", "mobile_api_get_clinical_data"))

  override suspend fun getNextRequestUrl(context: SyncDownloadContext): String? {
    return urls.poll() ?: return null
  }

  // App developer implements this logic
  override suspend fun processResponse(response: JSONObject): Collection<JsonResource> {
    val statusCode = response.getInt("status_code")
    if (statusCode != 200) {
      throw Exception(response.getString("message"))
    }

    val message = response.getString("message")
    val bundleCollection = mutableListOf<JsonResource>()
    // Extract resource type from the message field as it will contain what resource was retrieved
    if (message.contains("Family Data")){
       bundleCollection.addAll(extractResource(response.getJSONArray("data"), "Family", "family_id" ))
    } else if (message.contains("Clinical Data")) {
      val clinicalJsonObject= response.getJSONObject("data")
      bundleCollection.addAll(extractResource(clinicalJsonObject.getJSONArray("VACCINATION_MASTER"),
                           "VaccinationMaster",
                           "vaccination_id" ))
      bundleCollection.addAll(extractResource(clinicalJsonObject.getJSONArray("PROTOCOL_MASTER"),
                                              "ProtocolMaster",
                                              "protocol_id" ))

      bundleCollection.addAll(extractResource(clinicalJsonObject.getJSONArray("DRUGS_MASTER"),
                                              "DrugsMaster",
                                              "drug_id" ))
      // There are other resources that can be extracted but I got bored, and as this is a demo, I stopped
      // I also assumed that all fields were populated and are not empty. Need to implement a check for this
    }

    return bundleCollection

  }

  private fun extractResource(jsonArray: JSONArray, resourceType: String, id: String): Collection<JsonResource> {
    val resources = mutableListOf<JsonResource>()
    for (i in 0 until jsonArray.length()) {
      resources.add(object : JsonResource {
        override val id: String
          get() = jsonArray.getJSONObject(i).getString(id)
        override val resourceType: String
          get() = resourceType
        override val versionId: String?
          get() = null
        override val lastUpdated: Date?
          get() = Date.from(Instant.now())
        override val payload: JSONObject
          get() = jsonArray.getJSONObject(i)
      }
      )
    }
    return resources
  }
}
