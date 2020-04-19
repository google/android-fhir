package com.google.fhirengine.cql;

import org.hl7.fhir.instance.model.api.IBase;
import org.opencds.cqf.cql.data.CompositeDataProvider;
import org.opencds.cqf.cql.model.ModelResolver;
import org.opencds.cqf.cql.model.R4FhirModelResolver;
import org.opencds.cqf.cql.retrieve.RetrieveProvider;

import java.util.List;

import javax.inject.Inject;

import ca.uhn.fhir.context.BaseRuntimeChildDefinition;
import ca.uhn.fhir.context.RuntimeResourceDefinition;

/**
 * FHIR Engine's implementation of a {@link org.opencds.cqf.cql.data.DataProvider} which provides
 * the {@link org.opencds.cqf.cql.execution.CqlEngine} required data to complete CQL evaluation.
 */
public class FhirEngineDataProvider extends CompositeDataProvider {
  @Inject
  public FhirEngineDataProvider(ModelResolver modelResolver, RetrieveProvider retrieveProvider) {
    super(modelResolver, retrieveProvider);
  }
}
