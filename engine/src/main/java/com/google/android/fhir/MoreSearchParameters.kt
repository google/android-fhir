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

package com.google.android.fhir

import com.google.android.fhir.index.SearchParamDefinition
import org.hl7.fhir.r4.model.SearchParameter

/**
 * Converts a list of [SearchParameter]s into a Map of resourceType string and list of associated
 * [SearchParamDefinition].
 */
internal fun List<SearchParameter>.asMapOfResourceTypeToSearchParamDefinitions():
  Map<String, List<SearchParamDefinition>> =
  flatMap { it.toSearchParamDefinition() }.groupBy({ it.first }, { it.second })

/** @return List of pairs of resourceType string and associated [SearchParamDefinition]. */
internal fun SearchParameter.toSearchParamDefinition(): List<Pair<String, SearchParamDefinition>> {
  require(!name.isNullOrEmpty()) { "SearchParameter.name can't be null or empty." }

  requireNotNull(type) { "SearchParameter.type can't be null." }

  require(!expression.isNullOrEmpty()) { "SearchParameter.expression can't be null or empty." }

  return getResourceToPathMap(this).map { (resourceType, path) ->
    resourceType to SearchParamDefinition(name = name, type = type, path = path)
  }
}

private fun getResourceToPathMap(searchParam: SearchParameter): Map<String, String> {
  // the if block is added because of the issue https://jira.hl7.org/browse/FHIR-22724 and can
  // be removed once the issue is resolved
  return if (searchParam.base.size == 1) {
    mapOf(searchParam.base.single().valueAsString to searchParam.expression)
  } else {
    searchParam.expression
      .split("|")
      .groupBy { splitString -> splitString.split(".").first().trim().removePrefix("(") }
      .mapValues { it.value.joinToString(" | ") { join -> join.trim() } }
  }
}
