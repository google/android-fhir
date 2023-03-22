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

import ca.uhn.fhir.rest.gclient.IParam
import ca.uhn.fhir.rest.gclient.ReferenceClientParam
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/** Lets users perform a nested search using [Search.has] api. */
@PublishedApi internal data class NestedSearch(val param: ReferenceClientParam, val search: Search)

/** Keeps the parent context for a nested query loop. */
internal data class NestedContext(val parentType: ResourceType, val param: IParam)

/**
 * Provides limited support for the reverse chaining on [Search]
 * [https://www.hl7.org/fhir/search.html#has]. For example: search all Patient that have Condition -
 * Diabetes. This search uses the subject field in the Condition resource. Code snippet:
 * ```
 *     FhirEngine.search<Patient> {
 *        has<Condition>(Condition.SUBJECT) {
 *          filter(Condition.CODE, Coding("http://snomed.info/sct", "44054006", "Diabetes"))
 *        }
 *     }
 * ```
 */
inline fun <reified R : Resource> Search.has(
  referenceParam: ReferenceClientParam,
  init: Search.() -> Unit
) {
  nestedSearches.add(
    NestedSearch(referenceParam, Search(type = R::class.java.newInstance().resourceType)).apply {
      search.init()
    }
  )
}

/**
 * Provide limited support for reverse chaining on [Search] (See
 * [this](https://www.hl7.org/fhir/search.html#has)).
 *
 * Example usage (Search for all Patients with Condition - Diabetes):
 *
 * ```
 *   fhirEngine.search<Patient> {
 *        has(resourceType = ResourceType.Condition, referenceParam = (Condition.SUBJECT) {
 *          filter(Condition.CODE, Coding("http://snomed.info/sct", "44054006", "Diabetes"))
 *        }
 *     }
 * ```
 */
fun Search.has(
  resourceType: ResourceType,
  referenceParam: ReferenceClientParam,
  init: Search.() -> Unit
) {
  nestedSearches.add(
    NestedSearch(referenceParam, Search(type = resourceType)).apply { search.init() }
  )
}

/**
 * Generates the complete nested query going to several depths depending on the [Search] dsl
 * specified by the user .
 */
internal fun List<NestedSearch>.nestedQuery(
  type: ResourceType,
  operation: Operation
): SearchQuery? {
  return if (isEmpty()) {
    null
  } else {
    map { it.nestedQuery(type) }
      .let { searchQueries ->
        SearchQuery(
          query =
            searchQueries.joinToString(
              prefix = "AND a.resourceUuid IN ",
              separator = " ${operation.logicalOperator} a.resourceUuid IN"
            ) { searchQuery -> "(\n${searchQuery.query}\n) " },
          args = searchQueries.flatMap { it.args }
        )
      }
  }
}

private fun NestedSearch.nestedQuery(type: ResourceType): SearchQuery {
  return search.getQuery(nestedContext = NestedContext(type, param))
}
