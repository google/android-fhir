package com.google.fhirengine.db;

/** Thrown to indicate that the requested resource is not found in the database. */
public class ResourceNotFoundInDbException extends Exception {
  private String type;
  private String id;

  public ResourceNotFoundInDbException(String type, String id) {
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
