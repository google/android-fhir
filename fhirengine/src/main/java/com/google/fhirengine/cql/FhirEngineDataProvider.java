package com.google.fhirengine.cql;

import org.opencds.cqf.cql.data.CompositeDataProvider;
import org.opencds.cqf.cql.model.R4FhirModelResolver;

public class FhirEngineDataProvider extends CompositeDataProvider {
  public FhirEngineDataProvider() {
    super(new R4FhirModelResolver(), new FhirEngineRetrieveProvider());
  }
}
