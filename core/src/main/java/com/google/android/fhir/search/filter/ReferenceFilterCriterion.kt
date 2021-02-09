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

package com.google.android.fhir.search.filter

import ca.uhn.fhir.rest.gclient.ReferenceClientParam
import com.google.android.fhir.search.impl.ResourceIdQuery
import org.hl7.fhir.r4.model.Resource

/** [FilterCriterion] on a reference value. */
class ReferenceFilterCriterion constructor(
    val param: ReferenceClientParam,
    val value: String
) : FilterCriterion {
    override fun <R : Resource> query(clazz: Class<R>): ResourceIdQuery {
        // TODO: implement different queries for different operators.
        return ResourceIdQuery("""
            SELECT resourceId FROM ReferenceIndexEntity
            WHERE resourceType = ? AND index_name = ? AND index_value = ?""".trimIndent(),
            listOf(clazz.simpleName, param.paramName, value))
    }
}

fun reference(param: ReferenceClientParam, value: String) = ReferenceFilterCriterion(param, value)
