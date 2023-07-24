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
    val search = translate("Patient?_sort=-name")

    assertThat(search.sort!!.paramName).isEqualTo("name")
    assertThat(search.order!!).isEqualTo(Order.DESCENDING)
  }

  @Test
  fun `translate() should add ascending sort for sort param`() {
    val search = translate("Patient?_sort=address-country")

    assertThat(search.sort!!.paramName).isEqualTo("address-country")
    assertThat(search.order!!).isEqualTo(Order.ASCENDING)
  }

  @Test
  fun `translate() should not add sort for missing value for sort param`() {
    val search = translate("Patient?_sort=")

    assertThat(search.sort).isNull()
    assertThat(search.order).isNull()
  }

  @Test
  fun `translate() should not add sort for missing sort param`() {
    val search = translate("Patient")

    assertThat(search.sort).isNull()
    assertThat(search.order).isNull()
  }

  @Test
  fun `translate() should throw IllegalArgumentException for unrecognized sort param`() {
    val exception =
      assertThrows(IllegalArgumentException::class.java) { translate("Patient?_sort=customParam") }
    assertThat(exception.message).isEqualTo("customParam not found in Patient")
  }

  @Test
  fun `translate() should add limit for count param`() {
    val search = translate("Patient?_count=10")

    assertThat(search.count).isEqualTo(10)
  }

  @Test
  fun `translate() should not add limit for missing value for count param`() {
    val search = translate("Patient?_count=")

    assertThat(search.count).isNull()
  }

  @Test
  fun `translate() should not add limit for missing count param`() {
    val search = translate("Patient")

    assertThat(search.count).isNull()
  }

  @Test
  fun `translate() should add filters`() {
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

  @Test
  fun `translate() should throw IllegalArgumentException for unrecognized filter param`() {
    val exception =
      assertThrows(IllegalArgumentException::class.java) { translate("Patient?customParam=Abc") }
    assertThat(exception.message).isEqualTo("customParam not found in Patient")
  }

  @Test
  fun `translate() should not add filters for missing value for filter param`() {
    val search = translate("Patient?gender=&name=&birthdate=&general-practitioner=")

    assertThat(search.stringFilterCriteria).isEmpty()

    assertThat(search.tokenFilterCriteria).isEmpty()
    assertThat(search.dateTimeFilterCriteria).isEmpty()
    assertThat(search.referenceFilterCriteria).isEmpty()
  }

  @Test
  fun `applySortParam() should add sort param for string type`() {
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
  fun `applySortParam() should add sort param for number type`() {
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
  fun `applySortParam() should add sort param for date type`() {
    val search = Search(ResourceType.Patient)

    search.applySortParam(
      SearchParamDefinition("birthdate", Enumerations.SearchParamType.DATE, "Patient.birthDate")
    )

    assertThat(search.sort!!.paramName).isEqualTo("birthdate")
  }

  @Test
  fun `applySortParam() should throw UnsupportedOperationException for unsupported type`() {
    val search = Search(ResourceType.Patient)

    val exception =
      assertThrows(UnsupportedOperationException::class.java) {
        search.applySortParam(
          SearchParamDefinition(
            "deceased",
            Enumerations.SearchParamType.TOKEN,
            "Patient.deceased.exists() and Patient.deceased != false"
          )
        )
      }
    assertThat(exception.message).isEqualTo("TOKEN sort not supported in x-fhir-query")
  }

  @Test
  fun `applyFilterParam() should add filter param for number type`() {
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
  fun `applyFilterParam() should add filter param for date type`() {
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
  fun `applyFilterParam() should add filter param for datetime type`() {
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
  fun `applyFilterParam() should add filter param for quantity type`() {
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
  fun `applyFilterParam() should add filter param for string type`() {
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
  fun `applyFilterParam() should add filter param for token type`() {
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
  fun `applyFilterParam() should add filter param for reference type`() {
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
  fun `applyFilterParam() should add filter param for uri type`() {
    val search = Search(ResourceType.Measure)

    search.applyFilterParam(
      SearchParamDefinition("url", Enumerations.SearchParamType.URI, "Measure.url"),
      "http://fhir.org/Measure/meaure-1"
    )

    val applyFilterParam = search.uriFilterCriteria.first().filters.first()
    assertThat(applyFilterParam.parameter.paramName).isEqualTo("url")
    assertThat(applyFilterParam.value).isEqualTo("http://fhir.org/Measure/meaure-1")
  }

  @Test
  fun `applyFilterParam() should throw UnsupportedOperationException for unrecognized type`() {
    val search = Search(ResourceType.Location)

    val exception =
      assertThrows(UnsupportedOperationException::class.java) {
        search.applyFilterParam(
          SearchParamDefinition("near", Enumerations.SearchParamType.SPECIAL, "Location.position"),
          "20.000839 30.378273"
        )
      }
    assertThat(exception.message).isEqualTo("SPECIAL type not supported in x-fhir-query")
  }

  @Test
  fun `translate() should add a filter for search parameter _tag`() {
    val search = translate("Location?_tag=salima-catchment")

    search.tokenFilterCriteria.first().run {
      assertThat(this.parameter.paramName).isEqualTo("_tag")
      assertThat(this.filters.first().value!!.tokenFilters.first().code)
        .isEqualTo("salima-catchment")
    }
  }

  @Test
  fun `translate() should add a filter for search parameter _profile`() {

    val search =
      translate("Patient?_profile=http://hl7.org/fhir/us/core/StructureDefinition/us-core-patient")

    search.uriFilterCriteria.first().run {
      assertThat(this.parameter.paramName).isEqualTo("_profile")
      assertThat(this.filters.first().value)
        .isEqualTo("http://hl7.org/fhir/us/core/StructureDefinition/us-core-patient")
    }
  }
}
