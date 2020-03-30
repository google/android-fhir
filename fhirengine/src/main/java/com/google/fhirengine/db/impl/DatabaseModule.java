package com.google.fhirengine.db.impl;

import com.google.fhirengine.db.Database;

import dagger.Binds;
import dagger.Module;

/** Dagger module for the FHIR resource database. */
@Module
public abstract class DatabaseModule {
  @Binds
  abstract Database bindDatabase(DatabaseImpl database);
}
