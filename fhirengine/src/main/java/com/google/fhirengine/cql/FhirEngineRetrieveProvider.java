package com.google.fhirengine.cql;

import org.opencds.cqf.cql.runtime.Code;
import org.opencds.cqf.cql.runtime.Interval;

public class FhirEngineRetrieveProvider implements org.opencds.cqf.cql.retrieve.RetrieveProvider {
  @Override
  public Iterable<Object> retrieve(String context, String contextPath, Object contextValue,
      String dataType, String templateId, String codePath, Iterable<Code> codes, String valueSet,
      String datePath, String dateLowPath, String dateHighPath, Interval dateRange) {
    return null;
  }
}
