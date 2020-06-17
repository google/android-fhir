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

package com.google.fhirengine

import com.google.fhirengine.search.Search
import org.hl7.fhir.r4.model.Resource
import org.opencds.cqf.cql.execution.EvaluationResult

/** The FHIR Engine interface that handles the local storage of FHIR resources.  */
interface FhirEngine {
    /**
     * Saves a FHIR `resource` in the local storage.
     *
     * @param <R> The resource type which should be a subtype of [Resource].
     * @throws ResourceAlreadyExistsException if the resource already exists
     */
    @Throws(ResourceAlreadyExistsException::class)
    fun <R : Resource> save(resource: R)

    /**
     * Updates a FHIR `resource` in the local storage.
     *
     * @param <R> The resource type which should be a subtype of [Resource].
     */
    fun <R : Resource> update(resource: R)

    /**
     * Returns a FHIR resource of type `clazz` with `id` from the local storage.
     *
     * @param <R> The resource type which should be a subtype of [Resource].
     * @throws ResourceNotFoundException if the resource is not found
     */
    @Throws(ResourceNotFoundException::class)
    fun <R : Resource> load(clazz: Class<R>, id: String): R

    /**
     * Removes a FHIR resource of type `clazz` with `id` from the local storage.
     *
     * @param <R> The resource type which should be a subtype of [Resource].
     */
    fun <R : Resource> remove(clazz: Class<R>, id: String): R

    /** Returns the result of a CQL evaluation provided with the ID of the library.  */
    fun evaluateCql(libraryVersionId: String, context: String, expression: String): EvaluationResult

    /** Returns the entry point for [Search]. */
    fun search(): Search
}
