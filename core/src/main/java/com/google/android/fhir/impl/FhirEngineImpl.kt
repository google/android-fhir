/*
 * Copyright 2020 Google LLC
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

package com.google.android.fhir.impl

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.ResourceNotFoundException
import com.google.android.fhir.db.Database
import com.google.android.fhir.db.ResourceNotFoundInDbException
import com.google.android.fhir.resource.getResourceType
import com.google.android.fhir.search.Search
import com.google.android.fhir.sync.FhirDataSource
import com.google.android.fhir.sync.FhirSynchronizer
import com.google.android.fhir.sync.PeriodicSyncConfiguration
import com.google.android.fhir.sync.Result
import com.google.android.fhir.sync.SyncConfiguration
import com.google.android.fhir.sync.SyncWorkType
import org.hl7.fhir.r4.model.Resource

/** Implementation of [FhirEngine]. */
class FhirEngineImpl
constructor(
  private val database: Database,
  private val search: Search,
  private var periodicSyncConfiguration: PeriodicSyncConfiguration?,
  private val dataSource: FhirDataSource,
  private val context: Context
) : FhirEngine {

  init {
    periodicSyncConfiguration?.let { config -> triggerInitialDownload(config) }
  }

  override suspend fun <R : Resource> save(resource: R) {
    database.insert(resource)
  }

  override suspend fun <R : Resource> saveAll(resources: List<R>) {
    database.insert(*resources.toTypedArray<Resource>())
  }

  override suspend fun <R : Resource> update(resource: R) {
    database.update(resource)
  }

  @Throws(ResourceNotFoundException::class)
  override suspend fun <R : Resource> load(clazz: Class<R>, id: String): R {
    return try {
      database.select(clazz, id)
    } catch (e: ResourceNotFoundInDbException) {
      throw ResourceNotFoundException(getResourceType(clazz).name, id, e)
    }
  }

  override suspend fun <R : Resource> remove(clazz: Class<R>, id: String) {
    database.delete(clazz, id)
  }

  override fun search(): Search {
    return search
  }

  override suspend fun sync(syncConfiguration: SyncConfiguration): Result {
    return FhirSynchronizer(syncConfiguration, dataSource, database).sync()
  }

  override suspend fun periodicSync(): Result {
    val syncConfig =
      periodicSyncConfiguration
        ?: throw java.lang.UnsupportedOperationException("Periodic sync configuration was not set")
    val syncResult = FhirSynchronizer(syncConfig.syncConfiguration, dataSource, database).sync()
    setupNextDownload(syncConfig)
    return syncResult
  }

  override fun updatePeriodicSyncConfiguration(syncConfig: PeriodicSyncConfiguration) {
    periodicSyncConfiguration = syncConfig
    setupNextDownload(syncConfig)
  }

  private fun setupNextDownload(syncConfig: PeriodicSyncConfiguration) {
    setupDownload(syncConfig = syncConfig, withInitialDelay = true)
  }

  private fun triggerInitialDownload(syncConfig: PeriodicSyncConfiguration) {
    setupDownload(syncConfig = syncConfig, withInitialDelay = false)
  }

  private fun setupDownload(syncConfig: PeriodicSyncConfiguration, withInitialDelay: Boolean) {
    val workerClass = syncConfig.periodicSyncWorker
    val downloadRequest =
      if (withInitialDelay) {
        OneTimeWorkRequest.Builder(workerClass)
          .setConstraints(syncConfig.syncConstraints)
          .setInitialDelay(syncConfig.repeat.interval, syncConfig.repeat.timeUnit)
          .build()
      } else {
        OneTimeWorkRequest.Builder(workerClass).setConstraints(syncConfig.syncConstraints).build()
      }

    WorkManager.getInstance(context)
      .enqueueUniqueWork(SyncWorkType.DOWNLOAD.workerName, ExistingWorkPolicy.KEEP, downloadRequest)
  }
}
