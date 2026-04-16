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

import com.google.android.fhir.FhirEngine
import com.google.android.fhir.SearchResult
import com.google.android.fhir.getResourceType
import com.google.fhir.model.r4.Resource

/**
 * Searches the database and returns a list of resources matching the given [Search] criteria.
 *
 * Example usage:
 * ```
 * val patients = fhirEngine.search<Patient> {
 *   filter(StringClientParam("name"), { value = "John" })
 *   count = 10
 * }
 * ```
 */
suspend inline fun <reified R : Resource> FhirEngine.search(
  init: Search.() -> Unit,
): List<SearchResult<R>> {
  val search = Search(getResourceType(R::class))
  search.init()
  return search(search)
}

/**
 * Returns the total count of entities available for the given [Search] criteria.
 *
 * Example usage:
 * ```
 * val count = fhirEngine.count<Patient> {
 *   filter(StringClientParam("name"), { value = "John" })
 * }
 * ```
 */
suspend inline fun <reified R : Resource> FhirEngine.count(
  init: Search.() -> Unit,
): Long {
  val search = Search(getResourceType(R::class))
  search.init()
  return count(search)
}
