package com.google.fhirengine.db;

import org.hl7.fhir.r4.model.Resource;

/** The interface for the FHIR resource database. */
public interface Database {
  /**
   * Inserts the {@code resource} into the FHIR resource database.
   *
   * @param <R> The resource type
   */
  <R extends Resource> void insert(R resource) throws ResourceAlreadyExistsException;

  /**
   * Updates the {@code resource}.
   *
   * @param <R> The resource type
   */
  <R extends Resource> void update(R resource);

  /**
   * Selects the FHIR resource of type {@code clazz} with {@code id}.
   *
   * @param <R> The resource type
   * @throws ResourceNotFoundException if the resource is not found
   */
  <R extends Resource> R select(Class<R> clazz, String id) throws ResourceNotFoundException;

  /**
   * Deletes the FHIR resource of type {@code clazz} with {@code id}.
   *
   * @param <R> The resource type
   */
  <R extends Resource> void delete(Class<R> clazz, String id);
}
