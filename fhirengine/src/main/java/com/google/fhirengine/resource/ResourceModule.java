package com.google.fhirengine.resource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import dagger.Module;
import dagger.Provides;

/** Dagger module for FHIR resources. */
@Module
public abstract class ResourceModule {
  @Provides
  static FhirContext getFhirContext() {
    return FhirContext.forR4();
  }

  @Provides
  static IParser getIParser(FhirContext fhirContext) {
    return fhirContext.newJsonParser();
  }
}
