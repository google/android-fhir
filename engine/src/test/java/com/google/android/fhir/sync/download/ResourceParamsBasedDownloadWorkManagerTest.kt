/*
 * Copyright 2023 Google LLC
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

import com.google.android.fhir.logicalId
import com.google.android.fhir.sync.SyncDataParams
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.model.Binary
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ResourceParamsBasedDownloadWorkManagerTest {

  @Test
  fun getNextRequestUrl_shouldReturnNextResourceUrls() =
    runTest(StandardTestDispatcher()) {
      val downloadManager =
        ResourceParamsBasedDownloadWorkManager(
          mapOf(
            ResourceType.Patient to mapOf(Patient.ADDRESS_CITY.paramName to "NAIROBI"),
            ResourceType.Immunization to emptyMap(),
            ResourceType.Observation to emptyMap(),
          ),
          TestResourceParamsBasedDownloadWorkManagerContext("2022-03-20"),
        )

      val urlsToDownload = mutableListOf<String>()
      do {
        val url = downloadManager.getNextRequest()?.let { (it as UrlDownloadRequest).url }
        if (url != null) {
          urlsToDownload.add(url)
        }
      } while (url != null)

      assertThat(urlsToDownload)
        .containsExactly(
          "Patient?address-city=NAIROBI&_sort=_lastUpdated&_lastUpdated=gt2022-03-20",
          "Observation?_sort=_lastUpdated&_lastUpdated=gt2022-03-20",
          "Immunization?_sort=_lastUpdated&_lastUpdated=gt2022-03-20",
        )
    }

  @Test
  fun getNextRequestUrl_shouldReturnResourceAndPageUrlsAsNextUrls() =
    runTest(StandardTestDispatcher()) {
      val downloadManager =
        ResourceParamsBasedDownloadWorkManager(
          mapOf(ResourceType.Patient to emptyMap(), ResourceType.Observation to emptyMap()),
          TestResourceParamsBasedDownloadWorkManagerContext("2022-03-20"),
        )

      val urlsToDownload = mutableListOf<String>()
      do {
        val url = downloadManager.getNextRequest()?.let { (it as UrlDownloadRequest).url }
        if (url != null) {
          urlsToDownload.add(url)
        }
        // Call process response so that It can add the next page url to be downloaded next.
        when (url) {
          "Patient?_sort=_lastUpdated&_lastUpdated=gt2022-03-20",
          "Observation?_sort=_lastUpdated&_lastUpdated=gt2022-03-20", -> {
            downloadManager.processResponse(
              Bundle().apply {
                type = Bundle.BundleType.SEARCHSET
                addLink(
                  Bundle.BundleLinkComponent().apply {
                    relation = "next"
                    this.url = "http://url-to-next-page?token=pageToken"
                  },
                )
              },
            )
          }
        }
      } while (url != null)

      assertThat(urlsToDownload)
        .containsExactly(
          "Patient?_sort=_lastUpdated&_lastUpdated=gt2022-03-20",
          "http://url-to-next-page?token=pageToken",
          "Observation?_sort=_lastUpdated&_lastUpdated=gt2022-03-20",
          "http://url-to-next-page?token=pageToken",
        )
    }

  @Test
  fun getNextRequestUrl_withLastUpdatedTimeProvidedInContext_ShouldAppendGtPrefixToLastUpdatedSearchParam() =
    runTest(StandardTestDispatcher()) {
      val downloadManager =
        ResourceParamsBasedDownloadWorkManager(
          mapOf(ResourceType.Patient to emptyMap()),
          TestResourceParamsBasedDownloadWorkManagerContext("2022-06-28"),
        )
      val url = downloadManager.getNextRequest()?.let { (it as UrlDownloadRequest).url }
      assertThat(url).isEqualTo("Patient?_sort=_lastUpdated&_lastUpdated=gt2022-06-28")
    }

  @Test
  fun getNextRequestUrl_withLastUpdatedSyncParamProvided_shouldReturnUrlWithExactProvidedLastUpdatedSyncParam() =
    runTest(StandardTestDispatcher()) {
      val downloadManager =
        ResourceParamsBasedDownloadWorkManager(
          mapOf(
            ResourceType.Patient to
              mapOf(
                SyncDataParams.LAST_UPDATED_KEY to "2022-06-28",
                SyncDataParams.SORT_KEY to "status",
              ),
          ),
          TestResourceParamsBasedDownloadWorkManagerContext("2022-07-07"),
        )
      val url = downloadManager.getNextRequest()?.let { (it as UrlDownloadRequest).url }
      assertThat(url).isEqualTo("Patient?_lastUpdated=2022-06-28&_sort=status")
    }

  @Test
  fun getNextRequestUrl_withLastUpdatedSyncParamHavingGtPrefix_shouldReturnUrlWithExactProvidedLastUpdatedSyncParam() =
    runTest(StandardTestDispatcher()) {
      val downloadManager =
        ResourceParamsBasedDownloadWorkManager(
          mapOf(ResourceType.Patient to mapOf(SyncDataParams.LAST_UPDATED_KEY to "gt2022-06-28")),
          TestResourceParamsBasedDownloadWorkManagerContext("2022-07-07"),
        )
      val url = downloadManager.getNextRequest()?.let { (it as UrlDownloadRequest).url }
      assertThat(url).isEqualTo("Patient?_lastUpdated=gt2022-06-28&_sort=_lastUpdated")
    }

  @Test
  fun getNextRequestUrl_withNullUpdatedTimeStamp_shouldReturnUrlWithoutLastUpdatedQueryParam() =
    runTest(StandardTestDispatcher()) {
      val downloadManager =
        ResourceParamsBasedDownloadWorkManager(
          mapOf(ResourceType.Patient to mapOf(Patient.ADDRESS_CITY.paramName to "NAIROBI")),
          NoOpResourceParamsBasedDownloadWorkManagerContext,
        )
      val actual = downloadManager.getNextRequest()?.let { (it as UrlDownloadRequest).url }
      assertThat(actual).isEqualTo("Patient?address-city=NAIROBI&_sort=_lastUpdated")
    }

  @Test
  fun getNextRequestUrl_withEmptyUpdatedTimeStamp_shouldReturnUrlWithoutLastUpdatedQueryParam() =
    runTest(StandardTestDispatcher()) {
      val downloadManager =
        ResourceParamsBasedDownloadWorkManager(
          mapOf(ResourceType.Patient to mapOf(Patient.ADDRESS_CITY.paramName to "NAIROBI")),
          TestResourceParamsBasedDownloadWorkManagerContext(""),
        )
      val actual = downloadManager.getNextRequest()?.let { (it as UrlDownloadRequest).url }
      assertThat(actual).isEqualTo("Patient?address-city=NAIROBI&_sort=_lastUpdated")
    }

  @Test
  fun `getSummaryRequestUrls should return resource summary urls`() =
    runTest(StandardTestDispatcher()) {
      val downloadManager =
        ResourceParamsBasedDownloadWorkManager(
          mapOf(
            ResourceType.Patient to mapOf(Patient.ADDRESS_CITY.paramName to "NAIROBI"),
            ResourceType.Immunization to emptyMap(),
            ResourceType.Observation to emptyMap(),
          ),
          TestResourceParamsBasedDownloadWorkManagerContext("2022-03-20"),
        )

      val urls = downloadManager.getSummaryRequestUrls()

      assertThat(urls.map { it.key })
        .containsExactly(ResourceType.Patient, ResourceType.Immunization, ResourceType.Observation)
      assertThat(urls.map { it.value })
        .containsExactly(
          "Patient?address-city=NAIROBI&_sort=_lastUpdated&_lastUpdated=gt2022-03-20&_summary=count",
          "Immunization?_sort=_lastUpdated&_lastUpdated=gt2022-03-20&_summary=count",
          "Observation?_sort=_lastUpdated&_lastUpdated=gt2022-03-20&_summary=count",
        )
    }

  @Test
  fun `processResponse should throw exception including diagnostics from operation outcome`() {
    val downloadManager =
      ResourceParamsBasedDownloadWorkManager(
        emptyMap(),
        NoOpResourceParamsBasedDownloadWorkManagerContext,
      )
    val response =
      OperationOutcome().apply {
        addIssue(
          OperationOutcome.OperationOutcomeIssueComponent().apply {
            diagnostics = "Server couldn't fulfil the request."
          },
        )
      }

    val exception =
      assertThrows(FHIRException::class.java) {
        runTest(StandardTestDispatcher()) { downloadManager.processResponse(response) }
      }

    assertThat(exception.localizedMessage).isEqualTo("Server couldn't fulfil the request.")
  }

  @Test
  fun `processResponse should return empty list for resource that is not a bundle`() =
    runTest(StandardTestDispatcher()) {
      val downloadManager =
        ResourceParamsBasedDownloadWorkManager(
          emptyMap(),
          NoOpResourceParamsBasedDownloadWorkManagerContext,
        )
      val response = Binary().apply { contentType = "application/json" }

      assertThat(downloadManager.processResponse(response)).isEmpty()
    }

  @Test
  fun `processResponse should return empty list for bundle that is not a search set`() =
    runTest(StandardTestDispatcher()) {
      val downloadManager =
        ResourceParamsBasedDownloadWorkManager(
          emptyMap(),
          NoOpResourceParamsBasedDownloadWorkManagerContext,
        )
      val response =
        Bundle().apply {
          type = Bundle.BundleType.TRANSACTIONRESPONSE
          addEntry(
            Bundle.BundleEntryComponent().apply {
              resource = Patient().apply { id = "Patient-Id-001" }
            },
          )
          addEntry(
            Bundle.BundleEntryComponent().apply {
              resource = Patient().apply { id = "Patient-Id-002" }
            },
          )
        }

      assertThat(downloadManager.processResponse(response)).isEmpty()
    }

  @Test
  fun `processResponse should return resources for bundle search set`() =
    runTest(StandardTestDispatcher()) {
      val downloadManager =
        ResourceParamsBasedDownloadWorkManager(
          emptyMap(),
          NoOpResourceParamsBasedDownloadWorkManagerContext,
        )
      val response =
        Bundle().apply {
          type = Bundle.BundleType.SEARCHSET
          addEntry(
            Bundle.BundleEntryComponent().apply {
              resource = Patient().apply { id = "Patient-Id-001" }
            },
          )
          addEntry(
            Bundle.BundleEntryComponent().apply {
              resource = Patient().apply { id = "Patient-Id-002" }
            },
          )
        }

      assertThat(downloadManager.processResponse(response).map { it.logicalId })
        .containsExactly("Patient-Id-001", "Patient-Id-002")
    }

  @Test
  fun `processResponse should add next request`() =
    runTest(StandardTestDispatcher()) {
      val downloadManager =
        ResourceParamsBasedDownloadWorkManager(
          emptyMap(),
          NoOpResourceParamsBasedDownloadWorkManagerContext,
        )
      val response =
        Bundle().apply {
          type = Bundle.BundleType.SEARCHSET
          link =
            mutableListOf(
              Bundle.BundleLinkComponent().apply {
                relation = "next"
                url = "next_url"
              },
            )
        }

      downloadManager.processResponse(response)

      assertThat(downloadManager.getNextRequest()).isEqualTo(DownloadRequest.of("next_url"))
    }

  @Test
  fun `processResponse should not add next request if next url is missing`() =
    runTest(StandardTestDispatcher()) {
      val downloadManager =
        ResourceParamsBasedDownloadWorkManager(
          emptyMap(),
          NoOpResourceParamsBasedDownloadWorkManagerContext,
        )
      val response =
        Bundle().apply {
          type = Bundle.BundleType.SEARCHSET
          addEntry(
            Bundle.BundleEntryComponent().apply {
              resource = Patient().apply { id = "Patient-Id-001" }
            },
          )
        }

      downloadManager.processResponse(response)

      assertThat(downloadManager.getNextRequest()).isNull()
    }
}

val NoOpResourceParamsBasedDownloadWorkManagerContext =
  TestResourceParamsBasedDownloadWorkManagerContext(null)

class TestResourceParamsBasedDownloadWorkManagerContext(private val lastUpdatedTimeStamp: String?) :
  ResourceParamsBasedDownloadWorkManager.TimestampContext {
  override suspend fun saveLastUpdatedTimestamp(resourceType: ResourceType, timestamp: String?) {}

  override suspend fun getLasUpdateTimestamp(resourceType: ResourceType): String? =
    lastUpdatedTimeStamp
}
