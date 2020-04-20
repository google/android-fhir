package com.google.fhirengine.index;

import com.google.auto.value.AutoValue;

/** An code index for a specific resource. */
@AutoValue
public abstract class CodeIndex {
  public static CodeIndex create(String name, String path, String system, String code) {
    return new AutoValue_CodeIndex(name, path, system, code);
  }

  /** The name of the code index, e.g. "code". */
  public abstract String name();

  /** The path of the code index, e.g. "Observation.code". */
  public abstract String path();

  /** The system of the code index, e.g. "http://openmrs.org/concepts". */
  public abstract String system();

  /** The code of the code index, e.g. "1427AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA". */
  public abstract String code();
}
