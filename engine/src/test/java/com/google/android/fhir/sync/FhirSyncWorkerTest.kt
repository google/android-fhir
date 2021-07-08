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

package com.google.android.fhir.sync

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.SyncDownloadContext
import com.google.android.fhir.db.impl.dao.LocalChangeToken
import com.google.android.fhir.db.impl.dao.SquashedLocalChange
import com.google.android.fhir.search.Search
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class FhirSyncWorkerTest {
  private lateinit var context: Context
  class PassingPeriodicSyncWorker(appContext: Context, workerParams: WorkerParameters) :
    FhirSyncWorker(appContext, workerParams) {
    private var engine: FhirEngine =
      object : FhirEngine {
        override suspend fun <R : Resource> save(vararg resource: R) {}

        override suspend fun <R : Resource> update(resource: R) {}

        override suspend fun <R : Resource> load(clazz: Class<R>, id: String): R {
          return clazz.newInstance()
        }

        override suspend fun <R : Resource> remove(clazz: Class<R>, id: String) {}

        override suspend fun <R : Resource> search(search: Search): List<R> {
          return emptyList()
        }

        override suspend fun syncUpload(
          upload: suspend (List<SquashedLocalChange>) -> List<LocalChangeToken>
        ) {}

        override suspend fun syncDownload(
          download: suspend (SyncDownloadContext) -> List<Resource>
        ) {}

        override suspend fun count(search: Search): Long {
          return 0
        }
      }
    private var source: DataSource =
      object : DataSource {
        override suspend fun loadData(path: String): Bundle {
          return Bundle()
        }

        override suspend fun insert(
          resourceType: String,
          resourceId: String,
          payload: String
        ): Resource {
          return Observation()
        }

        override suspend fun update(
          resourceType: String,
          resourceId: String,
          payload: String
        ): OperationOutcome {
          return OperationOutcome()
        }

        override suspend fun delete(resourceType: String, resourceId: String): OperationOutcome {
          return OperationOutcome()
        }
      }
    override fun getFhirEngine(): FhirEngine = engine
    override fun getDataSource(): DataSource = source
    override fun getSyncData(): ResourceSyncParams = mapOf()
  }

  class FailingPeriodicSyncWorker(appContext: Context, workerParams: WorkerParameters) :
    FhirSyncWorker(appContext, workerParams) {
    private var engine =
      object : FhirEngine {
        override suspend fun <R : Resource> save(vararg resource: R) {}

        override suspend fun <R : Resource> update(resource: R) {}

        override suspend fun <R : Resource> load(clazz: Class<R>, id: String): R {
          return clazz.newInstance()
        }

        override suspend fun <R : Resource> remove(clazz: Class<R>, id: String) {}

        override suspend fun <R : Resource> search(search: Search): List<R> {
          return emptyList()
        }

        override suspend fun syncUpload(
          upload: suspend (List<SquashedLocalChange>) -> List<LocalChangeToken>
        ) {
          upload(listOf())
        }

        override suspend fun syncDownload(
          download: suspend (SyncDownloadContext) -> List<Resource>
        ) {
          download(
            object : SyncDownloadContext {
              override suspend fun getLatestTimestampFor(type: ResourceType): String {
                return "123456788"
              }
            }
          )
        }

        override suspend fun count(search: Search): Long {
          return 0
        }
      }
    private var source =
      object : DataSource {
        override suspend fun loadData(path: String): Bundle {
          throw Exception("Loading failed...")
        }

        override suspend fun insert(
          resourceType: String,
          resourceId: String,
          payload: String
        ): Resource {
          throw Exception("Insertion failed...")
        }

        override suspend fun update(
          resourceType: String,
          resourceId: String,
          payload: String
        ): OperationOutcome {
          throw Exception("Updating failed...")
        }

        override suspend fun delete(resourceType: String, resourceId: String): OperationOutcome {
          throw Exception("Deleting failed...")
        }
      }
    override fun getFhirEngine(): FhirEngine = engine
    override fun getDataSource(): DataSource = source
    override fun getSyncData(): ResourceSyncParams =
      mapOf(ResourceType.Patient to mapOf("address-city" to "NAIROBI"))
  }

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
  }

  @Test
  fun fhirSyncWorker_successfulTask_resultSuccess() {
    // In a ListenableWorker, runAttemptCount starts from 0. But in a TestListenableWorkerBuilder,
    // its set to start from 1. So overriding the default value when required in the test case.
    val worker =
      TestListenableWorkerBuilder<PassingPeriodicSyncWorker>(
          context,
          inputData = Data.Builder().putInt(MAX_RETRIES_ALLOWED, 1).build(),
          runAttemptCount = 0
        )
        .build()
    val result = runBlocking { worker.doWork() }
    assertThat(result).isEqualTo(ListenableWorker.Result.success())
  }

  @Test
  fun fhirSyncWorker_failedTaskWithZeroRetires_resultShouldBeFail() {
    val worker =
      TestListenableWorkerBuilder<FailingPeriodicSyncWorker>(
          context,
          inputData = Data.Builder().putInt(MAX_RETRIES_ALLOWED, 0).build(),
          runAttemptCount = 0
        )
        .build()
    val result = runBlocking { worker.doWork() }
    assertThat(result).isEqualTo(ListenableWorker.Result.failure())
  }

  @Test
  fun fhirSyncWorker_failedTaskWithCurrentRunAttemptSameAsTheReties_resultShouldBeFail() {
    val worker =
      TestListenableWorkerBuilder<FailingPeriodicSyncWorker>(
          context,
          inputData = Data.Builder().putInt(MAX_RETRIES_ALLOWED, 2).build(),
          runAttemptCount = 2
        )
        .build()
    val result = runBlocking { worker.doWork() }
    assertThat(result).isEqualTo(ListenableWorker.Result.failure())
  }

  @Test
  fun fhirSyncWorker_failedTaskWithCurrentRunAttemptSmallerThanTheReties_resultShouldBeRetry() {
    val worker =
      TestListenableWorkerBuilder<FailingPeriodicSyncWorker>(
          context,
          inputData = Data.Builder().putInt(MAX_RETRIES_ALLOWED, 2).build(),
          runAttemptCount = 1
        )
        .build()
    val result = runBlocking { worker.doWork() }
    assertThat(result).isEqualTo(ListenableWorker.Result.retry())
  }
}
