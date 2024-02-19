/*
 * Copyright 2022-2023 Google LLC
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
