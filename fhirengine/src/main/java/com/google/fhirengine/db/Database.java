package com.google.fhirengine.db;

import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;

import java.util.List;

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

  /**
   * Returns a {@link List} of {@link Resource}s that are of type {@code clazz} and have {@code
   * reference} with {@code value}.
   * <p>
   * For example, a search for {@link org.hl7.fhir.r4.model.Observation}s with {@code reference}
   * 'subject' and {@code value} 'Patient/1' will return all observations associated with the
   * particular patient.
   */
  <R extends Resource> List<R> searchByReference(Class<R> clazz, String reference, String value);

  /**
   * Returns a {@link List} of {@link Resource}s that are of type {@code clazz} and have {@code
   * string} with {@code value}.
   * <p>
   * For example, a search for {@link org.hl7.fhir.r4.model.Patient}s with {@code string} 'given'
   * and {@code value} 'Tom' will return all patients with a given name Tom.
   */
  <R extends Resource> List<R> searchByString(Class<R> clazz, String string, String value);

  /**
   * Returns a {@link List} of {@link Resource}s that are of type {@code clazz} and have {@code
   * code} with {@code system} and {@code value}.
   * <p>
   * For example, a search for {@link org.hl7.fhir.r4.model.Observation}s with {@code code} 'code',
   * {@code system} 'http://openmrs.org/concepts' and {@code value} '165395AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA'
   * will return all observations with that code.
   */
  <R extends Resource> List<R> searchByCode(Class<R> clazz, String code, String system,
      String value);
}
