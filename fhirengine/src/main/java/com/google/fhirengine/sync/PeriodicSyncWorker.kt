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

package com.google.fhirengine.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.fhirengine.FhirEngine
import com.google.fhirengine.sync.Result.Success

/**
 * A WorkManager Worker that handles periodic sync.
 */
abstract class PeriodicSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    abstract fun getFhirEngine(): FhirEngine

    override suspend fun doWork(): Result {
        // TODO handle retry
        val result = getFhirEngine().periodicSync()
        if (result is Success) {
            return Result.success()
        }
        return Result.failure()
    }
}
