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

package com.google.fhirengine.search.impl

import com.google.fhirengine.search.sort.SortCriterion
import org.hl7.fhir.r4.model.ResourceType

/** Query that returns a list of serialized resources. */
data class SerializedResourceQuery(
    val resourceType: ResourceType,
    val resourceIdQuery: ResourceIdQuery?,
    val sortCriterion: SortCriterion?,
    val limit: Int?,
    val skip: Int?
) : Query() {
    override fun getQueryString(): String {
        val queryBuilder = StringBuilder()
        queryBuilder.appendln("""
            SELECT a.serializedResource
            FROM ResourceEntity a
        """.trimIndent())
        sortCriterion?.also {
            queryBuilder.appendln("""
                LEFT JOIN ${sortCriterion.table} b
                ON a.resourceType = b.resourceType AND a.resourceId = b.resourceId AND b.index_name = ?
            """.trimIndent())
        }
        queryBuilder.appendln("""
            WHERE a.resourceType = ?${resourceIdQuery?.let { " AND a.resourceId IN (${it.query})" } ?: ""}
        """.trimIndent())
        sortCriterion?.also {
            queryBuilder.appendln("""
                ORDER BY b.index_value ${
                if (sortCriterion.ascending) {
                    "ASC"
                } else {
                    "DESC"
                }
            }
            """.trimIndent())
        }
        limit?.also {
            queryBuilder.appendln("""
                LIMIT ?${skip?.let { " OFFSET ?" } ?: ""}
            """.trimIndent())
        }
        return queryBuilder.toString().trimIndent()
    }

    override fun getQueryArgs(): List<Any> {
        var list: List<Any> = if (sortCriterion == null) {
            listOf()
        } else {
            listOf(sortCriterion.param)
        }
        list = list + resourceType.name
        resourceIdQuery?.also { list = list + it.getQueryArgs() }
        limit?.also {
            list = list + it
            skip?.also {
                list = list + it
            }
        }
        return list
    }
}
