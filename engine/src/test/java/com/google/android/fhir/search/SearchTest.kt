/*
 * Copyright 2020 Google LLC
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
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.DateTimeType
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.model.RiskAssessment
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
  fun search_filter() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(Patient.FAMILY) {
            prefix = ParamPrefixEnum.EQUAL
            value = "Jones"
          }
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceId IN (
        SELECT resourceId FROM StringIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value = ? COLLATE NOCASE
        )
        """.trimIndent()
      )
    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.FAMILY.paramName,
          "Jones"
        )
      )
  }

  @Test
  fun search_date_approximate() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(Patient.BIRTHDATE) {
            prefix = ParamPrefixEnum.APPROXIMATE
            value = DateTimeType("2013-03-14")
          }
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
    SELECT a.serializedResource
    FROM ResourceEntity a
    WHERE a.resourceType = ?
    AND a.resourceId IN (
    SELECT resourceId FROM DateIndexEntity
    WHERE resourceType = ? AND index_name = ?
    AND index_from BETWEEN ? AND ? AND index_to BETWEEN ? AND ?
    )
    """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.BIRTHDATE.paramName,
          DateTimeType("2013-03-04").value.time,
          DateTimeType("2013-03-24").value.time,
          DateTimeType("2013-03-04").value.time,
          DateTimeType("2013-03-24").value.time
        )
      )
  }

  @Test
  fun search_date_starts_after() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(Patient.BIRTHDATE) {
            prefix = ParamPrefixEnum.STARTS_AFTER
            value = DateTimeType("2013-03-14")
          }
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
    SELECT a.serializedResource
    FROM ResourceEntity a
    WHERE a.resourceType = ?
    AND a.resourceId IN (
    SELECT resourceId FROM DateIndexEntity
    WHERE resourceType = ? AND index_name = ?
    AND ? <= index_from
    )
    """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.BIRTHDATE.paramName,
          DateTimeType("2013-03-15").value.time
        )
      )
  }

  @Test
  fun search_date_ends_before() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(Patient.BIRTHDATE) {
            prefix = ParamPrefixEnum.ENDS_BEFORE
            value = DateTimeType("2013-03-14")
          }
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
    SELECT a.serializedResource
    FROM ResourceEntity a
    WHERE a.resourceType = ?
    AND a.resourceId IN (
    SELECT resourceId FROM DateIndexEntity
    WHERE resourceType = ? AND index_name = ?
    AND ? >= index_to
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
  fun search_date_not_equal() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(Patient.BIRTHDATE) {
            prefix = ParamPrefixEnum.NOT_EQUAL
            value = DateTimeType("2013-03-14")
          }
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
    SELECT a.serializedResource
    FROM ResourceEntity a
    WHERE a.resourceType = ?
    AND a.resourceId IN (
    SELECT resourceId FROM DateIndexEntity
    WHERE resourceType = ? AND index_name = ?
    AND index_from NOT BETWEEN ? AND ? OR index_to NOT BETWEEN ? AND ?
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
  fun search_date_equal() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(Patient.BIRTHDATE) {
            prefix = ParamPrefixEnum.EQUAL
            value = DateTimeType("2013-03-14")
          }
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
    SELECT a.serializedResource
    FROM ResourceEntity a
    WHERE a.resourceType = ?
    AND a.resourceId IN (
    SELECT resourceId FROM DateIndexEntity
    WHERE resourceType = ? AND index_name = ?
    AND index_from BETWEEN ? AND ? AND index_to BETWEEN ? AND ?
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
  fun search_date_greater() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(Patient.BIRTHDATE) {
            prefix = ParamPrefixEnum.GREATERTHAN
            value = DateTimeType("2013-03-14")
          }
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
    SELECT a.serializedResource
    FROM ResourceEntity a
    WHERE a.resourceType = ?
    AND a.resourceId IN (
    SELECT resourceId FROM DateIndexEntity
    WHERE resourceType = ? AND index_name = ?
    AND ? <= index_from
    )
    """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.BIRTHDATE.paramName,
          DateTimeType("2013-03-15").value.time
        )
      )
  }

  @Test
  fun search_date_greaterOrEqual() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(Patient.BIRTHDATE) {
            prefix = ParamPrefixEnum.GREATERTHAN_OR_EQUALS
            value = DateTimeType("2013-03-14")
          }
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
    SELECT a.serializedResource
    FROM ResourceEntity a
    WHERE a.resourceType = ?
    AND a.resourceId IN (
    SELECT resourceId FROM DateIndexEntity
    WHERE resourceType = ? AND index_name = ?
    AND ? <= index_from
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
  fun search_date_less() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(Patient.BIRTHDATE) {
            prefix = ParamPrefixEnum.LESSTHAN
            value = DateTimeType("2013-03-14")
          }
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
    SELECT a.serializedResource
    FROM ResourceEntity a
    WHERE a.resourceType = ?
    AND a.resourceId IN (
    SELECT resourceId FROM DateIndexEntity
    WHERE resourceType = ? AND index_name = ?
    AND ? >= index_to
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
  fun search_date_lessOrEqual() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(Patient.BIRTHDATE) {
            prefix = ParamPrefixEnum.LESSTHAN_OR_EQUALS
            value = DateTimeType("2013-03-14")
          }
        }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
    SELECT a.serializedResource
    FROM ResourceEntity a
    WHERE a.resourceType = ?
    AND a.resourceId IN (
    SELECT resourceId FROM DateIndexEntity
    WHERE resourceType = ? AND index_name = ?
    AND ? >= index_to
    )
    """.trimIndent()
      )

    assertThat(query.args)
      .isEqualTo(
        listOf(
          ResourceType.Patient.name,
          ResourceType.Patient.name,
          Patient.BIRTHDATE.paramName,
          DateTimeType("2013-03-15").value.time
        )
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
        ON a.resourceType = b.resourceType AND a.resourceId = b.resourceId AND b.index_name = ?
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
        ON a.resourceType = b.resourceType AND a.resourceId = b.resourceId AND b.index_name = ?
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
      ON a.resourceType = b.resourceType AND a.resourceId = b.resourceId AND b.index_name = ?
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
          filter(Patient.FAMILY) {
            prefix = ParamPrefixEnum.EQUAL
            value = "Jones"
          }
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
        ON a.resourceType = b.resourceType AND a.resourceId = b.resourceId AND b.index_name = ?
        WHERE a.resourceType = ?
        AND a.resourceId IN (
        SELECT resourceId FROM StringIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value = ? COLLATE NOCASE
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
}
