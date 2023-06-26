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

import com.google.android.fhir.logicalId
import com.google.android.fhir.sync.SyncDataParams
import com.google.android.fhir.sync.UrlRequest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runBlockingTest
import org.hl7.fhir.exceptions.FHIRException
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
  fun getNextRequestUrl_shouldReturnNextResourceUrls() = runBlockingTest {
    val downloadManager =
      ResourceParamsBasedDownloadWorkManager(
        mapOf(
          ResourceType.Patient to mapOf(Patient.ADDRESS_CITY.paramName to "NAIROBI"),
          ResourceType.Immunization to emptyMap(),
          ResourceType.Observation to emptyMap(),
        ),
        TestResourceParamsBasedDownloadWorkManagerContext("2022-03-20")
      )

    val urlsToDownload = mutableListOf<String>()
    do {
      val url = downloadManager.getNextRequest()?.let { (it as UrlRequest).url }
      if (url != null) {
        urlsToDownload.add(url)
      }
    } while (url != null)

    assertThat(urlsToDownload)
      .containsExactly(
        "Patient?address-city=NAIROBI&_sort=_lastUpdated&_lastUpdated=gt2022-03-20",
        "Observation?_sort=_lastUpdated&_lastUpdated=gt2022-03-20",
        "Immunization?_sort=_lastUpdated&_lastUpdated=gt2022-03-20"
      )
  }

  @Test
  fun getNextRequestUrl_shouldReturnResourceAndPageUrlsAsNextUrls() = runBlockingTest {
    val downloadManager =
      ResourceParamsBasedDownloadWorkManager(
        mapOf(ResourceType.Patient to emptyMap(), ResourceType.Observation to emptyMap()),
        TestResourceParamsBasedDownloadWorkManagerContext("2022-03-20")
      )

    val urlsToDownload = mutableListOf<String>()
    do {
      val url = downloadManager.getNextRequest()?.let { (it as UrlRequest).url }
      if (url != null) {
        urlsToDownload.add(url)
      }
      // Call process response so that It can add the next page url to be downloaded next.
      when (url) {
        "Patient?_sort=_lastUpdated&_lastUpdated=gt2022-03-20",
        "Observation?_sort=_lastUpdated&_lastUpdated=gt2022-03-20" -> {
          downloadManager.processResponse(
            Bundle().apply {
              type = Bundle.BundleType.SEARCHSET
              addLink(
                Bundle.BundleLinkComponent().apply {
                  relation = "next"
                  this.url = "http://url-to-next-page?token=pageToken"
                }
              )
            }
          )
        }
      }
    } while (url != null)

    assertThat(urlsToDownload)
      .containsExactly(
        "Patient?_sort=_lastUpdated&_lastUpdated=gt2022-03-20",
        "http://url-to-next-page?token=pageToken",
        "Observation?_sort=_lastUpdated&_lastUpdated=gt2022-03-20",
        "http://url-to-next-page?token=pageToken"
      )
  }

  @Test
  fun getNextRequestUrl_withLastUpdatedTimeProvidedInContext_ShouldAppendGtPrefixToLastUpdatedSearchParam() =
    runBlockingTest {
      val downloadManager =
        ResourceParamsBasedDownloadWorkManager(
          mapOf(ResourceType.Patient to emptyMap()),
          TestResourceParamsBasedDownloadWorkManagerContext("2022-06-28")
        )
      val url = downloadManager.getNextRequest()?.let { (it as UrlRequest).url }
      assertThat(url).isEqualTo("Patient?_sort=_lastUpdated&_lastUpdated=gt2022-06-28")
    }

  @Test
  fun getNextRequestUrl_withLastUpdatedSyncParamProvided_shouldReturnUrlWithExactProvidedLastUpdatedSyncParam() =
    runBlockingTest {
      val downloadManager =
        ResourceParamsBasedDownloadWorkManager(
          mapOf(
            ResourceType.Patient to
              mapOf(
                SyncDataParams.LAST_UPDATED_KEY to "2022-06-28",
                SyncDataParams.SORT_KEY to "status"
              )
          ),
          TestResourceParamsBasedDownloadWorkManagerContext("2022-07-07")
        )
      val url = downloadManager.getNextRequest()?.let { (it as UrlRequest).url }
      assertThat(url).isEqualTo("Patient?_lastUpdated=2022-06-28&_sort=status")
    }

  @Test
  fun getNextRequestUrl_withLastUpdatedSyncParamHavingGtPrefix_shouldReturnUrlWithExactProvidedLastUpdatedSyncParam() =
    runBlockingTest {
      val downloadManager =
        ResourceParamsBasedDownloadWorkManager(
          mapOf(ResourceType.Patient to mapOf(SyncDataParams.LAST_UPDATED_KEY to "gt2022-06-28")),
          TestResourceParamsBasedDownloadWorkManagerContext("2022-07-07")
        )
      val url = downloadManager.getNextRequest()?.let { (it as UrlRequest).url }
      assertThat(url).isEqualTo("Patient?_lastUpdated=gt2022-06-28&_sort=_lastUpdated")
    }

  @Test
  fun getNextRequestUrl_withNullUpdatedTimeStamp_shouldReturnUrlWithoutLastUpdatedQueryParam() =
    runBlockingTest {
      val downloadManager =
        ResourceParamsBasedDownloadWorkManager(
          mapOf(ResourceType.Patient to mapOf(Patient.ADDRESS_CITY.paramName to "NAIROBI")),
          NoOpResourceParamsBasedDownloadWorkManagerContext
        )
      val actual = downloadManager.getNextRequest()?.let { (it as UrlRequest).url }
      assertThat(actual).isEqualTo("Patient?address-city=NAIROBI&_sort=_lastUpdated")
    }

  @Test
  fun getNextRequestUrl_withEmptyUpdatedTimeStamp_shouldReturnUrlWithoutLastUpdatedQueryParam() =
    runBlockingTest {
      val downloadManager =
        ResourceParamsBasedDownloadWorkManager(
          mapOf(ResourceType.Patient to mapOf(Patient.ADDRESS_CITY.paramName to "NAIROBI")),
          TestResourceParamsBasedDownloadWorkManagerContext("")
        )
      val actual = downloadManager.getNextRequest()?.let { (it as UrlRequest).url }
      assertThat(actual).isEqualTo("Patient?address-city=NAIROBI&_sort=_lastUpdated")
    }

  @Test
  fun `getSummaryRequestUrls should return resource summary urls`() = runBlockingTest {
    val downloadManager =
      ResourceParamsBasedDownloadWorkManager(
        mapOf(
          ResourceType.Patient to mapOf(Patient.ADDRESS_CITY.paramName to "NAIROBI"),
          ResourceType.Immunization to emptyMap(),
          ResourceType.Observation to emptyMap(),
        ),
        TestResourceParamsBasedDownloadWorkManagerContext("2022-03-20")
      )

    val urls = downloadManager.getSummaryRequestUrls()

    assertThat(urls.map { it.key })
      .containsExactly(ResourceType.Patient, ResourceType.Immunization, ResourceType.Observation)
    assertThat(urls.map { it.value })
      .containsExactly(
        "Patient?address-city=NAIROBI&_sort=_lastUpdated&_lastUpdated=gt2022-03-20&_summary=count",
        "Immunization?_sort=_lastUpdated&_lastUpdated=gt2022-03-20&_summary=count",
        "Observation?_sort=_lastUpdated&_lastUpdated=gt2022-03-20&_summary=count"
      )
  }

  @Test
  fun processResponse_withBundleTypeSearchSet_shouldReturnPatient() = runBlockingTest {
    val downloadManager =
      ResourceParamsBasedDownloadWorkManager(
        emptyMap(),
        NoOpResourceParamsBasedDownloadWorkManagerContext
      )
    val response =
      Bundle().apply {
        type = Bundle.BundleType.SEARCHSET
        addEntry(
          Bundle.BundleEntryComponent().apply {
            resource = Patient().apply { id = "Patient-Id-001" }
          }
        )
        addEntry(
          Bundle.BundleEntryComponent().apply {
            resource = Patient().apply { id = "Patient-Id-002" }
          }
        )
      }
    val resources = downloadManager.processResponse(response)
    assertThat(resources.map { it.logicalId }).containsExactly("Patient-Id-001", "Patient-Id-002")
  }

  @Test
  fun processResponse_withTransactionResponseBundle_shouldReturnEmptyList() = runBlockingTest {
    val downloadManager =
      ResourceParamsBasedDownloadWorkManager(
        emptyMap(),
        NoOpResourceParamsBasedDownloadWorkManagerContext
      )
    val response =
      Bundle().apply {
        type = Bundle.BundleType.TRANSACTIONRESPONSE
        addEntry(
          Bundle.BundleEntryComponent().apply {
            resource = Patient().apply { id = "Patient-Id-001" }
          }
        )
        addEntry(
          Bundle.BundleEntryComponent().apply {
            resource = Patient().apply { id = "Patient-Id-002" }
          }
        )
      }

    val resources = downloadManager.processResponse(response)
    assertThat(resources).hasSize(0)
  }

  @Test
  fun processResponse_withOperationOutcome_shouldThrowException() {
    val downloadManager =
      ResourceParamsBasedDownloadWorkManager(
        emptyMap(),
        NoOpResourceParamsBasedDownloadWorkManagerContext
      )
    val response =
      OperationOutcome().apply {
        addIssue(
          OperationOutcome.OperationOutcomeIssueComponent().apply {
            diagnostics = "Server couldn't fulfil the request."
          }
        )
      }

    val exception =
      assertThrows(FHIRException::class.java) {
        runBlockingTest { downloadManager.processResponse(response) }
      }
    assertThat(exception.localizedMessage).isEqualTo("Server couldn't fulfil the request.")
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
