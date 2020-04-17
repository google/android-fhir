package com.google.fhirengine.cql;

import org.opencds.cqf.cql.runtime.Code;
import org.opencds.cqf.cql.terminology.CodeSystemInfo;
import org.opencds.cqf.cql.terminology.TerminologyProvider;
import org.opencds.cqf.cql.terminology.ValueSetInfo;

import javax.inject.Inject;

/** Fhir Engine's implementation of {@link TerminologyProvider}. */
public class FhirEngineTerminologyProvider implements TerminologyProvider {

  @Inject
  public FhirEngineTerminologyProvider() {
  }

  @Override
  public boolean in(Code code, ValueSetInfo valueSet) {
    return false;
  }

  @Override
  public Iterable<Code> expand(ValueSetInfo valueSet) {
    return null;
  }

  @Override
  public Code lookup(Code code, CodeSystemInfo codeSystem) {
    return null;
  }
}
