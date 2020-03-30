package com.google.fhirengine.impl;

import com.google.fhir.shaded.protobuf.Message;
import com.google.fhirengine.FhirEngine;
import com.google.fhirengine.ResourceAlreadyExistsException;
import com.google.fhirengine.ResourceNotFoundException;
import com.google.fhirengine.db.Database;
import com.google.fhirengine.db.ResourceAlreadyExistsInDbException;
import com.google.fhirengine.db.ResourceNotFoundInDbException;
import com.google.fhirengine.proto.ProtoUtils;

import javax.inject.Inject;

/** Implementation of {@link FhirEngine}. */
public class FhirEngineImpl implements FhirEngine {

  private Database database;

  @Inject
  public FhirEngineImpl(Database database) {
    this.database = database;
  }

  @Override
  public <M extends Message> void save(M resource) throws ResourceAlreadyExistsException {
    try {
      database.insert(resource);
    } catch (ResourceAlreadyExistsInDbException e) {
      throw new ResourceAlreadyExistsException(ProtoUtils.getResourceType(resource.getClass()),
          ProtoUtils.getResourceId(resource), e);
    }
  }

  @Override
  public <M extends Message> void update(M resource) {
    throw new UnsupportedOperationException("Not implemented yet!");
  }

  @Override
  public <M extends Message> M load(Class<M> clazz, String id) throws ResourceNotFoundException {
    try {
      return database.select(clazz, id);
    } catch (ResourceNotFoundInDbException e) {
      throw new ResourceNotFoundException(clazz.getName(), id, e);
    }
  }

  @Override
  public <M extends Message> M remove(Class<M> clazz, String id) {
    throw new UnsupportedOperationException("Not implemented yet!");
  }
}
