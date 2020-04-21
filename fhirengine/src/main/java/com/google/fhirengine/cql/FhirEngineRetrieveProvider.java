package com.google.fhirengine.cql;

import android.text.TextUtils;

import com.google.common.collect.ImmutableList;
import com.google.fhirengine.db.Database;
import com.google.fhirengine.resource.ResourceUtils;

import org.opencds.cqf.cql.runtime.Code;
import org.opencds.cqf.cql.runtime.Interval;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    Iterator<Code> codesit = codes.iterator();
    List<Code> codeList = new ArrayList<>();
    while (codesit.hasNext()) {
      codeList.add(codesit.next());
    }
    if (codeList.size() == 0) {
      ImmutableList<Object> result = ImmutableList.copyOf(database
          .searchByReference(ResourceUtils.getResourceClass(dataType), dataType + "." + contextPath,
              TextUtils.isEmpty((String) contextValue) ? null : context + "/" + contextValue));
      return result;
    } else if (codeList.size() == 1) {
      // TODO: create a database method that search by code as well as reference.
      Code code = codeList.get(0);
      ImmutableList<Object> result = ImmutableList.copyOf(database
          .searchByCode(ResourceUtils.getResourceClass(dataType), dataType + "." + codePath,
              code.getSystem(), code.getCode()));
      return result;
    } else {
      return ImmutableList.of();
    }
  }
}
