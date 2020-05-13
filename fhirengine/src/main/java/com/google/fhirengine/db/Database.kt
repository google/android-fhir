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

package com.google.fhirengine.db

import org.hl7.fhir.r4.model.Resource

/** The interface for the FHIR resource database.  */
interface Database {
    /**
     * Inserts the `resource` into the FHIR resource database.
     *
     * @param <R> The resource type
     * @throws ResourceAlreadyExistsInDbException if the resource already exists in the database
     */
    @Throws(ResourceAlreadyExistsInDbException::class)
    fun <R : Resource> insert(resource: R)

    /**
     * Updates the `resource` in the FHIR resource database. If the resource does not already
     * exist, a new record will be created.
     *
     * @param <R> The resource type
     */
    fun <R : Resource> update(resource: R)

    /**
     * Selects the FHIR resource of type `clazz` with `id`.
     *
     * @param <R> The resource type
     * @throws ResourceNotFoundInDbException if the resource is not found in the database
     */
    @Throws(ResourceNotFoundInDbException::class)
    fun <R : Resource> select(clazz: Class<R>, id: String): R

    /**
     * Deletes the FHIR resource of type `clazz` with `id`.
     *
     * @param <R> The resource type
     */
    fun <R : Resource> delete(clazz: Class<R>, id: String)
}
