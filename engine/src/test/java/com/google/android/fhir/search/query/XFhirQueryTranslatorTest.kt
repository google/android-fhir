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

package com.google.android.fhir.search.query

import android.os.Build
import com.google.android.fhir.index.SearchParamDefinition
import com.google.android.fhir.search.Order
import com.google.android.fhir.search.Search
import com.google.android.fhir.search.query.XFhirQueryTranslator.applyFilterParam
import com.google.android.fhir.search.query.XFhirQueryTranslator.applySortParam
import com.google.android.fhir.search.query.XFhirQueryTranslator.translate
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class XFhirQueryTranslatorTest {

  @Test
  fun translate_shouldAddDescendingSort_ForQueryString() {
    val search = translate("Patient?_sort=-name")

    assertThat(search.sort!!.paramName).isEqualTo("name")
    assertThat(search.order!!).isEqualTo(Order.DESCENDING)
  }

  @Test
  fun translate_shouldAddAscendingSort_ForQueryString() {
    val search = translate("Patient?_sort=address-country")

    assertThat(search.sort!!.paramName).isEqualTo("address-country")
    assertThat(search.order!!).isEqualTo(Order.ASCENDING)
  }

  @Test
  fun translate_shouldNotAddSort_WhenMissing() {
    val search = translate("Patient")

    assertThat(search.sort).isNull()
    assertThat(search.order).isNull()
  }

  @Test(expected = IllegalArgumentException::class)
  fun translate_shouldThrowException_ForUnrecognizedSortParam() {
    translate("Patient?_sort=customParam")
  }

  @Test
  fun translate_shouldAddLimit_ForQueryString() {
    val search = translate("Patient?_count=10")

    assertThat(search.count).isEqualTo(10)
  }

  @Test
  fun translate_shouldNotAddLimit_WhenMissing() {
    val search = translate("Patient")

    assertThat(search.count).isNull()
  }

  @Test
  fun translate_shouldAddFilters_ForQueryString() {
    val search =
      translate("Patient?gender=male&name=John&birthdate=2012-01-11&general-practitioner=12345")

    search.stringFilterCriteria.first().run {
      assertThat(this.parameter.paramName).isEqualTo("name")
      assertThat(this.filters.first().value).isEqualTo("John")
    }

    search.tokenFilterCriteria.first().run {
      assertThat(this.parameter.paramName).isEqualTo("gender")
      assertThat(this.filters.first().value!!.tokenFilters.first().code).isEqualTo("male")
    }
    search.dateTimeFilterCriteria.first().run {
      assertThat(this.parameter.paramName).isEqualTo("birthdate")
      assertThat(this.filters.first().value!!.date!!.toHumanDisplay()).isEqualTo("2012-01-11")
    }
    search.referenceFilterCriteria.first().run {
      assertThat(this.parameter.paramName).isEqualTo("general-practitioner")
      assertThat(this.filters.first().value).isEqualTo("12345")
    }
  }

  @Test(expected = IllegalArgumentException::class)
  fun translate_shouldThrowException_ForUnrecognizedFilterParam() {
    translate("Patient?customParam=Abc")
  }

  @Test
  fun applySortParam_shouldAddDslSort_forStringParam() {
    val search = Search(ResourceType.Patient)

    search.applySortParam(
      SearchParamDefinition(
        "address-country",
        Enumerations.SearchParamType.STRING,
        "Patient.address.country"
      )
    )

    assertThat(search.sort!!.paramName).isEqualTo("address-country")
  }

  @Test
  fun applySortParam_shouldAddDslSort_forNumberParam() {
    val search = Search(ResourceType.RiskAssessment)

    search.applySortParam(
      SearchParamDefinition(
        "probability",
        Enumerations.SearchParamType.NUMBER,
        "RiskAssessment.prediction.probability"
      )
    )

    assertThat(search.sort!!.paramName).isEqualTo("probability")
  }

  @Test
  fun applySortParam_shouldAddDslSort_forDateParam() {
    val search = Search(ResourceType.Patient)

    search.applySortParam(
      SearchParamDefinition("birthdate", Enumerations.SearchParamType.DATE, "Patient.birthDate")
    )

    assertThat(search.sort!!.paramName).isEqualTo("birthdate")
  }

  @Test(expected = UnsupportedOperationException::class)
  fun applySortParam_shouldThrowUnsupportedOperationException_forUnsupportedParam() {
    val search = Search(ResourceType.Patient)

    search.applySortParam(
      SearchParamDefinition(
        "deceased",
        Enumerations.SearchParamType.TOKEN,
        "Patient.deceased.exists() and Patient.deceased != false"
      )
    )
  }

  @Test
  fun applyFilterParam_shouldAddDslFilter_forNumberParam() {
    val search = Search(ResourceType.RiskAssessment)

    search.applyFilterParam(
      SearchParamDefinition(
        "probability",
        Enumerations.SearchParamType.NUMBER,
        "RiskAssessment.prediction.probability"
      ),
      "12"
    )

    val applyFilterParam = search.numberFilterCriteria.first().filters.first()
    assertThat(applyFilterParam.parameter.paramName).isEqualTo("probability")
    assertThat(applyFilterParam.value).isEqualTo(12.toBigDecimal())
  }

  @Test
  fun applyFilterParam_shouldAddDslFilter_forDateParam() {
    val search = Search(ResourceType.Patient)

    search.applyFilterParam(
      SearchParamDefinition("birthdate", Enumerations.SearchParamType.DATE, "Patient.birthDate"),
      "2022-01-21"
    )

    val applyFilterParam = search.dateTimeFilterCriteria.first().filters.first()
    assertThat(applyFilterParam.parameter.paramName).isEqualTo("birthdate")
    assertThat(applyFilterParam.value!!.date!!.toHumanDisplay()).isEqualTo("2022-01-21")
  }

  @Test
  fun applyFilterParam_shouldAddDslFilter_forDateTimeParam() {
    val search = Search(ResourceType.Patient)

    search.applyFilterParam(
      SearchParamDefinition("birthdate", Enumerations.SearchParamType.DATE, "Patient.birthDate"),
      "2022-01-21T12:21:59"
    )

    val applyFilterParam = search.dateTimeFilterCriteria.first().filters.first()
    assertThat(applyFilterParam.parameter.paramName).isEqualTo("birthdate")
    assertThat(applyFilterParam.value!!.dateTime!!.toHumanDisplay())
      .isEqualTo("Jan 21, 2022, 12:21:59 PM")
  }

  @Test
  fun applyFilterParam_shouldAddDslFilter_forQuantityParam() {
    val search = Search(ResourceType.Encounter)

    search.applyFilterParam(
      SearchParamDefinition("length", Enumerations.SearchParamType.QUANTITY, "Encounter.length"),
      "3|http://unitsofmeasure.org|months"
    )

    val applyFilterParam = search.quantityFilterCriteria.first().filters.first()
    assertThat(applyFilterParam.parameter.paramName).isEqualTo("length")
    assertThat(applyFilterParam.value).isEqualTo(3.0.toBigDecimal())
    assertThat(applyFilterParam.unit).isEqualTo("months")
    assertThat(applyFilterParam.system).isEqualTo("http://unitsofmeasure.org")
  }

  @Test
  fun applyFilterParam_shouldAddDslFilter_forStringParam() {
    val search = Search(ResourceType.Patient)

    search.applyFilterParam(
      SearchParamDefinition(
        "address-country",
        Enumerations.SearchParamType.STRING,
        "Patient.address.country"
      ),
      "Karachi"
    )

    val applyFilterParam = search.stringFilterCriteria.first().filters.first()
    assertThat(applyFilterParam.parameter.paramName).isEqualTo("address-country")
    assertThat(applyFilterParam.value).isEqualTo("Karachi")
  }

  @Test
  fun applyFilterParam_shouldAddDslFilter_forTokenParam() {
    val search = Search(ResourceType.Patient)

    search.applyFilterParam(
      SearchParamDefinition("identifier", Enumerations.SearchParamType.TOKEN, "Patient.identifier"),
      "http://snomed.org|001122"
    )

    val applyFilterParam = search.tokenFilterCriteria.first().filters.first()
    assertThat(applyFilterParam.parameter.paramName).isEqualTo("identifier")
    assertThat(applyFilterParam.value!!.tokenFilters.first().code).isEqualTo("001122")
    assertThat(applyFilterParam.value!!.tokenFilters.first().uri).isEqualTo("http://snomed.org")
  }

  @Test
  fun applyFilterParam_shouldAddDslFilter_forReferenceParam() {
    val search = Search(ResourceType.Patient)

    search.applyFilterParam(
      SearchParamDefinition(
        "general-practitioner",
        Enumerations.SearchParamType.REFERENCE,
        "Patient.generalPractitioner"
      ),
      "Practitioner/111"
    )

    val applyFilterParam = search.referenceFilterCriteria.first().filters.first()
    assertThat(applyFilterParam.parameter.paramName).isEqualTo("general-practitioner")
    assertThat(applyFilterParam.value).isEqualTo("Practitioner/111")
  }

  @Test
  fun applyFilterParam_shouldAddDslFilter_forUriParam() {
    val search = Search(ResourceType.Measure)

    search.applyFilterParam(
      SearchParamDefinition("url", Enumerations.SearchParamType.URI, "Measure.url"),
      "http://fhir.org/Measure/meaure-1"
    )

    val applyFilterParam = search.uriFilterCriteria.first().filters.first()
    assertThat(applyFilterParam.parameter.paramName).isEqualTo("url")
    assertThat(applyFilterParam.value).isEqualTo("http://fhir.org/Measure/meaure-1")
  }

  @Test(expected = UnsupportedOperationException::class)
  fun applyFilterParam_shouldThrowUnsupportedOperationException_forUnrecognizedParam() {
    val search = Search(ResourceType.Location)

    search.applyFilterParam(
      SearchParamDefinition("near", Enumerations.SearchParamType.SPECIAL, "Location.position"),
      "20.000839 30.378273"
    )
  }
}
