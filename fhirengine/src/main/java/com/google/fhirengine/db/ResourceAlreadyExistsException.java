package com.google.fhirengine.db;

/** Thrown to indicate that the resource already exists in the database. */
public class ResourceAlreadyExistsException extends Exception {
  private String type;
  private String id;

  public ResourceAlreadyExistsException(String type, String id, Throwable cause) {
    super("Resource with type " + type + " and id " + id + " already exists!", cause);
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
