package com.google.fhirengine.index;

import org.hl7.fhir.r4.model.Resource;

/**
 * The interface that handles the indexing of FHIR resources.
 * <p>
 * Note: this interface does not handle the actual storage of the indices.
 */
public interface FhirIndexer {
  /**
   * Returns the values to index for a FHIR {@code resource} in the local storage.
   *
   * @param <R> The resource type which should be a subtype of {@link Resource}.
   */
  <R extends Resource> ResourceIndices index(R resource);
}
