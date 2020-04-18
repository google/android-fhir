// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.fhirengine.cql;

import com.google.fhirengine.db.Database;
import com.google.fhirengine.db.ResourceNotFoundInDbException;

import org.cqframework.cql.elm.execution.Library;
import org.cqframework.cql.elm.execution.VersionedIdentifier;
import org.opencds.cqf.cql.execution.JsonCqlLibraryReader;
import org.opencds.cqf.cql.execution.LibraryLoader;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * FHIR Engine's implementation of {@link LibraryLoader} that loads a CQL/ELM library for the {@link
 * org.opencds.cqf.cql.execution.CqlEngine} to use.
 */
public class FhirEngineLibraryLoader implements LibraryLoader {
  private final Database database;

  /** Cached libraries. */
  Map<String, Library> libraryMap = new HashMap<>();

  @Inject
  public FhirEngineLibraryLoader(Database database) {
    this.database = database;
  }

  @Override
  public Library load(VersionedIdentifier libraryIdentifier) {
    for (String key : libraryMap.keySet()) {
      // TODO: Change this to an exact match once the libraries are correctly indexed by their name
      //  instead of FHIR resource ID.
      if (key.contains(libraryIdentifier.getId())) {
        return libraryMap.get(key);
      }
    }
    org.hl7.fhir.r4.model.Library fhirLibrary;
    try {
      fhirLibrary =
          database.select(org.hl7.fhir.r4.model.Library.class, libraryIdentifier.getId());
    } catch (ResourceNotFoundInDbException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }

    StringReader stringReader =
        new StringReader(new String(fhirLibrary.getContent().get(0).getData()));
    try {
      Library cqlLibrary = JsonCqlLibraryReader.read(stringReader);

      // TODO: Index the libraries by name rather than FHIR resource ID.
      libraryMap.put(libraryIdentifier.getId(), cqlLibrary);
      return cqlLibrary;
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
}
