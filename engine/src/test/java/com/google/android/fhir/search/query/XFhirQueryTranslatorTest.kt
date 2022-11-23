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
import com.google.android.fhir.index.SearchParamType
import com.google.android.fhir.search.Order
import com.google.android.fhir.search.Search
import com.google.android.fhir.search.SearchManagerForR4Test
import com.google.android.fhir.search.query.XFhirQueryTranslator.applyFilterParam
import com.google.android.fhir.search.query.XFhirQueryTranslator.applySortParam
import com.google.android.fhir.search.query.XFhirQueryTranslator.translate
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class XFhirQueryTranslatorTest {

  @Test
  fun `translate() should add descending sort for sort param with hyphen`() {
    val search = translate("Patient?_sort=-name", searchManager = SearchManagerForR4Test)

    assertThat(search.sort!!.paramName).isEqualTo("name")
    assertThat(search.order!!).isEqualTo(Order.DESCENDING)
  }

  @Test
  fun `translate() should add ascending sort for sort param`() {
    val search = translate("Patient?_sort=address-country", searchManager = SearchManagerForR4Test)

    assertThat(search.sort!!.paramName).isEqualTo("address-country")
    assertThat(search.order!!).isEqualTo(Order.ASCENDING)
  }

  @Test
  fun `translate() should not add sort for missing value for sort param`() {
    val search = translate("Patient?_sort=", searchManager = SearchManagerForR4Test)

    assertThat(search.sort).isNull()
    assertThat(search.order).isNull()
  }

  @Test
  fun `translate() should not add sort for missing sort param`() {
    val search = translate("Patient", searchManager = SearchManagerForR4Test)

    assertThat(search.sort).isNull()
    assertThat(search.order).isNull()
  }

  @Test
  fun `translate() should throw IllegalArgumentException for unrecognized sort param`() {
    val exception =
      assertThrows(IllegalArgumentException::class.java) {
        translate("Patient?_sort=customParam", searchManager = SearchManagerForR4Test)
      }
    assertThat(exception.message).isEqualTo("customParam not found in Patient")
  }

  @Test
  fun `translate() should add limit for count param`() {
    val search = translate("Patient?_count=10", searchManager = SearchManagerForR4Test)

    assertThat(search.count).isEqualTo(10)
  }

  @Test
  fun `translate() should not add limit for missing value for count param`() {
    val search = translate("Patient?_count=", searchManager = SearchManagerForR4Test)

    assertThat(search.count).isNull()
  }

  @Test
  fun `translate() should not add limit for missing count param`() {
    val search = translate("Patient", searchManager = SearchManagerForR4Test)

    assertThat(search.count).isNull()
  }

  @Test
  fun `translate() should add filters`() {
    val search =
      translate(
        "Patient?gender=male&name=John&birthdate=2012-01-11&general-practitioner=12345",
        searchManager = SearchManagerForR4Test
      )

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
      assertThat(DateType(this.filters.first().value!!.date!!.value).toHumanDisplay())
        .isEqualTo("2012-01-11")
    }
    search.referenceFilterCriteria.first().run {
      assertThat(this.parameter.paramName).isEqualTo("general-practitioner")
      assertThat(this.filters.first().value).isEqualTo("12345")
    }
  }

  @Test
  fun `translate() should throw IllegalArgumentException for unrecognized filter param`() {
    val exception =
      assertThrows(IllegalArgumentException::class.java) {
        translate("Patient?customParam=Abc", searchManager = SearchManagerForR4Test)
      }
    assertThat(exception.message).isEqualTo("customParam not found in Patient")
  }

  @Test
  fun `translate() should not add filters for missing value for filter param`() {
    val search =
      translate(
        "Patient?gender=&name=&birthdate=&general-practitioner=",
        searchManager = SearchManagerForR4Test
      )

    assertThat(search.stringFilterCriteria).isEmpty()

    assertThat(search.tokenFilterCriteria).isEmpty()
    assertThat(search.dateTimeFilterCriteria).isEmpty()
    assertThat(search.referenceFilterCriteria).isEmpty()
  }

  @Test
  fun `applySortParam() should add sort param for string type`() {
    val search = Search(ResourceType.Patient.name)

    search.applySortParam(
      SearchParamDefinition("address-country", SearchParamType.STRING, "Patient.address.country")
    )

    assertThat(search.sort!!.paramName).isEqualTo("address-country")
  }

  @Test
  fun `applySortParam() should add sort param for number type`() {
    val search = Search(ResourceType.RiskAssessment.name)

    search.applySortParam(
      SearchParamDefinition(
        "probability",
        SearchParamType.NUMBER,
        "RiskAssessment.prediction.probability"
      )
    )

    assertThat(search.sort!!.paramName).isEqualTo("probability")
  }

  @Test
  fun `applySortParam() should add sort param for date type`() {
    val search = Search(ResourceType.Patient.name)

    search.applySortParam(
      SearchParamDefinition("birthdate", SearchParamType.DATE, "Patient.birthDate")
    )

    assertThat(search.sort!!.paramName).isEqualTo("birthdate")
  }

  @Test
  fun `applySortParam() should throw UnsupportedOperationException for unsupported type`() {
    val search = Search(ResourceType.Patient.name)

    val exception =
      assertThrows(UnsupportedOperationException::class.java) {
        search.applySortParam(
          SearchParamDefinition(
            "deceased",
            SearchParamType.TOKEN,
            "Patient.deceased.exists() and Patient.deceased != false"
          )
        )
      }
    assertThat(exception.message).isEqualTo("TOKEN sort not supported in x-fhir-query")
  }

  @Test
  fun `applyFilterParam() should add filter param for number type`() {
    val search = Search(ResourceType.RiskAssessment.name)

    search.applyFilterParam(
      SearchParamDefinition(
        "probability",
        SearchParamType.NUMBER,
        "RiskAssessment.prediction.probability"
      ),
      "12",
      SearchManagerForR4Test
    )

    val applyFilterParam = search.numberFilterCriteria.first().filters.first()
    assertThat(applyFilterParam.parameter.paramName).isEqualTo("probability")
    assertThat(applyFilterParam.value).isEqualTo(12.toBigDecimal())
  }

  @Test
  fun `applyFilterParam() should add filter param for date type`() {
    val search = Search(ResourceType.Patient.name)

    search.applyFilterParam(
      SearchParamDefinition("birthdate", SearchParamType.DATE, "Patient.birthDate"),
      "2022-01-21",
      searchManager = SearchManagerForR4Test
    )

    val applyFilterParam = search.dateTimeFilterCriteria.first().filters.first()
    assertThat(applyFilterParam.parameter.paramName).isEqualTo("birthdate")
    assertThat(DateType(applyFilterParam.value!!.date!!.value).toHumanDisplay())
      .isEqualTo("2022-01-21")
  }

  @Test
  fun `applyFilterParam() should add filter param for datetime type`() {
    val search = Search(ResourceType.Patient.name)

    search.applyFilterParam(
      SearchParamDefinition("birthdate", SearchParamType.DATE, "Patient.birthDate"),
      "2022-01-21T12:21:59",
      SearchManagerForR4Test
    )

    val applyFilterParam = search.dateTimeFilterCriteria.first().filters.first()
    assertThat(applyFilterParam.parameter.paramName).isEqualTo("birthdate")
    assertThat(DateTimeType(applyFilterParam.value!!.dateTime!!.value).toHumanDisplay())
      .isEqualTo("Jan 21, 2022, 12:21:59 PM")
  }

  @Test
  fun `applyFilterParam() should add filter param for quantity type`() {
    val search = Search(ResourceType.Encounter.name)

    search.applyFilterParam(
      SearchParamDefinition("length", SearchParamType.QUANTITY, "Encounter.length"),
      "3|http://unitsofmeasure.org|months",
      SearchManagerForR4Test
    )

    val applyFilterParam = search.quantityFilterCriteria.first().filters.first()
    assertThat(applyFilterParam.parameter.paramName).isEqualTo("length")
    assertThat(applyFilterParam.value).isEqualTo(3.0.toBigDecimal())
    assertThat(applyFilterParam.unit).isEqualTo("months")
    assertThat(applyFilterParam.system).isEqualTo("http://unitsofmeasure.org")
  }

  @Test
  fun `applyFilterParam() should add filter param for string type`() {
    val search = Search(ResourceType.Patient.name)

    search.applyFilterParam(
      SearchParamDefinition("address-country", SearchParamType.STRING, "Patient.address.country"),
      "Karachi",
      SearchManagerForR4Test
    )

    val applyFilterParam = search.stringFilterCriteria.first().filters.first()
    assertThat(applyFilterParam.parameter.paramName).isEqualTo("address-country")
    assertThat(applyFilterParam.value).isEqualTo("Karachi")
  }

  @Test
  fun `applyFilterParam() should add filter param for token type`() {
    val search = Search(ResourceType.Patient.name)

    search.applyFilterParam(
      SearchParamDefinition("identifier", SearchParamType.TOKEN, "Patient.identifier"),
      "http://snomed.org|001122",
      SearchManagerForR4Test
    )

    val applyFilterParam = search.tokenFilterCriteria.first().filters.first()
    assertThat(applyFilterParam.parameter.paramName).isEqualTo("identifier")
    assertThat(applyFilterParam.value!!.tokenFilters.first().code).isEqualTo("001122")
    assertThat(applyFilterParam.value!!.tokenFilters.first().uri).isEqualTo("http://snomed.org")
  }

  @Test
  fun `applyFilterParam() should add filter param for reference type`() {
    val search = Search(ResourceType.Patient.name)

    search.applyFilterParam(
      SearchParamDefinition(
        "general-practitioner",
        SearchParamType.REFERENCE,
        "Patient.generalPractitioner"
      ),
      "Practitioner/111",
      SearchManagerForR4Test
    )

    val applyFilterParam = search.referenceFilterCriteria.first().filters.first()
    assertThat(applyFilterParam.parameter.paramName).isEqualTo("general-practitioner")
    assertThat(applyFilterParam.value).isEqualTo("Practitioner/111")
  }

  @Test
  fun `applyFilterParam() should add filter param for uri type`() {
    val search = Search(ResourceType.Measure.name)

    search.applyFilterParam(
      SearchParamDefinition("url", SearchParamType.URI, "Measure.url"),
      "http://fhir.org/Measure/meaure-1",
      SearchManagerForR4Test
    )

    val applyFilterParam = search.uriFilterCriteria.first().filters.first()
    assertThat(applyFilterParam.parameter.paramName).isEqualTo("url")
    assertThat(applyFilterParam.value).isEqualTo("http://fhir.org/Measure/meaure-1")
  }

  @Test
  fun `applyFilterParam() should throw UnsupportedOperationException for unrecognized type`() {
    val search = Search(ResourceType.Location.name)

    val exception =
      assertThrows(UnsupportedOperationException::class.java) {
        search.applyFilterParam(
          SearchParamDefinition("near", SearchParamType.SPECIAL, "Location.position"),
          "20.000839 30.378273",
          SearchManagerForR4Test
        )
      }
    assertThat(exception.message).isEqualTo("SPECIAL type not supported in x-fhir-query")
  }
}
