package com.google.android.fhir.sync

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.fhir.FhirEngine

object Sync {
  suspend fun oneTimeSync(
    fhirEngine: FhirEngine,
    dataSource: DataSource,
    resourceSyncParams: ResourceSyncParams
  ): Result {
    return FhirSynchronizer(fhirEngine, dataSource, resourceSyncParams).download()
  }

  inline fun <reified W : PeriodicSyncWorker> periodicSync(
    context: Context,
    periodicSyncConfiguration: PeriodicSyncConfiguration
  ) {
    val periodicWorkRequest =
      PeriodicWorkRequestBuilder<W>(
        periodicSyncConfiguration.repeat.interval,
        periodicSyncConfiguration.repeat.timeUnit
      )
        .setConstraints(periodicSyncConfiguration.syncConstraints)
        .build()
    WorkManager.getInstance(context)
      .enqueueUniquePeriodicWork(
        SyncWorkType.DOWNLOAD.workerName,
        ExistingPeriodicWorkPolicy.KEEP,
        periodicWorkRequest
      )
  }
}

/** Defines different types of synchronisation workers: download and upload */
enum class SyncWorkType(val workerName: String) {
  DOWNLOAD("download"),
  UPLOAD("upload")
}