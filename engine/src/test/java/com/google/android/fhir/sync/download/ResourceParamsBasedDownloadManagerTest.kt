/*
 * Copyright 2021 Google LLC
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
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.OperationOutcome
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ResourceParamsBasedDownloadManagerTest {

  private lateinit var downloadManager: ResourceParamsBasedDownloadManager

  @Before
  fun setup() {
    downloadManager =
      ResourceParamsBasedDownloadManager(
        linkedMapOf(
          ResourceType.Patient to mapOf(Patient.ADDRESS_CITY.paramName to "NAIROBI"),
          ResourceType.Immunization to emptyMap(),
          ResourceType.Observation to emptyMap(),
        )
      )
  }

  @Test
  fun getInitialUrl_shouldReturnPatientUrl() {
    val initialUrl = downloadManager.getInitialUrl()
    assertThat(initialUrl).isEqualTo("Patient?address-city=NAIROBI&_sort=_lastUpdated")
  }

  @Test
  fun getInitialUrl_emptySyncParamsShouldThrowException() {
    val downloadManager = ResourceParamsBasedDownloadManager(linkedMapOf())
    val exception =
      assertThrows(IllegalStateException::class.java) { downloadManager.getInitialUrl() }
    assertThat(exception.localizedMessage)
      .isEqualTo("No resource sync params provided to create the url")
  }

  @Test
  fun createDownloadUrl_shouldAddLastUpdatedInUrl() {
    val initialUrl = "Patient?address-city=NAIROBI&_sort=_lastUpdated"
    val processedUrl = downloadManager.createDownloadUrl(initialUrl, "2022-01-01T05:30:00+5:30")
    assertThat(processedUrl)
      .isEqualTo(
        "Patient?address-city=NAIROBI&_sort=_lastUpdated&_lastUpdated=gt2022-01-01T05:30:00+5:30"
      )
  }

  @Test
  fun createDownloadUrl_shouldBeSameAsInitialUrlIfNoLastUpdate() {
    val initialUrl = "Patient?address-city=NAIROBI&_sort=_lastUpdated"
    val processedUrlWithNullLastUpdate = downloadManager.createDownloadUrl(initialUrl, null)
    val processedUrlWithEmptyLastUpdate = downloadManager.createDownloadUrl(initialUrl, "")
    assertThat(processedUrlWithNullLastUpdate)
      .isEqualTo("Patient?address-city=NAIROBI&_sort=_lastUpdated")
    assertThat(processedUrlWithEmptyLastUpdate)
      .isEqualTo("Patient?address-city=NAIROBI&_sort=_lastUpdated")
  }

  @Test
  fun extractResourcesFromResponse_withBundleTypeSearchSet_shouldReturnPatient() {
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

    val resources = downloadManager.extractResourcesFromResponse(response)
    assertThat(resources).hasSize(2)
    assertThat(resources.map { it.logicalId }).containsExactly("Patient-Id-001", "Patient-Id-002")
  }

  @Test
  fun extractResourcesFromResponse_withResourceOtherThanBundleTypeSearchSet_shouldReturnEmptyList() {
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

    val resources = downloadManager.extractResourcesFromResponse(response)
    assertThat(resources).hasSize(0)
  }

  @Test
  fun extractResourcesFromResponse_withOperationOutcome_shouldThrowException() {
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
        downloadManager.extractResourcesFromResponse(response)
      }
    assertThat(exception.localizedMessage).isEqualTo("Server couldn't fulfil the request.")
  }

  @Test
  fun extractNextUrlsFromResource_withNextUrl_shouldReturnLinkedUrl() {
    val response =
      Bundle().apply {
        type = Bundle.BundleType.SEARCHSET
        addLink(
          Bundle.BundleLinkComponent().apply {
            relation = "next"
            url =
              "http://prod-fhir-server.com/_getpages=6b7c270f-bdd3-4285-9deb-251999f58513&_getpagesoffset=20&_count=20&_bundletype=searchset"
          }
        )
      }
    val nextUrls = downloadManager.extractNextUrlsFromResource(response)
    assertThat(nextUrls)
      .containsExactly(
        "http://prod-fhir-server.com/_getpages=6b7c270f-bdd3-4285-9deb-251999f58513&_getpagesoffset=20&_count=20&_bundletype=searchset"
      )
  }

  @Test
  fun extractNextUrlsFromResource_withNoNextUrl_shouldReturnNextResourceUrl() {
    val downloadManager =
      ResourceParamsBasedDownloadManager(
        linkedMapOf(
          ResourceType.Patient to mapOf(Patient.ADDRESS_CITY.paramName to "NAIROBI"),
          ResourceType.Observation to emptyMap(),
          ResourceType.Immunization to emptyMap(),
        )
      )
    val response =
      Bundle().apply {
        type = Bundle.BundleType.SEARCHSET
        addEntry(
          Bundle.BundleEntryComponent().apply {
            resource = Patient().apply { id = "Patient-Id-001" }
          }
        )
      }

    val initialUrl = downloadManager.getInitialUrl()
    assertThat(initialUrl).isEqualTo("Patient?address-city=NAIROBI&_sort=_lastUpdated")
    var nextUrls = downloadManager.extractNextUrlsFromResource(response)
    assertThat(nextUrls).containsExactly("Observation?_sort=_lastUpdated")
    nextUrls = downloadManager.extractNextUrlsFromResource(response)
    assertThat(nextUrls).containsExactly("Immunization?_sort=_lastUpdated")
  }

  @Test
  fun extractNextUrlsFromResource_withNoNextUrlOrResource_shouldReturnEmptyList() {
    val downloadManager =
      ResourceParamsBasedDownloadManager(
        linkedMapOf(
          ResourceType.Patient to mapOf(Patient.ADDRESS_CITY.paramName to "NAIROBI"),
        )
      )
    val response =
      Bundle().apply {
        type = Bundle.BundleType.SEARCHSET
        addEntry(
          Bundle.BundleEntryComponent().apply {
            resource = Patient().apply { id = "Patient-Id-001" }
          }
        )
      }

    val initialUrl = downloadManager.getInitialUrl()
    assertThat(initialUrl).isEqualTo("Patient?address-city=NAIROBI&_sort=_lastUpdated")
    var nextUrls = downloadManager.extractNextUrlsFromResource(response)
    assertThat(nextUrls).isEmpty()
    nextUrls = downloadManager.extractNextUrlsFromResource(response)
    assertThat(nextUrls).isEmpty()
  }
}
