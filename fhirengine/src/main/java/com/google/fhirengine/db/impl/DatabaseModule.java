package com.google.fhirengine.db.impl;

import com.google.fhirengine.db.Database;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;

/** Dagger module for the FHIR resource database. */
@Module
public abstract class DatabaseModule {
  @Binds
  abstract Database bindDatabase(DatabaseImpl database);

  @Provides
  static IParser getIParser() {
    return FhirContext.forR4().newJsonParser();
  }
}
