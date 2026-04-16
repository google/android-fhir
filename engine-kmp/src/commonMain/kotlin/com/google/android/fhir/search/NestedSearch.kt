/*
 * Copyright 2025-2026 Google LLC
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

import com.google.android.fhir.getResourceType
import com.google.fhir.model.r4.Resource
import com.google.fhir.model.r4.terminologies.ResourceType

/** Lets users perform a nested search using [Search.has] api. */
@PublishedApi internal data class NestedSearch(val param: ReferenceClientParam, val search: Search)

/** Keeps the parent context for a nested query loop. */
internal data class NestedContext(val parentType: ResourceType, val param: ClientParam)

/**
 * Provides limited support for the reverse chaining on [Search]. For example: search all Patient
 * that have Condition - Diabetes.
 */
inline fun <reified R : Resource> Search.has(
  referenceParam: ReferenceClientParam,
  init: BaseSearch.() -> Unit,
) {
  nestedSearches.add(
    NestedSearch(referenceParam, Search(type = getResourceType(R::class))).apply { search.init() },
  )
}

fun Search.has(
  resourceType: ResourceType,
  referenceParam: ReferenceClientParam,
  init: BaseSearch.() -> Unit,
) {
  nestedSearches.add(
    NestedSearch(referenceParam, Search(type = resourceType)).apply { search.init() },
  )
}

/**
 * Includes additional resources in the search results that are referenced by the base resource via
 * the given [referenceParam].
 */
inline fun <reified R : Resource> Search.include(
  referenceParam: ReferenceClientParam,
  init: BaseSearch.() -> Unit = {},
) {
  forwardIncludes.add(
    NestedSearch(referenceParam, Search(type = getResourceType(R::class))).apply { search.init() },
  )
}

fun Search.include(
  resourceType: ResourceType,
  referenceParam: ReferenceClientParam,
  init: BaseSearch.() -> Unit = {},
) {
  forwardIncludes.add(
    NestedSearch(referenceParam, Search(type = resourceType)).apply { search.init() },
  )
}

/**
 * Includes additional resources in the search results that reference the base resource via the
 * given [referenceParam].
 */
inline fun <reified R : Resource> Search.revInclude(
  referenceParam: ReferenceClientParam,
  init: BaseSearch.() -> Unit = {},
) {
  revIncludes.add(
    NestedSearch(referenceParam, Search(type = getResourceType(R::class))).apply { search.init() },
  )
}

fun Search.revInclude(
  resourceType: ResourceType,
  referenceParam: ReferenceClientParam,
  init: BaseSearch.() -> Unit = {},
) {
  revIncludes.add(
    NestedSearch(referenceParam, Search(type = resourceType)).apply { search.init() },
  )
}

/**
 * Generates the complete nested query going to several depths depending on the [Search] dsl
 * specified by the user.
 */
internal fun List<NestedSearch>.nestedQuery(
  type: ResourceType,
  operation: Operation,
): SearchQuery? {
  return if (isEmpty()) {
    null
  } else {
    map { it.nestedQuery(type) }
      .let { searchQueries ->
        SearchQuery(
          query =
            searchQueries.joinToString(
              prefix = "a.resourceUuid IN ",
              separator = " ${operation.logicalOperator} a.resourceUuid IN",
            ) { searchQuery ->
              "(\n${searchQuery.query}\n) "
            },
          args = searchQueries.flatMap { it.args },
        )
      }
  }
}

private fun NestedSearch.nestedQuery(type: ResourceType): SearchQuery {
  return search.getQuery(nestedContext = NestedContext(type, param))
}
