package com.google.fhirengine.db;

import org.hl7.fhir.r4.model.Resource;

/** The interface for the FHIR resource database. */
public interface Database {
  /**
   * Inserts the {@code resource} into the FHIR resource database.
   *
   * @param <R> The resource type
   * @throws ResourceAlreadyExistsInDbException if the resource already exists in the database
   */
  <R extends Resource> void insert(R resource) throws ResourceAlreadyExistsInDbException;

  /**
   * Updates the {@code resource} in the FHIR resource database. If the resource does not already
   * exist, a new record will be created.
   *
   * @param <R> The resource type
   */
  <R extends Resource> void update(R resource);

  /**
   * Selects the FHIR resource of type {@code clazz} with {@code id}.
   *
   * @param <R> The resource type
   * @throws ResourceNotFoundInDbException if the resource is not found in the database
   */
  <R extends Resource> R select(Class<R> clazz, String id) throws ResourceNotFoundInDbException;

  /**
   * Deletes the FHIR resource of type {@code clazz} with {@code id}.
   *
   * @param <R> The resource type
   */
  <R extends Resource> void delete(Class<R> clazz, String id);
}
