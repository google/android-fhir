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
  val resourceIdQuery: ResourceIdQuery,
  val sortCriterion: SortCriterion?,
  val skip: Int?,
  val limit: Int?
) : Query() {
    override fun getQueryString(): String = if (sortCriterion == null) {
        """
        SELECT serializedResource 
        FROM ResourceEntity
        WHERE resourceType = ? AND resourceId IN (${resourceIdQuery.query})
        ${limit?.let { "LIMIT $it${skip?.let { " OFFSET $it" }}" }}"""
    } else {
        """
        SELECT serializedResource 
        FROM ResourceEntity a
        LEFT JOIN ${sortCriterion.table} b
        ON a.resourceType = b.resourceType AND a.resourceId = b.resourceId AND b.index_name = ?
        WHERE a.resourceType = ? AND a.resourceId IN (${resourceIdQuery.query})
        ORDER BY b.index_value ${if (sortCriterion.ascending) {
            "ASC"
        } else {
            "DESC"
        }}
        ${limit?.let { "LIMIT $it${skip?.let { " OFFSET $it" }}" }}"""
    }.trimIndent()

    override fun getQueryArgs(): List<Any?> =
            if (sortCriterion == null) {
                listOf(resourceType.name) + resourceIdQuery.getQueryArgs()
            } else {
                listOf(sortCriterion.param, resourceType.name) + resourceIdQuery.getQueryArgs()
            }
}
