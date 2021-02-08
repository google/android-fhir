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

package com.google.android.fhir.search.impl

import android.os.Build
import com.google.android.fhir.search.sort.SortCriterion
import com.google.common.truth.Truth.assertThat
import org.hl7.fhir.r4.model.ResourceType
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Unit tests for {@link SerializedResourceQuery}. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SerializedResourceQueryTest {
    @Test
    fun getQueryString_noResourceIdQuery_noSortCriterion_noLimit_noSkip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, null, null, null, null)
        assertThat(serializedResourceQuery.getQueryString()).isEqualTo("""
            SELECT a.serializedResource
            FROM ResourceEntity a
            WHERE a.resourceType = ?
        """.trimIndent())
    }

    @Test
    fun getQueryArgs_noResourceIdQuery_noSortCriterion_noLimit_noSkip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, null, null, null, null)
        assertThat(serializedResourceQuery.getQueryArgs()).isEqualTo(listOf(RESOURCE_TYPE.name))
    }

    @Test
    fun getQueryString_noResourceIdQuery_noSortCriterion_limit_noSkip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, null, null, 10, null)
        assertThat(serializedResourceQuery.getQueryString()).isEqualTo("""
            SELECT a.serializedResource
            FROM ResourceEntity a
            WHERE a.resourceType = ?
            LIMIT ?
        """.trimIndent())
    }

    @Test
    fun getQueryArgs_noResourceIdQuery_noSortCriterion_limit_noSkip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, null, null, 10, null)
        assertThat(serializedResourceQuery.getQueryArgs()).isEqualTo(listOf(RESOURCE_TYPE.name, 10))
    }

    @Test
    fun getQueryString_noResourceIdQuery_noSortCriterion_limit_skip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, null, null, 10, 20)
        assertThat(serializedResourceQuery.getQueryString()).isEqualTo("""
            SELECT a.serializedResource
            FROM ResourceEntity a
            WHERE a.resourceType = ?
            LIMIT ? OFFSET ?
        """.trimIndent())
    }

    @Test
    fun getQueryArgs_noResourceIdQuery_noSortCriterion_limit_skip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, null, null, 10, 20)
        assertThat(serializedResourceQuery.getQueryArgs()).isEqualTo(
                listOf(RESOURCE_TYPE.name, 10, 20))
    }

    @Test
    fun getQueryString_noResourceIdQuery_sortCriterion_ascending_noLimit_noSkip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, null, SORT_CRITERION_ASCENDING,
                        null,
                        null)
        assertThat(serializedResourceQuery.getQueryString()).isEqualTo("""
            SELECT a.serializedResource
            FROM ResourceEntity a
            LEFT JOIN [[SORT_TABLE]] b
            ON a.resourceType = b.resourceType AND a.resourceId = b.resourceId AND b.index_name = ?
            WHERE a.resourceType = ?
            ORDER BY b.index_value ASC
        """.trimIndent())
    }

    @Test
    fun getQueryString_noResourceIdQuery_sortCriterion_descending_noLimit_noSkip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, null, SORT_CRITERION_DESCENDING,
                        null,
                        null)
        assertThat(serializedResourceQuery.getQueryString()).isEqualTo("""
            SELECT a.serializedResource
            FROM ResourceEntity a
            LEFT JOIN [[SORT_TABLE]] b
            ON a.resourceType = b.resourceType AND a.resourceId = b.resourceId AND b.index_name = ?
            WHERE a.resourceType = ?
            ORDER BY b.index_value DESC
        """.trimIndent())
    }

    @Test
    fun getQueryArgs_noResourceIdQuery_sortCriterion_noLimit_noSkip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, null, SORT_CRITERION_ASCENDING,
                        null,
                        null)
        assertThat(serializedResourceQuery.getQueryArgs()).isEqualTo(
                listOf("[[SORT_PARAM]]", RESOURCE_TYPE.name))
    }

    @Test
    fun getQueryString_noResourceIdQuery_sortCriterion_ascending_limit_noSkip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, null, SORT_CRITERION_ASCENDING,
                        10, null)
        assertThat(serializedResourceQuery.getQueryString()).isEqualTo("""
            SELECT a.serializedResource
            FROM ResourceEntity a
            LEFT JOIN [[SORT_TABLE]] b
            ON a.resourceType = b.resourceType AND a.resourceId = b.resourceId AND b.index_name = ?
            WHERE a.resourceType = ?
            ORDER BY b.index_value ASC
            LIMIT ?
        """.trimIndent())
    }

    @Test
    fun getQueryString_noResourceIdQuery_sortCriterion_descending_limit_noSkip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, null, SORT_CRITERION_DESCENDING,
                        10, null)
        assertThat(serializedResourceQuery.getQueryString()).isEqualTo("""
            SELECT a.serializedResource
            FROM ResourceEntity a
            LEFT JOIN [[SORT_TABLE]] b
            ON a.resourceType = b.resourceType AND a.resourceId = b.resourceId AND b.index_name = ?
            WHERE a.resourceType = ?
            ORDER BY b.index_value DESC
            LIMIT ?
        """.trimIndent())
    }

    @Test
    fun getQueryArgs_noResourceIdQuery_sortCriterion_limit_noSkip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, null, SORT_CRITERION_ASCENDING,
                        10, null)
        assertThat(serializedResourceQuery.getQueryArgs()).isEqualTo(
                listOf("[[SORT_PARAM]]", RESOURCE_TYPE.name, 10))
    }

    @Test
    fun getQueryString_noResourceIdQuery_sortCriterion_ascending_limit_skip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, null, SORT_CRITERION_ASCENDING,
                        10, 20)
        assertThat(serializedResourceQuery.getQueryString()).isEqualTo("""
            SELECT a.serializedResource
            FROM ResourceEntity a
            LEFT JOIN [[SORT_TABLE]] b
            ON a.resourceType = b.resourceType AND a.resourceId = b.resourceId AND b.index_name = ?
            WHERE a.resourceType = ?
            ORDER BY b.index_value ASC
            LIMIT ? OFFSET ?
        """.trimIndent())
    }

    @Test
    fun getQueryString_noResourceIdQuery_sortCriterion_descending_limit_skip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, null, SORT_CRITERION_DESCENDING,
                        10, 20)
        assertThat(serializedResourceQuery.getQueryString()).isEqualTo("""
            SELECT a.serializedResource
            FROM ResourceEntity a
            LEFT JOIN [[SORT_TABLE]] b
            ON a.resourceType = b.resourceType AND a.resourceId = b.resourceId AND b.index_name = ?
            WHERE a.resourceType = ?
            ORDER BY b.index_value DESC
            LIMIT ? OFFSET ?
        """.trimIndent())
    }

    @Test
    fun getQueryArgs_noResourceIdQuery_sortCriterion_limit_skip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, null, SORT_CRITERION_ASCENDING,
                        10, 20)
        assertThat(serializedResourceQuery.getQueryArgs()).isEqualTo(
                listOf("[[SORT_PARAM]]", RESOURCE_TYPE.name, 10, 20))
    }

    @Test
    fun getQueryString_resourceIdQuery_noSortCriterion_noLimit_noSkip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, RESOURCE_ID_QUERY, null, null, null)
        assertThat(serializedResourceQuery.getQueryString()).isEqualTo("""
            SELECT a.serializedResource
            FROM ResourceEntity a
            WHERE a.resourceType = ? AND a.resourceId IN ([[RESOURCE_ID_QUERY]])
        """.trimIndent())
    }

    @Test
    fun getQueryArgs_resourceIdQuery_noSortCriterion_noLimit_noSkip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, RESOURCE_ID_QUERY, null, null, null)
        assertThat(serializedResourceQuery.getQueryArgs()).isEqualTo(
                listOf(RESOURCE_TYPE.name, "[[RESOURCE_ID_QUERY_ARG]]"))
    }

    @Test
    fun getQueryString_resourceIdQuery_noSortCriterion_limit_noSkip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, RESOURCE_ID_QUERY, null, 10, null)
        assertThat(serializedResourceQuery.getQueryString()).isEqualTo("""
            SELECT a.serializedResource
            FROM ResourceEntity a
            WHERE a.resourceType = ? AND a.resourceId IN ([[RESOURCE_ID_QUERY]])
            LIMIT ?
        """.trimIndent())
    }

    @Test
    fun getQueryArgs_resourceIdQuery_noSortCriterion_limit_noSkip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, RESOURCE_ID_QUERY, null, 10, null)
        assertThat(serializedResourceQuery.getQueryArgs()).isEqualTo(
                listOf(RESOURCE_TYPE.name, "[[RESOURCE_ID_QUERY_ARG]]", 10))
    }

    @Test
    fun getQueryString_resourceIdQuery_noSortCriterion_limit_skip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, RESOURCE_ID_QUERY, null, 10, 20)
        assertThat(serializedResourceQuery.getQueryString()).isEqualTo("""
            SELECT a.serializedResource
            FROM ResourceEntity a
            WHERE a.resourceType = ? AND a.resourceId IN ([[RESOURCE_ID_QUERY]])
            LIMIT ? OFFSET ?
        """.trimIndent())
    }

    @Test
    fun getQueryArgs_resourceIdQuery_noSortCriterion_limit_skip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, RESOURCE_ID_QUERY, null, 10, 20)
        assertThat(serializedResourceQuery.getQueryArgs()).isEqualTo(
                listOf(RESOURCE_TYPE.name, "[[RESOURCE_ID_QUERY_ARG]]", 10, 20))
    }

    @Test
    fun getQueryString_resourceIdQuery_sortCriterion_ascending_noLimit_noSkip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, RESOURCE_ID_QUERY, SORT_CRITERION_ASCENDING,
                        null,
                        null)
        assertThat(serializedResourceQuery.getQueryString()).isEqualTo("""
            SELECT a.serializedResource
            FROM ResourceEntity a
            LEFT JOIN [[SORT_TABLE]] b
            ON a.resourceType = b.resourceType AND a.resourceId = b.resourceId AND b.index_name = ?
            WHERE a.resourceType = ? AND a.resourceId IN ([[RESOURCE_ID_QUERY]])
            ORDER BY b.index_value ASC
        """.trimIndent())
    }

    @Test
    fun getQueryString_resourceIdQuery_sortCriterion_descending_noLimit_noSkip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, RESOURCE_ID_QUERY, SORT_CRITERION_DESCENDING,
                        null,
                        null)
        assertThat(serializedResourceQuery.getQueryString()).isEqualTo("""
            SELECT a.serializedResource
            FROM ResourceEntity a
            LEFT JOIN [[SORT_TABLE]] b
            ON a.resourceType = b.resourceType AND a.resourceId = b.resourceId AND b.index_name = ?
            WHERE a.resourceType = ? AND a.resourceId IN ([[RESOURCE_ID_QUERY]])
            ORDER BY b.index_value DESC
        """.trimIndent())
    }

    @Test
    fun getQueryArgs_resourceIdQuery_sortCriterion_noLimit_noSkip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, RESOURCE_ID_QUERY, SORT_CRITERION_ASCENDING,
                        null,
                        null)
        assertThat(serializedResourceQuery.getQueryArgs()).isEqualTo(
                listOf("[[SORT_PARAM]]", RESOURCE_TYPE.name, "[[RESOURCE_ID_QUERY_ARG]]"))
    }

    @Test
    fun getQueryString_resourceIdQuery_sortCriterion_ascending_limit_noSkip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, RESOURCE_ID_QUERY, SORT_CRITERION_ASCENDING,
                        10, null)
        assertThat(serializedResourceQuery.getQueryString()).isEqualTo("""
            SELECT a.serializedResource
            FROM ResourceEntity a
            LEFT JOIN [[SORT_TABLE]] b
            ON a.resourceType = b.resourceType AND a.resourceId = b.resourceId AND b.index_name = ?
            WHERE a.resourceType = ? AND a.resourceId IN ([[RESOURCE_ID_QUERY]])
            ORDER BY b.index_value ASC
            LIMIT ?
        """.trimIndent())
    }

    @Test
    fun getQueryString_resourceIdQuery_sortCriterion_descending_limit_noSkip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, RESOURCE_ID_QUERY, SORT_CRITERION_DESCENDING,
                        10, null)
        assertThat(serializedResourceQuery.getQueryString()).isEqualTo("""
            SELECT a.serializedResource
            FROM ResourceEntity a
            LEFT JOIN [[SORT_TABLE]] b
            ON a.resourceType = b.resourceType AND a.resourceId = b.resourceId AND b.index_name = ?
            WHERE a.resourceType = ? AND a.resourceId IN ([[RESOURCE_ID_QUERY]])
            ORDER BY b.index_value DESC
            LIMIT ?
        """.trimIndent())
    }

    @Test
    fun getQueryArgs_resourceIdQuery_sortCriterion_limit_noSkip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, RESOURCE_ID_QUERY, SORT_CRITERION_ASCENDING,
                        10, null)
        assertThat(serializedResourceQuery.getQueryArgs()).isEqualTo(
                listOf("[[SORT_PARAM]]", RESOURCE_TYPE.name, "[[RESOURCE_ID_QUERY_ARG]]", 10))
    }

    @Test
    fun getQueryString_resourceIdQuery_sortCriterion_ascending_limit_skip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, RESOURCE_ID_QUERY, SORT_CRITERION_ASCENDING,
                        10, 20)
        assertThat(serializedResourceQuery.getQueryString()).isEqualTo("""
            SELECT a.serializedResource
            FROM ResourceEntity a
            LEFT JOIN [[SORT_TABLE]] b
            ON a.resourceType = b.resourceType AND a.resourceId = b.resourceId AND b.index_name = ?
            WHERE a.resourceType = ? AND a.resourceId IN ([[RESOURCE_ID_QUERY]])
            ORDER BY b.index_value ASC
            LIMIT ? OFFSET ?
        """.trimIndent())
    }

    @Test
    fun getQueryString_resourceIdQuery_sortCriterion_descending_limit_skip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, RESOURCE_ID_QUERY, SORT_CRITERION_DESCENDING,
                        10, 20)
        assertThat(serializedResourceQuery.getQueryString()).isEqualTo("""
            SELECT a.serializedResource
            FROM ResourceEntity a
            LEFT JOIN [[SORT_TABLE]] b
            ON a.resourceType = b.resourceType AND a.resourceId = b.resourceId AND b.index_name = ?
            WHERE a.resourceType = ? AND a.resourceId IN ([[RESOURCE_ID_QUERY]])
            ORDER BY b.index_value DESC
            LIMIT ? OFFSET ?
        """.trimIndent())
    }

    @Test
    fun getQueryArgs_resourceIdQuery_sortCriterion_limit_skip() {
        val serializedResourceQuery =
                SerializedResourceQuery(RESOURCE_TYPE, RESOURCE_ID_QUERY, SORT_CRITERION_ASCENDING,
                        10, 20)
        assertThat(serializedResourceQuery.getQueryArgs()).isEqualTo(
                listOf("[[SORT_PARAM]]", RESOURCE_TYPE.name, "[[RESOURCE_ID_QUERY_ARG]]", 10, 20))
    }

    companion object {
        val RESOURCE_TYPE = ResourceType.Patient
        val RESOURCE_ID_QUERY =
                ResourceIdQuery("[[RESOURCE_ID_QUERY]]", listOf("[[RESOURCE_ID_QUERY_ARG]]"))
        val SORT_CRITERION_ASCENDING = object : SortCriterion {
            override val table = "[[SORT_TABLE]]"
            override val param = "[[SORT_PARAM]]"
            override val ascending = true
        }
        val SORT_CRITERION_DESCENDING = object : SortCriterion {
            override val table = "[[SORT_TABLE]]"
            override val param = "[[SORT_PARAM]]"
            override val ascending = false
        }
    }
}
