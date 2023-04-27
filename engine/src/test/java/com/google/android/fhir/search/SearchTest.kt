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
import ca.uhn.fhir.model.api.TemporalPrecisionEnum
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import com.google.android.fhir.DateProvider
import com.google.android.fhir.epochDay
import com.google.common.truth.Truth.assertThat
import java.math.BigDecimal
import java.time.Instant
import java.util.Date
import kotlin.math.absoluteValue
import kotlin.math.roundToLong
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.CodeType
import org.hl7.fhir.r4.model.CodeableConcept
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.ContactPoint
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.DateType
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.Immunization
import org.hl7.fhir.r4.model.Library
import org.hl7.fhir.r4.model.Observation
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.RiskAssessment
import org.hl7.fhir.r4.model.UriType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Unit tests for [MoreSearch]. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SearchTest {

  @Test
  fun search() = runBlocking {
    val query = Search(ResourceType.Patient).getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        """.trimIndent()
      )
    assertThat(query.args).isEqualTo(listOf(ResourceType.Patient.name))
  }

  @Test
  fun count() = runBlocking {
    val query = Search(ResourceType.Patient).getQuery(true)

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT COUNT(*)
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        """.trimIndent()
      )
    assertThat(query.args).isEqualTo(listOf(ResourceType.Patient.name))
  }

  @Test
  fun search_size() {
    val query = Search(ResourceType.Patient).apply { count = 10 }.getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        LIMIT ?
        """.trimIndent()
      )
    assertThat(query.args).isEqualTo(listOf(ResourceType.Patient.name, 10))
  }

  @Test
  fun search_size_from() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          count = 10
          from = 20
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        LIMIT ? OFFSET ?
        """.trimIndent()
      )
    assertThat(query.args).isEqualTo(listOf(ResourceType.Patient.name, 10, 20))
  }

  @Test
  fun search_filter_date_approximate() {
    val mockDateType = DateType(Date(mockEpochTimeStamp), TemporalPrecisionEnum.DAY)
    DateProvider(Instant.ofEpochMilli(mockEpochTimeStamp))
    val value = DateType("2013-03-14")
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.BIRTHDATE,
            {
              this.value = of(value)
              prefix = ParamPrefixEnum.APPROXIMATE
            }
          )
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM DateIndexEntity
        WHERE resourceType = ? AND index_name = ? AND (index_from BETWEEN ? AND ? AND index_to BETWEEN ? AND ?)
        )
        """.trimIndent()
      )

    val diffStart =
      (value.rangeEpochDays.first -
          APPROXIMATION_COEFFICIENT *
            (value.rangeEpochDays.first - mockDateType.rangeEpochDays.first).absoluteValue)
        .roundToLong()
    val diffEnd =
      (value.rangeEpochDays.last +
          APPROXIMATION_COEFFICIENT *
            (value.rangeEpochDays.last - mockDateType.rangeEpochDays.last).absoluteValue)
        .roundToLong()
    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.BIRTHDATE.paramName,
          diffStart,
          diffEnd,
          diffStart,
          diffEnd
        )
      )
  }

  @Test
  fun search_filter_date_starts_after() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.BIRTHDATE,
            {
              prefix = ParamPrefixEnum.STARTS_AFTER
              value = of(DateType("2013-03-14"))
            }
          )
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM DateIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_from > ?
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.BIRTHDATE.paramName,
          DateTimeType("2013-03-14").value.epochDay
        )
      )
  }

  @Test
  fun search_filter_date_ends_before() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.BIRTHDATE,
            {
              value = of(DateType("2013-03-14"))
              prefix = ParamPrefixEnum.ENDS_BEFORE
            }
          )
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM DateIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_to < ?
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.BIRTHDATE.paramName,
          DateTimeType("2013-03-14").value.epochDay
        )
      )
  }

  @Test
  fun search_filter_date_not_equal() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.BIRTHDATE,
            {
              value = of(DateType("2013-03-14"))
              prefix = ParamPrefixEnum.NOT_EQUAL
            }
          )
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM DateIndexEntity
        WHERE resourceType = ? AND index_name = ? AND (index_from NOT BETWEEN ? AND ? OR index_to NOT BETWEEN ? AND ?)
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.BIRTHDATE.paramName,
          DateType("2013-03-14").value.epochDay,
          DateType("2013-03-14").value.epochDay,
          DateType("2013-03-14").value.epochDay,
          DateType("2013-03-14").value.epochDay
        )
      )
  }

  @Test
  fun search_filter_date_equal() {
    val query =
      Search(ResourceType.Patient)
        .apply { filter(Patient.BIRTHDATE, { value = of(DateType("2013-03-14")) }) }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM DateIndexEntity
        WHERE resourceType = ? AND index_name = ? AND (index_from BETWEEN ? AND ? AND index_to BETWEEN ? AND ?)
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.BIRTHDATE.paramName,
          DateType("2013-03-14").value.epochDay,
          DateType("2013-03-14").value.epochDay,
          DateType("2013-03-14").value.epochDay,
          DateType("2013-03-14").value.epochDay
        )
      )
  }

  @Test
  fun search_filter_date_greater() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.BIRTHDATE,
            {
              value = of(DateType("2013-03-14"))
              prefix = ParamPrefixEnum.GREATERTHAN
            }
          )
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM DateIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_to > ?
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.BIRTHDATE.paramName,
          DateTimeType("2013-03-14").value.epochDay
        )
      )
  }

  @Test
  fun search_filter_date_greaterOrEqual() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.BIRTHDATE,
            {
              value = of(DateType("2013-03-14"))
              prefix = ParamPrefixEnum.GREATERTHAN_OR_EQUALS
            }
          )
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM DateIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_to >= ?
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.BIRTHDATE.paramName,
          DateTimeType("2013-03-14").value.epochDay
        )
      )
  }

  @Test
  fun search_filter_date_less() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.BIRTHDATE,
            {
              value = of(DateType("2013-03-14"))
              prefix = ParamPrefixEnum.LESSTHAN
            }
          )
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM DateIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_from < ?
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.BIRTHDATE.paramName,
          DateType("2013-03-14").value.epochDay
        )
      )
  }

  @Test
  fun search_filter_date_lessOrEqual() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.BIRTHDATE,
            {
              value = of(DateType("2013-03-14"))
              prefix = ParamPrefixEnum.LESSTHAN_OR_EQUALS
            }
          )
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM DateIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_from <= ?
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.BIRTHDATE.paramName,
          DateType("2013-03-14").value.epochDay
        )
      )
  }

  @Test
  fun search_filter_dateTime_approximate() {
    val mockDateTimeType =
      DateTimeType(Date.from(Instant.ofEpochMilli(mockEpochTimeStamp)), TemporalPrecisionEnum.DAY)
    DateProvider(Instant.ofEpochMilli(mockEpochTimeStamp))
    val value = DateTimeType("2013-03-14")

    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.BIRTHDATE,
            {
              this.value = of(value)
              prefix = ParamPrefixEnum.APPROXIMATE
            }
          )
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM DateTimeIndexEntity
        WHERE resourceType = ? AND index_name = ? AND (index_from BETWEEN ? AND ? AND index_to BETWEEN ? AND ?)
        )
        """.trimIndent()
      )

    val diffStart =
      (value.rangeEpochMillis.first -
          APPROXIMATION_COEFFICIENT *
            (value.rangeEpochMillis.first - mockDateTimeType.rangeEpochMillis.first).absoluteValue)
        .roundToLong()
    val diffEnd =
      (value.rangeEpochMillis.last +
          APPROXIMATION_COEFFICIENT *
            (value.rangeEpochMillis.last - mockDateTimeType.rangeEpochMillis.last).absoluteValue)
        .roundToLong()

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.BIRTHDATE.paramName,
          diffStart,
          diffEnd,
          diffStart,
          diffEnd
        )
      )
  }

  @Test
  fun search_filter_dateTime_starts_after() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.BIRTHDATE,
            {
              value = of(DateTimeType("2013-03-14"))
              prefix = ParamPrefixEnum.STARTS_AFTER
            }
          )
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM DateTimeIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_from > ?
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.BIRTHDATE.paramName,
          DateTimeType("2013-03-15").value.time - 1
        )
      )
  }

  @Test
  fun search_filter_dateTime_ends_before() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.BIRTHDATE,
            {
              value = of(DateTimeType("2013-03-14"))
              prefix = ParamPrefixEnum.ENDS_BEFORE
            }
          )
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM DateTimeIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_to < ?
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.BIRTHDATE.paramName,
          DateTimeType("2013-03-14").value.time
        )
      )
  }

  @Test
  fun search_filter_dateTime_not_equal() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.BIRTHDATE,
            {
              value = of(DateTimeType("2013-03-14"))
              prefix = ParamPrefixEnum.NOT_EQUAL
            }
          )
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM DateTimeIndexEntity
        WHERE resourceType = ? AND index_name = ? AND (index_from NOT BETWEEN ? AND ? OR index_to NOT BETWEEN ? AND ?)
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.BIRTHDATE.paramName,
          DateTimeType("2013-03-14").value.time,
          DateTimeType("2013-03-15").value.time - 1,
          DateTimeType("2013-03-14").value.time,
          DateTimeType("2013-03-15").value.time - 1
        )
      )
  }

  @Test
  fun search_filter_dateTime_equal() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.BIRTHDATE,
            {
              value = of(DateTimeType("2013-03-14"))
              prefix = ParamPrefixEnum.EQUAL
            }
          )
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM DateTimeIndexEntity
        WHERE resourceType = ? AND index_name = ? AND (index_from BETWEEN ? AND ? AND index_to BETWEEN ? AND ?)
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.BIRTHDATE.paramName,
          DateTimeType("2013-03-14").value.time,
          DateTimeType("2013-03-15").value.time - 1,
          DateTimeType("2013-03-14").value.time,
          DateTimeType("2013-03-15").value.time - 1
        )
      )
  }

  @Test
  fun search_filter_dateTime_greater() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.BIRTHDATE,
            {
              value = of(DateTimeType("2013-03-14"))
              prefix = ParamPrefixEnum.GREATERTHAN
            }
          )
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM DateTimeIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_to > ?
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.BIRTHDATE.paramName,
          DateTimeType("2013-03-15").value.time - 1
        )
      )
  }

  @Test
  fun search_filter_dateTime_greaterOrEqual() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.BIRTHDATE,
            {
              value = of(DateTimeType("2013-03-14"))
              prefix = ParamPrefixEnum.GREATERTHAN_OR_EQUALS
            }
          )
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM DateTimeIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_to >= ?
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.BIRTHDATE.paramName,
          DateTimeType("2013-03-14").value.time
        )
      )
  }

  @Test
  fun search_filter_dateTime_less() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.BIRTHDATE,
            {
              value = of(DateTimeType("2013-03-14"))
              prefix = ParamPrefixEnum.LESSTHAN
            }
          )
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM DateTimeIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_from < ?
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.BIRTHDATE.paramName,
          DateTimeType("2013-03-14").value.time
        )
      )
  }

  @Test
  fun search_filter_dateTime_lessOrEqual() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.BIRTHDATE,
            {
              value = of(DateTimeType("2013-03-14"))
              prefix = ParamPrefixEnum.LESSTHAN_OR_EQUALS
            }
          )
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM DateTimeIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_from <= ?
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.BIRTHDATE.paramName,
          DateTimeType("2013-03-15").value.time - 1
        )
      )
  }

  @Test
  fun search_filter_string_default() {
    val query =
      Search(ResourceType.Patient)
        .apply { filter(Patient.ADDRESS, { value = "someValue" }) }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM StringIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value LIKE ? || '%' COLLATE NOCASE
        )
        """.trimIndent()
      )
    assertThat(query.args)
      .containsExactly(
        ResourceType.Patient.name,
        ResourceType.Patient.name,
        Patient.ADDRESS.paramName,
        "someValue"
      )
  }

  @Test
  fun search_filter_string_exact() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.ADDRESS,
            {
              modifier = StringFilterModifier.MATCHES_EXACTLY
              value = "someValue"
            }
          )
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM StringIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value = ?
        )
        """.trimIndent()
      )
    assertThat(query.args)
      .containsExactly(
        ResourceType.Patient.name,
        ResourceType.Patient.name,
        Patient.ADDRESS.paramName,
        "someValue"
      )
  }

  @Test
  fun search_filter_string_contains() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.ADDRESS,
            {
              modifier = StringFilterModifier.CONTAINS
              value = "someValue"
            }
          )
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM StringIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value LIKE '%' || ? || '%' COLLATE NOCASE
        )
        """.trimIndent()
      )
    assertThat(query.args)
      .containsExactly(
        ResourceType.Patient.name,
        ResourceType.Patient.name,
        Patient.ADDRESS.paramName,
        "someValue"
      )
  }

  @Test
  fun search_filter_token_coding() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.GENDER,
            {
              value =
                of(Coding("http://hl7.org/fhir/ValueSet/administrative-gender", "male", "Male"))
            }
          )
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM TokenIndexEntity
        WHERE resourceType = ? AND index_name = ? AND (index_value = ? AND IFNULL(index_system,'') = ?)
        )
        """.trimIndent()
      )
    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.GENDER.paramName,
          "male",
          "http://hl7.org/fhir/ValueSet/administrative-gender"
        )
      )
  }

  @Test
  fun search_filter_token_codeableConcept() {
    val query =
      Search(ResourceType.Immunization)
        .apply {
          filter(
            Immunization.VACCINE_CODE,
            {
              value =
                of(CodeableConcept(Coding("http://snomed.info/sct", "260385009", "Allergy X")))
            }
          )
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM TokenIndexEntity
        WHERE resourceType = ? AND index_name = ? AND (index_value = ? AND IFNULL(index_system,'') = ?)
        )
        """.trimIndent()
      )
    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Immunization.name,
          ResourceType.Immunization.name,
          Immunization.VACCINE_CODE.paramName,
          "260385009",
          "http://snomed.info/sct"
        )
      )
  }

  @Test
  fun search_filter_token_identifier() {
    val identifier = Identifier()
    identifier.value = "12345"
    identifier.system = "http://acme.org/patient"

    val query =
      Search(ResourceType.Patient)
        .apply { filter(Patient.IDENTIFIER, { value = of(identifier) }) }
        .getQuery()
    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM TokenIndexEntity
        WHERE resourceType = ? AND index_name = ? AND (index_value = ? AND IFNULL(index_system,'') = ?)
        )
        """.trimIndent()
      )
    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.IDENTIFIER.paramName,
          "12345",
          "http://acme.org/patient"
        )
      )
  }

  @Test
  fun search_filter_token_contactPoint() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.TELECOM,
            {
              value =
                of(
                  ContactPoint().apply {
                    system = ContactPoint.ContactPointSystem.EMAIL
                    use = ContactPoint.ContactPointUse.HOME
                    value = "test@gmail.com"
                  }
                )
            }
          )
        }
        .getQuery()
    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM TokenIndexEntity
        WHERE resourceType = ? AND index_name = ? AND (index_value = ? AND IFNULL(index_system,'') = ?)
        )
        """.trimIndent()
      )
    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.TELECOM.paramName,
          "test@gmail.com",
          ContactPoint.ContactPointUse.HOME.toCode()
        )
      )
  }

  @Test
  fun search_filter_token_contactPoint_missingUse() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.TELECOM,
            {
              value =
                of(
                  ContactPoint().apply {
                    system = ContactPoint.ContactPointSystem.EMAIL
                    value = "test@gmail.com"
                  }
                )
            }
          )
        }
        .getQuery()
    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM TokenIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value = ?
        )
        """.trimIndent()
      )
    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.TELECOM.paramName,
          "test@gmail.com"
        )
      )
  }

  @Test
  fun search_filter_token_codeType() {
    val query =
      Search(ResourceType.Patient)
        .apply { filter(Patient.GENDER, { value = of(CodeType("male")) }) }
        .getQuery()
    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM TokenIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value = ?
        )
        """.trimIndent()
      )
    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.GENDER.paramName,
          "male"
        )
      )
  }

  @Test
  fun search_filter_token_boolean() {
    val query =
      Search(ResourceType.Patient).apply { filter(Patient.ACTIVE, { value = of(true) }) }.getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM TokenIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value = ?
        )
        """.trimIndent()
      )
    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.ACTIVE.paramName,
          "true"
        )
      )
  }

  @Test
  fun search_filter_token_uriType() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.IDENTIFIER,
            { value = of(UriType("16009886-bd57-11eb-8529-0242ac130003")) }
          )
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM TokenIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value = ?
        )
        """.trimIndent()
      )
    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.IDENTIFIER.paramName,
          "16009886-bd57-11eb-8529-0242ac130003"
        )
      )
  }

  @Test
  fun search_filter_token_string() {
    val query =
      Search(ResourceType.Patient)
        .apply { filter(Patient.PHONE, { value = of("+14845219791") }) }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM TokenIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value = ?
        )
        """.trimIndent()
      )
    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.PHONE.paramName,
          "+14845219791"
        )
      )
  }

  @Test
  fun search_filter_quantity_equals() {
    val query =
      Search(ResourceType.Observation)
        .apply {
          filter(
            Observation.VALUE_QUANTITY,
            {
              prefix = ParamPrefixEnum.EQUAL
              unit = "g"
              value = BigDecimal("5.403")
            }
          )
        }
        .getQuery()
    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM QuantityIndexEntity
        WHERE resourceType = ? AND index_name = ? AND (index_code = ? AND index_value >= ? AND index_value < ?)
        )
        """.trimIndent()
      )
    assertThat(query.args)
      .isEqualTo(
        listOfNotNull(
          ResourceType.Observation.name,
          ResourceType.Observation.name,
          Observation.VALUE_QUANTITY.paramName,
          "g",
          BigDecimal("5.4025").toDouble(),
          BigDecimal("5.4035").toDouble()
        )
      )
  }

  @Test
  fun search_filter_quantity_less() {
    val query =
      Search(ResourceType.Observation)
        .apply {
          filter(
            Observation.VALUE_QUANTITY,
            {
              prefix = ParamPrefixEnum.LESSTHAN
              unit = "g"
              value = BigDecimal("5.403")
            }
          )
        }
        .getQuery()
    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM QuantityIndexEntity
        WHERE resourceType = ? AND index_name = ? AND (index_code = ? AND index_value < ?)
        )
        """.trimIndent()
      )
    assertThat(query.args)
      .isEqualTo(
        listOfNotNull(
          ResourceType.Observation.name,
          ResourceType.Observation.name,
          Observation.VALUE_QUANTITY.paramName,
          "g",
          BigDecimal("5.403").toDouble()
        )
      )
  }

  @Test
  fun search_filter_quantity_less_or_equal() {
    val query =
      Search(ResourceType.Observation)
        .apply {
          filter(
            Observation.VALUE_QUANTITY,
            {
              prefix = ParamPrefixEnum.LESSTHAN_OR_EQUALS
              system = "http://unitsofmeasure.org"
              value = BigDecimal("5.403")
            }
          )
        }
        .getQuery()
    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM QuantityIndexEntity
        WHERE resourceType = ? AND index_name = ? AND (index_system = ? AND index_value <= ?)
        )
        """.trimIndent()
      )
    assertThat(query.args)
      .isEqualTo(
        listOfNotNull(
          ResourceType.Observation.name,
          ResourceType.Observation.name,
          Observation.VALUE_QUANTITY.paramName,
          "http://unitsofmeasure.org",
          BigDecimal("5.403").toDouble()
        )
      )
  }

  @Test
  fun search_filter_quantity_greater() {
    val query =
      Search(ResourceType.Observation)
        .apply {
          filter(
            Observation.VALUE_QUANTITY,
            {
              prefix = ParamPrefixEnum.GREATERTHAN
              system = "http://unitsofmeasure.org"
              value = BigDecimal("5.403")
            }
          )
        }
        .getQuery()
    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM QuantityIndexEntity
        WHERE resourceType = ? AND index_name = ? AND (index_system = ? AND index_value > ?)
        )
        """.trimIndent()
      )
    assertThat(query.args)
      .isEqualTo(
        listOfNotNull(
          ResourceType.Observation.name,
          ResourceType.Observation.name,
          Observation.VALUE_QUANTITY.paramName,
          "http://unitsofmeasure.org",
          BigDecimal("5.403").toDouble()
        )
      )
  }

  @Test
  fun search_filter_quantity_greater_equal() {
    val query =
      Search(ResourceType.Observation)
        .apply {
          filter(
            Observation.VALUE_QUANTITY,
            {
              prefix = ParamPrefixEnum.GREATERTHAN_OR_EQUALS
              value = BigDecimal("5.403")
            }
          )
        }
        .getQuery()
    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM QuantityIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value >= ?
        )
        """.trimIndent()
      )
    assertThat(query.args)
      .isEqualTo(
        listOfNotNull(
          ResourceType.Observation.name,
          ResourceType.Observation.name,
          Observation.VALUE_QUANTITY.paramName,
          BigDecimal("5.403").toDouble()
        )
      )
  }

  @Test
  fun search_filter_quantity_not_equal() {
    val query =
      Search(ResourceType.Observation)
        .apply {
          filter(
            Observation.VALUE_QUANTITY,
            {
              prefix = ParamPrefixEnum.NOT_EQUAL
              value = BigDecimal("5.403")
            }
          )
        }
        .getQuery()
    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM QuantityIndexEntity
        WHERE resourceType = ? AND index_name = ? AND (index_value < ? OR index_value >= ?)
        )
        """.trimIndent()
      )
    assertThat(query.args)
      .isEqualTo(
        listOfNotNull(
          ResourceType.Observation.name,
          ResourceType.Observation.name,
          Observation.VALUE_QUANTITY.paramName,
          BigDecimal("5.4025").toDouble(),
          BigDecimal("5.4035").toDouble()
        )
      )
  }

  @Test
  fun search_filter_quantity_starts_after() {
    val query =
      Search(ResourceType.Observation)
        .apply {
          filter(
            Observation.VALUE_QUANTITY,
            {
              prefix = ParamPrefixEnum.STARTS_AFTER
              value = BigDecimal("5.403")
            }
          )
        }
        .getQuery()
    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM QuantityIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value > ?
        )
        """.trimIndent()
      )
    assertThat(query.args)
      .isEqualTo(
        listOfNotNull(
          ResourceType.Observation.name,
          ResourceType.Observation.name,
          Observation.VALUE_QUANTITY.paramName,
          BigDecimal("5.403").toDouble()
        )
      )
  }

  @Test
  fun search_filter_quantity_ends_before() {
    val query =
      Search(ResourceType.Observation)
        .apply {
          filter(
            Observation.VALUE_QUANTITY,
            {
              prefix = ParamPrefixEnum.ENDS_BEFORE
              value = BigDecimal("5.403")
            }
          )
        }
        .getQuery()
    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM QuantityIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value < ?
        )
        """.trimIndent()
      )
    assertThat(query.args)
      .isEqualTo(
        listOfNotNull(
          ResourceType.Observation.name,
          ResourceType.Observation.name,
          Observation.VALUE_QUANTITY.paramName,
          BigDecimal("5.403").toDouble()
        )
      )
  }

  @Test
  fun search_filter_quantity_canonical_match() {
    val query =
      Search(ResourceType.Observation)
        .apply {
          filter(
            Observation.VALUE_QUANTITY,
            {
              prefix = ParamPrefixEnum.EQUAL
              value = BigDecimal("5403")
              system = "http://unitsofmeasure.org"
              unit = "mg"
            }
          )
        }
        .getQuery()
    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM QuantityIndexEntity
        WHERE resourceType = ? AND index_name = ? AND (index_system = ? AND index_code = ? AND index_value >= ? AND index_value < ?)
        )
        """.trimIndent()
      )
    assertThat(query.args)
      .isEqualTo(
        listOfNotNull(
          ResourceType.Observation.name,
          ResourceType.Observation.name,
          Observation.VALUE_QUANTITY.paramName,
          "http://unitsofmeasure.org",
          "g",
          BigDecimal("5.4025").toDouble(),
          BigDecimal("5.4035").toDouble()
        )
      )
  }

  @Test
  fun search_filter_uri() {
    val query =
      Search(ResourceType.Library).apply { filter(Library.URL, { value = "someValue" }) }.getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM UriIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value = ?
        )
        """.trimIndent()
      )
    assertThat(query.args)
      .containsExactly(
        ResourceType.Library.name,
        ResourceType.Library.name,
        Library.URL.paramName,
        "someValue"
      )
  }

  @Test
  fun search_sort_string_ascending() {
    val query =
      Search(ResourceType.Patient).apply { sort(Patient.GIVEN, Order.ASCENDING) }.getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        LEFT JOIN StringIndexEntity b
        ON a.resourceType = b.resourceType AND a.resourceUuid = b.resourceUuid AND b.index_name = ?
        WHERE a.resourceType = ?
        ORDER BY b.index_value ASC
        """.trimIndent()
      )
    assertThat(query.args).isEqualTo(listOf(Patient.GIVEN.paramName, ResourceType.Patient.name))
  }

  @Test
  fun search_sort_string_descending() {
    val query =
      Search(ResourceType.Patient).apply { sort(Patient.GIVEN, Order.DESCENDING) }.getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        LEFT JOIN StringIndexEntity b
        ON a.resourceType = b.resourceType AND a.resourceUuid = b.resourceUuid AND b.index_name = ?
        WHERE a.resourceType = ?
        ORDER BY b.index_value DESC
        """.trimIndent()
      )
    assertThat(query.args).isEqualTo(listOf(Patient.GIVEN.paramName, ResourceType.Patient.name))
  }

  @Test
  fun search_sort_numbers_ascending() {
    val query =
      Search(ResourceType.RiskAssessment)
        .apply { sort(RiskAssessment.PROBABILITY, Order.ASCENDING) }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        LEFT JOIN NumberIndexEntity b
        ON a.resourceType = b.resourceType AND a.resourceUuid = b.resourceUuid AND b.index_name = ?
        WHERE a.resourceType = ?
        ORDER BY b.index_value ASC
        """.trimIndent()
      )
  }

  @Test
  fun search_filter_sort_size_from() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(Patient.FAMILY, { value = "Jones" })
          sort(Patient.GIVEN, Order.ASCENDING)
          count = 10
          from = 20
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        LEFT JOIN StringIndexEntity b
        ON a.resourceType = b.resourceType AND a.resourceUuid = b.resourceUuid AND b.index_name = ?
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM StringIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value LIKE ? || '%' COLLATE NOCASE
        )
        ORDER BY b.index_value ASC
        LIMIT ? OFFSET ?
        """.trimIndent()
      )
    assertThat(query.args)
      .isEqualTo(
        listOf(
          Patient.GIVEN.paramName,
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.FAMILY.paramName,
          "Jones",
          10,
          20
        )
      )
  }

  @Test
  fun search_has_patient_with_diabetes() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          has(resourceType = ResourceType.Condition, referenceParam = Condition.SUBJECT) {
            filter(
              Condition.CODE,
              { value = of(Coding("http://snomed.info/sct", "44054006", "Diabetes")) }
            )
          }
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """ 
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid
        FROM ResourceEntity a
        WHERE a.resourceId IN (
        SELECT substr(a.index_value, 9)
        FROM ReferenceIndexEntity a
        WHERE a.resourceType = ? AND a.index_name = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM TokenIndexEntity
        WHERE resourceType = ? AND index_name = ? AND (index_value = ? AND IFNULL(index_system,'') = ?)
        )
        )
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Condition.name,
          Condition.SUBJECT.paramName,
          ResourceType.Condition.name,
          Condition.CODE.paramName,
          "44054006",
          "http://snomed.info/sct"
        )
      )
  }

  @Test
  fun search_has_patient_with_influenza_vaccine_status_completed_in_India() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          has<Immunization>(Immunization.PATIENT) {
            filter(
              Immunization.VACCINE_CODE,
              {
                value =
                  of(
                    Coding(
                      "http://hl7.org/fhir/sid/cvx",
                      "140",
                      "Influenza, seasonal, injectable, preservative free"
                    )
                  )
              }
            )
            //      Follow Immunization.ImmunizationStatus
            filter(
              Immunization.STATUS,
              { value = of(Coding("http://hl7.org/fhir/event-status", "completed", "Body Weight")) }
            )
          }

          filter(
            Patient.ADDRESS_COUNTRY,
            {
              modifier = StringFilterModifier.MATCHES_EXACTLY
              value = "IN"
            }
          )
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM StringIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value = ?
        )
        AND a.resourceUuid IN (
        SELECT resourceUuid
        FROM ResourceEntity a
        WHERE a.resourceId IN (
        SELECT substr(a.index_value, 9)
        FROM ReferenceIndexEntity a
        WHERE a.resourceType = ? AND a.index_name = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM TokenIndexEntity
        WHERE resourceType = ? AND index_name = ? AND (index_value = ? AND IFNULL(index_system,'') = ?)
        )
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM TokenIndexEntity
        WHERE resourceType = ? AND index_name = ? AND (index_value = ? AND IFNULL(index_system,'') = ?)
        )
        )
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.ADDRESS_COUNTRY.paramName,
          "IN",
          ResourceType.Immunization.name,
          Immunization.PATIENT.paramName,
          ResourceType.Immunization.name,
          Immunization.VACCINE_CODE.paramName,
          "140",
          "http://hl7.org/fhir/sid/cvx",
          ResourceType.Immunization.name,
          Immunization.STATUS.paramName,
          "completed",
          "http://hl7.org/fhir/event-status"
        )
      )
  }

  @Test
  fun practitioner_has_patient_has_condition_diabetes_and_hypertension() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          has<Condition>(Condition.SUBJECT) {
            filter(
              Condition.CODE,
              { value = of(Coding("http://snomed.info/sct", "44054006", "Diabetes")) }
            )
          }
          has<Condition>(Condition.SUBJECT) {
            filter(
              Condition.CODE,
              { value = of(Coding("http://snomed.info/sct", "827069000", "Hypertension stage 1")) }
            )
          }
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid
        FROM ResourceEntity a
        WHERE a.resourceId IN (
        SELECT substr(a.index_value, 9)
        FROM ReferenceIndexEntity a
        WHERE a.resourceType = ? AND a.index_name = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM TokenIndexEntity
        WHERE resourceType = ? AND index_name = ? AND (index_value = ? AND IFNULL(index_system,'') = ?)
        )
        )
        )  AND a.resourceUuid IN(
        SELECT resourceUuid
        FROM ResourceEntity a
        WHERE a.resourceId IN (
        SELECT substr(a.index_value, 9)
        FROM ReferenceIndexEntity a
        WHERE a.resourceType = ? AND a.index_name = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM TokenIndexEntity
        WHERE resourceType = ? AND index_name = ? AND (index_value = ? AND IFNULL(index_system,'') = ?)
        )
        )
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Condition.name,
          Condition.SUBJECT.paramName,
          ResourceType.Condition.name,
          Condition.CODE.paramName,
          "44054006",
          "http://snomed.info/sct",
          ResourceType.Condition.name,
          Condition.SUBJECT.paramName,
          ResourceType.Condition.name,
          Condition.CODE.paramName,
          "827069000",
          "http://snomed.info/sct",
        )
      )
  }

  @Test
  fun search_date_sort() {
    val query =
      Search(ResourceType.Patient).apply { sort(Patient.BIRTHDATE, Order.ASCENDING) }.getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        LEFT JOIN DateIndexEntity b
        ON a.resourceType = b.resourceType AND a.resourceUuid = b.resourceUuid AND b.index_name = ?
        LEFT JOIN DateTimeIndexEntity c
        ON a.resourceType = c.resourceType AND a.resourceUuid = c.resourceUuid AND c.index_name = ?
        WHERE a.resourceType = ?
        ORDER BY b.index_from ASC, c.index_from ASC
        """.trimIndent()
      )
  }

  @Test
  fun search_date_sort_descending() {
    val query =
      Search(ResourceType.Patient).apply { sort(Patient.BIRTHDATE, Order.DESCENDING) }.getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        LEFT JOIN DateIndexEntity b
        ON a.resourceType = b.resourceType AND a.resourceUuid = b.resourceUuid AND b.index_name = ?
        LEFT JOIN DateTimeIndexEntity c
        ON a.resourceType = c.resourceType AND a.resourceUuid = c.resourceUuid AND c.index_name = ?
        WHERE a.resourceType = ?
        ORDER BY b.index_from DESC, c.index_from DESC
        """.trimIndent()
      )
  }

  @Test
  fun search_patient_single_search_param_multiple_values_disjunction() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.GIVEN,
            {
              value = "John"
              modifier = StringFilterModifier.MATCHES_EXACTLY
            },
            {
              value = "Jane"
              modifier = StringFilterModifier.MATCHES_EXACTLY
            },
            operation = Operation.OR
          )
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM StringIndexEntity
        WHERE resourceType = ? AND index_name = ? AND (index_value = ? OR index_value = ?)
        )
        """.trimIndent()
      )

    assertThat(query.args).isEqualTo(listOf("Patient", "Patient", "given", "John", "Jane"))
  }

  @Test
  fun search_patient_single_search_param_multiple_params_disjunction() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(
            Patient.GIVEN,
            {
              value = "John"
              modifier = StringFilterModifier.MATCHES_EXACTLY
            }
          )

          filter(
            Patient.GIVEN,
            {
              value = "Jane"
              modifier = StringFilterModifier.MATCHES_EXACTLY
            }
          )
          operation = Operation.OR
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM StringIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value = ?
        )
        OR a.resourceUuid IN (
        SELECT resourceUuid FROM StringIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value = ?
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(listOf("Patient", "Patient", "given", "John", "Patient", "given", "Jane"))
  }
  // Test for https://github.com/google/android-fhir/issues/903
  @Test
  fun search_patient_search_params_single_given_multiple_family() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(Patient.GIVEN, { value = "John" })
          filter(Patient.FAMILY, { value = "Doe" }, { value = "Roe" })
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM StringIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value LIKE ? || '%' COLLATE NOCASE
        )
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM StringIndexEntity
        WHERE resourceType = ? AND index_name = ? AND (index_value LIKE ? || '%' COLLATE NOCASE OR index_value LIKE ? || '%' COLLATE NOCASE)
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(listOf("Patient", "Patient", "given", "John", "Patient", "family", "Doe", "Roe"))
  }

  @Test
  fun `search filter should append index name only for token filter with code only`() {
    val query =
      Search(ResourceType.Condition)
        .apply {
          filter(Condition.CLINICAL_STATUS, { value = of(Coding().apply { code = "test-code" }) })
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM TokenIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value = ?
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(listOf("Condition", "Condition", "clinical-status", "test-code"))
  }

  @Test
  fun `search filter should append index name and index system for token filter with code and system`() {
    val query =
      Search(ResourceType.Condition)
        .apply {
          filter(
            Condition.CLINICAL_STATUS,
            {
              value =
                of(
                  Coding().apply {
                    code = "test-code"
                    system = "http://my-code-system.org"
                  }
                )
            }
          )
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceUuid IN (
        SELECT resourceUuid FROM TokenIndexEntity
        WHERE resourceType = ? AND index_name = ? AND (index_value = ? AND IFNULL(index_system,'') = ?)
        )
        """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          "Condition",
          "Condition",
          "clinical-status",
          "test-code",
          "http://my-code-system.org"
        )
      )
  }

  private companion object {
    const val mockEpochTimeStamp = 1628516301000
    const val APPROXIMATION_COEFFICIENT = 0.1
  }
}
