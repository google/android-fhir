package com.google.fhirengine.cql;

import org.opencds.cqf.cql.data.CompositeDataProvider;
import org.opencds.cqf.cql.model.R4FhirModelResolver;
import org.opencds.cqf.cql.retrieve.RetrieveProvider;

import javax.inject.Inject;

/**
 * FHIR Engine's implementation of a {@link org.opencds.cqf.cql.data.DataProvider} which provides
 * the {@link org.opencds.cqf.cql.execution.CqlEngine} required data to complete CQL evaluation.
 */
public class FhirEngineDataProvider extends CompositeDataProvider {
  @Inject
  public FhirEngineDataProvider(RetrieveProvider retrieveProvider) {
    super(new R4FhirModelResolver(), retrieveProvider);
  }
}
