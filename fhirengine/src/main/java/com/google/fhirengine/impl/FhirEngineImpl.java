package com.google.fhirengine.impl;

import com.google.fhirengine.FhirEngine;
import com.google.fhirengine.db.Database;

import org.hl7.fhir.r4.model.Resource;

import javax.inject.Inject;

/** Implementation of {@link FhirEngine}. */
public class FhirEngineImpl implements FhirEngine {

  private Database database;

  @Inject
  public FhirEngineImpl(Database database) {
    this.database = database;
  }

  @Override
  public <R extends Resource> void save(R resource) {
    throw new UnsupportedOperationException("Not implemented yet!");
  }

  @Override
  public <R extends Resource> void update(R resource) {
    throw new UnsupportedOperationException("Not implemented yet!");
  }

  @Override
  public <R extends Resource> R load(Class<R> clazz, String id) {
    throw new UnsupportedOperationException("Not implemented yet!");
  }

  @Override
  public <R extends Resource> R remove(Class<R> clazz, String id) {
    throw new UnsupportedOperationException("Not implemented yet!");
  }
}
