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
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Identifier
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
  fun search_string_default() {
    val query =
      Search(ResourceType.Patient)
        .apply { filter(Patient.ADDRESS) { value = "someValue" } }
        .getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceId IN (
        SELECT resourceId FROM StringIndexEntity
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
  fun search_string_exact() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(Patient.ADDRESS) {
            modifier = StringFilterModifier.MATCHES_EXACTLY
            value = "someValue"
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
  fun search_string_contains() {
    val query =
      Search(ResourceType.Patient)
        .apply {
          filter(Patient.ADDRESS) {
            modifier = StringFilterModifier.CONTAINS
            value = "someValue"
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
      Search(ResourceType.Patient).apply { filter(Patient.FAMILY) { value = "Jones" } }.getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceId IN (
        SELECT resourceId FROM StringIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value LIKE ? || '%' COLLATE NOCASE
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
  fun search_token_filter() {
    val query = Search(ResourceType.Patient).apply { filter(Patient.ACTIVE, true) }.getQuery()

    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceId IN (
        SELECT resourceId FROM TokenIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value = ?
        AND index_system IS NULL
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
  fun search_token_filter_with_system() {
    val identifier = Identifier()
    identifier.value = "12345"
    identifier.system = "http://acme.org/patient"

    val query =
      Search(ResourceType.Patient).apply { filter(Patient.IDENTIFIER, identifier) }.getQuery()
    assertThat(query.query)
      .isEqualTo(
        """
        SELECT a.serializedResource
        FROM ResourceEntity a
        WHERE a.resourceType = ?
        AND a.resourceId IN (
        SELECT resourceId FROM TokenIndexEntity
        WHERE resourceType = ? AND index_name = ? AND index_value = ?
        AND index_system = ?
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
          filter(Patient.FAMILY) { value = "Jones" }
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
}
