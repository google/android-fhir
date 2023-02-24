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

package com.google.android.fhir.sync.download

import com.google.android.fhir.SyncDownloadContext
import com.google.android.fhir.sync.DataSource
import com.google.android.fhir.sync.DownloadState
import com.google.common.truth.Truth.assertThat
import java.net.UnknownHostException
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DownloaderImplTest {

  val searchPageParamToSearchResponseBundleMap =
    mapOf(
      "patient-page1" to
        Bundle().apply {
          type = Bundle.BundleType.SEARCHSET
          addLink(
            Bundle.BundleLinkComponent().apply {
              relation = "next"
              url = "url-to-server/patient-page2"
            }
          )
          addEntry(Bundle.BundleEntryComponent().setResource(Patient().apply { id = "Patient-1" }))
        },
      "patient-page2" to
        Bundle().apply {
          type = Bundle.BundleType.SEARCHSET
          addEntry(Bundle.BundleEntryComponent().setResource(Patient().apply { id = "Patient-2" }))
        },
      "observation-page1" to
        Bundle().apply {
          type = Bundle.BundleType.SEARCHSET
          addLink(
            Bundle.BundleLinkComponent().apply {
              relation = "next"
              url = "url-to-server/observation-page2"
            }
          )
          addEntry(
            Bundle.BundleEntryComponent().setResource(Observation().apply { id = "Observation-1" })
          )
        },
      "observation-page2" to
        Bundle().apply {
          type = Bundle.BundleType.SEARCHSET
          addEntry(
            Bundle.BundleEntryComponent().setResource(Observation().apply { id = "Observation-2" })
          )
        }
    )

  @Test
  fun `downloader with patient and observations should download successfully`() = runBlocking {
    val downloader =
      DownloaderImpl(
        object : DataSource {
          override suspend fun download(path: String): Resource {
            return when {
              path.contains("summary") -> Bundle().apply { total = 1 }
              path.contains("patient-page1") ->
                searchPageParamToSearchResponseBundleMap["patient-page1"]!!
              path.contains("patient-page2") ->
                searchPageParamToSearchResponseBundleMap["patient-page2"]!!
              path.contains("observation-page1") ->
                searchPageParamToSearchResponseBundleMap["observation-page1"]!!
              path.contains("observation-page2") ->
                searchPageParamToSearchResponseBundleMap["observation-page2"]!!
              else -> OperationOutcome()
            }
          }

          override suspend fun upload(bundle: Bundle): Resource {
            TODO("Not yet implemented")
          }
        },
        ResourceParamsBasedDownloadWorkManager(
          mapOf(
            ResourceType.Patient to mapOf("param" to "patient-page1"),
            ResourceType.Observation to mapOf("param" to "observation-page1")
          )
        )
      )

    val result = mutableListOf<DownloadState>()
    downloader
      .download(
        object : SyncDownloadContext {
          override suspend fun getLatestTimestampFor(type: ResourceType): String? = null
        }
      )
      .collect { result.add(it) }

    assertThat(result.filterIsInstance<DownloadState.Started>())
      .containsExactly(
        DownloadState.Started(ResourceType.Bundle, 2), // 1 patient and 1 observation
      )

    assertThat(
        result.filterIsInstance<DownloadState.Success>().flatMap { it.resources }.map { it.id }
      )
      .containsExactly("Patient-1", "Patient-2", "Observation-1", "Observation-2")
      .inOrder()
  }

  @Test
  fun `downloader with patient and observations should return failure in case of server or network error`() =
    runBlocking {
      val downloader =
        DownloaderImpl(
          object : DataSource {
            override suspend fun download(path: String): Resource {
              return when {
                path.contains("summary") -> Bundle().apply { total = 1 }
                path.contains("patient-page1") ->
                  searchPageParamToSearchResponseBundleMap["patient-page1"]!!
                path.contains("patient-page2") ->
                  OperationOutcome().apply {
                    addIssue(
                      OperationOutcome.OperationOutcomeIssueComponent().apply {
                        diagnostics = "Server couldn't fulfil the request."
                      }
                    )
                  }
                path.contains("observation-page1") ->
                  searchPageParamToSearchResponseBundleMap["observation-page1"]!!
                path.contains("observation-page2") ->
                  throw UnknownHostException(
                    "Url host can't be found. Check if device is connected to internet."
                  )
                else -> OperationOutcome()
              }
            }

            override suspend fun upload(bundle: Bundle): Resource {
              TODO("Upload not tested in this path")
            }
          },
          ResourceParamsBasedDownloadWorkManager(
            mapOf(
              ResourceType.Patient to mapOf("param" to "patient-page1"),
              ResourceType.Observation to mapOf("param" to "observation-page1")
            )
          )
        )

      val result = mutableListOf<DownloadState>()
      downloader
        .download(
          object : SyncDownloadContext {
            override suspend fun getLatestTimestampFor(type: ResourceType) = null
          }
        )
        .collect { result.add(it) }

      assertThat(result.filterIsInstance<DownloadState.Started>())
        .containsExactly(
          DownloadState.Started(ResourceType.Bundle, 2), // 1 patient and 1 observation
        )

      assertThat(result.filterIsInstance<DownloadState.Failure>()).hasSize(2)

      assertThat(result.filterIsInstance<DownloadState.Failure>().map { it.syncError.resourceType })
        .containsExactly(ResourceType.Patient, ResourceType.Observation)
        .inOrder()
      assertThat(
          result.filterIsInstance<DownloadState.Failure>().map { it.syncError.exception.message }
        )
        .containsExactly(
          "Server couldn't fulfil the request.",
          "Url host can't be found. Check if device is connected to internet."
        )
        .inOrder()
    }

  @Test
  fun `downloader with patient and observations should continue to download observations if patient download fail`() =
    runBlocking {
      val downloader =
        DownloaderImpl(
          object : DataSource {
            override suspend fun download(path: String): Resource {
              return when {
                path.contains("summary") -> Bundle().apply { total = 1 }
                path.contains("patient-page1") || path.contains("patient-page2") ->
                  OperationOutcome().apply {
                    addIssue(
                      OperationOutcome.OperationOutcomeIssueComponent().apply {
                        diagnostics = "Server couldn't fulfil the request."
                      }
                    )
                  }
                path.contains("observation-page1") ->
                  searchPageParamToSearchResponseBundleMap["observation-page1"]!!
                path.contains("observation-page2") ->
                  searchPageParamToSearchResponseBundleMap["observation-page2"]!!
                else -> OperationOutcome()
              }
            }

            override suspend fun upload(bundle: Bundle): Resource {
              TODO("Not yet implemented")
            }
          },
          ResourceParamsBasedDownloadWorkManager(
            mapOf(
              ResourceType.Patient to mapOf("param" to "patient-page1"),
              ResourceType.Observation to mapOf("param" to "observation-page1")
            )
          )
        )

      val result = mutableListOf<DownloadState>()
      downloader
        .download(
          object : SyncDownloadContext {
            override suspend fun getLatestTimestampFor(type: ResourceType) = null
          }
        )
        .collect { result.add(it) }

      assertThat(result.filterIsInstance<DownloadState.Started>())
        .containsExactly(
          DownloadState.Started(ResourceType.Bundle, 2), // 1 patient and 1 observation
        )

      assertThat(result.filterIsInstance<DownloadState.Failure>().map { it.syncError.resourceType })
        .containsExactly(ResourceType.Patient)

      assertThat(
          result
            .filterIsInstance<DownloadState.Success>()
            .flatMap { it.resources }
            .filterIsInstance<Observation>()
        )
        .hasSize(2)
    }

  @Test
  fun `downloader should emit Started state`() = runBlocking {
    val downloader =
      DownloaderImpl(
        object : DataSource {
          override suspend fun download(path: String): Resource {
            return when {
              path.contains("patient-page1") ->
                searchPageParamToSearchResponseBundleMap["patient-page1"]!!
              else -> OperationOutcome()
            }
          }

          override suspend fun upload(bundle: Bundle): Resource {
            throw UnsupportedOperationException()
          }
        },
        ResourceParamsBasedDownloadWorkManager(
          mapOf(ResourceType.Patient to mapOf("param" to "patient-page1"))
        )
      )

    val result = mutableListOf<DownloadState>()
    downloader
      .download(
        object : SyncDownloadContext {
          override suspend fun getLatestTimestampFor(type: ResourceType): String? = null
        }
      )
      .collectIndexed { index, value -> result.add(value) }

    assertThat(result.first()).isInstanceOf(DownloadState.Started::class.java)
  }

  @Test
  fun `downloader should emit Success state`() = runBlocking {
    val downloader =
      DownloaderImpl(
        object : DataSource {
          override suspend fun download(path: String): Resource {
            return when {
              path.contains("patient-page1") ->
                searchPageParamToSearchResponseBundleMap["patient-page1"]!!.apply { total = 1 }
              else -> OperationOutcome()
            }
          }

          override suspend fun upload(bundle: Bundle): Resource {
            throw UnsupportedOperationException()
          }
        },
        ResourceParamsBasedDownloadWorkManager(
          mapOf(ResourceType.Patient to mapOf("param" to "patient-page1"))
        )
      )

    val result = mutableListOf<DownloadState>()
    downloader
      .download(
        object : SyncDownloadContext {
          override suspend fun getLatestTimestampFor(type: ResourceType): String? = null
        }
      )
      .collectIndexed { index, value -> result.add(value) }

    assertThat(result.first()).isInstanceOf(DownloadState.Started::class.java)
    assertThat(result.elementAt(1)).isInstanceOf(DownloadState.Success::class.java)

    val success = result.elementAt(1) as DownloadState.Success
    assertThat(success.total).isEqualTo(1)
    assertThat(success.completed).isEqualTo(1)
  }
}
