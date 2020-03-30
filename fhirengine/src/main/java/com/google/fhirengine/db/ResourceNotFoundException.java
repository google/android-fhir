package com.google.fhirengine.db;

import com.google.fhir.shaded.protobuf.Message;

/** Thrown to indicate that the requested resource is not found in the database. */
public class ResourceNotFoundException extends Exception {
  private String type;
  private String id;

  public ResourceNotFoundException(String type, String id) {
    super("Resource not found with type " + type + " and id " + id + "!");
    this.type = type;
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public String getId() {
    return id;
  }
}
