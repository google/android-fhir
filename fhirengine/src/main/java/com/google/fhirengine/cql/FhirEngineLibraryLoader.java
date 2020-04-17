package com.google.fhirengine.cql;

import com.google.fhirengine.db.Database;
import com.google.fhirengine.db.ResourceNotFoundInDbException;

import org.cqframework.cql.elm.execution.Library;
import org.cqframework.cql.elm.execution.VersionedIdentifier;
import org.opencds.cqf.cql.elm.execution.ObjectFactoryEx;
import org.opencds.cqf.cql.execution.JsonCqlLibraryReader;
import org.opencds.cqf.cql.execution.LibraryLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.inject.Inject;
import javax.xml.namespace.QName;

/**
 * FHIR Engine's implementation of {@link LibraryLoader} that loads a CQL/ELM library for the {@link
 * org.opencds.cqf.cql.execution.CqlEngine} to use.
 */
public class FhirEngineLibraryLoader implements LibraryLoader {
  private final Database database;

  @Inject
  public FhirEngineLibraryLoader(Database database) {
    this.database = database;
  }

  @Override
  public Library load(VersionedIdentifier libraryIdentifier) {
    org.hl7.fhir.r4.model.Library library;
    try {
      library = database.select(org.hl7.fhir.r4.model.Library.class, libraryIdentifier.getId());
    } catch (ResourceNotFoundInDbException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }

    StringReader stringReader = new StringReader(new String(library.getContent().get(0).getData()));
    try {
      return JsonCqlLibraryReader.read(stringReader);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
