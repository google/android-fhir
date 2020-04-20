package com.google.fhirengine.index;

import com.google.auto.value.AutoValue;

/** An string index for a specific resource. */
@AutoValue
public abstract class StringIndex {
  public static StringIndex create(String name, String path, String value) {
    return new AutoValue_StringIndex(name, path, value);
  }

  /** The name of the string index, e.g. "given". */
  public abstract String name();

  /** The path of the string index, e.g. "Patient.name.given". */
  public abstract String path();

  /** The value of the string index, e.g. "Tom". */
  public abstract String value();
}
