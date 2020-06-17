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

package com.google.fhirengine.search.criteria

import com.google.fhirengine.search.impl.ResourceIdQuery
import org.hl7.fhir.r4.model.Resource

/**
 * [FilterCriterion] that is satisfied if all of the sub [FilterCriterion]s are satisfied.
 */
class AndFilterCriterion constructor(
  val left: FilterCriterion,
  val right: FilterCriterion
) : FilterCriterion {
    override fun <R : Resource> query(clazz: Class<R>): ResourceIdQuery {
        val leftQuery = left.query(clazz)
        val rightQuery = right.query(clazz)
        return ResourceIdQuery(
                "${leftQuery.query}  INTERSECT ${rightQuery.query}",
                leftQuery.args + rightQuery.args)
    }
}

fun and(left: FilterCriterion, right: FilterCriterion) = AndFilterCriterion(left, right)
