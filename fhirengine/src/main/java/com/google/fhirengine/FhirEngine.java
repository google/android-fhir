package com.google.fhirengine;

import com.google.fhir.shaded.protobuf.Message;
import com.google.fhirengine.db.ResourceNotFoundInDbException;

/** The FHIR Engine interface that handles the local storage of FHIR resources. */
public interface FhirEngine {
  /**
   * Saves a FHIR {@code resource} in the local storage.
   *
   * @param <M> The resource type which should be a subtype of {@link Message}.
   * @throws ResourceAlreadyExistsException if the resource already exists
   */
  <M extends Message> void save(M resource) throws ResourceAlreadyExistsException;

  /**
   * Updates a FHIR {@code resource} in the local storage.
   *
   * @param <M> The resource type which should be a subtype of {@link Message}.
   */
  <M extends Message> void update(M resource);

  /**
   * Returns a FHIR resource of type {@code clazz} with {@code id} from the local storage.
   *
   * @param <M> The resource type which should be a subtype of {@link Message}.
   * @throws ResourceNotFoundException if the resource is not found
   */
  <M extends Message> M load(Class<M> clazz, String id) throws ResourceNotFoundException;

  /**
   * Removes a FHIR resource of type {@code clazz} with {@code id} from the local storage.
   *
   * @param <M> The resource type which should be a subtype of {@link Message}.
   */
  <M extends Message> M remove(Class<M> clazz, String id);
}
