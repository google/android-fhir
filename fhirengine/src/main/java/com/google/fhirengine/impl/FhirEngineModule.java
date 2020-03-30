package com.google.fhirengine.impl;

import com.google.fhirengine.FhirEngine;

import dagger.Binds;
import dagger.Module;

/** Dagger module for FHIR Engine. */
@Module
public abstract class FhirEngineModule {
  @Binds
  abstract FhirEngine bindFhirEngine(FhirEngineImpl fhirEngine);
}
