/*
 * Copyright 2025 Google LLC
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

package com.google.android.fhir.engine.benchmarks.app.data

import android.content.Context
import android.content.Intent
import androidx.tracing.traceAsync
import androidx.work.WorkerParameters
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.engine.benchmarks.app.MainApplication
import com.google.android.fhir.engine.benchmarks.app.measureTimedValueAsync
import com.google.android.fhir.search.search
import com.google.android.fhir.sync.AcceptLocalConflictResolver
import com.google.android.fhir.sync.AcceptRemoteConflictResolver
import com.google.android.fhir.sync.ConflictResolver
import com.google.android.fhir.sync.DownloadWorkManager
import com.google.android.fhir.sync.FhirSyncWorker
import com.google.android.fhir.sync.download.ResourceParamsBasedDownloadWorkManager
import com.google.android.fhir.sync.upload.HttpCreateMethod
import com.google.android.fhir.sync.upload.HttpUpdateMethod
import com.google.android.fhir.sync.upload.UploadStrategy
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType

internal const val SYNC_DOWNLOAD_BENCHMARK_BROADCAST_ACTION =
  "com.google.android.fhir.engine.benchmarks.app.ACTION_SYNC_DOWNLOAD_BENCHMARK_DATA"

internal const val SYNC_INDIVIDUAL_UPLOAD_BENCHMARK_BROADCAST_ACTION =
  "com.google.android.fhir.engine.benchmarks.app.ACTION_SYNC_INDIVIDUAL_UPLOAD_BENCHMARK_DATA"

internal const val SYNC_BUNDLE_UPLOAD_BENCHMARK_BROADCAST_ACTION =
  "com.google.android.fhir.engine.benchmarks.app.ACTION_SYNC_BUNDLE_UPLOAD_BENCHMARK_DATA"

internal class DownloadFhirSyncWorker(appContext: Context, workerParams: WorkerParameters) :
  FhirSyncWorker(appContext, workerParams) {

  override fun getFhirEngine(): FhirEngine = MainApplication.fhirEngine(applicationContext)

  override fun getDownloadWorkManager(): DownloadWorkManager =
    ResourceParamsBasedDownloadWorkManager(
      syncParams =
        mapOf(
          ResourceType.Patient to emptyMap(),
        ),
      context =
        object : ResourceParamsBasedDownloadWorkManager.TimestampContext {
          override suspend fun saveLastUpdatedTimestamp(
            resourceType: ResourceType,
            timestamp: String?,
          ) {
            // no-op
          }

          override suspend fun getLasUpdateTimestamp(resourceType: ResourceType): String? = null
        },
    )

  override fun getConflictResolver(): ConflictResolver = AcceptRemoteConflictResolver

  override fun getUploadStrategy(): UploadStrategy =
    UploadStrategy.forBundleRequest(
      methodForCreate = HttpCreateMethod.PUT,
      methodForUpdate = HttpUpdateMethod.PATCH,
      squash = true,
      bundleSize = 0,
    )

  override suspend fun doWork(): Result {
    getFhirEngine().clearDatabase()

    val (result, duration) =
      measureTimedValueAsync { traceAsync(DOWNLOAD_SYNC_TRACE_NAME, 14) { super.doWork() } }

    applicationContext.sendBroadcast(
      Intent(SYNC_DOWNLOAD_BENCHMARK_BROADCAST_ACTION).apply {
        putExtra("benchmarkDuration", duration.toString())
        setPackage("com.google.android.fhir.engine.benchmarks.app")
      },
    )

    return result
  }

  companion object {
    const val DOWNLOAD_SYNC_TRACE_NAME = "DownloadFhirSyncWorkerSection"
  }
}

internal class BundleUploadFhirSyncWorker(appContext: Context, workerParams: WorkerParameters) :
  UploadFhirSyncWorker(appContext, workerParams) {
  override val benchmarkBroadcastAction: String
    get() = SYNC_BUNDLE_UPLOAD_BENCHMARK_BROADCAST_ACTION

  override val traceSectionNameIdentifier: String
    get() = "BundleUploadFhirSyncWorkerSection"

  override fun getUploadStrategy(): UploadStrategy =
    UploadStrategy.forBundleRequest(
      methodForCreate = HttpCreateMethod.PUT,
      methodForUpdate = HttpUpdateMethod.PATCH,
      squash = true,
      bundleSize = 500,
    )
}

internal class PerResourceUploadFhirSyncWorker(
  appContext: Context,
  workerParams: WorkerParameters,
) : UploadFhirSyncWorker(appContext, workerParams) {
  override val benchmarkBroadcastAction: String
    get() = SYNC_INDIVIDUAL_UPLOAD_BENCHMARK_BROADCAST_ACTION

  override val traceSectionNameIdentifier: String
    get() = "PerResourceUploadFhirSyncWorkerSection"

  override fun getUploadStrategy(): UploadStrategy =
    UploadStrategy.forIndividualRequest(
      methodForCreate = HttpCreateMethod.PUT,
      methodForUpdate = HttpUpdateMethod.PATCH,
      squash = true,
    )
}

internal abstract class UploadFhirSyncWorker(appContext: Context, workerParams: WorkerParameters) :
  FhirSyncWorker(appContext, workerParams) {

  abstract val benchmarkBroadcastAction: String

  abstract val traceSectionNameIdentifier: String

  override fun getFhirEngine(): FhirEngine = MainApplication.fhirEngine(applicationContext)

  override fun getDownloadWorkManager(): DownloadWorkManager =
    ResourceParamsBasedDownloadWorkManager(
      syncParams = emptyMap(),
      context =
        object : ResourceParamsBasedDownloadWorkManager.TimestampContext {
          override suspend fun saveLastUpdatedTimestamp(
            resourceType: ResourceType,
            timestamp: String?,
          ) {
            // no-op
          }

          override suspend fun getLasUpdateTimestamp(resourceType: ResourceType): String? = null
        },
    )

  override fun getConflictResolver(): ConflictResolver = AcceptLocalConflictResolver

  override suspend fun doWork(): Result {
    batchUpdatePatients { it.active = !it.active }

    val (result, duration) =
      measureTimedValueAsync { traceAsync(traceSectionNameIdentifier, 65) { super.doWork() } }

    applicationContext.sendBroadcast(
      Intent(benchmarkBroadcastAction).apply {
        putExtra("benchmarkDuration", duration.toString())
        setPackage("com.google.android.fhir.engine.benchmarks.app")
      },
    )

    return result
  }

  private suspend fun batchUpdatePatients(batchSize: Int = 500, updateBlock: (Patient) -> Unit) {
    val fhirEngine = getFhirEngine()

    var counter = 0
    do {
      val patients =
        fhirEngine
          .search<Patient> {
            count = batchSize
            from = counter
          }
          .map { it.resource }
      patients.forEach { updateBlock(it) }
      fhirEngine.update(*patients.toTypedArray())
      counter += patients.size
    } while (patients.size == batchSize)
  }
}
