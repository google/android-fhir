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

package com.google.android.fhir

import com.google.android.fhir.search.Search
import com.google.android.fhir.sync.PeriodicSyncConfiguration
import com.google.android.fhir.sync.Result
import com.google.android.fhir.sync.SyncConfiguration
import org.hl7.fhir.r4.model.Resource

/** The FHIR Engine interface that handles the local storage of FHIR resources. */
interface FhirEngine {
  /**
   * Saves a FHIR `resource` in the local storage. If the resource already exists, it will be
   * overwritten
   *
   * @param <R> The resource type which should be a subtype of [Resource].
   */
  suspend fun <R : Resource> save(resource: R)

  /**
   * Saves a list of FHIR `resource` in the local storage. If any of the resources already exist,
   * they will be overwritten.
   *
   * @param <R> The resource type which should be a subtype of [Resource].
   */
  suspend fun <R : Resource> saveAll(resources: List<R>)

  /**
   * Updates a FHIR `resource` in the local storage.
   *
   * @param <R> The resource type which should be a subtype of [Resource].
   */
  suspend fun <R : Resource> update(resource: R)

  /**
   * Returns a FHIR resource of type `clazz` with `id` from the local storage.
   *
   * @param <R> The resource type which should be a subtype of [Resource].
   * @throws ResourceNotFoundException if the resource is not found
   */
  @Throws(ResourceNotFoundException::class)
  suspend fun <R : Resource> load(clazz: Class<R>, id: String): R

  /**
   * Removes a FHIR resource of type `clazz` with `id` from the local storage.
   *
   * @param <R> The resource type which should be a subtype of [Resource].
   */
  suspend fun <R : Resource> remove(clazz: Class<R>, id: String)

  /** Returns the entry point for [Search]. */
  fun search(): Search

  /**
   * One time sync.
   *
   * @param syncConfiguration
   * - configuration of data that needs to be synchronised
   */
  suspend fun sync(syncConfiguration: SyncConfiguration): Result

  suspend fun periodicSync(): Result

  fun updatePeriodicSyncConfiguration(syncConfig: PeriodicSyncConfiguration)
}
