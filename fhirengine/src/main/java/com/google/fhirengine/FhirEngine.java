package com.google.fhirengine;

import org.hl7.fhir.r4.model.Resource;

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
}
