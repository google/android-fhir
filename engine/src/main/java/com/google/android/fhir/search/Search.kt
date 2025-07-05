/*
 * Copyright 2022-2024 Google LLC
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
import com.google.android.fhir.search.query.XFhirQueryTranslator.translate
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

/**
 * Searches the database and returns a list of resources matching the [Search] specifications.
 *
 * Example:
 * ```
 * fhirEngine.search<Patient> {
 *  filter(Patient.GIVEN, {
 *    value = "Kiran"
 *    modifier = StringFilterModifier.MATCHES_EXACTLY
 *  })
 * }
 * ```
 * ### Performance Tip:
 * Avoid requesting all resources at once — especially if they number in the thousands — as it can
 * significantly impact performance due to memory use and JSON deserialization overhead.
 *
 * ### Recommended Usage:
 * Always paginate queries using `.count()` and `.from()` in [Search] to fetch only the number of
 * results needed for the UI.
 *
 * Example (Paginated Search):
 * ```
 * val results = fhirEngine.search<Patient> {
 *   count = 20  // Limit to 20 results
 *   from = 0    // Offset for pagination
 * }
 * ```
 *
 * For technical context, see:
 * - Issue: https://github.com/google/android-fhir/issues/2684
 * - PR with performance metrics: https://github.com/google/android-fhir/pull/2669
 *
 * @param init The lambda expression used to configure the [Search] object.
 * @return A list of [SearchResult] objects containing the matching resources and any included
 *   references.
 */
suspend inline fun <reified R : Resource> FhirEngine.search(
  init: Search.() -> Unit,
): List<SearchResult<R>> {
  val search = Search(type = R::class.java.newInstance().resourceType)
  search.init()
  return this.search(search)
}

suspend inline fun <reified R : Resource> FhirEngine.count(init: Search.() -> Unit): Long {
  val search = Search(type = R::class.java.newInstance().resourceType)
  search.init()
  return this.count(search)
}

suspend fun FhirEngine.search(xFhirQuery: String): List<SearchResult<Resource>> {
  return this.search(translate(xFhirQuery))
}

suspend fun FhirEngine.search(
  resourceType: ResourceType,
  init: Search.() -> Unit,
): List<SearchResult<Resource>> {
  val search = Search(type = resourceType)
  search.init()
  return this.search(search)
}
