package com.google.fhirengine.db.impl;

import android.content.Context;

import com.google.fhir.shaded.protobuf.Message;
import com.google.fhirengine.db.Database;

import javax.inject.Inject;

/** Implementation of {@link Database}. */
public class DatabaseImpl implements Database {

  private Context context;

  @Inject
  DatabaseImpl(Context context) {
    this.context = context;
  }

  @Override
  public <M extends Message> void insert(M resource) {
    throw new UnsupportedOperationException("Not implemented yet!");
  }

  @Override
  public <M extends Message> void update(M resource) {
    throw new UnsupportedOperationException("Not implemented yet!");
  }

  @Override
  public <M extends Message> M select(Class<M> clazz, String id) {
    throw new UnsupportedOperationException("Not implemented yet!");
  }

  @Override
  public <M extends Message> void delete(Class<M> clazz, String id) {
    throw new UnsupportedOperationException("Not implemented yet!");
  }
}
