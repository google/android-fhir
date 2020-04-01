package com.google.fhirengine;

/** Thrown to indicate that the requested resource is not found. */
public class ResourceNotFoundException extends Exception {
  private String type;
  private String id;

  public ResourceNotFoundException(String type, String id, Throwable throwable) {
    super("Resource not found with type " + type + " and id " + id + "!", throwable);
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
