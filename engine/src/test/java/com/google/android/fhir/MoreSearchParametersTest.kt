/*
 * Copyright 2022-2023 Google LLC
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

package com.google.android.fhir

import com.google.android.fhir.index.SearchParamDefinition
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.SearchParameter
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MoreSearchParametersTest {
  @Test
  fun asMapOfResourceTypeToSearchParamDefinitions() {
    val familyNameSearchParameter =
      SearchParameter().apply {
        name = "family"
        expression = "Patient.name.family | Practitioner.name.family"
        addBase("Patient")
        addBase("Practitioner")
        type = Enumerations.SearchParamType.STRING
      }

    val genderSearchParameter =
      SearchParameter().apply {
        name = "gender"
        expression = "Patient.gender | Person.gender | Practitioner.gender | RelatedPerson.gender"
        addBase("Patient")
        addBase("Person")
        addBase("Practitioner")
        addBase("RelatedPerson")
        type = Enumerations.SearchParamType.TOKEN
      }

    val result =
      listOf(familyNameSearchParameter, genderSearchParameter)
        .asMapOfResourceTypeToSearchParamDefinitions()

    assertThat(result)
      .containsEntry(
        "Patient",
        listOf(
          SearchParamDefinition(
            name = "family",
            type = Enumerations.SearchParamType.STRING,
            path = "Patient.name.family",
          ),
          SearchParamDefinition(
            name = "gender",
            type = Enumerations.SearchParamType.TOKEN,
            path = "Patient.gender",
          ),
        ),
      )
    assertThat(result)
      .containsEntry(
        "Practitioner",
        listOf(
          SearchParamDefinition(
            name = "family",
            type = Enumerations.SearchParamType.STRING,
            path = "Practitioner.name.family",
          ),
          SearchParamDefinition(
            name = "gender",
            type = Enumerations.SearchParamType.TOKEN,
            path = "Practitioner.gender",
          ),
        ),
      )
    assertThat(result)
      .containsEntry(
        "Person",
        listOf(
          SearchParamDefinition(
            name = "gender",
            type = Enumerations.SearchParamType.TOKEN,
            path = "Person.gender",
          ),
        ),
      )
    assertThat(result)
      .containsEntry(
        "RelatedPerson",
        listOf(
          SearchParamDefinition(
            name = "gender",
            type = Enumerations.SearchParamType.TOKEN,
            path = "RelatedPerson.gender",
          ),
        ),
      )
  }

  @Test
  fun toSearchParamDefinition() {
    val familySearchParameter =
      SearchParameter().apply {
        name = "family"
        expression = "Patient.name.family | Practitioner.name.family"
        addBase("Patient")
        addBase("Practitioner")
        type = Enumerations.SearchParamType.STRING
      }

    val result = familySearchParameter.toSearchParamDefinition()
    assertThat(result)
      .containsExactly(
        "Patient" to
          SearchParamDefinition(
            name = "family",
            type = Enumerations.SearchParamType.STRING,
            path = "Patient.name.family",
          ),
        "Practitioner" to
          SearchParamDefinition(
            name = "family",
            type = Enumerations.SearchParamType.STRING,
            path = "Practitioner.name.family",
          ),
      )
  }

  @Test
  fun toSearchParamDefinition_throws_IllegalArgumentException_when_name_is_null_or_empty() {
    val nullNameSearchParameter =
      SearchParameter().apply {
        name = null
        expression = "Patient.name.family | Practitioner.name.family"
        addBase("Patient")
        addBase("Practitioner")
        type = Enumerations.SearchParamType.STRING
      }

    var exception =
      assertThrows(null, IllegalArgumentException::class.java) {
        nullNameSearchParameter.toSearchParamDefinition()
      }
    assertThat(exception.message).isEqualTo("SearchParameter.name can't be null or empty.")

    val emptyNameSearchParameter =
      SearchParameter().apply {
        name = ""
        expression = "Patient.name.family | Practitioner.name.family"
        addBase("Patient")
        addBase("Practitioner")
        type = Enumerations.SearchParamType.STRING
      }

    exception =
      assertThrows(null, IllegalArgumentException::class.java) {
        emptyNameSearchParameter.toSearchParamDefinition()
      }
    assertThat(exception.message).isEqualTo("SearchParameter.name can't be null or empty.")
  }

  @Test
  fun toSearchParamDefinition_throws_IllegalArgumentException_when_expression_is_null_or_empty() {
    val nullExpressionSearchParameter =
      SearchParameter().apply {
        name = "family"
        expression = null
        addBase("Patient")
        addBase("Practitioner")
        type = Enumerations.SearchParamType.STRING
      }

    var exception =
      assertThrows(null, IllegalArgumentException::class.java) {
        nullExpressionSearchParameter.toSearchParamDefinition()
      }
    assertThat(exception.message).isEqualTo("SearchParameter.expression can't be null or empty.")

    val emptyExpressionSearchParameter =
      SearchParameter().apply {
        name = "family"
        expression = ""
        addBase("Patient")
        addBase("Practitioner")
        type = Enumerations.SearchParamType.STRING
      }

    exception =
      assertThrows(null, IllegalArgumentException::class.java) {
        emptyExpressionSearchParameter.toSearchParamDefinition()
      }
    assertThat(exception.message).isEqualTo("SearchParameter.expression can't be null or empty.")
  }

  @Test
  fun toSearchParamDefinition_throws_IllegalArgumentException_when_type_is_null() {
    val nullTypeSearchParameter =
      SearchParameter().apply {
        name = "family"
        expression = "Patient.name.family | Practitioner.name.family"
        addBase("Patient")
        addBase("Practitioner")
        type = null
      }

    val exception =
      assertThrows(null, IllegalArgumentException::class.java) {
        nullTypeSearchParameter.toSearchParamDefinition()
      }
    assertThat(exception.message).isEqualTo("SearchParameter.type can't be null.")
  }
}
