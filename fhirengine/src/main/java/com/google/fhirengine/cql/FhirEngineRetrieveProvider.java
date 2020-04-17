package com.google.fhirengine.cql;

import android.text.TextUtils;

import com.google.common.collect.ImmutableList;
import com.google.fhirengine.db.Database;
import com.google.fhirengine.resource.ResourceUtils;

import org.opencds.cqf.cql.runtime.Code;
import org.opencds.cqf.cql.runtime.Interval;

import javax.inject.Inject;

/**
 * FHIR Engine's implementation of a {@link org.opencds.cqf.cql.retrieve.RetrieveProvider} which
 * provides the {@link org.opencds.cqf.cql.execution.CqlEngine} required FHIR resources to complete
 * CQL evaluation.
 * <p>
 * Note: must be used in conjunction with a {@link org.opencds.cqf.cql.model.ModelResolver} for HAPI
 * FHIR resources.
 */
public class FhirEngineRetrieveProvider implements org.opencds.cqf.cql.retrieve.RetrieveProvider {
  private Database database;

  @Inject
  public FhirEngineRetrieveProvider(Database database) {
    this.database = database;
  }

  @Override
  public Iterable<Object> retrieve(String context, String contextPath, Object contextValue,
      String dataType, String templateId, String codePath, Iterable<Code> codes, String valueSet,
      String datePath, String dateLowPath, String dateHighPath, Interval dateRange) {
    return ImmutableList.copyOf(database
        .searchByReference(ResourceUtils.getResourceClass(dataType), dataType + "." + contextPath,
            TextUtils.isEmpty((String) contextValue) ? null : context + "/" + contextValue));
  }
}
