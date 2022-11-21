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

import android.content.Context
import androidx.work.WorkerParameters
import com.google.android.fhir.ResourceForDatabaseToSave
import com.google.android.fhir.ResourceType
import com.google.android.fhir.demo.FhirApplication
import com.google.android.fhir.sync.AcceptLocalConflictResolver
import com.google.android.fhir.sync.DownloadWorkManager
import com.google.android.fhir.sync.FhirSyncWorker
import com.google.android.fhir.sync.UploadWorkManager
import org.hl7.fhir.instance.model.api.IAnyResource
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Resource
import timber.log.Timber

class FhirSyncWorker(appContext: Context, workerParams: WorkerParameters) :
  FhirSyncWorker(appContext, workerParams) {

  override fun getDownloadWorkManager(): DownloadWorkManager {
    return DownloadWorkManagerImpl()
  }
  override fun getUploadWorkManager(): UploadWorkManager = TransactionBundleGenerator.getDefault()
  override fun getResourceToSave(): (IAnyResource) -> ResourceForDatabaseToSave? =
  {it ->
    when (it) {
      is Bundle -> updateVersionIdAndLastUpdated(it)
      else ->  updateVersionIdAndLastUpdated(it as Resource)
    }
  }

  private fun updateVersionIdAndLastUpdated(bundle: Bundle) : ResourceForDatabaseToSave? {
    when (bundle.type) {
      Bundle.BundleType.TRANSACTIONRESPONSE -> {
        bundle.entry.forEach {
          when {
            it.hasResource() -> updateVersionIdAndLastUpdated(it.resource)
            it.hasResponse() -> updateVersionIdAndLastUpdated(it.response)
          }
        }
      }
      else -> {
        // Leave it for now.
        Timber.i("Received request to update meta values for ${bundle.type}")
      }
    }
    return null
  }

  override fun getConflictResolver() = AcceptLocalConflictResolver

  override fun getFhirEngine() = FhirApplication.fhirEngine(applicationContext)
  private fun updateVersionIdAndLastUpdated(response: Bundle.BundleEntryResponseComponent) : ResourceForDatabaseToSave?  {
    if (response.hasEtag() && response.hasLastModified() && response.hasLocation()) {
      return response.resourceIdAndType?.let { (id, type) ->
        ResourceForDatabaseToSave(
          id,
          type,
          response.etag,
          response.lastModified.toInstant()
        )
      }
    }
    return null
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
}
