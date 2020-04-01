package com.google.fhirengine.proto;

import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;

import java.lang.reflect.InvocationTargetException;

/** Utilities for the HAPI FHIR resources. */
public class ResourceUtils {
  /** Returns the FHIR resource type. */
  public static <M extends Resource> ResourceType getResourceType(Class<M> clazz) {
    try {
      return clazz.getConstructor().newInstance().getResourceType();
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException("Cannot resolve resource type for " + clazz.getName(), e);
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException("Cannot resolve resource type for " + clazz.getName(), e);
    } catch (InstantiationException e) {
      throw new IllegalArgumentException("Cannot resolve resource type for " + clazz.getName(), e);
    } catch (InvocationTargetException e) {
      throw new IllegalArgumentException("Cannot resolve resource type for " + clazz.getName(), e);
    }
  }
}
