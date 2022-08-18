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

package com.google.android.fhir.r4

import com.google.android.fhir.FhirEngine
import com.google.android.fhir.db.ResourceNotFoundException
import com.google.android.fhir.r4.search.Search
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.model.ResourceType

typealias R4FhirEngine = FhirEngine<Resource, ResourceType, Search>

/**
 * Returns a FHIR resource of type [R] with [id] from the local storage.
 * @param <R> The resource type which should be a subtype of [Resource].
 * @throws ResourceNotFoundException if the resource is not found
 */
@Throws(ResourceNotFoundException::class)
suspend inline fun <reified R : Resource> R4FhirEngine.get(id: String): R {
  return get(getResourceType(R::class.java), id) as R
}

/**
 * Deletes a FHIR resource of type [R] with [id] from the local storage.
 * @param <R> The resource type which should be a subtype of [Resource].
 */
suspend inline fun <reified R : Resource> R4FhirEngine.delete(id: String) {
  delete(getResourceType(R::class.java), id)
}
