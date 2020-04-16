package com.google.fhirengine.index.impl;

import dagger.Binds;
import dagger.Module;

import com.google.fhirengine.index.FhirIndexer;

/** Dagger module for FHIR Indexer. */
@Module
public abstract class FhirIndexerModule {
  @Binds
  abstract FhirIndexer bindFhirIndexer(FhirIndexerImpl indexer);
}
