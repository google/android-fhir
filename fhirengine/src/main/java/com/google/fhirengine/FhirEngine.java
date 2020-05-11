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

package com.google.fhirengine;

import org.hl7.fhir.r4.model.Resource;
import org.opencds.cqf.cql.execution.EvaluationResult;

/** The FHIR Engine interface that handles the local storage of FHIR resources. */
public interface FhirEngine {
  /**
   * Saves a FHIR {@code resource} in the local storage.
   *
   * @param <R> The resource type which should be a subtype of {@link Resource}.
   * @throws ResourceAlreadyExistsException if the resource already exists
   */
  <R extends Resource> void save(R resource) throws ResourceAlreadyExistsException;

  /**
   * Updates a FHIR {@code resource} in the local storage.
   *
   * @param <R> The resource type which should be a subtype of {@link Resource}.
   */
  <R extends Resource> void update(R resource);

  /**
   * Returns a FHIR resource of type {@code clazz} with {@code id} from the local storage.
   *
   * @param <R> The resource type which should be a subtype of {@link Resource}.
   * @throws ResourceNotFoundException if the resource is not found
   */
  <R extends Resource> R load(Class<R> clazz, String id) throws ResourceNotFoundException;

  /**
   * Removes a FHIR resource of type {@code clazz} with {@code id} from the local storage.
   *
   * @param <R> The resource type which should be a subtype of {@link Resource}.
   */
  <R extends Resource> R remove(Class<R> clazz, String id);

  /** Returns the result of a CQL evaluation provided with the ID of the library. */
  EvaluationResult evaluateCql(String libraryVersionId, String context, String expression);
}
