package com.google.fhirengine.proto;

import com.google.fhir.r4.core.Id;
import com.google.fhir.r4.core.Patient;
import com.google.fhir.shaded.protobuf.Descriptors;
import com.google.fhir.shaded.protobuf.Message;

/** Utilities for the protocol buffers. */
public class ProtoUtils {
  /** Returns the FHIR resource type. */
  public static <M extends Message> String getResourceType(Class<M> clazz) {
    return clazz.getName();
  }

  /** Returns the value of the id field of the FHIR resource. */
  public static <M extends Message> String getResourceId(M resource) {
    for (Descriptors.FieldDescriptor field : resource.getDescriptorForType().getFields()) {
      if (field.getName().equals("id")) {
        return ((Id) resource.getField(field)).getValue();
      }
    }
    Patient p;
    throw new IllegalArgumentException("Missing ID!");
  }
}
