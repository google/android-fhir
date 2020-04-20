package com.google.fhirengine.index;

import com.google.auto.value.AutoValue;

/** An reference index for a specific resource. */
@AutoValue
public abstract class ReferenceIndex {
  public static ReferenceIndex create(String name, String path, String value) {
    return new AutoValue_ReferenceIndex(name, path, value);
  }

  /** The name of the string index, e.g. "given". */
  public abstract String name();

  /** The path of the string index, e.g. "Patient.name.given". */
  public abstract String path();

  /** The value of the string index, e.g. "Tom". */
  public abstract String value();
}
