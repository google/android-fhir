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

package com.google.fhirengine.search.filter

import ca.uhn.fhir.rest.gclient.StringClientParam
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import com.google.fhirengine.search.impl.ResourceIdQuery
import org.hl7.fhir.r4.model.Resource

/**
 * [FilterCriterion] on a string value.
 *
 * For example:
 * * name that matches 'Tom'
 * * address that includes 'London'
 */
class StringFilteringCriterion constructor(
    val param: StringClientParam,
    val operator: ParamPrefixEnum,
    val value: String
) : FilterCriterion {
    override fun <R : Resource> query(clazz: Class<R>): ResourceIdQuery {
        // TODO: implement different queries for different operators.
        return ResourceIdQuery("""
SELECT resourceId FROM StringIndexEntity
WHERE resourceType = ? AND index_name = ? AND index_value = ?""",
            listOf(clazz.simpleName, param.paramName, value))
    }
}

fun string(param: StringClientParam, operator: ParamPrefixEnum, value: String) =
    StringFilteringCriterion(param, operator, value)
