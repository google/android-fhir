package com.google.fhirengine.impl;

import com.google.fhir.shaded.protobuf.Message;
import com.google.fhirengine.FhirEngine;
import com.google.fhirengine.db.Database;

import javax.inject.Inject;

/** Implementation of {@link FhirEngine}. */
public class FhirEngineImpl implements FhirEngine {

  private Database database;

  @Inject
  public FhirEngineImpl(Database database) {
    this.database = database;
  }

  @Override
  public <M extends Message> void save(M resource) {
    throw new UnsupportedOperationException("Not implemented yet!");
  }

  @Override
  public <M extends Message> void update(M resource) {
    throw new UnsupportedOperationException("Not implemented yet!");
  }

  @Override
  public <M extends Message> M load(Class<M> clazz, String id) {
    throw new UnsupportedOperationException("Not implemented yet!");
  }

  @Override
  public <M extends Message> M remove(Class<M> clazz, String id) {
    throw new UnsupportedOperationException("Not implemented yet!");
  }
}
