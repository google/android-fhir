package com.google.fhirengine.db;

import com.google.fhir.shaded.protobuf.Message;

/** The interface for the FHIR resource database. */
public interface Database {
  /**
   * Inserts the {@code resource} into the FHIR resource database.
   *
   * @param <M> The resource type which should be a subtype of {@link Message}.
   */
  <M extends Message> void insert(M resource);

  /**
   * Updates the {@code resource}.
   *
   * @param <M> The resource type which should be a subtype of {@link Message}.
   */
  <M extends Message> void update(M resource);

  /**
   * Selects the FHIR resource of type {@code clazz} with {@code id}.
   *
   * @param <M> The resource type which should be a subtype of {@link Message}.
   */
  <M extends Message> M select(Class<M> clazz, String id);

  /**
   * Deletes the FHIR resource of type {@code clazz} with {@code id}.
   *
   * @param <M> The resource type which should be a subtype of {@link Message}.
   */
  <M extends Message> void delete(Class<M> clazz, String id);
}
