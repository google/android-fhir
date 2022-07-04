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

package com.google.android.fhir.search

import android.os.Build
import ca.uhn.fhir.rest.gclient.DateClientParam
import ca.uhn.fhir.rest.gclient.NumberClientParam
import ca.uhn.fhir.rest.gclient.StringClientParam
import com.google.android.fhir.index.SearchParamDefinition
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MoreSearchTest {

  @Test
  fun searchSort_shouldCallDslSort_forStringParam() = runBlocking {
    val search = spy(Search(ResourceType.Patient))

    search.sort(
      SearchParamDefinition(
        "address-country",
        Enumerations.SearchParamType.STRING,
        "Patient.address.country"
      )
    )

    val captor = argumentCaptor<StringClientParam>()
    verify(search).sort(captor.capture(), any())
    assertThat(captor.firstValue.paramName).isEqualTo("address-country")
  }

  @Test
  fun searchSort_shouldCallDslSort_forNumberParam() = runBlocking {
    val search = spy(Search(ResourceType.RiskAssessment))

    search.sort(
      SearchParamDefinition(
        "probability",
        Enumerations.SearchParamType.NUMBER,
        "RiskAssessment.prediction.probability"
      )
    )

    val captor = argumentCaptor<NumberClientParam>()
    verify(search).sort(captor.capture(), any())
    assertThat(captor.firstValue.paramName).isEqualTo("probability")
  }

  @Test
  fun searchSort_shouldCallDslSort_forDateParam() = runBlocking {
    val search = spy(Search(ResourceType.Patient))

    search.sort(
      SearchParamDefinition("birthdate", Enumerations.SearchParamType.DATE, "Patient.birthDate")
    )

    val captor = argumentCaptor<DateClientParam>()
    verify(search).sort(captor.capture(), any())
    assertThat(captor.firstValue.paramName).isEqualTo("birthdate")
  }

  @Test(expected = UnsupportedOperationException::class)
  fun searchSort_shouldThrowUnsupportedOperationException_forUnsupportedParam() = runBlocking {
    val search = spy(Search(ResourceType.Patient))

    search.sort(
      SearchParamDefinition(
        "deceased",
        Enumerations.SearchParamType.TOKEN,
        "Patient.deceased.exists() and Patient.deceased != false"
      )
    )
  }

  @Test
  fun searchFilter_shouldCallDslFilter_forNumberParam() = runBlocking {
    val search = spy(Search(ResourceType.RiskAssessment))

    search.filter(
      SearchParamDefinition(
        "probability",
        Enumerations.SearchParamType.NUMBER,
        "RiskAssessment.prediction.probability"
      ),
      "12"
    )

    val searchFilter = search.numberFilterCriteria.first().filters.first()
    assertThat(searchFilter.parameter.paramName).isEqualTo("probability")
    assertThat(searchFilter.value).isEqualTo(12.toBigDecimal())
  }

  @Test
  fun searchFilter_shouldCallDslFilter_forDateParam() = runBlocking {
    val search = spy(Search(ResourceType.Patient))

    search.filter(
      SearchParamDefinition("birthdate", Enumerations.SearchParamType.DATE, "Patient.birthDate"),
      "2022-01-21"
    )

    val searchFilter = search.dateTimeFilterCriteria.first().filters.first()
    assertThat(searchFilter.parameter.paramName).isEqualTo("birthdate")
    assertThat(searchFilter.value!!.date!!.toHumanDisplay()).isEqualTo("2022-01-21")
  }

  @Test
  fun searchFilter_shouldCallDslFilter_forDateTimeParam() = runBlocking {
    val search = spy(Search(ResourceType.Patient))

    search.filter(
      SearchParamDefinition("birthdate", Enumerations.SearchParamType.DATE, "Patient.birthDate"),
      "2022-01-21T12:21:59"
    )

    val searchFilter = search.dateTimeFilterCriteria.first().filters.first()
    assertThat(searchFilter.parameter.paramName).isEqualTo("birthdate")
    assertThat(searchFilter.value!!.dateTime!!.toHumanDisplay())
      .isEqualTo("Jan 21, 2022, 12:21:59 PM")
  }

  @Test
  fun searchFilter_shouldCallDslFilter_forQuantityParam() = runBlocking {
    val search = spy(Search(ResourceType.Encounter))

    search.filter(
      SearchParamDefinition("length", Enumerations.SearchParamType.QUANTITY, "Encounter.length"),
      "3|http://unitsofmeasure.org|months"
    )

    val searchFilter = search.quantityFilterCriteria.first().filters.first()
    assertThat(searchFilter.parameter.paramName).isEqualTo("length")
    assertThat(searchFilter.value).isEqualTo(3.toBigDecimal())
    assertThat(searchFilter.unit).isEqualTo("months")
    assertThat(searchFilter.system).isEqualTo("http://unitsofmeasure.org")
  }

  @Test
  fun searchFilter_shouldCallDslFilter_forStringParam() = runBlocking {
    val search = spy(Search(ResourceType.Patient))

    search.filter(
      SearchParamDefinition(
        "address-country",
        Enumerations.SearchParamType.STRING,
        "Patient.address.country"
      ),
      "Karachi"
    )

    val searchFilter = search.stringFilterCriteria.first().filters.first()
    assertThat(searchFilter.parameter.paramName).isEqualTo("address-country")
    assertThat(searchFilter.value).isEqualTo("Karachi")
  }

  @Test
  fun searchFilter_shouldCallDslFilter_forTokenParam() = runBlocking {
    val search = spy(Search(ResourceType.Patient))

    search.filter(
      SearchParamDefinition("identifier", Enumerations.SearchParamType.TOKEN, "Patient.identifier"),
      "http://snomed.org|001122"
    )

    val searchFilter = search.tokenFilterCriteria.first().filters.first()
    assertThat(searchFilter.parameter.paramName).isEqualTo("identifier")
    assertThat(searchFilter.value!!.tokenFilters.first().code).isEqualTo("001122")
    assertThat(searchFilter.value!!.tokenFilters.first().uri).isEqualTo("http://snomed.org")
  }

  @Test
  fun searchFilter_shouldCallDslFilter_forReferenceParam() = runBlocking {
    val search = spy(Search(ResourceType.Patient))

    search.filter(
      SearchParamDefinition(
        "general-practitioner",
        Enumerations.SearchParamType.REFERENCE,
        "Patient.generalPractitioner"
      ),
      "Practitioner/111"
    )

    val searchFilter = search.referenceFilterCriteria.first().filters.first()
    assertThat(searchFilter.parameter.paramName).isEqualTo("general-practitioner")
    assertThat(searchFilter.value).isEqualTo("Practitioner/111")
  }

  @Test
  fun searchFilter_shouldCallDslFilter_forUriParam() = runBlocking {
    val search = spy(Search(ResourceType.Measure))

    search.filter(
      SearchParamDefinition("url", Enumerations.SearchParamType.URI, "Measure.url"),
      "http://fhir.org/Measure/meaure-1"
    )

    val searchFilter = search.uriFilterCriteria.first().filters.first()
    assertThat(searchFilter.parameter.paramName).isEqualTo("url")
    assertThat(searchFilter.value).isEqualTo("http://fhir.org/Measure/meaure-1")
  }

  @Test(expected = UnsupportedOperationException::class)
  fun searchFilter_shouldThrowUnsupportedOperationException_forUnrecognizedParam() = runBlocking {
    val search = spy(Search(ResourceType.Location))

    search.filter(
      SearchParamDefinition("near", Enumerations.SearchParamType.SPECIAL, "Location.position"),
      "20.000839 30.378273"
    )
  }
}
